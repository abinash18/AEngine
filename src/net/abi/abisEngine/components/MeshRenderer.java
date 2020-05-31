package net.abi.abisEngine.components;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL45;

import net.abi.abisEngine.rendering.meshManagement.Mesh;
import net.abi.abisEngine.rendering.pipelineManagement.RenderingEngine;
import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.rendering.shaderManagement.Shader;

public class MeshRenderer extends SceneComponent {

	private Mesh mesh;
	private Material mat;

	public static boolean drawNormals = false, drawWireframes = false;

	public MeshRenderer(Mesh mesh, Material mat) {
		this.mesh = mesh;
		this.mat = mat;
	}

	public static void toggleWireFrames() {
		if (drawWireframes) {
			drawWireframes = false;
		} else {
			drawWireframes = true;
		}
	}

	private Shader wir = new Shader("wireframe");
	private Shader nor = new Shader("normals");

	@Override
	public void render(Shader shader, RenderingEngine engine) {

		if (drawWireframes) {
			//shader.bind();
			//shader.updateUniforms(super.getTransform(), mat, engine);
			//mesh.draw("vaoOne", GL15.GL_TRIANGLES);
			wir.bind();
			wir.updateUniforms(super.getTransform(), mat, engine);
			GL45.glEnable(GL45.GL_BLEND);
			GL45.glEnable(GL45.GL_SAMPLE_ALPHA_TO_COVERAGE);
			
			GL45.glBlendFunc(GL45.GL_SRC_ALPHA, GL45.GL_ONE_MINUS_SRC_ALPHA);
			mesh.draw("vaoOne", GL15.GL_TRIANGLES);
			//GL45.glPolygonMode(GL45.GL_FRONT_AND_BACK, GL45.GL_LINE);
			GL45.glDisable(GL45.GL_SAMPLE_ALPHA_TO_COVERAGE);
			GL45.glDisable(GL45.GL_BLEND);

		} else {
			//GL45.glPolygonMode(GL45.GL_FRONT_AND_BACK, GL45.GL_FILL);
			shader.bind();
			shader.updateUniforms(super.getTransform(), mat, engine);
			mesh.draw("vaoOne", GL15.GL_TRIANGLES);
		}

		if (drawNormals) {
			nor.bind();
			nor.updateUniforms(super.getTransform(), mat, engine);
			GL45.glEnable(GL45.GL_BLEND);
			GL45.glEnable(GL45.GL_SAMPLE_ALPHA_TO_COVERAGE);
			GL45.glBlendFunc(GL45.GL_SRC_ALPHA, GL45.GL_ONE_MINUS_SRC_ALPHA);
			mesh.draw("vaoOne", GL15.GL_POINTS);
			GL45.glDisable(GL45.GL_SAMPLE_ALPHA_TO_COVERAGE);
			GL45.glEnable(GL45.GL_BLEND);
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
