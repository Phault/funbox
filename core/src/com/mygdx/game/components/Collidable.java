package com.mygdx.game.components;

import com.artemis.PooledComponent;
import com.mygdx.game.shapes.IShape;
import com.mygdx.game.systems.CollisionSystem;

import java.util.LinkedList;

/**
 * Created by Casper on 27-07-2016.
 */

public class Collidable extends PooledComponent {

    public IShape shape;
    public boolean isTrigger = false;

    public LinkedList<CollisionSystem.Collision> collisions = new LinkedList<CollisionSystem.Collision>();

    @Override
    protected void reset() {
        shape = null;
        isTrigger = false;
        collisions.clear();
    }
}
