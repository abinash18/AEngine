package com.base.engine.core;

import com.base.engine.rendering.Shader;

public interface GameComponent {

	public void init(Transform transform);

	public void setGameObject();

	public void update(Transform transform, float delta);

	public void input(Transform transform, float delta);

	public void render(Transform transform, Shader shader);

}
