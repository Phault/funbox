package com.phault.funbox.systems.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.phault.artemis.essentials.shaperendering.systems.ShapeRenderSystem;

/**
 * Created by Casper on 12-10-2016.
 */
public abstract class SimpleShapeSpawner extends ShapeSpawner {

    protected ShapeRenderSystem shapeRenderSystem;

    private final IntMap<ShapeSketch> sketches = new IntMap<>();

    private static final Pool<ShapeSketch> sketchPool = new Pool<ShapeSketch>() {
        @Override
        protected ShapeSketch newObject() {
            return new ShapeSketch();
        }
    };

    @Override
    protected void processSystem() {
        shapeRenderSystem.setShapeType(ShapeRenderer.ShapeType.Filled);
        for (IntMap.Entry<ShapeSketch> entry: sketches.entries()) {
            shapeRenderSystem.setColor(entry.value.color);
            draw(entry.key, entry.value);
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (sketches.containsKey(pointer))
            return false;

        Vector2 worldPos = screenToWorld(screenX, screenY);

        ShapeSketch sketch = sketchPool.obtain();
        sketch.color.set(getRandomColor());
        sketch.left = worldPos.x;
        sketch.top = worldPos.y;
        sketches.put(pointer, sketch);

        return true;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        ShapeSketch sketch = sketches.get(pointer);

        if (sketch != null) {
            Vector2 worldPos = screenToWorld(screenX, screenY);
            sketch.right = worldPos.x;
            sketch.bottom = worldPos.y;

            return true;
        }

        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        ShapeSketch sketch = sketches.get(pointer);

        if (sketch != null) {
            Vector2 worldPos = screenToWorld(screenX, screenY);
            sketch.right = worldPos.x;
            sketch.bottom = worldPos.y;

            if (!isSketchValid(sketch))
                spawn(worldPos.x, worldPos.y, sketch.color);
            else
                spawn(sketch);

            sketches.remove(pointer);
            sketchPool.free(sketch);

            return true;
        }

        return super.touchUp(screenX, screenY, pointer, button);
    }

    protected boolean isSketchValid(ShapeSketch sketch) {
        return sketch.isValid()
                && Math.abs(sketch.right - sketch.left) > 5
                && Math.abs(sketch.bottom - sketch.top) > 5;
    }

    protected abstract void draw(int pointer, ShapeSketch sketch);
    public abstract int spawn(float x, float y, Color color);
    public abstract int spawn(ShapeSketch sketch);
}