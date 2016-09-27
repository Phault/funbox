package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.systems.ShapeSpawnSystem;

/**
 * Created by Casper on 21-09-2016.
 */
public class UIStage extends Stage {
    private Table table;
    private MyGdxGame context;

    private final ObjectMap<ShapeSpawnSystem.ShapeType, Drawable> shapeIcons = new ObjectMap<>();

    private Drawable pauseIcon, playIcon;

    private Skin skin;

    public UIStage(final MyGdxGame context) {
        super();

        skin = new Skin(new FileHandle("ui/uiskin.json"));

        ScreenViewport viewport = new ScreenViewport();
        switch (Gdx.app.getType()) {
            case Android:
            case iOS:
                viewport.setUnitsPerPixel(1f/Gdx.graphics.getDensity());
                break;
        }
        setViewport(viewport);

        this.context = context;

        loadTextures();

        table = new Table();
        table.setFillParent(true);
        addActor(table);

        VerticalGroup group = new VerticalGroup();
        group.fill();
        group.space(10);
        for (final ShapeSpawnSystem.ShapeType type : ShapeSpawnSystem.ShapeType.values()) {
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(skin.get(ImageButton.ImageButtonStyle.class));
            style.imageUp = shapeIcons.get(type);
            ImageButton shapeButton = new ImageButton(style);
            shapeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    context.getShapeSpawnSystem().setActiveType(type);
                }
            });
            group.addActor(shapeButton);
        }
        table.pad(10);
        table.add(group).expand().top().left();

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(skin.get(ImageButton.ImageButtonStyle.class));
        style.imageUp = pauseIcon;
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
        for (ShapeSpawnSystem.ShapeType type : ShapeSpawnSystem.ShapeType.values()) {
            String textureName = String.format("icon_%s", type.toString().toLowerCase());
            Drawable drawable = skin.getDrawable(textureName);
            shapeIcons.put(type, drawable);
        }

        pauseIcon = skin.getDrawable("icon_pause");
        playIcon = skin.getDrawable("icon_play");
    }
}
