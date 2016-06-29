package engine.textures;

/**
 * Created by Felix on 28.05.2016.
 */
public class TerrainTexture {

    private int textureID;

    public TerrainTexture(int texID) {
        textureID = texID;
    }

    public int getTextureID() {
        return textureID;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }
}
