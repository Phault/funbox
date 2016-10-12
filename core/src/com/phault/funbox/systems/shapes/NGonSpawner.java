package com.phault.funbox.systems.shapes;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.phault.funbox.shaperendering.utils.VertexArray;

/**
 * Created by Casper on 12-10-2016.
 */
@Wire(injectInherited = true)
public class NGonSpawner extends SimpleShapeSpawner {

    public int minSides = 5, maxSides = 9;
    public float minRadius = 10, maxRadius = 100;

    @Override
    public String iconPath() {
        return "icon_ngon";
    }

    @Override
    public int spawn(float x, float y) {
        int sides = minSides + MathUtils.random(minSides, maxSides);
        float radius = MathUtils.random(minRadius, maxRadius) * shapeSpawnSystem.getScaleModifier();
        return spawn(x, y, sides, radius, getRandomColor());
    }

    public int spawn(float x, float y, int sides, float radius, Color color) {
        return shapeSpawnSystem.spawnPolygon(x, y, generateNGon(sides, radius), color);
    }

    private VertexArray generateNGon(int sides, float radius) {
        sides = MathUtils.clamp(sides, 3, 8);

        VertexArray vertices = new VertexArray(sides);
        float degreesPerSide = 360f / sides;

        for (int i = 0; i < vertices.size(); i++) {
            float degrees = i * degreesPerSide;
            float x = MathUtils.cosDeg(degrees) * radius;
            float y = MathUtils.sinDeg(degrees) * radius;
            vertices.set(i, x, y);
        }

        return vertices;
    }
}
