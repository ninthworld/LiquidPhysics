package org.ninthworld.liquidphysics.shader;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class OuterglowFXShader extends AbstractShader {
    private static final String VERTEX_FILE = "/shaders/fx/fx.vert";
    private static final String FRAGMENT_FILE = "/shaders/fx/outerglow/outerglowfx.frag";

    private int location_colorTexture;
    private int location_maskTexture;
    private int location_glowColor;
    private int location_glowSize;
    private int location_isMask;
    private int location_screenSize;

    public OuterglowFXShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations(){
        location_colorTexture = super.getUniformLocation("colorTexture");
        location_maskTexture = super.getUniformLocation("maskTexture");
        location_glowColor = super.getUniformLocation("glowColor");
        location_glowSize = super.getUniformLocation("glowSize");
        location_isMask = super.getUniformLocation("isMask");
        location_screenSize = super.getUniformLocation("screenSize");
    }

    public void connectTextureUnits(){
        super.loadInteger(location_colorTexture, 0);
        super.loadInteger(location_maskTexture, 1);
    }

    public void loadGlowColor(Vector4f color){
        super.loadVector4f(location_glowColor, color);
    }

    public void loadGlowSize(int size){
        super.loadInteger(location_glowSize, size);
    }

    public void loadIsMask(boolean isSpecular){
        super.loadBoolean(location_isMask, isSpecular);
    }

    public void loadScreenSize(Vector2f size){
        super.loadVector2f(location_screenSize, size);
    }
}