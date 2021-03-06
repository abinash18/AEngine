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
package net.abi.abisEngine.entities;

import java.util.ArrayList;

import net.abi.abisEngine.components.SceneComponent;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.rendering.pipeline.RenderingEngine;
import net.abi.abisEngine.rendering.scene.Scene;
import net.abi.abisEngine.rendering.shader.legacy.Shader;

public class Entity implements EntityI {

	private ArrayList<Entity> children;
	private ArrayList<SceneComponent> components;
	private Transform transform;
	private Scene parentScene;

	public Entity() {
		this.children = new ArrayList<Entity>();
		this.components = new ArrayList<SceneComponent>();
		this.transform = new Transform();
		this.parentScene = null;
	}

	public ArrayList<Entity> getAllAttached() {
		ArrayList<Entity> result = new ArrayList<Entity>();
		for (Entity child : children) {
			result.addAll(child.getAllAttached());
		}
		result.add(this);
		return result;
	}

	public Entity addChild(Entity child) {
		child.getTransform().setParent(transform);
		child.setParentScene(parentScene);
		children.add(child);
		return this;
	}

	public Entity addComponent(SceneComponent gameComponent) {
		gameComponent.setParent(this);
		components.add(gameComponent);

		return this;
	}

	public ArrayList<Entity> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Entity> children) {
		this.children = children;
	}

	public void addToScene() {
		for (SceneComponent component : components) {
			component.addToScene();
		}

		for (Entity child : children) {
			child.addToScene();
		}
	}

	/**
	 * Initialize All Scene Objects And Components Attached.
	 */
	public void init() {
		for (SceneComponent component : components) {
			component.init();
		}
		for (Entity child : children) {
			child.init();
		}
	}

	/**
	 * Update Inputs For All Scene Objects And Components Attached.
	 * 
	 * @param delta
	 */
	public void inputAll(float delta) {
		this.input(delta);
		for (Entity child : children) {
			child.inputAll(delta);
		}
	}

	/**
	 * Update This And All Scene Objects And Components Attached.
	 * 
	 * @param delta
	 */
	public void updateAll(float delta) {
		this.update(delta);
		for (Entity child : children) {
			child.updateAll(delta);
		}
	}

	/**
	 * Only Update The Inputs For This Object.
	 */
	public void input(float delta) {
		/*
		 * This Has To Be Done To Allow For The Children To Have Correct Parent
		 * Transformations.
		 */
		transform.update();
		for (SceneComponent component : components) {
			component.input(delta);
		}
	}

	/**
	 * Only Update This Scene Object.
	 */
	public void update(float delta) {
		for (SceneComponent component : components) {
			component.update(delta);
		}
	}

	/**
	 * Only Render This Scene Object.
	 */
	public void render(Shader shader, RenderingEngine engine) {
		for (SceneComponent component : components) {
			component.render(shader, engine);
		}
	}

	public void renderAll(Shader shader, RenderingEngine engine) {
		this.render(shader, engine);
		for (Entity child : children) {
			child.renderAll(shader, engine);
		}
	}

	public ArrayList<SceneComponent> getComponents() {
		return components;
	}

	public void setComponents(ArrayList<SceneComponent> components) {
		this.components = components;
	}

	public Transform getTransform() {
		return transform;
	}

	public void setTransform(Transform transform) {
		this.transform = transform;
	}

	public void setParentScene(Scene prntScene) {
		if (this.parentScene != prntScene) {
			this.parentScene = prntScene;
			for (SceneComponent component : components) {
				component.addToScene();
			}
			for (Entity child : children) {
				child.setParentScene(prntScene);
			}
		}
	}

	public Scene getParentScene() {
		return parentScene;
	}

}
