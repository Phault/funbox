package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ShortArray;
import com.mygdx.game.box2d.systems.CollisionSystem;
import com.mygdx.game.scenegraph.components.Transform;
import com.mygdx.game.scenegraph.systems.WorldTransformationManager;
import com.mygdx.game.shaperendering.components.RenderCircle;
import com.mygdx.game.shaperendering.components.RenderPolygon;
import com.mygdx.game.shaperendering.components.RenderRectangle;
import com.mygdx.game.shaperendering.components.RenderTriangle;
import com.mygdx.game.shaperendering.utils.VertexArray;

import java.util.Random;

/**
 * Created by Casper on 06-09-2016.
 */
public class ShapeSpawnSystem extends BaseSystem implements InputProcessor {

    private CollisionSystem collisionSystem;
    private WorldTransformationManager worldTransformationManager;
    private CameraSystem cameraSystem;
    private InputSystem inputSystem;

    @Wire(failOnNull = false)
    private ShapeDragSystem dragSystem;

    @Wire(failOnNull = false)
    private ShapeDrawingSystem drawingSystem;

    private Vector2 minSize = new Vector2(25, 25), maxSize = new Vector2(200, 200);

    private float minTriangleAngle = 30, maxTriangleAngle = 150;

    private float minRadius = 10, maxRadius = 100;

    private final Random random = new Random();
    private BodyDef bodyDef;
    private FixtureDef fixtureDef;
    private final EarClippingTriangulator triangulator = new EarClippingTriangulator();

    private final Color tmpColor = new Color();

    private PolygonShape polygonShape;
    private CircleShape circleShape;

    private ShapeType activeType = ShapeType.Random;
    private float minimumDrawingPointDistance = 5;
    private ExplosionSystem explosionSystem;
    private float baseMinIntermediatePointDistance = 3;

    @Override
    protected void initialize() {
        super.initialize();

        inputSystem.addProcessor(this);

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        fixtureDef = new FixtureDef();
        fixtureDef.density = 10;

        circleShape = new CircleShape();
        polygonShape = new PolygonShape();
    }

    @Override
    protected void dispose() {
        super.dispose();

        inputSystem.removeProcessor(this);

        circleShape.dispose();
        polygonShape.dispose();
    }

    @Override
    protected void processSystem() {

    }

    public ShapeType getActiveType() {
        return activeType;
    }

    public void setActiveType(ShapeType activeType) {
        this.activeType = activeType;
    }

    private int spawnShape(ShapeType type, float x, float y) {
        switch (type) {
            case Cube:
                return spawnRandomCube(x, y);
            case Circle:
                return spawnRandomCircle(x, y);
            case Triangle:
                return spawnRandomTriangle(x, y);
            case NGon:
                return spawnRandomNGon(x, y);
            case Explosion:
                explosionSystem.spawnExplosion(x, y, 60, 100);
                return -1;
        }

        return spawnShape(ShapeType.getRandom(), x, y);
    }

    private int spawnRandomNGon(float x, float y) {
        int sides = 5 + random.nextInt(4);
        float radius = MathUtils.lerp(minRadius, maxRadius, random.nextFloat());
        return spawnNGon(x, y, sides, radius, getRandomColor());
    }

    private int spawnRandomCube(float x, float y) {
        float width = MathUtils.lerp(minSize.x, maxSize.x, random.nextFloat());
        float height = MathUtils.lerp(minSize.y, maxSize.y, random.nextFloat());

        return spawnCube(x, y, width, height, getRandomColor());
    }

    private int spawnRandomCircle(float x, float y) {
        float radius = MathUtils.lerp(minRadius, maxRadius, random.nextFloat());
        return spawnCircle(x, y, radius, getRandomColor());
    }

    public int spawnRandomTriangle(float x, float y) {
        Vector2[] triangle = generateTriangle(minSize.x, maxSize.x);
        return spawnTriangle(x, y, triangle, getRandomColor());
    }

    public int spawnCube(float x, float y, float width, float height, Color color) {
        int cube = world.create();
        EntityEdit edit = world.edit(cube);
        edit.create(Transform.class);

        Body boxBody = collisionSystem.createBody(cube, bodyDef);
        polygonShape.setAsBox(width * 0.5f * collisionSystem.getMetersPerPixel(),
                height * 0.5f * collisionSystem.getMetersPerPixel());
        fixtureDef.shape = polygonShape;
        boxBody.createFixture(fixtureDef);

        RenderRectangle rectangle = edit.create(RenderRectangle.class);
        rectangle.width = width;
        rectangle.height = height;
        rectangle.color.set(color);

        worldTransformationManager.setWorldPosition(cube, x, y);

        return cube;
    }

    public int spawnCircle(float x, float y, float radius, Color color) {
        int circle = world.create();
        EntityEdit edit = world.edit(circle);
        edit.create(Transform.class);

        Body body = collisionSystem.createBody(circle, bodyDef);
        circleShape.setRadius(radius * collisionSystem.getMetersPerPixel());
        fixtureDef.shape = circleShape;
        body.createFixture(fixtureDef);

        RenderCircle renderCircle = edit.create(RenderCircle.class);
        renderCircle.radius = radius;
        renderCircle.color.set(color);

        worldTransformationManager.setWorldPosition(circle, x, y);

        return circle;
    }

    private final Vector2[] genTriangle = new Vector2[] { new Vector2(), new Vector2(), new Vector2()};

    private Vector2[] generateTriangle(float minEdgeLength, float maxEdgeLength) {
        float firstAngle = 360f * random.nextFloat();
        float secondAngle = firstAngle + MathUtils.lerp(minTriangleAngle, maxTriangleAngle, random.nextFloat());

        float firstLength = MathUtils.lerp(minEdgeLength, maxEdgeLength, random.nextFloat());
        float secondLength = MathUtils.lerp(minEdgeLength, maxEdgeLength, random.nextFloat());

        genTriangle[0].set(0, 0);
        genTriangle[1].set(MathUtils.cosDeg(firstAngle), MathUtils.sinDeg(firstAngle)).scl(firstLength);
        genTriangle[2].set(MathUtils.cosDeg(secondAngle), MathUtils.sinDeg(secondAngle)).scl(secondLength);

        centerPolygon(genTriangle);

        return genTriangle;
    }
    private static void centerPolygon(Vector2[] polygon) {
        float x = 0;
        float y = 0;
        for (int i = 0; i < polygon.length; i++) {
            Vector2 point = polygon[i];
            x += point.x;
            y += point.y;
        }

        x /= polygon.length;
        y /= polygon.length;

        for (int i = 0; i < polygon.length; i++)
            polygon[i].sub(x, y);
    }

    public int spawnTriangle(float x, float y, Vector2[] points, Color color) {
        int triangle = world.create();
        EntityEdit edit = world.edit(triangle);
        edit.create(Transform.class);

        RenderTriangle renderTriangle = edit.create(RenderTriangle.class);
        renderTriangle.color.set(color);

        for (int i = 0; i < renderTriangle.points.length; i++)
            renderTriangle.points[i].set(points[i]);

        for (int i = 0; i < points.length; i++)
            points[i].scl(collisionSystem.getMetersPerPixel());

        Body body = collisionSystem.createBody(triangle, bodyDef);
        polygonShape.set(points);
        fixtureDef.shape = polygonShape;
        body.createFixture(fixtureDef);

        worldTransformationManager.setWorldPosition(triangle, x, y);

        return triangle;
    }

    private VertexArray generateNGon(int sides, float radius) {
        sides = MathUtils.clamp(sides, 3, 8);

        VertexArray vertices = new VertexArray(sides);
        float degreesPerSide = 360f / sides;

        for (int i = 0; i < vertices.size(); i++) {
            float degrees = i * degreesPerSide;
            float x = MathUtils.cosDeg(degrees) * radius;
            float y = MathUtils.sinDeg(degrees) * radius;
            vertices.set(i, x, y);
        }

        return vertices;
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

        Body body = collisionSystem.createBody(polygon, bodyDef);
        createPolygonFixtures(body, vertices, triangulation, fixtureDef);

        worldTransformationManager.setWorldPosition(polygon, x, y);

        return polygon;
    }

    public int spawnNGon(float x, float y, int sides, float radius, Color color) {
        return spawnPolygon(x, y, generateNGon(sides, radius), color);
    }

    private final float[] tmpTriangle = new float[6];

    private void createPolygonFixtures(Body body, VertexArray vertices, ShortArray triangulation, FixtureDef fixtureDef) {
        for (int i = 0; i < triangulation.size; i += 3) {
            for (int j = 0; j < 3; j++) {
                int polygonVertex = triangulation.get(i+j);
                float scaledX = collisionSystem.getMetersPerPixel() * vertices.getX(polygonVertex);
                float scaledY = collisionSystem.getMetersPerPixel() * vertices.getY(polygonVertex);

                int triangleVertex = j*2;
                tmpTriangle[triangleVertex] = scaledX;
                tmpTriangle[triangleVertex+1] = scaledY;
            }

            GeometryUtils.ensureCCW(tmpTriangle);

            if (!CollisionSystem.isTriangleValid(tmpTriangle))
                continue;

            polygonShape.set(tmpTriangle);
            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
        }
    }

    public Color getRandomColor() {
        tmpColor.set(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
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

    private final Vector2 worldPointer = new Vector2();

    private IntMap<ShapeDrawing> activeDrawings = new IntMap<>();

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        cameraSystem.screenToWorld(screenX, screenY, worldPointer);

        if (activeType == ShapeType.Custom) {
            if (drawingSystem != null && !activeDrawings.containsKey(pointer)) {
                ShapeDrawing shapeDrawing = drawingSystem.createDrawing(getRandomColor(),
                        minimumDrawingPointDistance * cameraSystem.getZoom());
                shapeDrawing.addPoint(worldPointer.x, worldPointer.y);
                activeDrawings.put(pointer, shapeDrawing);
            }
        }
        else {
            int spawnedId = spawnShape(activeType, worldPointer.x, worldPointer.y);
            if (dragSystem != null && spawnedId != -1) {
                Body body = collisionSystem.getAttachedBody(spawnedId);
                worldPointer.scl(collisionSystem.getMetersPerPixel());
                dragSystem.startDrag(body, pointer, worldPointer);
            }
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        ShapeDrawing shapeDrawing = activeDrawings.get(pointer);

        if (shapeDrawing != null) {
            activeDrawings.remove(pointer);

            shapeDrawing.optimize(baseMinIntermediatePointDistance * cameraSystem.getZoom());
            if (shapeDrawing.isValid()) {
                VertexArray vertices = new VertexArray(shapeDrawing.getPointCount());
                for (int i = 0; i < vertices.size(); i++)
                    vertices.set(i, shapeDrawing.getPointX(i), shapeDrawing.getPointY(i));
                GeometryUtils.ensureCCW(vertices.getBackingArray());
                spawnPolygon(0, 0, vertices, shapeDrawing.getColor());
            }

            drawingSystem.destroyDrawing(shapeDrawing);

            return true;
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        ShapeDrawing shapeDrawing = activeDrawings.get(pointer);

        if (shapeDrawing != null) {
            cameraSystem.screenToWorld(screenX, screenY, worldPointer);

            if (shapeDrawing.isValidForNextPoint(worldPointer.x, worldPointer.y))
                shapeDrawing.addPoint(worldPointer.x, worldPointer.y);

            return true;
        }

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

    public enum ShapeType {
        Random,
        Cube,
        Circle,
        Triangle,
        NGon,
        Custom,
        Explosion;

        public static ShapeType getRandom() {
            return values()[MathUtils.random(Cube.ordinal(), NGon.ordinal())];
        }
    }
}
