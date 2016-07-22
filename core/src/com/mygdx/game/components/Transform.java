package com.mygdx.game.components;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Casper on 19-07-2016.
 */
public class Transform extends PooledComponent {
    public Vector2 Position = new Vector2(0, 0);
    public float Rotation = 0;
    public Vector2 Scale = new Vector2(1, 1);

    @Override
    protected void reset() {
        Position.set(0, 0);
        Rotation = 0;
        Scale.set(1, 1);
    }
}
