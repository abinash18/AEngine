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
package tests.renderTest.windows;

import net.abi.abisEngine.rendering.pipeline.RenderingEngine;
import net.abi.abisEngine.rendering.window.GLFWWindow;
import net.abi.abisEngine.rendering.window.GLFWWindowManager;
import tests.renderTest.scenes.TestGame;

public class MainGame extends GLFWWindow {

	public MainGame() {
		super();
		super.properties.name = "EngineSplash";
		super.properties.title = "AEngine";
		super.properties.sc_height = 720;
		super.properties.sc_width = 1270;
		super.properties.fullscreen = false;
		super.properties.vSync = GLFW_FALSE;
		super.properties.renderEngine = new RenderingEngine();
	}

	@Override
	protected void addScenes() {
		new TestGame(this);
	}

	@Override
	protected void pre_init() {
	}

	@Override
	protected void close() {
		GLFWWindowManager.raiseStopFlag();
	}

	@Override
	protected void post_init() {
		// super.centerWindow();
		// super.showWindow();
	}

}
