package com.mygdx.game.shapes;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Projection;

/**
 * Created by Casper on 27-07-2016.
 */
public interface IShape {
    Vector2[] getPoints();
    Vector2[] getUniqueAxes();
    void project(Vector2 normalizedAxis, Projection projection);
    void projectTransformed(Vector2 normalizedAxis, Projection projection, Matrix3 transformation);
}

