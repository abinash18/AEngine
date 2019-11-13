package net.abi.abisEngine.rendering.meshLoading;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL45;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.meshLoading.legacy.IndexedModel;
import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.util.Util;

public class Mesh {

	private static final AIVector3D ZERO_VECTOR = AIVector3D.create().set(0.0f, 0.0f, 0.0f);

	private static final int VAO_POSITIONS_INDEX = 0, VAO_TEXTURE_COORDINATE_INDEX = 1, VAO_NORMAL_INDEX = 2,
			VAO_TANGENT_INDEX = 3, VAO_INDICIES_INDEX = 4, NUM_BUFFERS = 5;

	private static Logger logger = LogManager.getLogger(Mesh.class.getName());

	/*
	 * Saves An Unnecessary Allocation Of Resources If The Mesh Has Been Loaded In A
	 * Different Call.
	 */

	// private MeshResource meshBuffers; // The Mesh That Is Being Loaded.
	private String meshName;

	private Material mat;

	/**
	 * Mesh Resource ID's.
	 */
	private int VBO, IBO, size, refCount;

	private int VAO;

	private int[] VAOBuffers = new int[NUM_BUFFERS];

	public Mesh(String name, IndexedModel model) {
		this.meshName = name;

		if (!model.isValid()) {
			throw new IllegalStateException("Model: " + name + " Is Invalid.");
		}

		bindModel(model, GL15.GL_STATIC_DRAW);

	}

	/**
	 * Adds the index model to open gl using the draw_option.
	 * 
	 * @param vertices
	 * @param indices
	 */
	private void bindModel(IndexedModel model, int draw_option) {
		// meshBuffers.setSize(indices.length);

		size = model.getIndices().size();

		/* Generate VAO's */
		VAO = GL45.glGenVertexArrays();

		GL45.glBindVertexArray(VAO);

		GL45.glGenBuffers(VAOBuffers);

		/* Positions */

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VAOBuffers[VAO_POSITIONS_INDEX]);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Util.createFlippedBuffer(model.getPositions()), draw_option);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		/* Normals */

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VAOBuffers[VAO_TEXTURE_COORDINATE_INDEX]);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Util.createFlippedBuffer(model.getTexCoords()), draw_option);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		/* Texture Coordinates */

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VAOBuffers[VAO_NORMAL_INDEX]);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Util.createFlippedBuffer(model.getNormals()), draw_option);
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		/* Tangents */

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VAOBuffers[VAO_TANGENT_INDEX]);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Util.createFlippedBuffer(model.getTangents()), draw_option);
		GL20.glVertexAttribPointer(3, 3, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		/*
		 * Indicies
		 */
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VAOBuffers[VAO_INDICIES_INDEX]);

		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, Util.createIntBuffer(model.getIndices()), draw_option);
		// GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);

//		glBindBuffer(GL_ARRAY_BUFFER, VBO);
//		glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(Util.toVertexArray(model.getPositions(),
//				model.getNormals(), model.getTexCoords(), model.getTangents())), draw_option);
//
//		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, IBO);
//		glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(Util.listIntToArray(model.getIndices())),
//				draw_option);

//		glBindBuffer(GL_ARRAY_BUFFER, VAOBuffers[VAO_POSITIONS_INDEX]);
//		glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(model.getPositions()), draw_option);
//
//		glEnableVertexAttribArray(0);
//		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
//
//		glBindBuffer(GL_ARRAY_BUFFER, VAOBuffers[VAO_NORMAL_INDEX]);
//		glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(model.getNormals()), draw_option);
//
//		glEnableVertexAttribArray(1);
//		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
//
//		glBindBuffer(GL_ARRAY_BUFFER, VAOBuffers[VAO_TEXTURE_COORDINATE_INDEX]);
//		glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(model.getTexCoords()), draw_option);
//
//		glEnableVertexAttribArray(2);
//		glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
//
//		glBindBuffer(GL_ARRAY_BUFFER, VAOBuffers[VAO_TANGENT_INDEX]);
//		glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(model.getTangents()), draw_option);
//
//		glEnableVertexAttribArray(3);
//		glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
//
//		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, VAOBuffers[VAO_INDICIES_INDEX]);
//		glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.listIntToArray(model.getIndices()), draw_option);

		// glBindBuffer(GL_ARRAY_BUFFER, 0);
		// glBindVertexArray(0);

	}

	public void deleteMesh() {
		if (removeRefrence()) {
			AIMeshLoader.removeMesh(this);
			glDeleteBuffers(VAOBuffers);
			glDeleteVertexArrays(VAO);
		}
	}

	public void draw() {
		GL45.glBindVertexArray(VAO);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);

//		glBindBuffer(GL_ARRAY_BUFFER, VBO);
//
//		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
//		glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
//		glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);
//		glVertexAttribPointer(3, 3, GL_FLOAT, false, Vertex.SIZE * 4, 32);
////		 glVertexAttribPointer(3, 3, GL_FLOAT, false, Vertex.SIZE * 4, 44);
////
//		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, IBO);

		glDrawElements(GL15.GL_TRIANGLES, size, GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		GL45.glBindVertexArray(0);
	}

	private void genBuffers(int size) {
		// VAO = GL45.glGenVertexArrays();
		// GL45.glBindVertexArray(VAO);
		// GL45.glGenBuffers(VAOBuffers);
		VBO = GL15.glGenBuffers();
		IBO = GL15.glGenBuffers();
		this.size = size;
		this.refCount = 1;
	}

	public void addReference() {
		refCount++;
	}

	public boolean removeRefrence() {
		refCount--;
		return (refCount == 0);
	}

	public int getVbo() {
		return VBO;
	}

	public void setVbo(int vbo) {
		this.VBO = vbo;
	}

	public int getIbo() {
		return IBO;
	}

	public void setIbo(int ibo) {
		this.IBO = ibo;
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

	public String getMeshName() {
		return meshName;
	}

	public Material getMat() {
		return mat;
	}

	public void setMat(Material mat) {
		this.mat = mat;
	}
}
