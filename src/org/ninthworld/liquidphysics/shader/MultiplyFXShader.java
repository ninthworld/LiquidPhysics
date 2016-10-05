package org.ninthworld.liquidphysics.shader;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class MultiplyFXShader extends AbstractShader {
    private static final String VERTEX_FILE = "/shaders/fx/fx.vert";
    private static final String FRAGMENT_FILE = "/shaders/fx/multiply/multiplyfx.frag";

    private int location_colorTexture;
    private int location_multiply;

    public MultiplyFXShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations(){
        location_colorTexture = super.getUniformLocation("colorTexture");
        location_multiply = super.getUniformLocation("multiply");
    }

    public void connectTextureUnits(){
        super.loadInteger(location_colorTexture, 0);
    }

    public void loadMultiply(Vector4f multiply){
        super.loadVector4f(location_multiply, multiply);
    }
}