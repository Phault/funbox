package com.phault.funbox.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.IntMap;
import com.phault.funbox.box2d.systems.CollisionSystem;

/**
 * Created by Casper on 18-09-2016.
 */
public class ShapeDragSystem extends BaseSystem implements InputProcessor {

    private CollisionSystem collisionSystem;
    private InputSystem inputSystem;
    private CameraSystem cameraSystem;

    private MouseJointDef mouseJointDef;
    private Body groundBody;

    private final Vector2 worldPoint = new Vector2();
    private final Vector2 physicsWorldPoint = new Vector2();

    private final IntMap<MouseJoint> activeDrags = new IntMap<>();

    @Override
    protected void initialize() {
        super.initialize();

        groundBody = collisionSystem.getPhysicsWorld().createBody(new BodyDef());

        mouseJointDef = new MouseJointDef();
        mouseJointDef.bodyA = groundBody;
        mouseJointDef.collideConnected = false;

        inputSystem.addProcessor(this);
    }

    @Override
    protected void processSystem() {
        for (IntMap.Entry<MouseJoint> entry : activeDrags) {
            int pointer = entry.key;
            int screenX = Gdx.input.getX(pointer);
            int screenY = Gdx.input.getY(pointer);

            cameraSystem.screenToWorld(screenX, screenY, worldPoint);
            physicsWorldPoint.set(worldPoint).scl(collisionSystem.getMetersPerPixel());
            entry.value.setTarget(physicsWorldPoint);
        }
    }

    @Override
    protected void dispose() {
        super.dispose();

        inputSystem.removeProcessor(this);
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
        if (button != Input.Buttons.LEFT)
            return false;

        if (isDragging(pointer))
            return true;

        cameraSystem.screenToWorld(screenX, screenY, worldPoint);

        physicsWorldPoint.set(worldPoint).scl(collisionSystem.getMetersPerPixel());
        Body body = collisionSystem.queryPoint(physicsWorldPoint.x, physicsWorldPoint.y);

        if (body != null) {
            startDrag(body, pointer, physicsWorldPoint);
            return true;
        }

        return false;
    }

    public void startDrag(Body body, int pointer, Vector2 physicsWorldPoint) {
        if (isDragging(pointer))
            return;

        mouseJointDef.bodyB = body;
        mouseJointDef.maxForce = 500 * body.getMass();
        mouseJointDef.target.set(physicsWorldPoint);

        MouseJoint joint = (MouseJoint) collisionSystem.getPhysicsWorld().createJoint(mouseJointDef);
        activeDrags.put(pointer, joint);

        body.setAwake(true);
    }

    public boolean isDragging(int pointer) {
        return activeDrags.containsKey(pointer);
    }

    public void endDrag(int pointer) {
        MouseJoint joint = activeDrags.remove(pointer);
        if (joint != null)
            collisionSystem.getPhysicsWorld().destroyJoint(joint);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT)
            return false;

        if (isDragging(pointer)) {
            endDrag(pointer);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return isDragging(pointer);
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
