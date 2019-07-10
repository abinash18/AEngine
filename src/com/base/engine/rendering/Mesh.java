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

import com.base.engine.core.Util;
import com.base.engine.math.Vector3f;
import com.base.engine.rendering.meshLoading.IndexedModel;
import com.base.engine.rendering.meshLoading.OBJModel;

public class Mesh {
	private int vbo, size, ibo;

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
		vbo = glGenBuffers();
		ibo = glGenBuffers();
		size = 0;
	}

	private void addVertices(Vertex[] vertices, int[] indices, boolean calcNormals) {

		if (calcNormals) {
			calcNormals(vertices, indices);
		}

		size = indices.length;

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(vertices), GL_STATIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL_STATIC_DRAW);

	}

	public void draw() {
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);

		glBindBuffer(GL_ARRAY_BUFFER, vbo);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

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

//		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
//		ArrayList<Integer> indices = new ArrayList<Integer>();
//
//		// StringBuilder shaderSource = new StringBuilder();
//		BufferedReader meshReader = null;
//
//		try {
//			meshReader = new BufferedReader(new FileReader("./res/models/" + fileName));
//			String line;
//
//			while ((line = meshReader.readLine()) != null) {
//
//				String[] tokens = line.split(" ");
//
//				tokens = Util.removeEmptyStrings(tokens);
//
//				if (tokens.length == 0 || tokens[0].equals("#")) {
//					continue;
//				} else if (tokens[0].equals("v")) {
//					vertices.add(new Vertex(new Vector3f(Float.valueOf(tokens[1]), Float.valueOf(tokens[2]),
//							Float.valueOf(tokens[3]))));
//
//				} else if (tokens[0].equals("f")) {
//					indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
//					indices.add(Integer.parseInt(tokens[2].split("/")[0]) - 1);
//					indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
//
//					if (tokens.length > 4) {
//						indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
//						indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
//						indices.add(Integer.parseInt(tokens[4].split("/")[0]) - 1);
//					}
//
//				}
//
//			}
//
//			meshReader.close();
//
//			Vertex[] vertexData = new Vertex[vertices.size()];
//			vertices.toArray(vertexData);
//
//			Integer[] indicesData = new Integer[indices.size()];
//			indices.toArray(indicesData);
//
//			addVertices(vertexData, Util.toIntArray(indicesData), calcNormals);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(1);
//		}

		return null;

	}

}
