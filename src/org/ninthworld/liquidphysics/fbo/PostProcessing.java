package org.ninthworld.liquidphysics.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.ninthworld.liquidphysics.model.Loader;
import org.ninthworld.liquidphysics.model.RawModel;
import org.ninthworld.liquidphysics.shader.MainFXShader;

/**
 * Created by NinthWorld on 9/25/2016.
 */
public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;

	private static BlurFXRenderer blurFXRenderer;
	private static ConstrainFXRenderer constrainFXRenderer;
    private static MainFXRenderer mainFXRenderer;

	public static void init(Loader loader){
		quad = loader.loadToVao(POSITIONS, 2);
		blurFXRenderer = new BlurFXRenderer();
		constrainFXRenderer = new ConstrainFXRenderer();
        mainFXRenderer = new MainFXRenderer();
	}

	public static void doPostProcessingBlur(int texture, float radius, float resolution, Vector2f dir){
		start();
		blurFXRenderer.render(texture, radius, resolution, dir);
		end();
	}

	public static void doPostProcessingConstrain(int texture){
		start();
		constrainFXRenderer.render(texture);
		end();
	}

    public static void doPostProcessingMain(int[] textures, int[] masks){
        start();
        mainFXRenderer.render(textures, masks);
        end();
    }
	
	public static void cleanUp(){
		blurFXRenderer.cleanUp();
		constrainFXRenderer.cleanUp();
        mainFXRenderer.cleanUp();
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}



}
