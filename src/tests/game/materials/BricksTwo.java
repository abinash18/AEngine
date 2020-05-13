package tests.game.materials;

import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.rendering.textureManagement.Texture;

public class BricksTwo extends Material {

	public BricksTwo() {
		super();
		super.addTexture("diffuse", new Texture("bricks2.jpg"));
		super.addTexture("normal_map", new Texture("bricks2_normal.jpg"));
		super.addFloat("specularIntensity", 1);
		super.addFloat("specularPower", 8);
	}
	
}
