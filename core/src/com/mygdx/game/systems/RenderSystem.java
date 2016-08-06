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

    private CameraSystem cameraSystem;

    private SpriteBatch spriteBatch;

    public RenderSystem() {
        super(Aspect.all(Sprite.class, Transform.class));

        spriteBatch = new SpriteBatch();
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void begin() {

        spriteBatch.setProjectionMatrix(cameraSystem.getMatrix());
        spriteBatch.begin();
    }

    @Override
    protected void process(int entityId) {
        Sprite sprite = mSprite.get(entityId);

        Vector2 pos = transformManager.getWorldPosition(entityId);
        Vector2 scale = transformManager.getWorldScale(entityId);
        float rotation = transformManager.getWorldRotation(entityId);

        float width = sprite.texture.getRegionWidth();
        float height = sprite.texture.getRegionHeight();

        float originX = sprite.origin.x * width;
        float originY = sprite.origin.y * height;

        spriteBatch.draw(sprite.texture,
                pos.x - originX,
                pos.y - originY,
                originX,
                originY,
                width,
                height,
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
