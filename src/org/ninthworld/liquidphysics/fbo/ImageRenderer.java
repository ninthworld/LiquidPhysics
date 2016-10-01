package org.ninthworld.liquidphysics.fbo;

import org.lwjgl.opengl.GL11;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class ImageRenderer {

	private Fbo fbo;

	public ImageRenderer(int width, int height) {
		this.fbo = new Fbo(width, height);
	}

	public ImageRenderer() {}

	public void renderQuad() {
		if (fbo != null) {
			fbo.bindFrameBuffer();
		}

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);

		if (fbo != null) {
			fbo.unbindFrameBuffer();
		}
	}

	public int getOutputTexture() {
		return fbo.getColorTexture();
	}

	public void cleanUp() {
		if (fbo != null) {
			fbo.cleanUp();
		}
	}

}