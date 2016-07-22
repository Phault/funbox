package com.mygdx.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.components.Sprite;
import com.mygdx.game.components.Transform;

/**
 * Created by Casper on 19-07-2016.
 */
public class RenderSystem extends IteratingSystem
{
    private WorldTransformationManager transformManager;
    private ComponentMapper<Sprite> mSprite;
    private ComponentMapper<Transform> mTransform;

    private SpriteBatch spriteBatch;

    public RenderSystem() {
        super(Aspect.all(Sprite.class, Transform.class));

        spriteBatch = new SpriteBatch();
    }

    @Override
    protected void initialize() {
        super.initialize();

        transformManager = world.getSystem(WorldTransformationManager.class);
    }

    @Override
    protected void begin() {
        spriteBatch.begin();
    }

    @Override
    protected void process(int entityId) {
        Sprite sprite = mSprite.get(entityId);
//        Transform transform = mTransform.get(entityId);

        Vector2 pos = transformManager.getWorldPosition(entityId);
        Vector2 scale = transformManager.getWorldScale(entityId);
        float rotation = transformManager.getWorldRotation(entityId);

        spriteBatch.draw(sprite.Texture,
                pos.x,
                pos.y,
                0,
                0,
                sprite.Texture.getRegionWidth(),
                sprite.Texture.getRegionHeight(),
                scale.x,
                scale.y,
                rotation);
    }

    @Override
    protected void end() {
        spriteBatch.end();
    }

    @Override
    protected void dispose() {
        super.dispose();
        spriteBatch.dispose();
    }
}
