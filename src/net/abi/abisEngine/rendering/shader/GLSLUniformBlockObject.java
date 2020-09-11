package net.abi.abisEngine.rendering.shader;

import static org.lwjgl.opengl.GL15.glGenBuffers;

import org.lwjgl.opengl.GL31;

/*
 * Uniform Buffer Object.
 * 
 * The class representing a Uniform block in gl memory.
 */
public class GLSLUniformBlockObject {

	// TODO: Window Contexts.
	private int id;
	private int binding;
	private int size;

	private GLSLUniformBlockObjectData data;

	public GLSLUniformBlockObject(GLSLUniformBlockObjectData data) {
		this.data = data;
	}

	/*
	 * Creates a GL_UNIFORM_BUFFER with the specified size in bytes.
	 */
	public int create(int size) {
		id = glGenBuffers();
		GL31.glBindBuffer(GL31.GL_UNIFORM_BUFFER, id);
		GL31.glBufferData(GL31.GL_UNIFORM_BUFFER, size, GL31.GL_STATIC_DRAW);
		GL31.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
		return id;
	}

	public void bind() {
		GL31.glBindBuffer(GL31.GL_UNIFORM_BUFFER, id);
	}

	public void unbind() {
		GL31.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
	}

	public int getSize() {
		return size;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBinding() {
		return binding;
	}

	public GLSLUniformBlockObjectData getData() {
		return data;
	}

	public void setData(GLSLUniformBlockObjectData data) {
		this.data = data;
	}

}
