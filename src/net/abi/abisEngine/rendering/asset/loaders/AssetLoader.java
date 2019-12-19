package net.abi.abisEngine.rendering.asset.loaders;

import net.abi.abisEngine.rendering.asset.AssetLoaderParameters;

/**
 * 
 * @author abinash
 *
 * @param <T> The type of asset this loads.
 */
public abstract class AssetLoader<T, P extends AssetLoaderParameters<T>> {

	String assetsDir;

	public AssetLoader(String assetsDir) {
		if (!assetsDir.endsWith("/")) {
			assetsDir += "/";
		}
		this.assetsDir = assetsDir;
	}

	public String resolve(String fileName) {
		return assetsDir + fileName;
	}

}
