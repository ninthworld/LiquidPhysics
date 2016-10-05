package org.ninthworld.liquidphysics.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.ninthworld.liquidphysics.shader.CombineFXShader;
import org.ninthworld.liquidphysics.shader.MainFXShader;

/**
 * Created by NinthWorld on 9/26/2016.
 */
public class CombineFXRenderer {

    private CombineFXShader shader;
    private ImageRenderer renderer;

    public CombineFXRenderer() {
        shader = new CombineFXShader();
        renderer = new ImageRenderer();
    }

    public void render(int[][] textures){
        for(int i=0; i<textures.length; i++){
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i*2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[i][0]);
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i*2 + 1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[i][1]);
        }

        shader.start();
        shader.connectTextureUnits(textures.length);
        renderer.renderQuad();
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
        renderer.cleanUp();
    }
}
