package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    /** "CONSTANT" Variables for the Projection Matrix. */
    private static final float FOV = 70;             // Theta, or angle made by the field of view of camera (View Matrix).
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private static final float RED = 0.5f;
    private static final float GREEN = 0.5f;
    private static final float BLUE = 0.5f;

    private Matrix4f projectionMatrix;

    private StaticShader shader = new StaticShader();
    private EntityRenderer renderer;

    private TerrainShader terrainShader = new TerrainShader();
    private TerrainRenderer terrainRenderer;

    private List<Terrain> terrains = new ArrayList<>();
    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    public MasterRenderer() {
        enableCulling();
        createProjectionMatrix();
        renderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    /** This method renders both the entities and terrains, it uses "shader.render()" (for entities)
     *  and "terrainShader.render()" (for terrains). Therefore a single MasterRender object
     *  render call will render all of these.
     */
    public void render(Light sun, Camera camera) {
        prepare();
        shader.start();
        shader.loadSkyColour(RED, GREEN, BLUE);
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        terrainShader.start();
        terrainShader.loadSkyColour(RED, GREEN, BLUE);
        terrainShader.loadLight(sun);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        terrains.clear();
        entities.clear();
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);      //Disables rendering of triangles that are facing away from camera, i.e, inside model.
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }


    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(RED, GREEN , BLUE, 1);
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

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void cleanUp() {
        shader.cleanUp();
        terrainShader.cleanUp();
    }

    /** The concept of the projection matrix is one of the most complex when it comes to 3d graphics
     *  Projection concerns how to display 3D models onto a 2D screen. The two major types of projection
     *  are: Orthographic and perspective.
     *
     *  Orthographic maps a rectangular eye space into a conventional and default viewbox that
     *  ranges from (-1, -1, 0) to (1,1,1). Orthographic projection lacks a sense of depth.
     *
     *  Perspective maps a frustum (truncated pyramid) into the same viewbox ranging from (-1, -1, 0)
     *  to (1,1,1). Because the far end of the frustum is larger in area than the near end, it is compressed
     *  when mapped to the viewbox and therefore objects further back by the far end are made to appear smaller.
     *
     *  Matrices are a convenient way of performing this transformation from a rectangular/frustum
     *  eye space to the viewbox. One can mathematically compute the formulae to map each the x, y, and z
     *  coordinates for each transformation respectively and represent the transformation as a 4x4 matrix.
     *
     *  The 4th column of the matrix consists of constants in the formulae.
     */
    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV/2f))) * aspectRatio);    //FOV would be theta here. 1/tan(theta/2)
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;      //This implements a "perspective" (uses frustum) projection space,
        //as opposed to a "orthographic" (uses rectangle) projection space.

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE - NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * FAR_PLANE - NEAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }



}
