package com.base.engine.rendering.resourceManagement;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.base.engine.handlers.logging.LogManager;
import com.base.engine.handlers.logging.Logger;

public class TextureResource {
	private int id, refCount;

	private static Logger logger = LogManager.getLogger(TextureResource.class.getName());

	public TextureResource() {
		this.id = GL11.glGenTextures();
		this.refCount = 1;
	}

	@Override
	protected void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
			logger.error("Unable to finalize.", e);
			// e.printStackTrace();
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
