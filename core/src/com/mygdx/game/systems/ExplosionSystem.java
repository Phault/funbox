package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.box2d.systems.CollisionSystem;

/**
 * Created by Casper on 27-09-2016.
 */
public class ExplosionSystem extends BaseSystem {
    private CollisionSystem collisionSystem;

    private BodyDef particleBodyDef;
    private FixtureDef particleFixtureDef;

    private final Bag<Body> activeParticles = new Bag<>();

    @Override
    protected void initialize() {
        super.initialize();

        particleBodyDef = new BodyDef();
        particleBodyDef.type = BodyDef.BodyType.DynamicBody;
        particleBodyDef.fixedRotation = true;
        particleBodyDef.bullet = true;
        particleBodyDef.linearDamping = 10;
        particleBodyDef.gravityScale = 0;

        CircleShape shape = new CircleShape();
        shape.setRadius(0.05f);

        particleFixtureDef = new FixtureDef();
        particleFixtureDef.shape = shape;
        particleFixtureDef.friction = 0;
        particleFixtureDef.restitution = 0.99f;
        particleFixtureDef.filter.groupIndex = -1;
    }

    @Override
    protected void processSystem() {
        for (int i = activeParticles.size() - 1; i >= 0; i--) {
            Body particle = activeParticles.get(i);
            if (particle.getLinearVelocity().len2() < 1) {
                activeParticles.remove(i);
                collisionSystem.getPhysicsWorld().destroyBody(particle);
            }
        }
    }

    @Override
    protected void dispose() {
        super.dispose();

        activeParticles.clear();
    }

    public void spawnExplosion(float x, float y, int numParticles, float power) {
        x *= collisionSystem.getMetersPerPixel();
        y *= collisionSystem.getMetersPerPixel();

        particleFixtureDef.density = 500f / numParticles;

        float deltaAngle = 360 / numParticles * MathUtils.degreesToRadians;

        for (int i = 0; i < numParticles; i++) {
            float dirX = MathUtils.cos(i * deltaAngle);
            float dirY = MathUtils.sin(i * deltaAngle);

            spawnParticle(x, y, dirX, dirY, power);
        }
    }

    private void spawnParticle(float x, float y, float dirX, float dirY, float power) {
        particleBodyDef.position.set(x, y);
        particleBodyDef.linearVelocity.set(dirX, dirY).scl(power);
        Body body = collisionSystem.getPhysicsWorld().createBody(particleBodyDef);
        body.createFixture(particleFixtureDef);
        activeParticles.add(body);
    }
}
