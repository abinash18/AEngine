/**
 * 
 */
package net.abi.abisEngine.rendering.mesh;

import net.abi.abisEngine.rendering.material.Material;

/**
 * Contains the Model Data and the name and Materials that are used on this
 * mesh.
 * 
 * @author abinash
 *
 */
class MeshData {
	Model model;
	String meshName;
	Material material;

	int refCount = 1;

	/**
	 * @param model
	 * @param meshName
	 * @param material
	 */
	public MeshData(Model model, String meshName, Material material) {
		this.model = model;
		this.meshName = meshName;
		this.material = material;
	}

}
