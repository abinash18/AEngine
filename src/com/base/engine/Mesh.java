package com.base.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Mesh {

	private int vbo, size;
	
	public Mesh() {
		vbo = glGenBuffers();
		size = 0;
	}
	
	public void addVertices(Vertex[] vertices) {
		
		size = vertices.length * Vertex.SIZE;
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
	}
	
}
