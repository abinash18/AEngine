package com.base.engine.internalGame;

import com.base.engine.core.GameObject;

public abstract class Game {

	private GameObject root;

	public void init() {
		getRootObject().init();
	}

	public void update() {
		getRootObject().update();
	}

	public void input() {
		getRootObject().input();
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
