package tests.renderTest.materials;

import net.abi.abisEngine.rendering.material.Material;
import net.abi.abisEngine.rendering.texture.Texture;

public class BricksTwo extends Material {

	public BricksTwo() {
		super();
		super.addTexture("diffuse", new Texture("bricks2.jpg").load());
		super.addTexture("normal_map", new Texture("bricks2_normal.jpg").load());
		super.addFloat("specularIntensity", 1);
		super.addFloat("specularPower", 8);
	}

}
