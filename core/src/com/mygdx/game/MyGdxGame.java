package com.mygdx.game;

import com.artemis.*;
import com.artemis.link.EntityLinkManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.components.Parented;
import com.mygdx.game.components.Sprite;
import com.mygdx.game.components.TestMovement;
import com.mygdx.game.components.Transform;
import com.mygdx.game.systems.RenderSystem;
import com.mygdx.game.systems.TestMovementSystem;
import com.mygdx.game.systems.WorldTransformationManager;

public class MyGdxGame extends ApplicationAdapter {
	Texture img;
    private World world;

    @Override
	public void create () {
        img = new Texture("badlogic.jpg");

        WorldTransformationManager transformManager = new WorldTransformationManager();

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(new EntityLinkManager())
                .with(transformManager)
                .with(new RenderSystem())
                .with(new TestMovementSystem())
                .build();

        world = new World(config);

        world.getSystem(EntityLinkManager.class).register(Parented.class, transformManager);

        int parent = world.create();
        world.edit(parent).create(TestMovement.class);
        world.edit(parent).create(Sprite.class).texture = new TextureRegion(img);
        Transform parentTransform = world.edit(parent).create(Transform.class);
        parentTransform.position.set(200, 0);
        parentTransform.rotation = 45;
        parentTransform.scale.set(0.5f, 1);

        int child = world.create();
        world.edit(child).create(Sprite.class).texture = new TextureRegion(img);
        Transform childTransform = world.edit(child).create(Transform.class);
        childTransform.position.set(50, 50);
        childTransform.rotation = -45;
        world.edit(child).create(Parented.class).target = parent;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
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