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
package tests.renderTest;

import net.abi.abisEngine.core.CoreEngine;
import net.abi.abisEngine.core.Main;
import net.abi.abisEngine.rendering.window.models.EngineLoader;
import net.abi.abisEngine.util.exceptions.AEGLFWWindowInitializationException;

public class Start extends Main {

	static Start s;

	public static void main(String[] args) {
		// LogManager.setCurrentLevel(LogLevel.DEBUG);
		s = new Start();
		s.run(args);
		//System.out.println(Runtime.get);
		// PathHandle p =
		// AEShader.DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild("frameworkTest.ae-shader");
		// AEShaderCompiler.compile(AEShaderParserYAML.parse(p), p);
		// GL_CLIPPING_OUTPUT_PRIMITIVES
	}

	@Override
	public void run(String[] args) {
		super.run(args);
	}

	@Override
	protected void openStartingWindow(CoreEngine e) {
		try {
			e.getWindowManager().openWindow(new EngineLoader());
		} catch (AEGLFWWindowInitializationException e1) {
			e1.printStackTrace();
		}
	}

}
