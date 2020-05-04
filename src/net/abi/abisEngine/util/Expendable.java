package net.abi.abisEngine.util;

/**
 * An interface used to identify and organize entitys which can be destroyed at run time or at the end of runtime.
 * Caution: Implementing this interface means that the object <b>CAN<b> be destroyed, unlike static objects which last for all runtime.
 */
public interface Expendable {
	public void dispose();
}
