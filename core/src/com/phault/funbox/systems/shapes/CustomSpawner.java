package com.phault.funbox.systems.shapes;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.phault.funbox.shaperendering.utils.VertexArray;
import com.phault.funbox.systems.ShapeDrawing;
import com.phault.funbox.systems.ShapeDrawingSystem;

/**
 * Created by Casper on 12-10-2016.
 */
@Wire(injectInherited = true)
public class CustomSpawner extends ShapeSpawner {

    public float minimumDrawingPointDistance = 5;
    public float baseMinIntermediatePointDistance = 2;

    private ShapeDrawingSystem drawingSystem;
    private final IntMap<ShapeDrawing> activeDrawings = new IntMap<>();

    @Override
    public String iconPath() {
        return "icon_custom";
    }

    @Override
    protected void processSystem() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 worldPos = screenToWorld(screenX, screenY);

        if (!activeDrawings.containsKey(pointer)) {
            ShapeDrawing shapeDrawing = drawingSystem.createDrawing(getRandomColor(),
                    minimumDrawingPointDistance * shapeSpawnSystem.getScaleModifier());
            shapeDrawing.addPoint(worldPos.x, worldPos.y);
            activeDrawings.put(pointer, shapeDrawing);
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        ShapeDrawing shapeDrawing = activeDrawings.get(pointer);

        if (shapeDrawing != null) {
            Vector2 worldPos = screenToWorld(screenX, screenY);

            if (shapeDrawing.isValidForNextPoint(worldPos.x, worldPos.y))
                shapeDrawing.addPoint(worldPos.x, worldPos.y);

            return true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        ShapeDrawing shapeDrawing = activeDrawings.get(pointer);

        if (shapeDrawing != null) {
            activeDrawings.remove(pointer);
            spawnDrawing(shapeDrawing);
            drawingSystem.destroyDrawing(shapeDrawing);

            return true;
        }

        return false;
    }

    private int spawnDrawing(ShapeDrawing drawing) {
        float firstX = drawing.getPointX(0);
        float firstY = drawing.getPointY(0);

        drawing.center();

        float deltaFirstX = firstX - drawing.getPointX(0);
        float deltaFirstY = firstY - drawing.getPointY(0);

        drawing.optimize(baseMinIntermediatePointDistance * shapeSpawnSystem.getScaleModifier());

        if (drawing.isValid()) {
            VertexArray vertices = new VertexArray(drawing.getPointCount());
            for (int i = 0; i < vertices.size(); i++)
                vertices.set(i, drawing.getPointX(i), drawing.getPointY(i));
            GeometryUtils.ensureCCW(vertices.getBackingArray());

            return shapeSpawnSystem.spawnPolygon(deltaFirstX, deltaFirstY, vertices, drawing.getColor());
        }

        return -1;
    }

    @Override
    public void endTouches() {
        super.endTouches();

        for (IntMap.Entry<ShapeDrawing> entry : activeDrawings.entries()) {
            int pointer = entry.key;
            touchUp(Gdx.input.getX(pointer), Gdx.input.getY(pointer), pointer, 0);
        }
    }
}
