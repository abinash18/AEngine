/**
 * 
 */
package net.abi.abisEngine.handlers.file;

/**
 * @author abinash
 *
 */
/*
 * Interface to resolve file paths.
 */
public interface PathResolveI {

	/**
	 * Returns a PathHandle that is bound to the file or directory provided.
	 * 
	 * @param path
	 * @param type
	 * @return
	 */
	public PathHandle resolve(String path, PathType type);

	/**
	 * Returns a PathHandle that is bound to the file or directory provided in the
	 * ClassPath.
	 * 
	 * @param path
	 * @return
	 */
	public PathHandle resolveClasspath(String path);

	/**
	 * Returns a PathHandle that is bound to the file or directory present in the
	 * home directory of the current user on the desktop.
	 * 
	 * @param path
	 * @return
	 */
	public PathHandle resolveExternal(String path);

	/**
	 * Returns a PathHandle that is bound to the file or directory provided that the
	 * path is fully qualified and absolute.
	 * 
	 * @param path
	 * @return
	 */
	public PathHandle resolveAbsolute(String path);

	public PathHandle resolveAssets(String path);

}
