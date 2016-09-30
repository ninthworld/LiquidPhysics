package org.ninthworld.liquidphysics.fluid;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.geom.Circle;
import org.ninthworld.liquidphysics.helper.Vec2Helper;

import java.util.*;

/**
 * Created by NinthWorld on 9/30/2016.
 */
public class FluidEngine {

    public static final boolean DRAW_CELLS = false;
    public static final int MAX_PARTICLES = 20000;
    public static final int MAX_NEIGHBORS = 75;
    public static final float CELL_SIZE = 0.6f;
    public static final float RADIUS = 0.9f;
    public static final float VISCOSITY = 0.004f;
    public static final float IDEAL_RADIUS = 50f;
    public static final float IDEAL_RADIUS_SQ = IDEAL_RADIUS * IDEAL_RADIUS;
    public static final float MULTIPLIER = IDEAL_RADIUS / RADIUS;
    public static final float DT = 1f / 60f;
    private int _numActiveParticles = 0;
    private Particle[] _liquid;
    private List<Integer> _activeParticles;
    private Vec2[] _delta;
    private Vec2[] _scaledPositions;
    private Vec2[] _scaledVelocities;
    private Map<Integer, Map<Integer, List<Integer>>> _grid;
    private World _world;
    private Vec2 _mouse;
    private Random _random;
    private AABB _simulationAABB;
    private Vec2 _gravity = new Vec2(0, 9.8f / 3000f);
    private Vec2 _halfScreen;

    private int getGridX(float x) { return (int)Math.floor(x / CELL_SIZE); }
    private int getGridY(float y) { return (int)Math.floor(y / CELL_SIZE); }

    public FluidEngine(World world){
        _world = world;
        _activeParticles = new ArrayList<>(MAX_PARTICLES);
        _liquid = new Particle[MAX_PARTICLES];
        for (int i = 0; i < MAX_PARTICLES; i++)
        {
            _liquid[i] = new Particle(new Vec2(), new Vec2(), false);
            _liquid[i].index = i;
        }

        _delta = new Vec2[MAX_PARTICLES];
        _scaledPositions = new Vec2[MAX_PARTICLES];
        _scaledVelocities = new Vec2[MAX_PARTICLES];

        _grid = new HashMap<>();

        _random = new Random();

        _halfScreen = new Vec2(Display.getWidth()/2f, Display.getHeight()/2f);
        _simulationAABB.lowerBound.x = -(_halfScreen.x + 100f);
        _simulationAABB.lowerBound.y = -(_halfScreen.y + 100f);
        _simulationAABB.upperBound.x = _halfScreen.x + 100f;
        _simulationAABB.upperBound.y = _halfScreen.y + 100f;
    }

    private void findNeighbors(Particle particle){
        particle.neighborCount = 0;
        Map<Integer, List<Integer>> gridX;
        List<Integer> gridY;

        for (int nx = -1; nx < 2; nx++)
        {
            for (int ny = -1; ny < 2; ny++)
            {
                int x = particle.ci + nx;
                int y = particle.cj + ny;
                if (_grid.containsKey(x)){
                    gridX = _grid.get(x);
                    if(gridX.containsKey(y)){
                        gridY = gridX.get(y);
                        for (int a = 0; a < gridY.size(); a++){
                            if (gridY.get(a) != particle.index){
                                particle.neighbors[particle.neighborCount] = gridY.get(a);
                                particle.neighborCount++;

                                if (particle.neighborCount >= MAX_NEIGHBORS)
                                    return;
                            }
                        }
                    }
                }
            }
        }
    }

    // prepareSimulation
    private void prepareSimulation(int index){
        Particle particle = _liquid[index];

        // Find neighbors
        findNeighbors(particle);

        // Scale positions and velocities
        _scaledPositions[index] = new Vec2(particle.position.x * MULTIPLIER, particle.position.y * MULTIPLIER);
        _scaledVelocities[index] = new Vec2(particle.velocity.x * MULTIPLIER, particle.velocity.y * MULTIPLIER);

        // Reset collision information
        particle.numFixturesToTest = 0;

        // Reset deltas
        _delta[index] = new Vec2();

        // Reset pressures
        _liquid[index].p = 0;
        _liquid[index].pnear = 0;

        // Store old position
        particle.oldPosition = particle.position;
    }

    // prepareCollisions
    private void prepareCollisions(){
        // Query the world using the screen's AABB
        _world.queryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                Map<Integer, List<Integer>> collisionGridX;
                List<Integer> collisionGridY;

                AABB aabb = new AABB();
                Transform transform = fixture.m_body.getTransform();
                fixture.m_shape.computeAABB(aabb, transform);

                // Get the top left corner of the AABB in grid coordinates
                int Ax = getGridX(aabb.lowerBound.x);
                int Ay = getGridY(aabb.lowerBound.y);

                // Get the bottom right corner of the AABB in grid coordinates
                int Bx = getGridX(aabb.upperBound.x) + 1;
                int By = getGridY(aabb.upperBound.y) + 1;

                // Loop through all the grid cells in the fixture's AABB
                for (int i = Ax; i < Bx; i++){
                    for (int j = Ay; j < By; j++){
                        if(_grid.containsKey(i)){
                            collisionGridX = _grid.get(i);
                            if(collisionGridX.containsKey(j)){
                                collisionGridY = collisionGridX.get(j);
                                // Tell any particles we find that this fixture should be tested
                                for (int k = 0; k < collisionGridY.size(); k++)
                                {
                                    Particle particle = _liquid[collisionGridY.get(k)];
                                    if (particle.numFixturesToTest < Particle.MAX_FIXTURES_TO_TEST){
                                        particle.fixturesToTest[particle.numFixturesToTest] = fixture;
                                        particle.numFixturesToTest++;
                                    }
                                }
                            }
                        }
                    }
                }

                return true;
            }
        }, _simulationAABB);
    }

    // calculatePressure
    private void calculatePressure(int index){
        Particle particle = _liquid[index];

        for (int a = 0; a < particle.neighborCount; a++){
            Vec2 relativePosition = new Vec2(_scaledPositions[particle.neighbors[a]].x - _scaledPositions[index].x, _scaledPositions[particle.neighbors[a]].y - _scaledPositions[index].y);
            float distanceSq = relativePosition.lengthSquared();

            //within idealRad check
            if (distanceSq < IDEAL_RADIUS_SQ){
                particle.distances[a] = (float) Math.sqrt(distanceSq);
                //if (particle.distances[a] < Settings.EPSILON) particle.distances[a] = IDEAL_RADIUS - .01f;
                float oneminusq = 1.0f - (particle.distances[a] / IDEAL_RADIUS);
                particle.p = (particle.p + oneminusq * oneminusq);
                particle.pnear = (particle.pnear + oneminusq * oneminusq * oneminusq);
            }else{
                particle.distances[a] = Float.MAX_VALUE;
            }
        }
    }

    // calculateForce
    private Vec2[] calculateForce(int index, Vec2[] accumulatedDelta)
    {
        Particle particle = _liquid[index];

        // Calculate forces
        float pressure = (particle.p - 5f) / 2.0f; //normal pressure term
        float presnear = particle.pnear / 2.0f; //near particles term
        Vec2 change = new Vec2();
        for (int a = 0; a < particle.neighborCount; a++){
            Vec2 relativePosition = new Vec2(_scaledPositions[particle.neighbors[a]].x - _scaledPositions[index].x, _scaledPositions[particle.neighbors[a]].y - _scaledPositions[index].y);

            if (particle.distances[a] < IDEAL_RADIUS)
            {
                float q = particle.distances[a] / IDEAL_RADIUS;
                float oneminusq = 1.0f - q;
                float factor = oneminusq * (pressure + presnear * oneminusq) / (2.0F * particle.distances[a]);
                Vec2 d = new Vec2(relativePosition.x * factor, relativePosition.y * factor);
                Vec2 relativeVelocity = new Vec2(_scaledVelocities[particle.neighbors[a]].x - _scaledVelocities[index].x, _scaledVelocities[particle.neighbors[a]].y - _scaledVelocities[index].y);

                factor = VISCOSITY * oneminusq * DT;
                d = new Vec2(d.x - relativeVelocity.x * factor, d.y - relativeVelocity.y * factor);
                accumulatedDelta[particle.neighbors[a]] = new Vec2(accumulatedDelta[particle.neighbors[a]].x + d.x, accumulatedDelta[particle.neighbors[a]].y + d.y);
                change = new Vec2(change.x - d.x, change.y - d.y);
            }
        }
        accumulatedDelta[index] = new Vec2(accumulatedDelta[index].x + change.x, accumulatedDelta[index].y + change.y);

        // Apply gravitational force
        particle.velocity = new Vec2(particle.velocity.x + _gravity.x, particle.velocity.y + _gravity.y);

        return accumulatedDelta;
    }

    // resolveCollisions
    private void resolveCollision(int index){
        Particle particle = _liquid[index];

        // Test all fixtures stored in this particle
        for (int i = 0; i < particle.numFixturesToTest; i++){
            Fixture fixture = particle.fixturesToTest[i];

            // Determine where the particle will be after being moved
            Vec2 newPosition = Vec2Helper.add(Vec2Helper.add(particle.position, particle.velocity), _delta[index]);

            // Test to see if the new particle position is inside the fixture
            if (fixture.testPoint(newPosition)){
                Body body = fixture.m_body;
                Vec2 closestPoint = new Vec2();
                Vec2 normal = new Vec2();

                // Resolve collisions differently based on what type of shape they are
                if (fixture.m_shape.m_type == ShapeType.POLYGON)
                {
                    PolygonShape shape = (PolygonShape) fixture.m_shape;
                    Transform collisionXF = body.getTransform();

                    for (int v = 0; v < shape.m_vertices.length; v++){
                        // Transform the shape's vertices from local space to world space
                        particle.collisionVertices[v] = MathUtils.multiply(collisionXF, shape.m_vertices[v]);

                        // Transform the shape's normals using the rotation matrix
                        particle.collisionNormals[v] = MathUtils.multiply(collisionXF.R, shape.m_vertices[v]);
                    }

                    // Find closest edge
                    float shortestDistance = 9999999f;
                    for (int v = 0; v < shape.m_vertices.length; v++){
                        // Project the vertex position relative to the particle position onto the edge's normal to find the distance
                        float distance = Vec2.dot(particle.collisionNormals[v], Vec2Helper.sub(particle.collisionVertices[v], particle.position));
                        if (distance < shortestDistance){
                            // Store the shortest distance
                            shortestDistance = distance;

                            // Push the particle out of the shape in the direction of the closest edge's normal
                            closestPoint = Vec2Helper.add(Vec2Helper.scalar(distance, particle.collisionNormals[v]), particle.position);
                            normal = particle.collisionNormals[v];
                        }
                    }
                    particle.position = Vec2Helper.add(Vec2Helper.scalar(0.05f, normal), closestPoint);
                }else if (fixture.m_shape.m_type == ShapeType.CIRCLE){
                    // Push the particle out of the circle by normalizing the circle's center relative to the particle position,
                    // and pushing the particle out in the direction of the normal
                    CircleShape shape = (CircleShape) fixture.m_shape;
                    Vec2 center = Vec2Helper.add(shape.m_p, body.getPosition());
                    Vec2 difference = Vec2Helper.sub(particle.position, center);
                    normal = difference;
                    normal.normalize();
                    closestPoint = Vec2Helper.add(center, Vec2Helper.scalar((shape.m_radius / difference.length()), difference));
                    particle.position = Vec2Helper.add(closestPoint, Vec2Helper.scalar(0.05f, normal));
                }

                // Update velocity
                particle.velocity = Vec2Helper.scalar(0.85f, Vec2Helper.sub(particle.velocity, Vec2Helper.scalar(1.2f * Vec2.dot(particle.velocity, normal), normal)));

                // Reset delta
                _delta[index] = new Vec2();
            }
        }
    }

    // moveParticle
    private void moveParticle(int index){
        Particle particle = _liquid[index];
        int x = getGridX(particle.position.x);
        int y = getGridY(particle.position.y);

        // Update velocity
        particle.velocity = Vec2Helper.add(particle.velocity, _delta[index]);

        // Update position
        particle.position = Vec2Helper.add(particle.position, _delta[index]);
        particle.position = Vec2Helper.add(particle.position, particle.velocity);

        // Update particle cell
        if (particle.ci == x && particle.cj == y) {
            return;
        }else{
            _grid.get(particle.ci).get(particle.cj).remove(index);

            if (_grid.get(particle.ci).get(particle.cj).size() == 0){
                _grid.get(particle.ci).remove(particle.cj);

                if (_grid.get(particle.ci).size() == 0){
                    _grid.remove(particle.ci);
                }
            }

            if (!_grid.containsKey(x)) {
                _grid.put(x, new HashMap<>());
            }
            if (!_grid.get(x).containsKey(y)) {
                _grid.get(x).put(y, new ArrayList<>(20));
            }

            _grid.get(x).get(y).add(index);
            particle.ci = x;
            particle.cj = y;
        }
    }

    public void createParticle(int numParticlesToSpawn){
        List<Particle> inactiveParticles = new ArrayList<>();
        for(Particle particle : _liquid){
            if(!particle.alive && inactiveParticles.size() <= numParticlesToSpawn){
                inactiveParticles.add(particle);
            }
        }

        for(Particle particle : inactiveParticles){
            if (_numActiveParticles < MAX_PARTICLES){
                Vec2 jitter = new Vec2((float)(_random.nextDouble() * 2 - 1), (float)(_random.nextDouble()) - 0.5f);

                particle.position = Vec2Helper.add(_mouse, jitter);
                particle.velocity = new Vec2();
                particle.alive = true;
                particle.ci = getGridX(particle.position.x);
                particle.cj = getGridY(particle.position.y);

                // Create grid cell if necessary
                if (!_grid.containsKey(particle.ci))
                    _grid[particle.ci] = new Dictionary<int, List<int>>();
                if (!_grid[particle.ci].ContainsKey(particle.cj))
                    _grid[particle.ci][particle.cj] = new List<int>();
                _grid[particle.ci][particle.cj].Add(particle.index);

                _activeParticles.Add(particle.index);
                _numActiveParticles++;
            }
        }
    }

    public void update()
    {
        MouseState mouseState = Mouse.GetState();

        _halfScreen = new Vector2(
                _spriteBatch.GraphicsDevice.Viewport.Width,
                _spriteBatch.GraphicsDevice.Viewport.Height) / 2f;
        _mouse = (new Vector2(mouseState.X, mouseState.Y) - _halfScreen) / Game1.SCALE;

        if (mouseState.LeftButton == ButtonState.Pressed)
            createParticle();

        // Prepare simulation
        Parallel.For(0, _numActiveParticles, i => { prepareSimulation(_activeParticles[i]); });

        // Prepare collisions
        prepareCollisions();

        // Calculate pressures
        Parallel.For(0, _numActiveParticles, i => { calculatePressure(_activeParticles[i]); });

        // Calculate forces
        Parallel.For(
                0,
                _numActiveParticles,
                () => new Vector2[MAX_PARTICLES],
                (i, state, accumulatedDelta) => calculateForce(_activeParticles[i], accumulatedDelta),
            (accumulatedDelta) =>
        {
            lock (_calculateForcesLock)
            {
                for (int i = _numActiveParticles - 1; i >= 0; i--)
                {
                    int index = _activeParticles[i];
                    _delta[index] += accumulatedDelta[index] / MULTIPLIER;
                }
            }
        }
            );

        // Resolve collisions
        Parallel.For(0, _numActiveParticles, i => resolveCollision(_activeParticles[i]));

        // Update particle cells
        for (int i = 0; i < _numActiveParticles; i++)
            moveParticle(_activeParticles[i]);
    }
}
