package com.phault.funbox.systems.shapes;

import com.badlogic.gdx.utils.Pool;

public class ShapeSketch implements Pool.Poolable {
    public float left = Float.NaN,
            top = Float.NaN,
            right = Float.NaN,
            bottom = Float.NaN;

    public void setTopLeft(float x, float y) {
        left = x;
        top = y;
    }

    public void setBottomRight(float x, float y) {
        right = x;
        bottom = y;
    }

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
    }
}
