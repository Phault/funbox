package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.artemis.EntityEdit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.mygdx.game.box2d.systems.CollisionSystem;
import com.mygdx.game.scenegraph.components.Transform;
import com.mygdx.game.scenegraph.systems.WorldTransformationManager;
import com.mygdx.game.shaperendering.components.*;

import java.util.Random;

/**
 * Created by Casper on 06-09-2016.
 */
public class ShapeSpawnSystem extends BaseSystem {

    private CollisionSystem collisionSystem;
    private WorldTransformationManager worldTransformationManager;
    private CameraSystem cameraSystem;

    private Vector2 minSize = new Vector2(25, 25), maxSize = new Vector2(200, 200);

    private float minRadius = 10, maxRadius = 100;

    private Random random = new Random();
    private BodyDef bodyDef;

    @Override
    protected void initialize() {
        super.initialize();

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        groundBody = collisionSystem.getPhysicsWorld().createBody(new BodyDef());
    }

    private final Vector3 worldPointer = new Vector3();
    private final Vector2 physicsWorldPointer = new Vector2();
    private final Color tmpColor = new Color();
    private Body hitBody;
    private MouseJoint joint;

    private Body groundBody;

    private boolean isDragging() {
        return joint != null;
    }

    private QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if (fixture.testPoint(physicsWorldPointer.x, physicsWorldPointer.y)) {
                hitBody = fixture.getBody();
                return false;
            }

            return true;
        }
    };

    @Override
    protected void processSystem() {
        worldPointer.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        cameraSystem.getCamera().unproject(worldPointer);
        physicsWorldPointer.set(worldPointer.x, worldPointer.y).scl(collisionSystem.getMetersPerPixel());

        boolean touching = Gdx.input.isTouched();

        if (touching) {
            if (isDragging()) {
                joint.setTarget(physicsWorldPointer);
            }
            else {
                hitBody = null;
                collisionSystem.getPhysicsWorld().QueryAABB(callback,
                        physicsWorldPointer.x - 0.01f,
                        physicsWorldPointer.y - 0.01f,
                        physicsWorldPointer.x + 0.01f,
                        physicsWorldPointer.y + 0.01f);

                if (hitBody != null)
                    startDrag(hitBody);
                else if (Gdx.input.justTouched()) {
                    int cubeId = spawnRandomShape(worldPointer.x, worldPointer.y);
                    startDrag(collisionSystem.getAttachedBody(cubeId));
                }
            }
        }
        else {
            if (isDragging())
                endDrag();
        }
    }

    private void startDrag(Body body) {
        MouseJointDef def = new MouseJointDef();
        def.bodyA = groundBody;
        def.bodyB = body;
        def.collideConnected = false;
        def.target.set(physicsWorldPointer.x, physicsWorldPointer.y);
        def.maxForce = 500.0f * body.getMass();

        joint = (MouseJoint)collisionSystem.getPhysicsWorld().createJoint(def);
        body.setAwake(true);
    }

    private void endDrag() {
        collisionSystem.getPhysicsWorld().destroyJoint(joint);
        joint = null;
    }

    private int spawnRandomShape(float x, float y) {
        int shapeIndex = random.nextInt(2);

        switch (shapeIndex) {
            case 0:
                return spawnRandomCube(x, y);
            case 1:
                return spawnRandomCircle(x, y);
        }

        return -1;
    }

    private int spawnRandomCube(float x, float y) {
        float width = MathUtils.lerp(minSize.x, maxSize.x, random.nextFloat());
        float height = MathUtils.lerp(minSize.y, maxSize.y, random.nextFloat());

        tmpColor.set(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);

        return spawnCube(worldPointer.x, worldPointer.y, width, height, tmpColor);
    }

    private int spawnRandomCircle(float x, float y) {
        float radius = MathUtils.lerp(minRadius, maxRadius, random.nextFloat());

        tmpColor.set(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);

        return spawnCircle(worldPointer.x, worldPointer.y, radius, tmpColor);
    }

    public int spawnCube(float x, float y, float width, float height, Color color) {
        int cube = world.create();
        EntityEdit edit = world.edit(cube);
        edit.create(Transform.class);

        Body boxBody = collisionSystem.createBody(cube, bodyDef);
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(width * 0.5f * collisionSystem.getMetersPerPixel(),
                height * 0.5f * collisionSystem.getMetersPerPixel());
        boxBody.createFixture(boxShape, 2);

        RenderRectangle rectangle = edit.create(RenderRectangle.class);
        rectangle.width = width;
        rectangle.height = height;
        rectangle.color.set(color);

        worldTransformationManager.setWorldPosition(cube, x, y);

        return cube;
    }

    public int spawnCircle(float x, float y, float radius, Color color) {
        int circle = world.create();
        EntityEdit edit = world.edit(circle);
        edit.create(Transform.class);

        Body body = collisionSystem.createBody(circle, bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius * collisionSystem.getMetersPerPixel());
        body.createFixture(circleShape, 2);

        RenderCircle renderCircle = edit.create(RenderCircle.class);
        renderCircle.radius = radius;
        renderCircle.color.set(color);

        worldTransformationManager.setWorldPosition(circle, x, y);

        return circle;
    }
}
