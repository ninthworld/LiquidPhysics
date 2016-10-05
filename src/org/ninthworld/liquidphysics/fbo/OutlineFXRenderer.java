package org.ninthworld.liquidphysics.fbo;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.ninthworld.liquidphysics.shader.CombineFXShader;
import org.ninthworld.liquidphysics.shader.OutlineFXShader;

/**
 * Created by NinthWorld on 9/26/2016.
 */
public class OutlineFXRenderer {

    private OutlineFXShader shader;
    private ImageRenderer renderer;

    public OutlineFXRenderer() {
        shader = new OutlineFXShader();
        renderer = new ImageRenderer();
    }

    public void render(int colorTexture, int maskTexture, Vector4f outlineColor, int outlineSize, boolean isSpecular){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, maskTexture);

        shader.start();
        shader.connectTextureUnits();
        shader.loadOutlineColor(outlineColor);
        shader.loadOutlineSize(outlineSize);
        shader.loadIsSpecular(isSpecular);
        shader.loadScreenSize(new Vector2f(Display.getWidth(), Display.getHeight()));
        renderer.renderQuad();
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
        renderer.cleanUp();
    }
}
