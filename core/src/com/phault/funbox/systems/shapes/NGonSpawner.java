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
        float radius = sketch.dst() / 2;
        float angle = getAngle(sketch);
        generateNGon(defaultNGon, radius, angle);
        PolygonUtils.offsetPolygon(defaultNGon, sketch.centerX(), sketch.centerY());
        ShortArray triangulation = triangulator.computeTriangles(defaultNGon);
        shapeRenderSystem.drawPolygon(defaultNGon, triangulation);
    }

    private float getAngle(ShapeSketch sketch) {
        float deltaX = sketch.right - sketch.left;
        float deltaY = sketch.top - sketch.bottom;
        return MathUtils.atan2(-deltaY, deltaX) * MathUtils.radDeg;
    }

    @Override
    protected boolean isSketchValid(ShapeSketch sketch) {
        return sketch.isValid() && sketch.dst() > 5;
    }

    @Override
    public int spawn(float x, float y, Color color) {
        int sides = MathUtils.random(minSides, maxSides);
        float radius = MathUtils.random(minRadius, maxRadius) * shapeSpawnSystem.getScaleModifier();
        return spawn(x, y, sides, radius, color, 0);
    }

    @Override
    public int spawn(ShapeSketch sketch) {
        float radius = sketch.dst() / 2;
        float angle = getAngle(sketch);
        return spawn(sketch.centerX(), sketch.centerY(), defaultSides, radius, sketch.color, angle);
    }

    public int spawn(float x, float y, int sides, float radius, Color color, float rotation) {
        return shapeSpawnSystem.spawnPolygon(x, y, generateNGon(sides, radius, rotation), color);
    }

    private VertexArray generateNGon(int sides, float radius, float rotation) {
        VertexArray vertices = new VertexArray(sides);
        generateNGon(vertices.getBackingArray(), radius, rotation);
        return vertices;
    }

    private float[] generateNGon(float[] container, float radius, float rotation) {
        int sides = container.length / 2;

        float degreesPerSide = 360f / sides;

        for (int i = 0; i < container.length; i += 2) {
            container[i] = MathUtils.cosDeg(rotation) * radius;
            container[i + 1] = MathUtils.sinDeg(rotation) * radius;
            rotation += degreesPerSide;
        }

        return container;
    }
}
