package com.phault.funbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.phault.funbox.systems.HotkeySystem;
import com.phault.funbox.systems.shapes.ShapeSpawner;

/**
 * Created by Casper on 21-09-2016.
 */
public class UIStage extends Stage {
    private Table table;
    private Funbox context;

    private final ObjectMap<String, Drawable> shapeIcons = new ObjectMap<>();

    private Drawable pauseIcon, playIcon;

    private Skin skin;

    public UIStage(final Funbox context) {
        super();

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

        ScreenViewport viewport = new ScreenViewport();
        switch (Gdx.app.getType()) {
            case Android:
            case iOS:
                viewport.setUnitsPerPixel(1f/Gdx.graphics.getDensity());
                break;
        }
        setViewport(viewport);

        this.context = context;
    }

    public void initialize() {
        HotkeySystem hotkeySystem = context.getWorld().getSystem(HotkeySystem.class);
        loadTextures();

        table = new Table();
        table.setFillParent(true);
        addActor(table);

        final ButtonGroup<ImageButton> buttonGroup = new ButtonGroup<>();

        VerticalGroup group = new VerticalGroup();
        group.fill();
        group.space(10);
        for (final ShapeSpawner spawner : context.getShapeSpawnSystem().getSpawners()) {
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(skin.get(ImageButton.ImageButtonStyle.class));
            style.imageUp = shapeIcons.get(spawner.iconPath());
            ImageButton shapeButton = new ImageButton(style);
            shapeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    context.getShapeSpawnSystem().setCurrentSpawner(spawner);
                }
            });
            buttonGroup.add(shapeButton);
            group.addActor(shapeButton);
        }

        for (int i = 0; i < 9; i++) {
            int key = Input.Keys.NUM_1 + i;
            final int index = i;
            hotkeySystem.addListener(key, HotkeySystem.Modifiers.NONE, new HotkeySystem.HotkeyListener() {
                @Override
                public boolean execute() {
                    if (buttonGroup.getButtons().size > index)
                        buttonGroup.getButtons().get(index).setChecked(true);
                    return true;
                }
            });
        }

        table.pad(10);
        table.add(group).expand().top().left();

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(skin.get(ImageButton.ImageButtonStyle.class));
        style.imageUp = pauseIcon;
        style.checked = null;
        final ImageButton pauseButton = new ImageButton(style);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                context.getCollisionSystem().setPaused(!context.getCollisionSystem().isPaused());
                pauseButton.getStyle().imageUp = (context.getCollisionSystem().isPaused() ? playIcon : pauseIcon);
            }
        });

        table.add(pauseButton).expand().top().right();
    }

    private void loadTextures() {
        for (ShapeSpawner spawner : context.getShapeSpawnSystem().getSpawners()) {
            String textureName = spawner.iconPath();
            Drawable drawable = skin.getDrawable(textureName);
            shapeIcons.put(spawner.iconPath(), drawable);
        }

        pauseIcon = skin.getDrawable("icon_pause");
        playIcon = skin.getDrawable("icon_play");
    }
}
