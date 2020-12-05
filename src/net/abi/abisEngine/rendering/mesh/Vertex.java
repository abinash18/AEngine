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

import net.abi.abisEngine.math.vector.Vector2f;
import net.abi.abisEngine.math.vector.Vector3f;

public class Vertex {
	public static final int SIZE = 12;

	private Vector3f pos, normal, tangent;
	private Vector2f texCoord;

	public Vertex(Vector3f pos) {
		this(pos, new Vector2f(0, 0));
	}

	public Vertex(Vector3f pos, Vector2f texCoord) {
		this(pos, texCoord, new Vector3f(0, 0, 0));
	}

	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal) {
		this(pos, texCoord, normal, new Vector3f(0, 0, 0));
	}

	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal, Vector3f tangent) {
		this.pos = pos;
		this.texCoord = texCoord;
		this.normal = normal;
		this.tangent = tangent;
	}

	public Vector2f getTexCoord() {
		return texCoord;
	}

	public void setTexCoord(Vector2f texCoord) {
		this.texCoord = texCoord;
	}

	public static int getSize() {
		return SIZE;
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}

	public Vector3f getTangent() {
		return tangent;
	}

	public void setTangent(Vector3f tangent) {
		this.tangent = tangent;
	}

}
