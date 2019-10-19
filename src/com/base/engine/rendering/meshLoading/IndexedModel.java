package com.base.engine.rendering.meshLoading;

import java.util.ArrayList;

import com.base.engine.math.Vector2f;
import com.base.engine.math.Vector3f;

public class IndexedModel {

	private ArrayList<Vector3f> positions, normals, tangents;
	private ArrayList<Vector2f> texCoords;
	private ArrayList<Integer> indices;

	public IndexedModel() {
		positions = new ArrayList<Vector3f>();
		normals = new ArrayList<Vector3f>();
		texCoords = new ArrayList<Vector2f>();
		indices = new ArrayList<Integer>();
		tangents = new ArrayList<Vector3f>();
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

	public void calcTangents() {
		for (int i = 0; i < indices.size(); i += 3) {
			int i0 = indices.get(i);
			int i1 = indices.get(i + 1);
			int i2 = indices.get(i + 2);

			Vector3f edge1 = positions.get(i1).sub(positions.get(i0));
			Vector3f edge2 = positions.get(i2).sub(positions.get(i0));

			float deltaU1 = texCoords.get(i1).getX() - texCoords.get(i0).getX();
			float deltaV1 = texCoords.get(i1).getY() - texCoords.get(i0).getY();
			float deltaU2 = texCoords.get(i2).getX() - texCoords.get(i0).getX();
			float deltaV2 = texCoords.get(i2).getY() - texCoords.get(i0).getY();

			float dividend = (deltaU1 * deltaV2 - deltaU2 * deltaV1);
			float f = dividend == 0 ? 0.0f : 1.0f / dividend;
			Vector3f tangent = new Vector3f(0, 0, 0);

			tangent.setX(f * (deltaV2 * edge1.getX() - deltaV1 * edge2.getX()));
			tangent.setY(f * (deltaV2 * edge1.getY() - deltaV1 * edge2.getY()));
			tangent.setZ(f * (deltaV2 * edge1.getZ() - deltaV1 * edge2.getZ()));

			tangents.get(i0).set(tangents.get(i0).add(tangent));
			tangents.get(i1).set(tangents.get(i1).add(tangent));
			tangents.get(i2).set(tangents.get(i2).add(tangent));

		}

		for (int i = 0; i < tangents.size(); i++) {
			tangents.get(i).set(tangents.get(i).normalize());
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

	public ArrayList<Vector3f> getTangents() {
		return tangents;
	}

	public void setTangents(ArrayList<Vector3f> tangents) {
		this.tangents = tangents;
	}

}
