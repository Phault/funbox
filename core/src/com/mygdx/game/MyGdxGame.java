package com.mygdx.game;

import com.artemis.EntityEdit;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.mygdx.game.box2d.systems.Box2DDebugRenderSystem;
import com.mygdx.game.box2d.systems.CollisionSystem;
import com.mygdx.game.hierarchy.systems.HierarchyManager;
import com.mygdx.game.scenegraph.components.Transform;
import com.mygdx.game.scenegraph.systems.WorldTransformationManager;
import com.mygdx.game.shaperendering.systems.ShapeRenderSystem;
import com.mygdx.game.systems.*;

public class MyGdxGame extends ApplicationAdapter {

    private World world;

    private CollisionSystem collisionSystem = new CollisionSystem();
    private WorldTransformationManager worldTransformationManager = new WorldTransformationManager();
    private InputSystem inputSystem = new InputSystem();
    private HotkeySystem hotkeySystem = new HotkeySystem();
    private Box2DDebugRenderSystem box2DDebugRenderSystem;

    @Override
	public void create () {

        box2DDebugRenderSystem = new Box2DDebugRenderSystem();

        WorldConfigurationBuilder builder = new WorldConfigurationBuilder()
                .with(new EntityLinkManager())
                .with(inputSystem)
                .with(hotkeySystem)
                .with(new HierarchyManager())
                .with(worldTransformationManager)
                .with(collisionSystem)
                .with(new ShapeDragSystem())
                .with(new ShapeSpawnSystem())
                .with(new TestMovementSystem())
                .with(new CameraSystem())
                .with(new ZoomSystem())
                .with(new ShapeRenderSystem())
                .with(new RenderSystem())
                .with(box2DDebugRenderSystem);

        Gdx.input.setInputProcessor(inputSystem);

        hotkeySystem.addListener(Input.Keys.D, HotkeySystem.Modifiers.CTRL, new HotkeySystem.HotkeyListener() {
            @Override
            public boolean execute() {
                box2DDebugRenderSystem.setEnabled(!box2DDebugRenderSystem.isEnabled());
                return true;
            }
        });

        WorldConfiguration config = builder.build();

        world = new World(config);
        box2DDebugRenderSystem.setEnabled(false);

        int ground = world.create();
        EntityEdit edit = world.edit(ground);
        edit.create(Transform.class);
        EdgeShape groundShape = new EdgeShape();
        groundShape.set(-100, 0, 100, 0);
        collisionSystem.createFixture(ground, groundShape, 1);
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
        world.dispose();
	}
}