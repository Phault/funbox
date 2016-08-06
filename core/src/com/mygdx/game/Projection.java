package com.mygdx.game;

public class Projection {
    public float min, max;

    public Projection() {
        this(Float.MAX_VALUE, Float.MIN_VALUE);
    }
    public Projection(float min, float max) {
        set(min, max);
    }

    public void set(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public void expand(float p) {
        if (p < min)
            min = p;

        if (p > max)
            max = p;
    }

    public boolean overlaps(Projection other)
    {
        return getOverlap(other) > 0;
    }

    public float getOverlap(Projection other) {
        return Math.min(max, other.max) - Math.max(min, other.min);
    }

    public boolean contains(Projection other)
    {
        return other.min > min && other.max < max;
    }
}
