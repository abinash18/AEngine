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

import org.lwjgl.opengl.GL45;

import net.abi.abisEngine.rendering.shader.compiler.AEGLInfo;

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
	public int size = -1;

	/**
	 * The attributes of this uniform such as its location, offset and others.
	 */
	private Map<Integer, Integer> attributes;

	/**
	 * Initializes the Uniform to the name.
	 */
	public GLUniform(String name) {
		this.name = name;
		attributes = new HashMap<>();
	}

	public void addAttribute(int attrib, int value) {
		attributes.put(attrib, value);
	}

	public int getAttribute(int attrib) {
		return attributes.get(attrib);
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("\nUniform Name: " + name + " Size: " + size + " \n");
		attributes.forEach((k, v) -> {
			if (k == GL45.GL_TYPE) {
				s.append("| " + AEGLInfo.spInternalF.get(k) + " : " + AEGLInfo.glslTypeToWord.get(v) + " |");
			} else {
				s.append("| " + AEGLInfo.spInternalF.get(k) + " : " + v + " |");
			}
		});
		return s.toString();// + super.toString();
	}
}
