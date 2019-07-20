package com.base.engine.rendering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import com.base.engine.core.Util;
import com.base.engine.math.Matrix4f;
import com.base.engine.math.Transform;
import com.base.engine.math.Vector2f;
import com.base.engine.math.Vector3f;

// TODO: Fix parsing: Make it faster by adding trim() to remove white space automatically.
public class Shader {
	private int program;
	private HashMap<String, Integer> uniforms;

	public Shader(String fileName) {
		program = GL20.glCreateProgram();
		uniforms = new HashMap<String, Integer>();

		if (program == 0) {
			System.err.println("Shader creation failed: Could not find valid memory location in constructor");
			System.exit(1);
		}

		String vertexShaderText = loadShader(fileName + ".glvs");
		String fragmentShaderText = loadShader(fileName + ".glfs");

		this.addVertexShader(vertexShaderText);
		this.addFragmentShader(fragmentShaderText);

		this.addAllAttributes(vertexShaderText);

		// After Setting Attributes And Loading Shaders
		this.compileShader();

		this.addAllUniforms(vertexShaderText);
		this.addAllUniforms(fragmentShaderText);

	}

	public void bind() {
		GL20.glUseProgram(program);
	}

	public void updateUniform(Transform transform, Material mat, RenderingEngine engine) {

	}

	private class GLSLStruct {
		public String name, type;
	}

	private HashMap<String, ArrayList<GLSLStruct>> findUniformStructs(String shaderText) {

		// System.out.println("Attempting To Add Uniforms Automatically...");

		HashMap<String, ArrayList<GLSLStruct>> result = new HashMap<String, ArrayList<GLSLStruct>>();

		final String STRUCT_KEYWORD = "struct", LINE_END_CHAR = ";", OPENING_BRACKET = "{", CLOSING_BRACKET = "}";

		int structStartLocation = shaderText.indexOf(STRUCT_KEYWORD);

		while (structStartLocation != -1) {

			if (!(structStartLocation != 0
					&& (Character.isWhitespace(shaderText.charAt(structStartLocation - 1))
							|| shaderText.charAt(structStartLocation - 1) == ';')
					&& Character.isWhitespace(shaderText.charAt(structStartLocation + STRUCT_KEYWORD.length())))) {
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

				int componentNameStart = componentLineEndCharPos;

				/*
				 * While the char it is looping over is not a white space. Because we are going
				 * backwards from the uniform definition. As soon as we hit a space it means we
				 * are at the end of the name of the uniform.
				 */
				while (!Character.isWhitespace(shaderText.charAt(componentNameStart - 1))) {
					componentNameStart--; // Back up a character.
				}

				int componentTypeEnd = componentNameStart - 1;
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

	public void addAllUniforms(String shaderText) {

		HashMap<String, ArrayList<GLSLStruct>> structs = findUniformStructs(shaderText);

		// System.out.println("Attempting To Add Uniforms Automatically...");

		final String UNIFORM_KEYWORD = "uniform", LINE_END_CHAR = ";";

		int uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD);

		while (uniformStartLocation != -1) {

			/*
			 * Calculates the starting position of the uniform variable type and name
			 * definition in the line.
			 */
			int beginUniformDefinitionIndex = uniformStartLocation + UNIFORM_KEYWORD.length() + 1;

			// Finds the line end character and returns the index of it.
			int endOfDefinitionIndex = shaderText.indexOf(LINE_END_CHAR, beginUniformDefinitionIndex);

			String uniformLine = shaderText.substring(beginUniformDefinitionIndex, endOfDefinitionIndex);

			int whiteSpacePos = uniformLine.indexOf(' ');

			// Returns that name of the uniform skipping the type and one is index after the
			// space to the name definition.
			String uniformName = uniformLine.substring(whiteSpacePos + 1, uniformLine.length());
			String uniformType = uniformLine.substring(0, whiteSpacePos);

			this.addUniformWithStructCheck(uniformName, uniformType, structs);

			// this.addUniform(uniformName);

			// System.out.println("'" + uniformLine + "' Extracted Text: '" + uniformName +
			// "'");

			uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD, uniformStartLocation
					+ UNIFORM_KEYWORD.length()); /* This is to prevent it from looping over the same uniform keyword. */
		}

		// System.out.println("Finished Adding Uniforms From File.");

	}

	private void addUniformWithStructCheck(String uniformName, String uniformType,
			HashMap<String, ArrayList<GLSLStruct>> structs) {

		boolean addThis = true;
		ArrayList<GLSLStruct> structComponents = structs.get(uniformType);

		if (structComponents != null) { // if there are no components in the struct.
			addThis = false;

			for (GLSLStruct struct : structComponents) {
				/*
				 * Send struct again to this recursively to check for for structs inside of
				 * this.
				 */
				addUniformWithStructCheck(uniformName + "." + struct.name, struct.type, structs);
				// System.out.println(uniformName + "." + struct.name);
			}

		}

		if (addThis) {
			this.addUniform(uniformName);
			// System.out.println(uniformType + " " + uniformName);
		}

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

	public void addUniform(String uniform) {
		int uniformLocation = GL20.glGetUniformLocation(program, uniform);

		if (uniformLocation == 0xFFFFFFFF) {
			System.err.println("Error: Could not find uniform: " + uniform);
			new Exception().printStackTrace();
			System.exit(1);
		}

		uniforms.put(uniform, uniformLocation);
	}

	public void setAttribLocation(String attribName, int location) {
		GL20.glBindAttribLocation(program, location, attribName);
	}

	public void addVertexShader(String text) {
		addProgram(text, GL20.GL_VERTEX_SHADER);
	}

	public void addGeometryShader(String text) {
		addProgram(text, GL32.GL_GEOMETRY_SHADER);
	}

	public void addFragmentShader(String text) {
		addProgram(text, GL20.GL_FRAGMENT_SHADER);
	}

	public void addVertexShaderFromFile(String fileName) {
		addProgram(loadShader(fileName), GL20.GL_VERTEX_SHADER);
	}

	public void addGeometryShaderFromFile(String fileName) {
		addProgram(loadShader(fileName), GL32.GL_GEOMETRY_SHADER);
	}

	public void addFragmentShaderFromFile(String fileName) {
		addProgram(loadShader(fileName), GL20.GL_FRAGMENT_SHADER);
	}

	public void compileShader() {
		GL20.glLinkProgram(program);

		if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == 0) {
			System.err.println(GL20.glGetProgramInfoLog(program, 1024));
			System.exit(1);
		}

		GL20.glValidateProgram(program);

		if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) == 0) {
			System.err.println(GL20.glGetProgramInfoLog(program, 1024));
			System.exit(1);
		}
	}

	private void addProgram(String text, int type) {
		int shader = GL20.glCreateShader(type);

		if (shader == 0) {
			System.err.println("Shader creation failed: Could not find valid memory location when adding shader");
			System.exit(1);
		}

		GL20.glShaderSource(shader, text);
		GL20.glCompileShader(shader);

		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {
			System.err.println(GL20.glGetShaderInfoLog(shader, 1024));
			System.exit(1);
		}

		GL20.glAttachShader(program, shader);
	}

	public void setUniformi(String uniformName, int value) {
		GL20.glUniform1i(uniforms.get(uniformName), value);
	}

	public void setUniformf(String uniformName, float value) {
		GL20.glUniform1f(uniforms.get(uniformName), value);
	}

	public void setUniform2f(String uniformName, Vector2f value) {
		GL20.glUniform3f(uniforms.get(uniformName), value.getX(), value.getY(), 0);
	}

	public void setUniform3f(String uniformName, Vector3f value) {
		GL20.glUniform3f(uniforms.get(uniformName), value.getX(), value.getY(), value.getZ());
	}

	public void setUniformMatrix4f(String uniformName, Matrix4f value) {
		GL20.glUniformMatrix4(uniforms.get(uniformName), true, Util.createFlippedBuffer(value));
	}

	public static String loadShader(String fileName) {
		StringBuilder shaderSource = new StringBuilder();
		BufferedReader shaderReader = null;

		final String INCLUDE_DIRECTIVE = "#include";

		// glsl include

		try {
			shaderReader = new BufferedReader(new FileReader("./res/shaders/" + fileName));
			String line;

			while ((line = shaderReader.readLine()) != null) {

				if (line.startsWith(INCLUDE_DIRECTIVE)) {

					// #include 'file'
					// INCLUDE_DIRECTIE length puts it at the end of the include and i add 2 to move
					// it past the first quote to the file name.
					// AND THEN IT ENDS AT THE END OF THE LINE NO WHITE SPACE AT THE END.
					shaderSource.append( // This appends the loaded shader to the source of the file
							loadShader( // Loads the shader specified in the #include
									line.substring(INCLUDE_DIRECTIVE.length() + 2, line.length() - 1)));
					// Figures out the name of the file being included.

				} else {
					shaderSource.append(line).append("\n");
				}

			}

			shaderReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return shaderSource.toString();
	}

	public int getProgram() {
		return program;
	}

	public void setProgram(int program) {
		this.program = program;
	}

	public HashMap<String, Integer> getUniforms() {
		return uniforms;
	}

	public void setUniforms(HashMap<String, Integer> uniforms) {
		this.uniforms = uniforms;
	}

}
