package renderEngine;

import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Loader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        vaos.add(vaoID);
        // We store vbo containing positions in attrib list 0 of vao.
        storeDataInAttributeList(0, 3,  positions);
        // We store vbo containting texture (mapped)  coordinates in attrib list 1 of vao.
        storeDataInAttributeList(1, 2,  textureCoords);
        // We store vbo containting vertex normals coordinates in attrib list 2 of vao.
        storeDataInAttributeList(2, 3,  normals);
        unbindVAO();
        return new RawModel(vaoID, indices.length);     // We divide by 3 because each vertex contains 3 coordinates, xyz.
    }

    public int loadTexture(String fileName) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + fileName + ".png"));
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.5f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int textureID = texture.getTextureID();
        textures.add(textureID);

        return textureID;
    }

    public void cleanUp() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL15.glDeleteBuffers(vbo);
        }
        for (int texture : textures) {
            GL11.glDeleteTextures(texture);
        }
    }

    private int createVAO() {
        /** MEMORY MANAGEMENT NOTE:
         *  Despite the fact that the variable vboID is local to this method, the glGenBuffers function
         *  creates actual vbos in memory such that, even after vboID is lost once the scope of this function
         *  is left, the vbos remain in memory, which can cause a leak if unatended. Therefore they must be deleted.
         */
        int vaoID = GL30.glGenVertexArrays();   //Creates empty VAO in memory and returns id.
        GL30.glBindVertexArray(vaoID);          //"Activates" VAO by binding it. We specify which by inputting VAO's id.
        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        /** MEMORY MANAGEMENT NOTE:
         *  Despite the fact that the variable vboID is local to this method, the glGenBuffers function
         *  creates actual vbos in memory such that, even after vboID is lost once the scope of this function
         *  is left, the vbos remain in memory, which can cause a leak if unatended. Therefore they must be deleted.
         */
        int vboID = GL15.glGenBuffers(); //Creates empty VBO in memory and returns id.
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }


    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }


    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

}
