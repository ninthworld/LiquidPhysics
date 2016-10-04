package org.ninthworld.liquidphysics.entities;

/**
 * Created by NinthWorld on 10/4/2016.
 */
public class SteamEntity extends LiquidEntity {

    public int timer;

    public SteamEntity(){
        super(1f);
        this.timer = 0;
    }
}
