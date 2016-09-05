package com.mygdx.game;

import com.artemis.*;
import com.artemis.World;
import com.artemis.link.EntityLinkManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.box2d.systems.Box2DDebugRenderSystem;
import com.mygdx.game.box2d.systems.CollisionSystem;
import com.mygdx.game.components.*;
import com.mygdx.game.scenegraph.components.Transform;
import com.mygdx.game.hierarchy.systems.HierarchyManager;
import com.mygdx.game.scenegraph.systems.WorldTransformationManager;
import com.mygdx.game.systems.*;

public class MyGdxGame extends ApplicationAdapter {
    private Texture img;
    private World world;

    @Override
	public void create () {
        img = new Texture("badlogic.jpg");

        CollisionSystem collisionSystem = new CollisionSystem();

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(new EntityLinkManager())
                .with(new HierarchyManager())
                .with(new WorldTransformationManager())
                .with(collisionSystem)
                .with(new TestMovementSystem())
                .with(new CameraSystem())
                .with(new RenderSystem())
                .with(new Box2DDebugRenderSystem())
                .build();

        world = new World(config);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(0, 10);

        int box = world.create();
        EntityEdit edit = world.edit(box);
        edit.create(Transform.class);
        edit.create(TestMovement.class);
        Body boxBody = collisionSystem.createBody(box, def);
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(1, 1);
        boxBody.createFixture(boxShape, 1);
        Sprite sprite = edit.create(Sprite.class);
        sprite.texture = new TextureRegion(img);

        int ground = world.create();
        edit = world.edit(ground);
        edit.create(Transform.class);
        EdgeShape groundShape = new EdgeShape();
        groundShape.set(-100, 0, 100, 0);
        collisionSystem.createFixture(ground, groundShape, 1);

//        int parent = world.create();
//        world.edit(parent).create(TestMovement.class);
//        world.edit(parent).create(Velocity.class);
//        world.edit(parent).create(Sprite.class).texture = new TextureRegion(img);
//        Transform parentTransform = world.edit(parent).create(Transform.class);
//        parentTransform.position.set(200, 0);
//        parentTransform.rotation = 45;
//        parentTransform.scale.set(0.5f, 1);
//
//        int child = world.create();
//        world.edit(child).create(Sprite.class).texture = new TextureRegion(img);
//        Transform childTransform = world.edit(child).create(Transform.class);
//        childTransform.position.set(150, 50);
//        childTransform.rotation = -45;
//        childTransform.scale.set(2, 1);
//        world.edit(child).create(Parented.class).target = parent;
//        world.edit(child).create(Velocity.class).y = 10;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        world.getSystem(CameraSystem.class).resize(width, height);
    }

    @Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.setDelta(Gdx.graphics.getDeltaTime());
        world.process();
	}
	
	@Override
	public void dispose () {
		img.dispose();
        world.dispose();
	}
}