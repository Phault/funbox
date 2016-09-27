package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Pool;

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

    public ShapeDrawingSystem() {

    }

    @Override
    protected void begin() {
        super.begin();

        shapeRenderer.setProjectionMatrix(cameraSystem.getMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
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

    private void renderDrawing(ShapeDrawing shapeDrawing) {
        shapeRenderer.setColor(shapeDrawing.getColor());
        if (shapeDrawing.getPointCount() > 2)
            shapeRenderer.polyline(shapeDrawing.getPoints().items, 0, shapeDrawing.getPoints().size);
        else if (shapeDrawing.getPointCount() == 2) {
            shapeRenderer.line(shapeDrawing.getPointX(0), shapeDrawing.getPointY(0),
                    shapeDrawing.getPointX(1), shapeDrawing.getPointY(1));
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
}
