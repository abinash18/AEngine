package net.abi.abisEngine.rendering.meshLoading;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;

import net.abi.abisEngine.rendering.resourceManagement.Material;

/**
 * This class stores all meshes in the AIScene, and converts them to the
 * engine's mesh format;
 * 
 * @param scene
 */
public class ModelScene {

	private AIScene ai_scene;

	private List<Mesh> meshes;

	private List<Material> mats;

	public ModelScene(AIScene scene) {
		this.ai_scene = scene;

		int meshLength = scene.mNumMeshes(), matLength = scene.mNumMaterials();

		PointerBuffer meshbuffer = scene.mMeshes(), materialbuffer = scene.mMaterials();

		meshes = new ArrayList<Mesh>();
		for (int i = 0; i < meshLength; i++) {
			meshes.add(new Mesh(AIMesh.create(meshbuffer.get(i))));
		}

		mats = new ArrayList<Material>();
		for (int i = 0; i < matLength; i++) {
			// mats.add(new Material(AIMaterial.create(materialbuffer.get(i))));
		}
	}

	public void free() {
		aiReleaseImport(ai_scene);
		ai_scene = null;
		meshes = null;
		mats = null;
	}

	public AIMesh getMesh(int index) {
		AIMesh meshResult = null;
		meshes.get(index);
		return meshResult;
	}

}
