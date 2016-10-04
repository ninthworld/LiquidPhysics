package org.ninthworld.liquidphysics.entities;

/**
 * Created by NinthWorld on 10/4/2016.
 */
public class WaterEntity extends LiquidEntity {

    public static float toSteamTemp = 1f;
    public static float toIceTemp = -0.5f;

    public WaterEntity(){
        super(0.5f);
    }
}
