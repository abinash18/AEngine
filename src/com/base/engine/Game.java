package com.base.engine;

public class Game {
	private Mesh mesh;
	private Shader shader;
	private Transform transform;

	public Game() {
		mesh = new Mesh();
		shader = new Shader();

		Vertex[] data = new Vertex[] { new Vertex(new Vector3f(-1, -1, 0)), new Vertex(new Vector3f(0, 1, 0)),
				new Vertex(new Vector3f(1, -1, 0)) };

		mesh.addVertices(data);

		transform = new Transform();

		shader.addVertexShader(ResourceLoader.loadShader("basicVertex.vs"));
		shader.addFragmentShader(ResourceLoader.loadShader("basicFragment.fs"));
		shader.compileShader();

		shader.addUniform("transform");
	}

	public void input() {
		if (Input.getKeyDown(Input.KEY_UP))
			System.out.println("We've just pressed up!");
		if (Input.getKeyUp(Input.KEY_UP))
			System.out.println("We've just released up!");

		if (Input.getMouseDown(1))
			System.out.println("We've just right clicked at " + Input.getMousePosition().toString());
		if (Input.getMouseUp(1))
			System.out.println("We've just released right mouse button!");
	}

	float temp = 0.0f;

	public void update() {
		temp += Time.getDelta();

		float sin = (float) Math.sin(temp);
		
		//transform.setTranslation(sin, 0, 0);
		//transform.setRotation(0, 0, sin * 180);
		transform.setScale(sin, sin, sin);
	}

	public void render() {
		shader.bind();
		shader.setUniform("transform", transform.getTransformation());
		mesh.draw();
	}
}
