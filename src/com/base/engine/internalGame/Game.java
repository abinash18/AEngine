package com.base.engine.internalGame;

import com.base.engine.core.GameObject;

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

//	public void render() {
//		getRootObject().render();
//	}

	public GameObject getRootObject() {
		if (root == null) {
			root = new GameObject();
		}
		return root;

	}
}
