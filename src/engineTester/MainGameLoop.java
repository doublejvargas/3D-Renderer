package engineTester;

import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;

import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        Light light = new Light(new Vector3f(20000, 40000, 20000), new Vector3f(1, 1, 1));

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkflowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture,
                bTexture);

        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        Terrain terrain  = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");

        ArrayList<RawModel> rawModels = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String name = null;
            if (i == 0) name = "tree";
            if (i == 1) name = "grassModel";
            if (i == 2) name = "fern";
            if (i == 3) name = "lowPolyTree";
            if (i == 4) name = "grassModel";
            ModelData data = OBJFileLoader.loadOBJ(name);
            RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(),
                    data.getNormals(), data.getIndices());
            rawModels.add(model);
        }

        TexturedModel tree = new TexturedModel(rawModels.get(0), new ModelTexture(loader.loadTexture("tree")));
        TexturedModel grass = new TexturedModel(rawModels.get(1), new ModelTexture(loader.loadTexture("grassTexture")));
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fernAtlas"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(rawModels.get(2), fernTextureAtlas);
        TexturedModel lowPolyTree = new TexturedModel(rawModels.get(3), new ModelTexture(loader.loadTexture("lowPolyTree")));
        TexturedModel flower = new TexturedModel(rawModels.get(4), new ModelTexture(loader.loadTexture("flower")));

        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);
        flower.getTexture().setHasTransparency(true);
        flower.getTexture().setUseFakeLighting(true);
        fern.getTexture().setHasTransparency(true);

        List<Entity> entities = new ArrayList<>();
        Random random = new Random(676452);
        for(int i = 0; i < 100; i++){
            if (i % 7 == 0) {
                float x = random.nextFloat() * 400 - 200;
                float z = random.nextFloat() * -400;
                float y = terrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(grass, new Vector3f(x, y, z), 0, 0, 0, 1.8f));
                x = random.nextFloat() * 400 - 200;
                z = random.nextFloat() * -400;
                y = terrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(flower, new Vector3f(x,y,z), 0, 0, 0, 2.3f));
            }
            if (i % 3 == 0) {
                float x = random.nextFloat() * 400 - 200;
                float z = random.nextFloat() * -400;
                float y = terrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x,y,z), 0, random.nextFloat()* 360, 0, 0.9f));
                x = random.nextFloat() * 400 - 200;
                z = random.nextFloat() * -400;
                y = terrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(lowPolyTree, new Vector3f(x,y,z), 0, random.nextFloat()* 360, 0, random.nextFloat() * 0.1f + 0.6f));
                x = random.nextFloat() * 400 - 200;
                z = random.nextFloat() * -400;
                y = terrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(tree, new Vector3f(x,y,z), 0, 0, 0, random.nextFloat() * 1 + 4));
            }
        }

        MasterRenderer renderer = new MasterRenderer();

        RawModel bunnyModel = OBJLoader.loadOBJModel("person", loader);
        TexturedModel playerModel = new TexturedModel(bunnyModel,
                new ModelTexture(loader.loadTexture("playerTexture")));

        Player player = new Player(playerModel, new Vector3f(100, 0, -50), 0, 180, 0, 0.6f);
        Camera camera = new Camera(player);

        while(!Display.isCloseRequested()) {
            camera.move();
            player.move(terrain);
            renderer.processEntity(player);
            renderer.processTerrain(terrain);
            for(Entity entity : entities) {
                renderer.processEntity(entity);
            }
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        entities.clear();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

}