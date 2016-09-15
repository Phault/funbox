package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.artemis.EntityEdit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.mygdx.game.box2d.systems.CollisionSystem;
import com.mygdx.game.scenegraph.components.Transform;
import com.mygdx.game.scenegraph.systems.WorldTransformationManager;
import com.mygdx.game.shaperendering.components.*;
import com.mygdx.game.shaperendering.utils.VertexArray;

import java.util.Random;

/**
 * Created by Casper on 06-09-2016.
 */
public class ShapeSpawnSystem extends BaseSystem {

    private CollisionSystem collisionSystem;
    private WorldTransformationManager worldTransformationManager;
    private CameraSystem cameraSystem;

    private Vector2 minSize = new Vector2(25, 25), maxSize = new Vector2(200, 200);

    private float minTriangleAngle = 30, maxTriangleAngle = 150;
    private float minTriangleLength = 25, maxTriangleLength = 200;

    private float minRadius = 10, maxRadius = 100;

    private Random random = new Random();
    private BodyDef bodyDef;

    @Override
    protected void initialize() {
        super.initialize();

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        groundBody = collisionSystem.getPhysicsWorld().createBody(new BodyDef());
    }

    private final Vector3 worldPointer = new Vector3();
    private final Vector2 physicsWorldPointer = new Vector2();
    private final Color tmpColor = new Color();
    private Body hitBody;
    private MouseJoint joint;

    private Body groundBody;

    private boolean isDragging() {
        return joint != null;
    }

    private QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if (fixture.testPoint(physicsWorldPointer.x, physicsWorldPointer.y)) {
                hitBody = fixture.getBody();
                return false;
            }

            return true;
        }
    };

    @Override
    protected void processSystem() {
        worldPointer.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        cameraSystem.getCamera().unproject(worldPointer);
        physicsWorldPointer.set(worldPointer.x, worldPointer.y).scl(collisionSystem.getMetersPerPixel());

        boolean touching = Gdx.input.isTouched();

        if (touching) {
            if (isDragging()) {
                joint.setTarget(physicsWorldPointer);
            }
            else {
                hitBody = null;
                collisionSystem.getPhysicsWorld().QueryAABB(callback,
                        physicsWorldPointer.x - 0.01f,
                        physicsWorldPointer.y - 0.01f,
                        physicsWorldPointer.x + 0.01f,
                        physicsWorldPointer.y + 0.01f);

                if (hitBody != null)
                    startDrag(hitBody);
                else if (Gdx.input.justTouched()) {
                    int cubeId = spawnRandomShape(worldPointer.x, worldPointer.y);
                    startDrag(collisionSystem.getAttachedBody(cubeId));
                }
            }
        }
        else {
            if (isDragging())
                endDrag();
        }
    }

    private void startDrag(Body body) {
        MouseJointDef def = new MouseJointDef();
        def.bodyA = groundBody;
        def.bodyB = body;
        def.collideConnected = false;
        def.target.set(physicsWorldPointer.x, physicsWorldPointer.y);
        def.maxForce = 500.0f * body.getMass();

        joint = (MouseJoint)collisionSystem.getPhysicsWorld().createJoint(def);
        body.setAwake(true);
    }

    private void endDrag() {
        collisionSystem.getPhysicsWorld().destroyJoint(joint);
        joint = null;
    }

    private int spawnRandomShape(float x, float y) {
        int shapeIndex = random.nextInt(4);

        switch (shapeIndex) {
            case 0:
                return spawnRandomCube(x, y);
            case 1:
                return spawnRandomCircle(x, y);
            case 2:
                return spawnRandomTriangle(x, y);
            case 3:
                return spawnRandomNGon(x, y);
        }

        return -1;
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
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(width * 0.5f * collisionSystem.getMetersPerPixel(),
                height * 0.5f * collisionSystem.getMetersPerPixel());
        boxBody.createFixture(boxShape, 2);

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
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius * collisionSystem.getMetersPerPixel());
        body.createFixture(circleShape, 2);

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

        for (int i = 0; i < polygon.length; i++) {
            polygon[i].sub(x, y);
        }
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
        PolygonShape shape = new PolygonShape();
        shape.set(points);
        body.createFixture(shape, 2);

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

    private float[] tmpNGon = new float[8 * 2];

    public int spawnNGon(float x, float y, int sides, float radius, Color color) {
        int nGon = world.create();
        EntityEdit edit = world.edit(nGon);
        edit.create(Transform.class);

        RenderPolygon renderPolygon = edit.create(RenderPolygon.class);
        renderPolygon.color.set(color);
        renderPolygon.vertices = generateNGon(sides, radius);

        float[] backingArray = renderPolygon.vertices.getBackingArray();
        for (int i = 0; i < backingArray.length; i++)
            tmpNGon[i] = backingArray[i] * collisionSystem.getMetersPerPixel();

        Body body = collisionSystem.createBody(nGon, bodyDef);
        PolygonShape shape = new PolygonShape();

        shape.set(tmpNGon, 0, backingArray.length);
        body.createFixture(shape, 2);

        worldTransformationManager.setWorldPosition(nGon, x, y);

        return nGon;
    }

    public Color getRandomColor() {
        tmpColor.set(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
        return tmpColor;
    }
}
