package com.base.engine.internalGame;

import com.base.engine.components.GameComponent;
import com.base.engine.core.GameObject;
import com.base.engine.rendering.RenderingEngine;

public abstract class Game {

	private GameObject root;

	public void init() {
		getRootObject().init();
	}

	public void update(float delta) {
		getRootObject().update(delta);
	}

	public void input(float delta) {
		getRootObject().input(delta);
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
		}
		return root;

	}
}
