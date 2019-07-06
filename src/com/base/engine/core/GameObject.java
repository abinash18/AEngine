package com.base.engine.core;

import java.util.ArrayList;

import com.base.engine.components.GameComponent;
import com.base.engine.math.Transform;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;

public class GameObject {

	private ArrayList<GameObject> children;
	private ArrayList<GameComponent> components;
	private Transform transform;

	public GameObject() {
		this.children = new ArrayList<GameObject>();
		this.components = new ArrayList<GameComponent>();
		this.transform = new Transform();
	}

	public void addChild(GameObject child) {
		child.getTransform().setParent(transform);
		children.add(child);
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

	public void addToRenderingEngine(RenderingEngine engine) {
		for (GameComponent component : components) {
			component.addToRenderingEngine(engine);
		}

		for (GameObject child : children) {
			child.addToRenderingEngine(engine);
		}
	}

	public void init() {
		for (GameComponent component : components) {
			component.init();
		}
		for (GameObject child : children) {
			child.init();
		}
	}

	public void input(float delta) {

		for (GameComponent component : components) {
			component.input(delta);
		}

		for (GameObject child : children) {
			child.input(delta);
		}
	}

	public void update(float delta) {
		for (GameComponent component : components) {
			component.update(delta);
		}
		for (GameObject child : children) {
			child.update(delta);
		}
	}

	public void render(Shader shader) {
		for (GameComponent component : components) {
			component.render(shader);
		}
		for (GameObject child : children) {
			child.render(shader);
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

}
