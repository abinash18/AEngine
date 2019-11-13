package net.abi.abisEngine.rendering.meshLoading;

import static org.lwjgl.assimp.Assimp.aiReleaseImport;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector2D;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import net.abi.abisEngine.math.Vector2f;
import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.meshLoading.legacy.IndexedModel;
import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.rendering.resourceManagement.Texture;
import net.abi.abisEngine.util.Color;
import net.abi.abisEngine.util.Util;

/**
 * This class stores all meshes in the AIScene, and converts them to the
 * engine's mesh format;
 * 
 */
public class ModelScene {
	private AIScene ai_scene;

	private Map<String, Mesh> meshes;

	public ModelScene(AIScene scene) {
		this.ai_scene = scene;

		int meshLength = scene.mNumMeshes(), matLength = scene.mNumMaterials();

		meshes = new HashMap<String, Mesh>();
		List<Material> mats = new ArrayList<Material>();

		PointerBuffer _mats = scene.mMaterials();

		for (int i = 0; i < matLength; i++) {
			AIMaterial _mat = AIMaterial.create(_mats.get(i));
			mats.add(processMaterial(_mat));
		}

		PointerBuffer _meshes = scene.mMeshes();

		for (int i = 0; i < meshLength; i++) {
			AIMesh _mesh = AIMesh.create(_meshes.get(i));
			meshes.put(_mesh.mName().dataString(), processMesh(_mesh, mats));
		}
	}

	private Mesh processMesh(AIMesh _mesh, List<Material> mats) {
		Mesh mesh = null;

		ArrayList<Vector3f> positions = new ArrayList<Vector3f>(), normals = new ArrayList<Vector3f>(),
				tangents = new ArrayList<Vector3f>();
		ArrayList<Vector2f> texCoords = new ArrayList<Vector2f>();
		ArrayList<Integer> indices = new ArrayList<Integer>();

		for (int i = 0; i < _mesh.mNumVertices(); i++) {
			AIVector3D pos = _mesh.mVertices().get(i);
			AIVector3D nor = _mesh.mNormals().get(i);
			AIVector3D tc = _mesh.mTextureCoords(0).get(i);

			if (tc == null) {
				tc = AIVector3D.create().set(0f, 0f, 0f);
			}

			AIVector3D tngt = _mesh.mTangents().get(i);

			positions.add(new Vector3f(pos.x(), pos.y(), pos.z()));
			normals.add(new Vector3f(nor.x(), nor.y(), nor.z()).normalize());
			// texCoords.add(new Vector2f(tc.x(), (1 - tc.y())));
			texCoords.add(new Vector2f(tc.x(), tc.y()).normalize());
			tangents.add(new Vector3f(tngt.x(), tngt.y(), tngt.z()));

		}

		for (int i = 0; i < _mesh.mNumFaces(); i++) {
			AIFace face = _mesh.mFaces().get(i);
			assert (face.mNumIndices() == 3);
			indices.add(face.mIndices().get(0));
			indices.add(face.mIndices().get(1));
			indices.add(face.mIndices().get(2));
		}

		mesh = new Mesh(_mesh.mName().dataString(), new IndexedModel(positions, normals, texCoords, tangents, indices));

		Material mat = null;

		int matIndex = _mesh.mMaterialIndex();

		if (matIndex >= 0 && matIndex < mats.size()) {
			mat = mats.get(matIndex);
		} else {
			mat = new Material();
		}

		mesh.setMat(mat);

		return mesh;
	}

	private ArrayList<Vector3f> processTangents(AIMesh _mesh) {
		ArrayList<Vector3f> tans = new ArrayList<Vector3f>();

		AIVector3D.Buffer _tans = _mesh.mTangents();

		while (_tans != null && _tans.remaining() > 0) {
			AIVector3D _tan = _tans.get();
			tans.add(new Vector3f(_tan.x(), _tan.y(), _tan.z()));
		}

		return tans;
	}

	private ArrayList<Vector3f> processVerticies(AIMesh _mesh) {
		ArrayList<Vector3f> verts = new ArrayList<Vector3f>();

		AIVector3D.Buffer _verts = _mesh.mVertices();

		while (_verts.remaining() > 0) {
			AIVector3D _vert = _verts.get();
			verts.add(new Vector3f(_vert.x(), _vert.y(), _vert.z()));
		}

		return verts;
	}

	private ArrayList<Vector3f> processNormals(AIMesh _mesh) {
		ArrayList<Vector3f> norms = new ArrayList<Vector3f>();

		AIVector3D.Buffer _norms = _mesh.mNormals();

		while (_norms != null && _norms.remaining() > 0) {
			AIVector3D _norm = _norms.get();
			norms.add(new Vector3f(_norm.x(), _norm.y(), _norm.z()));
		}

		return norms;
	}

	private ArrayList<Vector2f> processTexCoords(AIMesh _mesh) {
		ArrayList<Vector2f> texCoords = new ArrayList<Vector2f>();

		AIVector3D.Buffer _texCoords = _mesh.mTextureCoords(0);

		int numTexCoords = 0;

		if (_texCoords != null) {
			numTexCoords = _texCoords.remaining();
		}

		for (int i = 0; i < numTexCoords; i++) {
			AIVector3D _texC = _texCoords.get();
			texCoords.add(new Vector2f(_texC.x(), (1 - _texC.y())));
		}

		return texCoords;
	}

	private ArrayList<Integer> processIndicies(AIMesh _mesh) {
		ArrayList<Integer> indicies = new ArrayList<Integer>();

		AIFace.Buffer _faces = _mesh.mFaces();

		int numFaces = _mesh.mNumFaces();

		for (int i = 0; i < numFaces; i++) {
			AIFace _face = _faces.get(i);
			IntBuffer buffer = _face.mIndices();
			while (buffer.remaining() > 0) {
				indicies.add(buffer.get());
			}
		}

		return indicies;
	}

	private Material processMaterial(AIMaterial _mat) {

		Material mat = new Material();

		processMaterialTexture(_mat, AIMeshLoader.aiTextureType_DIFFUSE, mat);

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

	private Color processMaterialColor(AIMaterial _mat, String color_type, int texture_type, Material mat, int index) {
		AIColor4D color = AIColor4D.create();
		Color c = Color.DEFAULT_COLOR;
		if (Assimp.aiGetMaterialColor(_mat, color_type, texture_type, index, color) != 0) {
			c = new Color(color.r(), color.g(), color.b(), color.a());
		}
		return c;
	}

	/**
	 * Adds the textures of that type to the material specified from the _mat.
	 * 
	 * @param _mat
	 * @param texture_type
	 * @param mat
	 */
	private void processMaterialTexture(AIMaterial _mat, int texture_type, Material mat) {
		AIString path = AIString.calloc();

		Assimp.aiGetMaterialTexture(_mat, texture_type, 0, path, (IntBuffer) null, null, null, null, null, null);
		String texPath = path.dataString();
		if (!texPath.isEmpty()) {
			mat.addTexture("diffuse", new Texture(texPath));
		}

		/*
		 * This is when i have completed the asset import library implementation and
		 * have configured my engine to the appropriate state. and am ready to have
		 * multiple textures. The material properties are named in such fashion for
		 * Retrieval: Textures:
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

	private String typeToString(int type) {
		String typeString = "Type Not Found.";

		switch (type) {
		case AIMeshLoader.aiTextureType_AMBIENT:
			typeString = "ambient";
			break;
		case AIMeshLoader.aiTextureType_DISPLACEMENT:
			typeString = "displacementmap";
			break;
		case AIMeshLoader.aiTextureType_EMISSIVE:
			typeString = "emissive";
			break;
		case AIMeshLoader.aiTextureType_HEIGHT:
			typeString = "heightmap";
			break;
		case AIMeshLoader.aiTextureType_LIGHTMAP:
			typeString = "lightmap";
			break;
		case AIMeshLoader.aiTextureType_NORMALS:
			typeString = "normals";
			break;
		case AIMeshLoader.aiTextureType_OPACITY:
			typeString = "opacity";
			break;
		case AIMeshLoader.aiTextureType_REFLECTION:
			typeString = "reflection";
			break;
		case AIMeshLoader.aiTextureType_SHININESS:
			typeString = "shininess";
			break;
		case AIMeshLoader.aiTextureType_SPECULAR:
			typeString = "specular";
			break;
		case AIMeshLoader.aiTextureType_DIFFUSE:
			typeString = "diffuse";
			break;

		default:
			break;
		}

		return typeString;

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

}