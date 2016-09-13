package com.mygdx.game.shaperendering.systems;

import com.artemis.*;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.scenegraph.components.Transform;
import com.mygdx.game.scenegraph.systems.WorldTransformationManager;
import com.mygdx.game.shaperendering.components.RenderCircle;
import com.mygdx.game.shaperendering.components.RenderRectangle;
import com.mygdx.game.shaperendering.components.RenderShape;
import com.mygdx.game.shaperendering.components.RenderTriangle;
import com.mygdx.game.systems.CameraSystem;

/**
 * Created by Casper on 13-09-2016.
 */
public class ShapeRenderSystem extends IteratingSystem {

    private ComponentMapper<RenderRectangle> mRectangles;
    private ComponentMapper<RenderCircle> mCircles;
    private ComponentMapper<RenderTriangle> mTriangles;

    private CameraSystem cameraSystem;
    private WorldTransformationManager transformManager;

    private final Vector2 position = new Vector2();
    private final Vector2 scale = new Vector2();
    private float degrees = 0;
    private int currentEntityId = -1;

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    public ShapeRenderSystem() {
        super(Aspect.all(Transform.class).one(RenderCircle.class, RenderRectangle.class, RenderTriangle.class));
    }

    @Override
    protected void begin() {
        super.begin();

        shapeRenderer.setProjectionMatrix(cameraSystem.getMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    }

    @Override
    protected void process(int entityId) {
        transformManager.getWorldPosition(entityId, position);
        transformManager.getWorldScale(entityId, scale);
        degrees = transformManager.getWorldRotation(entityId);
        currentEntityId = entityId;

        RenderShape shape = mRectangles.get(entityId);
        if (shape != null)
            renderRectangle((RenderRectangle) shape);

        shape = mCircles.get(entityId);
        if (shape != null)
            renderCircle((RenderCircle) shape);

        shape = mTriangles.get(entityId);
        if (shape != null)
            renderTriangle((RenderTriangle) shape);
    }

    private void renderRectangle(RenderRectangle rectangle) {
        float originX = rectangle.origin.x * rectangle.width;
        float originY = rectangle.origin.y * rectangle.height;

        shapeRenderer.setColor(rectangle.color);
        shapeRenderer.rect(position.x - originX,
                position.y - originY,
                originX,
                originY,
                rectangle.width,
                rectangle.height,
                scale.x,
                scale.y,
                degrees);
    }

    private void renderCircle(RenderCircle circle) {
        float width = circle.radius * 2 * scale.x;
        float height = circle.radius * 2 * scale.y;

        float originX = circle.origin.x * width;
        float originY = circle.origin.y * height;

        shapeRenderer.setColor(circle.color);
        shapeRenderer.ellipse(position.x - originX,
                position.y - originY,
                width,
                height,
                degrees);
    }

    private void renderTriangle(RenderTriangle triangle) {
        shapeRenderer.getTransformMatrix().set(transformManager.getLocalToWorldMatrix(currentEntityId));
        shapeRenderer.updateMatrices();

        shapeRenderer.setColor(triangle.color);
        shapeRenderer.triangle(triangle.points[0].x, triangle.points[0].y,
                triangle.points[1].x, triangle.points[1].y,
                triangle.points[2].x, triangle.points[2].y);

        shapeRenderer.identity();
    }

    @Override
    protected void end() {
        super.end();

        shapeRenderer.end();
    }

    @Override
    protected void dispose() {
        super.dispose();

        shapeRenderer.dispose();
    }
}
