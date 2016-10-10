package com.phault.funbox.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.phault.funbox.box2d.components.Rigidbody;
import com.phault.funbox.components.TestMovement;

/**
 * Created by Casper on 20-07-2016.
 */
public class TestMovementSystem extends IteratingSystem {

    private ComponentMapper<TestMovement> mTestMovement;
    private ComponentMapper<Rigidbody> mRigidbody;

    public TestMovementSystem() {
        super(Aspect.all(TestMovement.class, Rigidbody.class));
    }

    @Override
    protected void process(int i) {
        TestMovement mov = mTestMovement.get(i);
        Rigidbody rigidbody = mRigidbody.get(i);
        float deltaTime = Gdx.graphics.getDeltaTime();

        updateMovement(mov, rigidbody, deltaTime);
        updateRotation(mov, rigidbody, deltaTime);
    }

    private void updateMovement(TestMovement mov, Rigidbody rigidbody, float deltaTime) {
        float axisX = Gdx.input.isKeyPressed(Input.Keys.A) ? -1 : 0;
        axisX += Gdx.input.isKeyPressed(Input.Keys.D) ? 1 : 0;

        float axisY = Gdx.input.isKeyPressed(Input.Keys.S) ? -1 : 0;
        axisY += Gdx.input.isKeyPressed(Input.Keys.W) ? 1 : 0;

        rigidbody.body.applyLinearImpulse(axisX * mov.speed * deltaTime,
                axisY * mov.speed * deltaTime,
                rigidbody.body.getWorldCenter().x,
                rigidbody.body.getWorldCenter().y,
                true);
    }

    private void updateRotation(TestMovement mov, Rigidbody rigidbody, float deltaTime) {
        float dir = Gdx.input.isKeyPressed(Input.Keys.Q) ? 1 : 0;
        dir += Gdx.input.isKeyPressed(Input.Keys.E) ? -1 : 0;

        rigidbody.body.applyAngularImpulse(dir * mov.angularSpeed * deltaTime, true);
    }
}
