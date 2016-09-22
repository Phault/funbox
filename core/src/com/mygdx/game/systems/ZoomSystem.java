package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Casper on 18-09-2016.
 */
public class ZoomSystem extends BaseSystem implements InputProcessor {

    public float minZoom = 1f, maxZoom = 10;
    public float step = 0.1f;

    private CameraSystem cameraSystem;
    private InputSystem inputSystem;

    @Override
    protected void initialize() {
        super.initialize();

        inputSystem.addProcessor(this);
    }

    @Override
    protected void dispose() {
        super.dispose();

        inputSystem.removeProcessor(this);
    }

    @Override
    protected void processSystem() {

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

        float zoom = cameraSystem.getZoom();
        zoom += step * amount;
        zoom = MathUtils.clamp(zoom, minZoom, maxZoom);
        cameraSystem.setZoom(zoom);

        return true;
    }
}