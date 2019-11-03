package tests.game.materials;

import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.rendering.resourceManagement.Texture;

public class BricksOne extends Material {

	public BricksOne() {
		super();
		super.addTexture("diffuse", new Texture("bricks.jpg"));
		super.addTexture("normal_map", new Texture("bricks_normal.jpg"));
		super.addFloat("specularIntensity", 0.25f);
		super.addFloat("specularPower", 12);
	}

}
