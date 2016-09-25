package com.mygdx.game.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Casper on 24-09-2016.
 */
public class ShapeDrawing implements Pool.Poolable {
    private Color color = new Color(1, 1, 1, 1);
    private FloatArray points = new FloatArray();

    public int getPointCount() {
        return points.size / 2;
    }

    public void getPoint(int index, Vector2 result) {
        result.set(getPointX(index), getPointY(index));
    }

    public float getPointX(int index) {
        return points.get(index * 2);
    }

    public float getPointY(int index) {
        return points.get(index * 2 + 1);
    }

    public void addPoint(float x, float y) {
        points.add(x);
        points.add(y);
    }

    public void setPoint(int index, float x, float y) {
        points.set(index * 2, x);
        points.set(index * 2 + 1, y);
    }

    public boolean isValidForNextPoint(float x, float y) {
        if (containsPoint(x, y, 5f))
            return false;

        if (getPointCount() <= 2)
            return true;

        float lastPointX = getPointX(getPointCount() - 1);
        float lastPointY = getPointY(getPointCount() - 1);

        float deltaX = x - lastPointX;
        float deltaY = y - lastPointY;

        lastPointX += Math.signum(deltaX) * 0.01f;
        lastPointY += Math.signum(deltaY) * 0.01f;

        x += Math.signum(deltaX) * 3f;
        y += Math.signum(deltaY) * 3f;

        for (int i = 0; i < getPointCount() - 1; i++) {

            float x1 = getPointX(i);
            float y1 = getPointY(i);

            float x2 = getPointX(i+1);
            float y2 = getPointY(i+1);

            if (Intersector.intersectSegments(lastPointX, lastPointY, x, y, x1, y1, x2, y2, null)) {
                return false;
            }
        }

        return true;
    }

    public boolean containsPoint(float x, float y, float tolerance) {
        float sqrTolerance = tolerance * tolerance;

        for (int i = 0; i < getPointCount(); i++) {
            float pointX = getPointX(i);
            float pointY = getPointY(i);

            if (Vector2.dst2(x, y, pointX, pointY) < sqrTolerance)
                return true;
        }

        return false;
    }

    public FloatArray getPoints() {
        return points;
    }

    @Override
    public void reset() {
        color.set(1, 1, 1, 1);
        points.clear();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }
}
