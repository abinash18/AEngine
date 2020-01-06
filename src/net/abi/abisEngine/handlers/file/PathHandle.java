/**
 * Provides a handle to a specified path which can then be manipulated to create/delete/read/write directory's and files.
 */
package net.abi.abisEngine.handlers.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.util.AERuntimeException;

/**
 * @author abinash
 *
 */
public class PathHandle {

	private static final LinkOption DEFAULT_LINK_ACTION = LinkOption.NOFOLLOW_LINKS;

	private static Logger logger = LogManager.getLogger(PathHandle.class);

	private Path path;
	private PathType pathType;

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
		this.pathType = Files.isDirectory(this.path, DEFAULT_LINK_ACTION) == true ? PathType.Directory
				: PathType.Absolute;
	}

	public PathHandle(Path path) {
		this.path = path;
		this.pathType = Files.isDirectory(this.path, DEFAULT_LINK_ACTION) == true ? PathType.Directory
				: PathType.Absolute;
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

	public PathType getPathType() {
		return pathType;
	}

	public File getFileInstance() {
		return path.toFile();
	}

	public boolean isDirectory() {
		return Files.isDirectory(path, DEFAULT_LINK_ACTION);
	}

	public boolean isAbsolute() {
		return path.isAbsolute();
	}

	/**
	 * Creates a directory as the sibling of the current. Example: Current:
	 * ./foo/bar then it creates one like so ./foo/foo2 as a sibling of the
	 * previous. If the current Path if the current path is a directory. Other wise
	 * creates one in the parent directory. And returns the PathHandle to that
	 * directory.
	 * 
	 * Example: if the current path is a directory is ./foo/bar/ and you use this
	 * method it will create a directory like this: ./foo/bar/@name@ when the @name@
	 * variable is the one specified. If the current path points to a file,
	 * ./foo/fi/bar.txt, then it will create a directory in the parent directory of
	 * the file like so: ./foo/fi/@name@ and return the file handle.
	 * 
	 * @param name
	 * @param attribs
	 * @return PathHandle Of type Directory.
	 * @throws IOException
	 */
	public PathHandle createSiblingDirectory(String name, FileAttribute<?>... attribs) throws IOException {
		Path dirPath = path.resolveSibling(name);
		Files.createDirectory(dirPath, attribs);
		return new PathHandle(dirPath, PathType.Directory);
	}

	/**
	 * Creates a directory inside the current if the current path is a directory.
	 * Other wise creates one in the parent directory. And returns the PathHandle to
	 * that directory.
	 * 
	 * Example: if the current path is a directory is ./foo/bar/ and you use this
	 * method it will create a directory like this: ./foo/bar/@name@ when the @name@
	 * variable is the one specified. If the current path points to a file,
	 * ./foo/fi/bar.txt, then it will create a directory in the parent directory of
	 * the file like so: ./foo/fi/@name@ and return the file handle.
	 * 
	 * 
	 * @param name
	 * @param attribs
	 * @throws IOException
	 */
	public PathHandle createDirectory(String name, FileAttribute<?>... attribs) throws IOException {
		Path dirPath;
		if (Files.isDirectory(path, DEFAULT_LINK_ACTION)) {
			dirPath = path.resolve(name);
		} else {
			dirPath = path.resolveSibling(name);
		}

		return new PathHandle(Files.createDirectory(dirPath, attribs), PathType.Directory);
	}

	public PathHandle resolveSibling(String _path) {
		Path p = path.resolveSibling(_path);
		return new PathHandle(p);
	}

	public PathHandle resolveChild(String _path) {
		Path p = path.resolve(_path);
		return new PathHandle(p);
	}

	public PathHandle[] list() {
		if (pathType == PathType.ClassPath)
			throw new AERuntimeException("Cannot list a classpath directory: " + path);
		String[] relativePaths = getFileInstance().list();
		if (relativePaths == null)
			return new PathHandle[0];
		PathHandle[] handles = new PathHandle[relativePaths.length];
		for (int i = 0, n = relativePaths.length; i < n; i++)
			handles[i] = resolveChild(relativePaths[i]);
		return handles;
	}

	public PathHandle parent() {
		Path parent = path.getParent();
		if (parent == null) {
			if (pathType == PathType.Absolute)
				parent = Paths.get("/");
			else
				parent = Paths.get("");
		}
		return new PathHandle(parent.toAbsolutePath());
	}

	public PathHandle mkdirs(FileAttribute<?>... attribs) throws IOException {
		if (pathType == PathType.ClassPath || pathType == PathType.Internal) {
			throw new AERuntimeException("Unsupported Path Type: " + pathType);
		}
		return new PathHandle(Files.createDirectories(path, attribs));
	}

	public boolean exists() {
		switch (pathType) {
		case Internal:
			if (Files.exists(path, DEFAULT_LINK_ACTION))
				return true;
		case ClassPath:
			return PathHandle.class.getResource("/" + path.toString().replace("\\", "/")) != null;
		}
		return Files.exists(path, DEFAULT_LINK_ACTION);
	}

	private class ContainsFileVisitor implements FileVisitor<Path> {
		boolean isPresent = false;
		String fileName;

		/**
		 * 
		 */
		public ContainsFileVisitor(String fileName) {
			this.fileName = fileName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.nio.file.FileVisitor#postVisitDirectory(java.lang.Object,
		 * java.io.IOException)
		 */
		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.nio.file.FileVisitor#preVisitDirectory(java.lang.Object,
		 * java.nio.file.attribute.BasicFileAttributes)
		 */
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.nio.file.FileVisitor#visitFile(java.lang.Object,
		 * java.nio.file.attribute.BasicFileAttributes)
		 */
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if (path.getFileName().equals(fileName)) {
				isPresent = true;
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.nio.file.FileVisitor#visitFileFailed(java.lang.Object,
		 * java.io.IOException)
		 */
		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public boolean contains(String fileName) {
		if (pathType != PathType.Directory && !Files.isDirectory(path, DEFAULT_LINK_ACTION))
			throw new AERuntimeException("Path dose not lead to a Directory. Path: " + path);
		ContainsFileVisitor c = new ContainsFileVisitor(fileName);
		try {
			Files.walkFileTree(path, c);
		} catch (IOException e) {
			logger.error("Unable To Walk File Tree Of Path: " + path.toString(), e);
		}

		return c.isPresent;

	}

	@Override
	public String toString() {
		return "PathHandle [path=" + path + ", pathType=" + pathType + "]";
	}

}
