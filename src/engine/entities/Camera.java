package engine.entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;


/**
 * Created by Felix on 22.05.2016.
 */
public class Camera {

    private Vector3f position = new Vector3f(-200, 100, -200);
    private float pitch = MIN_PITCH;
    private float yaw;
    private float roll = 180;


    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    private final static float MAX_PITCH = 60;
    private final static float MIN_PITCH = 6;
    private final static float MAX_DIST = 500;
    private final static float MIN_DIST = 15;

    private Player player;

    public Camera(Player player) {
        this.player = player;
    }


    /**
     * moves the camera based on key input
     */
    public void move() {
        calculateAngleAroundPlayer();
        calculatePitch();
        calculateZoom();
        float horizontaldist = calculateHorizontalDistance();
        float verticaldist = calculateVertivalDistance();
        calculateCameraPos(horizontaldist, verticaldist);
        yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }


    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f pos) {
        position = pos;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel() * 0.1f;
        distanceFromPlayer -= zoomLevel;
        if(distanceFromPlayer < MIN_DIST) {
            distanceFromPlayer = MIN_DIST;
        } else if(distanceFromPlayer > MAX_DIST) {
            distanceFromPlayer = MAX_DIST;
        }

    }

    private void calculatePitch() {
        if(Mouse.isButtonDown(0)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
            if(pitch > MAX_PITCH) {
                pitch = MAX_PITCH;
            }
            if(pitch < MIN_PITCH) {
                pitch = MIN_PITCH;
            }
        }
    }

    public void invertPitch() {
        pitch = -pitch;
    }

    private void calculateAngleAroundPlayer() {
        if(Mouse.isButtonDown(0)) {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;

        }
    }

    private void calculateCameraPos(float horizontalDist, float verticalDist) {
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizontalDist * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDist * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;

        position.y = player.getPosition().y + verticalDist + 4;

    }

    private float calculateHorizontalDistance() {

        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVertivalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }
}
