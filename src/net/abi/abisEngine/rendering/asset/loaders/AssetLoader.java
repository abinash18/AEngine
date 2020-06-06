/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package net.abi.abisEngine.rendering.asset.loaders;

import java.util.ArrayList;

import net.abi.abisEngine.rendering.asset.libGDX.AssetDescriptor;
import net.abi.abisEngine.rendering.asset.libGDX.AssetLoaderParameters;

/**
 * Abstract base class for asset loaders.
 * 
 * @author mzechner
 * 
 * @param <T> the class of the asset the loader supports
 * @param <P> the class of the loading parameters the loader supports.
 */
public abstract class AssetLoader<T, P extends AssetLoaderParameters<T>> {

	private String assetsDir = "";

	/**
	 * Constructor, sets the {@link FileHandleResolver} to use to resolve the file
	 * associated with the asset name.
	 * 
	 * @param assetsDir
	 */
	public AssetLoader(String assetsDir) {
		this.assetsDir = assetsDir;
	}

	/**
	 * @param fileName file name to resolve
	 * @return handle to the file, as resolved by the {@link FileHandleResolver} set
	 *         on the loader
	 */
	public String resolve(String fileName) {
		if (!assetsDir.endsWith("/")) {
			assetsDir += "/";
		}

		return assetsDir + fileName;

	}

	/**
	 * Returns the assets this asset requires to be loaded first. This method may be
	 * called on a thread other than the GL thread.
	 * 
	 * @param fileName  name of the asset to load
	 * @param file      the resolved file to load
	 * @param parameter parameters for loading the asset
	 * @return other assets that the asset depends on and need to be loaded first or
	 *         null if there are no dependencies.
	 */
	public abstract ArrayList<AssetDescriptor> getDependencies(String fileName, String file, P parameter);
}
