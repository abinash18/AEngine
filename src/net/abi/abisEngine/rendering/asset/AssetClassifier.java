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
