/**
 * 
 */
package net.abi.abisEngine.rendering.window;

import net.abi.abisEngine.util.Expendable;
import net.abi.abisEngine.util.exceptions.AECursorInitializationException;

/**
 * @author abina
 *
 */
public interface CursorI extends Expendable {
	public long create() throws AECursorInitializationException;

	public String getID();

	public StaticCursorResource getCursorResource();

	public long getHandle();
}
