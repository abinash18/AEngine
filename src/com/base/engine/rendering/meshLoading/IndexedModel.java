package com.base.engine.rendering.meshLoading;

import java.util.ArrayList;

import com.base.engine.math.Vector2f;
import com.base.engine.math.Vector3f;

public class IndexedModel {

	private ArrayList<Vector3f> positions, normals;
	private ArrayList<Vector2f> texCoords;
	private ArrayList<Integer> indices;

	public IndexedModel() {
		positions = new ArrayList<Vector3f>();
		normals = new ArrayList<Vector3f>();
		texCoords = new ArrayList<Vector2f>();
		indices = new ArrayList<Integer>();
	}

	public void calcNormals() {

		for (int i = 0; i < indices.size(); i += 3) {
			int i0 = indices.get(i);
			int i1 = indices.get(i + 1);
			int i2 = indices.get(i + 2);

			Vector3f v1 = positions.get(i1).sub(positions.get(i0));
			Vector3f v2 = positions.get(i2).sub(positions.get(i0));

			Vector3f normal = v1.cross(v2).normalize();

			normals.get(i0).set(normals.get(i0).add(normal));
			normals.get(i1).set(normals.get(i1).add(normal));
			normals.get(i2).set(normals.get(i2).add(normal));

		}

		for (int i = 0; i < normals.size(); i++) {
			normals.get(i).set(normals.get(i).normalize());
		}

	}

	public ArrayList<Vector3f> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<Vector3f> positions) {
		this.positions = positions;
	}

	public ArrayList<Vector3f> getNormals() {
		return normals;
	}

	public void setNormals(ArrayList<Vector3f> normals) {
		this.normals = normals;
	}

	public ArrayList<Vector2f> getTexCoords() {
		return texCoords;
	}

	public void setTexCoords(ArrayList<Vector2f> texCoords) {
		this.texCoords = texCoords;
	}

	public ArrayList<Integer> getIndices() {
		return indices;
	}

	public void setIndices(ArrayList<Integer> indices) {
		this.indices = indices;
	}

}
