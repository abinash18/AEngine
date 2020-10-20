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

import net.abi.abisEngine.entities.Entity;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.rendering.pipeline.RenderingEngine;
import net.abi.abisEngine.rendering.scene.Scene;
import net.abi.abisEngine.rendering.shader.legacy.Shader;

public abstract class SceneComponent {

	private Entity parent;
	private String Name;

	public Transform getTransform() {
		return parent.getTransform();
	}

	public Scene getParentScene() {
		return parent.getParentScene();
	}

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
	}

	public void init() {
	}

	public void update(float delta) {
	}

	public void input(float delta) {
	}

	public void render(Shader shader, RenderingEngine engine) {
	}

	public void addToScene() {

	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

}
