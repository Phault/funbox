package com.mygdx.game.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.PooledLinkedList;
import com.mygdx.game.Projection;
import com.mygdx.game.components.Collidable;
import com.mygdx.game.components.Transform;
import com.mygdx.game.shapes.Circle;
import com.mygdx.game.shapes.IShape;

/**
 * Created by Casper on 27-07-2016.
 */
public class CollisionSystem extends BaseEntitySystem {
    
    private ComponentMapper<Collidable> mCollidable;
    private WorldTransformationManager transformManager;

    private PooledLinkedList<Collision> activeCollisions = new PooledLinkedList<Collision>(Integer.MAX_VALUE);

    public CollisionSystem() {
        super(Aspect.all(Collidable.class, Transform.class));
    }

    @Override
    protected void processSystem() {
        IntBag entities = subscription.getEntities();

        for (int i = 0; i < entities.size(); i++) {
            int entityId1 = entities.get(i);

            for (int j = entities.size() - 1; j >= 0; j--) {
                if (i == j) continue;

                int entityId2 = entities.get(j);

                checkCollision(entityId1, entityId2);
            }
        }

        processListeners();
    }

    private void processListeners() {
        ObjectMap.Entries<Collision, State> entries = test.entries();
        while (entries.hasNext()) {
            ObjectMap.Entry<Collision, State> current = entries.next();

            switch (current.value) {

                case Enter:
                    // call enter on listeners
                    break;
                case Stay:
                    // call stay on listeners
                    break;
                case Exit:
                    // call exit on listeners
                    break;
            }
        }
    }

    private Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

    private Collision workingCollision;

    private void checkCollision(int entityId1, int entityId2) {

        boolean previouslyColliding = isCollisionRegistered(entityId1, entityId2);
        boolean currentlyColliding = overlap(mCollidable.get(entityId1).shape, mCollidable.get(entityId2).shape, mtv);

        if (currentlyColliding) {
            if (previouslyColliding)
                ; // stay
            else
                registerCollision(workingCollider1, workingCollider2, mtv);
                ; // enter
        }
        else if (previouslyColliding)
        {
            test.put(new Collision(), State.Enter);
            ; // exit
        }
    }

    ObjectMap<Collision, State> test = new ObjectMap<Collision, State>();

    private enum State {
        Enter,
        Stay,
        Exit
    }

    private boolean isCollisionRegistered(int entityId1, int entityId2) {
        Collidable a = mCollidable.get(entityId1);
        Collidable b = mCollidable.get(entityId2);

        return isCollisionRegistered(a, b);
    }

    private boolean isCollisionRegistered(Collidable a, Collidable b) {
        for (int i = 0; i < a.collisions.size(); i++) {
            Collision col = a.collisions.get(i);
            if (col.collidable == b)
                return true;
        }
        return false;
    }

    public boolean overlap(IShape a, IShape b, Intersector.MinimumTranslationVector mtv) {
        mtv.depth = 0;
        mtv.normal.set(0, 0);

        if (a == null || b == null)
            return false;

        // todo: return early if bounds don't intersect

        if (!getMTV(getAxes(a, b), mtv))
            return false;

        if (!getMTV(getAxes(b, a), mtv))
            return false;

        return true;
    }

    private Vector2[] getAxes(IShape shape, IShape other) {
        if (shape instanceof Circle) {
            // todo: return directions from center to each vertex of other shape
            return null;
        }

        return shape.getUniqueAxes();
    }

    private final Projection p1 = new Projection();
    private final Projection p2 = new Projection();

    private boolean getMTV(Vector2[] axes, Intersector.MinimumTranslationVector mtv) {
        IShape a = workingCollider1.shape;
        IShape b = workingCollider2.shape;

        for (int i = 0; i < axes.length; i++) {
            Vector2 axis = axes[i];

            a.projectTransformed(axis, p1, transformManager.getLocalToWorldMatrix(workingEntityId1));
            b.projectTransformed(axis, p2, transformManager.getLocalToWorldMatrix(workingEntityId2));

            float overlap = p1.getOverlap(p2);
            if (overlap > 0) {
                if (p1.contains(p2) || p2.contains(p1)) {
                    float deltaMin = Math.abs(p1.min - p2.min);
                    float deltaMax = Math.abs(p1.max - p2.max);

                    overlap += Math.min(deltaMin, deltaMax);
                }

                if (overlap < mtv.depth) {
                    mtv.depth = overlap;
                    mtv.normal.set(axis);
                }
            }
            else
                return false;
        }
        return true;
    }

    public class Collision implements Pool.Poolable{
        public int entityId = -1;
        public Collidable collidable;
        public Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

        @Override
        public void reset() {
            entityId = -1;
            collidable = null;
            mtv.depth = 0;
            mtv.normal.set(0, 0);
        }
    }

    public interface ICollisionListener {
        void onCollisionEnter(Collision collision);
        void onCollisionStay(Collision collision);
        void onCollisionExit(Collision collision);
    }

    public abstract class CollisionAdapter implements ICollisionListener {
        @Override
        public void onCollisionEnter(Collision collision) {

        }

        @Override
        public void onCollisionStay(Collision collision) {

        }

        @Override
        public void onCollisionExit(Collision collision) {

        }
    }
}
