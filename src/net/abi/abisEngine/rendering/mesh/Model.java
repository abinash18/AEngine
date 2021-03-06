/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.rendering.mesh;

import java.util.ArrayList;

import net.abi.abisEngine.math.vector.Vector2f;
import net.abi.abisEngine.math.vector.Vector3f;

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
