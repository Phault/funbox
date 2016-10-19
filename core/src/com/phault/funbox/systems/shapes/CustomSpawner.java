package com.phault.funbox.systems.shapes;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.phault.artemis.essentials.shaperendering.utils.VertexArray;
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

            float firstX = shapeDrawing.getPointX(0);
            float firstY = shapeDrawing.getPointY(0);

            shapeDrawing.center();

            float deltaFirstX = firstX - shapeDrawing.getPointX(0);
            float deltaFirstY = firstY - shapeDrawing.getPointY(0);

            shapeDrawing.optimize(baseMinIntermediatePointDistance * shapeSpawnSystem.getScaleModifier());

            if (shapeDrawing.isValid()) {
                VertexArray vertices = new VertexArray(shapeDrawing.getPointCount());
                for (int i = 0; i < vertices.size(); i++)
                    vertices.set(i, shapeDrawing.getPointX(i), shapeDrawing.getPointY(i));
                GeometryUtils.ensureCCW(vertices.getBackingArray());

                shapeSpawnSystem.spawnPolygon(deltaFirstX, deltaFirstY, vertices, shapeDrawing.getColor());
            }

            drawingSystem.destroyDrawing(shapeDrawing);

            return true;
        }

        return false;
    }
}
