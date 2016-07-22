package com.mygdx.game.components;

import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;

/**
 * Created by Casper on 19-07-2016.
 */
public class Parented extends PooledComponent {
    @EntityId public int Parent = -1;

    @Override
    protected void reset() {
        Parent = -1;
    }
}
