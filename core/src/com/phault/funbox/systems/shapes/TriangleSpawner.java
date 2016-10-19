package com.phault.funbox.systems.shapes;

import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.phault.artemis.essentials.scenegraph.components.Transform;
import com.phault.artemis.essentials.shaperendering.components.RenderTriangle;
import com.phault.artemis.essentials.utils.PolygonUtils;
import com.phault.funbox.systems.ShapeSpawnSystem;

/**
 * Created by Casper on 12-10-2016.
 */
@Wire(injectInherited = true)
public class TriangleSpawner extends SimpleShapeSpawner {
    public final Vector2 minSize = new Vector2(25, 25);
    public final Vector2 maxSize = new Vector2(200, 200);

    private float minAngle = 30, maxAngle = 150;

    private PolygonShape shape;

    @Override
    public String iconPath() {
        return "icon_triangle";
    }

    @Override
    protected void initialize() {
        super.initialize();

        shape = new PolygonShape();
    }

    @Override
    protected void draw(int pointer, ShapeSketch sketch) {
        float[] triangle = triangleFromSketch(sketch);
        shapeRenderSystem.drawTriangle(triangle);
    }

    @Override
    public int spawn(float x, float y, Color color) {
        float[] triangle = generateTriangle(minSize.x * shapeSpawnSystem.getScaleModifier(),
                maxSize.x * shapeSpawnSystem.getScaleModifier());
        return spawn(x, y, triangle, color);
    }

    private final Vector2 tmpCenter = new Vector2();

    @Override
    public int spawn(ShapeSketch sketch) {
        float[] triangle = triangleFromSketch(sketch);
        Vector2 center = PolygonUtils.getPolygonCenter(triangle, tmpCenter);
        PolygonUtils.offsetPolygon(triangle, -center.x, -center.y);
        return spawn(center.x, center.y, triangle, sketch.color);
    }

    public int spawn(float x, float y, float[] polygon, Color color) {
        int triangle = world.create();
        EntityEdit edit = world.edit(triangle);
        edit.create(Transform.class);

        RenderTriangle renderTriangle = edit.create(RenderTriangle.class);
        renderTriangle.color.set(color);

        for (int i = 0; i < renderTriangle.points.length; i++)
            renderTriangle.points[i].set(polygon[i*2], polygon[i*2+1]);

        for (int i = 0; i < polygon.length; i++)
            polygon[i] *= collisionSystem.getMetersPerPixel();

        ShapeSpawnSystem.subtractSkinRadius(polygon, shape.getRadius() * 2);
        PolygonUtils.centerPolygon(polygon);
        shape.set(polygon);

        Body body = shapeSpawnSystem.createBody(triangle);
        shapeSpawnSystem.createFixture(body, shape);

        worldTransformationManager.setWorldPosition(triangle, x, y);

        return triangle;
    }

    private final float[] tmpTriangle = new float[6];

    private float[] generateTriangle(float minEdgeLength, float maxEdgeLength) {
        float firstAngle = MathUtils.random(360f);
        float secondAngle = firstAngle + MathUtils.random(minAngle, maxAngle);

        float firstLength = MathUtils.random(minEdgeLength, maxEdgeLength);
        float secondLength = MathUtils.random(minEdgeLength, maxEdgeLength);

        tmpTriangle[0] = 0;
        tmpTriangle[1] = 0;

        tmpTriangle[2] = MathUtils.cosDeg(firstAngle) * firstLength;
        tmpTriangle[3] = MathUtils.sinDeg(firstAngle) * firstLength;

        tmpTriangle[4] = MathUtils.cosDeg(secondAngle) * secondLength;
        tmpTriangle[5] = MathUtils.sinDeg(secondAngle) * secondLength;

        PolygonUtils.centerPolygon(tmpTriangle);

        return tmpTriangle;
    }

    private float[] triangleFromSketch(ShapeSketch sketch) {
        tmpTriangle[0] = sketch.left;
        tmpTriangle[1] = sketch.top;

        tmpTriangle[2] = sketch.left;
        tmpTriangle[3] = sketch.bottom;

        tmpTriangle[4] = sketch.right;
        tmpTriangle[5] = sketch.bottom;

        return tmpTriangle;
    }
}
