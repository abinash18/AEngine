package net.abi.abisEngine.rendering.shader;

public class GLSLUniform {
	public String name = "";
	/** The raw glsl type integer */
	public int type = 0;
	/**
	 * The location this uniform is bound in on the shader program if -1 then not
	 * bound.
	 */
	public int location = -1;

	/**
	 * The size of this uniform variable if in an array the array stride property
	 * will be non 0, and if this variable is a Matrix then the Matrix Stride will
	 * be non 0.s
	 */
	public int size = 0;

	/**
	 * If the current uniform is a Mat then this will be its stride otherwise it is
	 * 0 by default.
	 */
	public int matrixStride = 0;

	/**
	 * If the current uniform is a Array then this will be its stride otherwise it
	 * is 0.
	 */
	public int arrayStride = 0;

	public GLSLUniform() {
	}

}
