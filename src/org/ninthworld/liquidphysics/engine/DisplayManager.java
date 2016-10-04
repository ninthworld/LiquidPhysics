package org.ninthworld.liquidphysics.engine;


import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.liquidphysics.helper.MatrixHelper;

import java.awt.*;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class DisplayManager {

    private static final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    private static final int        WIDTH   = 720; //(int)(gd.getDisplayMode().getWidth() * 0.8);
    private static final int        HEIGHT  = 480; //(int)(gd.getDisplayMode().getHeight() * 0.8);
    private static final int        FPS_CAP = 60;
    private static final boolean    VSYNC   = true;
    private static final String     TITLE   = "LiquidPhysics";
    private static StringBuilder titleAppend;

    public static void createDisplay(){
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setVSyncEnabled(VSYNC);
            Display.create(new PixelFormat(), new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true));
            //GL11.glEnable(GL13.GL_MULTISAMPLE);
            Display.setTitle(TITLE);
        } catch(LWJGLException e){
            e.printStackTrace();
            System.exit(0);
        }

        GL11.glViewport(0, 0, WIDTH, HEIGHT);

        titleAppend = new StringBuilder();
    }

    private static long fpsTime = System.nanoTime();
    private static int fpsCount = 0;
    private static String fpsString = "";
    public static void showFPS(){
        if(System.nanoTime() - fpsTime > 1000000000L){
            fpsString = " - " + fpsCount + " FPS";
            fpsTime = System.nanoTime();
            fpsCount = 0;
        }else{
            fpsCount++;
        }

        titleAppend.append(fpsString);
    }

    public static void showString(String str){
        titleAppend.append(" - " + str);
    }

    public static void updateDisplay(){
        Display.setTitle(TITLE + "" + titleAppend);
        titleAppend = new StringBuilder();

        Display.sync(FPS_CAP);
        Display.update();
    }

    public static void closeDisplay(){
        Display.destroy();
    }
}
