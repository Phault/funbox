package com.phault.funbox.utils;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Casper on 12-10-2016.
 */
public class PolygonUtils {

    private static final Vector2 tmpCenter = new Vector2();
    public static void centerPolygon(float[] polygon) {
        getPolygonCenter(polygon, tmpCenter);

        for (int i = 0; i < polygon.length; i += 2) {
            polygon[i] -= tmpCenter.x;
            polygon[i+1] -= tmpCenter.y;
        }
    }

    public static Vector2 getPolygonCenter(float[] polygon, Vector2 result) {
        result.setZero();
        for (int i = 0; i < polygon.length; i += 2) {
            float x = polygon[i];
            float y = polygon[i+1];
            result.x += x;
            result.y += y;
        }

        result.x /= polygon.length / 2;
        result.y /= polygon.length / 2;

        return result;
    }
}
