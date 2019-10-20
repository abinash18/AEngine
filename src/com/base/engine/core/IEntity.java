/**
 * 
 */
package com.base.engine.core;

import com.base.engine.math.Transform;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.shaders.Shader;

/**
 * @author abinash Basic Way Of Communicating With An Game Entity
 */
public interface IEntity {

	/**
	 * Initializes The Entity Before The First Frame Of The Scene That Is Being
	 * Rendered.
	 */
	public void init();

	/**
	 * This Function Is Intended For Updating All Input Mechanics For The Entity.
	 * 
	 * @param delta The Difference In Time. This Parameter Is Usually Requested
	 *              Directly From The Core Engine.
	 */
	public void input(float delta);

	/**
	 * This Function Is Intended For Updating The Entity (Ex. Animations And
	 * Movements)
	 * 
	 * @param delta The Difference In Time. This Parameter Is Usually Requested
	 *              Directly From The Core Engine.
	 */
	public void update(float delta);

	/**
	 * This Function Renders The Entity With The Given Shader, And With The Supplied
	 * Rendering Engine.
	 * 
	 * @param shader Shader That Is Used To Render The Entity.
	 * @param engine The Engine Used To Render The Entity.
	 */
	public void render(Shader shader, RenderingEngine engine);

	/**
	 * Returns The Transform For The Entity Used To Manipulate The Entity.
	 * 
	 * @return Transform Object.
	 */
	public Transform getTransform();

	/**
	 * Sets The Entity's Transform To The Transform Provided.
	 * 
	 * @param trnsfrm
	 */
	public void setTransform(Transform trnsfrm);
}
