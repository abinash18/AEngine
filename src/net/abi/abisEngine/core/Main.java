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
package net.abi.abisEngine.core;

import net.abi.abisEngine.handlers.logging.LogLevel;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;

public abstract class Main {

	private static Logger logger = LogManager.getLogger(Main.class.getName());

	protected abstract void openStartingWindow(CoreEngine e);

	public void run(String[] args) {
		int frameRate = 500;
		// Options opts = new Options();
		// LogManager.addFileHandler(1000);
		CoreEngine engine = new CoreEngine(frameRate);
		openStartingWindow(engine);
		engine.start();
	};

}
