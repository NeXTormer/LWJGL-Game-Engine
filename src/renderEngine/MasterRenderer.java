package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import skybox.SkyboxShader;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Felix on 28.05.2016.
 */
public class MasterRenderer {

    private StaticShader shader = new StaticShader();
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private SkyboxRenderer skyboxRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    public static final float FOV = 70;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 2000;

    public static final float RED = 0.5f;
    public static final float GREEN = 0.5f;
    public static final float BLUE = 0.5f;

    private Matrix4f projectionMatrix;

    private NormalMappingRenderer normalMapRenderer;


    private Light sun;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
    private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();

    private List<Terrain> terrains = new ArrayList<>();

    public MasterRenderer(Loader loader, Light sun) {
        enableCulling();
        createProjectionMatrix();
        skyboxRenderer = new SkyboxRenderer(loader, sun, projectionMatrix);
        entityRenderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        normalMapRenderer = new NormalMappingRenderer(projectionMatrix);

    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE); //dont render the insides of a model (faceing away from the camera)
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void renderScene(List<Entity> entities, List<Entity> normalMapEntities, List<Terrain> terrains, List<Light> lights, Camera camera, Vector4f clipPlane) {
        for(Terrain terrain : terrains) {
            processTerrain(terrain);
        }
        for(Entity entity : entities) {
            processEntity(entity);
        }
        for(Entity entity : normalMapEntities) {
            processNormalMapEntity(entity);
        }
        render(lights, camera, clipPlane);
    }

    public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
        if(Keyboard.isKeyDown(Keyboard.KEY_L)) GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        else {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }
        prepare();
        shader.start();
        shader.loadClipPlane(clipPlane);
        shader.loadSkyColor(RED, GREEN, BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        shader.stop();
        normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera);
        terrainShader.start();
        terrainShader.loadClippingPlane(clipPlane);
        terrainShader.loadSkyColor(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        skyboxRenderer.render(camera, RED, GREEN, BLUE);
        terrains.clear();
        entities.clear();
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void prepare() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClearColor(RED, GREEN, BLUE, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if(batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processNormalMapEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = normalMapEntities.get(entityModel);
        if(batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            normalMapEntities.put(entityModel, newBatch);
        }
    }


    public void cleanUp() {
        terrainShader.cleanUp();
        shader.cleanUp();
        normalMapRenderer.cleanUp();
    }
}
