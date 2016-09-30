package org.ninthworld.liquidphysics.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.liquidphysics.entities.CameraEntity;
import org.ninthworld.liquidphysics.entities.Entity;
import org.ninthworld.liquidphysics.entities.LiquidEntity;
import org.ninthworld.liquidphysics.helper.MatrixHelper;
import org.ninthworld.liquidphysics.model.RawModel;
import org.ninthworld.liquidphysics.shader.LiquidShader;

import java.util.List;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class LiquidRenderer {

    private LiquidShader shader;

    public LiquidRenderer(Matrix4f projectionMatrix){
        this.shader = new LiquidShader();

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
    }

    public void render(List<LiquidEntity> entities, CameraEntity camera){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClearColor(0, 0, 0, 0);

        shader.start();
        shader.loadViewMatrix(camera);
        renderEntities(entities, shader);
        shader.stop();
    }

    private void renderEntities(List<LiquidEntity> entities, LiquidShader shader){
        for(LiquidEntity entity : entities){
            prepareRawModel(entity.getRawModel());
            prepareEntity(entity, shader);
            GL11.glDrawElements(GL11.GL_TRIANGLE_FAN, entity.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindRawModel();
        }
    }

    private void prepareEntity(Entity entity, LiquidShader shader) {
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