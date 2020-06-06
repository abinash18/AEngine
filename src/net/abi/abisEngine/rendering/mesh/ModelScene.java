package net.abi.abisEngine.rendering.mesh;

import static org.lwjgl.assimp.Assimp.aiReleaseImport;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import net.abi.abisEngine.math.Vector2f;
import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.asset.AssetManager;
import net.abi.abisEngine.rendering.material.Material;
import net.abi.abisEngine.rendering.texture.Texture;
import net.abi.abisEngine.util.Expendable;

/**
 * This class stores all meshes in the AIScene, and converts them to the
 * engine's mesh format;
 * 
 */
public class ModelScene implements Expendable {
	private AIScene ai_scene;

	private Map<String, Mesh> meshes;

	private List<Material> mats;

	public ModelScene(AIScene scene, AssetManager man) {
		this.ai_scene = scene;
		meshes = new HashMap<String, Mesh>();
		mats = new ArrayList<Material>();
		processScene(man);
	}

	public void processScene(AssetManager man) {
		processMaterials(man);
		processMeshes(man);
	}

	/**
	 * @return Returns the scene with bound models to VBO's in the current context.
	 */
	public ModelScene bindModels(int draw_usage) {
		for (Mesh _mesh : meshes.values()) {
			_mesh.bindModel(draw_usage);
		}
		return this;
	}

	public ModelScene bindModels() {
		return this.bindModels(GL15.GL_STATIC_DRAW);
	}

	private void processMaterials(AssetManager man) {
		int matLength = ai_scene.mNumMaterials();

		PointerBuffer _mats = ai_scene.mMaterials();

		for (int i = 0; i < matLength; i++) {
			AIMaterial _mat = AIMaterial.create(_mats.get(i));
			mats.add(processMaterial(_mat, i));
		}
	}

	private void processMeshes(AssetManager man) {
		int meshLength = ai_scene.mNumMeshes();
		PointerBuffer _meshes = ai_scene.mMeshes();

		for (int i = 0; i < meshLength; i++) {
			AIMesh _mesh = AIMesh.create(_meshes.get(i));
			// meshes.put(_mesh.mName().dataString(), processMesh(_mesh, mats));
			meshes.put(_mesh.mName().dataString(), processMesh(_mesh, man));
		}
	}

	private Mesh processMesh(AIMesh _mesh, AssetManager man) {
		Mesh mesh = null;

		ArrayList<Vector3f> positions = new ArrayList<Vector3f>(), normals = new ArrayList<Vector3f>(),
				tangents = new ArrayList<Vector3f>();
		ArrayList<Vector2f> texCoords = new ArrayList<Vector2f>();
		ArrayList<Integer> indices = new ArrayList<Integer>();

		for (int i = 0; i < _mesh.mNumVertices(); i++) {
			AIVector3D pos = _mesh.mVertices().get(i);
			AIVector3D nor = _mesh.mNormals().get(i);

			AIVector3D.Buffer _tc = _mesh.mTextureCoords(0);
			AIVector3D tc = AIVector3D.create().set(0f, 0f, 0f);
			if (_tc != null) {
				tc = _tc.get(i);
			}

			AIVector3D.Buffer tngt = _mesh.mTangents();
			AIVector3D _tngt = AIVector3D.create().set(0f, 0f, 0f);
			if (tngt != null) {
				_tngt = tngt.get(i);
			}

			positions.add(new Vector3f(pos.x(), pos.y(), pos.z()));
			normals.add(new Vector3f(nor.x(), nor.y(), nor.z()).normalize());
			texCoords.add(new Vector2f(tc.x(), (1 - tc.y())));
			// texCoords.add(new Vector2f(tc.x(), tc.y()).normalize());
			tangents.add(new Vector3f(_tngt.x(), _tngt.y(), _tngt.z()));

		}
		ArrayList<Vector3f> vBCC = new ArrayList<Vector3f>();
		for (int i = 0; i < _mesh.mNumFaces(); i++) {
			AIFace face = _mesh.mFaces().get(i);
			assert (face.mNumIndices() == 3);
			indices.add(face.mIndices().get(0));
			// vBCC.add(new Vector3f(1, 0, 0));
			indices.add(face.mIndices().get(1));
			// vBCC.add(new Vector3f(0, 1, 0));
			indices.add(face.mIndices().get(2));
			// vBCC.add(new Vector3f(0, 0, 1));
		}

		//calculateBarycentric(indices.size());

//		for (int i = 0; i < indices.size() / 3; i++) {
//			vBCC.add(new Vector3f(1, 0, 0));
//			vBCC.add(new Vector3f(0, 1, 0));
//			vBCC.add(new Vector3f(0, 0, 1));
//		}

//		 = calculateBarycentric(indices.size() - 1);

//		Material mat = null;
//
//		int matIndex = _mesh.mMaterialIndex();
//
//		if (matIndex >= 0 && matIndex < mats.size()) {
//			mat = mats.get(matIndex);
//		} else {
//			mat = new Material();
//		}

		// mesh.setMat(mat);
//		mesh = new Mesh(_mesh.mName().dataString(), new Model(positions, normals, texCoords, tangents, indices, vBCC),
//				man.getGlfw_handle(), null);
		mesh = new Mesh(_mesh.mName().dataString(), new Model(positions, normals, texCoords, tangents, indices),
				0, null);

		return mesh;

	}

	public String[] getTexturePaths() {

		int matLength = ai_scene.mNumMaterials();
		String[] paths = new String[matLength];

		PointerBuffer _mats = ai_scene.mMaterials();

		for (int i = 0; i < matLength; i++) {
			AIMaterial _mat = AIMaterial.create(_mats.get(i));
			AIString path = AIString.calloc();

			Assimp.aiGetMaterialTexture(_mat, AIMeshLoader.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null,
					null, null, null);
			// TODO: The rest of the texture types.
			String texPath = path.dataString();
			paths[i] = texPath;
		}
		return paths;
	}

	private Material processMaterial(AIMaterial _mat, int index) {

		Material mat = new Material();

		processMaterialTexture(_mat, AIMeshLoader.aiTextureType_DIFFUSE, mat, index);

		// TODO: Make Use of these.
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_AMBIENT);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_DISPLACEMENT);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_EMISSIVE);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_HEIGHT);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_LIGHTMAP);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_NORMALS);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_OPACITY);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_REFLECTION);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_SHININESS);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_SPECULAR);

		return mat;

	}

	private Material processMaterial(AIMaterial _mat, int index, AssetManager man) {

		Material mat = new Material();

		processMaterialTexture(_mat, AIMeshLoader.aiTextureType_DIFFUSE, mat, index);

		// TODO: Make Use of these.
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_AMBIENT);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_DISPLACEMENT);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_EMISSIVE);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_HEIGHT);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_LIGHTMAP);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_NORMALS);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_OPACITY);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_REFLECTION);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_SHININESS);
		// processMaterialTexture(_mat, AIMeshLoader.aiTextureType_SPECULAR);

		return mat;

	}
//	private Color processMaterialColor(AIMaterial _mat, String color_type, int texture_type, Material mat, int index) {
//		AIColor4D color = AIColor4D.create();
//		Color c = Color.DEFAULT_COLOR;
//		if (Assimp.aiGetMaterialColor(_mat, color_type, texture_type, index, color) != 0) {
//			c = new Color(color.r(), color.g(), color.b(), color.a());
//		}
//		return c;
//	}

	private void processMaterialTexture(AIMaterial _mat, int texture_type, Material mat, int index) {
		AIString path = AIString.calloc();

		Assimp.aiGetMaterialTexture(_mat, texture_type, 0, path, (IntBuffer) null, null, null, null, null, null);
		String texPath = path.dataString();

		if (!texPath.isEmpty()) {
			Texture tex = new Texture(texPath);
			mat.addTexture(String.valueOf(texture_type + index), tex);
		}

		/*
		 * This is when i have completed the asset import library implementation and
		 * have configured my engine to the appropriate state. and am ready to have
		 * multiple textures. The material properties are named in such fashion for
		 * Retrieval: Textures:
		 * 
		 * You concat the integer value of the enum to the string.
		 * 
		 * aiTextureType_* + index of the texture in the file.
		 * 
		 * Colors: AI_MATKEY_COLOR_* + aiTextureType_* + index of the texture.
		 * 
		 */
//		for (int i = 0; i < Assimp.aiGetMaterialTextureCount(_mat, texture_type) - 1; i++) {
//			Assimp.aiGetMaterialTexture(_mat, texture_type, i, path, (IntBuffer) null, null, null, null, null, null);
//			String texPath = path.dataString();
//			if (!texPath.isEmpty()) {
//				/*
//				 * I add i to the end so that if there are multiple textures then dose not
//				 * conflict with the previous textures.
//				 */
//				mat.addTexture(typeToString(texture_type) + i, new Texture(texPath));
//
//				mat.addColor(AIMeshLoader.AI_MATKEY_COLOR_DIFFUSE + typeToString(texture_type) + i,
//						processMaterialColor(_mat, AIMeshLoader.AI_MATKEY_COLOR_DIFFUSE, texture_type, mat, i));
//				mat.addColor(AIMeshLoader.AI_MATKEY_COLOR_AMBIENT + typeToString(texture_type) + i,
//						processMaterialColor(_mat, AIMeshLoader.AI_MATKEY_COLOR_AMBIENT, texture_type, mat, i));
//				mat.addColor(AIMeshLoader.AI_MATKEY_COLOR_EMISSIVE + typeToString(texture_type) + i,
//						processMaterialColor(_mat, AIMeshLoader.AI_MATKEY_COLOR_EMISSIVE, texture_type, mat, i));
//				mat.addColor(AIMeshLoader.AI_MATKEY_COLOR_REFLECTIVE + typeToString(texture_type) + i,
//						processMaterialColor(_mat, AIMeshLoader.AI_MATKEY_COLOR_REFLECTIVE, texture_type, mat, i));
//				mat.addColor(AIMeshLoader.AI_MATKEY_COLOR_SPECULAR + typeToString(texture_type) + i,
//						processMaterialColor(_mat, AIMeshLoader.AI_MATKEY_COLOR_SPECULAR, texture_type, mat, i));
//				mat.addColor(AIMeshLoader.AI_MATKEY_COLOR_TRANSPARENT + typeToString(texture_type) + i,
//						processMaterialColor(_mat, AIMeshLoader.AI_MATKEY_COLOR_TRANSPARENT, texture_type, mat, i));
//			}
//		}
	}

	public void free() {
		aiReleaseImport(ai_scene);
		ai_scene = null;
		meshes = null;
	}

	public Mesh getMesh(String name) {
		Mesh meshResult = null;
		meshResult = meshes.get(name);
		return meshResult;
	}

	public void removeMesh(String meshName) {
		meshes.remove(meshName);
	}

	public Map<String, Mesh> getMeshes() {
		return meshes;
	}

	@Override
	public void dispose() {
		this.free();
	}

}