package com.phault.funbox.systems.shapes;

import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.phault.artemis.essentials.scenegraph.components.Transform;
import com.phault.artemis.essentials.shaperendering.components.RenderRectangle;

/**
 * Created by Casper on 12-10-2016.
 */
@Wire(injectInherited = true)
public class SquareSpawner extends SimpleShapeSpawner {

    public final Vector2 minSize = new Vector2(25, 25);
    public final Vector2 maxSize = new Vector2(200, 200);
    private PolygonShape shape;

    @Override
    public String iconPath() {
        return "icon_square";
    }

    @Override
    protected void initialize() {
        super.initialize();

        shape = new PolygonShape();
    }

    @Override
    protected void draw(int pointer, ShapeSketch sketch) {
        float x = Math.min(sketch.left, sketch.right);
        float y = Math.min(sketch.top, sketch.bottom);

        shapeRenderSystem.drawRect(x, y, sketch.width(), sketch.height());
    }

    @Override
    public int spawn(float x, float y, Color color) {
        float width = MathUtils.random(minSize.x, maxSize.x) * shapeSpawnSystem.getScaleModifier();
        float height = MathUtils.random(minSize.y, maxSize.y) * shapeSpawnSystem.getScaleModifier();

        return spawn(x, y, width, height, color);
    }

    @Override
    public int spawn(ShapeSketch sketch) {

        float x = MathUtils.lerp(sketch.left, sketch.right, 0.5f);
        float y = MathUtils.lerp(sketch.top, sketch.bottom, 0.5f);

        return spawn(x, y, sketch.width(), sketch.height(), sketch.color);
    }

    public int spawn(float x, float y, float width, float height, Color color) {
        int cube = world.create();
        EntityEdit edit = world.edit(cube);
        edit.create(Transform.class);

        Body body = shapeSpawnSystem.createBody(cube);
        float skinWidth = shape.getRadius();
        shape.setAsBox(width * 0.5f * collisionSystem.getMetersPerPixel() - skinWidth*0.5f,
                height * 0.5f * collisionSystem.getMetersPerPixel() - skinWidth*0.5f);
        shapeSpawnSystem.createFixture(body, shape);

        RenderRectangle rectangle = edit.create(RenderRectangle.class);
        rectangle.width = width;
        rectangle.height = height;
        rectangle.color.set(color);

        worldTransformationManager.setWorldPosition(cube, x, y);

        return cube;
    }
}
