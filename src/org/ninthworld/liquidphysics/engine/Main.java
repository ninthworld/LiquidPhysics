package org.ninthworld.liquidphysics.engine;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.particle.ParticleDef;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.ninthworld.liquidphysics.entities.CameraEntity;
import org.ninthworld.liquidphysics.entities.LiquidEntity;
import org.ninthworld.liquidphysics.fbo.Fbo;
import org.ninthworld.liquidphysics.fbo.PostProcessing;
import org.ninthworld.liquidphysics.helper.MatrixHelper;
import org.ninthworld.liquidphysics.model.Loader;
import org.ninthworld.liquidphysics.model.RawModel;
import org.ninthworld.liquidphysics.renderer.GeometryRenderer;
import org.ninthworld.liquidphysics.renderer.LiquidRenderer;

import java.util.*;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class Main {

    private static Loader loader;
    private static LiquidRenderer liquidRenderer;
    private static GeometryRenderer geometryRenderer;

    private static Vec2[] geometry;

    private static CameraEntity camera;

    public static final int MAX_PARTICLES = 1000;
    public static final float PARTICLE_RADIUS = 4;
    private static World world;

    private static Set<Body> bodies;
    private static RawModel liquidModel;
    private static RawModel boxModel;

    private static Map<String, Fbo> fbos;

    private static void initialize(){
        DisplayManager.createDisplay();

        loader = new Loader();
        PostProcessing.init(loader);
        camera = new CameraEntity();

        Matrix4f projectionMatrix = MatrixHelper.createProjectionMatrix();
        liquidRenderer = new LiquidRenderer(projectionMatrix);
        geometryRenderer = new GeometryRenderer(projectionMatrix);

        liquidModel = LiquidEntity.createLiquidModel(loader);

        float size = 32f;
        float[] vertices = new float[]{
                size, size,
                size, -size,
                -size, -size
        };
        float[] colors = new float[]{
                1f, 0f, 1f,
                1f, 0f, 1f,
                1f, 0f, 1f
        };
        int[] indices = new int[]{
                0, 1, 2
        };

        boxModel = loader.loadToVao(vertices, colors, indices);

        geometry = new Vec2[]{
                new Vec2(Display.getWidth()/1.1f, Display.getHeight()/1.1f)
        };

        fbos = new HashMap<>();
        fbos.put("geometryColor", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("geometryMask", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("particles", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("blurX1", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("blurX2", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("blurY1", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("blurY2", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("particlesMask", new Fbo(Display.getWidth(), Display.getHeight()));

        world = new World(new Vec2(0, 9.8f * 10f));
        world.setParticleRadius(PARTICLE_RADIUS);
        world.setParticleDamping(1f);
        world.setParticleDensity(1f);
        world.setParticleGravityScale(1f);
        world.setParticleMaxCount(2000);

//        for(int i=0; i<MAX_PARTICLES/8; i++) {
//            ParticleDef particleDef = new ParticleDef();
//            particleDef.position.set(Display.getWidth()/2f + i*0.4f, Display.getHeight()/2f);
//            world.createParticle(particleDef);
//        }

        bodies = new HashSet<>();

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
        rightWall.createFixture(rightWallFixture);
        bodies.add(rightWall);

        BodyDef boxDef = new BodyDef();
        boxDef.position.set(geometry[0]);
        boxDef.type = BodyType.STATIC;
        PolygonShape boxShape = new PolygonShape();

        Vec2[] verts = new Vec2[vertices.length/2];
        for(int i=0; i<verts.length; i++){
            verts[i] = new Vec2(vertices[i*2], -vertices[i*2+1]);
        }
        boxShape.set(verts, verts.length);

        Body box = world.createBody(boxDef);
        FixtureDef boxFixture = new FixtureDef();
        boxFixture.density = 1;
        boxFixture.shape = boxShape;
        box.createFixture(boxFixture);
        bodies.add(box);

        update();
    }

    private static void cleanUp(){
        liquidRenderer.cleanUp();

        for(Fbo fbo : fbos.values()){
            fbo.cleanUp();
        }

        loader.cleanUp();
        PostProcessing.cleanUp();
        DisplayManager.closeDisplay();
    }

    private static int particleCount = 0;
    private static void update(){
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            world.step(1/60f, 8, 3);

            if(Mouse.isButtonDown(0) && particleCount < MAX_PARTICLES){
                ParticleDef particleDef = new ParticleDef();
                particleDef.position.set(Mouse.getX(), Display.getHeight() - Mouse.getY());
                world.createParticle(particleDef);
                particleCount++;
            }

            fbos.get("geometryMask").bindFrameBuffer();
            geometryRenderer.render(boxModel, geometry, camera, true);
            fbos.get("geometryMask").unbindFrameBuffer();

            fbos.get("geometryColor").bindFrameBuffer();
            geometryRenderer.render(boxModel, geometry, camera, false);
            fbos.get("geometryColor").unbindFrameBuffer();

            fbos.get("particles").bindFrameBuffer();
            liquidRenderer.render(liquidModel, world.getParticlePositionBuffer(), camera, true);
            fbos.get("particles").unbindFrameBuffer();

            fbos.get("blurX1").bindFrameBuffer();
            PostProcessing.doPostProcessingBlur(fbos.get("particles").getColorTexture(), 2, Display.getWidth(), new Vector2f(1, 0));
            fbos.get("blurX1").unbindFrameBuffer();

            fbos.get("blurX2").bindFrameBuffer();
            PostProcessing.doPostProcessingBlur(fbos.get("blurX1").getColorTexture(), 2, Display.getWidth(), new Vector2f(1, 0));
            fbos.get("blurX2").unbindFrameBuffer();

            fbos.get("blurY1").bindFrameBuffer();
            PostProcessing.doPostProcessingBlur(fbos.get("blurX2").getColorTexture(), 2, Display.getHeight(), new Vector2f(0, 1));
            fbos.get("blurY1").unbindFrameBuffer();

            fbos.get("blurY2").bindFrameBuffer();
            PostProcessing.doPostProcessingBlur(fbos.get("blurY1").getColorTexture(), 2, Display.getHeight(), new Vector2f(0, 1));
            fbos.get("blurY2").unbindFrameBuffer();

            fbos.get("particlesMask").bindFrameBuffer();
            PostProcessing.doPostProcessingConstrain(fbos.get("blurY2").getColorTexture());
            fbos.get("particlesMask").unbindFrameBuffer();

            PostProcessing.doPostProcessingMain(
                    new int[]{
                            fbos.get("geometryColor").getColorTexture(),
                            fbos.get("particlesMask").getColorTexture()
                    }, new int[]{
                            fbos.get("geometryMask").getColorTexture(),
                            fbos.get("particlesMask").getColorTexture()
                    }
            );

            DisplayManager.showFPS();
            DisplayManager.updateDisplay();
        }

        cleanUp();
    }

    public static void main(String[] args){
        initialize();
    }
}
