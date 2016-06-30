package game.test;

import engine.entities.Camera;
import engine.entities.Entity;
import engine.entities.Light;
import engine.entities.Player;
import engine.guis.GUITexture;
import engine.guis.GuiRenderer;
import engine.models.ModelTexture;
import engine.models.TexturedModel;
import engine.objConverter.ModelData;
import engine.objConverter.OBJFileLoader;
import engine.toolbox.FrameBufferObject;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import engine.renderEngine.*;
import engine.terrains.Terrain;
import engine.toolbox.MousePicker;
import engine.toolbox.Tools;
import engine.water.WaterFrameBuffers;
import engine.water.WaterRenderer;
import engine.water.WaterShader;
import engine.water.WaterTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {



    /*
    PETER RENDL
    PETER RENDL
    PETER RENDL
    PETER RENDL
    PETER RENDL
    PETER RENDL
    PETER RENDL
    PETER RENDL
    PETER RENDL
    PETER RENDL
    PETER RENDL
    PETER RENDL
     */

    Loader loader;
    GuiRenderer guiRenderer;
    MasterRenderer renderer;
    Player player;
    Camera camera;
    Light sun;
    MousePicker picker;
    Tools tools;
    Random random;
    Terrain terrain;

    WaterShader waterShader;
    WaterRenderer waterRenderer;

    List<Entity> entities;
    List<Light> lights;
    List<Terrain> terrains;
    List<WaterTile> waterTiles;
    List<GUITexture> guiTextures;


    public Main() {

        DisplayManager.createDisplay();

        entities = new ArrayList<>();
        lights = new ArrayList<>();
        terrains = new ArrayList<>();
        waterTiles = new ArrayList<>();
        guiTextures = new ArrayList<>();

        /*
            Initialize Objects
         */

        loader = new Loader();
        tools = new Tools();
        player = tools.createPlayer(loader, "person", "playerTexture", new Vector3f(-284.1552f, 5.624076f, -294.10355f), 0, 40, 0, 1);
        entities.add(player);
        camera = new Camera(player);
        sun = new Light(new Vector3f(20000, 60000, 20000), new Vector3f(1, 1, 1));
        lights.add(sun);
        renderer = new MasterRenderer(loader, sun);
        waterShader = new WaterShader();
        WaterFrameBuffers fbos = new WaterFrameBuffers();
        waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
        guiRenderer = new GuiRenderer(loader);
        random = new Random();
        terrain = tools.createTerrain(loader, 0, 0, "blendmap", "heightmap");
        picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

        terrains.add(terrain);

        /*
            Add Fern model
         */

        // Fern Model
        ModelData fernData = OBJFileLoader.loadOBJ(("fern"));
        RawModel fernModel = loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(), fernData.getNormals(), fernData.getIndices());
        ModelTexture fernModelTexture = new ModelTexture(loader.loadTexture("fern"));
        fernModelTexture.setHasTransparency(true);
        fernModelTexture.setUseFakeLighting(true);
        fernModelTexture.setNumberOfRows(2);
        TexturedModel fernTexturedModel = new TexturedModel(fernModel, fernModelTexture);

        for(int i=0;i<100;i++){
            float randx = -random.nextInt(400);
            float randz = -random.nextInt(400);
            entities.add(new Entity(fernTexturedModel, random.nextInt(4), new Vector3f(randx, terrain.getHeightOfTerrain(randx, randz), randz),0,0,0,1));
        }



        WaterTile waterTile = new WaterTile(-200, -180, 0);
        waterTiles.add(waterTile);



        //Portal FrameBuffer
        RawModel grass; //might not have been initialized

        FrameBufferObject pfbo = new FrameBufferObject(400, 400);
        Entity portal = new Entity(grass, new Vector3f(100, 30, 100), 0, 0, 0, 3);


        /*
            GameLoop
         */
        while (!Display.isCloseRequested()) {
            camera.move();
            player.move(terrain);

            mousePicker();



            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            //Render Reflection Texture
            fbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - waterTile.getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, 1, 0, -waterTile.getHeight() + 0.4f));
            camera.getPosition().y += distance;
            camera.invertPitch();

            //Render Refraction Texture
            fbos.bindRefractionFrameBuffer();
            renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, waterTile.getHeight()));

            //Render portal
            pfbo.bindFrameBuffer();
            Vector3f camPos = camera.getPosition();
            camera.setPosition(new Vector3f(-200.0f, 30.0f, -200.0f));
            float pitch = camera.getPitch();
            float yaw = camera.getYaw();
            camera.setPitch(29);
            camera.setYaw(49);

            renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, 1, 0, -waterTile.getHeight() + 0.4f));

            camera.setPitch(pitch);
            camera.setYaw(yaw);

            camera.setPosition(camPos);

            portal.setTexture(pfbo.getTexture());

            //Render to screen
            fbos.unbindCurrentFrameBuffer();
            renderer.processEntity(portal);
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, 10000));
            waterRenderer.render(waterTiles, camera, lights.get(0));
            tools.printLocation(player);
            //guiRenderer.render(guiTextures);


            DisplayManager.updateDisplay();
        }






        renderer.cleanUp();
        loader.cleanUp();
        fbos.cleanUp();
        waterShader.cleanUp();
        //guiRenderer.cleanUp();
        DisplayManager.closeDisplay();
    }

    public static void main(String[] args) {
        Main main = new Main();
    }

    private void mousePicker() {
        if(Mouse.isButtonDown(1)) {
            picker.update();
            Vector3f pos = picker.getCurrentTerrainPoint();
            if (pos != null) {
                entities.add(tools.createTree(loader, pos));
            }
        }
    }
}
