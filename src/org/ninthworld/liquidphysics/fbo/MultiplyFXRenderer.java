package org.ninthworld.liquidphysics.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector4f;
import org.ninthworld.liquidphysics.shader.CombineFXShader;
import org.ninthworld.liquidphysics.shader.MultiplyFXShader;

/**
 * Created by NinthWorld on 9/26/2016.
 */
public class MultiplyFXRenderer {

    private MultiplyFXShader shader;
    private ImageRenderer renderer;

    public MultiplyFXRenderer() {
        shader = new MultiplyFXShader();
        renderer = new ImageRenderer();
    }

    public void render(int colorTexture, Vector4f multiply){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);

        shader.start();
        shader.connectTextureUnits();
        shader.loadMultiply(multiply);
        renderer.renderQuad();
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
        renderer.cleanUp();
    }
}
