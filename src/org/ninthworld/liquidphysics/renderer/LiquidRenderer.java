package org.ninthworld.liquidphysics.renderer;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.particle.ParticleColor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.ninthworld.liquidphysics.entities.CameraEntity;
import org.ninthworld.liquidphysics.entities.ModelEntity;
import org.ninthworld.liquidphysics.helper.MatrixHelper;
import org.ninthworld.liquidphysics.model.Loader;
import org.ninthworld.liquidphysics.model.RawModel;
import org.ninthworld.liquidphysics.shader.LiquidShader;

import java.util.List;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class LiquidRenderer {

    private static final String liquidTexture = "/textures/liquid.png";

    private LiquidShader shader;

    private int liquidTextureId;

    public LiquidRenderer(Loader loader, Matrix4f projectionMatrix){
        this.shader = new LiquidShader();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        liquidTextureId = loader.loadTexture(liquidTexture);

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
    }

    public void render(RawModel model, Vec2[] particles, int particleCount, ParticleColor[] particleColors, ParticleColor particleColor, CameraEntity camera){
        GL11.glClearColor(0, 0, 0, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, liquidTextureId);

        shader.start();
        shader.loadViewMatrix(camera);
        shader.connectTextureUnits();
        renderEntities(model, particles, particleCount, particleColors, particleColor, shader);
        shader.stop();
    }

    public void render(RawModel model, List<Body> bodies, CameraEntity camera){
        GL11.glClearColor(0, 0, 0, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, liquidTextureId);

        shader.start();
        shader.loadViewMatrix(camera);
        shader.connectTextureUnits();
        renderEntities(model, bodies, shader);
        shader.stop();
    }

    private void renderEntities(RawModel model, Vec2[] particles, int particleCount, ParticleColor[] particleColors, ParticleColor particleColor, LiquidShader shader){
        prepareRawModel(model);
        if(particles != null){
            for(int i = 0; i < particleCount; i++){
                if(particleColors[i].r == particleColor.r && particleColors[i].g == particleColor.g && particleColors[i].b == particleColor.b) {
                    Vec2 particle = particles[i];
                    prepareEntity(particle, shader);
                    GL11.glDrawElements(GL11.GL_TRIANGLE_FAN, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                }
            }
        }
        unbindRawModel();
    }

    private void renderEntities(RawModel model, List<Body> bodies, LiquidShader shader){
        prepareRawModel(model);
        for(Body body : bodies){
            prepareEntity(body.getPosition(), shader);
            GL11.glDrawElements(GL11.GL_TRIANGLE_FAN, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        }
        unbindRawModel();
    }


    private void prepareEntity(Vec2 pos, LiquidShader shader) {
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