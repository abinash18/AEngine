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

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1fv;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform1iv;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform2fv;
import static org.lwjgl.opengl.GL20.glUniform2i;
import static org.lwjgl.opengl.GL20.glUniform2iv;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniform3i;
import static org.lwjgl.opengl.GL20.glUniform3iv;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniform4fv;
import static org.lwjgl.opengl.GL20.glUniform4i;
import static org.lwjgl.opengl.GL20.glUniform4iv;
import static org.lwjgl.opengl.GL20.glUniformMatrix2fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL21.glUniformMatrix2x3fv;
import static org.lwjgl.opengl.GL21.glUniformMatrix2x4fv;
import static org.lwjgl.opengl.GL21.glUniformMatrix3x2fv;
import static org.lwjgl.opengl.GL21.glUniformMatrix3x4fv;
import static org.lwjgl.opengl.GL21.glUniformMatrix4x2fv;
import static org.lwjgl.opengl.GL21.glUniformMatrix4x3fv;
import static org.lwjgl.opengl.GL30.glUniform1ui;
import static org.lwjgl.opengl.GL30.glUniform1uiv;
import static org.lwjgl.opengl.GL30.glUniform2ui;
import static org.lwjgl.opengl.GL30.glUniform2uiv;
import static org.lwjgl.opengl.GL30.glUniform3ui;
import static org.lwjgl.opengl.GL30.glUniform3uiv;
import static org.lwjgl.opengl.GL30.glUniform4ui;
import static org.lwjgl.opengl.GL30.glUniform4uiv;
import static org.lwjgl.opengl.GL40.glUniform1d;
import static org.lwjgl.opengl.GL40.glUniform1dv;
import static org.lwjgl.opengl.GL40.glUniform2d;
import static org.lwjgl.opengl.GL40.glUniform2dv;
import static org.lwjgl.opengl.GL40.glUniform3d;
import static org.lwjgl.opengl.GL40.glUniform3dv;
import static org.lwjgl.opengl.GL40.glUniform4d;
import static org.lwjgl.opengl.GL40.glUniform4dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix2dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix2x3dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix2x4dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix3dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix3x2dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix3x4dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix4dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix4x2dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix4x3dv;
import static org.lwjgl.opengl.GL43.GL_LOCATION;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.file.PathType;

public class AEShader {

	public static final PathHandle DEFAULT_SHADER_ASSET_DIRECTORY_PATH = new PathHandle("res/shaders/",
			PathType.Internal);

	private AEShaderResource resource;

	public AEShader(AEShaderResource r) {
		this.resource = r;
	}

	public void setUniform1d(String n, double v) {
		glUniform1d(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1dv(String n, double[] v) {
		glUniform1dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1dv(String n, DoubleBuffer v) {
		glUniform1dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1f(String n, float v) {
		glUniform1f(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1fv(String n, float[] v) {
		glUniform1fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1fv(String n, FloatBuffer v) {
		glUniform1fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1i(String n, int v) {
		glUniform1i(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1iv(String n, int[] v) {
		glUniform1iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1iv(String n, IntBuffer v) {
		glUniform1iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1ui(String n, int v) {
		glUniform1ui(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1uiv(String n, int[] v) {
		glUniform1uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1uiv(String n, IntBuffer v) {
		glUniform1uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	/*
	 * 2 component uniforms
	 */

	public void setUniform2d(String n, double v0, double v1) {
		glUniform2d(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1);
	}

	public void setUniform2dv(String n, double[] v0) {
		glUniform2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0);
	}

	public void setUniform2dv(String n, DoubleBuffer v) {
		glUniform2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform2f(String n, float v0, float v1) {
		glUniform2f(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1);
	}

	public void setUniform2fv(String n, float[] v) {
		glUniform2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform2fv(String n, FloatBuffer v) {
		glUniform2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform2i(String n, int v0, int v1) {
		glUniform2i(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1);
	}

	public void setUniform2iv(String n, int[] v) {
		glUniform2iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform2iv(String n, IntBuffer v) {
		glUniform2iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform2ui(String n, int v0, int v1) {
		glUniform2ui(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1);
	}

	public void setUniform2uiv(String n, int[] v) {
		glUniform2uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform2uiv(String n, IntBuffer v) {
		glUniform2uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	/*
	 * 3 component uniform
	 */

	public void setUniform3d(String n, double v0, double v1, double v2) {
		glUniform3d(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2);
	}

	public void setUniform3dv(String n, double[] v0) {
		glUniform3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0);
	}

	public void setUniform3dv(String n, DoubleBuffer v) {
		glUniform3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform3f(String n, float v0, float v1, float v2) {
		glUniform3f(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2);
	}

	public void setUniform3fv(String n, float[] v) {
		glUniform3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform3fv(String n, FloatBuffer v) {
		glUniform3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform3i(String n, int v0, int v1, int v2) {
		glUniform3i(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2);
	}

	public void setUniform3iv(String n, int[] v) {
		glUniform3iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform3iv(String n, IntBuffer v) {
		glUniform3iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform3ui(String n, int v0, int v1, int v2) {
		glUniform3ui(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2);
	}

	public void setUniform3uiv(String n, int[] v) {
		glUniform3uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform3uiv(String n, IntBuffer v) {
		glUniform3uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	/*
	 * 4 component uniform
	 */

	public void setUniform4d(String n, double v0, double v1, double v2, double v3) {
		glUniform4d(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2, v3);
	}

	public void setUniform4dv(String n, double[] v0) {
		glUniform4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0);
	}

	public void setUniform4dv(String n, DoubleBuffer v) {
		glUniform4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform4f(String n, float v0, float v1, float v2, float v3) {
		glUniform4f(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2, v3);
	}

	public void setUniform4fv(String n, float[] v) {
		glUniform4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform4fv(String n, FloatBuffer v) {
		glUniform4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform4i(String n, int v0, int v1, int v2, int v3) {
		glUniform4i(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2, v3);
	}

	public void setUniform4iv(String n, int[] v) {
		glUniform4iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform4iv(String n, IntBuffer v) {
		glUniform4iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform4ui(String n, int v0, int v1, int v2, int v3) {
		glUniform4ui(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2, v3);
	}

	public void setUniform4uiv(String n, int[] v) {
		glUniform4uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform4uiv(String n, IntBuffer v) {
		glUniform4uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniformMatrix2dv(String n, boolean transpose, double[] v) {
		glUniformMatrix2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2fv(String n, boolean transpose, float[] v) {
		glUniformMatrix2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x3dv(String n, boolean transpose, double[] v) {
		glUniformMatrix2x3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x3dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix2x3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x3fv(String n, boolean transpose, float[] v) {
		glUniformMatrix2x3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x3fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix2x3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x4dv(String n, boolean transpose, double[] v) {
		glUniformMatrix2x4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x4dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix2x4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x4fv(String n, boolean transpose, float[] v) {
		glUniformMatrix2x4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x4fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix2x4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	// mat3

	public void setUniformMatrix3dv(String n, boolean transpose, double[] v) {
		glUniformMatrix3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3fv(String n, boolean transpose, float[] v) {
		glUniformMatrix3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x3dv(String n, boolean transpose, double[] v) {
		glUniformMatrix3x2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x2dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix3x2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x2fv(String n, boolean transpose, float[] v) {
		glUniformMatrix3x2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x2fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix3x2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x4dv(String n, boolean transpose, double[] v) {
		glUniformMatrix3x4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x4dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix3x4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x4fv(String n, boolean transpose, float[] v) {
		glUniformMatrix3x4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x4fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix3x4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	// mat4

	public void setUniformMatrix4dv(String n, boolean transpose, double[] v) {
		glUniformMatrix4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4fv(String n, boolean transpose, float[] v) {
		glUniformMatrix4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x3dv(String n, boolean transpose, double[] v) {
		glUniformMatrix4x2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x2dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix4x2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x2fv(String n, boolean transpose, float[] v) {
		glUniformMatrix4x2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x2fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix4x2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x4dv(String n, boolean transpose, double[] v) {
		glUniformMatrix4x3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x4dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix4x3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x4fv(String n, boolean transpose, float[] v) {
		glUniformMatrix4x3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x4fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix4x3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

}
