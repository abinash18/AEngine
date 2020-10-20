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

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER_BINDING;
import static org.lwjgl.opengl.GL15.GL_BUFFER_SIZE;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING;
import static org.lwjgl.opengl.GL15.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGetBufferParameteri;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORMS;
import static org.lwjgl.opengl.GL20.GL_FLOAT_MAT2;
import static org.lwjgl.opengl.GL20.GL_FLOAT_MAT3;
import static org.lwjgl.opengl.GL20.GL_FLOAT_MAT4;
import static org.lwjgl.opengl.GL20.GL_MAX_VERTEX_ATTRIBS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_ATTRIB_ARRAY_ENABLED;
import static org.lwjgl.opengl.GL20.GL_VERTEX_ATTRIB_ARRAY_NORMALIZED;
import static org.lwjgl.opengl.GL20.GL_VERTEX_ATTRIB_ARRAY_SIZE;
import static org.lwjgl.opengl.GL20.GL_VERTEX_ATTRIB_ARRAY_STRIDE;
import static org.lwjgl.opengl.GL20.GL_VERTEX_ATTRIB_ARRAY_TYPE;
import static org.lwjgl.opengl.GL20.glGetProgramiv;
import static org.lwjgl.opengl.GL20.glGetVertexAttribi;
import static org.lwjgl.opengl.GL20.glIsProgram;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT2x3;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT2x4;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT3x2;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT3x4;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT4x2;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT4x3;
import static org.lwjgl.opengl.GL30.GL_VERTEX_ATTRIB_ARRAY_INTEGER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glIsVertexArray;
import static org.lwjgl.opengl.GL33.GL_VERTEX_ATTRIB_ARRAY_DIVISOR;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT2;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT2x3;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT2x4;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT3;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT3x2;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT3x4;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT4;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT4x2;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT4x3;
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

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.ARBColorBufferFloat;
import org.lwjgl.opengl.ARBTextureCompressionBPTC;
import org.lwjgl.opengl.ATITextureCompression3DC;
import org.lwjgl.opengl.EXTPackedFloat;
import org.lwjgl.opengl.EXTTextureCompressionLATC;
import org.lwjgl.opengl.EXTTextureCompressionS3TC;
import org.lwjgl.opengl.EXTTextureInteger;
import org.lwjgl.opengl.EXTTextureSRGB;
import org.lwjgl.opengl.EXTTextureSnorm;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengles.APPLERGB422;

/**
 * A Static Class for grabbing quick and in depth info for certain things GL.
 * This Library has been ported from LightHouse3D's VSLGLInfo.
 * https://github.com/lighthouse3d/VSL
 */
public class AEGLInfo {
	public final static String EXTRACTOR_VERSION = "1.0.0";

	// OpenGL Fields And Variables for use in the Extractor
	// static local variables
	public static Map<Integer, String> spInternalF = new HashMap<>();
	public static Map<Integer, String> spDataF = new HashMap<>();
	public static Map<Integer, String> spTextureDataType = new HashMap<>();
	public static Map<Integer, String> spGLSLType = new HashMap<>();
	public static Map<Integer, Integer> spGLSLTypeSize = new HashMap<>();
	public static Map<Integer, String> spTextureFilter = new HashMap<>();
	public static Map<Integer, String> spTextureWrap = new HashMap<>();
	public static Map<Integer, String> spTextureCompFunc = new HashMap<>();
	public static Map<Integer, String> spTextureCompMode = new HashMap<>();
	public static Map<Integer, String> spTextureUnit = new HashMap<>();
	public static Map<Integer, Integer> spTextureBound = new HashMap<>();
	public static Map<Integer, String> spHint = new HashMap<>();
	public static Map<Integer, String> spTextureTarget = new HashMap<>();
	public static Map<Integer, String> spBufferAccess = new HashMap<>();
	public static Map<Integer, String> spBufferUsage = new HashMap<>();
	public static Map<Integer, String> spBufferBinding = new HashMap<>();
	public static Map<Integer, Integer> spBufferBound = new HashMap<>();
	public static Map<Integer, Integer> spBoundBuffer = new HashMap<>();
	public static Map<Integer, String> spShaderType = new HashMap<>();
	public static Map<Integer, String> spTransFeedBufferMode = new HashMap<>();
	public static Map<Integer, String> spGLSLPrimitives = new HashMap<>();
	public static Map<Integer, String> spTessGenSpacing = new HashMap<>();
	public static Map<Integer, String> spVertexOrder = new HashMap<>();
	public static Map<Integer, String> spShaderPrecision = new HashMap<>();

	public static PrintStream out = System.out;

	public static void setOutputStream(PrintStream _out) {
		out = _out;
	}

	public static void getVAOInfo(int buffer) {
		int count, info, prevBuffer;
		out.println();
		if (!glIsVertexArray(buffer)) {
			out.println("name: " + buffer + " is not a VAO");
			return;
		}
		out.println("VAO Info for name: " + buffer);
		glBindVertexArray(buffer);
		info = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
		if (info != 0) {
			out.println("Element Array: " + info);
		}
		count = glGetInteger(GL_MAX_VERTEX_ATTRIBS);
		for (int i = 0; i < count; i++) {
			info = glGetVertexAttribi(i, GL_VERTEX_ATTRIB_ARRAY_ENABLED);
			if (info != 0) {
				out.println("Attrib index: " + i);
				info = glGetVertexAttribi(i, GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING);
				out.println("    Buffer bound: " + info);
				prevBuffer = glGetInteger(GL_ARRAY_BUFFER_BINDING);
				glBindBuffer(GL_ARRAY_BUFFER, info);
				info = glGetBufferParameteri(GL_ARRAY_BUFFER, GL_BUFFER_SIZE);
				glBindBuffer(GL_ARRAY_BUFFER, prevBuffer);
				out.println("    Size: " + info);
				info = glGetVertexAttribi(i, GL_VERTEX_ATTRIB_ARRAY_SIZE);
				out.println("    Components: " + info);
				info = glGetVertexAttribi(i, GL_VERTEX_ATTRIB_ARRAY_TYPE);
				out.println("    Data Type: " + spDataF.get(info));
				info = glGetVertexAttribi(i, GL_VERTEX_ATTRIB_ARRAY_STRIDE);
				out.println("    Stride: " + info);
				info = glGetVertexAttribi(i, GL_VERTEX_ATTRIB_ARRAY_NORMALIZED);
				out.println("    Normalized: " + info);
				info = glGetVertexAttribi(i, GL_VERTEX_ATTRIB_ARRAY_DIVISOR);
				out.println("    Divisor: " + info);
				info = glGetVertexAttribi(i, GL_VERTEX_ATTRIB_ARRAY_INTEGER);
				out.println("    Integer: " + info);
			}
		}
	}

	public static void getUniformsInfo(int program) {
		out.println();
		if (!glIsProgram(program)) {
			out.println("name: " + program + " is not a program");
			return;
		}
		int index, uniSize, uniMatStride, uniArrayStride;
		String name;
		int numUniforms;
		out.println("Uniforms Info for program: " + program + " {");
		int properties[] = { GL_BLOCK_INDEX, GL_TYPE, GL_NAME_LENGTH, GL_LOCATION, GL_ARRAY_STRIDE };
		int[] values = new int[1];
		// glGetProgramInterfaceiv(program, GL_UNIFORM, GL_ACTIVE_RESOURCES, values);
		glGetProgramiv(program, GL_ACTIVE_UNIFORMS, values);
		numUniforms = values[0];
		out.println("Num Uniforms: " + numUniforms + " {");
		for (int i = 0; i < numUniforms; i++) {
			values = new int[properties.length];
			glGetProgramResourceiv(program, GL_UNIFORM, i, properties, null, values);
			index = values[0];
			if (index == -1) {
				name = glGetProgramResourceName(program, GL_UNIFORM, i);
				out.println("\tName: " + name);
				out.println("\tType: " + spGLSLType.get(values[1]));
				out.println("\tLocation: " + values[3]);
				int auxSize;
				if (values[4] > 0) {
					auxSize = values[4] * spGLSLTypeSize.get(values[1]);
				} else {
					auxSize = spGLSLTypeSize.get(values[1]);
				}
				out.println("\tSize: " + auxSize);
				if (values[4] > 0) {
					out.println("\tStride: " + values[4]);
				}
			}
		}
		int blockQueryProperties[] = { GL_BUFFER_DATA_SIZE, GL_BUFFER_BINDING, GL_BLOCK_INDEX };
		int _blockQueryProperties[] = { GL_NUM_ACTIVE_VARIABLES };
		int activeUniformQueryProperties[] = { GL_ACTIVE_VARIABLES };
		int uniformQueryProperties[] = { GL_NAME_LENGTH, GL_TYPE, GL_LOCATION, GL_OFFSET, GL_ARRAY_STRIDE,
				GL_MATRIX_STRIDE, GL_IS_ROW_MAJOR };
		int count = glGetProgramInterfacei(program, GL_UNIFORM_BLOCK, GL_ACTIVE_RESOURCES);
		out.println("Uniform Block Objects: " + count + " {");
		for (int i = 0; i < count; i++) {
			out.println("\tBlock: " + i + " {");
			values = new int[blockQueryProperties.length];
			glGetProgramResourceiv(program, GL_UNIFORM_BLOCK, i, blockQueryProperties, null, values);
			name = glGetProgramResourceName(program, GL_UNIFORM_BLOCK, values[2]);
			out.println("\t\tName: " + name + "\n\t\tSize: " + values[0]);
			out.println("\t\tBlock binding point: " + values[2]);
			out.println("\t\tBuffer bound to binding point: " + values[1]);
			values = new int[_blockQueryProperties.length];
			glGetProgramResourceiv(program, GL_UNIFORM_BLOCK, i, _blockQueryProperties, null, values);
			int numActiveUnifs = values[0];
			if (numActiveUnifs == 0) {
				continue;
			}
			values = new int[numActiveUnifs];
			glGetProgramResourceiv(program, GL_UNIFORM_BLOCK, i, activeUniformQueryProperties, null, values);
			int[] blockUnifs = values;
			out.println("\tMembers of block: " + i + " : {");
			for (int k = 0; k < numActiveUnifs; k++) {
				out.println("\t{");
				values = new int[uniformQueryProperties.length];
				glGetProgramResourceiv(program, GL_UNIFORM, blockUnifs[k], uniformQueryProperties, null, values);
				name = glGetProgramResourceName(program, GL_UNIFORM, blockUnifs[k]);
				out.println("\t\t" + name + "\n\t\t" + spGLSLType.get(values[1]));
				out.println("\t\tOffset: " + values[3]);
				uniSize = spGLSLTypeSize.get(values[1]);
				uniArrayStride = values[4];
				uniMatStride = values[5];
				int auxSize;
				auxSize = getUniformByteSize(uniSize, values[1], uniArrayStride, uniMatStride);
				out.println("\t\tSize: " + auxSize);
				if (uniArrayStride > 0) {
					out.println("\t\tArray stride:" + uniArrayStride);
				}
				if (uniMatStride > 0) {
					out.println("\t\tMatrix stride:" + uniMatStride);
				}
				out.println("\t},");
			}
			out.println("\t}");
		}
		out.println("\t}\n\t}\n}");
	}

	public static int getUniformByteSize(int uniSize, int uniType, int uniArrayStride, int uniMatStride) {
		int auxSize = 0;
		if (uniArrayStride > 0) {
			auxSize = uniArrayStride * uniSize;
		} else if (uniMatStride > 0) {
			switch (uniType) {
			case GL_FLOAT_MAT2:
			case GL_FLOAT_MAT2x3:
			case GL_FLOAT_MAT2x4:
			case GL_DOUBLE_MAT2:
			case GL_DOUBLE_MAT2x3:
			case GL_DOUBLE_MAT2x4:
				auxSize = 2 * uniMatStride;
				break;
			case GL_FLOAT_MAT3:
			case GL_FLOAT_MAT3x2:
			case GL_FLOAT_MAT3x4:
			case GL_DOUBLE_MAT3:
			case GL_DOUBLE_MAT3x2:
			case GL_DOUBLE_MAT3x4:
				auxSize = 3 * uniMatStride;
				break;
			case GL_FLOAT_MAT4:
			case GL_FLOAT_MAT4x2:
			case GL_FLOAT_MAT4x3:
			case GL_DOUBLE_MAT4:
			case GL_DOUBLE_MAT4x2:
			case GL_DOUBLE_MAT4x3:
				auxSize = 4 * uniMatStride;
				break;
			}
		} else {
			auxSize = spGLSLTypeSize.get(uniType);
		}
		return auxSize;
	}

	static {
		spShaderPrecision.put(GL46.GL_LOW_FLOAT, "GL_LOW_FLOAT");
		spShaderPrecision.put(GL46.GL_MEDIUM_FLOAT, "GL_MEDIUM_FLOAT");
		spShaderPrecision.put(GL46.GL_HIGH_FLOAT, "GL_HIGH_FLOAT");
		spShaderPrecision.put(GL46.GL_LOW_INT, "GL_LOW_INT");
		spShaderPrecision.put(GL46.GL_MEDIUM_INT, "GL_MEDIUM_INT");
		spShaderPrecision.put(GL46.GL_HIGH_INT, "GL_HIGH_INT");

		spTessGenSpacing.put(GL46.GL_EQUAL, "GL_EQUAL");
		spTessGenSpacing.put(GL46.GL_FRACTIONAL_EVEN, "GL_FRACTIONAL_EVEN");
		spTessGenSpacing.put(GL46.GL_FRACTIONAL_ODD, "GL_FRACTIONAL_ODD");

		spVertexOrder.put(GL46.GL_CCW, "GL_CCW");
		spVertexOrder.put(GL46.GL_CW, "GL_CW");

		spGLSLPrimitives.put(GL46.GL_QUADS, "GL_QUADS");
		spGLSLPrimitives.put(GL46.GL_ISOLINES, "GL_ISOLINES");
		spGLSLPrimitives.put(GL46.GL_POINTS, "GL_POINTS");
		spGLSLPrimitives.put(GL46.GL_LINES, "GL_LINES");
		spGLSLPrimitives.put(GL46.GL_LINES_ADJACENCY, "GL_LINES_ADJACENCY");
		spGLSLPrimitives.put(GL46.GL_TRIANGLES, "GL_TRIANGLES");
		spGLSLPrimitives.put(GL46.GL_LINE_STRIP, "GL_LINE_STRIP");
		spGLSLPrimitives.put(GL46.GL_TRIANGLE_STRIP, "GL_TRIANGLE_STRIP");
		spGLSLPrimitives.put(GL46.GL_TRIANGLES_ADJACENCY, "GL_TRIANGLES_ADJACENCY");

		spTransFeedBufferMode.put(GL46.GL_SEPARATE_ATTRIBS, "GL_SEPARATE_ATTRIBS");
		spTransFeedBufferMode.put(GL46.GL_INTERLEAVED_ATTRIBS, "GL_INTERLEAVED_ATTRIBS");

		spShaderType.put(GL46.GL_VERTEX_SHADER, "GL_VERTEX_SHADER");
		spShaderType.put(GL46.GL_GEOMETRY_SHADER, "GL_GEOMETRY_SHADER");
		spShaderType.put(GL46.GL_TESS_CONTROL_SHADER, "GL_TESS_CONTROL_SHADER");
		spShaderType.put(GL46.GL_TESS_EVALUATION_SHADER, "GL_TESS_EVALUATION_SHADER");
		spShaderType.put(GL46.GL_FRAGMENT_SHADER, "GL_FRAGMENT_SHADER");

		spHint.put(GL46.GL_FASTEST, "GL_FASTEST");
		spHint.put(GL46.GL_NICEST, "GL_NICEST");
		spHint.put(GL46.GL_DONT_CARE, "GL_DONT_CARE");

		spBufferBinding.put(GL46.GL_ARRAY_BUFFER_BINDING, "GL_ARRAY_BUFFER");
		spBufferBinding.put(GL46.GL_ELEMENT_ARRAY_BUFFER_BINDING, "GL_ELEMENT_ARRAY_BUFFER");
		spBufferBinding.put(GL46.GL_PIXEL_PACK_BUFFER_BINDING, "GL_PIXEL_PACK_BUFFER");
		spBufferBinding.put(GL46.GL_PIXEL_UNPACK_BUFFER_BINDING, "GL_PIXEL_UNPACK_BUFFER");
		spBufferBinding.put(GL46.GL_TRANSFORM_FEEDBACK_BUFFER_BINDING, "GL_TRANSFORM_FEEDBACK_BUFFER");
		spBufferBinding.put(GL46.GL_UNIFORM_BUFFER_BINDING, "GL_UNIFORM_BUFFER");

		spBufferBinding.put(GL46.GL_TEXTURE_BUFFER_BINDING, "GL_TEXTURE_BUFFER");
		spBufferBinding.put(GL46.GL_COPY_READ_BUFFER_BINDING, "GL_COPY_READ_BUFFER");
		spBufferBinding.put(GL46.GL_COPY_WRITE_BUFFER_BINDING, "GL_COPY_WRITE_BUFFER");
		spBufferBinding.put(GL46.GL_DRAW_INDIRECT_BUFFER_BINDING, "GL_DRAW_INDIRECT_BUFFER");
		spBufferBinding.put(GL46.GL_ATOMIC_COUNTER_BUFFER_BINDING, "GL_ATOMIC_COUNTER_BUFFER");

		spBufferBinding.put(GL46.GL_ARRAY_BUFFER, "GL_ARRAY_BUFFER");
		spBufferBinding.put(GL46.GL_ELEMENT_ARRAY_BUFFER, "GL_ELEMENT_ARRAY_BUFFER");
		spBufferBinding.put(GL46.GL_PIXEL_PACK_BUFFER, "GL_PIXEL_PACK_BUFFER");
		spBufferBinding.put(GL46.GL_PIXEL_UNPACK_BUFFER, "GL_PIXEL_UNPACK_BUFFER");
		spBufferBinding.put(GL46.GL_TRANSFORM_FEEDBACK_BUFFER, "GL_TRANSFORM_FEEDBACK_BUFFER");
		spBufferBinding.put(GL46.GL_UNIFORM_BUFFER, "GL_UNIFORM_BUFFER");

		spBufferBinding.put(GL46.GL_TEXTURE_BUFFER, "GL_TEXTURE_BUFFER");
		spBufferBinding.put(GL46.GL_COPY_READ_BUFFER, "GL_COPY_READ_BUFFER");
		spBufferBinding.put(GL46.GL_COPY_WRITE_BUFFER, "GL_COPY_WRITE_BUFFER");
		spBufferBinding.put(GL46.GL_DRAW_INDIRECT_BUFFER, "GL_DRAW_INDIRECT_BUFFER");
		spBufferBinding.put(GL46.GL_ATOMIC_COUNTER_BUFFER, "GL_ATOMIC_COUNTER_BUFFER");

		spBufferBound.put(GL46.GL_ARRAY_BUFFER_BINDING, GL46.GL_ARRAY_BUFFER);
		spBufferBound.put(GL46.GL_ELEMENT_ARRAY_BUFFER_BINDING, GL46.GL_ELEMENT_ARRAY_BUFFER);
		spBufferBound.put(GL46.GL_PIXEL_PACK_BUFFER_BINDING, GL46.GL_PIXEL_PACK_BUFFER);
		spBufferBound.put(GL46.GL_PIXEL_UNPACK_BUFFER_BINDING, GL46.GL_PIXEL_UNPACK_BUFFER);
		spBufferBound.put(GL46.GL_TRANSFORM_FEEDBACK_BUFFER_BINDING, GL46.GL_TRANSFORM_FEEDBACK_BUFFER);
		spBufferBound.put(GL46.GL_UNIFORM_BUFFER_BINDING, GL46.GL_UNIFORM_BUFFER);

		spBufferBound.put(GL46.GL_TEXTURE_BUFFER_BINDING, GL46.GL_TEXTURE_BUFFER);
		spBufferBound.put(GL46.GL_COPY_READ_BUFFER_BINDING, GL46.GL_COPY_READ_BUFFER);
		spBufferBound.put(GL46.GL_COPY_WRITE_BUFFER_BINDING, GL46.GL_COPY_WRITE_BUFFER);
		spBufferBound.put(GL46.GL_DRAW_INDIRECT_BUFFER_BINDING, GL46.GL_DRAW_INDIRECT_BUFFER);
		spBufferBound.put(GL46.GL_ATOMIC_COUNTER_BUFFER_BINDING, GL46.GL_ATOMIC_COUNTER_BUFFER);

		spBoundBuffer.put(GL46.GL_ARRAY_BUFFER, GL46.GL_ARRAY_BUFFER_BINDING);
		spBoundBuffer.put(GL46.GL_ELEMENT_ARRAY_BUFFER, GL46.GL_ELEMENT_ARRAY_BUFFER_BINDING);
		spBoundBuffer.put(GL46.GL_PIXEL_PACK_BUFFER, GL46.GL_PIXEL_PACK_BUFFER_BINDING);
		spBoundBuffer.put(GL46.GL_PIXEL_UNPACK_BUFFER, GL46.GL_PIXEL_UNPACK_BUFFER_BINDING);
		spBoundBuffer.put(GL46.GL_TRANSFORM_FEEDBACK_BUFFER, GL46.GL_TRANSFORM_FEEDBACK_BUFFER_BINDING);
		spBoundBuffer.put(GL46.GL_UNIFORM_BUFFER, GL46.GL_UNIFORM_BUFFER_BINDING);

		spBoundBuffer.put(GL46.GL_TEXTURE_BUFFER, GL46.GL_TEXTURE_BUFFER_BINDING);
		spBoundBuffer.put(GL46.GL_COPY_READ_BUFFER, GL46.GL_COPY_READ_BUFFER_BINDING);
		spBoundBuffer.put(GL46.GL_COPY_WRITE_BUFFER, GL46.GL_COPY_WRITE_BUFFER_BINDING);
		spBoundBuffer.put(GL46.GL_DRAW_INDIRECT_BUFFER, GL46.GL_DRAW_INDIRECT_BUFFER);
		spBoundBuffer.put(GL46.GL_ATOMIC_COUNTER_BUFFER, GL46.GL_ATOMIC_COUNTER_BUFFER);

		spBufferUsage.put(GL46.GL_STREAM_DRAW, "GL_STREAM_DRAW");
		spBufferUsage.put(GL46.GL_STREAM_READ, "GL_STREAM_READ");
		spBufferUsage.put(GL46.GL_STREAM_COPY, "GL_STREAM_COPY");
		spBufferUsage.put(GL46.GL_STATIC_DRAW, "GL_STATIC_DRAW");
		spBufferUsage.put(GL46.GL_STATIC_READ, "GL_STATIC_READ");
		spBufferUsage.put(GL46.GL_STATIC_COPY, "GL_STATIC_COPY");
		spBufferUsage.put(GL46.GL_DYNAMIC_DRAW, "GL_DYNAMIC_DRAW");
		spBufferUsage.put(GL46.GL_DYNAMIC_READ, "GL_DYNAMIC_READ");
		spBufferUsage.put(GL46.GL_DYNAMIC_COPY, "GL_DYNAMIC_COPY");

		spBufferAccess.put(GL46.GL_READ_ONLY, "GL_READ_ONLY");
		spBufferAccess.put(GL46.GL_WRITE_ONLY, "GL_WRITE_ONLY");
		spBufferAccess.put(GL46.GL_READ_WRITE, "GL_READ_WRITE");

		spTextureTarget.put(GL46.GL_TEXTURE_1D, "GL_TEXTURE_1D");
		spTextureTarget.put(GL46.GL_TEXTURE_1D_ARRAY, "GL_TEXTURE_1D_ARRAY");
		spTextureTarget.put(GL46.GL_TEXTURE_2D, "GL_TEXTURE_2D");
		spTextureTarget.put(GL46.GL_TEXTURE_2D_ARRAY, "GL_TEXTURE_2D_ARRAY");
		spTextureTarget.put(GL46.GL_TEXTURE_2D_MULTISAMPLE, "GL_TEXTURE_2D_MULTISAMPLE");
		spTextureTarget.put(GL46.GL_TEXTURE_2D_MULTISAMPLE_ARRAY, "GL_TEXTURE_2D_MULTISAMPLE_ARRAY");
		spTextureTarget.put(GL46.GL_TEXTURE_3D, "GL_TEXTURE_3D");
		spTextureTarget.put(GL46.GL_TEXTURE_BUFFER, "GL_TEXTURE_BUFFER");
		spTextureTarget.put(GL46.GL_TEXTURE_CUBE_MAP, "GL_TEXTURE_CUBE_MAP");
		spTextureTarget.put(GL46.GL_TEXTURE_RECTANGLE, "GL_TEXTURE_RECTANGLE");

		spTextureBound.put(GL46.GL_TEXTURE_1D, GL46.GL_TEXTURE_BINDING_1D);
		spTextureBound.put(GL46.GL_TEXTURE_1D_ARRAY, GL46.GL_TEXTURE_BINDING_1D_ARRAY);
		spTextureBound.put(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_BINDING_2D);
		spTextureBound.put(GL46.GL_TEXTURE_2D_ARRAY, GL46.GL_TEXTURE_BINDING_2D_ARRAY);
		spTextureBound.put(GL46.GL_TEXTURE_2D_MULTISAMPLE, GL46.GL_TEXTURE_BINDING_2D_MULTISAMPLE);
		spTextureBound.put(GL46.GL_TEXTURE_2D_MULTISAMPLE_ARRAY, GL46.GL_TEXTURE_BINDING_2D_MULTISAMPLE_ARRAY);
		spTextureBound.put(GL46.GL_TEXTURE_3D, GL46.GL_TEXTURE_BINDING_3D);
		spTextureBound.put(GL46.GL_TEXTURE_BUFFER, GL46.GL_TEXTURE_BINDING_BUFFER);
		spTextureBound.put(GL46.GL_TEXTURE_CUBE_MAP, GL46.GL_TEXTURE_BINDING_CUBE_MAP);
		spTextureBound.put(GL46.GL_TEXTURE_RECTANGLE, GL46.GL_TEXTURE_BINDING_RECTANGLE);

		spTextureUnit.put(GL46.GL_TEXTURE0, "GL_TEXTURE0");
		spTextureUnit.put(GL46.GL_TEXTURE1, "GL_TEXTURE1");
		spTextureUnit.put(GL46.GL_TEXTURE2, "GL_TEXTURE2");
		spTextureUnit.put(GL46.GL_TEXTURE3, "GL_TEXTURE3");
		spTextureUnit.put(GL46.GL_TEXTURE4, "GL_TEXTURE4");
		spTextureUnit.put(GL46.GL_TEXTURE5, "GL_TEXTURE5");
		spTextureUnit.put(GL46.GL_TEXTURE6, "GL_TEXTURE6");
		spTextureUnit.put(GL46.GL_TEXTURE7, "GL_TEXTURE7");

		spTextureCompMode.put(GL46.GL_NONE, "GL_NONE");
		spTextureCompFunc.put(GL46.GL_COMPARE_REF_TO_TEXTURE, "GL_COMPARE_REF_TO_TEXTURE");

		spTextureCompFunc.put(GL46.GL_LEQUAL, "GL_LEQUAL");
		spTextureCompFunc.put(GL46.GL_GEQUAL, "GL_GEQUAL");
		spTextureCompFunc.put(GL46.GL_LESS, "GL_LESS");
		spTextureCompFunc.put(GL46.GL_GREATER, "GL_GREATER");
		spTextureCompFunc.put(GL46.GL_EQUAL, "GL_EQUAL");
		spTextureCompFunc.put(GL46.GL_NOTEQUAL, "GL_NOTEQUAL");
		spTextureCompFunc.put(GL46.GL_ALWAYS, "GL_ALWAYS");
		spTextureCompFunc.put(GL46.GL_NEVER, "GL_NEVER");

		spTextureWrap.put(GL46.GL_CLAMP_TO_EDGE, "GL_CLAMP_TO_EDGE");
		spTextureWrap.put(GL46.GL_CLAMP_TO_BORDER, "GL_CLAMP_TO_BORDER");
		spTextureWrap.put(GL46.GL_MIRRORED_REPEAT, "GL_MIRRORED_REPEAT");
		spTextureWrap.put(GL46.GL_REPEAT, "GL_REPEAT");

		spTextureFilter.put(GL46.GL_NEAREST, "GL_NEAREST");
		spTextureFilter.put(GL46.GL_LINEAR, "GL_LINEAR");
		spTextureFilter.put(GL46.GL_NEAREST_MIPMAP_NEAREST, "GL_NEAREST_MIPMAP_NEAREST");
		spTextureFilter.put(GL46.GL_LINEAR_MIPMAP_NEAREST, "GL_LINEAR_MIPMAP_NEAREST");
		spTextureFilter.put(GL46.GL_NEAREST_MIPMAP_LINEAR, "GL_NEAREST_MIPMAP_LINEAR");
		spTextureFilter.put(GL46.GL_LINEAR_MIPMAP_LINEAR, "GL_LINEAR_MIPMAP_LINEAR");

		spGLSLTypeSize.put(GL46.GL_FLOAT, Float.BYTES);
		spGLSLTypeSize.put(GL46.GL_FLOAT_VEC2, Float.BYTES * 2);
		spGLSLTypeSize.put(GL46.GL_FLOAT_VEC3, Float.BYTES * 3);
		spGLSLTypeSize.put(GL46.GL_FLOAT_VEC4, Float.BYTES * 4);

		spGLSLTypeSize.put(GL46.GL_DOUBLE, Double.BYTES);
		spGLSLTypeSize.put(GL46.GL_DOUBLE_VEC2, Double.BYTES * 2);
		spGLSLTypeSize.put(GL46.GL_DOUBLE_VEC3, Double.BYTES * 3);
		spGLSLTypeSize.put(GL46.GL_DOUBLE_VEC4, Double.BYTES * 4);

		spGLSLTypeSize.put(GL46.GL_SAMPLER_1D, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_2D, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_3D, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_CUBE, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_1D_SHADOW, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_2D_SHADOW, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_1D_ARRAY, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_2D_ARRAY, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_1D_ARRAY_SHADOW, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_2D_ARRAY_SHADOW, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_2D_MULTISAMPLE, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_2D_MULTISAMPLE_ARRAY, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_CUBE_SHADOW, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_BUFFER, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_2D_RECT, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_SAMPLER_2D_RECT_SHADOW, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_INT_SAMPLER_1D, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_INT_SAMPLER_2D, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_INT_SAMPLER_3D, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_INT_SAMPLER_CUBE, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_INT_SAMPLER_1D_ARRAY, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_INT_SAMPLER_2D_ARRAY, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_INT_SAMPLER_2D_MULTISAMPLE, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_INT_SAMPLER_BUFFER, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_INT_SAMPLER_2D_RECT, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_SAMPLER_1D, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_SAMPLER_2D, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_SAMPLER_3D, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_SAMPLER_CUBE, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_SAMPLER_1D_ARRAY, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_SAMPLER_2D_ARRAY, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_SAMPLER_BUFFER, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_SAMPLER_2D_RECT, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_BOOL, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_INT, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_BOOL_VEC2, Integer.BYTES * 2);
		spGLSLTypeSize.put(GL46.GL_INT_VEC2, Integer.BYTES * 2);
		spGLSLTypeSize.put(GL46.GL_BOOL_VEC3, Integer.BYTES * 3);
		spGLSLTypeSize.put(GL46.GL_INT_VEC3, Integer.BYTES * 3);
		spGLSLTypeSize.put(GL46.GL_BOOL_VEC4, Integer.BYTES * 4);
		spGLSLTypeSize.put(GL46.GL_INT_VEC4, Integer.BYTES * 4);

		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT, Integer.BYTES);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_VEC2, Integer.BYTES * 2);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_VEC3, Integer.BYTES * 2);
		spGLSLTypeSize.put(GL46.GL_UNSIGNED_INT_VEC4, Integer.BYTES * 2);

		spGLSLTypeSize.put(GL46.GL_FLOAT_MAT2, Float.BYTES * 4);
		spGLSLTypeSize.put(GL46.GL_FLOAT_MAT3, Float.BYTES * 9);
		spGLSLTypeSize.put(GL46.GL_FLOAT_MAT4, Float.BYTES * 16);
		spGLSLTypeSize.put(GL46.GL_FLOAT_MAT2x3, Float.BYTES * 6);
		spGLSLTypeSize.put(GL46.GL_FLOAT_MAT2x4, Float.BYTES * 8);
		spGLSLTypeSize.put(GL46.GL_FLOAT_MAT3x2, Float.BYTES * 6);
		spGLSLTypeSize.put(GL46.GL_FLOAT_MAT3x4, Float.BYTES * 12);
		spGLSLTypeSize.put(GL46.GL_FLOAT_MAT4x2, Float.BYTES * 8);
		spGLSLTypeSize.put(GL46.GL_FLOAT_MAT4x3, Float.BYTES * 12);
		spGLSLTypeSize.put(GL46.GL_DOUBLE_MAT2, Double.BYTES * 4);
		spGLSLTypeSize.put(GL46.GL_DOUBLE_MAT3, Double.BYTES * 9);
		spGLSLTypeSize.put(GL46.GL_DOUBLE_MAT4, Double.BYTES * 16);
		spGLSLTypeSize.put(GL46.GL_DOUBLE_MAT2x3, Double.BYTES * 6);
		spGLSLTypeSize.put(GL46.GL_DOUBLE_MAT2x4, Double.BYTES * 8);
		spGLSLTypeSize.put(GL46.GL_DOUBLE_MAT3x2, Double.BYTES * 6);
		spGLSLTypeSize.put(GL46.GL_DOUBLE_MAT3x4, Double.BYTES * 12);
		spGLSLTypeSize.put(GL46.GL_DOUBLE_MAT4x2, Double.BYTES * 8);
		spGLSLTypeSize.put(GL46.GL_DOUBLE_MAT4x3, Double.BYTES * 12);

		spGLSLType.put(GL46.GL_FLOAT, "GL_FLOAT");
		spGLSLType.put(GL46.GL_FLOAT_VEC2, "GL_FLOAT_VEC2");
		spGLSLType.put(GL46.GL_FLOAT_VEC3, "GL_FLOAT_VEC3");
		spGLSLType.put(GL46.GL_FLOAT_VEC4, "GL_FLOAT_VEC4");
		spGLSLType.put(GL46.GL_DOUBLE, "GL_DOUBLE");
		spGLSLType.put(GL46.GL_DOUBLE_VEC2, "GL_DOUBLE_VEC2");
		spGLSLType.put(GL46.GL_DOUBLE_VEC3, "GL_DOUBLE_VEC3");
		spGLSLType.put(GL46.GL_DOUBLE_VEC4, "GL_DOUBLE_VEC4");
		spGLSLType.put(GL46.GL_SAMPLER_1D, "GL_SAMPLER_1D");
		spGLSLType.put(GL46.GL_SAMPLER_2D, "GL_SAMPLER_2D");
		spGLSLType.put(GL46.GL_SAMPLER_3D, "GL_SAMPLER_3D");
		spGLSLType.put(GL46.GL_SAMPLER_CUBE, "GL_SAMPLER_CUBE");
		spGLSLType.put(GL46.GL_SAMPLER_1D_SHADOW, "GL_SAMPLER_1D_SHADOW");
		spGLSLType.put(GL46.GL_SAMPLER_2D_SHADOW, "GL_SAMPLER_2D_SHADOW");
		spGLSLType.put(GL46.GL_SAMPLER_1D_ARRAY, "GL_SAMPLER_1D_ARRAY");
		spGLSLType.put(GL46.GL_SAMPLER_2D_ARRAY, "GL_SAMPLER_2D_ARRAY");
		spGLSLType.put(GL46.GL_SAMPLER_1D_ARRAY_SHADOW, "GL_SAMPLER_1D_ARRAY_SHADOW");
		spGLSLType.put(GL46.GL_SAMPLER_2D_ARRAY_SHADOW, "GL_SAMPLER_2D_ARRAY_SHADOW");
		spGLSLType.put(GL46.GL_SAMPLER_2D_MULTISAMPLE, "GL_SAMPLER_2D_MULTISAMPLE");
		spGLSLType.put(GL46.GL_SAMPLER_2D_MULTISAMPLE_ARRAY, "GL_SAMPLER_2D_MULTISAMPLE_ARRAY");
		spGLSLType.put(GL46.GL_SAMPLER_CUBE_SHADOW, "GL_SAMPLER_CUBE_SHADOW");
		spGLSLType.put(GL46.GL_SAMPLER_BUFFER, "GL_SAMPLER_BUFFER");
		spGLSLType.put(GL46.GL_SAMPLER_2D_RECT, "GL_SAMPLER_2D_RECT");
		spGLSLType.put(GL46.GL_SAMPLER_2D_RECT_SHADOW, "GL_SAMPLER_2D_RECT_SHADOW");
		spGLSLType.put(GL46.GL_INT_SAMPLER_1D, "GL_INT_SAMPLER_1D");
		spGLSLType.put(GL46.GL_INT_SAMPLER_2D, "GL_INT_SAMPLER_2D");
		spGLSLType.put(GL46.GL_INT_SAMPLER_3D, "GL_INT_SAMPLER_3D");
		spGLSLType.put(GL46.GL_INT_SAMPLER_CUBE, "GL_INT_SAMPLER_CUBE");
		spGLSLType.put(GL46.GL_INT_SAMPLER_1D_ARRAY, "GL_INT_SAMPLER_1D_ARRAY");
		spGLSLType.put(GL46.GL_INT_SAMPLER_2D_ARRAY, "GL_INT_SAMPLER_2D_ARRAY");
		spGLSLType.put(GL46.GL_INT_SAMPLER_2D_MULTISAMPLE, "GL_INT_SAMPLER_2D_MULTISAMPLE");
		spGLSLType.put(GL46.GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, "GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY");
		spGLSLType.put(GL46.GL_INT_SAMPLER_BUFFER, "GL_INT_SAMPLER_BUFFER");
		spGLSLType.put(GL46.GL_INT_SAMPLER_2D_RECT, "GL_INT_SAMPLER_2D_RECT");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_SAMPLER_1D, "GL_UNSIGNED_INT_SAMPLER_1D");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_SAMPLER_2D, "GL_UNSIGNED_INT_SAMPLER_2D");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_SAMPLER_3D, "GL_UNSIGNED_INT_SAMPLER_3D");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_SAMPLER_CUBE, "GL_UNSIGNED_INT_SAMPLER_CUBE");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_SAMPLER_1D_ARRAY, "GL_UNSIGNED_INT_SAMPLER_1D_ARRAY");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_SAMPLER_2D_ARRAY, "GL_UNSIGNED_INT_SAMPLER_2D_ARRAY");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE, "GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY,
				"GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_SAMPLER_BUFFER, "GL_UNSIGNED_INT_SAMPLER_BUFFER");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_SAMPLER_2D_RECT, "GL_UNSIGNED_INT_SAMPLER_2D_RECT");
		spGLSLType.put(GL46.GL_BOOL, "GL_BOOL");
		spGLSLType.put(GL46.GL_INT, "GL_INT");
		spGLSLType.put(GL46.GL_BOOL_VEC2, "GL_BOOL_VEC2");
		spGLSLType.put(GL46.GL_INT_VEC2, "GL_INT_VEC2");
		spGLSLType.put(GL46.GL_BOOL_VEC3, "GL_BOOL_VEC3");
		spGLSLType.put(GL46.GL_INT_VEC3, "GL_INT_VEC3");
		spGLSLType.put(GL46.GL_BOOL_VEC4, "GL_BOOL_VEC4");
		spGLSLType.put(GL46.GL_INT_VEC4, "GL_INT_VEC4");
		spGLSLType.put(GL46.GL_UNSIGNED_INT, "GL_UNSIGNED_INT");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_VEC2, "GL_UNSIGNED_INT_VEC2");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_VEC3, "GL_UNSIGNED_INT_VEC3");
		spGLSLType.put(GL46.GL_UNSIGNED_INT_VEC4, "GL_UNSIGNED_INT_VEC4");
		spGLSLType.put(GL46.GL_FLOAT_MAT2, "GL_FLOAT_MAT2");
		spGLSLType.put(GL46.GL_FLOAT_MAT3, "GL_FLOAT_MAT3");
		spGLSLType.put(GL46.GL_FLOAT_MAT4, "GL_FLOAT_MAT4");
		spGLSLType.put(GL46.GL_FLOAT_MAT2x3, "GL_FLOAT_MAT2x3");
		spGLSLType.put(GL46.GL_FLOAT_MAT2x4, "GL_FLOAT_MAT2x4");
		spGLSLType.put(GL46.GL_FLOAT_MAT3x2, "GL_FLOAT_MAT3x2");
		spGLSLType.put(GL46.GL_FLOAT_MAT3x4, "GL_FLOAT_MAT3x4");
		spGLSLType.put(GL46.GL_FLOAT_MAT4x2, "GL_FLOAT_MAT4x2");
		spGLSLType.put(GL46.GL_FLOAT_MAT4x3, "GL_FLOAT_MAT4x3");
		spGLSLType.put(GL46.GL_DOUBLE_MAT2, "GL_DOUBLE_MAT2");
		spGLSLType.put(GL46.GL_DOUBLE_MAT3, "GL_DOUBLE_MAT3");
		spGLSLType.put(GL46.GL_DOUBLE_MAT4, "GL_DOUBLE_MAT4");
		spGLSLType.put(GL46.GL_DOUBLE_MAT2x3, "GL_DOUBLE_MAT2x3");
		spGLSLType.put(GL46.GL_DOUBLE_MAT2x4, "GL_DOUBLE_MAT2x4");
		spGLSLType.put(GL46.GL_DOUBLE_MAT3x2, "GL_DOUBLE_MAT3x2");
		spGLSLType.put(GL46.GL_DOUBLE_MAT3x4, "GL_DOUBLE_MAT3x4");
		spGLSLType.put(GL46.GL_DOUBLE_MAT4x2, "GL_DOUBLE_MAT4x2");
		spGLSLType.put(GL46.GL_DOUBLE_MAT4x3, "GL_DOUBLE_MAT4x3");

		spTextureDataType.put(GL46.GL_NONE, "GL_NONE");
		spTextureDataType.put(GL46.GL_SIGNED_NORMALIZED, "GL_SIGNED_NORMALIZED");
		spTextureDataType.put(GL46.GL_UNSIGNED_NORMALIZED, "GL_UNSIGNED_NORMALIZED");
		spTextureDataType.put(GL46.GL_FLOAT, "GL_FLOAT");
		spTextureDataType.put(GL46.GL_INT, "GL_INT");
		spTextureDataType.put(GL46.GL_UNSIGNED_INT, "GL_UNSIGNED_INT");

		spDataF.put(GL46.GL_UNSIGNED_BYTE, "GL_UNSIGNED_BYTE");
		spDataF.put(GL46.GL_BYTE, "GL_BYTE");
		spDataF.put(GL46.GL_UNSIGNED_SHORT, "GL_UNSIGNED_SHORT");
		spDataF.put(GL46.GL_SHORT, "GL_SHORT");
		spDataF.put(GL46.GL_UNSIGNED_INT, "GL_UNSIGNED_INT");
		spDataF.put(GL46.GL_INT, "GL_INT");
		spDataF.put(GL46.GL_HALF_FLOAT, "GL_HALF_FLOAT");
		spDataF.put(GL46.GL_FLOAT, "GL_FLOAT");

		spDataF.put(GL46.GL_UNSIGNED_BYTE_3_3_2, "GL_UNSIGNED_BYTE_3_3_2");
		spDataF.put(GL46.GL_UNSIGNED_BYTE_2_3_3_REV, "GL_UNSIGNED_BYTE_2_3_3_REV");
		spDataF.put(GL46.GL_UNSIGNED_SHORT_5_6_5, "GL_UNSIGNED_SHORT_5_6_5");
		spDataF.put(GL46.GL_UNSIGNED_SHORT_5_6_5_REV, "GL_UNSIGNED_SHORT_5_6_5_REV");
		spDataF.put(GL46.GL_UNSIGNED_SHORT_4_4_4_4, "GL_UNSIGNED_SHORT_4_4_4_4");
		spDataF.put(GL46.GL_UNSIGNED_SHORT_4_4_4_4_REV, "GL_UNSIGNED_SHORT_4_4_4_4_REV");
		spDataF.put(GL46.GL_UNSIGNED_SHORT_5_5_5_1, "GL_UNSIGNED_SHORT_5_5_5_1");
		spDataF.put(GL46.GL_UNSIGNED_SHORT_1_5_5_5_REV, "GL_UNSIGNED_SHORT_1_5_5_5_REV");
		spDataF.put(GL46.GL_UNSIGNED_INT_8_8_8_8, "GL_UNSIGNED_INT_8_8_8_8");
		spDataF.put(GL46.GL_UNSIGNED_INT_8_8_8_8_REV, "GL_UNSIGNED_INT_8_8_8_8_REV");
		spDataF.put(GL46.GL_UNSIGNED_INT_10_10_10_2, "GL_UNSIGNED_INT_10_10_10_2");
		spDataF.put(GL46.GL_UNSIGNED_INT_2_10_10_10_REV, "GL_UNSIGNED_INT_2_10_10_10_REV");

		spInternalF.put(GL46.GL_STENCIL_INDEX, "GL_STENCIL_INDEX");
		spInternalF.put(GL46.GL_DEPTH_COMPONENT, "GL_DEPTH_COMPONENT");
		spInternalF.put(GL46.GL_DEPTH_STENCIL, "GL_DEPTH_STENCIL");
		spInternalF.put(GL46.GL_DEPTH_COMPONENT16, "GL_DEPTH_COMPONENT16");
		spInternalF.put(GL46.GL_DEPTH_COMPONENT24, "GL_DEPTH_COMPONENT24");
		spInternalF.put(GL46.GL_DEPTH_COMPONENT32, "GL_DEPTH_COMPONENT32");
		spInternalF.put(GL46.GL_DEPTH_COMPONENT32F, "GL_DEPTH_COMPONENT32F");
		spInternalF.put(GL46.GL_DEPTH24_STENCIL8, "GL_DEPTH24_STENCIL8");
		spInternalF.put(GL46.GL_DEPTH32F_STENCIL8, "GL_DEPTH32F_STENCIL8");
		spInternalF.put(GL46.GL_RED_INTEGER, "GL_RED_INTEGER");
		spInternalF.put(GL46.GL_GREEN_INTEGER, "GL_GREEN_INTEGER");
		spInternalF.put(GL46.GL_BLUE_INTEGER, "GL_BLUE_INTEGER");

		spInternalF.put(GL46.GL_RG_INTEGER, "GL_RG_INTEGER");
		spInternalF.put(GL46.GL_RGB_INTEGER, "GL_RGB_INTEGER");
		spInternalF.put(GL46.GL_RGBA_INTEGER, "GL_RGBA_INTEGER");
		spInternalF.put(GL46.GL_BGR_INTEGER, "GL_BGR_INTEGER");
		spInternalF.put(GL46.GL_BGRA_INTEGER, "GL_BGRA_INTEGER");

		spInternalF.put(GL46.GL_RED, "GL_RED");
		spInternalF.put(GL46.GL_RG, "GL_RG");
		spInternalF.put(GL46.GL_RGB, "GL_RGB");
		spInternalF.put(GL46.GL_RGBA, "GL_RGBA");
		spInternalF.put(GL46.GL_R3_G3_B2, "GL_R3_G3_B2");
		/* TODO: Not Available in current lwjgl build */
		/* spInternalF.put(GL46.GL_RGB2_EXT, "GL_RGB2_EXT"); */
		spInternalF.put(0x804E, "GL_RGB2_EXT");

		spInternalF.put(GL46.GL_COMPRESSED_RED, "GL_COMPRESSED_RED");
		spInternalF.put(GL46.GL_COMPRESSED_RG, "GL_COMPRESSED_RG");
		spInternalF.put(GL46.GL_COMPRESSED_RGB, "GL_COMPRESSED_RGB");
		spInternalF.put(GL46.GL_COMPRESSED_RGBA, "GL_COMPRESSED_RGBA");
		spInternalF.put(GL46.GL_COMPRESSED_SRGB, "GL_COMPRESSED_SRGB");
		spInternalF.put(GL46.GL_COMPRESSED_SRGB_ALPHA, "GL_COMPRESSED_SRGB_ALPHA");
		spInternalF.put(GL46.GL_COMPRESSED_RED_RGTC1, "GL_COMPRESSED_RED_RGTC1");
		spInternalF.put(GL46.GL_COMPRESSED_SIGNED_RED_RGTC1, "GL_COMPRESSED_SIGNED_RED_RGTC1");
		spInternalF.put(GL46.GL_COMPRESSED_RG_RGTC2, "GL_COMPRESSED_RG_RGTC2");
		spInternalF.put(GL46.GL_RG, "GL_RG");
		spInternalF.put(GL46.GL_COMPRESSED_SIGNED_RG_RGTC2, "GL_COMPRESSED_SIGNED_RG_RGTC2");

		spInternalF.put(GL46.GL_COMPRESSED_RGBA_BPTC_UNORM, "GL_COMPRESSED_RGBA_BPTC_UNORM");
		spInternalF.put(GL46.GL_COMPRESSED_SRGB_ALPHA_BPTC_UNORM, "GL_COMPRESSED_SRGB_ALPHA_BPTC_UNORM");
		spInternalF.put(GL46.GL_COMPRESSED_RGB_BPTC_SIGNED_FLOAT, "GL_COMPRESSED_RGB_BPTC_SIGNED_FLOAT");
		spInternalF.put(GL46.GL_COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT, "GL_COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT");

		spInternalF.put(GL46.GL_R8, "GL_R8");
		spInternalF.put(GL46.GL_R16, "GL_R16");
		spInternalF.put(GL46.GL_RG8, "GL_RG8");
		spInternalF.put(GL46.GL_RG16, "GL_RG16");
		spInternalF.put(GL46.GL_R16F, "GL_R16F");
		spInternalF.put(GL46.GL_R32F, "GL_R32F");
		spInternalF.put(GL46.GL_RG16F, "GL_RG16F");
		spInternalF.put(GL46.GL_RG32F, "GL_RG32F");
		spInternalF.put(GL46.GL_R8I, "GL_R8I");
		spInternalF.put(GL46.GL_R8UI, "GL_R8UI");
		spInternalF.put(GL46.GL_R16I, "GL_R16I");
		spInternalF.put(GL46.GL_R16UI, "GL_R16UI");
		spInternalF.put(GL46.GL_R32I, "GL_R32I");
		spInternalF.put(GL46.GL_R32UI, "GL_R32UI");
		spInternalF.put(GL46.GL_RG8I, "GL_RG8I");
		spInternalF.put(GL46.GL_RG8UI, "GL_RG8UI");
		spInternalF.put(GL46.GL_RG16I, "GL_RG16I");
		spInternalF.put(GL46.GL_RG16UI, "GL_RG16UI");
		spInternalF.put(GL46.GL_RG32I, "GL_RG32I");
		spInternalF.put(GL46.GL_RG32UI, "GL_RG32UI");
		/* TODO: Not Available in current lwjgl build */
		/*
		 * spInternalF.put(GL46.GL_RGB_S3TC, "GL_RGB_S3TC");
		 * spInternalF.put(GL46.GL_RGB4_S3TC, "GL_RGB4_S3TC");
		 * spInternalF.put(GL46.GL_RGBA_S3TC, "GL_RGBA_S3TC");
		 * spInternalF.put(GL46.GL_RGBA4_S3TC, "GL_RGBA4_S3TC");
		 * spInternalF.put(GL46.GL_RGBA_DXT5_S3TC, "GL_RGBA_DXT5_S3TC");
		 * spInternalF.put(GL46.GL_RGBA4_DXT5_S3TC, "GL_RGBA4_DXT5_S3TC");
		 */
		spInternalF.put(0x83A0, "GL_RGB_S3TC");
		spInternalF.put(0x83A1, "GL_RGB4_S3TC");
		spInternalF.put(0x83A2, "GL_RGBA_S3TC");
		spInternalF.put(0x83A3, "GL_RGBA4_S3TC");
		spInternalF.put(0x83A4, "GL_RGBA_DXT5_S3TC");
		spInternalF.put(0x83A5, "GL_RGBA4_DXT5_S3TC");

		spInternalF.put(EXTTextureCompressionS3TC.GL_COMPRESSED_RGB_S3TC_DXT1_EXT, "GL_COMPRESSED_RGB_S3TC_DXT1_EXT");
		spInternalF.put(EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT, "GL_COMPRESSED_RGBA_S3TC_DXT1_EXT");
		spInternalF.put(EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT, "GL_COMPRESSED_RGBA_S3TC_DXT3_EXT");
		spInternalF.put(EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT, "GL_COMPRESSED_RGBA_S3TC_DXT5_EXT");
		/* TODO: Not Available in current lwjgl build */
		/*
		 * spInternalF.put(GL_R1UI_V3F_SUN, "GL_R1UI_V3F_SUN");
		 * spInternalF.put(GL_R1UI_C4UB_V3F_SUN, "GL_R1UI_C4UB_V3F_SUN");
		 * spInternalF.put(GL_R1UI_C3F_V3F_SUN, "GL_R1UI_C3F_V3F_SUN");
		 * spInternalF.put(GL_R1UI_N3F_V3F_SUN, "GL_R1UI_N3F_V3F_SUN");
		 * spInternalF.put(GL_R1UI_C4F_N3F_V3F_SUN, "GL_R1UI_C4F_N3F_V3F_SUN");
		 * spInternalF.put(GL_R1UI_T2F_V3F_SUN, "GL_R1UI_T2F_V3F_SUN");
		 * spInternalF.put(GL_R1UI_T2F_N3F_V3F_SUN, "GL_R1UI_T2F_N3F_V3F_SUN");
		 * spInternalF.put(GL_R1UI_T2F_C4F_N3F_V3F_SUN, "GL_R1UI_T2F_C4F_N3F_V3F_SUN");
		 * 
		 * spInternalF.put(GL_RGB_SIGNED_SGIX, "GL_RGB_SIGNED_SGIX");
		 * spInternalF.put(GL_RGBA_SIGNED_SGIX, "GL_RGBA_SIGNED_SGIX");
		 * spInternalF.put(GL_RGB16_SIGNED_SGIX, "GL_RGB16_SIGNED_SGIX");
		 * spInternalF.put(GL_RGBA16_SIGNED_SGIX, "GL_RGBA16_SIGNED_SGIX");
		 * spInternalF.put(GL_RGB_EXTENDED_RANGE_SGIX, "GL_RGB_EXTENDED_RANGE_SGIX");
		 * spInternalF.put(GL_RGBA_EXTENDED_RANGE_SGIX, "GL_RGBA_EXTENDED_RANGE_SGIX");
		 * spInternalF.put(GL_RGB16_EXTENDED_RANGE_SGIX,
		 * "GL_RGB16_EXTENDED_RANGE_SGIX");
		 * spInternalF.put(GL_RGBA16_EXTENDED_RANGE_SGIX,
		 * "GL_RGBA16_EXTENDED_RANGE_SGIX");
		 * 
		 * spInternalF.put(GL_COMPRESSED_RGB_FXT1_3DFX, "GL_COMPRESSED_RGB_FXT1_3DFX");
		 * spInternalF.put(GL_COMPRESSED_RGBA_FXT1_3DFX,
		 * "GL_COMPRESSED_RGBA_FXT1_3DFX");
		 * spInternalF.put(GL_RGBA_UNSIGNED_DOT_PRODUCT_MAPPING_NV,
		 * "GL_RGBA_UNSIGNED_DOT_PRODUCT_MAPPING_NV");
		 */
		spInternalF.put(0x85C4, "GL_R1UI_V3F_SUN");
		spInternalF.put(0x85C5, "GL_R1UI_C4UB_V3F_SUN");
		spInternalF.put(0x85C6, "GL_R1UI_C3F_V3F_SUN");
		spInternalF.put(0x85C7, "GL_R1UI_N3F_V3F_SUN");
		spInternalF.put(0x85C8, "GL_R1UI_C4F_N3F_V3F_SUN");
		spInternalF.put(0x85C9, "GL_R1UI_T2F_V3F_SUN");
		spInternalF.put(0x85CA, "GL_R1UI_T2F_N3F_V3F_SUN");
		spInternalF.put(0x85CB, "GL_R1UI_T2F_C4F_N3F_V3F_SUN");
		spInternalF.put(0x85E0, "GL_RGB_SIGNED_SGIX");
		spInternalF.put(0x85E1, "GL_RGBA_SIGNED_SGIX");
		spInternalF.put(0x85E6, "GL_RGB16_SIGNED_SGIX");
		spInternalF.put(0x85E7, "GL_RGBA16_SIGNED_SGIX");
		spInternalF.put(0x85EC, "GL_RGB_EXTENDED_RANGE_SGIX");
		spInternalF.put(0x85ED, "GL_RGBA_EXTENDED_RANGE_SGIX");
		spInternalF.put(0x85F2, "GL_RGB16_EXTENDED_RANGE_SGIX");
		spInternalF.put(0x85F3, "GL_RGBA16_EXTENDED_RANGE_SGIX");
		spInternalF.put(0x86B0, "GL_COMPRESSED_RGB_FXT1_3DFX");
		spInternalF.put(0x86B1, "GL_COMPRESSED_RGBA_FXT1_3DFX");
		spInternalF.put(0x86D9, "GL_RGBA_UNSIGNED_DOT_PRODUCT_MAPPING_NV");
		spInternalF.put(ARBColorBufferFloat.GL_RGBA_FLOAT_MODE_ARB, "GL_RGBA_FLOAT_MODE_ARB");
		spInternalF.put(ATITextureCompression3DC.GL_COMPRESSED_LUMINANCE_ALPHA_3DC_ATI,
				"GL_COMPRESSED_LUMINANCE_ALPHA_3DC_ATI");
		spInternalF.put(APPLERGB422.GL_RGB_422_APPLE, "GL_RGB_422_APPLE");
		spInternalF.put(EXTPackedFloat.GL_RGBA_SIGNED_COMPONENTS_EXT, "GL_RGBA_SIGNED_COMPONENTS_EXT");
		spInternalF.put(EXTTextureSRGB.GL_COMPRESSED_SRGB_S3TC_DXT1_EXT, "GL_COMPRESSED_SRGB_S3TC_DXT1_EXT");
		spInternalF.put(EXTTextureSRGB.GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT1_EXT,
				"GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT1_EXT");
		spInternalF.put(EXTTextureSRGB.GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT3_EXT,
				"GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT3_EXT");
		spInternalF.put(EXTTextureSRGB.GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT5_EXT,
				"GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT5_EXT");
		spInternalF.put(EXTTextureCompressionLATC.GL_COMPRESSED_LUMINANCE_LATC1_EXT,
				"GL_COMPRESSED_LUMINANCE_LATC1_EXT");
		spInternalF.put(EXTTextureCompressionLATC.GL_COMPRESSED_SIGNED_LUMINANCE_LATC1_EXT,
				"GL_COMPRESSED_SIGNED_LUMINANCE_LATC1_EXT");
		spInternalF.put(EXTTextureCompressionLATC.GL_COMPRESSED_LUMINANCE_ALPHA_LATC2_EXT,
				"GL_COMPRESSED_LUMINANCE_ALPHA_LATC2_EXT");
		spInternalF.put(EXTTextureCompressionLATC.GL_COMPRESSED_SIGNED_LUMINANCE_ALPHA_LATC2_EXT,
				"GL_COMPRESSED_SIGNED_LUMINANCE_ALPHA_LATC2_EXT");
		spInternalF.put(EXTTextureInteger.GL_RGBA_INTEGER_MODE_EXT, "GL_RGBA_INTEGER_MODE_EXT");
		spInternalF.put(ARBTextureCompressionBPTC.GL_COMPRESSED_RGBA_BPTC_UNORM_ARB,
				"GL_COMPRESSED_RGBA_BPTC_UNORM_ARB");
		spInternalF.put(ARBTextureCompressionBPTC.GL_COMPRESSED_SRGB_ALPHA_BPTC_UNORM_ARB,
				"GL_COMPRESSED_SRGB_ALPHA_BPTC_UNORM_ARB");
		spInternalF.put(ARBTextureCompressionBPTC.GL_COMPRESSED_RGB_BPTC_SIGNED_FLOAT_ARB,
				"GL_COMPRESSED_RGB_BPTC_SIGNED_FLOAT_ARB");
		spInternalF.put(ARBTextureCompressionBPTC.GL_COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT_ARB,
				"GL_COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT_ARB");
		spInternalF.put(EXTTextureSnorm.GL_RG_SNORM, "GL_RG_SNORM");
		spInternalF.put(EXTTextureSnorm.GL_RGB_SNORM, "GL_RGB_SNORM");
		spInternalF.put(EXTTextureSnorm.GL_RGBA_SNORM, "GL_RGBA_SNORM");
		spInternalF.put(GL46.GL_R8_SNORM, "GL_R8_SNORM");
		spInternalF.put(GL46.GL_RG8_SNORM, "GL_RG8_SNORM");
		spInternalF.put(GL46.GL_RGB8_SNORM, "GL_RGB8_SNORM");
		spInternalF.put(GL46.GL_RGBA8_SNORM, "GL_RGBA8_SNORM");
		spInternalF.put(GL46.GL_R16_SNORM, "GL_R16_SNORM");
		spInternalF.put(GL46.GL_RG16_SNORM, "GL_RG16_SNORM");
		spInternalF.put(GL46.GL_RGB16_SNORM, "GL_RGB16_SNORM");
		spInternalF.put(GL46.GL_RGBA16_SNORM, "GL_RGBA16_SNORM");
		spInternalF.put(GL46.GL_RGB10_A2UI, "GL_RGB10_A2UI");
	}
}
