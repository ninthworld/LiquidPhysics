package org.ninthworld.liquidphysics.shader;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class MainFXShader extends AbstractShader {
    private static final String VERTEX_FILE = "/shaders/fx/fx.vert";
    private static final String FRAGMENT_FILE = "/shaders/fx/main/mainfx.frag";

    public static final int numTextures = 16;
    private int[] location_textures;
    private int location_numColor;
    private int location_numMask;
    private int location_screenSize;

    public MainFXShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations(){
        location_textures = new int[numTextures];
        for(int i=0; i<numTextures; i++){
            location_textures[i] = super.getUniformLocation("textures[" + i + "]");
        }

        location_numColor = super.getUniformLocation("numColor");
        location_numMask = super.getUniformLocation("numMask");
        location_screenSize = super.getUniformLocation("screenSize");
    }

    public void connectTextureUnits(int numColor, int numMask){
        for(int i=0; i<numColor+numMask; i++){
            super.loadInteger(location_textures[i], i);
        }

        super.loadInteger(location_numColor, numColor);
        super.loadInteger(location_numMask, numMask);

        super.loadVector2f(location_screenSize, new Vector2f(Display.getWidth(), Display.getHeight()));
    }
}