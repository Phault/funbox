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
            if (shapeDrawing != null && shapeDrawing.getPointCount() > 2)
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
        shapeRenderer.polyline(shapeDrawing.getPoints().items, 0, shapeDrawing.getPoints().size);
    }

    public ShapeDrawing createDrawing(Color color) {
        ShapeDrawing shapeDrawing = drawingPool.obtain();
        shapeDrawing.setColor(color);
        activeDrawings.add(shapeDrawing);
        return shapeDrawing;
    }

    public void destroyDrawing(ShapeDrawing shapeDrawing) {
        activeDrawings.remove(shapeDrawing);
        drawingPool.free(shapeDrawing);
    }
}
