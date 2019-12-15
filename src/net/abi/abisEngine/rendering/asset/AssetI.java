/**
 * 
 */
package net.abi.abisEngine.rendering.asset;

import net.abi.abisEngine.util.Expendable;

/**
 * @author abinash
 *
 */
public interface AssetI extends Expendable {

	/**
	 * Increments the asset's reference count by one.
	 */
	public void incRef();

	public int incAndGetRef();

	/**
	 * Decrements the asset's reference count by one.
	 */
	public void decRef();

	public int decAndGetRef();

	/**
	 * Returns the integer representation of the references.
	 * 
	 * @return The count of refrences.
	 */
	public int getRefs();
}
