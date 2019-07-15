package com.base.engine.components;

import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;

public class MeshRenderer extends GameComponent {

	private Mesh mesh;
	private Material mat;

	public MeshRenderer(Mesh mesh, Material mat) {
		this.mesh = mesh;
		this.mat = mat;
	}

	@Override
	public void render(Shader shader, RenderingEngine engine) {
		// Shader shader = BasicShader.getInstance();

		// shader.compileShader();
		shader.bind();
		shader.updateUniform(super.getTransform(), mat, engine);
		mesh.draw();
	}

	@Override
	public void init() {

	}

	@Override
	public void update(float delta) {

	}

	@Override
	public void input(float delta) {

	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
