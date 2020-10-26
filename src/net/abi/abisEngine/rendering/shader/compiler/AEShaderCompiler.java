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
import static org.lwjgl.opengl.GL45.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.file.PathType;
import net.abi.abisEngine.rendering.gl.memory.GLBuffer;
import net.abi.abisEngine.rendering.gl.memory.GLUniform;
import net.abi.abisEngine.rendering.gl.memory.GLUniformBuffer;
import net.abi.abisEngine.rendering.shader.AEShader;
import net.abi.abisEngine.rendering.shader.AEShaderResource;
import net.abi.abisEngine.rendering.shader.AEShaderType;
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

	public static final PathHandle DEFAULT_SHADER_ASSET_DIRECTORY_PATH = new PathHandle("res/shaders/",
			PathType.Internal);
	private static final String CURRENT_VERSION = "Compiler Version: 2.03-rev2";
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
		float start = System.nanoTime();
		out.println("===========================================================");
		out.println("Compiling Shader: '" + p.getAE_SHADER_NAME() + "'");
		// We create a reference of this program to keep.
		out.println("---Creating Program---");
		AEShaderResource program = new AEShaderResource(p.getAE_SHADER_NAME(), path);
		program.createProgram();
		out.println("--Processing File--");
		// Now parse the file.
		ArrayList<ShaderSource> _unProccessedShaders = processFile(p);
		out.println("Attaching shaders to program: " + " Program Name: " + program.getName() + " Program ID: "
				+ program.getProgram());
		attach(program, _unProccessedShaders);
		out.println("---Linking---");
		// Now we link.
		linkProgram(program);
		out.println();
		// And also process inputs and outputs.
		processInputs(program);
		processOutputs(program);
		// If the program is to determine each of the uniform's locations we process
		// this.
		// AEGLInfo.getUniformsInfo(program.getProgram());
		processUniforms(program);
		// linked program.
		AEShader shader = new AEShader(program);
		out.println("Successfuly Compiled Shader.");
		float end = System.nanoTime();
		float elapsedTime = end - start;
		out.println("Time To Completion: " + (elapsedTime / 1000000000) + "s");
		out.println("===========================================================");
		return shader;
	}

	/**
	 * Processes the inputs of the first stage of the shader.
	 * 
	 * @param _program
	 */
	private static void processInputs(AEShaderResource _program) {
		int query[] = { GL_TYPE, GL_ARRAY_SIZE, GL_LOCATION, GL_IS_PER_PATCH, GL_LOCATION_COMPONENT };
		int program = _program.getProgram();
		int numInputs = glGetProgramInterfacei(program, GL_PROGRAM_INPUT, GL_ACTIVE_RESOURCES);
		int values[];
		for (int i = 0; i < numInputs; i++) {
			values = new int[query.length];
			glGetProgramResourceiv(program, GL_PROGRAM_INPUT, i, query, null, values);
			GLUniform u = new GLUniform(glGetProgramResourceName(program, GL_PROGRAM_INPUT, i));
			_program.getInputs().put(u.name, u);
			for (int j = 0; j < query.length; j++) {
				u.setSize(AEGLInfo.spGLSLTypeSize.get(values[0]) * values[1]);
				u.addAttribute(query[j], values[j]);
			}
		}
	}

	/**
	 * Processes the outputs of the last stage of shader.
	 * 
	 * @param _program
	 */
	private static void processOutputs(AEShaderResource _program) {
		int query[] = { GL_TYPE, GL_ARRAY_SIZE, GL_LOCATION, GL_IS_PER_PATCH, GL_LOCATION_COMPONENT,
				GL_LOCATION_INDEX };
		int program = _program.getProgram();
		int numInputs = glGetProgramInterfacei(program, GL_PROGRAM_OUTPUT, GL_ACTIVE_RESOURCES);
		int values[];
		for (int i = 0; i < numInputs; i++) {
			values = new int[query.length];
			glGetProgramResourceiv(program, GL_PROGRAM_OUTPUT, i, query, null, values);
			GLUniform u = new GLUniform(glGetProgramResourceName(program, GL_PROGRAM_OUTPUT, i));
			_program.getOutputs().put(u.name, u);
			for (int j = 0; j < query.length; j++) {
				u.setSize(AEGLInfo.spGLSLTypeSize.get(values[0]) * values[1]);
				u.addAttribute(query[j], values[j]);
			}
		}
	}

	private static ArrayList<ShaderSource> processFile(AEShaderFileYAML p) {
		ArrayList<ShaderSource> _shaders = new ArrayList<ShaderSource>();
		// Loading the imports first will ensure that we don't have a recursive
		// overflow.
		loadAllImportsInFile(p);
		for (AEShaderGLSLProgram gp : p.getAE_SHADER_GLSL_PROGRAMS()) {
			if (AEShaderType.valueOf(gp.getAE_SHADER_GLSL_PROGRAM_TYPE()) != AEShaderType.AE_SHADER_IMPORT) {
				List<String> callingPrograms = new ArrayList<>();
				callingPrograms.add(gp.getAE_SHADER_GLSL_PROGRAM_NAME());
				ShaderSource s = new ShaderSource(gp.getAE_SHADER_GLSL_PROGRAM_NAME(),
						processShaderSource(gp.getAE_SHADER_GLSL_PROGRAM_SOURCE(), callingPrograms),
						AEShaderType.valueOf(gp.getAE_SHADER_GLSL_PROGRAM_TYPE()));
				_shaders.add(s);
				out.println("Shader Name: " + gp.getAE_SHADER_GLSL_PROGRAM_NAME() + "\nShader Type: "
						+ gp.getAE_SHADER_GLSL_PROGRAM_TYPE() + "--------");
			}
		}
		return _shaders;
	}

	/**
	 * Automatically adds uniforms from the shader program. And also adds Uniform
	 * blocks see {@link AEShaderCompiler#processUniformBlocks(AEShaderResource)}
	 * 
	 * @param program a AEShaderResource
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
		String name;
		int numUniforms;
		// These are the elements we need to retrieve from
		int properties[] = { GL_TYPE, GL_LOCATION, GL_OFFSET, GL_ARRAY_STRIDE, GL_MATRIX_STRIDE, GL_IS_ROW_MAJOR,
				GL_BLOCK_INDEX };
		// A temporary array in which the native GL methods will fill values into.
		int[] values;
		numUniforms = glGetProgramInterfacei(program, GL_UNIFORM, GL_ACTIVE_RESOURCES);
		// Loop over all resources found and IF they are uniforms in the DEFAULT BLOCK
		// then process, we process blocked uniforms later.
		for (int i = 0; i < numUniforms; i++) {
			// We now make a new array to be filled with the properties we want.
			values = new int[properties.length];
			// We send the array into the native method to retrieve the properties.
			glGetProgramResourceiv(program, GL_UNIFORM, i, properties, null, values);
			// If the blocks index is -1 then it is in the default block so we process it.
			if (values[6] // GL_BLOCK_INDEX
					== -1) {
				name = glGetProgramResourceName(program, GL_UNIFORM, i);
				if (_program.getUniforms().containsKey(name)) {
					continue;
				}
				GLUniform u = new GLUniform(name);
				// Add the uniform to the list in the shader program.
				_program.getUniforms().put(name, u);
				int auxSize;
				auxSize = AEGLInfo.getUniformByteSize(AEGLInfo.spGLSLTypeSize.get(values[0]), values[0], values[3],
						values[4]);
				u.setSize(auxSize);
				for (int ij = 0; ij < properties.length; ij++) {
					u.addAttribute(properties[ij], values[ij]);
				}
				out.println(u);
			}
		}
		// Out source to a different function to add UBOs
		processUniformBlocks(_program);
		out.println("Done.");
	}

	/**
	 * Query {@link AEShaderResource} 's GLSL program for active or any Uniform
	 * blocks and adds them to the engine's shader resource. This function can add
	 * GLSLUniformBlocks to the engine but not bind. This function will auto add
	 * UBOs, if it finds a uniform that is the same name it will check against the
	 * one in record or it will use the one found and it will find discrepancies and
	 * warn. If there isn't one found the function will use the first found in code
	 * as a reference, and add that to the uniform buffer map and bind it.
	 * 
	 * TODO: Use a global list of block bindings or the block index to bind blocks
	 * without explicit definition of binding.
	 * 
	 * @param _program An active {@link AEShaderResource}, which has been created
	 *                 and validated.
	 */
	private static void processUniformBlocks(AEShaderResource _program) {
		// Check if the program is valid. Then look for number of active resources.
		int program = _program.getProgram();
		String name;
		// A temporary array in which the native GL methods will fill values into.
		int[] values;
		// Now we account for the Uniform blocks. These are arrays of query properties.
		int blockQueryProperties[] = { GL_BUFFER_DATA_SIZE, GL_BUFFER_BINDING };
		int activeUniformQueryProperties[] = { GL_ACTIVE_VARIABLES };
		int uniformQueryProperties[] = { GL_TYPE, GL_LOCATION, GL_OFFSET, GL_ARRAY_STRIDE, GL_MATRIX_STRIDE,
				GL_IS_ROW_MAJOR, GL_BLOCK_INDEX };
		// Number of active uniform blocks.
		int numUniformBlocks = glGetProgramInterfacei(program, GL_UNIFORM_BLOCK, GL_ACTIVE_RESOURCES);
		// Loop over the uniform blocks found.
		for (int blockIndex = 0; blockIndex < numUniformBlocks; blockIndex++) {
			GLUniformBuffer ubo;
			// Create a new array to fit the block query properties.
			values = new int[blockQueryProperties.length];
			glGetProgramResourceiv(program, GL_UNIFORM_BLOCK, blockIndex, blockQueryProperties, null, values);
			name = glGetProgramResourceName(program, GL_UNIFORM_BLOCK, blockIndex);
			// If the buffer has been defined by the engine we can continue, else we can
			// move to the next buffer.
			// If we find a UBO already defined we have to compare so we can warn the user.
			if ((ubo = GLUniformBuffer.GlobalUniformBuffers.get(name)) == null) {
				GLUniformBuffer.GlobalUniformBuffers.put(name, (ubo = new GLUniformBuffer(name, values[0])));
			}
			if (ubo.getBinding() == GLBuffer.NULL_BOUND_BUFFER_OBJECT) {
				ubo.setBinding(values[1]);
			} else {
				if (ubo.getBinding() != values[1]) {
					out.println("Block with same name bound to location: " + ubo.getBinding()
							+ " while current block has binding: " + values[1] + " Skipping.");
				}
				continue;
			}
			// We now have to receive the amount of active uniform objects inside the block.
			values = new int[1];
			glGetProgramResourceiv(program, GL_UNIFORM_BLOCK, blockIndex, new int[] { GL_NUM_ACTIVE_VARIABLES }, null,
					values);
			int numActiveUnifs = values[0];
			// If they are no active uniforms we just move on to the next block.
			if (numActiveUnifs == 0) {
				continue;
			}
			// Receive the active uniform query properties.
			values = new int[numActiveUnifs];
			glGetProgramResourceiv(program, GL_UNIFORM_BLOCK, blockIndex, activeUniformQueryProperties, null, values);
			int[] blockUnifs = values;
			// Now we loop over all the found uniforms inside the block and add them to the
			// UBO.
			for (int k = 0; k < numActiveUnifs; k++) {
				GLUniform u;
				// Create a new array to hold the uniform's properties.
				values = new int[uniformQueryProperties.length];
				// Get the uniform's properties.
				glGetProgramResourceiv(program, GL_UNIFORM, blockUnifs[k], uniformQueryProperties, null, values);
				name = glGetProgramResourceName(program, GL_UNIFORM, blockUnifs[k]);
				if ((u = ubo.getUniform(name)) == null) {
					u = new GLUniform(name);
					ubo.addUniform(name, u);
				}
				int auxSize;
				auxSize = AEGLInfo.getUniformByteSize(AEGLInfo.spGLSLTypeSize.get(values[0]), values[0], values[3],
						values[4]);
				if (u.size == -1) {
					u.setSize(auxSize);
					for (int i = 0; i < uniformQueryProperties.length; i++) {
						u.addAttribute(uniformQueryProperties[i], values[i]);
					}
				}
			}
		}
	}

	private static void linkProgram(AEShaderResource program) {
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
				// Attach each program
				addShaderSource(program, shaderSource);
			}
		}
	}

	/**
	 * Adds a program to the current shader program.
	 * 
	 * @param text
	 * @param type
	 */
	private static void addShaderSource(AEShaderResource program, ShaderSource s) {
		out.println("---Creating shader: " + s.name + "---");
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
		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
			out.println("Error From Shader Program: '" + program.getName() + "'");
			out.println(GL20.glGetShaderInfoLog(shader, 2048));
			throw new AEShaderCompilerRuntimeException(
					"Could not create program: " + program.getName() + " Path:" + program.getPath());
		}
		out.println("----Attaching----");
		GL20.glAttachShader(program.getProgram(), shader);
		out.println("Successfully Attached Shader: " + program.getProgram() + " Log: " + "\n"
				+ GL20.glGetShaderInfoLog(shader, 1024));
		// out.println("Shader Text For '" + s.type.toString() + "': '" +
		// program.getName() + "'\n");
		GL20.glDeleteShader(shader);
		if (GL20.glGetShaderi(shader, GL20.GL_DELETE_STATUS) == GL20.GL_FALSE) {
			out.println("Error From Shader Program: '" + program.getName() + "'");
			out.println(GL20.glGetShaderInfoLog(shader, 2048));
			throw new AEShaderCompilerRuntimeException(
					"Could not delete program: " + program.getName() + " Path:" + program.getPath());
		}
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
					"Recursive Overflow Detected, This include is calling it self. Program: " + line);
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
			out.println("Unable to parse shader " + f.getName() + " " + e.getStackTrace());
			out.println("Exiting...");
			throw new AEShaderCompilerRuntimeException("Unable to parse shader.", e);
		}
		out.println("Including file (" + file + ") in shader: " + callingPrograms);
		return processShaderSource(source.toString(), callingPrograms);
	}

	private static String processImportLine(String line, List<String> callingPrograms) {
		StringBuilder loadedImport = new StringBuilder();
		// Now we need to separate out the import file and programs.
		String importFile; // The file from which to import
		// I do this since the user can accidentally have more than one whitespace. So
		// we find the first one and then token-ize the line using this.
		String importDefinition; // There are only two possible tokens the import directive and the import call.
		String temp = ""; // This is to spell out the token and add it to the array.
		ArrayList<String> program_names = new ArrayList<String>();
		// First we need to check if the import call is valid.
		if (!line.startsWith(Tokens.AE_IMPORT_DIRECTIVE)) {
			return "";
		}
		if (Character.isWhitespace(line.charAt(Tokens.AE_IMPORT_DIRECTIVE.length()))) {
			int importFileSeperatorIndex = 0;
			temp = line.substring(Tokens.AE_IMPORT_DIRECTIVE.length(), line.length());
			temp = temp.replaceAll("\\s+", ""); // Clear all the white space.
			// We substring the import line from the start of the temp to the : char. And
			// while we do so we also define the index of it to use to separate the program
			// tokens.
			importFile = temp.substring(0, (importFileSeperatorIndex = temp.indexOf(Operators.COLON.op)));
			// Now we separate the dictionary containing the import programs from the line
			// it self.
			importDefinition = temp.substring(importFileSeperatorIndex + 1, temp.length()).replaceAll("\\{", "")
					.replaceAll("\\}", "");
			out.println("Importing Programs : " + line);
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
			// Prevents Recursive overflows, meaning that the same import can't call it self
			// ever.
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
		// Check if the import was in this file or was loaded before.
		if ((gp = loadedImports.get(fileName + Tokens.AE_SHADER_FILE_TYPE, programName)) == null) {
			// If it wasn't try finding it then loading it.
			loadAllImportsInFile(AEShaderParserYAML
					.parse(DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild(fileName + Tokens.AE_SHADER_FILE_TYPE)));
			// If we still can't find it then give up and go throw a exception.
			if ((gp = loadedImports.get(fileName + Tokens.AE_SHADER_FILE_TYPE, programName)) == null) {
				throw new AEShaderCompilerRuntimeException("Import in file not fo)und : " + fileName
						+ Tokens.AE_SHADER_FILE_TYPE + " Import: " + programName);
			}
		}
		// Now that we have determined the file exists we can continue. Or we have not
		// and the program exits. Now we have to find that program.
		// If the program doesn't exist we throw an exception.
		if ((gp = loadedImports.get(fileName + Tokens.AE_SHADER_FILE_TYPE, programName)) == null) {
			throw new AEShaderCompilerRuntimeException("Shader Import not found in file: " + fileName
					+ Tokens.AE_SHADER_FILE_TYPE + " Import: " + programName);
		}
		// Otherwise we recursively process it and append it to the original source.
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
		// Cleans the line of comments and such.
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
		public Map<String, ShaderSource> proccessedSources;

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
