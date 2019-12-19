package net.abi.abisEngine.rendering.asset.loaders;
/**
 * 
 */

import net.abi.abisEngine.rendering.asset.AssetLoaderParameters;
import net.abi.abisEngine.rendering.asset.AssetManager;

/**
 * @author abinash
 *
 */
public abstract class SyncAssetLoader<T, P extends AssetLoaderParameters<T>> extends AssetLoader<T, P> {

	public SyncAssetLoader(String assetsDir) {
		super(assetsDir);
	}

	public abstract T loadSync(String fileName, AssetManager manager);

}
