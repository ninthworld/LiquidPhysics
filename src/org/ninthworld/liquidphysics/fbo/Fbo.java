
package org.ninthworld.liquidphysics.fbo;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class Fbo {

	private final int width;
	private final int height;

	private int frameBuffer;

	private boolean multisample = false;

	private int colorTexture;
	private int colorBuffer;

	/**
	 * Creates an FBO of a specified width and height, with the desired type of
	 * depth buffer attachment.
	 * 
	 * @param width
	 *            - the width of the FBO.
	 * @param height
	 *            - the height of the FBO.
	 */
	public Fbo(int width, int height) {
		this.width = width;
		this.height = height;
		initialiseFrameBuffer();
	}

    public void setMultisample(boolean multisample){
        this.multisample = multisample;
    }

	/**
	 * Deletes the frame buffer and its attachments when the game closes.
	 */
	public void cleanUp() {
		GL30.glDeleteFramebuffers(frameBuffer);
		GL11.glDeleteTextures(colorTexture);
		GL30.glDeleteRenderbuffers(colorBuffer);
	}

	/**
	 * Binds the frame buffer, setting it as the current render target. Anything
	 * rendered after this will be rendered to this FBO, and not to the screen.
	 */
	public void bindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);
	}

	/**
	 * Unbinds the frame buffer, setting the default frame buffer as the current
	 * render target. Anything rendered after this will be rendered to the
	 * screen, and not this FBO.
	 */
	public void unbindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	/**
	 * Binds the current FBO to be read from (not used in tutorial 43).
	 */
	public void bindToRead() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
	}

	/**
	 * @return The ID of the texture containing the colour buffer of the FBO.
	 */
	public int getColorTexture() {
		return colorTexture;
	}

    public void resolveToFbo(Fbo outputFbo){
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, outputFbo.frameBuffer);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, outputFbo.width, outputFbo.height, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        this.unbindFrameBuffer();
    }

    public void resolveToScreen(){
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer);
        GL11.glDrawBuffer(GL11.GL_BACK);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, Display.getWidth(), Display.getHeight(), GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        this.unbindFrameBuffer();
    }

	/**
	 * Creates the FBO along with a colour buffer texture attachment, and
	 * possibly a depth buffer.
	 */
	private void initialiseFrameBuffer() {
		createFrameBuffer();
        if(multisample){
            createMultisampleColorAttachment();
        }else {
            createTextureAttachment();
        }

		unbindFrameBuffer();
	}

	/**
	 * Creates a new frame buffer object and sets the buffer to which drawing
	 * will occur - colour attachment 0. This is the attachment where the colour
	 * buffer texture is.
	 * 
	 */
	private void createFrameBuffer() {
		frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
	}

	/**
	 * Creates a texture and sets it as the colour buffer attachment for this
	 * FBO.
	 */
	private void createTextureAttachment() {
		colorTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				(ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexture,
				0);
	}

    private void createMultisampleColorAttachment() {
        colorBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colorBuffer);
        GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL11.GL_RGBA8, width, height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER, colorBuffer);
    }
}
