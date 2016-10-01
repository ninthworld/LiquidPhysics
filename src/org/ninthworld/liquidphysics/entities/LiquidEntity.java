package org.ninthworld.liquidphysics.entities;

import org.ninthworld.liquidphysics.engine.Main;
import org.ninthworld.liquidphysics.model.Loader;
import org.ninthworld.liquidphysics.model.RawModel;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class LiquidEntity extends ModelEntity {

    public LiquidEntity(){
        super();
    }

    public static RawModel createLiquidModel(Loader loader){
        float radius = Main.PARTICLE_RADIUS;
        int verts = 8;

        float[] vertices = new float[verts * 2];
        float[] colors = new float[verts * 3];
        int[] indices = new int[verts];

        double V = (double) verts;
        for(int i=0; i<verts; i++){
            double I = (double) i;
            vertices[i*2 + 0] = (float) Math.cos((2*Math.PI*I)/V)*radius;
            vertices[i*2 + 1] = (float) Math.sin((2*Math.PI*I)/V)*radius;
            colors[i*3 + 0] = 0.6f;
            colors[i*3 + 1] = 0.6f;
            colors[i*3 + 2] = 1f;
            indices[i] = i;
        }

        return loader.loadToVao(vertices, colors, indices);
    }
}
