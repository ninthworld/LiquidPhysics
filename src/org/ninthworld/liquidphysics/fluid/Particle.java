package org.ninthworld.liquidphysics.fluid;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

/**
 * Created by NinthWorld on 9/30/2016.
 */
public class Particle
{
    public static final int MAX_FIXTURES_TO_TEST = 20;
    public Vec2 position;
    public Vec2 velocity;
    public boolean alive;
    public float[] distances;
    public int[] neighbors;
    public int neighborCount;
    public int ci;
    public int cj;
    public int index;
    public float p;
    public float pnear;
    public Fixture[] fixturesToTest;
    public int numFixturesToTest;
    public Vec2 oldPosition;
    public Vec2[] collisionVertices;
    public Vec2[] collisionNormals;

    public Particle(Vec2 position, Vec2 velocity, boolean alive)
    {
        this.position = position;
        this.velocity = velocity;
        this.alive = alive;

        distances = new float[FluidEngine.MAX_NEIGHBORS];
        neighbors = new int[FluidEngine.MAX_NEIGHBORS];
        fixturesToTest = new Fixture[MAX_FIXTURES_TO_TEST];
//        collisionVertices = new Vec2[Settings.MaxPolygonVertices];
//        collisionNormals = new Vec2[Settings.MaxPolygonVertices];
    }
}