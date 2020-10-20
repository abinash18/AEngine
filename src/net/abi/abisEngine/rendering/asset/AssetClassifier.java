/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.rendering.asset;
/**
 * 
 */

/**
 * @author abinash
 * @param <T>
 *
 */
public class AssetClassifier<T> {

	private String fileName;
	/**
	 * Absolute path to the file.
	 */
	private String fileLocation;
	private AssetLoaderParameters<T> parameter;
	private Class<T> type;
	private TaskCompleteHandler callback;

	/**
	 * @param fileName
	 * @param fileLocation
	 * @param type
	 */
	public AssetClassifier(String fileName, Class<T> type) {
		this(fileName, type, null);
	}

	/**
	 * @param fileName
	 * @param fileLocation
	 * @param parameter
	 * @param type
	 */
	public AssetClassifier(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
		this.fileName = fileName.replaceAll("\\\\", "/");
		this.parameter = parameter;
		this.type = type;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public Class<T> getType() {
		return type;
	}

	public AssetLoaderParameters<T> getParameter() {
		return parameter;
	}

	public void setParameter(AssetLoaderParameters<T> parameter) {
		this.parameter = parameter;
	}

}
