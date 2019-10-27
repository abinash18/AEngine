package com.base.engine.components;

import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.meshLoading.legacy.Mesh;
import com.base.engine.rendering.resourceManagement.Material;
import com.base.engine.rendering.shaders.Shader;

public class MeshRenderer extends SceneComponent {

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
		shader.updateUniforms(super.getTransform(), mat, engine);
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
