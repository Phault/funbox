package com.mygdx.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.components.Transform;
import com.mygdx.game.components.Velocity;

/**
 * Created by Casper on 25-07-2016.
 */
public class VelocitySystem extends IteratingSystem {
    private ComponentMapper<Velocity> mVelocity;

    private WorldTransformationManager transformManager;
    
    public VelocitySystem() {
        super(Aspect.all(Transform.class, Velocity.class));
    }

    @Override
    protected void process(int i) {
        Velocity velocity = mVelocity.get(i);

        Vector2 currentPos = transformManager.getWorldPosition(i);

        transformManager.setWorldPosition(i,
                currentPos.x + velocity.x * Gdx.graphics.getDeltaTime(),
                currentPos.y + velocity.y * Gdx.graphics.getDeltaTime());
    }
}
