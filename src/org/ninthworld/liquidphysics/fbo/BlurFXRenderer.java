package org.ninthworld.liquidphysics.fbo;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;
import org.ninthworld.liquidphysics.shader.BlurFXShader;

/**
 * Created by NinthWorld on 9/26/2016.
 */
public class BlurFXRenderer {

    private BlurFXShader shader;
    private ImageRenderer renderer;


    public BlurFXRenderer() {
        shader = new BlurFXShader();
        renderer = new ImageRenderer();
    }

    public void render(int texture, float radius, float resolution, Vector2f dir){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

        shader.start();
        shader.loadAttributes(resolution, radius, dir);
        shader.connectTextureUnits();
        renderer.renderQuad();
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
        renderer.cleanUp();
    }
}
