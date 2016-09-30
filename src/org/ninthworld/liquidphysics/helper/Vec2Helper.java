package org.ninthworld.liquidphysics.helper;

import org.jbox2d.common.Vec2;

/**
 * Created by NinthWorld on 9/30/2016.
 */
public class Vec2Helper {

    public static Vec2 scalar(float scalar, Vec2 v1){
        return new Vec2(scalar * v1.x, scalar * v1.y);
    }

    public static Vec2 add(Vec2 v1, Vec2 v2){
        return new Vec2(v1.x + v2.x, v1.y + v2.y);
    }

    public static Vec2 sub(Vec2 v1, Vec2 v2){
        return new Vec2(v1.x - v2.x, v1.y - v2.y);
    }
}
