package com.mygdx.game.shaperendering.components;

import com.mygdx.game.shaperendering.utils.VertexArray;

/**
 * Created by Casper on 15-09-2016.
 */
public class RenderPolygon extends RenderShape {
    public VertexArray vertices;

    @Override
    protected void reset() {
        super.reset();
        vertices = null;
    }
}

