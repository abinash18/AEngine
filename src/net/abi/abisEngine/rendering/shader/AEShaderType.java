/**
 * 
 */
package net.abi.abisEngine.rendering.shader;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;

/**
 * @author abina
 *
 */
public enum AEShaderType {
	AE_VERTEX_SHADER(GL_VERTEX_SHADER), AE_FRAGMENT_SHADER(GL_FRAGMENT_SHADER), AE_GEOMETRY_SHADER(GL_GEOMETRY_SHADER),
	AE_TESSELATION_EVALUATION_SHADER(GL_TESS_EVALUATION_SHADER), AE_TESSELATION_CONTROL_SHADER(GL_TESS_CONTROL_SHADER),
	AE_COMPUTE_SHADER(0), AE_SHADER_IMPORT(0);

	public int glType;

	private AEShaderType(int glType) {
		this.glType = glType;
	}
}
