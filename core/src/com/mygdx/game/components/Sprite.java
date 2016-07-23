package com.mygdx.game.components;

import com.artemis.PooledComponent;
import com.artemis.annotations.Transient;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Casper on 19-07-2016.
 */
@Transient
public class Sprite extends PooledComponent {
    public TextureRegion texture = null;

    @Override
    protected void reset() {
        texture = null;
    }
}
