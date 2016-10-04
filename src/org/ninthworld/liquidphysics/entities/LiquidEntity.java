package org.ninthworld.liquidphysics.entities;

/**
 * Created by NinthWorld on 10/4/2016.
 */
public class LiquidEntity {
    public static final int WATER = 0;
    public static final int LAVA = 1;
    public static final int STEAM = 2;

    public float temperature;

    public LiquidEntity(float temperature){
        this.temperature = temperature;
    }
}
