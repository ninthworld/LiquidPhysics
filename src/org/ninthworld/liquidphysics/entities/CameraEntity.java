package org.ninthworld.liquidphysics.entities;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class CameraEntity extends Entity {

    public CameraEntity(){
        super();
    }

    public void increasePosition(float x, float y){
        this.position.x += x;
        this.position.y += y;
    }
}
