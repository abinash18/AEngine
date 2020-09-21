package tests.renderTest.materials;

import net.abi.abisEngine.rendering.material.Material;
import net.abi.abisEngine.rendering.texture.Texture;

public class BricksOne extends Material {

	public BricksOne() {
		super();
		super.addTexture("diffuse", new Texture("bricks.jpg").load());
		super.addTexture("normal_map", new Texture("bricks_normal.jpg").load());
		super.addFloat("specularIntensity", 0.25f);
		super.addFloat("specularPower", 12);
	}

}
