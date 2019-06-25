package com.base.engine.internalGame;

import com.base.engine.core.GameComponent;
import com.base.engine.core.Material;
import com.base.engine.core.Transform;
import com.base.engine.rendering.BasicShader;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.PhongShader;
import com.base.engine.rendering.Shader;

public class MeshRenderer implements GameComponent {

	private Mesh mesh;
	private Material mat;

	public MeshRenderer(Mesh mesh, Material mat) {
		this.mesh = mesh;
		this.mat = mat;
	}

	@Override
	public void render(Transform transform, Shader shader) {
		// Shader shader = BasicShader.getInstance();

		// shader.compileShader();
		shader.bind();
		shader.updateUniform(transform.getTransformation(), transform.getProjectedTransformation(), mat);
		mesh.draw();
	}

	@Override
	public void init(Transform transform) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameObject() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Transform transform) {
		// TODO Auto-generated method stub

	}

	@Override
	public void input(Transform transform) {
		// TODO Auto-generated method stub

	}

}
