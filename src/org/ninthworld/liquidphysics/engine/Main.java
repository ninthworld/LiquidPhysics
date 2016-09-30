package org.ninthworld.liquidphysics.engine;

import javafx.scene.Camera;
import javafx.scene.shape.Polygon;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.ninthworld.liquidphysics.entities.CameraEntity;
import org.ninthworld.liquidphysics.entities.LiquidEntity;
import org.ninthworld.liquidphysics.helper.MatrixHelper;
import org.ninthworld.liquidphysics.model.Loader;
import org.ninthworld.liquidphysics.model.RawModel;
import org.ninthworld.liquidphysics.renderer.LiquidRenderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class Main {

    private static Loader loader;
    private static LiquidRenderer liquidRenderer;

    private static CameraEntity camera;
    private static List<LiquidEntity> liquidEntities;

    private static World world;
    private static Set<Body> bodies;

    private static RawModel rawModel;

    private static void initialize(){
        DisplayManager.createDisplay();

        loader = new Loader();

        Matrix4f projectionMatrix = MatrixHelper.createProjectionMatrix();
        liquidRenderer = new LiquidRenderer(projectionMatrix);

        camera = new CameraEntity();
        liquidEntities = new ArrayList<>();

        rawModel = LiquidEntity.createLiquidModel(loader);
        LiquidEntity liquidEntity = new LiquidEntity();
        liquidEntity.setRawModel(rawModel);
        liquidEntities.add(liquidEntity);

        liquidEntity.setPosition(new Vector2f(0, 0));

        world = new World(new Vec2(0, 9.8f * 10f), false);
        bodies = new HashSet<>();

//        for(int i=0; i<10; i++) {
//            for(int j=0; j<10; j++) {
//                BodyDef circleDef = new BodyDef();
//                circleDef.position.set(Display.getWidth() / 2f + i, Display.getHeight() / 2f + j);
//                circleDef.type = BodyType.DYNAMIC;
//                CircleShape circleShape = new CircleShape();
//                circleShape.m_radius = 8f;
//                Body circle = world.createBody(circleDef);
//                FixtureDef circleFixture = new FixtureDef();
//                circleFixture.density = 0.5f;
//                circleFixture.restitution = 0.5f;
//                circleFixture.friction = 0.2f;
//                circleFixture.shape = circleShape;
//                circle.createFixture(circleFixture);
//                bodies.add(circle);
//            }
//        }

        BodyDef groundDef = new BodyDef();
        groundDef.position.set(0, Display.getHeight());
        groundDef.type = BodyType.STATIC;
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(Display.getWidth(), 0);
        Body ground = world.createBody(groundDef);
        FixtureDef groundFixture = new FixtureDef();
        groundFixture.density = 1;
        groundFixture.shape = groundShape;
        ground.createFixture(groundFixture);
        bodies.add(ground);

        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(0, Display.getHeight()/2f);
        leftWallDef.type = BodyType.STATIC;
        PolygonShape leftWallShape = new PolygonShape();
        leftWallShape.setAsBox(0, Display.getHeight());
        Body leftWall = world.createBody(leftWallDef);
        FixtureDef leftWallFixture = new FixtureDef();
        leftWallFixture.density = 1;
        leftWallFixture.shape = leftWallShape;
        leftWall.createFixture(leftWallFixture);
        bodies.add(leftWall);

        BodyDef rightWallDef = new BodyDef();
        rightWallDef.position.set(Display.getWidth(), Display.getHeight()/2f);
        rightWallDef.type = BodyType.STATIC;
        PolygonShape rightWallShape = new PolygonShape();
        rightWallShape.setAsBox(0, Display.getHeight());
        Body rightWall = world.createBody(rightWallDef);
        FixtureDef rightWallFixture = new FixtureDef();
        rightWallFixture.density = 1;
        rightWallFixture.shape = rightWallShape;
        rightWall.createFixture(leftWallFixture);
        bodies.add(rightWall);

        update();
    }

    private static void cleanUp(){
        DisplayManager.closeDisplay();
    }

    private static void update(){
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            world.step(1/60f, 8, 3);

            GL11.glClearColor(0.169f, 0.169f, 0.169f, 1f);

            List<LiquidEntity> entities = new ArrayList<>();
            for(Body body : bodies){
                if(body.getType() == BodyType.DYNAMIC){
                    LiquidEntity entity = new LiquidEntity();
                    entity.setRawModel(rawModel);
                    entity.setPosition(new Vector2f(body.getPosition().x, body.getPosition().y));
                    entities.add(entity);
                }
            }
            liquidRenderer.render(entities, camera);

            DisplayManager.showFPS();
            DisplayManager.updateDisplay();
        }

        cleanUp();
    }

    public static void main(String[] args){
        initialize();
    }
}
