/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.rendering.shader.compiler;

import static org.lwjgl.opengl.GL20.glIsProgram;
import static org.lwjgl.opengl.GL43.GL_ACTIVE_RESOURCES;
import static org.lwjgl.opengl.GL43.GL_ACTIVE_VARIABLES;
import static org.lwjgl.opengl.GL43.GL_ARRAY_STRIDE;
import static org.lwjgl.opengl.GL43.GL_BLOCK_INDEX;
import static org.lwjgl.opengl.GL43.GL_BUFFER_BINDING;
import static org.lwjgl.opengl.GL43.GL_BUFFER_DATA_SIZE;
import static org.lwjgl.opengl.GL43.GL_IS_ROW_MAJOR;
import static org.lwjgl.opengl.GL43.GL_LOCATION;
import static org.lwjgl.opengl.GL43.GL_MATRIX_STRIDE;
import static org.lwjgl.opengl.GL43.GL_NAME_LENGTH;
import static org.lwjgl.opengl.GL43.GL_NUM_ACTIVE_VARIABLES;
import static org.lwjgl.opengl.GL43.GL_OFFSET;
import static org.lwjgl.opengl.GL43.GL_TYPE;
import static org.lwjgl.opengl.GL43.GL_UNIFORM;
import static org.lwjgl.opengl.GL43.GL_UNIFORM_BLOCK;
import static org.lwjgl.opengl.GL43.glGetProgramInterfacei;
import static org.lwjgl.opengl.GL43.glGetProgramResourceName;
import static org.lwjgl.opengl.GL43.glGetProgramResourceiv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.file.PathType;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.gl.memory.GLBuffer;
import net.abi.abisEngine.rendering.gl.memory.GLUniform;
import net.abi.abisEngine.rendering.gl.memory.GLUniformBuffer;
import net.abi.abisEngine.rendering.shader.AEShader;
import net.abi.abisEngine.rendering.shader.AEShader.AEShaderType;
import net.abi.abisEngine.rendering.shader.AEShaderResource;
import net.abi.abisEngine.rendering.shader.compiler.Tokens.Operators;
import net.abi.abisEngine.rendering.shader.compiler.parser.AEShaderParserYAML;
import net.abi.abisEngine.rendering.shader.compiler.parser.fileTypes.yaml.AEShaderFileYAML;
import net.abi.abisEngine.rendering.shader.compiler.parser.fileTypes.yaml.AEShaderGLSLProgram;
import net.abi.abisEngine.util.cacheing.TwoFactorGenericCache;
import net.abi.abisEngine.util.exceptions.AERuntimeException;
import net.abi.abisEngine.util.exceptions.AEShaderCompilerRuntimeException;

/**
 * Compiles and creates of Shader objects in AE.
 * 
 * NOTE: Uniforms and Uniform Buffers will be added automatically and so will
 * attributes.
 * 
 * @author Abinash Singh
 *
 */
public class AEShaderCompiler {

	private static Logger logger = LogManager.getLogger(AEShaderCompiler.class);
	public static final PathHandle DEFAULT_SHADER_ASSET_DIRECTORY_PATH = new PathHandle("res/shaders/",
			PathType.Internal);
	private static final String CURRENT_VERSION = "Compiler Version: 2.03-rev2";
	private static TwoFactorGenericCache<String, String, AEShaderGLSLProgram> loadedImports = new TwoFactorGenericCache<String, String, AEShaderGLSLProgram>(
			String.class, String.class, AEShaderGLSLProgram.class);

	private static PrintStream out;

	public AEShaderCompiler(PrintStream _out) {
		out = _out;
	}

	public AEShaderCompiler() {
		out = System.out;
	}

	/**
	 * Compiles and pre process the AE Shader file also auto adds uniforms and
	 * <b>Must be run on render thread, and in proper context.</b>
	 * 
	 * @return
	 */
	public static AEShader compile(AEShaderFileYAML p, PathHandle path) {
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
		 * If the program is to determine each of the uniform's locations we process
		 * this.
		 */
		// AEGLInfo.getUniformsInfo(program.getProgram());
		// if (autoBindUniforms) {
		// processUniforms(program, _unProccessedShaders);
		processUniforms(program);
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
				List<String> callingPrograms = new ArrayList<>();
				callingPrograms.add(gp.getAE_SHADER_GLSL_PROGRAM_NAME());
				ShaderSource s = new ShaderSource(gp.getAE_SHADER_GLSL_PROGRAM_NAME(),
						/*
						 * process the source.
						 */
						processShaderSource(gp.getAE_SHADER_GLSL_PROGRAM_SOURCE(), callingPrograms),
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
	 * Introspect the <b>program</b> and
	 * 
	 * @param program
	 * @return
	 */
	private static void processUniforms(AEShaderResource _program) {
		out.println("Processing Shader Uniforms for: " + _program.getName() + "...");

		// Check if the program is valid. Then look for number of active resources.
		int program = _program.getProgram();

		if (!glIsProgram(program)) {
			out.println("name: " + program + " is not a program");
			return;
		}

		// Temporary variables for uniform resources.
		int index, uniSize, uniMatStride, uniArrayStride;
		String name;
		int numUniforms;

		// These are the elements we need to retrieve from
		int properties[] = { GL_BLOCK_INDEX, GL_TYPE, GL_NAME_LENGTH, GL_LOCATION, GL_ARRAY_STRIDE, GL_MATRIX_STRIDE };

		// A temporary array in which the native gl methods will fill values into.
		int[] values;
		numUniforms = glGetProgramInterfacei(program, GL_UNIFORM, GL_ACTIVE_RESOURCES);

		// Loop over all resources found and IF they are uniforms in the DEFAULT BLOCK
		// then process, we process blocked uniforms later.
		for (int i = 0; i < numUniforms; i++) {

			// We now make a new array to be filled with the properties we want.
			values = new int[properties.length];

			// We send the array into the native method to retrieve the properties.
			glGetProgramResourceiv(program, GL_UNIFORM, i, properties, null, values);
			index = values[0]; // GL_BLOCK_INDEX

			// If the blocks index is -1 then it is in the default block so we process it.
			if (index == -1) {
				name = glGetProgramResourceName(program, GL_UNIFORM, i);
				GLUniform u = new GLUniform(name);

				_program.getUniforms().put(name, u);

				u.addAttribute(GL_LOCATION, values[3]); // GL_LOCATION
				u.addAttribute(GL_TYPE, values[1]); // GL_TYPE

				int auxSize;
				if (values[4] > 0) { // If the array stride is greater than 0
					auxSize = values[4] * AEGLInfo.spGLSLTypeSize.get(values[1]);
				} else {
					auxSize = AEGLInfo.spGLSLTypeSize.get(values[1]);
				}

				u.setSize(auxSize);
				u.addAttribute(GL_ARRAY_STRIDE, values[4]); // GL_ARRAY_STRIDE
				u.addAttribute(GL_MATRIX_STRIDE, values[5]); // GL_MATRIX_STRIDE

				// Add the uniform to the list in the shader program.
				_program.getUniforms().put(u.name, u);
			}
		} // End Uniform Loop

		// Out source to a different function to add UBOs
		processUniformBlocks(_program);

		out.print(" Done.");
	}

	/**
	 * Query {@link AEShaderResource} 's GLSL program for active or any Uniform
	 * blocks and adds them to the engine's shader resource. This function can add
	 * GLSLUniformBlocks to the engine and bind them. This function will auto add
	 * UBOs, if it finds a uniform that is the same name it will check against the
	 * one in record or it will use the one found and it will find discrepancies and
	 * warn. If there isn't one found the function will use the first found in code
	 * as a reference, and add that to the uniform buffer map and bind it.
	 * 
	 * @param _program An active {@link AEShaderResource}, which has been created
	 *                 and validated.
	 */
	private static void processUniformBlocks(AEShaderResource _program) {
		// Check if the program is valid. Then look for number of active resources.
		int program = _program.getProgram();

		// Temporary variables for uniform resources.
		int index, uniSize, uniMatStride, uniArrayStride;
		String name;

		// A temporary array in which the native GL methods will fill values into.
		int[] values;

		// Now we account for the Uniform blocks. These are arrays of query properties.
		int blockQueryProperties[] = { GL_BUFFER_DATA_SIZE, GL_BUFFER_BINDING, GL_BLOCK_INDEX };
		int activeUniformQueryProperties[] = { GL_ACTIVE_VARIABLES };
		int uniformQueryProperties[] = { GL_NAME_LENGTH, GL_TYPE, GL_LOCATION, GL_OFFSET, GL_ARRAY_STRIDE,
				GL_MATRIX_STRIDE, GL_IS_ROW_MAJOR };

		// Number of active uniform blocks.
		int numUniformBlocks = glGetProgramInterfacei(program, GL_UNIFORM_BLOCK, GL_ACTIVE_RESOURCES);

		// Loop over the uniform blocks found.
		for (int i = 0; i < numUniformBlocks; i++) {
			GLUniformBuffer ubo;
			// Create a new array to fit the block query properties.
			values = new int[blockQueryProperties.length];
			glGetProgramResourceiv(program, GL_UNIFORM_BLOCK, i, blockQueryProperties, null, values);
			name = glGetProgramResourceName(program, GL_UNIFORM_BLOCK, values[2]);

			// If the buffer has been defined by the engine we can continue, else we can
			// move to the next buffer.
			// If we find a UBO already defined we have to compare so we can warn the user.
			if ((ubo = GLUniformBuffer.GlobalUniformBuffers.get(name)) == null) {
				GLUniformBuffer.GlobalUniformBuffers.put(name, (ubo = new GLUniformBuffer(name, values[0])));
			}

			// We check the buffer to see if its bound, if not then bind it to the base that
			// has been defined. Else, means that it has been bound previously but to a
			// different base, we then warn the user.
			// If its bound but not to the same base we warn and continue.
			if (ubo.getBinding() == GLBuffer.NULL_BOUND_BUFFER_OBJECT) {
				ubo.bindBufferBase(values[1]);
			} else {
				if (ubo.getBinding() != values[1]) {
					out.print("WARNING!: UBO: " + name
							+ " is bound to different basses in different programs. It is bound to base: " + values[1]
							+ " in shader: " + _program.toString() + " but is already bound to base: "
							+ ubo.getBinding() + " in another previously compiled. Continuing.");
				}
			}

			// We now have to receive the amount of active uniform objects inside the block.
			values = new int[1];
			glGetProgramResourceiv(program, GL_UNIFORM_BLOCK, i, new int[] { GL_NUM_ACTIVE_VARIABLES }, null, values);
			int numActiveUnifs = values[0];

			// If they are no active uniforms we just move on to the next block.
			if (numActiveUnifs == 0) {
				continue;
			}

			// Receive the active uniform query properties.
			values = new int[numActiveUnifs];
			glGetProgramResourceiv(program, GL_UNIFORM_BLOCK, i, activeUniformQueryProperties, null, values);
			int[] blockUnifs = values;

			// Now we loop over all the found uniforms inside the block and add them to the
			// UBO.
			for (int k = 0; k < numActiveUnifs; k++) {
				// Create a new array to hold the uniform's properties.
				values = new int[uniformQueryProperties.length];

				// Get the uniform's properties.
				glGetProgramResourceiv(program, GL_UNIFORM, blockUnifs[k], uniformQueryProperties, null, values);
				name = glGetProgramResourceName(program, GL_UNIFORM, blockUnifs[k]);
				uniSize = AEGLInfo.spGLSLTypeSize.get(values[1]);
				uniArrayStride = values[4];
				uniMatStride = values[5];
				int auxSize;
				auxSize = AEGLInfo.getUniformByteSize(uniSize, values[1], uniArrayStride, uniMatStride);
			}
		}
	}

	private static void bindAttribute(AEShaderResource program, int index, String attribname) {
		GL45.glBindAttribLocation(program.getProgram(), index, attribname);
	}

	public static void setAttribLocation(int program, String attribName, int location) {
		GL20.glBindAttribLocation(program, location, attribName);
	}

	public static void addUniform(AEShaderResource program, GLUniform uniform) {
		int uniformLocation = 0;
		// GL20.glGetUniformLocation(program.getProgram(), uniform.name);
		// uniform.qualifiers.layoutQualifierIDList.put(Qualifiers.LOCATION,
		// String.valueOf(uniformLocation));
		out.println("Uniform " + "'" + uniform.name + "'" + " At Location: " + uniformLocation);
		if (uniformLocation == 0xFFFFFFFF) {
			logger.warning("Error: Could not find uniform: " + "'" + uniform.name + "'"
					+ "Or it is not being used please check shader code and remove any un-used code or variables.");
			new Exception().printStackTrace();
		}
		program.getUniforms().put(uniform.name, uniform);
	}

	public static class GLSLStruct {
		ArrayList<GLUniform> components;
		String name;
	}

	private static void link(AEShaderResource program) {

		out.println("Compiling Shader: " + program.getName());

		GL20.glLinkProgram(program.getProgram());

		if (GL20.glGetProgrami(program.getProgram(), GL20.GL_LINK_STATUS) == 0) {
			out.println("Error From Shader Program: '" + program.getName() + "'");
			out.println(GL20.glGetProgramInfoLog(program.getProgram(), 1024));
			throw new AEShaderCompilerRuntimeException("Error Linking Program: " + program.getName());
		}

		GL20.glValidateProgram(program.getProgram());

		if (GL20.glGetProgrami(program.getProgram(), GL20.GL_VALIDATE_STATUS) == 0) {
			out.println("Error From Shader Program: '" + program.getName() + "'");
			out.println(GL20.glGetProgramInfoLog(program.getProgram(), 1024));
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
			out.println("Shader creation failed: Could not find valid memory location when adding shader");
			throw new AEShaderCompilerRuntimeException(
					"Could not create program: " + program.getName() + " Path:" + program.getPath());
		}

		out.println("----Valadating----");
		GL20.glShaderSource(shader, s.source);
		GL20.glCompileShader(shader);
		out.println("Success");
		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {
			out.println("Error From Shader Program: '" + program.getName() + "'");
			out.println(GL20.glGetShaderInfoLog(shader, 2048));
			throw new AEShaderCompilerRuntimeException(
					"Could not create program: " + program.getName() + " Path:" + program.getPath());
		}

		out.println("----Attaching----");
		GL20.glAttachShader(program.getProgram(), shader);
		// TODO: attach status
		out.println("Successfully Attached Shader: " + program.getProgram() + " Log: " + "\n"
				+ GL20.glGetShaderInfoLog(shader, 1024));
		out.println("Shader Text For '" + s.type.toString() + "': '" + program.getName() + "'\n" + s.source);

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

	private static String processShaderSource(String gp, List<String> callingPrograms) {
		String[] lines = gp.split("\\r?\\n");
		StringBuilder proccessedSource = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			line = cleanLine(line);
			if (line.isEmpty() || line.isBlank())
				continue;
			if (line.startsWith(Tokens.AE_IMPORT_DIRECTIVE)) {
				proccessedSource.append(processImportLine(line, callingPrograms));
			} else if (line.startsWith(Tokens.AE_INCLUDE_DIRECTIVE)) {
				proccessedSource.append(processIncludeLine(line, callingPrograms));
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
	 * @param callingPrograms
	 * @return
	 */
	private static String processIncludeLine(String line, List<String> callingPrograms) {
		/* Optimize the loading process. */
		String file;
		StringBuilder source = new StringBuilder();
		file = line.replace(Tokens.AE_INCLUDE_DIRECTIVE, "").trim().replaceAll("\"", "");
		if (callingPrograms.contains(file)) {
			throw new AEShaderCompilerRuntimeException(
					"Recursive Overflow Detected, This include is calling it self. Program: " + callingPrograms);
		} else {
			callingPrograms.add(file);
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
			throw new AEShaderCompilerRuntimeException("Unable to parse shader.", e);
		}
		out.println("Including file (" + file + ") in shader: " + callingPrograms);
		return processShaderSource(source.toString(), callingPrograms);
	}

	private static String processImportLine(String line, List<String> callingPrograms) {
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
			if (callingPrograms.contains(program)) {
				throw new AEShaderCompilerRuntimeException(
						"Recursive Overflow Detected, This import is calling it self. Program: " + program);
			} else {
				callingPrograms.add(program);
			}
			loadedImport.append(processImport(program, importFile, callingPrograms));
		}
		return loadedImport.toString();
	}

	private static String processImport(String programName, String fileName, List<String> callingPrograms) {
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
		callingPrograms.add(gp.getAE_SHADER_GLSL_PROGRAM_NAME());
		program.append(processShaderSource(gp.getAE_SHADER_GLSL_PROGRAM_SOURCE(), callingPrograms));
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
