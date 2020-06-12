package net.abi.abisEngine.components;

import net.abi.abisEngine.entities.Entity;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.rendering.renderPipeline.RenderingEngine;
import net.abi.abisEngine.rendering.scene.Scene;
import net.abi.abisEngine.rendering.shader.Shader;

public abstract class SceneComponent {

	private Entity parent;
	private String Name;

	public Transform getTransform() {
		return parent.getTransform();
	}

	public Scene getParentScene() {
		return parent.getParentScene();
	}

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
	}

	public void init() {
	}

	public void update(float delta) {
	}

	public void input(float delta) {
	}

	public void render(Shader shader, RenderingEngine engine) {
	}

	public void addToScene() {

	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

}