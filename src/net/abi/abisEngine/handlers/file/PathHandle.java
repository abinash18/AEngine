/**
 * Provides a handle to a specified path which can then be manipulated to create/delete/read/write directory's and files.
 */
package net.abi.abisEngine.handlers.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

/**
 * @author abinash
 *
 */
public class PathHandle {

	Path path;
	PathType pathType;

	/**
	 * @param path
	 * @param pathType
	 */
	public PathHandle(Path path, PathType pathType) {
		this.path = path;
		this.pathType = pathType;
	}

	public PathHandle(String path) {
		this.path = Paths.get(path);
		this.pathType = PathType.Absolute;
	}

	public PathHandle(String path, PathType pathType) {
		this.path = Paths.get(path);
		this.pathType = pathType;
	}

	public String getPath() {
		return path.toString().replace('\\', '/');
	}

	public String getName() {
		return path.getFileName().toString();
	}

	public String getExtension() {
		String fileName = path.getFileName().toString();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}

	public String getNameWithoutExtension() {
		String fileName = path.getFileName().toString();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(0, fileName.lastIndexOf("."));
		else
			return fileName;
	}

	public String getPathWithoutExtension() {
		String path = this.path.toString().replace('\\', '/');
		int dotIndex = path.lastIndexOf('.');
		if (dotIndex == -1)
			return path;
		return path.substring(0, dotIndex);
	}

	public PathType getType() {
		return pathType;
	}

	public File getFileInstance() {
		return path.toFile();
	}

	public boolean isDirectory() {
		return Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS);
	}

	public PathHandle createDirectoryInCurrent(String name, FileAttribute<?>... attribs) throws IOException {
		Path dirPath = path.resolveSibling(name);
		Files.createDirectory(dirPath, attribs);
	}

	/**
	 * Creates a directory inside the current if the current path is a directory is
	 * a directory. Other wise creates one in the parent directory. And returns the
	 * PathHandle to that directory.
	 * 
	 * Example: if the current path is a directory is ./foo/bar/ and you use this
	 * method it will create a directory like this: ./foo/bar/@name@ when the @name@
	 * variable is the one specified. If the current path points to a file,
	 * ./foo/fi/bar.txt , then it will create a directory in the parent directory of
	 * the file like so: ./foo/fi/@name@ and return the file handle.
	 * 
	 * 
	 * @param name
	 * @param attribs
	 * @throws IOException
	 */
	public PathHandle createDirectory(String name, FileAttribute<?>... attribs) throws IOException {
		Path dirPath;
		if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			dirPath = path.resolve(name);
		} else {
			dirPath = path.resolveSibling(name);
		}

		return new PathHandle(Files.createDirectory(dirPath, attribs), PathType.Directory);
	}

}
