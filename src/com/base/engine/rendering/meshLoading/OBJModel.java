package com.base.engine.rendering.meshLoading;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.base.engine.core.Util;
import com.base.engine.math.Vector2f;
import com.base.engine.math.Vector3f;

public class OBJModel {

	private ArrayList<Vector3f> positions, normals;
	private ArrayList<Vector2f> texCoords;
	private ArrayList<OBJIndex> indices;
	private boolean hasTexCoords, hasNormals;

	public OBJModel(String fileName) {
		positions = new ArrayList<Vector3f>();
		normals = new ArrayList<Vector3f>();
		texCoords = new ArrayList<Vector2f>();
		indices = new ArrayList<OBJIndex>();

		hasTexCoords = false;
		hasNormals = false;

		BufferedReader meshReader = null;

		try {
			meshReader = new BufferedReader(new FileReader(fileName));
			String line;

			while ((line = meshReader.readLine()) != null) {

				String[] tokens = line.split(" ");

				tokens = Util.removeEmptyStrings(tokens);

				if (tokens.length == 0 || tokens[0].equals("#")) {
					continue;
				} else if (tokens[0].equals("v")) {
					positions.add(
							new Vector3f(Float.valueOf(tokens[1]), Float.valueOf(tokens[2]), Float.valueOf(tokens[3])));

				} else if (tokens[0].equals("vt")) {
					texCoords.add(new Vector2f(Float.valueOf(tokens[1]), Float.valueOf(tokens[2])));
				} else if (tokens[0].equals("vn")) {
					normals.add(
							new Vector3f(Float.valueOf(tokens[1]), Float.valueOf(tokens[2]), Float.valueOf(tokens[3])));

				} else if (tokens[0].equals("f")) {

					for (int i = 0; i < tokens.length - 3; i++) {
						indices.add(parseOBJIndex(tokens[1]));
						indices.add(parseOBJIndex(tokens[2 + i]));
						indices.add(parseOBJIndex(tokens[3 + i]));
					}
				}
			}

			meshReader.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public IndexedModel toIndexedModel() {
		IndexedModel result = new IndexedModel();
		IndexedModel normalModel = new IndexedModel();
		HashMap<OBJIndex, Integer> resultIndexMap = new HashMap<OBJIndex, Integer>();
		HashMap<Integer, Integer> normalIndexMap = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> indexMap = new HashMap<Integer, Integer>();

		for (int i = 0; i < indices.size(); i++) {
			OBJIndex currentIndex = indices.get(i);

			Vector3f currentPosition = positions.get(currentIndex.vertexIndex);
			Vector2f currentTexCoord;
			Vector3f currentNormal;

			if (hasTexCoords) {
				currentTexCoord = texCoords.get(currentIndex.texCoordIndex);
			} else {
				currentTexCoord = new Vector2f(0, 0);
			}

			if (hasNormals) {
				currentNormal = normals.get(currentIndex.normalIndex);
			} else {
				currentNormal = new Vector3f(0, 0, 0);
			}

			// int modelVertexIndex = -1;

			Integer modelVertexIndex = resultIndexMap.get(currentIndex);

			if (modelVertexIndex == null) {

				// The result positions size is the same as the current Vertex index counter.
				modelVertexIndex = result.getPositions().size();
				resultIndexMap.put(currentIndex, modelVertexIndex);

				result.getPositions().add(currentPosition);
				result.getTexCoords().add(currentTexCoord);
				if (hasNormals) {
					result.getNormals().add(currentNormal);
				}
				result.getTangents().add(new Vector3f(0, 0, 0));
			}

			Integer normalModelIndex = normalIndexMap.get(currentIndex.vertexIndex);

			if (normalModelIndex == null) {
				// The result positions size is the same as the current Vertex index counter.
				normalModelIndex = normalModel.getPositions().size();
				normalIndexMap.put(currentIndex.vertexIndex, normalModelIndex);

				normalModel.getPositions().add(currentPosition);
				normalModel.getTexCoords().add(currentTexCoord);
				normalModel.getNormals().add(currentNormal);
				normalModel.getTangents().add(new Vector3f(0, 0, 0));
			}

			result.getIndices().add(modelVertexIndex);
			normalModel.getIndices().add(normalModelIndex);
			indexMap.put(modelVertexIndex, normalModelIndex);
		}

		if (!hasNormals) {
			normalModel.calcNormals();
			for (int i = 0; i < result.getPositions().size(); i++) {
				result.getNormals().add(normalModel.getNormals().get(indexMap.get(i)));
			}
		}

		normalModel.calcTangents();
		for (int i = 0; i < result.getPositions().size(); i++) {
			result.getTangents().add(normalModel.getTangents().get(indexMap.get(i)));
		}

		return result;
	}

	private OBJIndex parseOBJIndex(String token) {
		String[] values = token.split("/");

		OBJIndex result = new OBJIndex();
		result.vertexIndex = Integer.parseInt(values[0]) - 1;
		if (values.length > 1) {
			if (!values[1].isEmpty()) {
				hasTexCoords = true;
				result.texCoordIndex = Integer.parseInt(values[1]) - 1;
			}
			if (values.length > 2) {
				hasNormals = true;
				result.normalIndex = Integer.parseInt(values[2]) - 1;
			}
		}

		return result;
	}

}
