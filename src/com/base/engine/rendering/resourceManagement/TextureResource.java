package com.base.engine.rendering.resourceManagement;

import org.lwjgl.opengl.GL15;

public class TextureResource {
	private int id, refCount;

	public TextureResource(int id) {
		this.id = id;
		this.refCount = 1;
	}

	@Override
	protected void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		GL15.glDeleteBuffers(id);

	}

	public void addReference() {
		refCount++;
	}

	public boolean removeRefrence() {
		refCount--;
		return refCount == 0;
	}

	public int getRefCount() {
		return refCount;
	}

	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
