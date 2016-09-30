package com.mygdx.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.utils.MathHelper;

/**
 * Created by Casper on 24-09-2016.
 */
public class ShapeDrawing implements Pool.Poolable {
    private final static String TAG = "ShapeDrawing";

    private Color color = new Color(1, 1, 1, 1);
    private FloatArray points = new FloatArray();
    private float minimumPointDistance;

    public int getPointCount() {
        return points.size / 2;
    }

    public void getPoint(int index, Vector2 result) {
        result.set(getPointX(index), getPointY(index));
    }

    public int getPointWrapping(int index, Vector2 result) {
        int wrappedIndex = MathHelper.wrapAround(index, 0, getPointCount()-1);
        getPoint(wrappedIndex, result);
        return wrappedIndex;
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

    public void removePoint(int index) {
        points.removeIndex(index * 2 + 1);
        points.removeIndex(index * 2);
    }

    public void setPoint(int index, float x, float y) {
        points.set(index * 2, x);
        points.set(index * 2 + 1, y);
    }

    public boolean isValidForNextPoint(float x, float y) {
        if (containsPoint(x, y, minimumPointDistance))
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

    public boolean isValid() {
        return getPointCount() >= 3 && !isSelfIntersecting();
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

    public boolean isSelfIntersecting() {
        if (getPointCount() <= 2)
            return false;

        for (int i = 0; i < getPointCount(); i++) {
            float fromX1 = getPointX(i);
            float fromY1 = getPointY(i);
            int nextPoint = (i+1) % getPointCount();
            float toX1 = getPointX(nextPoint);
            float toY1 = getPointY(nextPoint);

            fromX1 = MathUtils.lerp(fromX1, toX1, 0.00001f);
            fromY1 = MathUtils.lerp(fromY1, toY1, 0.00001f);
            toX1 = MathUtils.lerp(toX1, fromX1, 0.00001f);
            toY1 = MathUtils.lerp(toY1, fromY1, 0.00001f);

            for (int j = 0; j < getPointCount(); j++) {
                if (i == j) continue;

                float fromX2 = getPointX(j);
                float fromY2 = getPointY(j);
                nextPoint = (j+1) % getPointCount();
                float toX2 = getPointX(nextPoint);
                float toY2 = getPointY(nextPoint);

                if (Intersector.intersectSegments(fromX1, fromY1, toX1, toY1,
                        fromX2, fromY2, toX2, toY2, null)) {
                    return true;
                }
            }
        }

        return false;
    }

    private final Vector2 previous = new Vector2();
    private final Vector2 current = new Vector2();
    private final Vector2 next = new Vector2();
    private final Vector2 previousLine = new Vector2();
    private final Vector2 nextLine = new Vector2();
    public void optimize() {
        int vertexCountBefore = getPointCount();
        for (int i = 0; i < getPointCount(); i++) {
            getPointWrapping(i - 1, previous);
            getPoint(i, current);
            getPointWrapping(i + 1, next);

            previousLine.set(current).sub(previous).nor();
            nextLine.set(next).sub(current).nor();

            float dot = nextLine.dot(previousLine);
            if (dot > 0.98f) {
                removePoint(i);
                i--;
            }
        }
        Gdx.app.log(TAG, "Vertex count before: " + vertexCountBefore + ", after: "+getPointCount());
    }

    public float getMinimumPointDistance() {
        return minimumPointDistance;
    }

    public void setMinimumPointDistance(float minimumPointDistance) {
        this.minimumPointDistance = minimumPointDistance;
    }
}
