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

import java.beans.AppletInitializer;
import java.util.ArrayList;
import java.util.HashMap;
import static org.lwjgl.assimp.Assimp.*;
import org.lwjgl.assimp.*;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.opengl.GL15;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.meshLoading.legacy.IndexedModel;
import net.abi.abisEngine.rendering.meshLoading.legacy.OBJModel;
import net.abi.abisEngine.rendering.resourceManagement.MeshResource;
import net.abi.abisEngine.util.Util;

public class Mesh {

	private static Logger logger = LogManager.getLogger(Mesh.class.getName());
	/*
	 * Saves An Unnecessary Allocation Of Resources If The Mesh Has Been Loaded In A
	 * Different Call.
	 */
	private static HashMap<String, MeshResource> loadedModels = new HashMap<String, MeshResource>();
	private MeshResource meshBuffers; // The Mesh That Is Being Loaded.
	private String fileName;

	private AIMesh ai_mesh;

	/**
	 * Mesh Resource ID's.
	 */
	private int vbo, ibo, size, refCount;

	public Mesh(AIMesh aiMesh) {
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();

		for (int i = 0; i < model.getPositions().size(); i++) {
			vertices.add(new Vertex(model.getPositions().get(i), model.getTexCoords().get(i), model.getNormals().get(i),
					model.getTangents().get(i)));
		}

		Vertex[] vertexData = new Vertex[vertices.size()];
		vertices.toArray(vertexData);

		Integer[] indicesData = new Integer[model.getIndices().size()];
		model.getIndices().toArray(indicesData);

		addVertices(vertexData, Util.toIntArray(indicesData), calcNormals);
	}

	private void genBuffers(int size) {
		this.vbo = GL15.glGenBuffers();
		this.ibo = GL15.glGenBuffers();
		this.size = size;
		this.refCount = 1;
	}

	private void addVertices(Vertex[] vertices, int[] indices, boolean calcNormals) {

		if (calcNormals) {
			calcNormals(vertices, indices);
		}

		// meshBuffers.setSize(indices.length);
		meshBuffers = new MeshResource(indices.length);

		glBindBuffer(GL_ARRAY_BUFFER, meshBuffers.getVbo());
		glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(vertices), GL_STATIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, meshBuffers.getIbo());
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL_STATIC_DRAW);

	}

	public void draw() {
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		// glEnableVertexAttribArray(4);

		glBindBuffer(GL_ARRAY_BUFFER, meshBuffers.getVbo());

		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);
		glVertexAttribPointer(3, 3, GL_FLOAT, false, Vertex.SIZE * 4, 32);
		// glVertexAttribPointer(3, 3, GL_FLOAT, false, Vertex.SIZE * 4, 44);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, meshBuffers.getIbo());

		glDrawElements(GL_TRIANGLES, meshBuffers.getSize(), GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		// glDisableVertexAttribArray(4);
	}

	@Deprecated
	public Mesh(String fileName, boolean calcNormals) {

		MeshResource oldResource = loadedModels.get(fileName);

		this.fileName = fileName;

		// If the mesh being loaded already exists in the loadedModels map than use the
		// mesh there
		// if (oldResource != null) {
		// this.meshBuffers = oldResource;
		// this.meshBuffers.addReference(); // Increment the reference counter for
		// garbage collection
		// } else { // Else if the mesh dose not exist create a new mesh by calling the
		// load mesh
		// method.
		// initMeshData();
		this.loadMesh(fileName, calcNormals);
		Mesh.loadedModels.put(fileName, meshBuffers); // Then put the mesh in the loaded models map for future use.
		// }

	}

	@Deprecated
	public Mesh(Vertex[] vertices, int[] indices) {
		this(vertices, indices, false);
	}

	@Deprecated
	public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals) {
		initMeshData();
		this.fileName = "";
		this.addVertices(vertices, indices, calcNormals);
	}

	@Deprecated
	private void initMeshData() {
		meshBuffers = new MeshResource(0);
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

		addVertices(vertexData, Util.toIntArray(indicesData), calcNormals);

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
		if (meshBuffers.removeRefrence() && fileName.isEmpty()) {
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
