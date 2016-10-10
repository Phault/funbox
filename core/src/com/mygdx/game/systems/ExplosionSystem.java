package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.game.box2d.systems.CollisionSystem;
import com.mygdx.game.box2d.utils.RaycastClosestCallback;

/**
 * Created by Casper on 27-09-2016.
 */
public class ExplosionSystem extends BaseSystem {
    private CollisionSystem collisionSystem;

    private final RaycastClosestCallback callback = new RaycastClosestCallback();

    @Override
    protected void processSystem() {
    }


    public void spawnExplosion(float x, float y, float radius, float power, int rayCount) {
        x *= collisionSystem.getMetersPerPixel();
        y *= collisionSystem.getMetersPerPixel();
        radius *= collisionSystem.getMetersPerPixel();

        float powerEach = power / rayCount;
        float deltaAngle = MathUtils.PI2 / rayCount;
        for (int i = 0; i < rayCount; i++) {
            float angle = deltaAngle * i;
            float dirX = MathUtils.cos(angle);
            float dirY = MathUtils.sin(angle);
            float endX = x + radius * dirX;
            float endY = y + radius * dirY;

            callback.reset();
            collisionSystem.getPhysicsWorld().rayCast(callback, x, y, endX, endY);
            if (callback.fixture != null) {
                Body body = callback.fixture.getBody();
                float fraction = 1-callback.fraction;
                body.applyLinearImpulse(dirX * powerEach * fraction,
                        dirY * powerEach * fraction,
                        callback.point.x, callback.point.y,
                        true);
            }
        }
    }
}
