/**
 * 
 */
package net.abi.abisEngine.rendering.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import net.abi.abisEngine.input.GLFWInput;
import net.abi.abisEngine.rendering.image.AEImage;
import net.abi.abisEngine.util.exceptions.AECursorInitializationException;

/**
 * @author abina
 *
 */
public class StaticCursor implements CursorI {
	StaticCursorResource cr;

	/**
	 * Creates a standard GLFW_ARROW_CURSOR
	 * 
	 * @param id
	 */
	public StaticCursor(String id) {
		this(id, GLFWInput.GLFW_ARROW_CURSOR);
	}

	public StaticCursor(String id, int standardCursor) {
		if ((this.cr = GLFWWindow.cursors.get(id)) == null) {
			this.cr = new StaticCursorResource();
			this.cr.id = id;
			this.cr.standardCursorType = standardCursor;
			GLFWWindow.cursors.put(id, cr);
		} else {
			// This is the only thing we need to do since the user decided to make a copy of
			// the cursor.
			this.cr.incRef();
		}
	}

	public StaticCursor(String id, AEImage imageToUse, int xHot, int yHot) {
		if ((this.cr = GLFWWindow.cursors.get(id)) == null) {
			this.cr = new StaticCursorResource();
			this.cr.id = id;
			this.cr.image = imageToUse;
			this.cr.xHotspot = xHot;
			this.cr.yHotspot = yHot;
		} else {
			// This is the only thing we need to do since the user decided to make a copy of
			// the cursor.
			this.cr.incRef();
		}
	}

	public long create() throws AECursorInitializationException {
		if (this.cr.standardCursorType != 0) {
			this.cr.cursor_handle = GLFW.glfwCreateStandardCursor(this.cr.standardCursorType);
		} else {
			GLFWImage i = GLFWImage.malloc();
			i.set(this.cr.image.getImageMetaData().width, this.cr.image.getImageMetaData().height,
					this.cr.image.getData().getPixelsInByteBuffer());
			this.cr.cursor_handle = GLFW.glfwCreateCursor(i, this.cr.xHotspot, this.cr.yHotspot);
			i.free();
		}
		if (this.cr.cursor_handle == GLFWWindow.NULL) {
			throw new AECursorInitializationException("Failed to create cursor.", this);
		}
		return this.cr.cursor_handle;
	}

	public String getID() {
		return cr.id;
	}

	@Override
	public void dispose() {
		this.cr.decRef();
	}

	@Override
	public long getHandle() {
		return cr.cursor_handle;
	}

	@Override
	public StaticCursorResource getCursorResource() {
		return cr;
	}
}
