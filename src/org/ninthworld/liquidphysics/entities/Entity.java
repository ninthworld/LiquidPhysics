package org.ninthworld.liquidphysics.entities;

import org.lwjgl.util.vector.Vector2f;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class Entity {

    protected Vector2f position;
    protected Vector2f scale;
    protected float rotation;

    public Entity(){
        this.position = new Vector2f();
        this.scale = new Vector2f(1f, 1f);
        this.rotation = 0.0f;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Vector2f getScale() {
        return scale;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }
}
