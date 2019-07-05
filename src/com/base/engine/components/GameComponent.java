package com.base.engine.components;

import com.base.engine.core.GameObject;
import com.base.engine.core.Transform;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;

public abstract class GameComponent {

	private GameObject parent;

	public Transform getTransform() {
		return parent.getTransform();
	}

	public GameObject getParent() {
		return parent;
	}

	public void setParent(GameObject parent) {
		this.parent = parent;
	}

	public void init() {
	}
	
	public void update(float delta) {
	}

	public void input(float delta) {
	}

	public void render(Shader shader) {
	}

	public void addToRenderingEngine(RenderingEngine engine) {

	}

}
