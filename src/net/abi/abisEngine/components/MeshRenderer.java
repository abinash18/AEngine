package net.abi.abisEngine.components;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL45;

import net.abi.abisEngine.rendering.Mesh;
import net.abi.abisEngine.rendering.RenderingEngine;
import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.rendering.shaders.Shader;

public class MeshRenderer extends SceneComponent {

	private Mesh mesh;
	private Material mat;

	private int drawOption = GL15.GL_TRIANGLES;

	public static boolean drawNormals = false;

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

	private Shader wir = new Shader("visualizers/wireframe/wireframe");
	private Shader nor = new Shader("visualizers/normals/normals");

	@Override
	public void render(Shader shader, RenderingEngine engine) {

		if (drawOption == GL15.GL_LINES) {
			shader.bind();
			shader.updateUniforms(super.getTransform(), mat, engine);
			mesh.draw(GL15.GL_TRIANGLES);
			wir.bind();
			wir.updateUniforms(super.getTransform(), mat, engine);
			GL45.glEnable(GL45.GL_BLEND);
			GL45.glEnable(GL45.GL_SAMPLE_ALPHA_TO_COVERAGE);
			GL45.glBlendFunc(GL45.GL_SRC_ALPHA, GL45.GL_ONE_MINUS_SRC_ALPHA);
			// GL45.glLineWidth(10);
			mesh.draw(GL15.GL_TRIANGLES);
			GL45.glDisable(GL45.GL_SAMPLE_ALPHA_TO_COVERAGE);
			// GL45.glLineWidth(1);

		} else {
			shader.bind();
			shader.updateUniforms(super.getTransform(), mat, engine);
			mesh.draw(drawOption);
		}

		if (drawNormals) {
			nor.bind();
			nor.updateUniforms(super.getTransform(), mat, engine);
			mesh.draw(GL15.GL_POINTS);
		}

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
