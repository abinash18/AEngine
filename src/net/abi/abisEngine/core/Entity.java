package net.abi.abisEngine.core;

import java.util.ArrayList;

import net.abi.abisEngine.components.SceneComponent;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.rendering.pipelineManagement.RenderingEngine;
import net.abi.abisEngine.rendering.sceneManagement.Scene;
import net.abi.abisEngine.rendering.shaderManagement.Shader;

public class Entity implements IEntity {

	private ArrayList<Entity> children;
	private ArrayList<SceneComponent> components;
	private Transform transform;
	private Scene parentScene;

	public Entity() {
		this.children = new ArrayList<Entity>();
		this.components = new ArrayList<SceneComponent>();
		this.transform = new Transform();
		this.parentScene = null;
	}

	public ArrayList<Entity> getAllAttached() {
		ArrayList<Entity> result = new ArrayList<Entity>();
		for (Entity child : children) {
			result.addAll(child.getAllAttached());
		}
		result.add(this);
		return result;
	}

	public Entity addChild(Entity child) {
		child.getTransform().setParent(transform);
		child.setParentScene(parentScene);
		children.add(child);
		return this;
	}

	public Entity addComponent(SceneComponent gameComponent) {
		gameComponent.setParent(this);
		components.add(gameComponent);

		return this;
	}

	public ArrayList<Entity> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Entity> children) {
		this.children = children;
	}

	public void addToScene() {
		for (SceneComponent component : components) {
			component.addToScene();
		}

		for (Entity child : children) {
			child.addToScene();
		}
	}

	/**
	 * Initialize All Scene Objects And Components Attached.
	 */
	public void init() {
		for (SceneComponent component : components) {
			component.init();
		}
		for (Entity child : children) {
			child.init();
		}
	}

	/**
	 * Update Inputs For All Scene Objects And Components Attached.
	 * 
	 * @param delta
	 */
	public void inputAll(float delta) {
		this.input(delta);

		for (Entity child : children) {
			child.inputAll(delta);
		}
	}

	/**
	 * Update This And All Scene Objects And Components Attached.
	 * 
	 * @param delta
	 */
	public void updateAll(float delta) {
		this.update(delta);
		for (Entity child : children) {
			child.updateAll(delta);
		}
	}

	/**
	 * Only Update The Inputs For This Object.
	 */
	public void input(float delta) {
		/*
		 * This Has To Be Done To Allow For The Children To Have Correct Parent
		 * Transformations.
		 */
		transform.update();

		for (SceneComponent component : components) {
			component.input(delta);
		}
	}

	/**
	 * Only Update This Scene Object.
	 */
	public void update(float delta) {
		for (SceneComponent component : components) {
			component.update(delta);
		}
	}

	/**
	 * Only Render This Scene Object.
	 */
	public void render(Shader shader, RenderingEngine engine) {
		for (SceneComponent component : components) {
			component.render(shader, engine);
		}
	}

	public void renderAll(Shader shader, RenderingEngine engine) {
		this.render(shader, engine);
		for (Entity child : children) {
			child.renderAll(shader, engine);
		}
	}

	public ArrayList<SceneComponent> getComponents() {
		return components;
	}

	public void setComponents(ArrayList<SceneComponent> components) {
		this.components = components;
	}

	public Transform getTransform() {
		return transform;
	}

	public void setTransform(Transform transform) {
		this.transform = transform;
	}

	public void setParentScene(Scene prntScene) {
		if (this.parentScene != prntScene) {
			this.parentScene = prntScene;
			for (SceneComponent component : components) {
				component.addToScene();
			}
			for (Entity child : children) {
				child.setParentScene(prntScene);
			}
		}
	}

	public Scene getParentScene() {
		return parentScene;
	}

}
