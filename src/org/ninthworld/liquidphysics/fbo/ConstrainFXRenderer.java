package org.ninthworld.liquidphysics.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;
import org.ninthworld.liquidphysics.shader.BlurFXShader;
import org.ninthworld.liquidphysics.shader.ConstrainFXShader;

/**
 * Created by NinthWorld on 9/26/2016.
 */
public class ConstrainFXRenderer {

    private ConstrainFXShader shader;
    private ImageRenderer renderer;


    public ConstrainFXRenderer() {
        shader = new ConstrainFXShader();
        renderer = new ImageRenderer();
    }

    public void render(int texture){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

        shader.start();
        shader.connectTextureUnits();
        renderer.renderQuad();
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
        renderer.cleanUp();
    }
}
