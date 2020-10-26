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
package net.abi.abisEngine.rendering.shader;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.asset.AssetI;
import net.abi.abisEngine.rendering.gl.memory.GLUniform;
import net.abi.abisEngine.rendering.gl.memory.GLUniformBuffer;
import net.abi.abisEngine.rendering.shader.compiler.AEShaderCompiler.ShaderSource;
import net.abi.abisEngine.rendering.window.GLFWWindowManager;

public class AEShaderResource implements AssetI {
	private static Logger logger = LogManager.getLogger(AEShaderResource.class);
	private int program, refCount;
	private String name;
	private PathHandle path;
	private HashMap<String, ShaderSource> subPrograms;
	private Map<String, GLUniform> uniforms;
	private Map<String, GLUniformBuffer> ubos;
	// The inputs and outputs on a shader program are not uniform but its just a
	// wrapper for properties.
	private Map<String, GLUniform> inputs;
	private Map<String, GLUniform> outputs;

	public AEShaderResource(String name, PathHandle pathToShaderDirectory) {
		this.path = pathToShaderDirectory;
		this.subPrograms = new HashMap<String, ShaderSource>();
		this.uniforms = new HashMap<String, GLUniform>();
		this.ubos = new HashMap<String, GLUniformBuffer>();
		this.inputs = new HashMap<String, GLUniform>();
		this.outputs = new HashMap<String, GLUniform>();
		this.refCount = 1;
		this.name = name;
	}

	public void createProgram() {
		this.program = GL20.glCreateProgram();
		if (program == 0) {
			System.out.println("Shader creation failed: Could not find valid memory location in constructor"
					+ new Exception().getStackTrace());
			System.out.println("Exiting...");
			GLFWWindowManager.raiseStopFlag();
		}
	}

	public Map<String, GLUniformBuffer> getUBOS() {
		return ubos;
	}

	public void setUbos(Map<String, GLUniformBuffer> ubos) {
		this.ubos = ubos;
	}

	public String getName() {
		return name;
	}

	public PathHandle getPath() {
		return path;
	}

	public void setPath(PathHandle path) {
		this.path = path;
	}

	public HashMap<String, ShaderSource> getSubPrograms() {
		return subPrograms;
	}

	public void setSubPrograms(HashMap<String, ShaderSource> subPrograms) {
		this.subPrograms = subPrograms;
	}

	public Map<String, GLUniform> getUniforms() {
		return uniforms;
	}

	public void setUniforms(Map<String, GLUniform> uniforms) {
		this.uniforms = uniforms;
	}

	public int getRefCount() {
		return refCount;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addShaderSource(ShaderSource source) {
		subPrograms.put(source.name, source);
	}

	public ShaderSource getShaderSource(String name) {
		return subPrograms.get(name);
	}

	public void incRef() {
		refCount++;
	}

	public void decRef() {
		refCount--;
	}

	public int getRefs() {
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

	@Override
	public void dispose() {
		GL15.glDeleteBuffers(program);
	}

	@Override
	public int incAndGetRef() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int decAndGetRef() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return the inputs
	 */
	public Map<String, GLUniform> getInputs() {
		return inputs;
	}

	/**
	 * @param inputs the inputs to set
	 */
	public void setInputs(Map<String, GLUniform> inputs) {
		this.inputs = inputs;
	}

	/**
	 * @return the outputs
	 */
	public Map<String, GLUniform> getOutputs() {
		return outputs;
	}

	/**
	 * @param outputs the outputs to set
	 */
	public void setOutputs(Map<String, GLUniform> outputs) {
		this.outputs = outputs;
	}

	/**
	 * @return the ubos
	 */
	public Map<String, GLUniformBuffer> getUbos() {
		return ubos;
	}

}
