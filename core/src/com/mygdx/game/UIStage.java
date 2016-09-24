package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mygdx.game.systems.ShapeSpawnSystem;

/**
 * Created by Casper on 21-09-2016.
 */
public class UIStage extends Stage {
    private Table table;
    private MyGdxGame context;

    public UIStage(final MyGdxGame context) {
        super();
        ScreenViewport viewport = new ScreenViewport();
        switch (Gdx.app.getType()) {
            case Android:
            case iOS:
                viewport.setUnitsPerPixel(1f/Gdx.graphics.getDensity());
                break;
        }
        setViewport(viewport);

        this.context = context;

        table = new Table();
        table.setFillParent(true);
        addActor(table);

        VerticalGroup group = new VerticalGroup();
        group.fill();
        group.space(10);
        for (final ShapeSpawnSystem.ShapeType type : ShapeSpawnSystem.ShapeType.values()) {
            VisTextButton shapeButton = new VisTextButton(type.name(), new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    context.getShapeSpawnSystem().setActiveType(type);
                }
            });
            group.addActor(shapeButton);
        }
        table.top().left().pad(10);
        table.add(group);
    }
}
