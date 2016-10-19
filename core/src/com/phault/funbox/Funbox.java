package com.phault.funbox;

import com.artemis.*;
import com.artemis.link.EntityLinkManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Antialiasing;
import com.bitfire.postprocessing.effects.Fxaa;
import com.bitfire.utils.ShaderLoader;
import com.kotcrab.vis.ui.VisUI;
import com.phault.artemis.essentials.box2d.systems.Box2DDebugRenderSystem;
import com.phault.artemis.essentials.box2d.systems.CollisionSystem;
import com.phault.artemis.essentials.hierarchy.systems.HierarchyManager;
import com.phault.artemis.essentials.scenegraph.components.Transform;
import com.phault.artemis.essentials.scenegraph.systems.WorldTransformationManager;
import com.phault.artemis.essentials.shaperendering.components.RenderRectangle;
import com.phault.artemis.essentials.shaperendering.systems.ShapeRenderSystem;
import com.phault.artemis.essentials.systems.CameraSystem;
import com.phault.artemis.essentials.systems.HotkeySystem;
import com.phault.artemis.essentials.systems.InputSystem;
import com.phault.artemis.essentials.systems.RenderSystem;
import com.phault.funbox.systems.ShapeDragSystem;
import com.phault.funbox.systems.ShapeDrawingSystem;
import com.phault.funbox.systems.ShapeSpawnSystem;
import com.phault.funbox.systems.ZoomSystem;
import com.phault.funbox.systems.shapes.*;

public class Funbox extends ApplicationAdapter {

    private World world;

    private CollisionSystem collisionSystem = new CollisionSystem();
    private WorldTransformationManager worldTransformationManager = new WorldTransformationManager();
    private InputSystem inputSystem = new InputSystem();
    private HotkeySystem hotkeySystem = new HotkeySystem();
    private Box2DDebugRenderSystem box2DDebugRenderSystem;
    private ShapeSpawnSystem shapeSpawnSystem = new ShapeSpawnSystem();

    private PostProcessor postProcessor;

    private UIStage uiStage;

    private final Color clearColor = new Color(0.07f, 0.07f, 0.07f, 1);

    @Override
	public void create () {

//        VisUI.load(VisUI.SkinScale.X2);
        uiStage = new UIStage(this);
        inputSystem.addProcessor(uiStage);

        box2DDebugRenderSystem = new Box2DDebugRenderSystem();


        SimpleShapeSpawner[] primitiveSpawners = new SimpleShapeSpawner[] {
                new SquareSpawner(),
                new CircleSpawner(),
                new TriangleSpawner(),
                new NGonSpawner()
        };
        RandomSpawner randomSpawner = new RandomSpawner();
        randomSpawner.addSpawners(primitiveSpawners);

        WorldConfigurationBuilder builder = new WorldConfigurationBuilder()
                .with(new EntityLinkManager())
                .with(inputSystem)
                .with(hotkeySystem)
                .with(new HierarchyManager())
                .with(worldTransformationManager)
                .with(collisionSystem)
                .with(new ShapeDragSystem())
                .with(shapeSpawnSystem)

                .with(randomSpawner)
                .with((BaseSystem[]) primitiveSpawners)
                .with(new CustomSpawner())
                .with(new ExplosionSpawner())

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

        createGround();

        boolean isDesktop = Gdx.app.getType() == Application.ApplicationType.Desktop;
        ShaderLoader.BasePath = "shaders/";
        postProcessor = new PostProcessor(false, false, isDesktop);
        postProcessor.setClearColor(clearColor);

        Antialiasing antialiasing = new Fxaa(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        postProcessor.addEffect(antialiasing);

        hotkeySystem.addListener(Input.Keys.A, HotkeySystem.Modifiers.NONE, new HotkeySystem.HotkeyListener() {
            @Override
            public boolean execute() {
                postProcessor.setEnabled(!postProcessor.isEnabled());
                return true;
            }
        });

        uiStage.initialize();
    }

    private int createGround() {
        int ground = world.create();
        EntityEdit edit = world.edit(ground);
        edit.create(Transform.class);

        float width = 15000;
        float height = 400;

        RenderRectangle groundVisual = edit.create(RenderRectangle.class);
        groundVisual.width = width;
        groundVisual.height = height;
        groundVisual.origin.set(0.5f, 1);
        groundVisual.color.set(Color.DARK_GRAY);

        float halfWidth = 0.5f * width * collisionSystem.getMetersPerPixel();

        EdgeShape groundShape = new EdgeShape();
        float skinRadius = groundShape.getRadius();
        groundShape.set(-halfWidth, -skinRadius, halfWidth, -skinRadius);
        collisionSystem.createFixture(ground, groundShape, 1);

        return ground;
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
        Gdx.gl20.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
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

    public CollisionSystem getCollisionSystem() {
        return collisionSystem;
    }

    public World getWorld() {
        return world;
    }
}