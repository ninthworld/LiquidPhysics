package org.ninthworld.liquidphysics.engine;

import javafx.scene.Camera;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.ninthworld.liquidphysics.entities.CameraEntity;
import org.ninthworld.liquidphysics.entities.LiquidEntity;
import org.ninthworld.liquidphysics.helper.MatrixHelper;
import org.ninthworld.liquidphysics.model.Loader;
import org.ninthworld.liquidphysics.model.RawModel;
import org.ninthworld.liquidphysics.renderer.LiquidRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class Main {

    private static Loader loader;
    private static LiquidRenderer liquidRenderer;

    private static CameraEntity camera;
    private static List<LiquidEntity> liquidEntities;

    private static void initialize(){
        DisplayManager.createDisplay();

        loader = new Loader();

        Matrix4f projectionMatrix = MatrixHelper.createProjectionMatrix();
        liquidRenderer = new LiquidRenderer(projectionMatrix);

        camera = new CameraEntity();
        liquidEntities = new ArrayList<>();

        RawModel rawModel = LiquidEntity.createLiquidModel(loader);
        LiquidEntity liquidEntity = new LiquidEntity();
        liquidEntity.setRawModel(rawModel);
        liquidEntities.add(liquidEntity);

        liquidEntity.setPosition(new Vector2f(32, 32));

        update();
    }

    private static void cleanUp(){
        DisplayManager.closeDisplay();
    }

    private static void update(){
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){

            liquidRenderer.render(liquidEntities, camera);

            DisplayManager.showFPS();
            DisplayManager.updateDisplay();
        }

        cleanUp();
    }

    public static void main(String[] args){
        initialize();
    }
}
