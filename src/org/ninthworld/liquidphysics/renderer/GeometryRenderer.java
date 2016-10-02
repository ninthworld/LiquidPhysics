package org.ninthworld.liquidphysics.renderer;

import org.jbox2d.common.Vec2;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.ninthworld.liquidphysics.engine.Main;
import org.ninthworld.liquidphysics.entities.CameraEntity;
import org.ninthworld.liquidphysics.entities.ModelEntity;
import org.ninthworld.liquidphysics.helper.MatrixHelper;
import org.ninthworld.liquidphysics.model.RawModel;
import org.ninthworld.liquidphysics.shader.GeometryShader;

import java.util.List;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class GeometryRenderer {

    private GeometryShader shader;

    public GeometryRenderer(Matrix4f projectionMatrix){
        this.shader = new GeometryShader();

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
    }

    public void render(List<ModelEntity> entities, CameraEntity camera, boolean isMask){
        GL11.glClearColor(0, 0, 0, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        shader.start();
        shader.loadViewMatrix(camera);
        shader.setIsMask(isMask);
        renderEntities(entities, shader);
        shader.stop();
    }

    private void renderEntities(List<ModelEntity> entities, GeometryShader shader){
        for(ModelEntity entity : entities){
            prepareRawModel(entity.getRawModel());
            prepareEntity(entity, shader);
            GL11.glDrawElements(GL11.GL_TRIANGLE_FAN, entity.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindRawModel();
        }
    }

    private void prepareEntity(ModelEntity entity, GeometryShader shader) {
        Matrix4f transformationMatrix = MatrixHelper.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }

    private void prepareRawModel(RawModel rawModel) {
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
    }

    private void unbindRawModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }
}