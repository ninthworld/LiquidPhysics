package org.ninthworld.liquidphysics.shader;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class CombineFXShader extends AbstractShader {
    private static final String VERTEX_FILE = "/shaders/fx/fx.vert";
    private static final String FRAGMENT_FILE = "/shaders/fx/combine/combinefx.frag";

    private static final int maxTextures = 16;
    private int[] location_textures;

    public CombineFXShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations(){
        location_textures = new int[maxTextures];
        for(int i=0; i<location_textures.length; i++) {
            location_textures[i] = super.getUniformLocation("textures[" + i + "]");
        }
    }

    public void connectTextureUnits(int texturesCount){
        for(int i=0; i<texturesCount * 2; i++) {
            super.loadInteger(location_textures[i], i);
        }
    }
}