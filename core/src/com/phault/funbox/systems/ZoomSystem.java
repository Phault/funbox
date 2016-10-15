package com.phault.funbox.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Casper on 18-09-2016.
 */
public class ZoomSystem extends BaseSystem implements InputProcessor {

    public float minZoom = 1f, maxZoom = 10;
    public float step = 0.1f;

    public float smoothingSpeed = 3;

    private float target;

    private CameraSystem cameraSystem;
    private InputSystem inputSystem;

    @Override
    protected void initialize() {
        super.initialize();

        inputSystem.addProcessor(this);

        target = cameraSystem.getZoom();
        scrolled(0);
    }

    @Override
    protected void dispose() {
        super.dispose();

        inputSystem.removeProcessor(this);
    }

    @Override
    protected void processSystem() {
        float current = cameraSystem.getZoom();
        cameraSystem.setZoom(MathUtils.lerp(current, target, smoothingSpeed * world.getDelta()));
        cameraSystem.getCamera().update();

        // make sure to keep the bottom of the camera at 0 in y
        cameraSystem.screenToWorld(0, Gdx.graphics.getHeight() - 25, tmpPos);
        tmpPos.x = 0;
        tmpPos.scl(-1).add(cameraSystem.getPosition().x, cameraSystem.getPosition().y);
        cameraSystem.setPosition(tmpPos.x, tmpPos.y);
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

    private final Vector2 tmpPos = new Vector2();
    @Override
    public boolean scrolled(int amount) {

        target = MathUtils.clamp(target + step * amount, minZoom, maxZoom);
        return true;
    }
}
