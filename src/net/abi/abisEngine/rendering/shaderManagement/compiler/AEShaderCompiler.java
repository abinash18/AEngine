package net.abi.abisEngine.rendering.shaderManagement.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.file.PathType;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.shaderManagement.Shader;
import net.abi.abisEngine.rendering.shaderManagement.Shader.ShaderSource;
import net.abi.abisEngine.rendering.shaderManagement.compiler.fileTypes.yaml.AEShaderFileYAML;
import net.abi.abisEngine.rendering.shaderManagement.compiler.fileTypes.yaml.AEShaderGLSLProgram;
import net.abi.abisEngine.rendering.shaderManagement.compiler.loader.AEShaderLoader;
import net.abi.abisEngine.rendering.shaderManagement.compiler.loader.AEShaderLoader.ShaderType;
import net.abi.abisEngine.rendering.shaderManagement.compiler.parsers.AEShaderParserYAML;
import net.abi.abisEngine.util.cacheing.GenericCache;
import net.abi.abisEngine.util.cacheing.TwoFactorGenericCache;
import net.abi.abisEngine.util.exceptions.AERuntimeException;
import net.abi.abisEngine.util.exceptions.AEShaderCompilerRuntimeException;

/**
 * Compiles and creates of Shader objects in AE.
 * 
 * @author abina
 *
 */
public class AEShaderCompiler {

	private static Logger logger = LogManager.getLogger(AEShaderCompiler.class);
	private static final String INCLUDE_DIRECTIVE = "#include", COMMENT_PREFIX = "//", AE_IMPORT_DIRECTIVE = "#import",
			AE_SHADER_FILE_TYPE = ".ae-shader";
	private static final PathHandle DEFAULT_SHADER_ASSET_DIRECTORY_PATH = new PathHandle("res/shaders/",
			PathType.Internal);
	private static final char FILE_TO_PROGRAM_SEPERATOR = ':', PROGRAM_NAME_SEPERATOR = ',',
			PROGRAM_NAME_CONTAINER = '"', PROGRAM_LIST_CONTAINER_START = '{', PROGRAM_LIST_CONTAINER_END = '}';

	private TwoFactorGenericCache<String, String, AEShaderGLSLProgram> loadedImports = new TwoFactorGenericCache<String, String, AEShaderGLSLProgram>(
			String.class, String.class, AEShaderGLSLProgram.class);

	public Shader compile(AEShaderFileYAML p) {
		HashMap<String, StringBuilder> _shaders = processFile(p);

		return null;
	}

	private HashMap<String, StringBuilder> processFile(AEShaderFileYAML p) {
		HashMap<String, StringBuilder> _shaders = null;
		StringBuilder file = new StringBuilder();
		/*
		 * Loading the imports first will ensure that we don't have a recursive
		 * overflow.
		 */
		loadAllImportsInFile(p);

		for (AEShaderGLSLProgram gp : p.getAE_SHADER_GLSL_PROGRAMS()) {
			switch (ShaderType.valueOf(gp.getAE_SHADER_GLSL_PROGRAM_TYPE())) {
			case AE_SHADER_IMPORT:
				break;
			case AE_FRAGMENT_SHADER:
				
				break;
			}
			if (ShaderType.valueOf(gp.getAE_SHADER_GLSL_PROGRAM_TYPE()) != ShaderType.AE_SHADER_IMPORT) {
				file.append(processShaderSource(gp));
			}
		}
		System.out.println(file);
		return _shaders;
	}

	private void loadAllImportsInFile(AEShaderFileYAML file) {
		for (AEShaderGLSLProgram p : file.getAE_SHADER_GLSL_PROGRAMS()) {
			if (ShaderType.valueOf(p.getAE_SHADER_GLSL_PROGRAM_TYPE()) == ShaderType.AE_SHADER_IMPORT) {
				System.out.println(file.getAE_SHADER_NAME() + " Loading Import: " + p.getAE_SHADER_GLSL_PROGRAM_NAME()
						+ " " + p.getAE_SHADER_GLSL_PROGRAM_TYPE());
				loadedImports.put(file.getAE_SHADER_NAME(), p.getAE_SHADER_GLSL_PROGRAM_NAME(), p);
			}
		}
	}

	private String processShaderSource(AEShaderGLSLProgram gp) {
		String[] lines = gp.getAE_SHADER_GLSL_PROGRAM_SOURCE().split("\\r?\\n");
		StringBuilder proccessedSource = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			line = cleanLine(line);
			if (line.isEmpty() || line.isBlank())
				continue;
			if (line.startsWith(AE_IMPORT_DIRECTIVE)) {
				proccessedSource.append(processImportLine(line));
			} else {
				if (!line.isEmpty() && !line.isBlank()) {
					proccessedSource.append(line).append("\n");
				}
			}
		}
		return proccessedSource.toString();
	}

	private String processImportLine(String line) {
		StringBuilder loadedImport = new StringBuilder();

		/*
		 * Now we need to separate out the import file and programs.
		 */
		String importFile; // The file from which to import
		/*
		 * I do this since the user can accidentally have more than one whitespace. So
		 * we find the first one and then token-ize the line using this.
		 */
		String importDefinition; // There are only two possible tokens the import directive and the import call.
		String temp = ""; // This is to spell out the token and add it to the array.
		ArrayList<String> program_names = new ArrayList<String>();

		/*
		 * First we need to check if the import call is valid.
		 */
		if (!line.startsWith(AE_IMPORT_DIRECTIVE)) {
			return "";
		}

		if (Character.isWhitespace(line.charAt(AE_IMPORT_DIRECTIVE.length()))) {

			int importFileSeperatorIndex = 0;

			temp = line.substring(AE_IMPORT_DIRECTIVE.length(), line.length());
			temp = temp.replaceAll("\\s+", ""); // Clear all the white space.
			// System.out.println(temp);
			/*
			 * We substring the import line from the start of the temp to the : char. And
			 * while we do so we also define the index of it to use to separate the program
			 * tokens.
			 */
			importFile = temp.substring(0, (importFileSeperatorIndex = temp.indexOf(FILE_TO_PROGRAM_SEPERATOR)));
			// System.out.println(importFile);
			/*
			 * Now we separate the dictionary containing the import programs from the line
			 * it self.
			 */
			importDefinition = temp.substring(importFileSeperatorIndex + 1, temp.length()).replaceAll("\\{", "")
					.replaceAll("\\}", "");
			logger.fine("Importing Programs : " + line);
			String[] programs = importDefinition.split(",");
			// System.out.println(importDefinition);
			for (int i = 0; i < programs.length; i++) {
				programs[i] = programs[i].replaceAll("\"", "").replaceAll("\\s+", "");
				if (programs[i].isEmpty())
					continue;
				program_names.add(programs[i]);
			}
		} else {
			throw new AERuntimeException(
					"Invalid Import call in shader line : (Space between #import and file needed, well its not really but i just dont want to write the code to do so. "
							+ line);
		}

		for (String program : program_names) {
			loadedImport.append(processImport(program, importFile));
		}

		return loadedImport.toString();

	}

	private String processImport(String programName, String fileName) {
		StringBuilder program = new StringBuilder();

		AEShaderGLSLProgram gp = null;
		/*
		 * Check if the import was in this file or was loaded before.
		 */
		if ((gp = loadedImports.get(fileName + AE_SHADER_FILE_TYPE, programName)) == null) {
			//System.out.println("Looking for : " + fileName + AE_SHADER_FILE_TYPE + " " + programName);
			/*
			 * If it wasn't try finding it then loading it.
			 */
			loadAllImportsInFile(AEShaderLoader.getParserInstance()
					.parse(DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild(fileName + AE_SHADER_FILE_TYPE)));
			/*
			 * If we still can't find it then give up and go throw a exception.
			 */
			if ((gp = loadedImports.get(fileName + AE_SHADER_FILE_TYPE, programName)) == null) {
				throw new AEShaderCompilerRuntimeException(
						"Import in file not found : " + fileName + AE_SHADER_FILE_TYPE + " Import: " + programName);
			}
		}
		/*
		 * Now that we have determined the file exists we can continue. Or we have not
		 * and the program exits. Now we have to find that program.
		 */
		/*
		 * If the program doesn't exist we throw an exception.
		 */
		if ((gp = loadedImports.get(fileName + AE_SHADER_FILE_TYPE, programName)) == null) {
			throw new AEShaderCompilerRuntimeException(
					"Shader Import not found in file: " + fileName + AE_SHADER_FILE_TYPE + " Import: " + programName);
		}

		/*
		 * Otherwise we recursively process it and append it to the original source.
		 */
		program.append(processShaderSource(gp));
		return program.toString();
	}

	private String cleanLine(String line) {
		String returnLine;
		returnLine = line.replaceAll("[\\n\\t ]", "");
		returnLine = line = line.trim();
		if (line.startsWith(COMMENT_PREFIX)) {
			returnLine = line = "";
			return returnLine;
		}
		/*
		 * Cleans the line of comments and such.
		 */
		int commentindex;
		if (line.contains(COMMENT_PREFIX)) {
			commentindex = line.indexOf(COMMENT_PREFIX);
			returnLine = line.substring(0, commentindex);

		}
		return (returnLine = line.trim());
	}

}
