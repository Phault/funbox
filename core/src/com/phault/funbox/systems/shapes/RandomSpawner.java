package com.phault.funbox.systems.shapes;

import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by Casper on 12-10-2016.
 */
@Wire(injectInherited = true)
public class RandomSpawner extends SimpleShapeSpawner {

    private final Bag<SimpleShapeSpawner> spawners = new Bag<>();
    private final IntMap<SimpleShapeSpawner> pendingSpawns = new IntMap<>();

    private SimpleShapeSpawner currentSpawner;

    @Override
    public String iconPath() {
        return "icon_random";
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!pendingSpawns.containsKey(pointer))
            pendingSpawns.put(pointer, getRandomSpawner());

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        currentSpawner = pendingSpawns.remove(pointer);

        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    protected void draw(int pointer, ShapeSketch sketch) {
        SimpleShapeSpawner spawner = pendingSpawns.get(pointer);

        if (spawner != null)
            spawner.draw(pointer, sketch);
    }

    @Override
    protected boolean isSketchValid(ShapeSketch sketch) {

        if (currentSpawner != null)
            return currentSpawner.isSketchValid(sketch);

        return super.isSketchValid(sketch);
    }

    @Override
    public int spawn(float x, float y) {

        if (currentSpawner != null)
            return currentSpawner.spawn(x, y);

        return -1;
    }

    @Override
    public int spawn(ShapeSketch sketch) {

        if (currentSpawner != null)
            return currentSpawner.spawn(sketch);

        return -1;
    }

    private SimpleShapeSpawner getRandomSpawner() {
        if (spawners.size() > 0) {
            int index = MathUtils.random(0, spawners.size() - 1);
            return spawners.get(index);
        }

        return null;
    }

    public void addSpawner(SimpleShapeSpawner spawner) {
        if (spawner != null && !spawners.contains(spawner))
            spawners.add(spawner);
    }

    public void addSpawners(SimpleShapeSpawner... spawners) {
        for (int i = 0; i < spawners.length; i++)
            addSpawner(spawners[i]);
    }

    public void removeSpawner(SimpleShapeSpawner spawner) {
        spawners.remove(spawner);
    }
}
