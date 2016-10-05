package org.ninthworld.liquidphysics.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.ninthworld.liquidphysics.model.Loader;
import org.ninthworld.liquidphysics.model.RawModel;
import org.ninthworld.liquidphysics.shader.CombineFXShader;
import org.ninthworld.liquidphysics.shader.MainFXShader;
import org.ninthworld.liquidphysics.shader.MultiplyFXShader;

/**
 * Created by NinthWorld on 9/25/2016.
 */
public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;

	private static BlurFXRenderer blurFXRenderer;
	private static ConstrainFXRenderer constrainFXRenderer;
    private static MainFXRenderer mainFXRenderer;
	private static CombineFXRenderer combineFXRenderer;
    private static OutlineFXRenderer outlineFXRenderer;
    private static MultiplyFXRenderer multiplyFXRenderer;
    private static OuterglowFXRenderer outerglowFXRenderer;

	public static void init(Loader loader){
		quad = loader.loadToVao(POSITIONS, 2);
		blurFXRenderer = new BlurFXRenderer();
		constrainFXRenderer = new ConstrainFXRenderer();
        mainFXRenderer = new MainFXRenderer();
		combineFXRenderer = new CombineFXRenderer();
        outlineFXRenderer = new OutlineFXRenderer();
        multiplyFXRenderer = new MultiplyFXRenderer();
        outerglowFXRenderer = new OuterglowFXRenderer();
	}

    public static void cleanUp(){
        blurFXRenderer.cleanUp();
        constrainFXRenderer.cleanUp();
        mainFXRenderer.cleanUp();
        combineFXRenderer.cleanUp();
        outlineFXRenderer.cleanUp();
        multiplyFXRenderer.cleanUp();
        outerglowFXRenderer.cleanUp();
    }

    public static void doPostProcessingOuterglow(int colorTexture, int maskTexture, Vector4f glowColor, int glowSize, boolean isMask){
        start();
        outerglowFXRenderer.render(colorTexture, maskTexture, glowColor, glowSize, isMask);
        end();
    }

	public static void doPostProcessingMultiply(int colorTexture, Vector4f multiply){
        start();
        multiplyFXRenderer.render(colorTexture, multiply);
        end();
    }

	public static void doPostProcessingOutline(int colorTexture, int maskTexture, Vector4f outlineColor, int outlineSize, boolean isSpecular){
        start();
        outlineFXRenderer.render(colorTexture, maskTexture, outlineColor, outlineSize, isSpecular);
        end();
    }

	public static void doPostProcessingCombine(int[][] textures){
		start();
        combineFXRenderer.render(textures);
        end();
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
