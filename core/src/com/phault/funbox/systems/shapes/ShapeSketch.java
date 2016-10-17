package com.phault.funbox.systems.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class ShapeSketch implements Pool.Poolable {
    public float left = Float.NaN,
            top = Float.NaN,
            right = Float.NaN,
            bottom = Float.NaN;

    public final Color color = Color.WHITE.cpy();

    public void setTopLeft(float x, float y) {
        left = x;
        top = y;
    }

    public void setBottomRight(float x, float y) {
        right = x;
        bottom = y;
    }

    public float width() {
        return Math.abs(right - left);
    }

    public float height() {
        return Math.abs(bottom - top);
    }

    public float dst() {
        return Vector2.dst(left, top, right, bottom);
    }

    public float centerX() { return MathUtils.lerp(left, right, 0.5f); }
    public float centerY() { return MathUtils.lerp(top, bottom, 0.5f); }

    public boolean isValid() {
        if (Float.isNaN(left)
                || Float.isNaN(top)
                || Float.isNaN(right)
                || Float.isNaN(bottom))
            return false;

        return true;
    }

    @Override
    public void reset() {
        left = Float.NaN;
        top = Float.NaN;
        right = Float.NaN;
        bottom = Float.NaN;

        color.set(Color.WHITE);
    }
}
