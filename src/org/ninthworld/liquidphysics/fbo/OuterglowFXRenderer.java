package org.ninthworld.liquidphysics.fbo;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.ninthworld.liquidphysics.shader.OuterglowFXShader;
import org.ninthworld.liquidphysics.shader.OutlineFXShader;

/**
 * Created by NinthWorld on 9/26/2016.
 */
public class OuterglowFXRenderer {

    private OuterglowFXShader shader;
    private ImageRenderer renderer;

    public OuterglowFXRenderer() {
        shader = new OuterglowFXShader();
        renderer = new ImageRenderer();
    }

    public void render(int colorTexture, int maskTexture, Vector4f glowColor, int glowSize, boolean isMask){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, maskTexture);

        shader.start();
        shader.connectTextureUnits();
        shader.loadGlowColor(glowColor);
        shader.loadGlowSize(glowSize);
        shader.loadIsMask(isMask);
        shader.loadScreenSize(new Vector2f(Display.getWidth(), Display.getHeight()));
        renderer.renderQuad();
        shader.stop();
    }

    public void cleanUp(){
        shader.cleanUp();
        renderer.cleanUp();
    }
}
