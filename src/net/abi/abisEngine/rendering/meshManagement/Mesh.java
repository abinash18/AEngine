package net.abi.abisEngine.rendering.meshManagement;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.asset.AssetI;
import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.util.Util;
import net.abi.abisEngine.util.cacheing.GenericCache;
import net.abi.abisEngine.util.cacheing.TwoFactorGenericCache;

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
			VAO_TANGENT_INDEX = 3, VAO_INDICES_INDEX = 4, VAO_BC_INDEX = 1, NUM_BUFFERS = 5;

	/**
	 * Contains the VAOID ID and VBO Buffer ID's for the mesh.
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
		private int VAOID;

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

		public void bind() {
			if (bound)
				return;
			/* Bind the VAOID to create buffers */
			GL30.glBindVertexArray(VAOID);
		}

		public VAO initVAO(int numBuffers) {

			VAOBuffers = new int[numBuffers];

			/* Generate a VAOID to use. */
			VAOID = GL30.glGenVertexArrays();

			/* Bind the VAOID to create buffers */
			GL30.glBindVertexArray(VAOID);

			/* Generate VBOs */
			GL30.glGenBuffers(VAOBuffers);

			bound = true;
			return this;
		}

		public void unbind() {
			GL30.glBindVertexArray(0);
			bound = false;
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
	 * and VAOID and MeshData.
	 * 
	 * @author abinash
	 *
	 */
	class MeshResource implements AssetI {
		String name;
		int size, refCount;
		HashMap<String, VAO> vaos;
		MeshData meshData;

		boolean initialized = false;

		/**
		 * @param size
		 * @param meshData
		 */
		public MeshResource(String name, MeshData meshData) {
			this.name = name;
			this.size = meshData.model.getIndices().size() - 1;
			this.refCount = 1;
			this.meshData = meshData;
			this.vaos = new HashMap<String, VAO>();
		}

		VAO addVAO(String name) {
			VAO _vao = new VAO(name);

			this.vaos.put(name, _vao);

			return _vao;
		}

		VAO getVAO(String name) {
			return vaos.get(name);
		}

		public void incRef() {
			refCount++;
		}

		public void decRef() {
			refCount--;
		}

		public int decAndGetRef() {
			refCount--;
			return refCount;
		}

		int getAndDecRef() {
			int _refs = refCount;
			refCount--;
			return _refs;
		}

		public int incAndGetRef() {
			refCount++;
			return refCount;
		}

		int getAndIncRef() {
			int _refs = refCount;
			refCount++;
			return _refs;
		}

		@Override
		public void dispose() {

		}

		@Override
		public int getRefs() {
			return refCount;
		}

	}

	// private static HashMap<Long, HashMap<String, MeshResource>> loadedModels =
	// new HashMap<Long, HashMap<String, MeshResource>>();

	private static TwoFactorGenericCache<Long, String, MeshResource> loadedModels = new TwoFactorGenericCache<Long, String, MeshResource>(
			Long.class, String.class, MeshResource.class);

	/**
	 * The data is managed seperatly from the MeshResources since there can be
	 * multiple GLContexts
	 */
	// private static HashMap<String, MeshData> loadedModelData = new
	// HashMap<String, MeshData>();

	private static GenericCache<String, MeshData> loadedModelData = new GenericCache<String, MeshData>(String.class,
			MeshData.class);

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
		MeshData _data;// = loadedModelData.get(name);

		if (loadedModelData.containsKey(name)) {
			_data = loadedModelData.get(name);
			// System.out.println("ssssss");
			/**
			 * If the data is not the same then we set the data to the data given. Else we
			 * Don't change the data and we keep going.
			 */
			if (!_data.model.equals(model)) {
				_data = new MeshData(model, name, _mat);
			}
		} else {
			_data = new MeshData(model, name, _mat);
			loadedModelData.put(name, _data);
		}

//		if (_data == null) {
//			_data = new MeshData(model, name, _mat);
//			loadedModelData.put(name, _data);
//		} else {
//			/**
//			 * If the data is not the same then we set the data to the data given. Else we
//			 * Don't change the data and we keep going.
//			 */
//			if (!_data.model.equals(model)) {
//				_data = new MeshData(model, name, _mat);
//			}
//		}

		/*
		 * Find the Context in the cache.
		 */
		// HashMap<String, MeshResource> _mm =
		// loadedModels.get(Long.valueOf(context_handle));

		/*
		 * If we find the resource all together for this context we can inc ref.
		 */
		MeshResource mr;
		if ((mr = loadedModels.get(this.context_handle, name)) != null) {
			/*
			 * Even though the cache is compatible for incrementing references that is only
			 * for put.
			 */
			mr.incRef();
		} else {
			loadedModels.put(this.context_handle, name, (mr = new MeshResource(name, _data)));
		}

		/*
		 * If the Context was not found then create a map and add it to the cache.
		 */
//		if (_mm == null) {
//			_mm = new HashMap<String, MeshResource>();
//			loadedModels.put(context_handle, _mm);
//		}

		// MeshResource _mr;

		/*
		 * Find the MeshResource in the Context thats found or was newly created.
		 */
//		if ((_mr = _mm.get(name)) == null) {
//			_mr = new MeshResource(_data);
//			_mm.put(name, _mr);
//		} else {
//			/*
//			 * If the context was found then increment the references and we are done.
//			 */
//			_mr.incRefs();
//		}

		this.meshResource = mr;

		// bindModel(model, GL15.GL_STATIC_DRAW);

	}

	public Mesh bindModel() {
		return bindModel(GL15.GL_STATIC_DRAW);
	}

	/**
	 * Adds the index model to open GL using the draw_option.
	 * 
	 * @param draw_usage This hints the GL Implementation on how the data provided
	 *                   will be used.
	 */
	public Mesh bindModel(int draw_usage) {

		if (meshResource.initialized) {
			return this;
		}

		VAO _v = meshResource.addVAO("vaoOne").initVAO(NUM_BUFFERS);

		/* Positions */
		bindBuffer(_v, GL15.GL_ARRAY_BUFFER, VAO_POSITIONS_INDEX, VAO_POSITIONS_INDEX, 3,
				Util.createFlippedBuffer(meshResource.meshData.model.getPositions()), draw_usage);

		/* Texture Coordinates */
		bindBuffer(_v, GL15.GL_ARRAY_BUFFER, VAO_TEXTURE_COORDINATE_INDEX, VAO_TEXTURE_COORDINATE_INDEX, 2,
				Util.createFlippedBuffer(meshResource.meshData.model.getTexCoords()), draw_usage);

		/* Normals */
		bindBuffer(_v, GL15.GL_ARRAY_BUFFER, VAO_NORMAL_INDEX, VAO_NORMAL_INDEX, 3,
				Util.createFlippedBuffer(meshResource.meshData.model.getNormals()), draw_usage);

		/* Tangents */
		bindBuffer(_v, GL15.GL_ARRAY_BUFFER, VAO_TANGENT_INDEX, VAO_TANGENT_INDEX, 3,
				Util.createFlippedBuffer(meshResource.meshData.model.getTangents()), draw_usage);

		/* Indices */
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, _v.VAOBuffers[VAO_INDICES_INDEX]);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, Util.createIntBuffer(meshResource.meshData.model.getIndices()),
				draw_usage);

		_v.unbind();
		meshResource.initialized = true;

		return this;
	}

	private void bindBuffer(VAO _v, int type, int index, int pos, int numValues, FloatBuffer data, int draw_usage) {
		GL15.glBindBuffer(type, _v.VAOBuffers[index]);
		GL15.glBufferData(type, data, draw_usage);
		GL20.glVertexAttribPointer(pos, numValues, GL11.GL_FLOAT, false, 0, 0);
		// GL15.glBindBuffer(type, 0);
	}

	public void deleteMesh() {
		// meshResource.vaos.forEach(v -> {
		// glDeleteBuffers(v.VAOBuffers);
		// glDeleteVertexArrays(v.VAO);
		// });

		for (Iterator<Map.Entry<String, VAO>> iterator = meshResource.vaos.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, VAO> entry = iterator.next();

			VAO _v = entry.getValue();

			glDeleteBuffers(_v.VAOBuffers);
			glDeleteVertexArrays(_v.VAOID);
		}

	}

	private void init(VAO _v) {

		_v.bind();

		for (int i = 0; i < _v.VAOBuffers.length; i++) {
			GL30.glEnableVertexAttribArray(i);
		}
		// GL30.glBindVertexArray(meshResource.vaos.get("vaoOne").VAOID);
		// glEnableVertexAttribArray(0);
		// glEnableVertexAttribArray(1);
		// glEnableVertexAttribArray(2);
		// glEnableVertexAttribArray(3);
		// glEnableVertexAttribArray(4);
	}

	public void draw(String vaoName, int draw_option) {
		init(meshResource.getVAO(vaoName));
		glDrawElements(draw_option, meshResource.size, GL_UNSIGNED_INT, 0);
		deInit(meshResource.getVAO(vaoName));
	}

	private void deInit(VAO _v) {
		for (int i = 0; i < _v.VAOBuffers.length; i++) {
			glDisableVertexAttribArray(i);
		}
		_v.unbind();

		// glDisableVertexAttribArray(0);
		// glDisableVertexAttribArray(1);
		// glDisableVertexAttribArray(2);
		// glDisableVertexAttribArray(3);
		// glDisableVertexAttribArray(4);
		// GL30.glBindVertexArray(0);
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
		Mesh.loadedModels.remove(this.context_handle, this.meshResource.name);
		this.deleteMesh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#incRef()
	 */
	@Override
	public void incRef() {
		this.meshResource.incRef();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#decRef()
	 */
	@Override
	public void decRef() {
		if (this.meshResource.decAndGetRef() <= 0) {
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
