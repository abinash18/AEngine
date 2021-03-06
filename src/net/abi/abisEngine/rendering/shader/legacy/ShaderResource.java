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
package net.abi.abisEngine.rendering.shader.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.shader.legacy.Shader.ShaderSource;
import net.abi.abisEngine.rendering.shader.legacy.Shader.ShaderType;
import net.abi.abisEngine.rendering.window.GLFWWindowManager;

public class ShaderResource {
	private int program, refCount;
	private String name;
	private PathHandle path;
	private HashMap<ShaderType, ShaderSource> subPrograms = new HashMap<ShaderType, ShaderSource>();
	// vertexShaderName, fragmentShaderName, vertexShaderText,
	// fragmentShaderText;

	private static Logger logger = LogManager.getLogger(ShaderResource.class);

	private Map<String, Integer> uniforms;
	private List<String> uniformNames;
	private List<String> uniformTypes;

	public ShaderResource(String name, PathHandle pathToShaderDirectory) {
		this.path = pathToShaderDirectory;

		this.program = GL20.glCreateProgram();

		if (program == 0) {
			logger.error("Shader creation failed: Could not find valid memory location in constructor",
					new Exception());
			logger.info("Exiting...");
			GLFWWindowManager.raiseStopFlag();
		}

		uniforms = new HashMap<String, Integer>();
		uniformNames = new ArrayList<>();
		uniformTypes = new ArrayList<>();

		this.refCount = 1;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	protected void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
			logger.error("Unable to finalize.", e);
			// e.printStackTrace();
		}
		GL15.glDeleteBuffers(program);

	}

	public void addShaderSource(ShaderType type, ShaderSource source) {
		subPrograms.put(type, source);
	}

	public ShaderSource getShaderSource(String name) {
		return subPrograms.get(name);
	}

	public void incRefs() {
		refCount++;
	}

	public boolean decRefs() {
		refCount--;
		return refCount == 0;
	}

	public int getRefCount() {
		return refCount;
	}

	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}

	public int getProgram() {
		return program;
	}

	public void setProgram(int program) {
		this.program = program;
	}

	public HashMap<String, Integer> getUniforms() {
		return (HashMap<String, Integer>) uniforms;
	}

	public void setUniforms(Map<String, Integer> uniforms) {
		this.uniforms = uniforms;
	}

	public List<String> getUniformNames() {
		return uniformNames;
	}

	public void setUniformNames(List<String> uniformNames) {
		this.uniformNames = uniformNames;
	}

	public List<String> getUniformTypes() {
		return uniformTypes;
	}

	public void setUniformTypes(List<String> uniformTypes) {
		this.uniformTypes = uniformTypes;
	}

	public HashMap<ShaderType, ShaderSource> getSubPrograms() {
		return subPrograms;
	}

}
