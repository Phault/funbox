package com.phault.funbox.systems.shapes;

import com.artemis.BaseSystem;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.phault.funbox.box2d.systems.CollisionSystem;
import com.phault.funbox.scenegraph.systems.WorldTransformationManager;
import com.phault.funbox.systems.CameraSystem;
import com.phault.funbox.systems.ShapeSpawnSystem;

/**
 * Created by Casper on 12-10-2016.
 */
public abstract class ShapeSpawner extends BaseSystem implements InputProcessor {

    protected ShapeSpawnSystem shapeSpawnSystem;
    protected CameraSystem cameraSystem;
    protected CollisionSystem collisionSystem;
    protected WorldTransformationManager worldTransformationManager;
    private final Vector2 worldPos = new Vector2();

    public abstract String iconPath();

    @Override
    protected void initialize() {
        super.initialize();

        shapeSpawnSystem.addSpawner(this);
    }

    @Override
    protected void dispose() {
        super.dispose();

        shapeSpawnSystem.removeSpawner(this);
    }

    protected Vector2 screenToWorld(int x, int y) {
        return cameraSystem.screenToWorld(x, y, worldPos);
    }

    private final Color tmpColor = new Color();
    protected Color getRandomColor() {
        tmpColor.set(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
        return tmpColor;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
