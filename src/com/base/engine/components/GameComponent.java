package com.base.engine.components;

import com.base.engine.core.RenderingEngine;
import com.base.engine.core.Transform;
import com.base.engine.rendering.Shader;

public abstract class GameComponent {

	public void init(Transform transform) {
	}

	public void setGameObject() {
	}

	public void update(Transform transform, float delta) {
	}

	public void input(Transform transform, float delta) {
	}

	public void render(Transform transform, Shader shader) {
	}
	
	public void addToRenderingEngine(RenderingEngine engine) {
		
	}

}
