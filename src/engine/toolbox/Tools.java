package engine.toolbox;

import engine.entities.Entity;
import engine.entities.Player;
import engine.models.ModelTexture;
import engine.models.TexturedModel;
import engine.objConverter.ModelData;
import engine.objConverter.OBJFileLoader;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import engine.renderEngine.Loader;
import engine.renderEngine.RawModel;
import engine.terrains.Terrain;
import engine.textures.TerrainTexture;
import engine.textures.TerrainTexturePack;

/**
 * Created by Felix on 12.06.2016.
 */
public class Tools {

    public Tools() {

    }

    public void printLocation(Player player) {
        if(Keyboard.isKeyDown(Keyboard.KEY_P)) {
            System.out.println(player.getPosition().x + "f, " + player.getPosition().y + "f, " + player.getPosition().z + "f");
        }
    }

    public Player createPlayer(Loader loader, String model, String texture, Vector3f pos, float rotx, float roty, float rotz, float scale) {
        ModelData playerData = OBJFileLoader.loadOBJ((model));
        RawModel playerModel = loader.loadToVAO(playerData.getVertices(), playerData.getTextureCoords(), playerData.getNormals(), playerData.getIndices());
        ModelTexture playerTexture = new ModelTexture(loader.loadTexture(texture));
        TexturedModel playerTexturedModel = new TexturedModel(playerModel, playerTexture);
        playerTexture.setReflectivity(1);
        playerTexture.setShineDamper(10);
        return new Player(playerTexturedModel, pos, rotx, roty, rotz, scale);
    }

    public Terrain createTerrain(Loader loader, int gridx, int gridz, String blendmap, String heightmap) {
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(blendmap));
        return new Terrain(gridx, gridz, loader, texturePack, blendMap, heightmap);
    }

    public Entity createTree(Loader loader, Vector3f pos) {
        ModelData treeData = OBJFileLoader.loadOBJ(("lowPolyTree"));
        RawModel treeModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());

        ModelTexture treeModelTexture = new ModelTexture(loader.loadTexture("lowPolyTree"));
        treeModelTexture.setShineDamper(10);
        treeModelTexture.setReflectivity(1);
        TexturedModel treeTexturedModel = new TexturedModel(treeModel, treeModelTexture);

        return new Entity(treeTexturedModel, pos, 0, 0, 0, 3);
    }


}
