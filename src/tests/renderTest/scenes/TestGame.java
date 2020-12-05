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
package tests.renderTest.scenes;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.rendering.scene.Scene;
import net.abi.abisEngine.rendering.shader.AEShader;
import net.abi.abisEngine.rendering.shader.compiler.AEShaderCompiler;
import net.abi.abisEngine.rendering.shader.compiler.parser.AEShaderParserYAML;
import net.abi.abisEngine.rendering.window.GLFWWindow;

public class TestGame extends Scene {

	public TestGame(GLFWWindow prnt) {
		super("TestGame", prnt);
	}

	public void init() {
		super.init();
		PathHandle p = AEShader.DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild("PointLight.ae-shader");
		AEShaderCompiler.compile(AEShaderParserYAML.parse(p), p);
	}

	float temp = 0.0f;

	@Override
	public void update(float delta) {
		super.update(delta);
	}

	@Override
	public void input(float delta) {
		super.input(delta);
	}
}
