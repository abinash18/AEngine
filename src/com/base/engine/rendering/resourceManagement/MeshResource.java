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

	private int vbo, ibo, size, refCount;

	public MeshResource(int size) {
		this.vbo = GL15.glGenBuffers();
		this.ibo = GL15.glGenBuffers();
		this.size = size;
		this.refCount = 1;
	}

	@Deprecated
	public MeshResource() {
		this.vbo = GL15.glGenBuffers();
		this.ibo = GL15.glGenBuffers();
		this.size = 0;
	}

	@Override
	protected void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		GL15.glDeleteBuffers(vbo);
		GL15.glDeleteBuffers(ibo);

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

	@Deprecated
	public void setSize(int size) {
		this.size = size;
	}

}
