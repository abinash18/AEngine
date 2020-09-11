package net.abi.abisEngine.rendering.shader.compiler;

import static org.lwjgl.system.MemoryStack.stackPush;

import static org.lwjgl.opengl.GL46.*;

import static net.abi.abisEngine.rendering.shader.compiler.Tokens.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.file.PathType;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.shader.AEShader;
import net.abi.abisEngine.rendering.shader.AEShader.AEShaderType;
import net.abi.abisEngine.rendering.shader.AEShaderResource;
import net.abi.abisEngine.rendering.shader.GLSLLayoutQualifier;
import net.abi.abisEngine.rendering.shader.GLSLUniform;
import net.abi.abisEngine.rendering.shader.GLSLUniformBlockObject;
import net.abi.abisEngine.rendering.shader.GLSLUniformBlockObjectData;
import net.abi.abisEngine.rendering.shader.compiler.Tokens.Keywords;
import net.abi.abisEngine.rendering.shader.compiler.Tokens.Operators;
import net.abi.abisEngine.rendering.shader.compiler.Tokens.Qualifiers;
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
	public static final PathHandle DEFAULT_SHADER_ASSET_DIRECTORY_PATH = new PathHandle("res/shaders/",
			PathType.Internal);
	private static final String CURRENT_VERSION = "Compiler Version: 1.02-rev6";
	private static TwoFactorGenericCache<String, String, AEShaderGLSLProgram> loadedImports = new TwoFactorGenericCache<String, String, AEShaderGLSLProgram>(
			String.class, String.class, AEShaderGLSLProgram.class);

	private static PrintStream out = System.out;

	/**
	 * Compiles and pre process the AE Shader file also auto adds uniforms and
	 * <b>Must be run on render thread, and in proper context.</b>
	 * 
	 * @return
	 */
	public static AEShader compile(AEShaderFileYAML p, PathHandle path) {
		return compile(p, path, true);
	}

	public static AEShader compile(AEShaderFileYAML p, PathHandle path, PrintStream debugOut) {
		AEShaderCompiler.out = debugOut;
		return compile(p, path, true);
	}

	public static AEShader compile(AEShaderFileYAML p, PathHandle path, boolean autoBindUniforms) {

		float start = System.nanoTime();
		out.println("Compiling Shader: '" + p.getAE_SHADER_NAME() + "'");
		/*
		 * We create a reference of this program to keep.
		 */
		AEShaderResource program = new AEShaderResource(p.getAE_SHADER_NAME(), path);
		program.createProgram();
		out.println("--Processing File--");
		/*
		 * Now parse the file.
		 */
		ArrayList<ShaderSource> _unProccessedShaders = processFile(p);
		out.println("Attaching shaders to program: " + "\n{\n\t Program Name: " + program.getName()
				+ "\n\t Program ID: " + program.getProgram() + "\n}");
		attach(program, _unProccessedShaders);

		/*
		 * Now we add the attributes to the program before we link.
		 */
		for (ShaderSource s : _unProccessedShaders) {
			if (s.type == AEShaderType.AE_VERTEX_SHADER) {
				// processAttributes(program, s);
			}
		}

		/*
		 * Now we link.
		 */
		link(program);

		/*
		 * If the program is to determine each of the uniform's locations we proccess
		 * this.
		 */
		// if (autoBindUniforms) {
		// processUniforms(program, _unProccessedShaders);
		// }

		/*
		 * Now we have to create an instance of AEShader the user can then access the
		 * linked program.
		 */
		AEShader shader = null;
		out.println("Successfuly Compiled Shader.");
		float end = System.nanoTime();
		float elapsedTime = end - start;
		out.println("Time To Completion: " + (elapsedTime / 1000000000) + "s");
		return shader;
	}

	private static ArrayList<ShaderSource> processFile(AEShaderFileYAML p) {
		ArrayList<ShaderSource> _shaders = new ArrayList<ShaderSource>();
		/*
		 * Loading the imports first will ensure that we don't have a recursive
		 * overflow.
		 */
		loadAllImportsInFile(p);
		for (AEShaderGLSLProgram gp : p.getAE_SHADER_GLSL_PROGRAMS()) {
			if (AEShaderType.valueOf(gp.getAE_SHADER_GLSL_PROGRAM_TYPE()) != AEShaderType.AE_SHADER_IMPORT) {
				ShaderSource s = new ShaderSource(gp.getAE_SHADER_GLSL_PROGRAM_NAME(),
						/*
						 * process the source.
						 */
						processShaderSource(gp.getAE_SHADER_GLSL_PROGRAM_SOURCE(), gp.getAE_SHADER_GLSL_PROGRAM_NAME()),
						AEShaderType.valueOf(gp.getAE_SHADER_GLSL_PROGRAM_TYPE()));
				_shaders.add(s);
				out.println("Shader Name: " + gp.getAE_SHADER_GLSL_PROGRAM_NAME() + "\nShader Type: "
						+ gp.getAE_SHADER_GLSL_PROGRAM_TYPE() + "\nShader Source: \n--------\n" + s.source
						+ "--------");
			}
		}
		return _shaders;
	}

	/**
	 * Auto binds attributes in the vertex stage. TODO: Auto detect index overlap.
	 */
	private static void processAttributes(AEShaderResource program, ShaderSource gp) {
		String source = gp.source;
		int inStartIndex = source.indexOf(Keywords.IN.word), inLocation = 0;
		while (inStartIndex != -1) {
			/*
			 * Check if the Keyword is a token and not part of a word.
			 */
			if (!(inStartIndex != 0
					&& (Character.isWhitespace(source.charAt(inStartIndex - 1))
							|| Operators.SEMICOLON.op.equals(Character.toString(source.charAt(inStartIndex - 1))))
					&& Character.isWhitespace(source.charAt(inStartIndex + Keywords.IN.word.length())))) {
				inStartIndex = source.indexOf(Keywords.IN.word, inStartIndex + Keywords.IN.word.length());
				continue;
			}
			/*
			 * Beginning index of the in definition.
			 */
			int beginAtrribDefinitionIndex = inStartIndex + Keywords.IN.word.length() + 1;
			int endOfDefinitionIndex = source.indexOf(Operators.SEMICOLON.op, beginAtrribDefinitionIndex);
			/*
			 * Now we have to find the layout keyword if there is one to define location or
			 * use the in file location instead.
			 */
			String attribLine = source.substring(beginAtrribDefinitionIndex, endOfDefinitionIndex);
			// System.out.println(attribLine);
			String attribName = attribLine.substring(attribLine.indexOf(' ') + 1, attribLine.length());
			String attribType = attribLine.substring(0, attribLine.indexOf(' '));
			/*
			 * If there is a layout qualifier than there will be a empty space then a
			 * Parenthesis end char right before the in.
			 */
			int parenthesisEndIndex = inStartIndex; // = source.lastIndexOf(Operators.PARANTHESIS_END.op, inStartIndex -
													// 1);
			while (Character.isWhitespace(source.charAt(parenthesisEndIndex - 1))) {
				parenthesisEndIndex--;
			}
			/*
			 * We substring the the in and the end parenthesis and then remove white space,
			 * if they are right next to each other than we know that the layout belongs to
			 * this attribute.
			 */
			String temp = source.substring(parenthesisEndIndex - 1, inStartIndex + 1);
			temp = cleanLine(temp).replaceAll("\\s+", "");
			if (Operators.PARANTHESIS_END.op.equals(Character.toString(temp.charAt(0))) && temp.charAt(1) == 'i') {
				/*
				 * Qualifier, Value
				 */
				Map<Qualifiers, String> qualifierTokens;
				/*
				 * Now that we have the end of the parenthesis we can find the layout's index
				 * which gives us
				 */
				int layoutIndex = source.lastIndexOf(Keywords.LAYOUT.word, parenthesisEndIndex);
				int parenthesisStartIndex = source.indexOf(Operators.PARANTHESIS_BEGIN.op,
						layoutIndex + Keywords.LAYOUT.word.length());
				qualifierTokens = extractQualifiers(
						source.substring(parenthesisStartIndex + 1, parenthesisEndIndex - 1));
				String v = qualifierTokens.get(Qualifiers.LOCATION);
				if (v == null) {
					throw new AEShaderCompilerRuntimeException(
							"Invalid Layout invocation, no index for location provided. Shader: " + program.getName());
				}
				inLocation = Integer.parseInt(v);
			}
			bindAttribute(program, inLocation, attribName);
			/*
			 * Increment the location for the next cycle.
			 */
			inLocation++;
			inStartIndex = source.indexOf(Keywords.IN.word, endOfDefinitionIndex);
		}

	}

	private static void bindAttribute(AEShaderResource program, int index, String attribname) {
		GL45.glBindAttribLocation(program.getProgram(), index, attribname);
	}

	/**
	 * Extracts qualifiers from the layout keyword. layout(qualifier1,
	 * qualifier2,...)
	 * 
	 * @param layout
	 * @return
	 */
	private static HashMap<Qualifiers, String> extractQualifiers(String layout) {
		HashMap<Qualifiers, String> qualifierTokens = new HashMap<>();
		String[] qualifiers = layout.replaceAll("\\s+", "").split(",");
		for (int i = 0; i < qualifiers.length; i++) {
			String[] values = qualifiers[i].split("=");
			Qualifiers q = Qualifiers.valueOf(values[0].toUpperCase());
			qualifierTokens.put(q, (values.length > 1) ? values[1] : null);
		}
		return qualifierTokens;
	}

	public static void setAttribLocation(int program, String attribName, int location) {
		GL20.glBindAttribLocation(program, location, attribName);
	}

	/*
	 * TODO:
	 */
	/**
	 * Automatically binds uniforms found in program source provided.
	 */
	private static void processUniforms(AEShaderResource program, ArrayList<ShaderSource> _unProccessedShaders) {
		out.println("---Processing Uniforms---");
		for (ShaderSource s : _unProccessedShaders) {
			float start = System.nanoTime();
			out.println("----Currently Proccessing Source: " + s.name + "----");
			Map<String, GLSLStruct> structs = processStructs(s.source);
			/*
			 * A list to track all the buffers used in this program, or if they don't exist
			 * throw an exception.
			 */
			ArrayList<GLSLUniformBlockObject> UBOs = new ArrayList<GLSLUniformBlockObject>();

			// Find the start of the uniform keyword.
			int uniformStartIndex = s.source.indexOf(Keywords.UNIFORM.word);

			// Loop while there is a new index.
			while (uniformStartIndex != -1) {
				/*
				 * Check if the Keyword is a token and not part of a word.
				 */
				if (!(uniformStartIndex != 0
						&& (Character.isWhitespace(s.source.charAt(uniformStartIndex - 1)) || Operators.SEMICOLON.op
								.equals(Character.toString(s.source.charAt(uniformStartIndex - 1))))
						&& Character
								.isWhitespace(s.source.charAt(uniformStartIndex + Keywords.UNIFORM.word.length())))) {
					uniformStartIndex = s.source.indexOf(Keywords.UNIFORM.word,
							uniformStartIndex + Keywords.UNIFORM.word.length());
					continue;
				}

				int beginUniformDefinitionIndex = uniformStartIndex + Keywords.UNIFORM.word.length();
				boolean isBlock = false;
				int lineEnd = beginUniformDefinitionIndex;
				/*
				 * Loop while it doesn't reach a semicolon (line end).
				 * 
				 * If the uniform is in fact a block, and has correct syntax, then between the
				 * uniform keyword index and the semicolon index we will find a opening brace.
				 * if not then it means its not a block.
				 */
				while (s.source.charAt(lineEnd + 1) != ';') {
					lineEnd++;
					if (s.source.charAt(lineEnd) == '{') {
						isBlock = true;
					}
				}

				/*
				 * Figure out if this is a uniform we care about or its a uniform def that sets
				 * defaults for the program.
				 */
				// TODO: Use defaults for uniforms that don't have a layout-qualifier.
				if (s.source.substring(beginUniformDefinitionIndex, lineEnd + 1).isBlank()) {
					continue;
				}

				/*
				 * There has to be a line end before this or it is at the start of the file.
				 */
				int lastLineEnd = ((lastLineEnd = s.source.lastIndexOf(Operators.SEMICOLON.op,
						uniformStartIndex)) == -1) ? 0 : lastLineEnd;

				/*
				 * Now we find if there is a layout def between the last line end and this one.
				 */
				String potentialLayoutDef = s.source.substring(lastLineEnd, uniformStartIndex).trim();
				/*
				 * Now we actually look for the specific definition of the layout keyword.
				 */
				int layoutIndex = potentialLayoutDef.indexOf(Keywords.LAYOUT.word);
				/*
				 * If its there then only one of it should be there so we can just look for (
				 * and ) and send this to extract qualifiers.
				 */
				HashMap<Qualifiers, String> qualifiers = null;
				if (layoutIndex != -1) {
					qualifiers = extractQualifiers(
							potentialLayoutDef.substring(potentialLayoutDef.indexOf(Operators.PARANTHESIS_BEGIN.op) + 1,
									potentialLayoutDef.indexOf(Operators.PARANTHESIS_END.op)));
				}

				// If the check for this being a block is false then we treat this as a normal
				// typed uniform.
				if (!isBlock) {
					int beginUniformTypeIndex = beginUniformDefinitionIndex;
					while (Character.isWhitespace(s.source.charAt(beginUniformTypeIndex + 1))) {
						beginUniformTypeIndex++;
					}

					int endUniformTypeIndex = beginUniformTypeIndex + 1;
					while (!Character.isWhitespace(s.source.charAt(endUniformTypeIndex + 1))
							&& !(isBlock = Operators.CURLEY_BRACKET_BEGIN.op
									.equals(Character.toString(s.source.charAt(endUniformTypeIndex + 1))))) {
						endUniformTypeIndex++;
					}
					String uniformType = s.source.substring(beginUniformTypeIndex, endUniformTypeIndex + 1).trim();
					int beginUniformNameIndex = endUniformTypeIndex;
					while (Character.isWhitespace(s.source.charAt(beginUniformNameIndex + 1))) {
						beginUniformNameIndex++;
					}
					int endUniformNameIndex = beginUniformNameIndex;
					while (!Character.isWhitespace(s.source.charAt(endUniformNameIndex + 1))) {
						endUniformNameIndex++;
					}
					String uniformName = s.source.substring(beginUniformNameIndex, endUniformNameIndex).trim();
					GLSLUniform uniform = new GLSLUniform();
					uniform.name = uniformName;
					uniform.type = uniformType;
					program.getUniforms().put(uniformName, uniform);
					addUniform(program, uniform, structs);
				} else { // Else here we treat it as a block, since blocks arc over all of the programs
							// in the current context we need to handle this separately.
					String UBOName;
					int beginNameIndex = beginUniformDefinitionIndex;
					while (Character.isWhitespace(s.source.charAt(beginNameIndex + 1))) {
						beginNameIndex++;
					}
					int endNameIndex = beginNameIndex + 1;
					while (!Character.isWhitespace(s.source.charAt(endNameIndex + 1))) {
						endNameIndex++;
					}
					UBOName = s.source.substring(beginNameIndex, endNameIndex + 1).trim();
					GLSLUniformBlockObjectData ubod = new GLSLUniformBlockObjectData(UBOName);
					GLSLLayoutQualifier q = new GLSLLayoutQualifier();
					q.layoutQualifierIDList = qualifiers;
					ubod.setQualifiers(q);
					GLSLUniformBlockObject ubo = new GLSLUniformBlockObject(ubod);
					addUniformBufferObject(program, ubo, structs);
				}
				uniformStartIndex = s.source.indexOf(Keywords.UNIFORM.word,
						uniformStartIndex + Keywords.UNIFORM.word.length());
			}
			out.println("----Done Processing Source----");
		}
	}

	public static void addUniformBufferObject(AEShaderResource program, GLSLUniformBlockObject buffer,
			Map<String, GLSLStruct> structs) {
		int uboIndexInShader = GL31.glGetUniformBlockIndex(program.getProgram(), buffer.getData().getName());
		program.getUbos().put(uboIndexInShader, buffer);
		int definedBinding = 0;
		if (buffer.getData().getQualifiers().layoutQualifierIDList.containsKey(Qualifiers.BINDING)) {
			definedBinding = Integer
					.parseInt(buffer.getData().getQualifiers().layoutQualifierIDList.get(Qualifiers.BINDING));

		} else {
			// TODO: Make a look up table for block bindings specific to the engine, else
			// throw and exception.
		}

	}

	public static void addUniform(AEShaderResource program, GLSLUniform uniform, Map<String, GLSLStruct> structs) {
		boolean addThis = true;
		GLSLStruct struct = structs.get(uniform.type);
		if (struct != null) {
			addThis = false;
			for (GLSLUniform component : struct.components) {
				GLSLUniform temp = new GLSLUniform();
				temp.name = uniform.name + "." + component.name;
				temp.type = component.type;
				addUniform(program, temp, structs);
			}
		}
		if (!addThis) {
			return;
		}
		int uniformLocation = 0;
		// GL20.glGetUniformLocation(program.getProgram(), uniform.name);
		uniform.qualifiers.layoutQualifierIDList.put(Qualifiers.LOCATION, String.valueOf(uniformLocation));
		out.println("Uniform " + "'" + uniform.name + "'" + " At Location: " + uniformLocation);
		if (uniformLocation == 0xFFFFFFFF) {
			logger.warning("Error: Could not find uniform: " + "'" + uniform.name + "'"
					+ "Or it is not being used please check shader code and remove any un-used code or variables.");
			new Exception().printStackTrace();
		}
		program.getUniforms().put(uniform.name, uniform);
	}

	/*
	 * Finds and binds UBOS.
	 */
//	private static GLSLUniformBlockObject processUBO(GLSLUniformBlockObjectData ub) {
	// GLSLUniformBlockObject ubo = new GLSLUniformBlockObject();

	// return ubo;
	// }

	private static Map<String, GLSLStruct> processStructs(String source) {
		Map<String, GLSLStruct> structs = new HashMap<String, GLSLStruct>();
		/*
		 * Find the structs using the keyword.
		 */
		int structStartIndex = source.indexOf(Keywords.STRUCT.word);
		while (structStartIndex != -1) {
			/*
			 * Check if the Keyword is a token and not part of a word.
			 */
			if (!(structStartIndex != 0
					&& (Character.isWhitespace(source.charAt(structStartIndex - 1))
							|| Operators.SEMICOLON.op.equals(Character.toString(source.charAt(structStartIndex - 1))))
					&& Character.isWhitespace(source.charAt(structStartIndex + Keywords.STRUCT.word.length())))) {
				structStartIndex = source.indexOf(Keywords.STRUCT.word,
						structStartIndex + Keywords.STRUCT.word.length());
				continue;
			}

			int beginStructNameDefinitionIndex = structStartIndex + Keywords.STRUCT.word.length() + 1;
			int structBracketBeginIndex = source.indexOf(Operators.CURLEY_BRACKET_BEGIN.op,
					beginStructNameDefinitionIndex);
			int structBracketEndIndex = source.indexOf(Operators.CURLEY_BRACKET_END.op, structBracketBeginIndex);

			String structName = source.substring(beginStructNameDefinitionIndex, structBracketBeginIndex).trim();
			// Semi colon at the end of the struct component.
			int structComponentEndIndex = source.indexOf(Operators.SEMICOLON.op, structBracketBeginIndex);
			GLSLStruct result = new GLSLStruct();
			result.components = new ArrayList<GLSLUniform>();
			while (structComponentEndIndex != -1 && structComponentEndIndex < structBracketEndIndex) {
				int structComponentNameEnd = structComponentEndIndex + 1;
				while (Character.isWhitespace(source.charAt(structComponentNameEnd - 1)) || Operators.SEMICOLON.op
						.equals(Character.toString(source.charAt(structComponentNameEnd - 1)))) {
					structComponentNameEnd--;
				}

				int structComponentNameStart = structComponentEndIndex;
				while (!Character.isWhitespace(source.charAt(structComponentNameStart - 1))) {
					structComponentNameStart--; // Back up a character.
				}

				int structComponentTypeEnd = structComponentNameStart;
				while (Character.isWhitespace(source.charAt(structComponentTypeEnd - 1))) {
					structComponentTypeEnd--;
				}

				int structComponentTypeStart = structComponentTypeEnd;
				while (!Character.isWhitespace(source.charAt(structComponentTypeStart - 1))) {
					structComponentTypeStart--; // Back up a character.
				}

				String componentName = source.substring(structComponentNameStart, structComponentEndIndex);
				String componentType = source.substring(structComponentTypeStart, structComponentTypeEnd);

				GLSLUniform structComponent = new GLSLUniform();
				structComponent.name = componentName;
				structComponent.type = componentType;
				result.components.add(structComponent);
				structComponentEndIndex = source.indexOf(Operators.SEMICOLON.op, structComponentEndIndex + 1);
			}
			result.name = structName;
			structs.put(structName, result);
			structStartIndex = source.indexOf(Keywords.STRUCT.word, structStartIndex + Keywords.STRUCT.word
					.length()); /* This is to prevent it from looping over the same uniform keyword. */
		}
		return structs;
	}

	public static class GLSLStruct {
		ArrayList<GLSLUniform> components;
		String name;
	}

	private static void link(AEShaderResource program) {

		out.println("Compiling Shader: " + program.getName());

		GL20.glLinkProgram(program.getProgram());

		if (GL20.glGetProgrami(program.getProgram(), GL20.GL_LINK_STATUS) == 0) {
			out.println("Error From Shader Program: '" + program.getName() + "'");
			logger.error(GL20.glGetProgramInfoLog(program.getProgram(), 1024));
			throw new AEShaderCompilerRuntimeException("Error Linking Program: " + program.getName());
		}

		GL20.glValidateProgram(program.getProgram());

		if (GL20.glGetProgrami(program.getProgram(), GL20.GL_VALIDATE_STATUS) == 0) {
			out.println("Error From Shader Program: '" + program.getName() + "'");
			logger.error(GL20.glGetProgramInfoLog(program.getProgram(), 1024));
			throw new AEShaderCompilerRuntimeException("Error Validating Program: " + program.getName());
		}

		out.println("Shader Compiled Successfully");

	}

	/**
	 * Attaches programs to the current shader resource and validates them.
	 * 
	 * @param sources
	 */
	private static void attach(AEShaderResource program, ArrayList<ShaderSource> sources) {
		for (ShaderSource shaderSource : sources) {
			if (shaderSource.type != AEShaderType.AE_SHADER_IMPORT
					&& shaderSource.type != AEShaderType.AE_COMPUTE_SHADER) {
				/*
				 * Attach each program
				 */
				addProgram(program, shaderSource);
			}
		}
	}

	/**
	 * Adds a program to the current shader program.
	 * 
	 * @param text
	 * @param type
	 */
	private static void addProgram(AEShaderResource program, ShaderSource s) {
		out.println("---Creating shader: " + s.name + " with source " + s.source + "---");
		int shader = GL20.glCreateShader(s.type.glType);
		if (shader == 0) {
			out.println("Error From Shader Program: '" + program.getName() + "'");
			logger.error("Shader creation failed: Could not find valid memory location when adding shader");
			throw new AEShaderCompilerRuntimeException(
					"Could not create program: " + program.getName() + " Path:" + program.getPath());
		}

		out.println("----Valadating----");
		GL20.glShaderSource(shader, s.source);
		GL20.glCompileShader(shader);
		out.println("Success");
		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {
			out.println("Error From Shader Program: '" + program.getName() + "'");
			logger.error(GL20.glGetShaderInfoLog(shader, 2048));
			throw new AEShaderCompilerRuntimeException(
					"Could not create program: " + program.getName() + " Path:" + program.getPath());
		}

		out.println("----Attaching----");
		GL20.glAttachShader(program.getProgram(), shader);
		// TODO: attach status
		out.println("Successfully Attached Shader: " + program.getProgram() + " Log: " + "\n"
				+ GL20.glGetShaderInfoLog(shader, 1024));
		logger.finest("Shader Text For '" + s.type.toString() + "': '" + program.getName() + "'\n" + s.source);

		GL20.glDeleteShader(shader);
		// TODO:DeleteStatus
	}

	private static void loadAllImportsInFile(AEShaderFileYAML file) {
		for (AEShaderGLSLProgram p : file.getAE_SHADER_GLSL_PROGRAMS()) {
			if (AEShaderType.valueOf(p.getAE_SHADER_GLSL_PROGRAM_TYPE()) == AEShaderType.AE_SHADER_IMPORT) {
				loadedImports.put(file.getAE_SHADER_NAME(), p.getAE_SHADER_GLSL_PROGRAM_NAME(), p);
			}
		}
	}

	private static String processShaderSource(String gp, String callingProgram) {
		String[] lines = gp.split("\\r?\\n");
		StringBuilder proccessedSource = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			line = cleanLine(line);
			if (line.isEmpty() || line.isBlank())
				continue;
			if (line.startsWith(Tokens.AE_IMPORT_DIRECTIVE)) {
				proccessedSource.append(processImportLine(line, callingProgram));
			} else if (line.startsWith(Tokens.AE_INCLUDE_DIRECTIVE)) {
				proccessedSource.append(processIncludeLine(line, callingProgram));
			} else {
				if (!line.isEmpty() && !line.isBlank()) {
					proccessedSource.append(line).append("\n");
				}
			}
		}
		return proccessedSource.toString();
	}

	/**
	 * 
	 * @param line
	 * @param callingProgram
	 * @return
	 */
	private static String processIncludeLine(String line, String callingProgram) {
		/* Optimize the loading process. */
		String file;
		StringBuilder source = new StringBuilder();
		file = line.replace(Tokens.AE_INCLUDE_DIRECTIVE, "").trim();
		if (file.equals(callingProgram)) {
			throw new AEShaderCompilerRuntimeException(
					"Recursive Overflow Detected, This include is calling it self. Program: " + callingProgram);
		}
		File f = DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild(file).getFileInstance();
		BufferedReader shaderReader = null;
		try {
			shaderReader = new BufferedReader(new FileReader(f));
			String _line;
			while ((_line = shaderReader.readLine()) != null) {
				source.append(_line).append("\n");
			}
			shaderReader.close();
		} catch (Exception e) {
			logger.error("Unable to parse shader " + f.getName(), e);
			logger.info("Exiting...");
			throw new AEShaderCompilerRuntimeException("Enable to parse shader.", e);
		}
		out.println("Including file (" + file + ") in shader: " + callingProgram);
		return processShaderSource(source.toString(), file);
	}

	private static String processImportLine(String line, String callingProgram) {
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
		if (!line.startsWith(Tokens.AE_IMPORT_DIRECTIVE)) {
			return "";
		}
		if (Character.isWhitespace(line.charAt(Tokens.AE_IMPORT_DIRECTIVE.length()))) {
			int importFileSeperatorIndex = 0;
			temp = line.substring(Tokens.AE_IMPORT_DIRECTIVE.length(), line.length());
			temp = temp.replaceAll("\\s+", ""); // Clear all the white space.
			/*
			 * We substring the import line from the start of the temp to the : char. And
			 * while we do so we also define the index of it to use to separate the program
			 * tokens.
			 */
			importFile = temp.substring(0, (importFileSeperatorIndex = temp.indexOf(Operators.COLON.op)));
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
				programs[i] = programs[i].replaceAll("\"", "").replaceAll("\\s+", "").replaceAll(";", "");
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

	private static String processImport(String programName, String fileName) {
		StringBuilder program = new StringBuilder();
		AEShaderGLSLProgram gp = null;
		/*
		 * Check if the import was in this file or was loaded before.
		 */
		if ((gp = loadedImports.get(fileName + Tokens.AE_SHADER_FILE_TYPE, programName)) == null) {
			/*
			 * If it wasn't try finding it then loading it.
			 */
			loadAllImportsInFile(AEShaderParserYAML
					.parse(DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild(fileName + Tokens.AE_SHADER_FILE_TYPE)));
			/*
			 * If we still can't find it then give up and go throw a exception.
			 */
			if ((gp = loadedImports.get(fileName + Tokens.AE_SHADER_FILE_TYPE, programName)) == null) {
				throw new AEShaderCompilerRuntimeException("Import in file not fo)und : " + fileName
						+ Tokens.AE_SHADER_FILE_TYPE + " Import: " + programName);
			}
		}
		/*
		 * Now that we have determined the file exists we can continue. Or we have not
		 * and the program exits. Now we have to find that program.
		 */
		/*
		 * If the program doesn't exist we throw an exception.
		 */
		if ((gp = loadedImports.get(fileName + Tokens.AE_SHADER_FILE_TYPE, programName)) == null) {
			throw new AEShaderCompilerRuntimeException("Shader Import not found in file: " + fileName
					+ Tokens.AE_SHADER_FILE_TYPE + " Import: " + programName);
		}
		/*
		 * Otherwise we recursively process it and append it to the original source.
		 */
		program.append(processShaderSource(gp.getAE_SHADER_GLSL_PROGRAM_SOURCE(), gp.getAE_SHADER_GLSL_PROGRAM_NAME()));
		return program.toString();
	}

	private static String cleanLine(String line) {
		String returnLine;
		returnLine = line.replaceAll("[\\n\\t ]", "");
		returnLine = line = line.trim();
		if (line.startsWith(Tokens.COMMENT_PREFIX)) {
			returnLine = line = "";
			return returnLine;
		}
		/*
		 * Cleans the line of comments and such.
		 */
		int commentindex;
		if (line.contains(Tokens.COMMENT_PREFIX)) {
			commentindex = line.indexOf(Tokens.COMMENT_PREFIX);
			returnLine = line.substring(0, commentindex);

		}
		return (returnLine = line.trim());
	}

	public static void printLibVersionInfo() {
		out.println("-------AE-Shader-Compiler-----");
		out.println("AE Shader Compiler Version: " + CURRENT_VERSION);
		printDeviceCapabilities();
		out.println("-------AE-Shader-Compiler-----");
	}

	public static void printDeviceCapabilities() {
		out.println("Graphics Vendor: " + GL45.glGetString(GL40.GL_VENDOR));
		out.println("Graphics Renderer: " + GL45.glGetString(GL40.GL_RENDERER));
		out.println("Graphics Driver Version: " + GL45.glGetString(GL45.GL_VERSION));
		out.println("Graphics GLSL Language Max Version: " + GL45.glGetString(GL45.GL_SHADING_LANGUAGE_VERSION));
		out.println("Num. GLSL Extensions: " + GL40.glGetInteger(GL40.GL_NUM_EXTENSIONS));
		out.println("Max Geometry Uniform Blocks: " + GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS + " bytes");
		out.println("Max Geometry Shader Invocations: " + GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS + " bytes");
		out.println("Max Uniform Buffer Bindings: " + GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS + " bytes");
		out.println("Max Uniform Block Size: " + GL31.GL_MAX_UNIFORM_BLOCK_SIZE + " bytes");
		out.println("Max SSBO Block Size: " + GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE + " bytes");
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
