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
package net.abi.abisEngine.components;

import net.abi.abisEngine.math.Quaternionf;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.rendering.pipeline.RenderingEngine;
import net.abi.abisEngine.rendering.shader.legacy.Shader;

public class LockView extends SceneComponent {

	RenderingEngine engine;

	@Override
	public void update(float delta) {
		if (engine != null) {
			Quaternionf newRotation = super.getTransform().getLookAtDirection(
					super.getParentScene().getMainCamera().getTransform().getTransformedPosition(), Transform.Y_AXIS);
			// super.getTransform().setRotation(super.getTransform().getRotation().nlerp(newRotation,
			// delta * 2, true));

			super.getTransform().setRotation(super.getTransform().getRotation().slerp(newRotation, delta * 10, true));

		}
	}

	@Override
	public void render(Shader shader, RenderingEngine engine) {
		this.engine = engine;
	}

}
