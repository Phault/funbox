package com.phault.funbox.systems;

import com.artemis.BaseSystem;
import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;
import com.phault.funbox.box2d.systems.CollisionSystem;
import com.phault.funbox.scenegraph.components.Transform;
import com.phault.funbox.scenegraph.systems.WorldTransformationManager;
import com.phault.funbox.shaperendering.components.RenderPolygon;
import com.phault.funbox.shaperendering.utils.VertexArray;
import com.phault.funbox.systems.shapes.ShapeSpawner;
import com.phault.funbox.utils.MathHelper;

/**
 * Created by Casper on 06-09-2016.
 */
public class ShapeSpawnSystem extends BaseSystem implements InputProcessor {

    private CollisionSystem collisionSystem;
    private WorldTransformationManager worldTransformationManager;
    private InputSystem inputSystem;
    private CameraSystem cameraSystem;

    private BodyDef bodyDef;
    private FixtureDef fixtureDef;
    private final EarClippingTriangulator triangulator = new EarClippingTriangulator();

    private PolygonShape polygonShape;

    private final Array<ShapeSpawner> spawners = new Array<>();
    private ShapeSpawner currentSpawner;

    @Override
    protected void initialize() {
        super.initialize();

        inputSystem.addProcessor(this);

        polygonShape = new PolygonShape();

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        fixtureDef = new FixtureDef();
        fixtureDef.density = 10;
    }

    @Override
    protected void dispose() {
        super.dispose();

        inputSystem.removeProcessor(this);
    }

    @Override
    protected void processSystem() {

    }

    public int spawnPolygon(float x, float y, VertexArray vertices, Color color) {
        int polygon = world.create();
        EntityEdit edit = world.edit(polygon);
        edit.create(Transform.class);

        RenderPolygon renderPolygon = edit.create(RenderPolygon.class);
        renderPolygon.color.set(color);
        renderPolygon.vertices = vertices;
        ShortArray triangulation = triangulator.computeTriangles(vertices.getBackingArray());
        renderPolygon.triangulation.addAll(triangulation);

        Body body = createBody(polygon);
        createPolygonFixtures(body, vertices, triangulation);

        worldTransformationManager.setWorldPosition(polygon, x, y);

        return polygon;
    }

    private final float[] tmpTriangle = new float[6];

    private void createPolygonFixtures(Body body, VertexArray vertices, ShortArray triangulation) {
        float skinRadius = polygonShape.getRadius();
        for (int i = 0; i < triangulation.size; i += 3) {
            for (int j = 0; j < 3; j++) {
                int polygonVertex = triangulation.get(i+j);
                float scaledX = collisionSystem.getMetersPerPixel() * vertices.getX(polygonVertex);
                float scaledY = collisionSystem.getMetersPerPixel() * vertices.getY(polygonVertex);

                int triangleVertex = j*2;
                tmpTriangle[triangleVertex] = scaledX;
                tmpTriangle[triangleVertex+1] = scaledY;
            }

            subtractSkinRadius(tmpTriangle, skinRadius*2);

            GeometryUtils.ensureCCW(tmpTriangle);

            if (!CollisionSystem.isTriangleValid(tmpTriangle))
                continue;

            polygonShape.set(tmpTriangle);
            createFixture(body, polygonShape);
        }
    }

    private static final Vector2 tmpVertex = new Vector2();
    public static void subtractSkinRadius(float[] polygon, float skinRadius) {
        for (int i = 0; i < polygon.length; i += 2) {
            MathHelper.moveTowards(polygon[i], polygon[i + 1], 0, 0, skinRadius, tmpVertex);

            polygon[i] = tmpVertex.x;
            polygon[i+1] = tmpVertex.y;
        }
    }

    public float getScaleModifier() {
        return cameraSystem.getZoom();
    }

    public Body createBody(int entityId) {
        return collisionSystem.createBody(entityId, bodyDef);
    }

    public Fixture createFixture(Body body, Shape shape) {
        fixtureDef.shape = shape;
        return body.createFixture(fixtureDef);
    }

    public void addSpawner(ShapeSpawner spawner) {
        if (spawner != null && !spawners.contains(spawner, true)) {
            spawners.add(spawner);

            if (currentSpawner == null)
                setCurrentSpawner(spawner);
        }
    }

    public void removeSpawner(ShapeSpawner spawner) {
        spawners.removeValue(spawner, true);

        if (currentSpawner == spawner)
            setCurrentSpawner(spawners.size > 0 ? spawners.first() : null);
    }

    public Array<ShapeSpawner> getSpawners() {
        return spawners;
    }

    public ShapeSpawner getCurrentSpawner() {
        return currentSpawner;
    }


    public void setCurrentSpawner(ShapeSpawner currentSpawner) {
        this.currentSpawner = currentSpawner;
    }

    @Override
    public boolean keyDown(int keycode) {
        ShapeSpawner spawner = getCurrentSpawner();
        if (spawner != null)
            return spawner.keyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        ShapeSpawner spawner = getCurrentSpawner();
        if (spawner != null)
            return spawner.keyUp(keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        ShapeSpawner spawner = getCurrentSpawner();
        if (spawner != null)
            return spawner.keyTyped(character);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        ShapeSpawner spawner = getCurrentSpawner();
        if (spawner != null)
            return spawner.touchDown(screenX, screenY, pointer, button);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        ShapeSpawner spawner = getCurrentSpawner();
        if (spawner != null)
            return spawner.touchUp(screenX, screenY, pointer, button);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        ShapeSpawner spawner = getCurrentSpawner();
        if (spawner != null)
            return spawner.touchDragged(screenX, screenY, pointer);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        ShapeSpawner spawner = getCurrentSpawner();
        if (spawner != null)
            return spawner.mouseMoved(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        ShapeSpawner spawner = getCurrentSpawner();
        if (spawner != null)
            return spawner.scrolled(amount);
        return false;
    }
}
