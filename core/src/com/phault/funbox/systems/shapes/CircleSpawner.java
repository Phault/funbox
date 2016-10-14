package com.phault.funbox.systems.shapes;

import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.phault.funbox.scenegraph.components.Transform;
import com.phault.funbox.shaperendering.components.RenderCircle;

/**
 * Created by Casper on 12-10-2016.
 */
@Wire(injectInherited = true)
public class CircleSpawner extends SimpleShapeSpawner {
    public float minRadius = 10,
            maxRadius = 100;

    private CircleShape shape;

    @Override
    public String iconPath() {
        return "icon_circle";
    }

    @Override
    protected void initialize() {
        super.initialize();

        shape = new CircleShape();
    }

    @Override
    protected void draw(int pointer, ShapeSketch sketch) {
        float radius = Vector2.dst(sketch.left, sketch.top, sketch.right, sketch.bottom);
        shapeRenderSystem.drawCircle(sketch.left, sketch.top, radius);
    }

    @Override
    protected boolean isSketchValid(ShapeSketch sketch) {
        return sketch.isValid() && sketch.dst() > 5;
    }

    @Override
    public int spawn(float x, float y) {
        float radius = MathUtils.random(minRadius, maxRadius) * shapeSpawnSystem.getScaleModifier();
        return spawn(x, y, radius, getRandomColor());
    }

    @Override
    public int spawn(ShapeSketch sketch) {
        float radius = sketch.dst();
        return spawn(sketch.left, sketch.top, radius, sketch.color);
    }

    public int spawn(float x, float y, float radius, Color color) {
        int circle = world.create();
        EntityEdit edit = world.edit(circle);
        edit.create(Transform.class);

        Body body = shapeSpawnSystem.createBody(circle);
        shape.setRadius(radius * collisionSystem.getMetersPerPixel());
        shapeSpawnSystem.createFixture(body, shape);

        RenderCircle renderCircle = edit.create(RenderCircle.class);
        renderCircle.radius = radius;
        renderCircle.color.set(color);

        worldTransformationManager.setWorldPosition(circle, x, y);

        return circle;
    }
}
