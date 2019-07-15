/**
 * 
 */
package com.base.engine.rendering.resourceManagement;

import org.lwjgl.opengl.GL15;

/**
 * @author abinash
 *
 */
public class MeshResource {

	private int vbo, ibo;

	public MeshResource() {
		vbo = GL15.glGenBuffers();
		ibo = GL15.glGenBuffers();
	}

	@Override
	protected void finalize() {

		GL15.glDeleteBuffers(vbo);
		GL15.glDeleteBuffers(ibo);

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

}
