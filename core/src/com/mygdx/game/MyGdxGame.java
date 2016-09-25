package com.mygdx.game;

import com.artemis.EntityEdit;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Antialiasing;
import com.bitfire.postprocessing.effects.Fxaa;
import com.bitfire.utils.ShaderLoader;
import com.kotcrab.vis.ui.VisUI;
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
    private ShapeSpawnSystem shapeSpawnSystem = new ShapeSpawnSystem();

    private PostProcessor postProcessor;

    private UIStage uiStage;

    @Override
	public void create () {

        VisUI.load(VisUI.SkinScale.X2);
        uiStage = new UIStage(this);
        inputSystem.addProcessor(uiStage);

        box2DDebugRenderSystem = new Box2DDebugRenderSystem();

        WorldConfigurationBuilder builder = new WorldConfigurationBuilder()
                .with(new EntityLinkManager())
                .with(inputSystem)
                .with(hotkeySystem)
                .with(new HierarchyManager())
                .with(worldTransformationManager)
                .with(collisionSystem)
                .with(new ShapeDragSystem())
                .with(shapeSpawnSystem)
                .with(new TestMovementSystem())
                .with(new CameraSystem())
                .with(new ZoomSystem())
                .with(new ShapeRenderSystem())
                .with(new ShapeDrawingSystem())
                .with(new RenderSystem())
                .with(box2DDebugRenderSystem);

        Gdx.input.setInputProcessor(inputSystem);

        hotkeySystem.addListener(Input.Keys.D, HotkeySystem.Modifiers.NONE, new HotkeySystem.HotkeyListener() {
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

        boolean isDesktop = Gdx.app.getType() == Application.ApplicationType.Desktop;
        ShaderLoader.BasePath = "shaders/";
        postProcessor = new PostProcessor(false, false, isDesktop);

        Antialiasing antialiasing = new Fxaa(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        postProcessor.addEffect(antialiasing);

        hotkeySystem.addListener(Input.Keys.A, HotkeySystem.Modifiers.NONE, new HotkeySystem.HotkeyListener() {
            @Override
            public boolean execute() {
                postProcessor.setEnabled(!postProcessor.isEnabled());
                return true;
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        world.getSystem(CameraSystem.class).resize(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void resume() {
        super.resume();
        postProcessor.rebind();
    }

    @Override
	public void render () {
        Gdx.gl20.glClearColor(0,0,0,1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        postProcessor.capture();

        world.setDelta(Gdx.graphics.getDeltaTime());
        world.process();

        postProcessor.render();

        uiStage.act(Gdx.graphics.getDeltaTime());
        uiStage.draw();
	}
	
	@Override
	public void dispose () {
        world.dispose();
        postProcessor.dispose();
        uiStage.dispose();
        VisUI.dispose();
	}

    public ShapeSpawnSystem getShapeSpawnSystem() {
        return shapeSpawnSystem;
    }
}