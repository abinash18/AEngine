package tests.game.entitys;

import net.abi.abisEngine.components.MeshRenderer;
import net.abi.abisEngine.core.Entity;
import net.abi.abisEngine.rendering.meshManagement.AIMeshLoader;
import tests.game.materials.BricksOne;

public class FlatPlane extends Entity {

	public FlatPlane() {
		super.addComponent(new MeshRenderer(AIMeshLoader.loadModel("monkey.obj", "Suzanne.001", 0), new BricksOne()));
		super.getTransform().setTranslation(0, -5, 0);
		// super.getTransform().getRotation().set(new Quaternion(Transform.X_AXIS,
		// (float) Math.toRadians(90)));
	}

}
