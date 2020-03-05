package entities;

import org.lwjgl.util.vector.Vector3f;

public class Light {

    private Vector3f position;          // 3D vector for the light's position.
    private Vector3f colour;            // vec3(r,g,b); Determines color as well as intensity (See fragmentShader)

    public Light(Vector3f position, Vector3f colour) {
        this.position = position;
        this.colour = colour;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }
}
