package com.base.engine;

public class Game {
	private Mesh mesh;
	private PhongShader shader;
	private Transform transform;
	// private Texture tex;
	private Material mat;
	private Camera cam;

	public Game() {
		mesh = ResourceLoader.LoadMesh("simpleCube.obj"); // new Mesh();//
		shader = new PhongShader().getInstance();
		cam = new Camera();
		mat = new Material(ResourceLoader.loadTexture("defaultTexture.png"), new Vector3f(0.1f, 1, 1));
		transform = new Transform();
		// tex = ResourceLoader.loadTexture("brick_wall.png");
//		Vertex[] data = new Vertex[] { new Vertex(new Vector3f(-1, -1, 0), new Vector2f(0, 0)),
//				new Vertex(new Vector3f(0, 1, 0), new Vector2f(0.5f, 0)),
//				new Vertex(new Vector3f(1, -1, 0), new Vector2f(1.0f, 0)),
//				new Vertex(new Vector3f(0, -1, 1), new Vector2f(0, 0.5f)) };
//
//		int[] indices = new int[] { 1, 3, 2, 2, 3, 0, 1, 2, 0 };
//
//		mesh.addVertices(data, indices, true);

		Vertex[] vertices = new Vertex[] { new Vertex(new Vector3f(-1.0f, -1.0f, 0.5773f), new Vector2f(0.0f, 0.0f)),
				new Vertex(new Vector3f(0.0f, -1.0f, -1.15475f), new Vector2f(0.5f, 0.0f)),
				new Vertex(new Vector3f(1.0f, -1.0f, 0.5773f), new Vector2f(1.0f, 0.0f)),
				new Vertex(new Vector3f(0.0f, 1.0f, 0.0f), new Vector2f(0.5f, 1.0f)) };

		int indices[] = { 0, 3, 1, 1, 3, 2, 2, 3, 0, 1, 2, 0 };

		//mesh.addVertices(vertices, indices, true);

		Transform.setCam(cam);

		Transform.setProjection(70f, Window.getWidth(), Window.getHeight(), 0.1f, 1000f);

		PhongShader.setAmbientLight(new Vector3f(.03f, .03f, .03f));
		PhongShader.getBaseLight().setIntensity(0.8f);
		PhongShader.setDirectionalLight(new DirectionalLight(PhongShader.getBaseLight(), new Vector3f(1, 1, 1)));

//		shader.addVertexShader(ResourceLoader.loadShader("basicVertex.vs"));
//		shader.addFragmentShader(ResourceLoader.loadShader("basicFragment.fs"));
//		shader.compileShader();
//
//		shader.addUniform("transform");
	}

	public void input() {

	}

	float temp = 0.0f;

	public void update() {
		temp += Time.getDelta();
		cam.input();
		float sin = (float) Math.sin(temp);

		transform.setTranslation(0, 0, 5);
		transform.setRotation(0, sin * 180, 0);
		// transform.setScale(0.25f, 0.25f, 0.25f);
	}

	public void render() {

		// RenderUtil.setClearColor(new Vector3f(0.0f, 0.0f, 0.0f));
		RenderUtil.setClearColor(Transform.getCam().getPos().div(2048f).abs());

		shader.bind();
		shader.updateUniform(transform.getProjectedTrasformation(), transform.getProjectedTrasformation(), mat);
		// tex.bind();
		// shader.setUniform("transform", transform.getProjectedTrasformation());
		mesh.draw();
	}
}
