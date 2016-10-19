package com.phault.funbox.systems.shapes;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ShortArray;
import com.phault.artemis.essentials.shaperendering.utils.VertexArray;
import com.phault.artemis.essentials.utils.PolygonUtils;

/**
 * Created by Casper on 12-10-2016.
 */
@Wire(injectInherited = true)
public class NGonSpawner extends SimpleShapeSpawner {

    public int minSides = 5, maxSides = 9;
    public float minRadius = 10, maxRadius = 100;

    private final int defaultSides = 6;

    @Override
    public String iconPath() {
        return "icon_ngon";
    }

    private final float[] defaultNGon = new float[defaultSides * 2];
    private final EarClippingTriangulator triangulator = new EarClippingTriangulator();

    @Override
    protected void draw(int pointer, ShapeSketch sketch) {
        float radius = sketch.dst();
        generateNGon(defaultNGon, radius);
        PolygonUtils.offsetPolygon(defaultNGon, sketch.left, sketch.top);
        ShortArray triangulation = triangulator.computeTriangles(defaultNGon);
        shapeRenderSystem.drawPolygon(defaultNGon, triangulation);
    }

    @Override
    protected boolean isSketchValid(ShapeSketch sketch) {
        return sketch.isValid() && sketch.dst() > 5;
    }

    @Override
    public int spawn(float x, float y, Color color) {
        int sides = MathUtils.random(minSides, maxSides);
        float radius = MathUtils.random(minRadius, maxRadius) * shapeSpawnSystem.getScaleModifier();
        return spawn(x, y, sides, radius, color);
    }

    @Override
    public int spawn(ShapeSketch sketch) {
        float radius = sketch.dst();
        return spawn(sketch.left, sketch.top, defaultSides, radius, sketch.color);
    }

    public int spawn(float x, float y, int sides, float radius, Color color) {
        return shapeSpawnSystem.spawnPolygon(x, y, generateNGon(sides, radius), color);
    }

    private VertexArray generateNGon(int sides, float radius) {
        VertexArray vertices = new VertexArray(sides);
        generateNGon(vertices.getBackingArray(), radius);
        return vertices;
    }

    private float[] generateNGon(float[] container, float radius) {
        int sides = container.length / 2;

        float degreesPerSide = 360f / sides;

        for (int i = 0; i < container.length; i += 2) {
            float degrees = i / 2 * degreesPerSide;
            container[i] = MathUtils.cosDeg(degrees) * radius;
            container[i + 1] = MathUtils.sinDeg(degrees) * radius;
        }

        return container;
    }
}
