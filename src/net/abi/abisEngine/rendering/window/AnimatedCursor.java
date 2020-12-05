/**
 * 
 */
package net.abi.abisEngine.rendering.window;

import net.abi.abisEngine.rendering.image.AEImage;
import net.abi.abisEngine.util.exceptions.AECursorInitializationException;

/**
 * @author abina
 *
 */
public class AnimatedCursor implements CursorI {
	String id;
	long cursor_handle;
	AEImage animationStages[];
	int yHotspot = 0, xHotspot = 0;

	@Override
	public void dispose() {

	}

	@Override
	public long create() throws AECursorInitializationException {
		return 0L;
	}

	@Override
	public String getID() {
		return null;
	}

	@Override
	public long getHandle() {
		return 0;
	}

	@Override
	public StaticCursorResource getCursorResource() {
		return null;
	}
}
