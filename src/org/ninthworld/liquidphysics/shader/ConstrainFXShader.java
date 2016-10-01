package org.ninthworld.liquidphysics.shader;

import org.lwjgl.util.vector.Vector2f;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class ConstrainFXShader extends AbstractShader {
    private static final String VERTEX_FILE = "/shaders/fx/fx.vert";
    private static final String FRAGMENT_FILE = "/shaders/fx/constrain/constrainfx.frag";

    private int location_texture;

    public ConstrainFXShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations(){
        location_texture = super.getUniformLocation("colorTexture");
    }

    public void connectTextureUnits(){
        super.loadInteger(location_texture, 0);
    }
}