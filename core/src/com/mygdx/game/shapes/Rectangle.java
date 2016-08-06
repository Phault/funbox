package com.mygdx.game.shapes;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Projection;

public class Rectangle extends com.badlogic.gdx.math.Rectangle implements IShape {

    private Vector2[] points = new Vector2[4];
    private final static Vector2[] axes = {
        Vector2.X,
        Vector2.Y
    };

    public Rectangle() {
        super();

        for (int i = 0; i < points.length; i++)
            points[i] = new Vector2();
    }

    public void getTopLeft(Vector2 topLeft) {
        topLeft.set(x, y);
    }

    public void getTopRight(Vector2 topRight) {
        topRight.set(x + width, y);
    }

    public void getBottomRight(Vector2 bottomRight) {
        bottomRight.set(x + width, y + height);
    }

    public void getBottomLeft(Vector2 bottomLeft) {
        bottomLeft.set(x, y + height);
    }

    @Override
    public Vector2[] getPoints() {
        getTopLeft(points[0]);
        getTopRight(points[1]);
        getBottomRight(points[2]);
        getBottomLeft(points[3]);
        return points;
    }

    public Vector2[] getUniqueAxes() {
        return axes;
    }

    @Override
    public void project(Vector2 normalizedAxis, Projection projection) {
        ShapeUtils.project(getPoints(), normalizedAxis, projection);
    }

    @Override
    public void projectTransformed(Vector2 normalizedAxis, Projection projection, Matrix3 transformation) {
        ShapeUtils.projectTransformed(getPoints(), normalizedAxis, projection, transformation);
    }
}
