package com.phault.funbox.systems.shapes;

import com.artemis.annotations.Wire;
import com.artemis.utils.Bag;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Casper on 12-10-2016.
 */
@Wire(injectInherited = true)
public class RandomSpawner extends SimpleShapeSpawner {

    private final Bag<SimpleShapeSpawner> spawners = new Bag<>();

    @Override
    public String iconPath() {
        return "icon_random";
    }

    @Override
    public int spawn(float x, float y) {

        if (spawners.size() > 0) {
            int index = MathUtils.random(0, spawners.size() - 1);
            return spawners.get(index).spawn(x, y);
        }

        return -1;
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
