package com.base.engine.rendering.resourceManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import com.base.engine.handlers.logging.LogManager;
import com.base.engine.handlers.logging.Logger;

public class ShaderResource {
	private int program, refCount;

	private static Logger logger = LogManager.getLogger(ShaderResource.class.getName());

	private Map<String, Integer> uniforms;
	private List<String> uniformNames;
	private List<String> uniformTypes;

	public ShaderResource() {
		this.program = GL20.glCreateProgram();

		if (program == 0) {
			// System.err.println("Shader creation failed: Could not find valid memory
			// location in constructor");
			logger.error("Shader creation failed: Could not find valid memory location in constructor",
					new Exception());
			logger.info("Exiting...");
			System.exit(1);
		}

		uniforms = new HashMap<String, Integer>();
		uniformNames = new ArrayList<>();
		uniformTypes = new ArrayList<>();

		this.refCount = 1;
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

	public void addReference() {
		refCount++;
	}

	public boolean removeRefrence() {
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

}
