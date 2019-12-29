/**
 * 
 */
package net.abi.abisEngine.handlers.file;

/**
 * @author abinash
 *
 */
public enum PathType {

	/**
	 * Path to the root of the Class path, these files are always read only.
	 */
	ClassPath,
	/**
	 * Absolute path in the file system.
	 */
	Absolute,

	/**
	 * File path to the engines default assets directory.
	 */
	Assets,

	/**
	 * Specifies the path to be a directory so it can be manipulated accordingly.
	 */
	Directory

}
