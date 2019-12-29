package net.abi.abisEngine.rendering;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.asset.AssetI;
import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.util.Util;

/*
 * TODO: Make this class extendible so the user can extend and add attributes pragmatically by extracting them from shaders.
 */

/**
 * 
 * @author abinash
 *
 */
public class Mesh implements AssetI {

	private static final AIVector3D ZERO_VECTOR = AIVector3D.create().set(0.0f, 0.0f, 0.0f);

	/**
	 * INDEXs for the specific buffers in the VAOBuffers Array.
	 */
	private static final int VAO_POSITIONS_INDEX = 0, VAO_TEXTURE_COORDINATE_INDEX = 1, VAO_NORMAL_INDEX = 2,
			VAO_TANGENT_INDEX = 3, VAO_INDICES_INDEX = 4, VAO_BC_INDEX = 5, NUM_BUFFERS = 6;

	/**
	 * Contains the VAO ID and VBO Buffer ID's for the mesh.
	 * 
	 * @author abinash
	 *
	 */
	class VAO {
		/**
		 * Just so I can recognize them while I am Debugging.
		 */
		String name = "randomVAO: ";
		/**
		 * Mesh Resource ID.
		 */
		private int VAO;

		/**
		 * Array Containing the IDs for all the opengl buffers.
		 */
		private int[] VAOBuffers;

		boolean bound = false;

		public VAO() {

		}

		public VAO(String name) {
			this.name = name;
		}

		public VAO bindVAO(int numBuffers) {

			VAOBuffers = new int[numBuffers];

			/* Generate a VAO to use. */
			VAO = GL30.glGenVertexArrays();

			/* Bind the VAO to create buffers */
			GL30.glBindVertexArray(VAO);

			/* Generate VBOs */
			GL30.glGenBuffers(VAOBuffers);

			bound = true;
			return this;
		}

	}

	/**
	 * Contains the Model Data and the name and Materials that are used on this
	 * mesh.
	 * 
	 * @author abinash
	 *
	 */
	class MeshData {
		Model model;
		String meshName;
		Material material;

		int refCount = 1;

		/**
		 * @param model
		 * @param meshName
		 * @param material
		 */
		public MeshData(Model model, String meshName, Material material) {
			this.model = model;
			this.meshName = meshName;
			this.material = material;
		}

	}

	/**
	 * Contains the reference count to the mesh along with the size of the indices
	 * and VAO and MeshData.
	 * 
	 * @author abinash
	 *
	 */
	class MeshResource {
		int size, refCount;
		HashMap<String, VAO> vaos;
		MeshData meshData;

		boolean initialized = false;

		/**
		 * @param size
		 * @param meshData
		 */
		public MeshResource(MeshData meshData) {
			this.size = meshData.model.getIndices().size() - 1;
			this.refCount = 1;
			this.meshData = meshData;
			this.vaos = new HashMap<String, VAO>();
		}

		VAO addVAO(String name) {
			VAO _vao = new VAO();

			this.vaos.put(name, _vao);

			return _vao;
		}

		VAO getVAO(String name) {
			return vaos.get(name);
		}

		void incRefs() {
			refCount++;
		}

		void decRefs() {
			refCount--;
		}

		int decAndGetRefs() {
			refCount--;
			return refCount;
		}

		int getAndDecRefs() {
			int _refs = refCount;
			refCount--;
			return _refs;
		}

		int incAndGetRefs() {
			refCount++;
			return refCount;
		}

		int getAndIncRefs() {
			int _refs = refCount;
			refCount++;
			return _refs;
		}

	}

	private static HashMap<Long, HashMap<String, MeshResource>> loadedModels = new HashMap<Long, HashMap<String, MeshResource>>();

	/**
	 * The data is managed seperatly from the MeshResources since there can be
	 * multiple GLContexts
	 */
	private static HashMap<String, MeshData> loadedModelData = new HashMap<String, MeshData>();

	private static Logger logger = LogManager.getLogger(Mesh.class.getName());

	private MeshResource meshResource;

	private Long context_handle;

	/**
	 * 
	 * @param name            The name of the file or the models name in the file.
	 * @param model
	 * @param context_handle  The handle of the context this is being bound to.
	 * @param initialMaterial The Material this mesh will start off with, more can
	 *                        be added after construction. If this is null then the
	 *                        material will be set to the default material of the
	 *                        engine.
	 */
	public Mesh(String name, Model model, long context_handle, Material initialMaterial) {

		this.context_handle = Long.valueOf(context_handle);

		/*
		 * Check If the model is in fact valid to continue.
		 */
		if (!model.isValid()) {
			throw new IllegalStateException("Model: " + name + " Is Invalid.");
		}

		Material _mat = Material.DEFAULT_MATERIAL;

		/*
		 * If the user's supplied material is not null then use it to create the Mesh.
		 */
		if (initialMaterial != null) {
			_mat = initialMaterial;
		}

		/*
		 * Find the MeshData in the cache.
		 */
		MeshData _data = loadedModelData.get(name);

		if (_data == null) {
			_data = new MeshData(model, name, _mat);
			loadedModelData.put(name, _data);
		} else {
			/**
			 * If the data is not the same then we set the data to the data given. Else we
			 * Don't change the data and we keep going.
			 */
			if (!_data.model.equals(model)) {
				_data = new MeshData(model, name, _mat);
			}
		}

		/*
		 * Find the Context in the cache.
		 */
		HashMap<String, MeshResource> _mm = loadedModels.get(Long.valueOf(context_handle));

		/*
		 * If the Context was not found then create a map and add it to the cache.
		 */
		if (_mm == null) {
			_mm = new HashMap<String, MeshResource>();
			loadedModels.put(context_handle, _mm);
		}

		MeshResource _mr;

		/*
		 * Find the MeshResource in the Context thats found or was newly created.
		 */
		if ((_mr = _mm.get(name)) == null) {
			_mr = new MeshResource(_data);
			_mm.put(name, _mr);
		} else {
			/*
			 * If the context was found then increment the references and we are done.
			 */
			_mr.incRefs();
		}

		this.meshResource = _mr;

		// bindModel(model, GL15.GL_STATIC_DRAW);

	}

	public Mesh bindModel() {
		return bindModel(GL15.GL_STATIC_DRAW);
	}

	/**
	 * Adds the index model to open gl using the draw_option.
	 * 
	 * @param vertices
	 * @param indices
	 */
	public Mesh bindModel(int draw_usage) {

		if (meshResource.initialized) {
			return this;
		}

		VAO _v = meshResource.addVAO("vaoOne").bindVAO(NUM_BUFFERS + 1);

		/* No need to do this since we do it in the bindVAO Method. */
		// GL30.glBindVertexArray(meshResource.vao.VAO);

		/* Positions */
//		bindBuffer(GL15.GL_ARRAY_BUFFER, VAO_POSITIONS_INDEX, 0, 3,
//				Util.createFlippedBuffer(meshResource.meshData.model.getPositions()), draw_usage);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _v.VAOBuffers[VAO_POSITIONS_INDEX]);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Util.createFlippedBuffer(meshResource.meshData.model.getPositions()),
				draw_usage);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		/* Normals */
//		bindBuffer(GL15.GL_ARRAY_BUFFER, VAO_TEXTURE_COORDINATE_INDEX, 1, 2,
//				Util.createFlippedBuffer(meshResource.meshData.model.getTexCoords()), draw_usage);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _v.VAOBuffers[VAO_TEXTURE_COORDINATE_INDEX]);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Util.createFlippedBuffer(meshResource.meshData.model.getTexCoords()),
				draw_usage);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		// GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		/* Texture Coordinates */
//		bindBuffer(GL15.GL_ARRAY_BUFFER, VAO_NORMAL_INDEX, 2, 3,
//				Util.createFlippedBuffer(meshResource.meshData.model.getNormals()), draw_usage);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _v.VAOBuffers[VAO_NORMAL_INDEX]);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Util.createFlippedBuffer(meshResource.meshData.model.getNormals()),
				draw_usage);
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);

		/* Tangents */
//		bindBuffer(GL15.GL_ARRAY_BUFFER, VAO_TANGENT_INDEX, 3, 3,
//				Util.createFlippedBuffer(meshResource.meshData.model.getTangents()), draw_usage);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _v.VAOBuffers[VAO_TANGENT_INDEX]);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Util.createFlippedBuffer(meshResource.meshData.model.getTangents()),
				draw_usage);
		GL20.glVertexAttribPointer(3, 3, GL11.GL_FLOAT, false, 0, 0);

		/* Indices */
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, _v.VAOBuffers[VAO_INDICES_INDEX]);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, Util.createIntBuffer(meshResource.meshData.model.getIndices()),
				draw_usage);

		/* Different VAO for barycentric co-ords for performance */
		// VAO _vb = meshResource.addVAO("baryCoordVAO").bindVAO(1);
		ArrayList<Vector3f> values = meshResource.meshData.model.getvBCC();
		// Vector3f[][] vBC =
		// calculateBarycentric(meshResource.meshData.model.getPositions().size() - 1);

		/* BaryCentric Coordinates. */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, _v.VAOBuffers[VAO_BC_INDEX]);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Util.createFlippedBuffer(meshResource.meshData.model.getvBCC()),
				draw_usage);
		GL20.glVertexAttribPointer(4, 3, GL11.GL_FLOAT, false, 0, 0);
		meshResource.initialized = true;

		return this;

	}

//	private Vector3f[][] calculateBarycentric(int length) {
//		int n = length / Vec;
//		System.out.println(length);
//		System.out.println(n);
//		Vector3f[][] barycentric = new Vector3f[n][3];
//		System.out.println(barycentric[0].length);
//		for (int i = 1; i < barycentric.length; i++) {
//			System.out.println(i);
//			barycentric[n][1] = new Vector3f(1, 0, 0);
//			barycentric[n][2] = new Vector3f(0, 1, 0);
//			barycentric[n][3] = new Vector3f(0, 0, 1);
//		}
//		return barycentric;
//
//	}
//	private void bindBuffer(int type, int index, int pos, int numValues, FloatBuffer data, int draw_usage) {
//		GL15.glBindBuffer(type, meshResource.vao.VAOBuffers[index]);
//		GL15.glBufferData(type, data, draw_usage);
//		GL20.glVertexAttribPointer(pos, numValues, GL11.GL_FLOAT, false, 0, 0);
//		//GL15.glBindBuffer(type, 0);
//	}

	public void deleteMesh() {
//		meshResource.vaos.forEach(v -> {
//			glDeleteBuffers(v.VAOBuffers);
//			glDeleteVertexArrays(v.VAO);
//		});

		for (Iterator<Map.Entry<String, VAO>> iterator = meshResource.vaos.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, VAO> entry = iterator.next();

			VAO _v = entry.getValue();

			glDeleteBuffers(_v.VAOBuffers);
			glDeleteVertexArrays(_v.VAO);
		}

	}

	private void init() {
//		meshResource.vaos.forEach(v -> {
//			GL30.glBindVertexArray(v.VAO);
//
//			for (int i = 0; i < NUM_BUFFERS; i++) {
//				glEnableVertexAttribArray(i);
//			}
//		});
		GL30.glBindVertexArray(meshResource.vaos.get("vaoOne").VAO);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
	}

	public void draw(int draw_option) {
		init();
		glDrawElements(draw_option, meshResource.size, GL_UNSIGNED_INT, 0);
		deInit();
	}

	private void deInit() {
//		meshResource.vaos.forEach(v -> {
//			for (int i = 0; i < NUM_BUFFERS; i++) {
//				glDisableVertexAttribArray(i);
//			}
//			GL30.glBindVertexArray(v.VAO);
//		});

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glDisableVertexAttribArray(4);
		GL30.glBindVertexArray(0);
	}

	public int getSize() {
		return meshResource.size;
	}

	public String getMeshName() {
		return meshResource.meshData.meshName;
	}

	public Material getMat() {
		return meshResource.meshData.material;
	}

	public void setMat(Material mat) {
		this.meshResource.meshData.material = mat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.util.Expendable#dispose()
	 */
	@Override
	public void dispose() {
		this.loadedModels.remove(this.context_handle, this.meshResource);
		this.deleteMesh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#incRef()
	 */
	@Override
	public void incRef() {
		this.meshResource.incRefs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#decRef()
	 */
	@Override
	public void decRef() {
		if (this.meshResource.decAndGetRefs() <= 0) {
			dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#getRefs()
	 */
	@Override
	public int getRefs() {
		return this.meshResource.refCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#incAndGetRef()
	 */
	@Override
	public int incAndGetRef() {
		incRef();
		return this.meshResource.refCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#decAndGetRef()
	 */
	@Override
	public int decAndGetRef() {
		decRef();
		return this.meshResource.refCount;
	}
}
