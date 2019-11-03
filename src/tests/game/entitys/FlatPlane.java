package tests.game.entitys;

import net.abi.abisEngine.components.MeshRenderer;
import net.abi.abisEngine.core.Entity;
import tests.game.materials.BricksOne;
import tests.game.models.FlatPlaneMesh;

public class FlatPlane extends Entity {

	public FlatPlane() {
		super.addComponent(new MeshRenderer(new FlatPlaneMesh(), new BricksOne()));
		super.getTransform().setTranslation(0, -5, 0);
		// super.getTransform().getRotation().set(new Quaternion(Transform.X_AXIS,
		// (float) Math.toRadians(90)));
	}

}
