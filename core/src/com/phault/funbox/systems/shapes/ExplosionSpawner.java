package com.phault.funbox.systems.shapes;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.phault.funbox.box2d.utils.RaycastClosestCallback;

/**
 * Created by Casper on 12-10-2016.
 */
@Wire(injectInherited = true)
public class ExplosionSpawner extends SimpleShapeSpawner {
    public float radius = 300;
    public float power = 400;
    public int rays = 60;

    private final RaycastClosestCallback callback = new RaycastClosestCallback();

    @Override
    public String iconPath() {
        return "icon_explosion";
    }

    @Override
    protected void draw(int pointer, ShapeSketch sketch) {
        float radius = sketch.dst();

        shapeRenderSystem.setShapeType(ShapeRenderer.ShapeType.Line);
        shapeRenderSystem.drawCircle(sketch.left, sketch.top, radius);
    }

    @Override
    protected boolean isSketchValid(ShapeSketch sketch) {
        return sketch.isValid() && sketch.dst() > 5;
    }

    @Override
    public int spawn(float x, float y) {
        spawn(x, y, radius * shapeSpawnSystem.getScaleModifier());
        return -1;
    }

    @Override
    public int spawn(ShapeSketch sketch) {
        float radius = sketch.dst();
        spawn(sketch.left, sketch.top, radius);

        return -1;
    }

    public void spawn(float x, float y, float radius) {
        float scaleModifier = radius / this.radius;
        spawn(x, y, radius, power * scaleModifier, (int) (rays * scaleModifier));
    }

    public void spawn(float x, float y, float radius, float power, int rayCount) {
        x *= collisionSystem.getMetersPerPixel();
        y *= collisionSystem.getMetersPerPixel();
        radius *= collisionSystem.getMetersPerPixel();

        float powerEach = power / rayCount;
        float deltaAngle = MathUtils.PI2 / rayCount;
        for (int i = 0; i < rayCount; i++) {
            float angle = deltaAngle * i;
            float dirX = MathUtils.cos(angle);
            float dirY = MathUtils.sin(angle);
            float endX = x + radius * dirX;
            float endY = y + radius * dirY;

            callback.reset();
            collisionSystem.getPhysicsWorld().rayCast(callback, x, y, endX, endY);
            if (callback.fixture != null) {
                Body body = callback.fixture.getBody();
                float fraction = 1-callback.fraction;
                body.applyLinearImpulse(dirX * powerEach * fraction,
                        dirY * powerEach * fraction,
                        callback.point.x, callback.point.y,
                        true);
            }
        }
    }
}
