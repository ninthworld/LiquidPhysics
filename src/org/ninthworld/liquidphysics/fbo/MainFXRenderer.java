package org.ninthworld.liquidphysics.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;
import org.ninthworld.liquidphysics.shader.BlurFXShader;
import org.ninthworld.liquidphysics.shader.MainFXShader;

/**
 * Created by NinthWorld on 9/26/2016.
 */
public class MainFXRenderer {

    private MainFXShader shader;
    private ImageRenderer renderer;


    public MainFXRenderer() {
        shader = new MainFXShader();
        renderer = new ImageRenderer();
    }

    public void render(int[] textures, int[] masks){
        for(int i=0; i<textures.length; i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i*2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[i]);
        }

        for(int i=0; i<masks.length; i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i*2 + 1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, masks[i]);
        }

        shader.start();
        shader.connectTextureUnits(textures.length, masks.length);
        renderer.renderQuad();
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
        renderer.cleanUp();
    }
}
