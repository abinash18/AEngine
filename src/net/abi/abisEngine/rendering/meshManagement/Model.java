package net.abi.abisEngine.rendering.meshManagement;

import java.util.ArrayList;

import net.abi.abisEngine.math.Vector2f;
import net.abi.abisEngine.math.Vector3f;

public class Model {
	private ArrayList<Vector3f> positions, normals, tangents, vBCC;
	private ArrayList<Vector2f> texCoords;
	private ArrayList<Integer> indices;

	/**
	 * @param positions
	 * @param normals
	 * @param tangents
	 * @param texCoords
	 * @param indices
	 */
	public Model(ArrayList<Vector3f> positions, ArrayList<Vector3f> normals, ArrayList<Vector2f> texCoords,
			ArrayList<Vector3f> tangents, ArrayList<Integer> indices) {
		this.positions = positions;
		this.normals = normals;
		this.tangents = tangents;
		this.texCoords = texCoords;
		this.indices = indices;
	}

	/**
	 * 
	 * @param positions
	 * @param normals
	 * @param texCoords
	 * @param tangents
	 * @param indices
	 * @param vBCC      Bary Centric Coordinates.
	 */
	public Model(ArrayList<Vector3f> positions, ArrayList<Vector3f> normals, ArrayList<Vector2f> texCoords,
			ArrayList<Vector3f> tangents, ArrayList<Integer> indices, ArrayList<Vector3f> vBCC) {
		this.positions = positions;
		this.normals = normals;
		this.tangents = tangents;
		this.texCoords = texCoords;
		this.indices = indices;
		this.vBCC = vBCC;
	}

	public boolean isValid() {
		return (positions.size() == texCoords.size() && texCoords.size() == normals.size()
				&& normals.size() == tangents.size());
	}

	public ArrayList<Vector3f> getPositions() {
		return positions;
	}

	public ArrayList<Vector3f> getNormals() {
		return normals;
	}

	public ArrayList<Vector3f> getTangents() {
		return tangents;
	}

	public ArrayList<Vector2f> getTexCoords() {
		return texCoords;
	}

	public ArrayList<Integer> getIndices() {
		return indices;
	}

	public ArrayList<Vector3f> getvBCC() {		
		return vBCC;
	}

}
