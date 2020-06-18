package net.abi.abisEngine.rendering.shader.compiler;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.GL20;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.file.PathType;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.shader.AEShader;
import net.abi.abisEngine.rendering.shader.AEShader.AEShaderType;
import net.abi.abisEngine.rendering.shader.AEShader.GLSLUniform;
import net.abi.abisEngine.rendering.shader.AEShaderResource;
import net.abi.abisEngine.rendering.shader.parser.AEShaderParserYAML;
import net.abi.abisEngine.rendering.shader.parser.fileTypes.yaml.AEShaderFileYAML;
import net.abi.abisEngine.rendering.shader.parser.fileTypes.yaml.AEShaderGLSLProgram;
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
	private static final char COLON = ':', COMMA = ',', AIR_QUOTES = '"', OPENING_BRACKET = '{', CLOSING_BRACKET = '}',
			LINE_END_CHAR = ';';
	private static final String STRUCT_KEYWORD = "struct", UNIFORM_KEYWORD = "uniform";
	private TwoFactorGenericCache<String, String, AEShaderGLSLProgram> loadedImports = new TwoFactorGenericCache<String, String, AEShaderGLSLProgram>(
			String.class, String.class, AEShaderGLSLProgram.class);

	private AEShaderResource currentProgram;
	private AEShaderFileYAML p;
	private boolean compileTestPassed = false;

	public AEShaderCompiler(AEShaderFileYAML p, PathHandle path) {
		this.p = p;
		this.currentProgram = new AEShaderResource(p.getAE_SHADER_NAME(), path);
	}

	public AEShader getShaderInstance() {
		return (compileTestPassed) ? new AEShader(currentProgram) : null;
	}

	public AEShader compile() {
		ArrayList<ShaderSource> _unProccessedShaders = processFile(p);

		precompile(_unProccessedShaders);

		return null;
	}

	private ArrayList<ShaderSource> processFile(AEShaderFileYAML p) {
		ArrayList<ShaderSource> _shaders = new ArrayList<ShaderSource>();
		/*
		 * Loading the imports first will ensure that we don't have a recursive
		 * overflow.
		 */
		loadAllImportsInFile(p);

		for (AEShaderGLSLProgram gp : p.getAE_SHADER_GLSL_PROGRAMS()) {
			if (AEShaderType.valueOf(gp.getAE_SHADER_GLSL_PROGRAM_TYPE()) != AEShaderType.AE_SHADER_IMPORT) {
				_shaders.add(new ShaderSource(gp.getAE_SHADER_GLSL_PROGRAM_NAME(), processShaderSource(gp),
						AEShaderType.valueOf(gp.getAE_SHADER_GLSL_PROGRAM_TYPE())));
			}
		}
		return _shaders;
	}

	private void precompile(ArrayList<ShaderSource> sources) {
		for (ShaderSource shaderSource : sources) {
			if (shaderSource.type != AEShaderType.AE_SHADER_IMPORT
					&& shaderSource.type != AEShaderType.AE_COMPUTE_SHADER) {
				/*
				 * Attach each program
				 */
				addProgram(shaderSource.source, shaderSource.type);
			}
		}
		validate();
	}

	public void addProgram(String text, AEShaderType type) {
		int shader = GL20.glCreateShader(type.glType);
		if (shader == 0) {
			logger.debug("Error From Shader Program: '" + currentProgram.getName() + "'");
			logger.error("Shader creation failed: Could not find valid memory location when adding shader");
			logger.info("Exiting...");
			throw new AEShaderCompilerRuntimeException(
					"Could not create program: " + currentProgram.getName() + " Path:" + currentProgram.path);
		}

		GL20.glShaderSource(shader, text);
		// GL20.glCompileShader(shader);

		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {
			logger.debug("Error From Shader Program: '" + currentProgram.getName() + "'");
			logger.error(GL20.glGetShaderInfoLog(shader, 2048));
			logger.info("Exiting...");
			System.exit(1);
		}

		GL20.glAttachShader(currentProgram.getProgram(), shader);

		logger.debug("Successfully Attached Shader: " + currentProgram.getProgram() + " Log: " + "\n"
				+ GL20.glGetShaderInfoLog(shader, 1024));
		logger.finest("Shader Text For '" + type.toString() + "': '" + currentProgram.getName() + "'\n" + text);
	}

	private void validate() {
	}

	private void loadAllImportsInFile(AEShaderFileYAML file) {
		for (AEShaderGLSLProgram p : file.getAE_SHADER_GLSL_PROGRAMS()) {
			if (AEShaderType.valueOf(p.getAE_SHADER_GLSL_PROGRAM_TYPE()) == AEShaderType.AE_SHADER_IMPORT) {
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
				proccessedSource.append(processImportLine(line, gp.getAE_SHADER_GLSL_PROGRAM_NAME()));
			} else {
				if (!line.isEmpty() && !line.isBlank()) {
					proccessedSource.append(line).append("\n");
				}
			}
		}
		return proccessedSource.toString();
	}

	private String processImportLine(String line, String callingProgram) {
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
			/*
			 * We substring the import line from the start of the temp to the : char. And
			 * while we do so we also define the index of it to use to separate the program
			 * tokens.
			 */
			importFile = temp.substring(0, (importFileSeperatorIndex = temp.indexOf(COLON)));
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
			/*
			 * Prevents Recursive overflows, meaning that the same import can't call it self
			 * ever.
			 */
			if (program.equals(callingProgram)) {
				throw new AEShaderCompilerRuntimeException(
						"Recursive Overflow Detected, This import is calling it self. Program: " + callingProgram);
			}
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
			/*
			 * If it wasn't try finding it then loading it.
			 */
			loadAllImportsInFile(AEShaderParserYAML
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

	private class GLSLStruct {
		public String name, type;
	}

	public HashMap<String, ArrayList<GLSLStruct>> findUniformStructs(String shaderText) {
		HashMap<String, ArrayList<GLSLStruct>> result = new HashMap<String, ArrayList<GLSLStruct>>();
		int structStartLocation = shaderText.indexOf(STRUCT_KEYWORD);
		while (structStartLocation != -1) {
			if (!(structStartLocation != 0
					&& (Character.isWhitespace(shaderText.charAt(structStartLocation - 1))
							|| shaderText.charAt(structStartLocation - 1) == ';')
					&& Character.isWhitespace(shaderText.charAt(structStartLocation + STRUCT_KEYWORD.length())))) {
				structStartLocation = shaderText.indexOf(STRUCT_KEYWORD, structStartLocation + STRUCT_KEYWORD.length());
				continue;
			}

			/*
			 * Calculates the starting position of the uniform variable type and name
			 * definition in the line.
			 */
			int beginStructNameDefinitionIndex = structStartLocation + STRUCT_KEYWORD.length() + 1;

			// Finds the start location of the {.
			int bracketBegin = shaderText.indexOf(OPENING_BRACKET, beginStructNameDefinitionIndex);

			int bracketEnd = shaderText.indexOf(CLOSING_BRACKET, bracketBegin);

			// Finds the name by taking the substring between the start of the name and the
			// start of the { .
			String structName = shaderText.substring(beginStructNameDefinitionIndex, bracketBegin)
					.trim(); /* Trim to remove white space. */
			ArrayList<GLSLStruct> glslStructs = new ArrayList<GLSLStruct>();

			/*
			 * Finds the names of the uniforms defined inside the struct.
			 */
			// The position of the first line end character in the struct will mean the end
			// of the uniform definition in the struct.
			int componentLineEndCharPos = shaderText.indexOf(LINE_END_CHAR, bracketBegin);

			/*
			 * Loops while there is a semicolon pos and while it is not greater than that of
			 * the end of the struct end bracket.
			 */
			while (componentLineEndCharPos != -1 && componentLineEndCharPos < bracketEnd) {

				int componentNameEnd = componentLineEndCharPos + 1;

				while (Character.isWhitespace(shaderText.charAt(componentNameEnd - 1))
						|| shaderText.charAt(componentNameEnd - 1) == ';')
					componentNameEnd--;

				int componentNameStart = componentLineEndCharPos;

				/*
				 * While the char it is looping over is not a white space. Because we are going
				 * backwards from the uniform definition. As soon as we hit a space it means we
				 * are at the end of the name of the uniform.
				 */
				while (!Character.isWhitespace(shaderText.charAt(componentNameStart - 1))) {
					componentNameStart--; // Back up a character.
				}

				int componentTypeEnd = componentNameStart;

				while (Character.isWhitespace(shaderText.charAt(componentTypeEnd - 1)))
					componentTypeEnd--;

				int componentTypeStart = componentTypeEnd;

				/*
				 * ComponentTypeEnd is one char before the component name start, it is the end
				 * char pos of the type. And this loop will find the position at which it
				 * starts, it loops as long as there is no white space at the character it is
				 * on.
				 */
				while (!Character.isWhitespace(shaderText.charAt(componentTypeStart - 1))) {
					componentTypeStart--; // Back up a character.
				}

				String componentName = shaderText.substring(componentNameStart, componentLineEndCharPos);
				String componentType = shaderText.substring(componentTypeStart, componentTypeEnd);
				GLSLStruct glslStruct = new GLSLStruct();
				glslStruct.name = componentName;
				glslStruct.type = componentType;
				glslStructs.add(glslStruct);

				/*
				 * Finds the next end char pos in the file.
				 */
				componentLineEndCharPos = shaderText.indexOf(LINE_END_CHAR, componentLineEndCharPos + 1);

			}

			// Adds the found struct and all of its component uniforms in the HashMap.
			result.put(structName, glslStructs);

			structStartLocation = shaderText.indexOf(STRUCT_KEYWORD, structStartLocation
					+ STRUCT_KEYWORD.length()); /* This is to prevent it from looping over the same uniform keyword. */
		}

		return result;

	}

	public void addAllUniforms(ShaderSource source) {
		this.addAllUniforms(source.source);
	}

	public void addAllUniforms(String shaderText) {
		HashMap<String, ArrayList<GLSLStruct>> structs = findUniformStructs(shaderText);
		int uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD);
		while (uniformStartLocation != -1) {
			/*
			 * This performs a check to see whether the keyword being parsed is in fact an
			 * keyword and not part of a function name or a variable name.
			 */
			if (!(uniformStartLocation != 0 /* Checks if the starting position is not the first character in the file */
					/* Checks if there is white space before the keyword. */
					&& (Character.isWhitespace(shaderText.charAt(uniformStartLocation - 1))
							/* Or if there is a semicolon on the previous character */
							|| shaderText.charAt(uniformStartLocation - 1) == ';')
					/* Checks if there is whitespace after the keyword. */
					&& Character.isWhitespace(shaderText.charAt(uniformStartLocation + UNIFORM_KEYWORD.length())))) {
				continue;
			}

			/*
			 * Calculates the starting position of the uniform variable type and name
			 * definition in the line.
			 */
			int beginUniformDefinitionIndex = uniformStartLocation + UNIFORM_KEYWORD.length() + 1;

			// Finds the line end character and returns the index of it.
			int endOfDefinitionIndex = shaderText.indexOf(LINE_END_CHAR, beginUniformDefinitionIndex);

			String uniformLine = shaderText.substring(beginUniformDefinitionIndex, endOfDefinitionIndex).trim();

			int whiteSpacePos = uniformLine.indexOf(' ');

			// Returns that name of the uniform skipping the type and one is index after the
			// space to the name definition.
			String uniformName = uniformLine.substring(whiteSpacePos + 1, uniformLine.length()).trim();
			String uniformType = uniformLine.substring(0, whiteSpacePos).trim();

			this.addUniform(uniformName, uniformType, structs);

			uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD, uniformStartLocation
					+ UNIFORM_KEYWORD.length()); /* This is to prevent it from looping over the same uniform keyword. */
		}
	}

	/**
	 * Previously Known As addUniformWithStructCheck.
	 * 
	 * @param uniformName
	 * @param uniformType
	 * @param structs
	 */
	public void addUniform(String uniformName, String uniformType, HashMap<String, ArrayList<GLSLStruct>> structs) {
		boolean addThis = true;
		ArrayList<GLSLStruct> structComponents = structs.get(uniformType);
		if (structComponents != null) { // if there are no components in the struct.
			addThis = false;
			for (GLSLStruct struct : structComponents) {
				/*
				 * Send struct again to this recursively to check for for structs inside of
				 * this.
				 */
				addUniform(uniformName + "." + struct.name, struct.type, structs);
			}
		}
		if (!addThis) {
			return;
		}
		int uniformLocation = GL20.glGetUniformLocation(currentProgram.getProgram(), uniformName);
		logger.fine("Uniform " + "'" + uniformName + "'" + " At Location: " + uniformLocation);
		if (uniformLocation == 0xFFFFFFFF) {
			/* Change level of this message to warning or informal */
			logger.warning("Error: Could not find uniform: " + "'" + uniformName + "'"
					+ "Or it is not being used please check shader code and remove any un-used code or variables.");
			new Exception().printStackTrace();
		}
		currentProgram.uniforms.put(uniformName, new GLSLUniform(uniformName, uniformType, uniformLocation));
	}

	public static class ShaderSource {
		public String name, source;
		AEShaderType type;

		/**
		 * @param name
		 * @param source
		 * @param tag
		 */
		public ShaderSource(String name, String source, AEShaderType type) {
			this.name = name;
			this.source = source;
			this.type = type;
		}
	}

	public static class ShaderData {
		public HashMap<String, ShaderSource> proccessedSources;

		public ShaderData(String shaderName) {
			this.proccessedSources = new HashMap<String, ShaderSource>();
		}

		public void addSource(ShaderSource source) {
			proccessedSources.put(source.name, source);
		}

		public ShaderSource getSource(String sourceName) {
			return proccessedSources.get(sourceName);
		}

	}

}
