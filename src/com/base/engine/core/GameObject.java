package com.base.engine.core;

import java.util.ArrayList;

import com.base.engine.components.GameComponent;
import com.base.engine.math.Transform;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.sceneManagement.Scene;
import com.base.engine.rendering.shaders.Shader;

public class GameObject {

	private ArrayList<GameObject> children;
	private ArrayList<GameComponent> components;
	private Transform transform;
	private Scene parentScene;

	public GameObject() {
		this.children = new ArrayList<GameObject>();
		this.components = new ArrayList<GameComponent>();
		this.transform = new Transform();
		this.parentScene = null;
	}

	public ArrayList<GameObject> getAllAttached() {
		ArrayList<GameObject> result = new ArrayList<GameObject>();
		for (GameObject child : children) {
			result.addAll(child.getAllAttached());
		}
		result.add(this);
		return result;
	}

	public GameObject addChild(GameObject child) {
		child.getTransform().setParent(transform);
		child.setParentScene(parentScene);
		children.add(child);
		return this;
	}

	public GameObject addComponent(GameComponent gameComponent) {
		gameComponent.setParent(this);
		components.add(gameComponent);

		return this;
	}

	public ArrayList<GameObject> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<GameObject> children) {
		this.children = children;
	}

	public void addToScene() {
		for (GameComponent component : components) {
			component.addToScene();
		}

		for (GameObject child : children) {
			child.addToScene();
		}
	}

	/**
	 * Initialize All Scene Objects And Components Attached.
	 */
	public void init() {
		for (GameComponent component : components) {
			component.init();
		}
		for (GameObject child : children) {
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

		for (GameObject child : children) {
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
		for (GameObject child : children) {
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

		for (GameComponent component : components) {
			component.input(delta);
		}
	}

	/**
	 * Only Update This Scene Object.
	 */
	public void update(float delta) {
		for (GameComponent component : components) {
			component.update(delta);
		}
	}

	/**
	 * Only Render This Scene Object.
	 */
	public void render(Shader shader, RenderingEngine engine) {
		for (GameComponent component : components) {
			component.render(shader, engine);
		}
	}

	public void renderAll(Shader shader, RenderingEngine engine) {
		this.render(shader, engine);
		for (GameObject child : children) {
			child.renderAll(shader, engine);
		}
	}

	public ArrayList<GameComponent> getComponents() {
		return components;
	}

	public void setComponents(ArrayList<GameComponent> components) {
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
			for (GameComponent component : components) {
				component.addToScene();
			}
			for (GameObject child : children) {
				child.setParentScene(prntScene);
			}
		}
	}

	public Scene getParentScene() {
		return parentScene;
	}

}
