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
