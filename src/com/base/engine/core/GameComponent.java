package com.base.engine.core;

import com.base.engine.rendering.Shader;

public interface GameComponent {

	public void init(Transform transform);

	public void setGameObject();

	public void update(Transform transform);

	public void input(Transform transform);

	public void render(Transform transform, Shader shader);

}
