package test;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GUITexture;
import guis.GuiRenderer;
import models.ModelTexture;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.RawModel;
import terrains.Terrain;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Felix on 06.06.2016.
 */
public class SimpleTerrain {



    public void main() {
        Random random = new Random();

        Loader loader = new Loader();
        GuiRenderer guiRenderer = new GuiRenderer(loader);

        List<GUITexture> guis = new ArrayList<>();
        List<Entity> entities = new ArrayList<Entity>();
        List<Light> lights = new ArrayList<>();



        //Lights and Lamps


        Light sun = new Light(new Vector3f(0, 1000, -7000), new Vector3f(0.7f, 0.7f, 0.7f));
        lights.add(sun);
        lights.add(new Light(new Vector3f(365.33673f, 0, -261.68216f), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light(new Vector3f(370, 17, -293), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
        Light lampLight = new Light(new Vector3f(403.1938f, 0, -274.897f), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f));
        lights.add(lampLight);

        ModelData lampData = OBJFileLoader.loadOBJ(("lamp"));
        RawModel lampModel = loader.loadToVAO(lampData.getVertices(), lampData.getTextureCoords(), lampData.getNormals(), lampData.getIndices());

        ModelTexture lampModelTexture = new ModelTexture(loader.loadTexture("lamp"));
        lampModelTexture.setShineDamper(10);
        lampModelTexture.setReflectivity(1);
        TexturedModel lampTexturedModel = new TexturedModel(lampModel, lampModelTexture);

        entities.add(new Entity(lampTexturedModel, new Vector3f(365.33673f, -15.043478f, -261.68216f), 0, 0, 0, 1));
        entities.add(new Entity(lampTexturedModel, new Vector3f(370, -14.4f, -300), 0, 0, 0, 1));
        Entity moveableLamp = new Entity(lampTexturedModel, new Vector3f(403.1938f, -10.163013f, -274.897f), 0, 0, 0, 1);
        entities.add(moveableLamp);
        // Terrain Textures

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        //create terrain
        Terrain terrain = new Terrain(1,0,loader,texturePack, blendMap, "island-height");



        //entities.add(RenderHelper.addTerrainDetails("stall", "stallTexture", 0, 0, -20, 1, loader));

        // Tree Model
        ModelData treeData = OBJFileLoader.loadOBJ(("tree"));
        RawModel treeModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());

        ModelTexture treeModelTexture = new ModelTexture(loader.loadTexture("tree"));
        treeModelTexture.setShineDamper(10);
        treeModelTexture.setReflectivity(1);
        TexturedModel treeTexturedModel = new TexturedModel(treeModel, treeModelTexture);


        for(int i=0;i<500;i++){
            float randx = random.nextInt(800);
            float randz = -random.nextInt(800);
            entities.add(new Entity(treeTexturedModel, new Vector3f(randx, terrain.getHeightOfTerrain(randx, randz), randz),0,0,0,3));
        }

        // Grass Model
        ModelData grassData = OBJFileLoader.loadOBJ(("grassModel"));
        RawModel grassModel = loader.loadToVAO(grassData.getVertices(), grassData.getTextureCoords(), grassData.getNormals(), grassData.getIndices());
        ModelTexture grassModelTexture = new ModelTexture(loader.loadTexture("grassTexture"));
        grassModelTexture.setHasTransparency(true);
        grassModelTexture.setUseFakeLighting(true);
        TexturedModel grassTexturedModel = new TexturedModel(grassModel,grassModelTexture);

        for(int i=0;i<500;i++){
            float randx = random.nextInt(800);
            float randz = -random.nextInt(800);
            entities.add(new Entity(grassTexturedModel, new Vector3f(randx, terrain.getHeightOfTerrain(randx, randz), randz),0,0,0,3));
        }

        // Fern Model
        ModelData fernData = OBJFileLoader.loadOBJ(("fern"));
        RawModel fernModel = loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(), fernData.getNormals(), fernData.getIndices());
        ModelTexture fernModelTexture = new ModelTexture(loader.loadTexture("fern"));
        fernModelTexture.setHasTransparency(true);
        fernModelTexture.setUseFakeLighting(true);
        fernModelTexture.setNumberOfRows(2);
        TexturedModel fernTexturedModel = new TexturedModel(fernModel, fernModelTexture);

        for(int i=0;i<300;i++){
            float randx = random.nextInt(800);
            float randz = -random.nextInt(800);
            entities.add(new Entity(fernTexturedModel, random.nextInt(4), new Vector3f(randx, terrain.getHeightOfTerrain(randx, randz), randz),0,0,0,1));
        }

        // Create Terrain
//      Terrain terrain2 = new Terrain(0,1,loader,texturePack, blendMap);
//      Terrain terrain3 = new Terrain(1,0,loader,texturePack, blendMap);
//      Terrain terrain4 = new Terrain(1,1,loader,texturePack, blendMap);



        // Create Player
        ModelData playerData = OBJFileLoader.loadOBJ(("person"));
        RawModel playerModel = loader.loadToVAO(playerData.getVertices(), playerData.getTextureCoords(), playerData.getNormals(), playerData.getIndices());
        ModelTexture playerTexture = new ModelTexture(loader.loadTexture("playerTexture"));
        TexturedModel playerTexturedModel = new TexturedModel(playerModel, playerTexture);
        playerTexture.setReflectivity(1);
        playerTexture.setShineDamper(10);
        Player player = new Player(playerTexturedModel, new Vector3f(370, -13.4f, -320), 0, 0, 0, 0.8f);
        entities.add(player);

        //create camera
        Camera camera = new Camera(player);                                                     //create the camera


        MasterRenderer renderer = new MasterRenderer(loader, sun);                                   //advanded renderer
        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

        //Gameloop
        while (!Display.isCloseRequested()) {
            camera.move();                                                                //update camera positions based on key input
            player.move(terrain);
            picker.update();
            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
            if(terrainPoint != null) {
                lampLight.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 15, terrainPoint.z));
                moveableLamp.setPosition(terrainPoint);
            }
            System.out.println(picker.getCurrentRay());
            //tell renderer what to render
            renderer.processTerrain(terrain);

            for(Entity entity: entities){
                renderer.processEntity(entity);
            }

            //render the objects for the camera and light
            //wrong clip plane
            renderer.render(lights, camera, new Vector4f(0, 0, 0, 0));

            guiRenderer.render(guis);
            DisplayManager.updateDisplay();
        }
    }



}
