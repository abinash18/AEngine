package net.abi.abisEngine.rendering.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;

import net.abi.abisEngine.components.DirectionalLight;
import net.abi.abisEngine.components.Light;
import net.abi.abisEngine.components.PointLight;
import net.abi.abisEngine.components.SpotLight;
import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.file.PathType;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.math.Matrix4f;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.math.Vector2f;
import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.math.Vector3i;
import net.abi.abisEngine.math.Vector4f;
import net.abi.abisEngine.rendering.RenderingEngine;
import net.abi.abisEngine.rendering.asset.AssetI;
import net.abi.abisEngine.rendering.resourceManagement.Material;
import net.abi.abisEngine.rendering.resourceManagement.Texture;
import net.abi.abisEngine.util.Util;

public class Shader implements AssetI {

	public static final Shader DEFAULT_SHADER = null;
	private static final PathHandle DEFAULT_SHADER_ASSET_DIRECTORY_PATH = new PathHandle("./res/shaders/",
			PathType.Internal);

	private static Logger logger = LogManager.getLogger(Shader.class);

	private static HashMap<Long, HashMap<String, ShaderResource>> loadedShaders = new HashMap<Long, HashMap<String, ShaderResource>>();

	/**
	 * The data is managed separately from the MeshResources since there can be
	 * multiple GLContexts
	 */
	private static HashMap<ShaderType, HashMap<String, ShaderSource>> loadedSources = new HashMap<ShaderType, HashMap<String, ShaderSource>>();

	private Long context_handle;
	private ShaderSource shaderData;
	private ShaderResource shaderProgram;

	public static enum ShaderType {
		VERTEX(".glvs"), FRAGMENT(".glfs"), GEOMETRY(".glgs"), TESSELATION_EVALUATION(".glte"),
		TESSELATION_CONTROL(".gltc"), COMPUTE(".glc"), HEADER(".glh");

		public String extention;

		private ShaderType(String extention) {
			this.extention = extention;
		}

	}

	private PathHandle pathToShaderDirectoryInDefaultDirectory;
	private String shaderName;

	/**
	 * Loads Or References a shader in the private context, which means where ever
	 * this shader was created thats were it will be usable since the context handle
	 * for this is 0. This shader cannot be used universally.
	 * 
	 * @param shaderName
	 */
	public Shader(String shaderName) {
		this(shaderName, 0L);
	}

	/**
	 * Creates a shader found in the shader assets directory.
	 * 
	 * @param shaderName
	 */
	public Shader(String shaderName, long context) {
		this.shaderName = shaderName;

		this.pathToShaderDirectoryInDefaultDirectory = DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild(shaderName);

		this.context_handle = Long.valueOf(context);

		/*
		 * Find the Context in the cache.
		 */
		HashMap<String, ShaderResource> _ss = loadedShaders.get(Long.valueOf(context_handle));

		/*
		 * If the Context was not found then create a map and add it to the cache.
		 */
		if (_ss == null) {
			_ss = new HashMap<String, ShaderResource>();
			loadedShaders.put(context_handle, _ss);
		}

		ShaderResource _sr;

		/*
		 * Find the ShaderResource in the Context thats found or was newly created.
		 */
		if ((_sr = _ss.get(shaderName)) != null) {
			/*
			 * If the resource was found in the context then increment the references and we
			 * are done.
			 */
			_sr.incRefs();

		} else {
			/*
			 * If it wasn't found then we create one and get the shader ready to use.
			 */
			_sr = new ShaderResource(shaderName, pathToShaderDirectoryInDefaultDirectory);
			_ss.put(shaderName, _sr);
		}

		this.shaderProgram = _sr;

		for (ShaderType t : ShaderType.values()) {
			PathHandle _h;
			if (!(_h = pathToShaderDirectoryInDefaultDirectory.resolveChild(shaderName + t.extention)).exists()) {
				logger.debug("Skipped File: " + _h);
				continue;
			}
			/* Find the type of shader in the cache. */
			HashMap<String, ShaderSource> _shd = loadedSources.get(t);

			/*
			 * If the shader types dose not exist then add it to the cache.
			 */
			if (_shd == null) {
				loadedSources.put(t, (_shd = new HashMap<String, ShaderSource>()));
			}

			/*
			 * Find the ShaderSource in the received cache.
			 */
			ShaderSource _data = _shd.get(shaderName);

			/*
			 * If the source dose not exist then add it to the cache map.
			 */
			if (_data == null) {
				/*
				 * So we load the source using the loadShaderSource Method
				 */
				_data = loadShaderSource(pathToShaderDirectoryInDefaultDirectory.resolveChild(shaderName + t.extention),
						t);
				_shd.put(shaderName, _data);
			} else {
				/**
				 * If the source exits in the cache. We already have the source stored in the
				 * temporary variable. So we don't need to do any thing here.
				 */
			}

			/*
			 * Add the source to the resource subProgram list.
			 */
			this.shaderProgram.addShaderSource(t, _data);

			this.addShader(t, _data);

		}

		shaderProgram.getSubPrograms().forEach((k, v) -> {
			if (k == ShaderType.VERTEX) {
				this.addAllAttributes(v);
				this.compileShader();
			}
			this.addAllUniforms(v);
		});
	}

	public void bind() {
		GL20.glUseProgram(shaderProgram.getProgram());
	}

	public void updateUniforms(Transform transform, Material mat, RenderingEngine engine) {

		Matrix4f worldMatrix = transform.getTransformation(),
				MVPMatrix = engine.getMainCamera().getViewProjection().mul(worldMatrix), MVNMatrix = worldMatrix;
		MVNMatrix.transpose().invertGeneric();

		for (int i = 0; i < shaderProgram.getUniformNames().size(); i++) {

			/*
			 * These Were Added In The Same Order And Time So They Should Have The Same
			 * Index.
			 */
			String uniformName = shaderProgram.getUniformNames().get(i);
			String uniformType = shaderProgram.getUniformTypes().get(i);

			if (uniformName.equals("normal_mapping_enabled")) {
				setUniformi(uniformName, 1);
			}

			if (uniformType.equals("sampler2D")) {
				int samplerSlot = engine.getSamplerSlot(uniformName);
				Texture tex = mat.getTexture(uniformName);
				if (tex != null) {
					tex.bind(samplerSlot);
				}
				setUniformi(uniformName, samplerSlot);
			} else if (uniformName.startsWith("T_")) {
				if (uniformName.equals("T_MVP")) {
					setUniformMatrix4fv(uniformName, MVPMatrix);
					// logger.finest("Added '" + uniformName + "' as MVP Matrix.");
				} else if (uniformName.equals("T_model")) {
					setUniformMatrix4fv(uniformName, worldMatrix);
					// logger.finest("Added '" + uniformName + "' as World Matrix.");
				} else if (uniformName.equals("T_MVN")) {
					setUniformMatrix4fv(uniformName, MVNMatrix);
				} else {
					logger.error("'" + uniformName
							+ "' is not a valid component of transform. Or is misspelled, please check shader program or change the prefix of the variable.",
							new IllegalArgumentException(
									"'" + uniformName + "' is not a valid component of transform."));
					// CoreEngine.exit(1);
				}
			} else if (uniformName.startsWith("R_")) {
				String unprefixedUniformName = uniformName.substring(2);
				if (uniformType.equals("vec3")) {
					setUniform3f(uniformName, engine.getVector3f(unprefixedUniformName));
				} else if (uniformType.equals("float")) {
					setUniformf(uniformName, engine.getFloat(unprefixedUniformName));
				} else if (uniformType.equals("DirectionalLight")) {
					setUniformDirectionalLight(uniformName, (DirectionalLight) engine.getActiveLight());
				} else if (uniformType.equals("PointLight")) {
					setUniformPointLight(uniformName, (PointLight) engine.getActiveLight());
				} else if (uniformType.equals("SpotLight")) {
					setUniformSpotLight(uniformName, (SpotLight) engine.getActiveLight());
				} else {
					engine.updateUniformStruct(transform, mat, this, unprefixedUniformName, uniformType);
				}
			} else if (uniformName.startsWith("C_")) {
				if (uniformName.equals("C_eyePos")) {
					setUniform3f(uniformName, engine.getMainCamera().getTransform().getTransformedPosition());
				} else {
					logger.error("'" + uniformName
							+ "' is not a valid component of Camera. Or is misspelled, please check shader program or change the prefix of the variable.",
							new IllegalArgumentException("'" + uniformName + "' is not a valid component of Camera."));
				}
			} else {

				if (uniformType.equals("vec3")) {
					setUniform3f(uniformName, mat.getVector3f(uniformName));
				} else if (uniformType.equals("float")) {
					setUniformf(uniformName, mat.getFloat(uniformName));
				}
			}
		}
	}

	private class GLSLStruct {
		public String name, type;
	}

	public HashMap<String, ArrayList<GLSLStruct>> findUniformStructs(String shaderText) {

		// System.out.println("Attempting To Add Uniforms Automatically...");

		HashMap<String, ArrayList<GLSLStruct>> result = new HashMap<String, ArrayList<GLSLStruct>>();

		final String STRUCT_KEYWORD = "struct", LINE_END_CHAR = ";", OPENING_BRACKET = "{", CLOSING_BRACKET = "}";

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

			// String structInside = shaderText.substring(bracketBegin + 1,
			// braceEnd).trim();
			// System.out.println(structInside);

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

				// structComponents.add(shaderText.substring(componentNameStart,
				// componentLineEndCharPos));

				// System.out.println("'" + shaderText.substring(componentNameStart,
				// componentLineEndCharPos) + "'");

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
		this.addAllUniforms(source.getSource());
	}

	public void addAllUniforms(String shaderText) {

		HashMap<String, ArrayList<GLSLStruct>> structs = findUniformStructs(shaderText);

		// System.out.println("Attempting To Add Uniforms Automatically...");

		final String UNIFORM_KEYWORD = "uniform", LINE_END_CHAR = ";";

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

			shaderProgram.getUniformNames().add(uniformName);
			shaderProgram.getUniformTypes().add(uniformType);

			this.addUniform(uniformName, uniformType, structs);

			// this.addUniform(uniformName);

			// System.out.println("'" + uniformLine + "' Extracted Text: '" + uniformName +
			// "'");

			uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD, uniformStartLocation
					+ UNIFORM_KEYWORD.length()); /* This is to prevent it from looping over the same uniform keyword. */
		}

		// System.out.println("Finished Adding Uniforms From File.");

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
				// System.out.println(uniformName + "." + struct.name);
			}

		}

		if (!addThis) {
			return;
		}
		// this.addUniform(uniformName);

		int uniformLocation = GL20.glGetUniformLocation(shaderProgram.getProgram(), uniformName);
		logger.fine("Uniform " + "'" + uniformName + "'" + " At Location: " + uniformLocation);

		/*
		 * System.out.println("Uniform " + "'" + uniform + "'" + " At Location: " +
		 * uniformLocation);
		 */

		if (uniformLocation == 0xFFFFFFFF) {
			/* Change level of this message to warning or informal */

			logger.warning("Error: Could not find uniform: " + "'" + uniformName + "'"
					+ "Or it is not being used please check shader code and remove any un-used code or variables.");

			/*
			 * System.err.println("Error: Could not find uniform: " + "'" + uniform + "'" +
			 * "Or it is not being used please check shader code and remove any un-used code or variables."
			 * );
			 */
			new Exception().printStackTrace();
			/* System.exit(1); */
		}

		shaderProgram.getUniforms().put(uniformName, uniformLocation);

	}

	public void addAllAttributes(ShaderSource source) {
		this.addAllAttributes(source.getSource());
	}

	public void addAllAttributes(String shaderText) {

		// System.out.println("Attempting To Add Attributes Automatically...");
		/*
		 * Oh this one wasn't easy on the brain. I had to re do the entire shader class
		 * just to find out that it was just the wrong keyword I had uniform here
		 * instead of attribute.
		 */
		final String ATTRIBUTE_KEYWORD = "attribute", LINE_END_CHAR = ";";

		int attribStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD);
		int attribLocationIndex = 0;

		while (attribStartLocation != -1) {

			if (!(attribStartLocation != 0
					&& (Character.isWhitespace(shaderText.charAt(attribStartLocation - 1))
							|| shaderText.charAt(attribStartLocation - 1) == ';')
					&& Character.isWhitespace(shaderText.charAt(attribStartLocation + ATTRIBUTE_KEYWORD.length())))) {
				attribStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD,
						attribStartLocation + ATTRIBUTE_KEYWORD.length());
				continue;

			}

			/*
			 * Calculates the starting position of the Attribute variable type and name
			 * definition in the line.
			 */
			int beginAtrribDefinitionIndex = attribStartLocation + ATTRIBUTE_KEYWORD.length() + 1;

			// Finds the line end character and returns the index of it.
			int endOfDefinitionIndex = shaderText.indexOf(LINE_END_CHAR, beginAtrribDefinitionIndex);

			String attribLine = shaderText.substring(beginAtrribDefinitionIndex, endOfDefinitionIndex);

			// Returns that name of the Attribute skipping the type and one index after the
			// space to the name definition.
			String attribName = attribLine.substring(attribLine.indexOf(' ') + 1, attribLine.length());

			// System.out.println("'" + attribLine + "' Extracted Text: '" + attribName +
			// "'");

			// Sets the attribute using attrib name and the attrib index counter and then
			// increments it for the next one.
			this.setAttribLocation(attribName, attribLocationIndex);
			attribLocationIndex++;

			attribStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD, attribStartLocation + ATTRIBUTE_KEYWORD
					.length()); /* This is to prevent it from looping over the same uniform keyword. */
		}

		// System.out.println("Finished Adding Attributes From File.");

	}

	@Deprecated
	public void addUniform(String uniform) {
		int uniformLocation = GL20.glGetUniformLocation(shaderProgram.getProgram(), uniform);
		logger.fine("Uniform " + "'" + uniform + "'" + " At Location: " + uniformLocation);

		/*
		 * System.out.println("Uniform " + "'" + uniform + "'" + " At Location: " +
		 * uniformLocation);
		 */

		if (uniformLocation == 0xFFFFFFFF) {
			/* Change level of this message to warning or informal */

			logger.warning("Error: Could not find uniform: " + "'" + uniform + "'"
					+ "Or it is not being used please check shader code and remove any un-used code or variables.");

			/*
			 * System.err.println("Error: Could not find uniform: " + "'" + uniform + "'" +
			 * "Or it is not being used please check shader code and remove any un-used code or variables."
			 * );
			 */
			new Exception().printStackTrace();
			/* System.exit(1); */
		}

		shaderProgram.getUniforms().put(uniform, uniformLocation);
		shaderProgram.getUniformNames().add(uniform);
	}

	public void setAttribLocation(String attribName, int location) {
		GL20.glBindAttribLocation(shaderProgram.getProgram(), location, attribName);
	}

	public void addVertexShader(String text) {
		addProgram(text, GL20.GL_VERTEX_SHADER, "Vertex Shader");
	}

	public void addShader(ShaderType type, ShaderSource source) {
		switch (type) {
		case VERTEX:
			this.addVertexShader(source);
			break;
		case FRAGMENT:
			this.addFragmentShader(source);
			break;
		case GEOMETRY:
			this.addGeometryShader(source);
			break;
		case COMPUTE:
			break;
		case TESSELATION_CONTROL:
			this.addTesselationControlShader(source);
			break;
		case TESSELATION_EVALUATION:
			this.addTesselationEvaluationShader(source);
			break;

		default:
			break;
		}
	}

	public void addVertexShader(ShaderSource source) {
		addProgram(source.getSource(), GL20.GL_VERTEX_SHADER, "Vertex Shader");
	}

	public void addGeometryShader(String text) {
		addProgram(text, GL32.GL_GEOMETRY_SHADER, "Geometry Shader");
	}

	public void addGeometryShader(ShaderSource source) {
		addProgram(source.getSource(), GL32.GL_GEOMETRY_SHADER, "Geometry Shader");
	}

	public void addFragmentShader(String text) {
		addProgram(text, GL20.GL_FRAGMENT_SHADER, "Fragment Shader");
	}

	public void addFragmentShader(ShaderSource source) {
		addProgram(source.getSource(), GL20.GL_FRAGMENT_SHADER, "Fragment Shader");
	}

	/**
	 * GL40 Shader Program.
	 * 
	 * @param text
	 */
	public void addTesselationControlShader(String text) {
		addProgram(text, GL40.GL_TESS_CONTROL_SHADER, "Tesselation Control Shader");
	}

	public void addTesselationControlShader(ShaderSource source) {
		addProgram(source.getSource(), GL40.GL_TESS_CONTROL_SHADER, "Tesselation Control Shader");
	}

	public void addTesselationEvaluationShader(ShaderSource source) {
		addProgram(source.getSource(), GL40.GL_TESS_EVALUATION_SHADER, "Tesselation Evaluation Shader");
	}

//	public void addVertexShaderFromFile(String fileName) {
//		addProgram(loadShader(fileName), GL20.GL_VERTEX_SHADER, "Vertex Shader");
//	}
//
//	public void addGeometryShaderFromFile(String fileName) {
//		addProgram(loadShader(fileName), GL32.GL_GEOMETRY_SHADER, "Geometry Shader");
//	}
//
//	public void addFragmentShaderFromFile(String fileName) {
//		addProgram(loadShader(fileName), GL20.GL_FRAGMENT_SHADER, "Fragment Shader");
//	}

	public void compileShader() {
		GL20.glLinkProgram(shaderProgram.getProgram());

		if (GL20.glGetProgrami(shaderProgram.getProgram(), GL20.GL_LINK_STATUS) == 0) {
			logger.debug("Error From Shader Program: '" + shaderProgram.getName() + "'");
			logger.error(GL20.glGetProgramInfoLog(shaderProgram.getProgram(), 1024));
			System.exit(1);
		}

		GL20.glValidateProgram(shaderProgram.getProgram());

		if (GL20.glGetProgrami(shaderProgram.getProgram(), GL20.GL_VALIDATE_STATUS) == 0) {
			logger.debug("Error From Shader Program: '" + shaderProgram.getName() + "'");
			logger.error(GL20.glGetProgramInfoLog(shaderProgram.getProgram(), 1024));
			System.exit(1);
		}
	}

	public void addProgram(String text, int type, String text_type) {
		int shader = GL20.glCreateShader(type);

		if (shader == 0) {
			logger.debug("Error From Shader Program: '" + shaderProgram.getName() + "'");
			logger.error("Shader creation failed: Could not find valid memory location when adding shader");
			logger.info("Exiting...");
			System.exit(1);
		}

		GL20.glShaderSource(shader, text);
		GL20.glCompileShader(shader);

		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {
			logger.debug("Error From Shader Program: '" + shaderProgram.getName() + "'");
			logger.error(GL20.glGetShaderInfoLog(shader, 2048));
			logger.info("Exiting...");
			// System.err.println(GL20.glGetShaderInfoLog(shader, 1024));
			System.exit(1);
		}

		GL20.glAttachShader(shaderProgram.getProgram(), shader);

		logger.fine("Successfully Attached Shader: " + shaderProgram.getProgram() + " Log: " + "\n"
				+ GL20.glGetShaderInfoLog(shader, 1024));
		logger.debug("Shader Text For '" + text_type + "': '" + shaderProgram.getName() + "'\n" + text);
	}

	/**
	 * 0 - true, 1 - false;
	 * 
	 * @param uniformName
	 * @param value
	 */
	public void setUniformBoolean(String uniformName, boolean value) {

		int b_v = 1;

		if (value) {
			b_v = 0;
		}

		setUniformi(uniformName, b_v);
	}

	public void setUniformi(String uniformName, int value) {
		GL40.glUniform1i(shaderProgram.getUniforms().get(uniformName), value);
	}

	public void setUniformiv(String uniformName, int[] value) {
		GL20.glUniform1iv(shaderProgram.getUniforms().get(uniformName), value);
	}

	public void setUniformf(String uniformName, float value) {
		GL20.glUniform1f(shaderProgram.getUniforms().get(uniformName), value);
	}

	public void setUniformfv(String uniformName, float[] value) {
		GL20.glUniform1fv(shaderProgram.getUniforms().get(uniformName), value);
	}

	public void setUniform2f(String uniformName, Vector2f value) {
		/* This was used before updating to lwjgl 3 */
		// GL20.glUniform3f(shaderProgram.getUniforms().get(uniformName), value.getX(),
		// value.getY(), 0);
		GL20.glUniform2f(shaderProgram.getUniforms().get(uniformName), value.x(), value.y());
	}

	public void setUniform2fv(String uniformName, float[] value) {
		GL20.glUniform2fv(shaderProgram.getUniforms().get(uniformName), value);
	}

	public void setUniform2iv(String uniformName, int[] value) {
		GL20.glUniform2iv(shaderProgram.getUniforms().get(uniformName), value);
	}

	public void setUniform3f(String uniformName, Vector3f value) {
		GL20.glUniform3f(shaderProgram.getUniforms().get(uniformName), value.x(), value.y(), value.z());
	}

	public void setUniform3fv(String uniformName, float[] value) {
		GL20.glUniform3fv(shaderProgram.getUniforms().get(uniformName), value);
	}

	public void setUniform3i(String uniformName, Vector3i value) {
		GL32.glUniform3i(shaderProgram.getUniforms().get(uniformName), value.getX(), value.getY(), value.getZ());
	}

	public void setUniform3iv(String uniformName, int[] value) {
		GL32.glUniform3iv(shaderProgram.getUniforms().get(uniformName), value);
	}

	public void setUniform3ui(String uniformName, Vector3i value) {
		GL32.glUniform3ui(shaderProgram.getUniforms().get(uniformName), value.getX(), value.getY(), value.getZ());
	}

	public void setUniform3uiv(String uniformName, int[] value) {
		GL32.glUniform3uiv(shaderProgram.getUniforms().get(uniformName), value);
	}

	public void setUniform4f(String uniformName, Vector4f value) {
		GL32.glUniform4f(shaderProgram.getUniforms().get(uniformName), value.x(), value.y(), value.z(), value.w());
	}

	/**
	 * All Matrices by default are transposed while loading.
	 */
	// TODO: All Other types of matrices.

	public void setUniformMatrix4fv(String uniformName, Matrix4f value) {
		GL20.glUniformMatrix4fv(shaderProgram.getUniforms().get(uniformName), true, Util.createFlippedBuffer(value));
	}

	// public static String loadShader(String fileName) {
	/**
	 * Loads a shader from the path to the shader directory and the type. Resolves a
	 * file from the path and then loads.
	 * 
	 * @param type
	 * @return
	 */
	public static ShaderSource loadShaderSource(PathHandle pathToShader, ShaderType type) {
		StringBuilder shaderSource = new StringBuilder();
		BufferedReader shaderReader = null;

		final String INCLUDE_DIRECTIVE = "#include", COMMENT_PREFIX = "//", NATIVE_IMPORT_DIRECTIVE = "#import";

		// glsl include

		try {
			shaderReader = new BufferedReader(new FileReader(pathToShader.getFileInstance()));
			String line;

			while ((line = shaderReader.readLine()) != null) {

				line.replaceAll("[\\n\\t ]", "");
				line = line.trim();

				if (line.startsWith(COMMENT_PREFIX)) {
					continue;
				}

				/*
				 * Cleans the line of comments and such.
				 */

				int commentindex;

				if (line.contains(COMMENT_PREFIX)) {
					commentindex = line.indexOf(COMMENT_PREFIX);
					line = line.substring(0, commentindex);

				}

				line = line.trim();

				if (line.isEmpty()) {
					continue;
				}

				if (line.startsWith(NATIVE_IMPORT_DIRECTIVE)) {
					processNativeImports(line);
				} else if (line.startsWith(INCLUDE_DIRECTIVE)) {

					/*
					 * #include 'file' INCLUDE_DIRECTIVE length puts it at the end of the include
					 * and i add 2 to move it past the first quote to the file name. AND THEN IT
					 * ENDS AT THE END OF THE LINE NO WHITE SPACE AT THE END.
					 */
					/* This appends the loaded shader to the source of the file */
					/* Loads the shader specified in the #include */
					PathHandle path = DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild(
							"includes/" + line.substring(INCLUDE_DIRECTIVE.length() + 2, line.length() - 1));
					shaderSource.append(loadShaderSource(path, ShaderType.HEADER).getSource());
					/* Figures out the name of the file being included. */

				} else {
					shaderSource.append(line).append("\n");
				}
			}
			shaderReader.close();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Unable to parse shader " + pathToShader.toString(), e);
			logger.info("Exiting...");
			System.exit(1);
		}
		return new ShaderSource(pathToShader.getNameWithoutExtension(), shaderSource.toString(), type);
	}

	/**
	 * @param line
	 */
	private static void processNativeImports(String line) {
		// TODO: Process Natives.
		// In future convert this to a meta file to get generic function file names
		// which can be included in the shader loading process.
		// Like #include or #import AE_Functions:LinearizeDepth
		// Then the loader will look through this meta file and determine the file name
		// of the function and include only that instead of all functions in the file.
		// When loading multiple functions from the file it can be defined as so:
		// #include or #import AE_Functions:{LinearizeDepth, DeLinearizeDepth, ...}
		// The AE_Functions file would be in a format such as JSON or XML

		// The convention will be that all files that contain native functions will have
		// a prefix of AE_
		// There will be a big file like AE_Functions which a compiler will run over and
		// find the functions needed for import.
		// Those functions will have a import notation as well like:

		// #import AE_Functions:DeLinearize
		// float LinearizeDepth(float depth) {
		// float z = depth * 2.0 - 1.0; // back to NDC
		// return (2.0 * near * far) / (far + near - z * (far - near));
		// }

		// And if there is no notation it will mean the function dose not have any
		// Dependencies.

		// The notation will be a line above so the compiler will have to send the line
		// index one above from the function
		// to the processNativeImports(String shaderText) function.
	}

	public int getProgram() {
		return shaderProgram.getProgram();
	}

	public void setProgram(int program) {
		this.shaderProgram.setProgram(program);
	}

	public HashMap<String, Integer> getUniforms() {
		return shaderProgram.getUniforms();
	}

	public void setUniforms(HashMap<String, Integer> uniforms) {
		this.shaderProgram.setUniforms(uniforms);
	}

	public void setUniformLight(String uniformName, Light baseLight) {
		setUniform3f(uniformName + ".color", baseLight.getColor());
		setUniformf(uniformName + ".intensity", baseLight.getIntensity());
	}

	public void setUniformDirectionalLight(String uniformName, DirectionalLight directionalLight) {
		setUniformLight(uniformName + ".base", (Light) directionalLight);
		setUniform3f(uniformName + ".direction", directionalLight.getDirection());
	}

	public void setUniformPointLight(String uniformName, PointLight pointLight) {
		setUniformLight(uniformName + ".base", pointLight);
		setUniformf(uniformName + ".atten.constant", pointLight.getAttenuation().getConstant());
		setUniformf(uniformName + ".atten.linear", pointLight.getAttenuation().getLinear());
		setUniformf(uniformName + ".atten.exponent", pointLight.getAttenuation().getExponent());
		setUniform3f(uniformName + ".position", pointLight.getTransform().getPosition());
		setUniformf(uniformName + ".range", pointLight.getRange());
	}

	public void setUniformSpotLight(String uniformName, SpotLight spotLight) {
		setUniformPointLight(uniformName + ".pointLight", (PointLight) spotLight);
		setUniform3f(uniformName + ".direction", spotLight.getDirection());
		setUniformf(uniformName + ".cutoff", spotLight.getCutoff());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.util.Expendable#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#incRef()
	 */
	@Override
	public void incRef() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#incAndGetRef()
	 */
	@Override
	public int incAndGetRef() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#decRef()
	 */
	@Override
	public void decRef() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#decAndGetRef()
	 */
	@Override
	public int decAndGetRef() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abi.abisEngine.rendering.asset.AssetI#getRefs()
	 */
	@Override
	public int getRefs() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static class ShaderSource {
		private String name, source, tag;
		ShaderType type;

		/**
		 * @param name
		 * @param source
		 * @param tag
		 */
		public ShaderSource(String name, String source, String tag, ShaderType type) {
			this.name = name;
			this.source = source;
			this.tag = tag;
			this.type = type;
		}

		public ShaderSource(String name, String source, ShaderType type) {
			this.name = name;
			this.source = source;
			this.tag = "default";
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public String getSource() {
			return source;
		}

		public String getTag() {
			return tag;
		}

	}

	class ShaderData {
		HashMap<String, ShaderSource> proccessedSources;

		public ShaderData(String shaderName) {
			this.proccessedSources = new HashMap<String, ShaderSource>();
		}

		public void addSource(ShaderSource source) {
			proccessedSources.put(source.getTag(), source);
		}

		public ShaderSource getSource(String sourceTag) {
			return proccessedSources.get(sourceTag);
		}

	}
}