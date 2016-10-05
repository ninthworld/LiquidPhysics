package org.ninthworld.liquidphysics.engine;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.particle.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.ninthworld.liquidphysics.entities.*;
import org.ninthworld.liquidphysics.fbo.Fbo;
import org.ninthworld.liquidphysics.fbo.PostProcessing;
import org.ninthworld.liquidphysics.helper.MatrixHelper;
import org.ninthworld.liquidphysics.model.Loader;
import org.ninthworld.liquidphysics.model.RawModel;
import org.ninthworld.liquidphysics.renderer.GeometryRenderer;
import org.ninthworld.liquidphysics.renderer.LiquidRenderer;
import org.ninthworld.liquidphysics.renderer.ShipRenderer;

import java.util.*;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class Main {

    private static CameraEntity camera;

    private static Loader loader;
    private static LiquidRenderer liquidRenderer;
    private static GeometryRenderer geometryRenderer;
    private static Map<String, Fbo> fbos;

    public static final float PARTICLE_RADIUS = 2;
    private static int WORLD_WIDTH;
    private static int WORLD_HEIGHT;

    private static World world;

    private static RawModel liquidModel;
    private static List<ModelEntity> polygons;
    private static int polygonSelected = 3;

    private static List<Body> obsidians;
    private static List<Body> ices;

    private static ParticleSystem particleSystem;
    private static ParticleSystem invParticleSystem;

    private static void initialize(){
        loader = new Loader();
        DisplayManager.createDisplay();
        WORLD_WIDTH = Display.getWidth();
        WORLD_HEIGHT = Display.getHeight();

        Matrix4f projectionMatrix = MatrixHelper.createProjectionMatrix();
        liquidRenderer = new LiquidRenderer(loader, projectionMatrix);
        geometryRenderer = new GeometryRenderer(projectionMatrix);
        PostProcessing.init(loader);

        camera = new CameraEntity();

        liquidModel = createPolygonModel(new Vec2[]{new Vec2(-16, -16), new Vec2(16, -16), new Vec2(16, 16), new Vec2(-16, 16)}, new Vector3f(1, 1, 1));

        fbos = new HashMap<>();
        fbos.put("geometryColor", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("geometryMask", new Fbo(Display.getWidth(), Display.getHeight()));

        fbos.put("waterColor", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("waterColor2", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("waterMask", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("waterMask2", new Fbo(Display.getWidth(), Display.getHeight()));

        fbos.put("lavaColor", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("lavaColor2", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("lavaMask", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("lavaMask2", new Fbo(Display.getWidth(), Display.getHeight()));

        fbos.put("obsidianColor", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("obsidianColor2", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("obsidianMask", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("obsidianMask2", new Fbo(Display.getWidth(), Display.getHeight()));

        fbos.put("steamColor", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("steamColor2", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("steamMask", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("steamMask2", new Fbo(Display.getWidth(), Display.getHeight()));

        fbos.put("iceColor", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("iceColor2", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("iceMask", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("iceMask2", new Fbo(Display.getWidth(), Display.getHeight()));

        world = new World(new Vec2(0, 9.8f * 10f));

        particleSystem = new ParticleSystem(world);
        particleSystem.setParticleRadius(PARTICLE_RADIUS);
        particleSystem.setParticleDamping(1f);
        particleSystem.setParticleDensity(1f);
        particleSystem.setParticleGravityScale(2f);
        particleSystem.setParticleMaxCount(20000);

        invParticleSystem = new ParticleSystem(world);
        invParticleSystem.setParticleRadius(1f);
        invParticleSystem.setParticleDamping(1f);
        invParticleSystem.setParticleDensity(1f);
        invParticleSystem.setParticleGravityScale(-1f);
        invParticleSystem.setParticleMaxCount(5000);

        obsidians = new ArrayList<>();
        ices = new ArrayList<>();

        polygons = new ArrayList<>();
        // Bottom
        polygons.add(createPolygon(WORLD_WIDTH/2, WORLD_HEIGHT, new Vec2[]{new Vec2(-WORLD_WIDTH/2, -8), new Vec2(WORLD_WIDTH/2, -8), new Vec2(WORLD_WIDTH/2, 8), new Vec2(-WORLD_WIDTH/2, 8)}, new Vector3f(0.8f, 0.8f, 0.8f)));
        // Top
        polygons.add(createPolygon(WORLD_WIDTH/2, 0, new Vec2[]{new Vec2(-WORLD_WIDTH/2, -8), new Vec2(WORLD_WIDTH/2, -8), new Vec2(WORLD_WIDTH/2, 8), new Vec2(-WORLD_WIDTH/2, 8)}, new Vector3f(0.8f, 0.8f, 0.8f)));
        // Left
        polygons.add(createPolygon(0, WORLD_HEIGHT/2, new Vec2[]{new Vec2(-8, -WORLD_HEIGHT/2), new Vec2(8, -WORLD_HEIGHT/2), new Vec2(8, WORLD_HEIGHT/2), new Vec2(-8, WORLD_HEIGHT/2)}, new Vector3f(0.8f, 0.8f, 0.8f)));
        // Right
        polygons.add(createPolygon(WORLD_WIDTH, WORLD_HEIGHT/2, new Vec2[]{new Vec2(-8, -WORLD_HEIGHT/2), new Vec2(8, -WORLD_HEIGHT/2), new Vec2(8, WORLD_HEIGHT/2), new Vec2(-8, WORLD_HEIGHT/2)}, new Vector3f(0.8f, 0.8f, 0.8f)));

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

    private static boolean iceRefresh = true;
    private static long iceRefreshTime = System.nanoTime();
    private static TimeStep timeStep = new TimeStep();
    private static void update(){
        timeStep.dt = 1/60f;
        timeStep.inv_dt = 60f;
        timeStep.positionIterations = 8;//*4;
        timeStep.velocityIterations = 3;//*4;
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            world.step(timeStep.dt, timeStep.positionIterations, timeStep.velocityIterations);
            particleSystem.solve(timeStep);
            invParticleSystem.solve(timeStep);

            if(System.nanoTime() > iceRefreshTime + 1000000L * 10L){
                iceRefreshTime = System.nanoTime();
                iceRefresh = true;
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_P)){
                for(int i=0; i<particleSystem.getParticleCount(); i++){
                    Vec2 pos = particleSystem.getParticlePositionBuffer()[i];
                    if(pos.x < 0 || pos.y < 0 || pos.x > WORLD_WIDTH || pos.y > WORLD_HEIGHT){
                        particleSystem.destroyParticle(i, false);
                    }
                }
                for(int i=0; i<invParticleSystem.getParticleCount(); i++){
                    Vec2 pos = invParticleSystem.getParticlePositionBuffer()[i];
                    if(pos.x < 0 || pos.y < 0 || pos.x > WORLD_WIDTH || pos.y > WORLD_HEIGHT){
                        invParticleSystem.destroyParticle(i, false);
                    }
                }
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_W) && camera.getPosition().y > 0){
                camera.increasePosition(0, -8);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_S) && camera.getPosition().y < WORLD_HEIGHT - Display.getHeight()){
                camera.increasePosition(0, 8);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_A) && camera.getPosition().x > 0){
                camera.increasePosition(-8, 0);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_D) && camera.getPosition().x < WORLD_WIDTH - Display.getWidth()){
                camera.increasePosition(8, 0);
            }

            for(int i=0; i<invParticleSystem.getParticleCount(); i++){
                SteamEntity steam = (SteamEntity) invParticleSystem.getParticleUserDataBuffer()[i];
                if(steam.timer > 100){
                    invParticleSystem.destroyParticle(i, false);
                }else{
                    steam.timer++;
                }
            }

            for(int i=0; i<particleSystem.m_bodyContactCount; i++){
                int p = particleSystem.m_bodyContactBuffer[i].index;
                Body body = particleSystem.m_bodyContactBuffer[i].body;

                if(body.getUserData() instanceof SolidEntity && particleSystem.getParticleUserDataBuffer()[p] instanceof LiquidEntity){
                    LiquidEntity liquid = (LiquidEntity) particleSystem.getParticleUserDataBuffer()[p];
                    SolidEntity solid = (SolidEntity) body.getUserData();

                    float avgTemp = (liquid.temperature + solid.temperature)/2f;
                    liquid.temperature = solid.temperature = avgTemp;

                    if(solid instanceof ObsidianEntity){
                        if(solid.temperature > ObsidianEntity.toLavaTemp){
                            createLava(body.getPosition().x, body.getPosition().y, solid.temperature);
                            world.destroyBody(body);
                            obsidians.remove(body);
                        }
                    }else if(solid instanceof IceEntity){
                        if(solid.temperature > IceEntity.toWaterTemp){
                            createWater(body.getPosition().x, body.getPosition().y, solid.temperature);
                            world.destroyBody(body);
                            ices.remove(body);
                        }
                    }
                }
            }

            for(int i=0; i<particleSystem.m_contactCount; i++){
                int pA = particleSystem.m_contactBuffer[i].indexA;
                int pB = particleSystem.m_contactBuffer[i].indexB;

                LiquidEntity pA_UD = (LiquidEntity) particleSystem.getParticleUserDataBuffer()[pA];
                LiquidEntity pB_UD = (LiquidEntity) particleSystem.getParticleUserDataBuffer()[pB];

                float avgTemp = (pA_UD.temperature + pB_UD.temperature)/2f;
                pA_UD.temperature = pB_UD.temperature = avgTemp;
            }

            for(int i=0; i<particleSystem.getParticleCount(); i++){
                LiquidEntity pUD = (LiquidEntity) particleSystem.getParticleUserDataBuffer()[i];
                Vec2 pos = particleSystem.getParticlePositionBuffer()[i];
                if(pUD instanceof WaterEntity){
                    if(pUD.temperature > WaterEntity.toSteamTemp){
                        createSteam(pos.x, pos.y, pUD.temperature);
                        particleSystem.destroyParticle(i, false);
                    }else if(pUD.temperature < WaterEntity.toIceTemp){
                        ices.add(createIce(pos.x, pos.y, pUD.temperature));
                        particleSystem.destroyParticle(i, false);
                    }
                }else if(pUD instanceof LavaEntity){
                    if(pUD.temperature < LavaEntity.toObsidianTemp){
                        obsidians.add(createObsidian(pos.x, pos.y, pUD.temperature));
                        particleSystem.destroyParticle(i, false);
                    }
                }
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                float x = Mouse.getX() + camera.getPosition().x;
                float y = Display.getHeight() - Mouse.getY() + camera.getPosition().y;

                for(int i=0; i<obsidians.size(); i++){
                    Body body = obsidians.get(i);
                    float dist = (float) Math.sqrt(Math.pow(x - body.getPosition().x, 2) + Math.pow(y - body.getPosition().y, 2));
                    if(dist < 32f){
                        obsidians.remove(i);
                        world.destroyBody(body);
                    }
                }
            }

            if(Mouse.isButtonDown(0) && particleSystem.getParticleCount() < particleSystem.getParticleMaxCount()){
                for(int r=0; r<3; r++){
                    for(int i=0; i<=r*Math.PI*2; i++){
                        ParticleDef particleDef = new ParticleDef();
                        particleDef.position.set(Mouse.getX() + camera.getPosition().x + (float) Math.cos(i/Math.PI*2)*((float) r*4), Display.getHeight() - Mouse.getY() + camera.getPosition().y + (float) Math.sin(i/Math.PI*2)*((float) r*4));
                        particleDef.userData = new WaterEntity();
                        particleSystem.createParticle(particleDef);
                    }
                }
            }
            if(Mouse.isButtonDown(1) && particleSystem.getParticleCount() < particleSystem.getParticleMaxCount()){
                for(int r=0; r<3; r++){
                    for(int i=0; i<=r*Math.PI*2; i++){
                        ParticleDef particleDef = new ParticleDef();
                        particleDef.position.set(Mouse.getX() + camera.getPosition().x + (float) Math.cos(i/Math.PI*2)*((float) r*4), Display.getHeight() - Mouse.getY() + camera.getPosition().y + (float) Math.sin(i/Math.PI*2)*((float) r*4));
                        particleDef.userData = new LavaEntity();
                        particleSystem.createParticle(particleDef);
                    }
                }
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_1)){
                ModelEntity entity = polygons.get(polygonSelected);

                float x = Mouse.getX() + camera.getPosition().x - entity.getPosition().x;
                float y = (Display.getHeight() - Mouse.getY() + camera.getPosition().y) - entity.getPosition().y;

                Vec2[] verts = new Vec2[entity.verts.length + 1];
                for(int i=0; i<entity.verts.length; i++){
                    verts[i] = entity.verts[i];
                }
                verts[verts.length - 1] = new Vec2(x, y);
                loader.cleanRawModel(entity.getRawModel());
                world.destroyBody(entity.getBody());
                polygons.set(polygonSelected, createPolygon(entity.getPosition().x, entity.getPosition().y, verts, new Vector3f(1, 1, 1)));
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_2)){
                polygonSelected++;
                float x = Mouse.getX() + camera.getPosition().x;
                float y = (Display.getHeight() - Mouse.getY() + camera.getPosition().y);
                polygons.add(createPolygon(x, y, new Vec2[]{new Vec2(0, 0)}, new Vector3f(1, 1, 1)));
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_3) && iceRefresh){
                float x = Mouse.getX() + camera.getPosition().x;
                float y = (Display.getHeight() - Mouse.getY() + camera.getPosition().y);
                ices.add(createIce(x, y, -2f));
                iceRefresh = false;
            }


            // Geometry
            fbos.get("geometryMask").bindFrameBuffer();
            geometryRenderer.render(polygons, camera, true);
            fbos.get("geometryMask").unbindFrameBuffer();

            fbos.get("geometryColor").bindFrameBuffer();
            geometryRenderer.render(polygons, camera, false);
            fbos.get("geometryColor").unbindFrameBuffer();

            fbos.get("geometryColor").bindFrameBuffer();
            PostProcessing.doPostProcessingMultiply(fbos.get("geometryColor").getTexture(), new Vector4f(0.42f, 0.66f, 0.74f, 1f));
            fbos.get("geometryColor").unbindFrameBuffer();

            // Water
            fbos.get("waterColor").bindFrameBuffer();
            liquidRenderer.render(liquidModel, particleSystem.getParticlePositionBuffer(), particleSystem.getParticleCount(), particleSystem.getParticleUserDataBuffer(), LiquidEntity.WATER, camera);
            fbos.get("waterColor").unbindFrameBuffer();

            fbos.get("waterMask").bindFrameBuffer();
            PostProcessing.doPostProcessingConstrain(fbos.get("waterColor").getTexture());
            fbos.get("waterMask").unbindFrameBuffer();

            fbos.get("waterColor2").bindFrameBuffer();
            PostProcessing.doPostProcessingMultiply(fbos.get("waterMask").getTexture(), new Vector4f(0.17f, 0.52f, 0.75f, 1f));
            fbos.get("waterColor2").unbindFrameBuffer();

            fbos.get("waterColor").bindFrameBuffer();
            PostProcessing.doPostProcessingOutline(fbos.get("waterColor2").getTexture(), fbos.get("waterMask").getTexture(), new Vector4f(0.46f, 0.80f, 0.74f, 1f), 2, true);
            fbos.get("waterColor").unbindFrameBuffer();

            fbos.get("waterMask2").bindFrameBuffer();
            PostProcessing.doPostProcessingOutline(fbos.get("waterMask").getTexture(), fbos.get("waterMask").getTexture(), new Vector4f(1f, 1f, 1f, 1f), 2, false);
            fbos.get("waterMask2").unbindFrameBuffer();

            fbos.get("waterMask").bindFrameBuffer();
            PostProcessing.doPostProcessingMultiply(fbos.get("waterMask2").getTexture(), new Vector4f(1f, 1f, 1f, 0.8f));
            fbos.get("waterMask").unbindFrameBuffer();

            // Lava
            fbos.get("lavaColor").bindFrameBuffer();
            liquidRenderer.render(liquidModel, particleSystem.getParticlePositionBuffer(), particleSystem.getParticleCount(), particleSystem.getParticleUserDataBuffer(), LiquidEntity.LAVA, camera);
            fbos.get("lavaColor").unbindFrameBuffer();

            fbos.get("lavaMask2").bindFrameBuffer();
            PostProcessing.doPostProcessingConstrain(fbos.get("lavaColor").getTexture());
            fbos.get("lavaMask2").unbindFrameBuffer();

            fbos.get("lavaColor").bindFrameBuffer();
            PostProcessing.doPostProcessingMultiply(fbos.get("lavaMask2").getTexture(), new Vector4f(0.95f, 0.64f, 0.34f, 1f));
            fbos.get("lavaColor").unbindFrameBuffer();

            fbos.get("lavaColor2").bindFrameBuffer();
            PostProcessing.doPostProcessingOutline(fbos.get("lavaColor").getTexture(), fbos.get("lavaMask2").getTexture(), new Vector4f(0.96f, 0.84f, 0.54f, 1f), 2, false);
            fbos.get("lavaColor2").unbindFrameBuffer();

            fbos.get("lavaMask").bindFrameBuffer();
            PostProcessing.doPostProcessingOutline(fbos.get("lavaMask2").getTexture(), fbos.get("lavaMask2").getTexture(), new Vector4f(1f, 1f, 1f, 1f), 2, false);
            fbos.get("lavaMask").unbindFrameBuffer();

            fbos.get("lavaColor").bindFrameBuffer();
            PostProcessing.doPostProcessingOuterglow(fbos.get("lavaColor2").getTexture(), fbos.get("lavaMask").getTexture(), new Vector4f(0.96f, 0.84f, 0.54f, 1), 8, false);
            fbos.get("lavaColor").unbindFrameBuffer();

            fbos.get("lavaMask2").bindFrameBuffer();
            PostProcessing.doPostProcessingOuterglow(fbos.get("lavaMask").getTexture(), fbos.get("lavaMask").getTexture(), new Vector4f(1, 1, 1, 1), 8, true);
            fbos.get("lavaMask2").unbindFrameBuffer();

            fbos.get("lavaMask").bindFrameBuffer();
            PostProcessing.doPostProcessingMultiply(fbos.get("lavaMask2").getTexture(), new Vector4f(1f, 1f, 1f, 0.8f));
            fbos.get("lavaMask").unbindFrameBuffer();

            // Steam
            fbos.get("steamColor").bindFrameBuffer();
            liquidRenderer.render(liquidModel, invParticleSystem.getParticlePositionBuffer(), invParticleSystem.getParticleCount(), invParticleSystem.getParticleUserDataBuffer(), LiquidEntity.STEAM, camera);
            fbos.get("steamColor").unbindFrameBuffer();

            fbos.get("steamMask").bindFrameBuffer();
            PostProcessing.doPostProcessingConstrain(fbos.get("steamColor").getTexture());
            fbos.get("steamMask").unbindFrameBuffer();

            fbos.get("steamColor2").bindFrameBuffer();
            PostProcessing.doPostProcessingMultiply(fbos.get("steamMask").getTexture(), new Vector4f(0.8f, 0.8f, 0.8f, 1f));
            fbos.get("steamColor2").unbindFrameBuffer();

            fbos.get("steamColor").bindFrameBuffer();
            PostProcessing.doPostProcessingOutline(fbos.get("steamColor2").getTexture(), fbos.get("steamMask").getTexture(), new Vector4f(1f, 1f, 1f, 1f), 2, false);
            fbos.get("steamColor").unbindFrameBuffer();

            fbos.get("steamMask2").bindFrameBuffer();
            PostProcessing.doPostProcessingOutline(fbos.get("steamMask").getTexture(), fbos.get("steamMask").getTexture(), new Vector4f(1f, 1f, 1f, 1f), 2, false);
            fbos.get("steamMask2").unbindFrameBuffer();

            fbos.get("steamMask").bindFrameBuffer();
            PostProcessing.doPostProcessingMultiply(fbos.get("steamMask2").getTexture(), new Vector4f(1f, 1f, 1f, 0.2f));
            fbos.get("steamMask").unbindFrameBuffer();

            // Obsidian
            fbos.get("obsidianColor").bindFrameBuffer();
            liquidRenderer.render(liquidModel, obsidians, camera);
            fbos.get("obsidianColor").unbindFrameBuffer();

            fbos.get("obsidianMask2").bindFrameBuffer();
            PostProcessing.doPostProcessingConstrain(fbos.get("obsidianColor").getTexture());
            fbos.get("obsidianMask2").unbindFrameBuffer();

            fbos.get("obsidianColor2").bindFrameBuffer();
            PostProcessing.doPostProcessingMultiply(fbos.get("obsidianMask2").getTexture(), new Vector4f(0.2f, 0.2f, 0.2f, 1f));
            fbos.get("obsidianColor2").unbindFrameBuffer();

            fbos.get("obsidianColor").bindFrameBuffer();
            PostProcessing.doPostProcessingOutline(fbos.get("obsidianColor2").getTexture(), fbos.get("obsidianMask2").getTexture(), new Vector4f(0.4f, 0.4f, 0.4f, 1f), 2, true);
            fbos.get("obsidianColor").unbindFrameBuffer();

            fbos.get("obsidianMask").bindFrameBuffer();
            PostProcessing.doPostProcessingOutline(fbos.get("obsidianMask2").getTexture(), fbos.get("obsidianMask2").getTexture(), new Vector4f(1f, 1f, 1f, 1f), 2, false);
            fbos.get("obsidianMask").unbindFrameBuffer();

            // Ice
            fbos.get("iceColor").bindFrameBuffer();
            liquidRenderer.render(liquidModel, ices, camera);
            fbos.get("iceColor").unbindFrameBuffer();

            fbos.get("iceMask2").bindFrameBuffer();
            PostProcessing.doPostProcessingConstrain(fbos.get("iceColor").getTexture());
            fbos.get("iceMask2").unbindFrameBuffer();

            fbos.get("iceColor2").bindFrameBuffer();
            PostProcessing.doPostProcessingMultiply(fbos.get("iceMask2").getTexture(), new Vector4f(0.62f, 0.84f, 0.91f, 1f));
            fbos.get("iceColor2").unbindFrameBuffer();

            fbos.get("iceColor").bindFrameBuffer();
            PostProcessing.doPostProcessingOutline(fbos.get("iceColor2").getTexture(), fbos.get("iceMask2").getTexture(), new Vector4f(0.58f, 0.85f, 0.95f, 1f), 2, true);
            fbos.get("iceColor").unbindFrameBuffer();

            fbos.get("iceMask").bindFrameBuffer();
            PostProcessing.doPostProcessingOutline(fbos.get("iceMask2").getTexture(), fbos.get("iceMask2").getTexture(), new Vector4f(1f, 1f, 1f, 1f), 2, false);
            fbos.get("iceMask").unbindFrameBuffer();

            PostProcessing.doPostProcessingCombine(new int[][]{
                    { fbos.get("geometryColor").getTexture(), fbos.get("geometryMask").getTexture() },
                    { fbos.get("waterColor").getTexture(), fbos.get("waterMask").getTexture() },
                    { fbos.get("lavaColor").getTexture(), fbos.get("lavaMask").getTexture() },
                    { fbos.get("steamColor").getTexture(), fbos.get("steamMask").getTexture() },
                    { fbos.get("obsidianColor").getTexture(), fbos.get("obsidianMask").getTexture() },
                    { fbos.get("iceColor").getTexture(), fbos.get("iceMask").getTexture() }
            });

            DisplayManager.showFPS();
            DisplayManager.showString(
                    "Particles: " + (particleSystem.getParticleCount() + invParticleSystem.getParticleCount())
                            + "/" + (particleSystem.getParticleMaxCount() + invParticleSystem.getParticleMaxCount())
                    );
            DisplayManager.updateDisplay();
        }

        cleanUp();
    }

    private static int createSteam(float x, float y, float temp){
        ParticleDef particleDef = new ParticleDef();
        particleDef.position.set(x, y);
        SteamEntity entity = new SteamEntity();
        entity.temperature = temp;
        particleDef.userData = entity;
        return invParticleSystem.createParticle(particleDef);
    }

    private static int createWater(float x, float y, float temp){
        ParticleDef particleDef = new ParticleDef();
        particleDef.position.set(x, y);
        WaterEntity entity = new WaterEntity();
        entity.temperature = temp;
        particleDef.userData = entity;
        return particleSystem.createParticle(particleDef);
    }

    private static int createLava(float x, float y, float temp){
        ParticleDef particleDef = new ParticleDef();
        particleDef.position.set(x, y);
        LavaEntity entity = new LavaEntity();
        entity.temperature = temp;
        particleDef.userData = entity;
        return particleSystem.createParticle(particleDef);
    }

    private static Body createIce(float x, float y, float temperature){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyType.STATIC;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(PARTICLE_RADIUS*3);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1;
        fixtureDef.shape = circleShape;

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        IceEntity entity = new IceEntity();
        entity.temperature = temperature;
        body.setUserData(entity);

        return body;
    }

    private static Body createObsidian(float x, float y, float temperature){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyType.STATIC;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(PARTICLE_RADIUS*3);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1;
        fixtureDef.shape = circleShape;

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        ObsidianEntity entity = new ObsidianEntity();
        entity.temperature = temperature;
        body.setUserData(entity);

        return body;
    }

    private static ModelEntity createPolygon(float x, float y, Vec2[] verts, Vector3f color){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyType.STATIC;

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(verts, verts.length);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1;
        fixtureDef.shape = polygonShape;

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        RawModel polygonModel = createPolygonModel(verts, color);
        ModelEntity polygonEntity = new ModelEntity();
        polygonEntity.setRawModel(polygonModel);
        polygonEntity.setPosition(new Vector2f(x, y));
        polygonEntity.setBody(body);
        polygonEntity.verts = verts;

        return polygonEntity;
    }

    private static RawModel createPolygonModel(Vec2[] verts, Vector3f color){
        float[] vertices = new float[verts.length * 2];
        float[] colors = new float[verts.length * 3];
        int[] indices = new int[verts.length];
        for(int i=0; i<verts.length; i++){
            vertices[i*2] = verts[i].x;
            vertices[i*2 + 1] = -verts[i].y;
            colors[i*3] = color.x;
            colors[i*3 + 1] = color.y;
            colors[i*3 + 2] = color.z;
            indices[i] = i;
        }

        RawModel polygonModel = loader.loadToVao(vertices, colors, indices);
        return polygonModel;
    }

    public static void main(String[] args){
        initialize();
    }
}
