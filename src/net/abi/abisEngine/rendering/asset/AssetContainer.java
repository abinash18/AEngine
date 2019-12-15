/**
 * 
 */
package net.abi.abisEngine.rendering.asset;

/**
 * @author abinash
 *
 */
public class AssetContainer implements AssetI {
	Object object;
	int refCount = 1;

	public AssetContainer(Object object) {
		if (object == null)
			throw new IllegalArgumentException("Object must not be null");
		this.object = object;
	}

	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}

	public <T> T getObject(Class<T> type) {
		return (T) object;
	}

	public void setObject(Object asset) {
		this.object = asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.util.Expendable#dispose()
	 */
	@Override
	public void dispose() {
		object = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#incRef()
	 */
	@Override
	public void incRef() {
		refCount++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#decRef()
	 */
	@Override
	public void decRef() {
		refCount--;
		if (refCount <= 0) {
			dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#getRefs()
	 */
	@Override
	public int getRefs() {
		return refCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#incAndGetRef()
	 */
	@Override
	public int incAndGetRef() {
		incRef();
		return refCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#decAndGetRef()
	 */
	@Override
	public int decAndGetRef() {
		decRef();
		return refCount;
	}
}
