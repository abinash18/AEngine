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

import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;

import net.abi.abisEngine.rendering.window.GLFWWindowManager.GLFWWindowContext;

/**
 * Uniform Buffer Object.
 * 
 * The class representing a Uniform block in GL memory.
 * 
 * NOTE: A UBO should be created before the shader is compiled.
 * 
 * TODO: Have a UBO in multiple contexts represented by only one of these
 * classes. By having a TwoFactorCache to store GLContexts.
 * 
 * @author Abinash Singh
 */
public class GLUniformBuffer extends GLBuffer {

	public static Map<String, GLUniformBuffer> GlobalUniformBuffers = new HashMap<>();

	private GLFWWindowContext context;

	/**
	 * Index of the binding point for this buffer.
	 */
	private int binding = NULL_BOUND_BUFFER_OBJECT;

	/**
	 * The defined name of the block. Not the instance name, there is no use for it
	 * outside the shader code.
	 */
	private String name;

	/**
	 * A map of the uniforms in this Block, mapped to their names.
	 */
	private Map<String, GLUniform> members;

	/**
	 * Creates a {@link GLBuffer} of type Uniform Buffer.
	 * 
	 * @param usage
	 * @param size
	 */
	public GLUniformBuffer(String name, int usage, int size) {
		super(GL31.GL_UNIFORM_BUFFER, usage, size);
		this.members = new HashMap<>();
		this.name = name;
	}

	/**
	 * Creates a {@link GLBuffer} of type Uniform Buffer. With GL_DYNAMIC_DRAW
	 * usage.
	 * 
	 * @param size
	 */
	public GLUniformBuffer(String name, int size) {
		super(GL31.GL_UNIFORM_BUFFER, GL31.GL_DYNAMIC_DRAW, size);
		this.members = new HashMap<>();
		this.name = name;
	}

	public GLUniform getUniform(String name) {
		return members.get(name);
	}

	/**
	 * Adds a empty uniform mapping to the name provided and initializes the uniform
	 * with he name parameter, with no associated type, size, offset, alignment or
	 * any other attribute. And as the shader is compiled the uniform will be auto
	 * filled. If you would like to define a uniform explicitly see
	 * {@link GLUniformBuffer#addUniform(String, GLUniform)}.
	 * 
	 * @param name
	 */
	public void addUniform(String name) {
		members.put(name, new GLUniform(name));
	}

	/**
	 * Adds a uniform to the uniform map. This will add a uniform to the map but the
	 * engine will not overwrite the contents instead it will find discrepancies and
	 * throw a warning. If there are gaps such as uniform indices which cannot be
	 * determined pre runtime, they will be filled by the compiler at runtime.
	 * 
	 * @param name
	 * @param uniform
	 */
	public void addUniform(String name, GLUniform uniform) {
		members.put(name, uniform);
	}

	/**
	 * Binds a buffer object to an indexed buffer target. NOTE: Requires for the
	 * buffer to be valid and created. This function only applies to one of:<br>
	 * <table>
	 * <tr>
	 * <td>{@link GL30C#GL_TRANSFORM_FEEDBACK_BUFFER TRANSFORM_FEEDBACK_BUFFER}</td>
	 * <td>{@link GL31#GL_UNIFORM_BUFFER UNIFORM_BUFFER}</td>
	 * <td>{@link GL42#GL_ATOMIC_COUNTER_BUFFER ATOMIC_COUNTER_BUFFER}</td>
	 * <td>{@link GL43#GL_SHADER_STORAGE_BUFFER SHADER_STORAGE_BUFFER}</td>
	 * </tr>
	 * </table>
	 * 
	 * @param binding The binding location for the buffer in the GL context.
	 */
	public void bindBufferBase(int binding) {
		if (getType() == GL45.GL_TRANSFORM_FEEDBACK_BUFFER || getType() == GL45.GL_UNIFORM_BUFFER
				|| getType() == GL45.GL_ATOMIC_COUNTER_BUFFER || getType() == GL45.GL_SHADER_STORAGE_BUFFER) {
			if (getBufferID() != NULL_BOUND_BUFFER_OBJECT) {
				GL31.glBindBufferBase(getType(), binding, getBufferID());
				this.binding = binding;
			}
		}
	}

	/**
	 * @return the context
	 */
	public GLFWWindowContext getContext() {
		return context;
	}

	/**
	 * @return the binding
	 */
	public int getBinding() {
		return binding;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param binding the binding to set
	 */
	public void setBinding(int binding) {
		this.binding = binding;
	}

	/**
	 * @return the members
	 */
	public Map<String, GLUniform> getMembers() {
		return members;
	}

	@Override
	public void dispose() {
		binding = NULL_BOUND_BUFFER_OBJECT;
		super.dispose();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(super.toString() + " Uniform Buffer Name: " + name + " Binding: " + binding + " ");
		members.forEach((k, v) -> {
			s.append(v);
		});
		return s.toString();
	}

}