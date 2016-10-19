package com.phault.funbox.systems;

import com.artemis.BaseSystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.phault.artemis.essentials.systems.CameraSystem;

/**
 * Created by Casper on 24-09-2016.
 */
public class ShapeDrawingSystem extends BaseSystem {

    private Bag<ShapeDrawing> activeDrawings = new Bag<>();

    private Pool<ShapeDrawing> drawingPool = new Pool<ShapeDrawing>() {
        @Override
        protected ShapeDrawing newObject() {
            return new ShapeDrawing();
        }
    };

    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private CameraSystem cameraSystem;

    private float lineWidth = 3;

    public ShapeDrawingSystem() {

    }

    @Override
    protected void begin() {
        super.begin();

        shapeRenderer.setProjectionMatrix(cameraSystem.getMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    }

    @Override
    protected void processSystem() {
        for (int i = 0; i < activeDrawings.size(); i++) {
            ShapeDrawing shapeDrawing = activeDrawings.get(i);
            if (shapeDrawing != null)
                renderDrawing(shapeDrawing);
        }
    }

    @Override
    protected void end() {
        super.end();

        shapeRenderer.end();
    }

    private final Vector2 current = new Vector2();
    private final Vector2 next = new Vector2();

    private void renderDrawing(ShapeDrawing shapeDrawing) {
        if (shapeDrawing.getPointCount() <= 1)
            return;

        shapeRenderer.setColor(shapeDrawing.getColor());

        for (int i = 0; i < shapeDrawing.getPointCount(); i++) {
            shapeDrawing.getPoint(i, current);
            shapeDrawing.getPointWrapping(i+1, next);

            shapeRenderer.rectLine(current.x, current.y,
                    next.x, next.y,
                    lineWidth * cameraSystem.getZoom());
        }
    }

    public ShapeDrawing createDrawing(Color color, float minimumPointDistance) {
        ShapeDrawing shapeDrawing = drawingPool.obtain();
        shapeDrawing.setColor(color);
        shapeDrawing.setMinimumPointDistance(minimumPointDistance);
        activeDrawings.add(shapeDrawing);
        return shapeDrawing;
    }

    public void destroyDrawing(ShapeDrawing shapeDrawing) {
        activeDrawings.remove(shapeDrawing);
        drawingPool.free(shapeDrawing);
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
}
