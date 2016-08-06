package com.mygdx.game.shapes;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Projection;

public class Circle extends com.badlogic.gdx.math.Circle implements IShape {
    private Vector2[] points = { new Vector2() };

    private Vector2[] tmpProjectionPoints = new Vector2[] {
            new Vector2(),
            new Vector2()
    };

    @Override
    public Vector2[] getPoints() {
        points[0].set(x, y);
        return points;
    }

    public Vector2[] getUniqueAxes() {
        throw new UnsupportedOperationException("Circles have an infinite amount of normals");
    }

    @Override
    public void project(Vector2 normalizedAxis, Projection projection) {
        tmpProjectionPoints[0].set(x, y).add(normalizedAxis.x * radius, normalizedAxis.y * radius);
        tmpProjectionPoints[1].set(x, y).sub(normalizedAxis.x * radius, normalizedAxis.y * radius);

        ShapeUtils.project(tmpProjectionPoints, normalizedAxis, projection);
    }

    @Override
    public void projectTransformed(Vector2 normalizedAxis, Projection projection, Matrix3 transformation) {
        tmpProjectionPoints[0].set(x, y).add(normalizedAxis.x * radius, normalizedAxis.y * radius);
        tmpProjectionPoints[1].set(x, y).sub(normalizedAxis.x * radius, normalizedAxis.y * radius);

        ShapeUtils.projectTransformed(tmpProjectionPoints, normalizedAxis, projection, transformation);
    }
}
