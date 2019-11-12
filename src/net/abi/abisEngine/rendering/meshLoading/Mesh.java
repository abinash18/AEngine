package net.abi.abisEngine.rendering.meshLoading;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.opengl.GL15;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.math.Vector2f;
import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.meshLoading.legacy.IndexedModel;
import net.abi.abisEngine.rendering.meshLoading.legacy.OBJModel;
import static net.abi.abisEngine.rendering.meshLoading.AIMeshLoader.*;
import net.abi.abisEngine.rendering.resourceManagement.MeshResource;
import net.abi.abisEngine.util.Util;

public class Mesh {

	private static final AIVector3D ZERO_VECTOR = AIVector3D.create().set(0.0f, 0.0f, 0.0f);
	private static Logger logger = LogManager.getLogger(Mesh.class.getName());
	/*
	 * Saves An Unnecessary Allocation Of Resources If The Mesh Has Been Loaded In A
	 * Different Call.
	 */
	private static HashMap<String, MeshResource> loadedModels = new HashMap<String, MeshResource>();
	// private MeshResource meshBuffers; // The Mesh That Is Being Loaded.
	private String fileName;

	private AIMesh ai_mesh;

	/**
	 * Mesh Resource ID's.
	 */
	private int vbo, ibo, size, refCount;

	public Mesh(AIMesh model) {
		this.ai_mesh = model;
		List<Vertex> vertices = new ArrayList<Vertex>();

		int _i = model.mNumVertices();

		for (int i = 0; i < _i; i++) {
			AIVector3D pos = model.mVertices().get(i);
			AIVector3D normal = model.mNormals().get(i);
			AIVector3D texCoord = model.mTextureCoords(0).get(i);
			/*
			 * if (model.mTextureCoords(0).sizeof() != 0) { texCoord =
			 * model.mTextureCoords(0).get(i); } else { texCoord = ZERO_VECTOR; }
			 */
			AIVector3D tangent = model.mTangents().get(i);

			vertices.add(new Vertex(new Vector3f(pos.x(), pos.y(), pos.z()), new Vector2f(texCoord.x(), texCoord.y()),
					new Vector3f(normal.x(), normal.y(), normal.z()),
					new Vector3f(tangent.x(), tangent.y(), tangent.z())));
		}

		Vertex[] vertexData = new Vertex[vertices.size()];
		vertices.toArray(vertexData);

		List<Integer> indicies = new ArrayList<Integer>();

		for (int i = 0; i < model.mNumFaces(); i++) {
			AIFace face = model.mFaces().get(i);
			assert (face.mNumIndices() == 3);
			indicies.add(face.mIndices().get(0));
			indicies.add(face.mIndices().get(1));
			indicies.add(face.mIndices().get(2));
		}

		Integer[] indicesData = new Integer[model.mNumFaces()];
		int[] ind = Util.toIntArray(indicies.toArray(indicesData));

		addVerticesSD(vertexData, ind);
	}

	private void genBuffers(int size) {
		this.vbo = GL15.glGenBuffers();
		this.ibo = GL15.glGenBuffers();
		this.size = size;
		this.refCount = 1;
	}

	/**
	 * Adds vertices and indices to the mesh buffer using STATIC DRAW.
	 * 
	 * @param vertices
	 * @param indices
	 */
	private void addVerticesSD(Vertex[] vertices, int[] indices) {
		addVertices(vertices, indices, GL_STATIC_DRAW);
	}

	/**
	 * Adds vertices and indices using the draw_option.
	 * 
	 * @param vertices
	 * @param indices
	 */
	private void addVertices(Vertex[] vertices, int[] indices, int draw_option) {
		// meshBuffers.setSize(indices.length);
		genBuffers(indices.length);

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(vertices), draw_option);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), draw_option);

	}

	public void draw() {
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);

		glBindBuffer(GL_ARRAY_BUFFER, vbo);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);
		glVertexAttribPointer(3, 3, GL_FLOAT, false, Vertex.SIZE * 4, 32);
		// glVertexAttribPointer(3, 3, GL_FLOAT, false, Vertex.SIZE * 4, 44);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

		glDrawElements(GL_TRIANGLES, size, GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
	}

	@Deprecated
	public Mesh(String fileName, boolean calcNormals) {

		this(AIMeshLoader.loadModel(fileName,
				aiProcess_Triangulate | aiProcess_GenSmoothNormals | aiProcess_FlipUVs | aiProcess_CalcTangentSpace)
				.getMesh(0));

	}

	@Deprecated
	public Mesh(Vertex[] vertices, int[] indices) {
		this.addVerticesSD(vertices, indices);
	}

	@Deprecated
	private void calcNormals(Vertex[] vertices, int[] indices) {

		for (int i = 0; i < indices.length; i += 3) {
			int i0 = indices[i];
			int i1 = indices[i + 1];
			int i2 = indices[i + 2];

			Vector3f v1 = vertices[i1].getPos().sub(vertices[i0].getPos());
			Vector3f v2 = vertices[i2].getPos().sub(vertices[i0].getPos());

			Vector3f normal = v1.cross(v2).normalize();

			vertices[i0].setNormal(vertices[i0].getNormal().add(normal));
			vertices[i1].setNormal(vertices[i1].getNormal().add(normal));
			vertices[i2].setNormal(vertices[i2].getNormal().add(normal));

		}

		for (int i = 0; i < vertices.length; i++) {
			vertices[i].setNormal(vertices[i].getNormal().normalize());
		}

	}

	@Deprecated
	private Mesh loadMesh(String fileName, boolean calcNormals) {

		String[] splitArray = fileName.split("\\.");

		String extenstion = splitArray[splitArray.length - 1];

		if (!extenstion.equals("obj")) {
			// System.err.println("File Format Not Supported For Mesh Loading " + fileName);
			// new Exception().printStackTrace();
			logger.error("File Format Not Supported For Mesh Loading " + fileName, new Exception());
			logger.info("Exiting...");
			System.exit(1);
		}

		OBJModel test = new OBJModel("./res/models/" + fileName);
		IndexedModel model = test.toIndexedModel();

		if (calcNormals) {
			model.calcNormals();
		}
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();

		for (int i = 0; i < model.getPositions().size(); i++) {
			vertices.add(new Vertex(model.getPositions().get(i), model.getTexCoords().get(i), model.getNormals().get(i),
					model.getTangents().get(i)));
		}

		Vertex[] vertexData = new Vertex[vertices.size()];
		vertices.toArray(vertexData);

		Integer[] indicesData = new Integer[model.getIndices().size()];
		model.getIndices().toArray(indicesData);

		addVerticesSD(vertexData, Util.toIntArray(indicesData));

		return this;

	}

	@Override
	protected void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
			// e.printStackTrace();
			logger.error("Unable to finalize.", e);
		}
		if (removeRefrence() && fileName.isEmpty()) {
			Mesh.loadedModels.remove(fileName);
		}
	}

	public void addReference() {
		refCount++;
	}

	public boolean removeRefrence() {
		refCount--;
		return refCount == 0;
	}

	public int getVbo() {
		return vbo;
	}

	public void setVbo(int vbo) {
		this.vbo = vbo;
	}

	public int getIbo() {
		return ibo;
	}

	public void setIbo(int ibo) {
		this.ibo = ibo;
	}

	public int getSize() {
		return size;
	}

	public int getRefCount() {
		return refCount;
	}

	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}
}
