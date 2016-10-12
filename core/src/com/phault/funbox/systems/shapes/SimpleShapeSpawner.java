package com.phault.funbox.systems.shapes;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.phault.funbox.systems.ShapeDragSystem;

/**
 * Created by Casper on 12-10-2016.
 */
public abstract class SimpleShapeSpawner extends ShapeSpawner {

    @Wire(failOnNull = false)
    private ShapeDragSystem dragSystem;

    @Override
    protected void processSystem() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 worldPos = screenToWorld(screenX, screenY);

        int spawnedId = spawn(worldPos.x, worldPos.y);
        if (spawnedId != -1 && dragSystem != null) {
            Body body = collisionSystem.getAttachedBody(spawnedId);
            worldPos.scl(collisionSystem.getMetersPerPixel());
            dragSystem.startDrag(body, pointer, worldPos);
        }

        return true;
    }

    public abstract int spawn(float x, float y);
}
