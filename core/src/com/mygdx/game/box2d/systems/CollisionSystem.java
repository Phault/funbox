package com.mygdx.game.box2d.systems;

import com.artemis.*;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.mygdx.game.box2d.components.FixtureComponent;
import com.mygdx.game.box2d.components.Rigidbody;
import com.mygdx.game.hierarchy.systems.HierarchyManager;
import jdk.nashorn.internal.objects.Global;

/**
 * Created by Casper on 06-08-2016.
 */
public class CollisionSystem extends BaseEntitySystem implements ContactListener {

    private com.mygdx.game.scenegraph.systems.WorldTransformationManager transformManager;
    private HierarchyManager hierarchyManager;

    private float pixelsPerMeter = 100;
    private Vector2 gravity = new Vector2(0, -10);
    private World physicsWorld;

    private float timeStep = 1/60f;
    private int velocityIterations = 6;
    private int positionIterations = 2;

    private ComponentMapper<FixtureComponent> mFixtureComponent;
    private ComponentMapper<Rigidbody> mRigidbody;

    private Body staticBody;
    private final static BodyDef staticBodyDef = new BodyDef();

    private final static int GlobalListenerId = -1000;

    public CollisionSystem() {
        super(Aspect.all(com.mygdx.game.scenegraph.components.Transform.class, Rigidbody.class));
        Box2D.init();

        physicsWorld = new World(gravity, true);
        physicsWorld.setContactListener(this);
        staticBody = physicsWorld.createBody(staticBodyDef);
    }

    private float accumulator = 0;

    private IntMap<Bag<ICollisionListener>> listeners = new IntMap<Bag<ICollisionListener>>();

    @Override
    protected void processSystem() {
        float frameTime = Math.min(Gdx.graphics.getRawDeltaTime(), 0.25f);
        accumulator += frameTime;

        while (accumulator >= timeStep) {
            physicsWorld.step(timeStep, velocityIterations, positionIterations);
            accumulator -= timeStep;
        }

        for (IntMap.Entry<Body> entry : bodyLinks) {
            int entityId = entry.key;
            Body body = entry.value;

            Vector2 position = body.getPosition();
            float rotation = MathUtils.radiansToDegrees * body.getAngle();

            transformManager.setWorldPosition(entityId, position.x * pixelsPerMeter, position.y * pixelsPerMeter);
            transformManager.setWorldRotation(entityId, rotation);
        }
    }

    private IntMap<Fixture> fixtureLinks = new IntMap<Fixture>();
    private IntMap<Body> bodyLinks = new IntMap<Body>();

    private void createLink(int entityId, Fixture fixture) {
        FixtureComponent component = mFixtureComponent.create(entityId);
        component.fixture = fixture;

        fixture.setUserData(entityId);
        fixtureLinks.put(entityId, fixture);
    }

    private void createLink(int entityId, Body body) {
        Rigidbody component = mRigidbody.create(entityId);
        component.body = body;

        body.setUserData(entityId);
        bodyLinks.put(entityId, body);
    }

    private void destroyFixtureLink(int entityId) {
        mFixtureComponent.remove(entityId);
        Fixture fixture = fixtureLinks.remove(entityId);
        if (fixture != null)
            fixture.setUserData(null);
    }

    private void destroyBodyLink(int entityId) {
        mRigidbody.remove(entityId);
        Body body = bodyLinks.remove(entityId);
        if (body != null)
            body.setUserData(null);
    }

    public Body createBody(int entityId, BodyDef definition) {
        Body body = physicsWorld.createBody(definition);
        createLink(entityId, body);
        return body;
    }

    public Fixture createFixture(int entityId, FixtureDef definition) {
        Body body = getAttachedBody(entityId);

        Fixture fixture = body.createFixture(definition);
        createLink(entityId, fixture);

        return fixture;
    }

    public Fixture createFixture(int entityId, Shape shape, float density) {
        Body body = getAttachedBody(entityId);

        Fixture fixture = body.createFixture(shape, density);
        createLink(entityId, fixture);

        return fixture;
    }

    private Body getAttachedBody(int entityId) {
        int rigidbodyId = hierarchyManager.getEntityWithComponentInParent(entityId, Rigidbody.class);

        if (rigidbodyId != -1)
            return bodyLinks.get(rigidbodyId);

        return staticBody;
    }

    public void destroyBody(int entityId) {
        Rigidbody rigidbody = mRigidbody.get(entityId);

        if (rigidbody == null)
            return;

        // since fixtures are destroyed as well, we'll have to clean up the components
        Array<Fixture> fixtures = rigidbody.body.getFixtureList();
        for (int i = 0; i < fixtures.size; i++) {
            Fixture fixture = fixtures.get(i);
            int fixtureId = (Integer) fixture.getUserData();
            destroyFixtureLink(fixtureId);
        }

        physicsWorld.destroyBody(rigidbody.body);
        destroyBodyLink(entityId);
    }

    public void destroyBody(Body body) {
        int entityId = (Integer) body.getUserData();
        destroyBody(entityId);
    }

    public void destroyFixture(int entityId) {
        FixtureComponent component = mFixtureComponent.get(entityId);
        if (component == null)
            return;

        Body body = component.fixture.getBody();
        body.destroyFixture(component.fixture);
        destroyFixtureLink(entityId);
    }

    public void destroyFixture(Fixture fixture) {
        int entityId = (Integer) fixture.getUserData();
        destroyFixture(entityId);
    }

    public void addGlobalListener(ICollisionListener listener) {
        addListener(GlobalListenerId, listener);
    }

    public void removeGlobalListener(ICollisionListener listener) {
        removeListener(GlobalListenerId, listener);
    }

    public void addListener(int entityId, ICollisionListener listener) {
        Bag<ICollisionListener> listenerBag = listeners.get(entityId);

        if (listenerBag == null) {
            listenerBag = new Bag<ICollisionListener>();
            listeners.put(entityId, listenerBag);
        }

        listenerBag.add(listener);
    }

    public void removeListener(int entityId, ICollisionListener listener) {
        Bag<ICollisionListener> listenerBag = listeners.get(entityId);

        if (listenerBag == null)
            return;

        listenerBag.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    public void clearListeners(int entityId) {
        listeners.remove(entityId);
    }

    @Override
    public void beginContact(Contact contact) {
        int idA = getBodyId(contact.getFixtureA());
        int idB = getBodyId(contact.getFixtureB());

        emitBeginContact(listeners.get(idA), idA, idB, contact);
        emitBeginContact(listeners.get(idB), idB, idA, contact);
        emitBeginContact(listeners.get(GlobalListenerId), idA, idB, contact);
    }

    private void emitBeginContact(Bag<ICollisionListener> listeners, int thisId, int otherId, Contact contact) {
        if (listeners != null) {
            for (ICollisionListener listener : listeners) {
                listener.onContactBegin(thisId, otherId, contact);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        int idA = getBodyId(contact.getFixtureA());
        int idB = getBodyId(contact.getFixtureB());

        emitEndContact(listeners.get(idA), idA, idB, contact);
        emitEndContact(listeners.get(idB), idB, idA, contact);
        emitEndContact(listeners.get(GlobalListenerId), idA, idB, contact);
    }

    private void emitEndContact(Bag<ICollisionListener> listeners, int thisId, int otherId, Contact contact) {
        if (listeners != null) {
            for (ICollisionListener listener : listeners) {
                listener.onContactEnd(thisId, otherId, contact);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        int idA = getBodyId(contact.getFixtureA());
        int idB = getBodyId(contact.getFixtureB());

        // todo: fixture order inside the contact is currently reversed compared to thisId and otherId parameters

        emitPreSolve(listeners.get(idA), idA, idB, contact, oldManifold);
        emitPreSolve(listeners.get(idB), idB, idA, contact, oldManifold);
        emitPreSolve(listeners.get(GlobalListenerId), idA, idB, contact, oldManifold);
    }

    private void emitPreSolve(Bag<ICollisionListener> listeners, int thisId, int otherId, Contact contact, Manifold oldManifold) {
        if (listeners != null) {
            for (ICollisionListener listener : listeners) {
                listener.onPreSolve(thisId, otherId, contact, oldManifold);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        int idA = getBodyId(contact.getFixtureA());
        int idB = getBodyId(contact.getFixtureB());

        emitPostSolve(listeners.get(idA), idA, idB, contact, impulse);
        emitPostSolve(listeners.get(idB), idB, idA, contact, impulse);
        emitPostSolve(listeners.get(GlobalListenerId), idA, idB, contact, impulse);
    }

    private void emitPostSolve(Bag<ICollisionListener> listeners, int thisId, int otherId, Contact contact, ContactImpulse impulse) {
        if (listeners != null) {
            for (ICollisionListener listener : listeners) {
                listener.onPostSolve(thisId, otherId, contact, impulse);
            }
        }
    }

    private int getEntityId(Fixture fixture) {
        if (fixture.getUserData() == null)
            return -1;

        return (Integer) fixture.getUserData();
    }

    private int getEntityId(Body body) {
        if (body.getUserData() == null)
            return -1;

        return (Integer) body.getUserData();
    }

    private int getBodyId(Fixture fixture) {
        if (fixture != null)
            return getEntityId(fixture.getBody());

        return -1;
    }

    @Override
    protected void removed(int entityId) {
        super.removed(entityId);

        destroyBody(entityId);
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    public float getPixelsPerMeter() {
        return pixelsPerMeter;
    }

    public void setPixelsPerMeter(float pixelsPerMeter) {
        this.pixelsPerMeter = pixelsPerMeter;
    }

    public interface ICollisionListener {
        void onContactBegin(int thisId, int otherId, Contact contact);
        void onContactEnd(int thisId, int otherId, Contact contact);
        void onPreSolve(int thisId, int otherId, Contact contact, Manifold oldManifold);
        void onPostSolve(int thisId, int otherId, Contact contact, ContactImpulse impulse);
    }
}
