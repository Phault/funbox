package com.mygdx.game.shapes;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Projection;

public class ShapeUtils {
    public static void project(Vector2[] vertices, Vector2 axis, Projection result) {
        result.set(Float.MAX_VALUE, Float.MIN_VALUE);

        for (int i = 0; i < vertices.length; i++) {
            Vector2 vertex = vertices[i];

            float p = Vector2.dot(axis.x, axis.y, vertex.x, vertex.y);
            result.expand(p);
        }
    }

    private static final Vector2 tmpTransformed = new Vector2();

    public static void projectTransformed(Vector2[] vertices, Vector2 axis, Projection result, Matrix3 transformation) {
        result.set(Float.MAX_VALUE, Float.MIN_VALUE);

        for (int i = 0; i < vertices.length; i++) {
            tmpTransformed.set(vertices[i]).mul(transformation);

            float p = Vector2.dot(axis.x, axis.y, tmpTransformed.x, tmpTransformed.y);
            result.expand(p);
        }
    }
}
