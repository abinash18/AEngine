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
package net.abi.abisEngine.rendering.gl.memory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Abinash Singh
 *
 */
public class GLUniform {

	/**
	 * The defined name of this uniform in GLSL.
	 */
	public String name;

	/**
	 * The size of this uniform variable if in an array the array stride property
	 * will be non 0, and if this variable is a Matrix then the Matrix Stride will
	 * be non 0.
	 */
	public int size = 0;

	/**
	 * The attributes of this uniform such as its location, offset and others.
	 */
	private Map<Integer, Integer> attributes;

	/**
	 * Initializes the Uniform to the name.
	 */
	public GLUniform(String name) {
		attributes = new HashMap<>();
	}

	public void addAttribute(int attrib, int value) {
		attributes.put(attrib, value);
	}

	public void getAttribute(int attrib, int value) {
		attributes.get(attrib);
	}

	public void setSize(int size) {
		this.size = size;
	}

//	/**
//	 * The raw GLSL type integer
//	 */
//	public int type = -1;
//
//	/**
//	 * The location this uniform is bound in on the shader program if -1 then not
//	 * bound.
//	 */
//	public int location = -1;
//

//
//	/**
//	 * If the current uniform is a Mat then this will be its stride otherwise it is
//	 * 0 by default.
//	 */
//	public int matrixStride = 0;
//
//	/**
//	 * If the current uniform is a Array then this will be its stride otherwise it
//	 * is 0.
//	 */
//	public int arrayStride = 0;
//	/**
//	 * 
//	 */
//	public int offset = 0;
}
