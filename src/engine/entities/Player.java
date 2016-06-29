package engine.entities;

import engine.models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import engine.renderEngine.DisplayManager;
import engine.terrains.Terrain;

/**
 * Created by Felix on 30.05.2016.
 */
public class Player extends Entity {

    private static final float MOVE_SPEED = 50;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -30;
    private static final float JUMP_POWER = 17f;

    private static final float TERRAIN_HEIGHT = 0;

    private float currentspeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(Terrain terrain) {
        checkInputs();
        //System.out.println(getPosition().getX() + ", " + getPosition().getY() + ", " + getPosition().getZ());
        super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeInSecounds(), 0);
        float distance = currentspeed * DisplayManager.getFrameTimeInSecounds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increadePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeInSecounds();
        super.increadePosition(0, upwardsSpeed * DisplayManager.getFrameTimeInSecounds(), 0);
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        //System.out.println("TerrainHeight: " + terrainHeight + ", Height: " + super.getPosition().y); //=================
        if(super.getPosition().y < terrainHeight) {
            upwardsSpeed = 0;
            super.getPosition().y = terrainHeight;
            isInAir = false;
        }



    }

    private void jump() {
        if(!isInAir) {
            upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    private void checkInputs() {
        if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
            currentspeed = MOVE_SPEED;
        } else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
            currentspeed = -MOVE_SPEED;
        } else {
            currentspeed = 0;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
            currentTurnSpeed = -TURN_SPEED;
        } else if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
            currentTurnSpeed = TURN_SPEED;
        } else {
            currentTurnSpeed = 0;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            jump();
        }
    }

}
