package com.mygdx.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.game.components.TestMovement;
import com.mygdx.game.components.Transform;

/**
 * Created by Casper on 20-07-2016.
 */
public class TestMovementSystem extends IteratingSystem {

    private ComponentMapper<Transform> mTransform;
    private ComponentMapper<TestMovement> mTestMovement;

    public TestMovementSystem() {
        super(Aspect.all(TestMovement.class, Transform.class));
    }

    @Override
    protected void process(int i) {
        Transform trans = mTransform.get(i);
        TestMovement mov = mTestMovement.get(i);

        float axisX = Gdx.input.isKeyPressed(Input.Keys.LEFT) ? -1 : 0;
        axisX += Gdx.input.isKeyPressed(Input.Keys.RIGHT) ? 1 : 0;

        float axisY = Gdx.input.isKeyPressed(Input.Keys.DOWN) ? -1 : 0;
        axisY += Gdx.input.isKeyPressed(Input.Keys.UP) ? 1 : 0;

        trans.Position.add(axisX * mov.Speed * world.getDelta(), axisY * mov.Speed * world.getDelta());
    }
}
