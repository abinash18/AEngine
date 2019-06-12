package com.base.engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Util {

	public static FloatBuffer CreateFloatBuffer(int size) {
		return (BufferUtils.createFloatBuffer(size));
	}

	public static FloatBuffer CreateFlippedBuffer(Vertex[] vertices) {
		FloatBuffer buffer = CreateFloatBuffer(vertices.length * Vertex.SIZE);

		for (int i = 0; i < vertices.length; i++) {
			buffer.put(vertices[i].getPos().getX());
			buffer.put(vertices[i].getPos().getY());
			buffer.put(vertices[i].getPos().getZ());
		}

		buffer.flip();

		return (buffer);

	}

}
