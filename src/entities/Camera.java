package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/** We use a lot of geometry and trigonometry in determining how this class will work.
 *  Some of the concepts we use here are:
 *      - Vertical Angles (Opposite angles)
 *      - Corresponding angles, interior and exterior, (parallel lines intersected by a traversal)
 *      - Vertical distance = d * sin(theta), where d is the distanceFromPlayer, and theta is the pitch, in radians.
 *      - Horizontal distance = d * cos(theta), where d is the distanceFromPlayer, and theta is the pitch, in radians.
 */
public class Camera {

    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    private Player player;
    private Vector3f position = new Vector3f(0 ,0,0);
    private float pitch = 20;
    private float yaw = 0;
    private float roll;

    public Camera(Player player) {
        this.player = player;
    }

    public void move() {
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();

        if (!player.isFirstCamera()) {
            float horizontalDistance = calculateHorizontalDistance();
            float verticalDistance = calculateVerticalDistance();
            calculateCameraPosition(horizontalDistance, verticalDistance);
        } else {
            firstPersonCam();
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    private void firstPersonCam() {
        this.position.y = player.getPosition().y + 8;
        this.position.x = player.getPosition().x;
        this.position.z = player.getPosition().z;
        if (player.isFirstCamera()) {
            calculatePitch();
        } else {
            this.pitch = player.getRx();
        }
        this.yaw = 180 - player.getRy();
    }

    private void calculateCameraPosition(float horizDistance, float verticDistance) {
        float theta = player.getRy() + angleAroundPlayer;
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
        this.position.y = player.getPosition().y + verticDistance + 6;      //+6 offsets to player's chest instead of feet.
        this.position.x = player.getPosition().x - offsetX;
        this.position.z = player.getPosition().z - offsetZ;
        this.yaw = 180 - (player.getRy() + angleAroundPlayer);
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel() * 0.1f;
        distanceFromPlayer -= zoomLevel;
        if (distanceFromPlayer > 100 ) {
            distanceFromPlayer = 100;
        }
        if (distanceFromPlayer < 25) {
            distanceFromPlayer = 25;
        }
    }

    private void calculatePitch() {
        if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
            if (!player.isFirstCamera()) {
                if (pitch > 50) {
                    pitch = 50;
                }
                if (pitch < player.getTerrainHeight()) {
                    pitch = player.getTerrainHeight();
                }
            } else {
                if (pitch > 45) {
                    pitch = 45;
                }
                if (pitch < -20) {
                    pitch = -20;
                }
            }
        }
    }

    private void calculateAngleAroundPlayer() {
        if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }

}
