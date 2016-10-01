package org.ninthworld.liquidphysics.renderer;

import org.jbox2d.common.Vec2;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.ninthworld.liquidphysics.engine.Main;
import org.ninthworld.liquidphysics.entities.CameraEntity;
import org.ninthworld.liquidphysics.helper.MatrixHelper;
import org.ninthworld.liquidphysics.model.RawModel;
import org.ninthworld.liquidphysics.shader.GeometryShader;

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

    public void render(RawModel model, Vec2[] entityPositions, CameraEntity camera, boolean isMask){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClearColor(0, 0, 0, 1);

        shader.start();
        shader.loadViewMatrix(camera);
        shader.setIsMask(isMask);
        renderEntities(model, entityPositions, shader);
        shader.stop();
    }

    private void renderEntities(RawModel model, Vec2[] entityPositions, GeometryShader shader){
        prepareRawModel(model);
        if(entityPositions != null){
            for(int i = 0; i<entityPositions.length; i++){
                Vec2 particle = entityPositions[i];
                prepareEntity(particle, shader);
                GL11.glDrawElements(GL11.GL_TRIANGLE_FAN, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
        }
        unbindRawModel();
    }

    private void prepareEntity(Vec2 pos, GeometryShader shader) {
        Matrix4f transformationMatrix = MatrixHelper.createTransformationMatrix(new Vector2f(pos.x, pos.y), 0f, new Vector2f(1, 1));
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