package com.base.engine.rendering;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.util.ArrayList;

import com.base.engine.components.MeshRenderer;
import com.base.engine.core.Util;
import com.base.engine.math.Vector3f;
import com.base.engine.rendering.meshLoading.IndexedModel;
import com.base.engine.rendering.meshLoading.OBJModel;
import com.base.engine.rendering.resourceManagement.MeshResource;

public class Mesh {
	private MeshResource meshBuffers;
	private int size;

	public Mesh(String fileName, boolean calcNormals) {
		initMeshData();
		loadMesh(fileName, calcNormals);
	}

	public Mesh(Vertex[] vertices, int[] indices) {
		this(vertices, indices, false);
	}

	public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals) {
		initMeshData();
		addVertices(vertices, indices, calcNormals);
	}

	private void initMeshData() {
		meshBuffers = new MeshResource();
		size = 0;
	}

	private void addVertices(Vertex[] vertices, int[] indices, boolean calcNormals) {

		if (calcNormals) {
			calcNormals(vertices, indices);
		}

		size = indices.length;

		glBindBuffer(GL_ARRAY_BUFFER, meshBuffers.getVbo());
		glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(vertices), GL_STATIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, meshBuffers.getIbo());
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL_STATIC_DRAW);

	}

	public void draw() {
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);

		glBindBuffer(GL_ARRAY_BUFFER, meshBuffers.getVbo());

		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, meshBuffers.getIbo());

		glDrawElements(GL_TRIANGLES, size, GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
	}

	private void calcNormals(Vertex[] vertices, int[] indices) {

		for (int i = 0; i < indices.length; i += 3) {
			int i0 = indices[i];
			int i1 = indices[i + 1];
			int i2 = indices[i + 2];

			Vector3f v1 = vertices[i1].getPos().sub(vertices[i0].getPos());
			Vector3f v2 = vertices[i2].getPos().sub(vertices[i0].getPos());

			Vector3f normal = v1.cross(v2).normalize();

			vertices[i0].setNormal(vertices[i0].getNormal().add(normal));
			vertices[i1].setNormal(vertices[i1].getNormal().add(normal));
			vertices[i2].setNormal(vertices[i2].getNormal().add(normal));

		}

		for (int i = 0; i < vertices.length; i++) {
			vertices[i].setNormal(vertices[i].getNormal().normalize());
		}

	}

	private Mesh loadMesh(String fileName, boolean calcNormals) {

		String[] splitArray = fileName.split("\\.");

		String extenstion = splitArray[splitArray.length - 1];

		if (!extenstion.equals("obj")) {
			System.err.println("File Format Not Supported For Mesh Loading " + fileName);
			new Exception().printStackTrace();
			System.exit(1);
		}

		OBJModel test = new OBJModel("./res/models/" + fileName);
		IndexedModel model = test.toIndexedModel();

		if (calcNormals) {
			model.calcNormals();
		}
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();

		for (int i = 0; i < model.getPositions().size(); i++) {
			vertices.add(
					new Vertex(model.getPositions().get(i), model.getTexCoords().get(i), model.getNormals().get(i)));
		}

		Vertex[] vertexData = new Vertex[vertices.size()];
		vertices.toArray(vertexData);

		Integer[] indicesData = new Integer[model.getIndices().size()];
		model.getIndices().toArray(indicesData);

		addVertices(vertexData, Util.toIntArray(indicesData), calcNormals);

		return null;

	}

}
