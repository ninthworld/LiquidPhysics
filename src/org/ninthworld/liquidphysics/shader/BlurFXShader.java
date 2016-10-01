package org.ninthworld.liquidphysics.shader;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.ninthworld.liquidphysics.entities.CameraEntity;
import org.ninthworld.liquidphysics.helper.MatrixHelper;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class BlurFXShader extends AbstractShader {
    private static final String VERTEX_FILE = "/shaders/fx/fx.vert";
    private static final String FRAGMENT_FILE = "/shaders/fx/blur/blurfx.frag";

    private int location_texture;
    private int location_resolution;
    private int location_radius;
    private int location_dir;

    public BlurFXShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations(){
        location_texture = super.getUniformLocation("colorTexture");
        location_resolution = super.getUniformLocation("resolution");
        location_radius = super.getUniformLocation("radius");
        location_dir = super.getUniformLocation("dir");
    }

    public void loadAttributes(float resolution, float radius, Vector2f dir){
        super.loadFloat(location_resolution, resolution);
        super.loadFloat(location_radius, radius);
        super.loadVector2f(location_dir, dir);
    }

    public void connectTextureUnits(){
        super.loadInteger(location_texture, 0);
    }
}