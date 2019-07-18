package com.base.engine.rendering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import com.base.engine.core.Util;
import com.base.engine.math.Matrix4f;
import com.base.engine.math.Transform;
import com.base.engine.math.Vector2f;
import com.base.engine.math.Vector3f;

public class Shader {
	private int program;
	private HashMap<String, Integer> uniforms;

	private static final Shader instance = new Shader();

	public static Shader getInstance() {
		return instance;
	}

	public Shader() {
		program = GL20.glCreateProgram();
		uniforms = new HashMap<String, Integer>();

		if (program == 0) {
			System.err.println("Shader creation failed: Could not find valid memory location in constructor");
			System.exit(1);
		}
	}

	public void bind() {
		GL20.glUseProgram(program);
	}

	public void updateUniform(Transform transform, Material mat, RenderingEngine engine) {

	}

	public void addAllUniforms(String shaderText) {

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

			// Returns that name of the uniform skipping the type and one index after the
			// space to the name definition.
			String uniformName = uniformLine.substring(uniformLine.indexOf(' ') + 1, uniformLine.length());

			this.addUniform(uniformName);

			// System.out.println("'" + uniformLine + "' Extracted Text: '" + uniformName +
			// "'");

			uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD, uniformStartLocation
					+ UNIFORM_KEYWORD.length()); /* This is to prevent it from looping over the same uniform keyword. */
		}

		// System.out.println("Finished Adding Uniforms From File.");

	}

	public void addAllAttributes(String shaderText) {

		// System.out.println("Attempting To Add Attributes Automatically...");

		final String ATTRIBUTE_KEYWORD = "uniform", LINE_END_CHAR = ";";

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
