package net.abi.abisEngine.components;

import org.lwjgl.opengl.GL15;

import net.abi.abisEngine.rendering.RenderingEngine;
import net.abi.abisEngine.rendering.meshLoading.Mesh;
import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.rendering.shaders.Shader;

public class MeshRenderer extends SceneComponent {

	private Mesh mesh;
	private Material mat;

	private int drawOption = GL15.GL_TRIANGLES;

	public MeshRenderer(Mesh mesh, Material mat) {
		this.mesh = mesh;
		this.mat = mat;
	}

	public MeshRenderer toggleWireFrames() {
		if (drawOption == GL15.GL_TRIANGLES) {
			drawOption = GL15.GL_LINES;
		} else {
			drawOption = GL15.GL_TRIANGLES;
		}
		return this;
	}

	@Override
	public void render(Shader shader, RenderingEngine engine) {
		shader.bind();
		shader.updateUniforms(super.getTransform(), mat, engine);
		mesh.draw(drawOption);
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
