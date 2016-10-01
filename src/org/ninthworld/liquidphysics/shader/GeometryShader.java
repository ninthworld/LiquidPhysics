package org.ninthworld.liquidphysics.shader;

import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.liquidphysics.entities.CameraEntity;
import org.ninthworld.liquidphysics.helper.MatrixHelper;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class GeometryShader extends AbstractShader {
    private static final String VERTEX_FILE = "/shaders/geometry/geometry.vert";
    private static final String FRAGMENT_FILE = "/shaders/geometry/geometry.frag";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;

    private int location_isMask;

    public GeometryShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "color");
    }

    @Override
    protected void getAllUniformLocations(){
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");

        location_isMask = super.getUniformLocation("isMask");
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(CameraEntity camera){
        Matrix4f viewMatrix = MatrixHelper.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void setIsMask(boolean isMask){
        super.loadInteger(location_isMask, (isMask ? 1 : 0));
    }
}