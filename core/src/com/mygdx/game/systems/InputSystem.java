package com.mygdx.game.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Casper on 18-09-2016.
 *
 * At the moment just copied straight from InputMultiplexer.
 */
public class InputSystem extends BaseSystem implements InputProcessor {
    private Array<InputProcessor> processors = new Array(4);

    @Override
    protected void processSystem() {

    }

    public void addProcessor (int index, InputProcessor processor) {
        if (processor == null) throw new NullPointerException("processor cannot be null");
        processors.insert(index, processor);
    }

    public void removeProcessor (int index) {
        processors.removeIndex(index);
    }

    public void addProcessor (InputProcessor processor) {
        if (processor == null) throw new NullPointerException("processor cannot be null");
        processors.add(processor);
    }

    public void removeProcessor (InputProcessor processor) {
        processors.removeValue(processor, true);
    }

    /** @return the number of processors in this multiplexer */
    public int size () {
        return processors.size;
    }

    public void clear () {
        processors.clear();
    }

    public void setProcessors (Array<InputProcessor> processors) {
        this.processors = processors;
    }

    public Array<InputProcessor> getProcessors () {
        return processors;
    }

    public boolean keyDown (int keycode) {
        for (int i = 0, n = processors.size; i < n; i++)
            if (processors.get(i).keyDown(keycode)) return true;
        return false;
    }

    public boolean keyUp (int keycode) {
        for (int i = 0, n = processors.size; i < n; i++)
            if (processors.get(i).keyUp(keycode)) return true;
        return false;
    }

    public boolean keyTyped (char character) {
        for (int i = 0, n = processors.size; i < n; i++)
            if (processors.get(i).keyTyped(character)) return true;
        return false;
    }

    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        for (int i = 0, n = processors.size; i < n; i++)
            if (processors.get(i).touchDown(screenX, screenY, pointer, button)) return true;
        return false;
    }

    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        for (int i = 0, n = processors.size; i < n; i++)
            if (processors.get(i).touchUp(screenX, screenY, pointer, button)) return true;
        return false;
    }

    public boolean touchDragged (int screenX, int screenY, int pointer) {
        for (int i = 0, n = processors.size; i < n; i++)
            if (processors.get(i).touchDragged(screenX, screenY, pointer)) return true;
        return false;
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        for (int i = 0, n = processors.size; i < n; i++)
            if (processors.get(i).mouseMoved(screenX, screenY)) return true;
        return false;
    }

    @Override
    public boolean scrolled (int amount) {
        for (int i = 0, n = processors.size; i < n; i++)
            if (processors.get(i).scrolled(amount)) return true;
        return false;
    }
}
