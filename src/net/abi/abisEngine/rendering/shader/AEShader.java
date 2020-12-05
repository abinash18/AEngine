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
package net.abi.abisEngine.rendering.shader;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1fv;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform1iv;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform2fv;
import static org.lwjgl.opengl.GL20.glUniform2i;
import static org.lwjgl.opengl.GL20.glUniform2iv;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniform3i;
import static org.lwjgl.opengl.GL20.glUniform3iv;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniform4fv;
import static org.lwjgl.opengl.GL20.glUniform4i;
import static org.lwjgl.opengl.GL20.glUniform4iv;
import static org.lwjgl.opengl.GL20.glUniformMatrix2fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL21.glUniformMatrix2x3fv;
import static org.lwjgl.opengl.GL21.glUniformMatrix2x4fv;
import static org.lwjgl.opengl.GL21.glUniformMatrix3x2fv;
import static org.lwjgl.opengl.GL21.glUniformMatrix3x4fv;
import static org.lwjgl.opengl.GL21.glUniformMatrix4x2fv;
import static org.lwjgl.opengl.GL21.glUniformMatrix4x3fv;
import static org.lwjgl.opengl.GL30.glUniform1ui;
import static org.lwjgl.opengl.GL30.glUniform1uiv;
import static org.lwjgl.opengl.GL30.glUniform2ui;
import static org.lwjgl.opengl.GL30.glUniform2uiv;
import static org.lwjgl.opengl.GL30.glUniform3ui;
import static org.lwjgl.opengl.GL30.glUniform3uiv;
import static org.lwjgl.opengl.GL30.glUniform4ui;
import static org.lwjgl.opengl.GL30.glUniform4uiv;
import static org.lwjgl.opengl.GL40.glUniform1d;
import static org.lwjgl.opengl.GL40.glUniform1dv;
import static org.lwjgl.opengl.GL40.glUniform2d;
import static org.lwjgl.opengl.GL40.glUniform2dv;
import static org.lwjgl.opengl.GL40.glUniform3d;
import static org.lwjgl.opengl.GL40.glUniform3dv;
import static org.lwjgl.opengl.GL40.glUniform4d;
import static org.lwjgl.opengl.GL40.glUniform4dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix2dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix2x3dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix2x4dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix3dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix3x2dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix3x4dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix4dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix4x2dv;
import static org.lwjgl.opengl.GL40.glUniformMatrix4x3dv;
import static org.lwjgl.opengl.GL43.GL_LOCATION;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.InvalidPathException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import net.abi.abisEngine.components.DirectionalLight;
import net.abi.abisEngine.components.Light;
import net.abi.abisEngine.components.PointLight;
import net.abi.abisEngine.components.SpotLight;
import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.file.PathType;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.math.matrix.Matrix4f;
import net.abi.abisEngine.math.vector.Vector2f;
import net.abi.abisEngine.math.vector.Vector3f;
import net.abi.abisEngine.math.vector.Vector4f;
import net.abi.abisEngine.rendering.material.Material;
import net.abi.abisEngine.rendering.pipeline.RenderingEngine;
import net.abi.abisEngine.rendering.shader.compiler.AEShaderCompiler;
import net.abi.abisEngine.rendering.shader.compiler.parser.AEShaderParserYAML;
import net.abi.abisEngine.util.exceptions.AERuntimeException;

public class AEShader {

	public static final PathHandle DEFAULT_SHADER_ASSET_DIRECTORY_PATH = new PathHandle("res/shaders/",
			PathType.Internal);
	public static Map<Long, HashMap<String, AEShaderResource>> loadedShaders = new HashMap<Long, HashMap<String, AEShaderResource>>();
	private AEShaderResource resource;

	public AEShader(AEShaderResource r) {
		this.resource = r;
	}

	public AEShader(String aeShaderFilename) {
		PathHandle p = null;
		try {
			p = AEShader.DEFAULT_SHADER_ASSET_DIRECTORY_PATH.resolveChild("wireframe.ae-shader");
		} catch (InvalidPathException e) {
			System.out.println("Invalid path specified or file was not found. Path: " + aeShaderFilename);
			e.printStackTrace();
		}
		if (p == null) {
			throw new AERuntimeException("Path invalid. Path: " + aeShaderFilename);
		}
		this.resource = AEShaderCompiler.compile(AEShaderParserYAML.parse(p), p);
	}

	public void use() {
		GL20.glUseProgram(resource.getProgram());
	}

	public void updateUniforms(Transform transform, Material mat, RenderingEngine engine) {
		Matrix4f T_VP = engine.getActiveCamera().getViewProjection();
		
//		Matrix4f MVMatrix = transform.getTransformation(), // Model view matrix, aka world matrix
//				MVPMatrix = engine.getMainCamera().getViewProjection().mul(MVMatrix), // Model View Projection Matrix
//				MVNMatrix = MVMatrix, // Model View Normal Matrix
//				T_PM, // Projection Matrix
//				T_VPM; // ViewPort Matrix
//		MVNMatrix.transpose().invertGeneric();
//		int[] ar = new int[4];
//		GL45.glGetIntegerv(GL45.GL_VIEWPORT, ar);
//		T_VPM = new Matrix4f();
//		T_VPM.set(0, 0, ar[0]);
//		T_VPM.set(1, 0, ar[1]);
//		T_VPM.set(2, 0, ar[2]);
//		T_VPM.set(3, 0, ar[3]);
//		for (int i = 0; i < resource.getUniforms().size(); i++) {
//			/*
//			 * These Were Added In The Same Order And Time So They Should Have The Same
//			 * Index.
//			 */
//			String uniformName = resource.getUniforms().get(i).name;
//			int uniformType = resource.getUniforms().get(i).getAttribute(GL45.GL_TYPE);
//
//			if (uniformName.equals("normal_mapping_enabled")) {
//				setUniform1i(uniformName, 1);
//			}
//
//			if (uniformType == GL45.GL_SAMPLER_2D) {
//				int samplerSlot = engine.getSamplerSlot(uniformName);
//				Texture tex = mat.getTexture(uniformName);
//				if (tex != null) {
//					tex.bind(samplerSlot);
//				}
//				setUniform1i(uniformName, samplerSlot);
//			} else if (uniformName.startsWith("T_")) {
//				if (uniformName.equals("T_MVP")) {
//					setUniformMatrix4f(uniformName, true, MVPMatrix);
//					// logger.finest("Added '" + uniformName + "' as MVP Matrix.");
//				} else if (uniformName.equals("T_model")) {
//					setUniformMatrix4f(uniformName, true, MVMatrix);
//					// logger.finest("Added '" + uniformName + "' as World Matrix.");
//				} else if (uniformName.equals("T_MVN")) {
//					setUniformMatrix4f(uniformName, true, MVNMatrix);
//				} else if (uniformName.equals("T_VPM")) {
//					setUniformMatrix4f(uniformName, true, T_VPM);
//				} else {
//					System.out.println("'" + uniformName
//							+ "' is not a valid component of transform. Or is misspelled, please check shader program or change the prefix of the variable."
//							+ "'" + uniformName + "' is not a valid component of transform.");
//					// CoreEngine.exit(1);
//				}
//			} else if (uniformName.startsWith("R_")) {
//				String unprefixedUniformName = uniformName.substring(2);
//				if (uniformType == GL45.GL_FLOAT_VEC3) {
//					setUniform3f(uniformName, engine.getVector3f(unprefixedUniformName));
//				} else if (uniformType.equals("float")) {
//					setUniformf(uniformName, engine.getFloat(unprefixedUniformName));
//				} else if (uniformType.equals("DirectionalLight")) {
//					setUniformDirectionalLight(uniformName, (DirectionalLight) engine.getActiveLight());
//				} else if (uniformType.equals("PointLight")) {
//					setUniformPointLight(uniformName, (PointLight) engine.getActiveLight());
//				} else if (uniformType.equals("SpotLight")) {
//					setUniformSpotLight(uniformName, (SpotLight) engine.getActiveLight());
//				} else {
//					engine.updateUniformStruct(transform, mat, this, unprefixedUniformName, uniformType);
//				}
//			} else if (uniformName.startsWith("C_")) {
//				if (uniformName.equals("C_eyePos")) {
//					setUniform3f(uniformName, engine.getMainCamera().getTransform().getTransformedPosition());
//				} else {
//					logger.error("'" + uniformName
//							+ "' is not a valid component of Camera. Or is misspelled, please check shader program or change the prefix of the variable.",
//							new IllegalArgumentException("'" + uniformName + "' is not a valid component of Camera."));
//				}
//			} else {
//
//				if (uniformType.equals("vec3")) {
//					setUniform3f(uniformName, mat.getVector3f(uniformName));
//				} else if (uniformType.equals("float")) {
//					setUniformf(uniformName, mat.getFloat(uniformName));
//				}
//			}
//		}
	}

	public AEShaderResource getProgramResource() {
		return resource;
	}

	public void setUniformLight(String uniformName, Light baseLight) {
		setUniform3f(uniformName + ".color", baseLight.getColor());
		setUniform1f(uniformName + ".intensity", baseLight.getIntensity());
	}

	public void setUniformDirectionalLight(String uniformName, DirectionalLight directionalLight) {
		setUniformLight(uniformName + ".base", (Light) directionalLight);
		setUniform3f(uniformName + ".direction", directionalLight.getDirection());
	}

	public void setUniformPointLight(String uniformName, PointLight pointLight) {
		setUniformLight(uniformName + ".base", pointLight);
		setUniform1f(uniformName + ".atten.constant", pointLight.getAttenuation().getConstant());
		setUniform1f(uniformName + ".atten.linear", pointLight.getAttenuation().getLinear());
		setUniform1f(uniformName + ".atten.exponent", pointLight.getAttenuation().getExponent());
		setUniform3f(uniformName + ".position", pointLight.getTransform().getPosition());
		setUniform1f(uniformName + ".range", pointLight.getRange());
	}

	public void setUniformSpotLight(String uniformName, SpotLight spotLight) {
		setUniformPointLight(uniformName + ".pointLight", (PointLight) spotLight);
		setUniform3f(uniformName + ".direction", spotLight.getDirection());
		setUniform1f(uniformName + ".cutoff", spotLight.getCutoff());
	}

	public void setUniform2f(String n, Vector2f v) {
		setUniform2f(n, v.x(), v.y());
	}

	public void setUniform3f(String n, Vector3f v) {
		setUniform3f(n, v.x(), v.y(), v.z());
	}

	public void setUniform4f(String n, Vector4f v) {
		setUniform4f(n, v.x(), v.y(), v.z(), v.w());
	}

	public void setUniformMatrix4f(String n, boolean transpose, Matrix4f v) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(16);
			for (int i = 0; i < 4; i++)
				for (int j = 0; j < 4; j++)
					buffer.put(v.get(i, j));
			buffer.flip();
			setUniformMatrix4fv(n, transpose, buffer);
		}
	}

	public void setUniform1d(String n, double v) {
		glUniform1d(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1dv(String n, double[] v) {
		glUniform1dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1dv(String n, DoubleBuffer v) {
		glUniform1dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1f(String n, float v) {
		glUniform1f(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1fv(String n, float[] v) {
		glUniform1fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1fv(String n, FloatBuffer v) {
		glUniform1fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1i(String n, int v) {
		glUniform1i(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1iv(String n, int[] v) {
		glUniform1iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1iv(String n, IntBuffer v) {
		glUniform1iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1ui(String n, int v) {
		glUniform1ui(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1uiv(String n, int[] v) {
		glUniform1uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform1uiv(String n, IntBuffer v) {
		glUniform1uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	/*
	 * 2 component uniforms
	 */

	public void setUniform2d(String n, double v0, double v1) {
		glUniform2d(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1);
	}

	public void setUniform2dv(String n, double[] v0) {
		glUniform2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0);
	}

	public void setUniform2dv(String n, DoubleBuffer v) {
		glUniform2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform2f(String n, float v0, float v1) {
		glUniform2f(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1);
	}

	public void setUniform2fv(String n, float[] v) {
		glUniform2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform2fv(String n, FloatBuffer v) {
		glUniform2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform2i(String n, int v0, int v1) {
		glUniform2i(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1);
	}

	public void setUniform2iv(String n, int[] v) {
		glUniform2iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform2iv(String n, IntBuffer v) {
		glUniform2iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform2ui(String n, int v0, int v1) {
		glUniform2ui(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1);
	}

	public void setUniform2uiv(String n, int[] v) {
		glUniform2uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform2uiv(String n, IntBuffer v) {
		glUniform2uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	/*
	 * 3 component uniform
	 */

	public void setUniform3d(String n, double v0, double v1, double v2) {
		glUniform3d(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2);
	}

	public void setUniform3dv(String n, double[] v0) {
		glUniform3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0);
	}

	public void setUniform3dv(String n, DoubleBuffer v) {
		glUniform3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform3f(String n, float v0, float v1, float v2) {
		glUniform3f(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2);
	}

	public void setUniform3fv(String n, float[] v) {
		glUniform3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform3fv(String n, FloatBuffer v) {
		glUniform3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform3i(String n, int v0, int v1, int v2) {
		glUniform3i(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2);
	}

	public void setUniform3iv(String n, int[] v) {
		glUniform3iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform3iv(String n, IntBuffer v) {
		glUniform3iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform3ui(String n, int v0, int v1, int v2) {
		glUniform3ui(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2);
	}

	public void setUniform3uiv(String n, int[] v) {
		glUniform3uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform3uiv(String n, IntBuffer v) {
		glUniform3uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	/*
	 * 4 component uniform
	 */

	public void setUniform4d(String n, double v0, double v1, double v2, double v3) {
		glUniform4d(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2, v3);
	}

	public void setUniform4dv(String n, double[] v0) {
		glUniform4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0);
	}

	public void setUniform4dv(String n, DoubleBuffer v) {
		glUniform4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform4f(String n, float v0, float v1, float v2, float v3) {
		glUniform4f(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2, v3);
	}

	public void setUniform4fv(String n, float[] v) {
		glUniform4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform4fv(String n, FloatBuffer v) {
		glUniform4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform4i(String n, int v0, int v1, int v2, int v3) {
		glUniform4i(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2, v3);
	}

	public void setUniform4iv(String n, int[] v) {
		glUniform4iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform4iv(String n, IntBuffer v) {
		glUniform4iv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform4ui(String n, int v0, int v1, int v2, int v3) {
		glUniform4ui(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v0, v1, v2, v3);
	}

	public void setUniform4uiv(String n, int[] v) {
		glUniform4uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniform4uiv(String n, IntBuffer v) {
		glUniform4uiv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), v);
	}

	public void setUniformMatrix2dv(String n, boolean transpose, double[] v) {
		glUniformMatrix2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2fv(String n, boolean transpose, float[] v) {
		glUniformMatrix2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x3dv(String n, boolean transpose, double[] v) {
		glUniformMatrix2x3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x3dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix2x3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x3fv(String n, boolean transpose, float[] v) {
		glUniformMatrix2x3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x3fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix2x3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x4dv(String n, boolean transpose, double[] v) {
		glUniformMatrix2x4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x4dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix2x4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x4fv(String n, boolean transpose, float[] v) {
		glUniformMatrix2x4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix2x4fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix2x4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	// mat3

	public void setUniformMatrix3dv(String n, boolean transpose, double[] v) {
		glUniformMatrix3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3fv(String n, boolean transpose, float[] v) {
		glUniformMatrix3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x3dv(String n, boolean transpose, double[] v) {
		glUniformMatrix3x2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x2dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix3x2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x2fv(String n, boolean transpose, float[] v) {
		glUniformMatrix3x2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x2fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix3x2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x4dv(String n, boolean transpose, double[] v) {
		glUniformMatrix3x4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x4dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix3x4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x4fv(String n, boolean transpose, float[] v) {
		glUniformMatrix3x4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix3x4fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix3x4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	// mat4

	public void setUniformMatrix4dv(String n, boolean transpose, double[] v) {
		glUniformMatrix4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix4dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4fv(String n, boolean transpose, float[] v) {
		glUniformMatrix4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix4fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x2dv(String n, boolean transpose, double[] v) {
		glUniformMatrix4x2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x2dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix4x2dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x2fv(String n, boolean transpose, float[] v) {
		glUniformMatrix4x2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x2fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix4x2fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x3dv(String n, boolean transpose, double[] v) {
		glUniformMatrix4x3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x3dv(String n, boolean transpose, DoubleBuffer v) {
		glUniformMatrix4x3dv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x3fv(String n, boolean transpose, float[] v) {
		glUniformMatrix4x3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

	public void setUniformMatrix4x3fv(String n, boolean transpose, FloatBuffer v) {
		glUniformMatrix4x3fv(resource.getUniforms().get(n).getAttribute(GL_LOCATION), transpose, v);
	}

}
