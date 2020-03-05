package toolbox;

import entities.Camera;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Maths {


    /**
     * A vector space is what is spanned by linearly independent vectors. It is a term we can use
     * interchangeably with coordinate system and/or basis. (Remember? Linearly independent and span the space = basis).
     * Each 3D model exists in its own vector space called Model Space.
     *  In order for two or more models to be rendered together, e.g, a cup on a table, they first
     *  need to be moved (transformed) into a common, active space called the World Space.
     *  Transformations consist of:
     *          - Translations
     *          - Rotations
     *          - Scaling
     *  It is important to know that these transformations are done by transforming the entire space
     *  the 3D model exists in. That is to say, every single point that would exist anywhere in that space
     *  would be mapped to new location in in the World Space after the transformation.
     *
     *  Space transformations are done through nothing more and nothing less than a matrix.
     *  We use 4x4 Matrices to transform a 3D vector in Model Space into a 3D vector in World Space.
     *
     *  After vector space is transformed, it is "lost" (think of it as paper after burnt). The exception
     *  being when the inverse of the transformation is done on the new space.
     */

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx,
                                                      float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);

        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix,
                viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0,1,0), viewMatrix,
                viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);

        return viewMatrix;
    }
}
