package com.phault.funbox.shaperendering.commands;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ShortArray;
import com.phault.funbox.shaperendering.utils.VertexArray;

public class DrawPolygonCommand extends DrawCommand {

    public VertexArray polygon;
    public ShortArray triangulation;

    private static final Vector2[] tmpTriangle = new Vector2[] {
            new Vector2(),
            new Vector2(),
            new Vector2(),
    };

    @Override
    public void execute(ShapeRenderer renderer) {
        super.execute(renderer);

        for (int i = 0; i < triangulation.size; i += 3) {
            polygon.get(triangulation.get(i), tmpTriangle[0]);
            polygon.get(triangulation.get(i + 1), tmpTriangle[1]);
            polygon.get(triangulation.get(i + 2), tmpTriangle[2]);
            renderer.triangle(tmpTriangle[0].x, tmpTriangle[0].y,
                    tmpTriangle[1].x, tmpTriangle[1].y,
                    tmpTriangle[2].x, tmpTriangle[2].y);
        }
    }

    @Override
    public void reset() {
        super.reset();

        polygon = null;
        triangulation = null;
    }
}
