package com.mygdx.game.utils;

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
}
