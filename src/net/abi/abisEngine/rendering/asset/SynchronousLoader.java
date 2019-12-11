package net.abi.abisEngine.rendering.asset;
/**
 * 
 */

/**
 * @author abinash
 *
 */
public abstract class SynchronousLoader<T, P extends AssetLoaderParameters<T>> extends AssetLoader<T, P> {

	public SynchronousLoader(String assetsDir) {
		super(assetsDir);
	}

	public abstract T loadSync(String fileName, AssetManager manager);

}
