package net.abi.abisEngine.rendering.shader;

import org.lwjgl.opengl.GL45;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.file.PathType;

public class AEShader {

	public static final PathHandle DEFAULT_SHADER_ASSET_DIRECTORY_PATH = new PathHandle("res/shaders/",
			PathType.Internal);

	public static enum AEShaderType {
		AE_VERTEX_SHADER(GL45.GL_VERTEX_SHADER), AE_FRAGMENT_SHADER(GL45.GL_FRAGMENT_SHADER),
		AE_GEOMETRY_SHADER(GL45.GL_GEOMETRY_SHADER), AE_TESSELATION_EVALUATION_SHADER(GL45.GL_TESS_EVALUATION_SHADER),
		AE_TESSELATION_CONTROL_SHADER(GL45.GL_TESS_CONTROL_SHADER), AE_COMPUTE_SHADER(0), AE_SHADER_IMPORT(0);

		public int glType;

		private AEShaderType(int glType) {
			this.glType = glType;
		}
	}

	public AEShader(AEShaderResource r) {

	}

}
