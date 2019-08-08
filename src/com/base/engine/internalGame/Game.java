package com.base.engine.internalGame;

import com.base.engine.components.GameComponent;
import com.base.engine.core.CoreEngine;
import com.base.engine.core.GameObject;
import com.base.engine.rendering.RenderingEngine;

public abstract class Game {

	private GameObject root;

	public void init() {
		getRootObject().init();
	}

	public void update(float delta) {
		getRootObject().updateAll(delta);
	}

	public void input(float delta) {
		getRootObject().inputAll(delta);
	}

	public void render(RenderingEngine engine) {
		engine.render(getRootObject());
	}

	public void addChild(GameObject child) {
		getRootObject().addChild(child);
	}

	public void addComponent(GameComponent component) {
		getRootObject().addComponent(component);
	}

	private GameObject getRootObject() {
		if (root == null) {
			root = new GameObject();
			// root.setCoreEngine(coreEngine);
		}
		return root;

	}

	public GameObject getRoot() {
		return root;
	}

	public void setRoot(GameObject root) {
		this.root = root;
	}

	public void setCoreEngine(CoreEngine coreEngine) {
		this.getRootObject().setCoreEngine(coreEngine);
	}
}
