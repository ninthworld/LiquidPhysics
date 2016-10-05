package org.ninthworld.liquidphysics.shader;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class OutlineFXShader extends AbstractShader {
    private static final String VERTEX_FILE = "/shaders/fx/fx.vert";
    private static final String FRAGMENT_FILE = "/shaders/fx/outline/outlinefx.frag";

    private int location_colorTexture;
    private int location_maskTexture;
    private int location_outlineColor;
    private int location_outlineSize;
    private int location_isSpecular;
    private int location_screenSize;

    public OutlineFXShader(){
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
        location_outlineColor = super.getUniformLocation("outlineColor");
        location_outlineSize = super.getUniformLocation("outlineSize");
        location_isSpecular = super.getUniformLocation("isSpecular");
        location_screenSize = super.getUniformLocation("screenSize");
    }

    public void connectTextureUnits(){
        super.loadInteger(location_colorTexture, 0);
        super.loadInteger(location_maskTexture, 1);
    }

    public void loadOutlineColor(Vector4f color){
        super.loadVector4f(location_outlineColor, color);
    }

    public void loadOutlineSize(int size){
        super.loadInteger(location_outlineSize, size);
    }

    public void loadIsSpecular(boolean isSpecular){
        super.loadBoolean(location_isSpecular, isSpecular);
    }

    public void loadScreenSize(Vector2f size){
        super.loadVector2f(location_screenSize, size);
    }
}