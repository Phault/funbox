package com.phault.funbox.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Casper on 26-09-2016.
 */
public class MathHelper {
    public static int wrapAround(int value, int min, int max)
    {
        int rangeSize = max - min + 1;

        if (rangeSize == 0)
            return min;

        if (value < min)
            value += rangeSize * ((min - value) / rangeSize + 1);

        return min + (value - min) % rangeSize;
    }

    private static final Vector2 lineDelta = new Vector2();
    private static final Vector2 startToPoint = new Vector2();

    public static float lineToPointDistance(float lineX1, float lineY1,
                                            float lineX2, float lineY2,
                                            float pointX, float pointY,
                                            boolean isSegment) {

        lineDelta.set(lineX2, lineY2).sub(lineX1, lineY1);
        startToPoint.set(pointX, pointY).sub(lineX1, lineY1);
        float p = startToPoint.dot(lineDelta) / lineDelta.len2();

        if (isSegment)
            p = MathUtils.clamp(p, 0, 1);

        return (lineDelta.scl(p).sub(startToPoint)).len();
    }

    public static float lineToPointDistance(Vector2 lineStart, Vector2 lineEnd, Vector2 point, boolean isSegment) {
        return lineToPointDistance(lineStart.x, lineStart.y,
                lineEnd.x, lineEnd.y,
                point.x, point.y,
                isSegment);
    }
}
