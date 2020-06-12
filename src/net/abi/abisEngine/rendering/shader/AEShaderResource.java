package net.abi.abisEngine.rendering.shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.asset.AssetI;
import net.abi.abisEngine.rendering.shader.AEShader.GLSLUniform;
import net.abi.abisEngine.rendering.shader.compiler.AEShaderCompiler.ShaderSource;
import net.abi.abisEngine.rendering.window.GLFWWindowManager;

public class AEShaderResource implements AssetI {
	private static Logger logger = LogManager.getLogger(AEShaderResource.class);
	public int program, refCount;
	public String name;
	public PathHandle path;
	public HashMap<String, ShaderSource> subPrograms;

	public Map<String, GLSLUniform> uniforms;

	public AEShaderResource(String name, PathHandle pathToShaderDirectory) {
		this.path = pathToShaderDirectory;
		this.subPrograms = new HashMap<String, ShaderSource>();
		this.program = GL20.glCreateProgram();

		if (program == 0) {
			logger.error("Shader creation failed: Could not find valid memory location in constructor",
					new Exception());
			logger.info("Exiting...");
			GLFWWindowManager.raiseStopFlag();
		}

		uniforms = new HashMap<String, GLSLUniform>();

		this.refCount = 1;
		this.name = name;
	}

	public String getName() {
		return name;
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

}
