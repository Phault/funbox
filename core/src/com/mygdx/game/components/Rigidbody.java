package com.mygdx.game.components;

import com.artemis.PooledComponent;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Casper on 06-08-2016.
 */
public class Rigidbody extends PooledComponent{
    public Body body;

    @Override
    protected void reset() {
        body = null;
    }
}
