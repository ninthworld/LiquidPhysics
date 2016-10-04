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
import org.ninthworld.liquidphysics.entities.CameraEntity;
import org.ninthworld.liquidphysics.entities.ModelEntity;
import org.ninthworld.liquidphysics.fbo.Fbo;
import org.ninthworld.liquidphysics.fbo.PostProcessing;
import org.ninthworld.liquidphysics.helper.MatrixHelper;
import org.ninthworld.liquidphysics.helper.Vec2Helper;
import org.ninthworld.liquidphysics.model.Loader;
import org.ninthworld.liquidphysics.model.RawModel;
import org.ninthworld.liquidphysics.renderer.GeometryRenderer;
import org.ninthworld.liquidphysics.renderer.LiquidRenderer;
import org.ninthworld.liquidphysics.renderer.ShipRenderer;

import java.security.Key;
import java.util.*;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class Main {

    private static CameraEntity camera;

    private static Loader loader;
    private static LiquidRenderer liquidRenderer;
    private static GeometryRenderer geometryRenderer;
    private static ShipRenderer shipRenderer;
    private static Map<String, Fbo> fbos;

    public static final float PARTICLE_RADIUS = 2;
    private static final int WORLD_WIDTH = 1280;
    private static final int WORLD_HEIGHT = 640;

    private static World world;

    private static RawModel liquidModel;
    private static List<ModelEntity> polygons;

    private static List<Body> obsidians;

    private static final ParticleColor WATER_COLOR = new ParticleColor(Color3f.BLUE);
    private static final ParticleColor LAVA_COLOR = new ParticleColor(Color3f.RED);
    private static final ParticleColor STEAM_COLOR = new ParticleColor(Color3f.WHITE);

    private static ParticleSystem particleSystem;
    private static ParticleSystem invParticleSystem;

    private static ModelEntity shipModel;
    private static Body shipBody;

    private static void initialize(){
        loader = new Loader();
        DisplayManager.createDisplay();
        Matrix4f projectionMatrix = MatrixHelper.createProjectionMatrix();
        liquidRenderer = new LiquidRenderer(loader, projectionMatrix);
        geometryRenderer = new GeometryRenderer(projectionMatrix);
        shipRenderer = new ShipRenderer(projectionMatrix);
        PostProcessing.init(loader);

        camera = new CameraEntity();

        liquidModel = createPolygonModel(new Vec2[]{new Vec2(-16, -16), new Vec2(16, -16), new Vec2(16, 16), new Vec2(-16, 16)}, new Vector3f(1, 1, 1));

        fbos = new HashMap<>();
        fbos.put("geometryColor", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("geometryMask", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("waterParticles", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("waterParticlesMask", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("lavaParticles", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("lavaParticlesMask", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("obsidianColor", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("obsidianMask", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("steamParticles", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("steamParticlesMask", new Fbo(Display.getWidth(), Display.getHeight()));
        fbos.put("ship", new Fbo(Display.getWidth(), Display.getHeight()));

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

        polygons = new ArrayList<>();
        // Bottom
        polygons.add(createPolygon(WORLD_WIDTH/2, WORLD_HEIGHT, new Vec2[]{new Vec2(-WORLD_WIDTH/2, -8), new Vec2(WORLD_WIDTH/2, -8), new Vec2(WORLD_WIDTH/2, 8), new Vec2(-WORLD_WIDTH/2, 8)}, new Vector3f(0.8f, 0.8f, 0.8f)));
        // Top
        polygons.add(createPolygon(WORLD_WIDTH/2, 0, new Vec2[]{new Vec2(-WORLD_WIDTH/2, -8), new Vec2(WORLD_WIDTH/2, -8), new Vec2(WORLD_WIDTH/2, 8), new Vec2(-WORLD_WIDTH/2, 8)}, new Vector3f(0.8f, 0.8f, 0.8f)));
        // Left
        polygons.add(createPolygon(0, WORLD_HEIGHT/2, new Vec2[]{new Vec2(-8, -WORLD_HEIGHT/2), new Vec2(8, -WORLD_HEIGHT/2), new Vec2(8, WORLD_HEIGHT/2), new Vec2(-8, WORLD_HEIGHT/2)}, new Vector3f(0.8f, 0.8f, 0.8f)));
        // Right
        polygons.add(createPolygon(WORLD_WIDTH, WORLD_HEIGHT/2, new Vec2[]{new Vec2(-8, -WORLD_HEIGHT/2), new Vec2(8, -WORLD_HEIGHT/2), new Vec2(8, WORLD_HEIGHT/2), new Vec2(-8, WORLD_HEIGHT/2)}, new Vector3f(0.8f, 0.8f, 0.8f)));

        polygons.add(createPolygon(WORLD_WIDTH/4, WORLD_HEIGHT/4, new Vec2[]{new Vec2(-100, -128), new Vec2(-128, -100), new Vec2(100, 128), new Vec2(128, 100)}, new Vector3f(0.8f, 0.8f, 0.8f)));

        polygons.add(createPolygon(WORLD_WIDTH/4 + 200, WORLD_HEIGHT/4, new Vec2[]{new Vec2(100, -128), new Vec2(-128, 100), new Vec2(-100, 128), new Vec2(128, -100)}, new Vector3f(0.8f, 0.8f, 0.8f)));

//        Vec2[] vecs = new Vec2[]{
//                new Vec2(0, 0.7f),
//                new Vec2(0.4f, 0.6f),
//                new Vec2(0.9f, 0.7f),
//                new Vec2(1f, 0.4f),
//                new Vec2(1.1f, 0f),
//                new Vec2(1f, -0.7f),
//                new Vec2(0.8f, -0.2f),
//                new Vec2(0.5f, 0f),
//                new Vec2(0.4f, -0.3f),
//                new Vec2(0.5f, -0.7f),
//                new Vec2(0.4f, -0.9f),
//                new Vec2(0.5f, -1.1f),
//                new Vec2(0.2f, -1.5f),
//                new Vec2(0.3f, -1.7f),
//                new Vec2(0.1f, -1.6f),
//                new Vec2(0f, -1.8f),
//                new Vec2(-0.1f, -1.6f),
//                new Vec2(-0.3f, -1.7f),
//                new Vec2(-0.2f, -1.5f),
//                new Vec2(-0.5f, -1.1f),
//                new Vec2(-0.4f, -0.9f),
//                new Vec2(-0.5f, -0.7f),
//                new Vec2(-0.4f, -0.3f),
//                new Vec2(-0.5f, 0f),
//                new Vec2(-0.8f, -0.2f),
//                new Vec2(-1f, -0.7f),
//                new Vec2(-1.1f, 0f),
//                new Vec2(-1f, 0.4f),
//                new Vec2(-0.9f, 0.7f),
//                new Vec2(-0.4f, 0.6f)
//        };
//        for(int i=0; i<vecs.length; i++){
//            vecs[i] = new Vec2(vecs[i].x * 20f, vecs[i].y * 20f);
//        }

        Vec2[] vecs = new Vec2[]{
                new Vec2(-4f, -16f),
                new Vec2(-16f, 16f),
                new Vec2(0f, 8f),
                new Vec2(16f, 16f),
                new Vec2(4f, -16f)
        };

        RawModel model = createPolygonModel(vecs, new Vector3f(1, 1, 1));

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(100, 100);
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.setGravityScale(0);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vecs, vecs.length);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        fixtureDef.shape = polygonShape;

        shipBody = world.createBody(bodyDef);
        shipBody.createFixture(fixtureDef);
        shipBody.setAngularDamping(2f);
        shipBody.setLinearDamping(2f);

        shipModel = new ModelEntity();
        shipModel.setRawModel(model);
        shipModel.setPosition(new Vector2f(100, 100));
        shipModel.setBody(shipBody);

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

    private static TimeStep timeStep = new TimeStep();
    private static void update(){
        timeStep.dt = 1/60f;
        timeStep.inv_dt = 60f;
        timeStep.positionIterations = 8*4;
        timeStep.velocityIterations = 3*4;
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            world.step(timeStep.dt, timeStep.positionIterations, timeStep.velocityIterations);
            particleSystem.solve(timeStep);
            invParticleSystem.solve(timeStep);

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
                ParticleColor color = invParticleSystem.getParticleColorBuffer()[i];
                if(color.a > 0x7e){
                    invParticleSystem.destroyParticle(i, false);
                }else{
                    color.a++;
                }
            }

            for(int i=0; i<particleSystem.m_contactCount; i++){
                int particleA = particleSystem.m_contactBuffer[i].indexA;
                int particleB = particleSystem.m_contactBuffer[i].indexB;

                ParticleColor particleColorA = particleSystem.getParticleColorBuffer()[particleA];
                ParticleColor particleColorB = particleSystem.getParticleColorBuffer()[particleB];

                if(particleColorA.r == WATER_COLOR.r && particleColorA.g == WATER_COLOR.g && particleColorA.b == WATER_COLOR.b && particleColorA.a == WATER_COLOR.a &&
                        particleColorB.r == LAVA_COLOR.r && particleColorB.g == LAVA_COLOR.g && particleColorB.b == LAVA_COLOR.b && particleColorB.a == LAVA_COLOR.a) {

                    obsidians.add(createObsidian(particleSystem.getParticlePositionBuffer()[particleB].x, particleSystem.getParticlePositionBuffer()[particleB].y));

                    ParticleDef particleDef = new ParticleDef();
                    particleDef.position.set(particleSystem.getParticlePositionBuffer()[particleA].x, particleSystem.getParticlePositionBuffer()[particleA].y);
                    particleDef.color = STEAM_COLOR;
                    particleDef.color.a = 0x0;
                    invParticleSystem.createParticle(particleDef);

                    particleSystem.destroyParticle(particleA, false);
                    particleSystem.destroyParticle(particleB, false);
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
                        particleDef.color = WATER_COLOR;
                        particleSystem.createParticle(particleDef);
                    }
                }
            }
            if(Mouse.isButtonDown(1) && particleSystem.getParticleCount() < particleSystem.getParticleMaxCount()){
                for(int r=0; r<3; r++){
                    for(int i=0; i<=r*Math.PI*2; i++){
                        ParticleDef particleDef = new ParticleDef();
                        particleDef.position.set(Mouse.getX() + camera.getPosition().x + (float) Math.cos(i/Math.PI*2)*((float) r*4), Display.getHeight() - Mouse.getY() + camera.getPosition().y + (float) Math.sin(i/Math.PI*2)*((float) r*4));
                        particleDef.color = LAVA_COLOR;
                        particleSystem.createParticle(particleDef);
                    }
                }
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_A)){
                shipBody.setAngularVelocity(shipBody.getAngularVelocity() + 0.2f);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_D)){
                shipBody.setAngularVelocity(shipBody.getAngularVelocity() - 0.2f);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_W)){
                float speed = 16f;
                //shipBody.setLinearVelocity(Vec2Helper.add(shipBody.getLinearVelocity(), new Vec2((float) -Math.sin(shipBody.getAngle())*speed, (float) -Math.cos(shipBody.getAngle())*speed)));
                shipBody.setLinearVelocity(new Vec2(100000000, 100000000));
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_S)){
                float speed = 16f;
                shipBody.setLinearVelocity(Vec2Helper.add(shipBody.getLinearVelocity(), new Vec2((float) Math.sin(shipBody.getAngle())*speed, (float) Math.cos(shipBody.getAngle())*speed)));
            }


            shipModel.getPosition().x = shipModel.getBody().getPosition().x;
            shipModel.getPosition().y = shipModel.getBody().getPosition().y;
//            shipModel.getScale().x = shipModel.getBody().getTransform().p.x;
//            shipModel.getScale().y = shipModel.getBody().getTransform().p.y;
            shipModel.setRotation(shipModel.getBody().getTransform().q.getAngle());

            fbos.get("ship").bindFrameBuffer();
            shipRenderer.render(shipModel, camera, false);
            fbos.get("ship").unbindFrameBuffer();

            fbos.get("geometryMask").bindFrameBuffer();
            geometryRenderer.render(polygons, camera, true);
            fbos.get("geometryMask").unbindFrameBuffer();

            fbos.get("geometryColor").bindFrameBuffer();
            geometryRenderer.render(polygons, camera, false);
            fbos.get("geometryColor").unbindFrameBuffer();

            fbos.get("waterParticles").bindFrameBuffer();
            liquidRenderer.render(liquidModel, particleSystem.getParticlePositionBuffer(), particleSystem.getParticleCount(), particleSystem.getParticleColorBuffer(), WATER_COLOR, camera);
            fbos.get("waterParticles").unbindFrameBuffer();

            fbos.get("waterParticlesMask").bindFrameBuffer();
            PostProcessing.doPostProcessingConstrain(fbos.get("waterParticles").getColorTexture());
            fbos.get("waterParticlesMask").unbindFrameBuffer();

            fbos.get("lavaParticles").bindFrameBuffer();
            liquidRenderer.render(liquidModel, particleSystem.getParticlePositionBuffer(), particleSystem.getParticleCount(), particleSystem.getParticleColorBuffer(), LAVA_COLOR, camera);
            fbos.get("lavaParticles").unbindFrameBuffer();

            fbos.get("lavaParticlesMask").bindFrameBuffer();
            PostProcessing.doPostProcessingConstrain(fbos.get("lavaParticles").getColorTexture());
            fbos.get("lavaParticlesMask").unbindFrameBuffer();

            fbos.get("steamParticles").bindFrameBuffer();
            liquidRenderer.render(liquidModel, invParticleSystem.getParticlePositionBuffer(), invParticleSystem.getParticleCount(), invParticleSystem.getParticleColorBuffer(), STEAM_COLOR, camera);
            fbos.get("steamParticles").unbindFrameBuffer();

            fbos.get("steamParticlesMask").bindFrameBuffer();
            PostProcessing.doPostProcessingConstrain(fbos.get("steamParticles").getColorTexture());
            fbos.get("steamParticlesMask").unbindFrameBuffer();

            fbos.get("obsidianColor").bindFrameBuffer();
            liquidRenderer.render(liquidModel, obsidians, camera);
            fbos.get("obsidianColor").unbindFrameBuffer();

            fbos.get("obsidianMask").bindFrameBuffer();
            PostProcessing.doPostProcessingConstrain(fbos.get("obsidianColor").getColorTexture());
            fbos.get("obsidianMask").unbindFrameBuffer();

            PostProcessing.doPostProcessingMain(
                    new int[]{
                            fbos.get("geometryColor").getColorTexture(),
                            fbos.get("waterParticlesMask").getColorTexture(),
                            fbos.get("lavaParticlesMask").getColorTexture(),
                            fbos.get("steamParticlesMask").getColorTexture(),
                            fbos.get("obsidianMask").getColorTexture(),
                            fbos.get("ship").getColorTexture()
                    }, new int[]{
                            fbos.get("geometryMask").getColorTexture(),
                            fbos.get("waterParticlesMask").getColorTexture(),
                            fbos.get("lavaParticlesMask").getColorTexture(),
                            fbos.get("steamParticlesMask").getColorTexture(),
                            fbos.get("obsidianMask").getColorTexture(),
                            fbos.get("ship").getColorTexture()
                    }
            );

            DisplayManager.showFPS();
            DisplayManager.showString(
                    "Particles: " + (particleSystem.getParticleCount() + invParticleSystem.getParticleCount())
                            + "/" + (particleSystem.getParticleMaxCount() + invParticleSystem.getParticleMaxCount())
                    );
            DisplayManager.updateDisplay();
        }

        cleanUp();
    }

    private static Body createObsidian(float x, float y){
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
