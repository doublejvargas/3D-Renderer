package renderEngine;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

import java.util.List;
import java.util.Map;

public class EntityRenderer {

    private StaticShader shader;

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    /** This render method becomes more efficient by loading textures for each type of entity once, by
     * sorting entities in a HashMap by ModelTexture.
     */
    public void render(Map<TexturedModel, List<Entity>> entities) {
        for(TexturedModel model:entities.keySet()) {
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            for(Entity entity:batch){
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
                        GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel();
        }
    }

    private void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());      // We bind ("activate") the VAO of the model
        GL20.glEnableVertexAttribArray(0);      // We activate the Attribute List 0 of the VAO
        GL20.glEnableVertexAttribArray(1);      // Now we activate Attrib List 1 of the VAO for textures
        GL20.glEnableVertexAttribArray(2);      // Activate Attrib List 2 of the VAO for normals.
        ModelTexture texture = model.getTexture();
        shader.loadNumberofRows(model.getTexture().getNumberOfRows());
        if (texture.isHasTransparency()) {
            MasterRenderer.disableCulling();
        }
        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);       // Texture bank 0, default for textureSampler2D. (sampler2D has to do with textures, see fragmentShader).
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);                  //Deactivate VAO, after deactivating both attrib lists.
    }

    private void prepareInstance(Entity entity) { //Transforms Model Space into World Space.
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRx(), entity.getRy(), entity.getRz(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }
    /**
     *  ** OLD, INEFFICIENT RENDER METHOD! **
    public void render (Entity entity, StaticShader shader) {
        TexturedModel model = entity.getModel();
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());      // We bind ("activate") the VAO of the model
        GL20.glEnableVertexAttribArray(0);      // We activate the Attribute List 0 of the VAO
        GL20.glEnableVertexAttribArray(1);      // Now we activate Attrib List 1 of the VAO for textures
        GL20.glEnableVertexAttribArray(2);      // Activate Attrib List 2 of the VAO for normals.
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRx(), entity.getRy(), entity.getRz(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        ModelTexture texture = model.getTexture();
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);       // Texture bank 0, default for textureSampler2D. (sampler2D has to do with textures, see fragmentShader).
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);                  //Deactivate VAO, after deactivating both attrib lists.
    }
     */



}
