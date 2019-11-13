package net.abi.abisEngine.rendering.meshLoading;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import java.nio.Buffer;
import java.nio.FloatBuffer;

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
			VAO_TANGENT_INDEX = 3, VAO_INDICES_INDEX = 4, NUM_BUFFERS = 5;

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
	private void bindModel(IndexedModel model, int draw_usage) {
		this.refCount = 1;
		size = model.getIndices().size();

		/* Generate VAO */
		VAO = GL45.glGenVertexArrays();

		GL45.glBindVertexArray(VAO);

		/* Generate VBOs */
		GL45.glGenBuffers(VAOBuffers);

		/* Positions */
		bindBuffer(GL15.GL_ARRAY_BUFFER, VAO_POSITIONS_INDEX, 0, 3, Util.createFlippedBuffer(model.getPositions()),
				draw_usage);

		/* Normals */
		bindBuffer(GL15.GL_ARRAY_BUFFER, VAO_TEXTURE_COORDINATE_INDEX, 1, 2,
				Util.createFlippedBuffer(model.getTexCoords()), draw_usage);

		/* Texture Coordinates */
		bindBuffer(GL15.GL_ARRAY_BUFFER, VAO_NORMAL_INDEX, 2, 3, Util.createFlippedBuffer(model.getNormals()),
				draw_usage);

		/* Tangents */
		bindBuffer(GL15.GL_ARRAY_BUFFER, VAO_TANGENT_INDEX, 3, 3, Util.createFlippedBuffer(model.getTangents()),
				draw_usage);

		/* Indices */
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VAOBuffers[VAO_INDICES_INDEX]);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, Util.createIntBuffer(model.getIndices()), draw_usage);

	}

	private void bindBuffer(int type, int index, int pos, int numValues, FloatBuffer data, int draw_usage) {
		GL15.glBindBuffer(type, VAOBuffers[index]);
		GL15.glBufferData(type, data, draw_usage);
		GL20.glVertexAttribPointer(pos, numValues, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(type, 0);
	}

	public void deleteMesh() {
		if (removeRefrence()) {
			AIMeshLoader.removeMesh(this);
			glDeleteBuffers(VAOBuffers);
			glDeleteVertexArrays(VAO);
		}
	}

	private void init() {
		GL45.glBindVertexArray(VAO);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
	}

	public void draw(int draw_option) {
		init();
		glDrawElements(draw_option, size, GL_UNSIGNED_INT, 0);
		deInit();
	}

	private void deInit() {
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		GL45.glBindVertexArray(0);
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
