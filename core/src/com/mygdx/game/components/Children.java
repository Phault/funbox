package com.mygdx.game.components;

import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.IntBag;

/**
 * Created by Casper on 22-07-2016.
 */
public class Children extends PooledComponent {
    @EntityId @LinkPolicy(LinkPolicy.Policy.CHECK_SOURCE)
    public IntBag Targets = new IntBag();

    @Override
    protected void reset() {
        Targets.setSize(0);
    }
}
