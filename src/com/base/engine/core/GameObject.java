package com.base.engine.core;

import java.util.ArrayList;

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
		children.add(child);
	}

	public void addComponent(GameComponent gameComponent) {
		components.add(gameComponent);
	}

	public ArrayList<GameObject> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<GameObject> children) {
		this.children = children;
	}

	public void init() {
		for (GameComponent component : components) {
			component.init(transform);
		}
		for (GameObject child : children) {
			child.init();
		}
	}

	public void input(float delta) {

		for (GameComponent component : components) {
			component.input(transform, delta);
		}

		for (GameObject child : children) {
			child.input(delta);
		}
	}

	public void update(float delta) {
		for (GameComponent component : components) {
			component.update(transform, delta);
		}
		for (GameObject child : children) {
			child.update(delta);
		}
	}

	public void render(Shader shader) {
		for (GameComponent component : components) {
			component.render(transform, shader);
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
