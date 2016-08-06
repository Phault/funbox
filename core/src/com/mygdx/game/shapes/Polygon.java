package com.mygdx.game.shapes;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Projection;

public class Polygon implements IShape {

    private com.badlogic.gdx.math.Polygon backingPolygon = new com.badlogic.gdx.math.Polygon();

    public Vector2[] getUniqueAxes() {
        return new Vector2[0];
    }

    @Override
    public void project(Vector2 normalizedAxis, Projection projection) {
    }

    @Override
    public void projectTransformed(Vector2 normalizedAxis, Projection projection, Matrix3 transformation) {
    }

    @Override
    public Vector2[] getPoints() {
        return null;
    }
}
