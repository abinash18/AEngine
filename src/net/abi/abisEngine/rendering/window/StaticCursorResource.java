/**
 * 
 */
package net.abi.abisEngine.rendering.window;

import static org.lwjgl.glfw.GLFW.glfwDestroyCursor;

import net.abi.abisEngine.rendering.asset.AssetI;
import net.abi.abisEngine.rendering.image.AEImage;

/**
 * @author abina
 *
 */
public class StaticCursorResource implements AssetI {
	String id;
	long cursor_handle;
	AEImage image;
	int standardCursorType = 0, yHotspot = 0, xHotspot = 0, refs = 1;

	@Override
	public void dispose() {
		if (refs <= 0) {
			image.decRef();
			glfwDestroyCursor(cursor_handle);
		}
	}

	@Override
	public void incRef() {
		refs += 1;
	}

	@Override
	public int incAndGetRef() {
		incRef();
		return refs;
	}

	@Override
	public void decRef() {
		refs -= 1;
	}

	@Override
	public int decAndGetRef() {
		decRef();
		return refs;
	}

	@Override
	public int getRefs() {
		return refs;
	}
}
