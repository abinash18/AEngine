package net.abi.abisEngine.components;

import net.abi.abisEngine.rendering.RenderingEngine;
import net.abi.abisEngine.rendering.meshLoading.Mesh;
import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.rendering.shaders.Shader;

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

}
