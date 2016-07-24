package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;

/**
 * Created by Casper on 24-07-2016.
 */
public class CameraSystem extends BaseSystem {

    private OrthographicCamera camera;

    public CameraSystem() {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false);
    }

    @Override
    protected void processSystem() {
        camera.update();
    }

    public Matrix4 getMatrix() {
        return camera.combined;
    }

    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
