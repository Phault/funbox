package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.artemis.EntityEdit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.game.box2d.systems.CollisionSystem;
import com.mygdx.game.components.Sprite;
import com.mygdx.game.components.TestMovement;
import com.mygdx.game.scenegraph.components.Transform;
import com.mygdx.game.scenegraph.systems.WorldTransformationManager;

import java.util.Random;

/**
 * Created by Casper on 06-09-2016.
 */
public class CubeSpawnSystem extends BaseSystem {

    private CollisionSystem collisionSystem;
    private WorldTransformationManager worldTransformationManager;
    private CameraSystem cameraSystem;

    private Vector2 minSize = new Vector2(25, 25), maxSize = new Vector2(200, 200);

    private Random random = new Random();
    private BodyDef bodyDef;

    private Texture img;

    @Override
    protected void initialize() {
        super.initialize();

        img = new Texture("square.png");

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
    }

    private final Vector3 tmpWorldPoint = new Vector3();
    private final Color tmpColor = new Color();

    @Override
    protected void processSystem() {
        if (Gdx.input.justTouched()) {
            float width = MathUtils.lerp(minSize.x, maxSize.x, random.nextFloat());
            float height = MathUtils.lerp(minSize.y, maxSize.y, random.nextFloat());

            tmpWorldPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            cameraSystem.getCamera().unproject(tmpWorldPoint);

            tmpColor.set(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);

            spawnCube(tmpWorldPoint.x, tmpWorldPoint.y, width, height, tmpColor);
        }
    }

    public int spawnCube(float x, float y, float width, float height, Color color) {
        int cube = world.create();
        EntityEdit edit = world.edit(cube);
        edit.create(Transform.class);
        edit.create(TestMovement.class);

        Body boxBody = collisionSystem.createBody(cube, bodyDef);
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(width * 0.5f * collisionSystem.getMetersPerPixel(),
                height * 0.5f * collisionSystem.getMetersPerPixel());
        boxBody.createFixture(boxShape, 1);

        Sprite sprite = edit.create(Sprite.class);
        sprite.texture = new TextureRegion(img);
        sprite.tint.set(color);

        float scaleX = width / sprite.texture.getRegionWidth();
        float scaleY = height / sprite.texture.getRegionHeight();

        worldTransformationManager.setLocalScale(cube, scaleX, scaleY);
        worldTransformationManager.setWorldPosition(cube, x, y);

        return cube;
    }

    @Override
    protected void dispose() {
        super.dispose();

        img.dispose();
    }
}
