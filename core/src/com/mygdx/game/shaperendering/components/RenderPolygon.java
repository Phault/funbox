package com.mygdx.game.shaperendering.components;

import com.badlogic.gdx.utils.ShortArray;
import com.mygdx.game.shaperendering.utils.VertexArray;

/**
 * Created by Casper on 15-09-2016.
 */
public class RenderPolygon extends RenderShape {
    public VertexArray vertices;
    public final ShortArray triangulation = new ShortArray();

    @Override
    protected void reset() {
        super.reset();
        vertices = null;
        triangulation.clear();
    }
}

