package com.mygdx.game.shaperendering.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ShortArray;
import com.mygdx.game.scenegraph.components.Transform;
import com.mygdx.game.scenegraph.systems.WorldTransformationManager;
import com.mygdx.game.shaperendering.components.*;
import com.mygdx.game.shaperendering.utils.VertexArray;
import com.mygdx.game.systems.CameraSystem;

/**
 * Created by Casper on 13-09-2016.
 */
public class ShapeRenderSystem extends IteratingSystem {

    private ComponentMapper<RenderRectangle> mRectangles;
    private ComponentMapper<RenderCircle> mCircles;
    private ComponentMapper<RenderTriangle> mTriangles;
    private ComponentMapper<RenderPolygon> mPolygons;

    private CameraSystem cameraSystem;
    private WorldTransformationManager transformManager;

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    public ShapeRenderSystem() {
        super(Aspect.all(Transform.class)
                .one(
                        RenderCircle.class,
                        RenderRectangle.class,
                        RenderTriangle.class,
                        RenderPolygon.class
                ));
    }

    @Override
    protected void begin() {
        super.begin();

        shapeRenderer.setProjectionMatrix(cameraSystem.getMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    }

    @Override
    protected void process(int entityId) {
        shapeRenderer.getTransformMatrix().set(transformManager.getLocalToWorldMatrix(entityId));
        shapeRenderer.updateMatrices();

        RenderShape shape = mRectangles.get(entityId);
        if (shape != null)
            renderRectangle((RenderRectangle) shape);

        shape = mCircles.get(entityId);
        if (shape != null)
            renderCircle((RenderCircle) shape);

        shape = mTriangles.get(entityId);
        if (shape != null)
            renderTriangle((RenderTriangle) shape);


        shape = mPolygons.get(entityId);
        if (shape != null)
            renderPolygon((RenderPolygon) shape);

        shapeRenderer.identity();
    }

    private void renderRectangle(RenderRectangle rectangle) {
        float originX = rectangle.origin.x * rectangle.width;
        float originY = rectangle.origin.y * rectangle.height;

        shapeRenderer.setColor(rectangle.color);
        shapeRenderer.rect(-originX,
                -originY,
                rectangle.width,
                rectangle.height);
    }

    private void renderCircle(RenderCircle circle) {
        float diameter = circle.radius * 2;
        float originX = (-0.5f + circle.origin.x) * diameter;
        float originY = (-0.5f + circle.origin.y) * diameter;

        shapeRenderer.setColor(circle.color);
        shapeRenderer.circle(originX, originY, circle.radius);
    }

    private void renderTriangle(RenderTriangle triangle) {
        shapeRenderer.setColor(triangle.color);
        shapeRenderer.triangle(triangle.points[0].x, triangle.points[0].y,
                triangle.points[1].x, triangle.points[1].y,
                triangle.points[2].x, triangle.points[2].y);
    }

    private final Vector2[] tmpTriangle = new Vector2[] {
            new Vector2(),
            new Vector2(),
            new Vector2(),
    };

    private void renderPolygon(RenderPolygon polygon) {
        shapeRenderer.setColor(polygon.color);

        VertexArray vertices = polygon.vertices;
        ShortArray triangulation = polygon.triangulation;

        for (int i = 0; i < triangulation.size; i += 3) {
            vertices.get(triangulation.get(i), tmpTriangle[0]);
            vertices.get(triangulation.get(i + 1), tmpTriangle[1]);
            vertices.get(triangulation.get(i + 2), tmpTriangle[2]);
            shapeRenderer.triangle(tmpTriangle[0].x, tmpTriangle[0].y,
                    tmpTriangle[1].x, tmpTriangle[1].y,
                    tmpTriangle[2].x, tmpTriangle[2].y);
        }
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
