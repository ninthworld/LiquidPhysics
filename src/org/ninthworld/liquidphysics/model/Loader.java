package org.ninthworld.liquidphysics.model;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.ninthworld.liquidphysics.helper.TextureData;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class Loader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public RawModel loadToVao(float[] positions, float[] colors, int[] indices){
        int vaoID = createVAO();
        int vboID = bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 3, colors);
        unbindVAO();
        return new RawModel(vaoID, vboID, indices.length);
    }

    public RawModel loadToVao(float[] positions, int dimesions){
        int vaoID = createVAO();
        this.storeDataInAttributeList(0, dimesions, positions);
        unbindVAO();
        return new RawModel(vaoID, 0, positions.length/dimesions);
    }

    public int loadTexture(String textureFile){
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", getClass().getResourceAsStream(textureFile));
        } catch (Exception e){
            e.printStackTrace();
        }
        textures.add(texture.getTextureID());
        return texture.getTextureID();
    }

    private TextureData decodeTextureFile(InputStream file) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            PNGDecoder decoder = new PNGDecoder(file);
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, PNGDecoder.RGBA);
            buffer.flip();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return new TextureData(buffer, width, height);
    }

    public void cleanRawModel(RawModel model){
        int vao = model.getVaoID();
        int vbo = model.getVboID();

        if(vaos.contains(vao)){
            GL30.glDeleteVertexArrays(vao);
            vaos.remove((Integer) vao);
        }

        if(vbos.contains(vao)){
            GL15.glDeleteBuffers(vbo);
            vbos.remove((Integer) vbo);
        }
    }

    public void cleanUp(){
        for(int vao : vaos){
            GL30.glDeleteVertexArrays(vao);
        }

        for(int vbo : vbos){
            GL15.glDeleteBuffers(vbo);
        }

        for(int texture : textures){
            GL11.glDeleteTextures(texture);
        }
    }

    private int createVAO(){
        int vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int dimensions, float[] data){
        int vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, dimensions, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
    }

    private void unbindVAO(){
        GL30.glBindVertexArray(0);
    }

    private int bindIndicesBuffer(int[] indices){
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        return vboID;
    }

    private IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();

        return buffer;
    }
}