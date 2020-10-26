/**
 * 
 */
package net.abi.abisEngine.rendering.mesh;

import org.lwjgl.opengl.GL30;

/**
 * Contains the VAOID ID and VBO Buffer ID's for the mesh.
 * 
 * @author abinash
 *
 */
public class VertexArrayObject {
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

	public VertexArrayObject() {

	}

	public VertexArrayObject(String name) {
		this.name = name;
	}

	public void bind() {
		if (bound)
			return;
		/* Bind the VAOID to create buffers */
		GL30.glBindVertexArray(VAOID);
	}

	public VertexArrayObject initVAO(int numBuffers) {

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

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the vAOID
	 */
	public int getVAOID() {
		return VAOID;
	}

	/**
	 * @return the vAOBuffers
	 */
	public int[] getVAOBuffers() {
		return VAOBuffers;
	}

	/**
	 * @return the bound
	 */
	public boolean isBound() {
		return bound;
	}
}