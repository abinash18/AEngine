/**
 * 
 */
package net.abi.abisEngine.rendering.gl.memory;

import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;

import net.abi.abisEngine.rendering.shader.compiler.AEGLInfo;
import net.abi.abisEngine.util.Expendable;

/**
 * A wrapper class for the GL buffer in memory.
 * 
 * @author Abinash Singh
 */
public class GLBuffer implements Expendable {

	/**
	 * A engine specific null FLAG, If the ID variable of a {@link GLBuffer
	 * AEGLBuffer} is equal to this than it will be considered as non existent in
	 * the current GL Context.s
	 */
	public static final int NULL_BOUND_BUFFER_OBJECT = -1;

	/**
	 * The ID of the created UBO in the GL Context. This is defaulted to
	 * {@link GLBuffer#NULL_BOUND_BUFFER_OBJECT NULL_BOUND_BUFFER_OBJECT} so that
	 * the system can recognize that it yet exist in the context.
	 */
	private int bufferID = NULL_BOUND_BUFFER_OBJECT;

	/** The size in bytes of the entirety of the buffer. */
	private int size;

	/**
	 * The type of buffer being defined. See {@link GL31#glBindBuffer(int, int)
	 * glBindBuffer(int, int)} for more info.
	 */
	private int type;

	/**
	 * The way that GL should draw this buffer, see
	 * {@link org.lwjgl.opengl.GL31#glBindBuffer(int, int) glBindBuffer(int, int)}
	 * for more info.
	 */
	private int usage;

	/**
	 * Creates a new AEGLBuffer class.
	 * 
	 * @param type  The type of buffer to create.
	 * @param usage The usage of this buffer, so GL can optimize memory allocation.
	 * @param size  The size of the buffer to create in bytes.
	 */
	public GLBuffer(int type, int usage, int size) {
		this.type = type;
		this.usage = usage;
		this.size = size;
	}

	/**
	 * Creates a Buffer in GL with the specified size in bytes, type and usage.
	 * After the function completes execution it will un-bind the buffer to
	 * {@link GLBuffer#NULL_BOUND_BUFFER_OBJECT NULL_BOUND_BUFFER_OBJECT}. NOTE:
	 * Leaves the buffer bound.
	 * 
	 * @return bufferID of the created buffer.
	 */
	public int create() {
		if (bufferID != -1) {
			System.out.println("Warning from AEGLBuffer: " + AEGLInfo.spBufferBinding.get(type)
					+ ": can't overwrite, It already exists and is in GL Context. To recreate use the reCreate() function which destroys and recreates the buffer at a different position.");
			return bufferID;
		}
		genBufferID();
		bind();
		GL31.glBufferData(type, size, usage);
		return bufferID;
	}

	/**
	 * Recreates the current buffer by reallocating. Destroys current buffer see
	 * {@link GLBuffer#}
	 * 
	 * @param preserveData If true, then the data that was present previously in the
	 *                     buffer will be preserved.
	 * @return A new BufferID for the newly generated buffer.
	 */
	public int reCreate(boolean preserveData) {
		ByteBuffer data = null;
		if (preserveData) {
			data = mapBuffer(GL31.GL_READ_ONLY);
		}
		dispose();
		create();
		addData(data);
		return bufferID;
	}

	/**
	 * Generates a empty unallocated GL Buffer object in the GL Context. See
	 * {@link GL31#glGenBuffers()} NOTE: This function returns the current bufferID
	 * if this buffer object is not a {@link #NULL_BOUND_BUFFER_OBJECT}. To recreate
	 * this buffer than use {@link #reCreate()} or destroy this buffer and create it
	 * again. This method should not be used after {@link GLBuffer#create()} has
	 * been called.
	 * 
	 * @return bufferID of the generated buffer.
	 */
	protected int genBufferID() {
		if (bufferID != NULL_BOUND_BUFFER_OBJECT) {
			return bufferID;
		}
		return (bufferID = GL31.glGenBuffers());
	}

	/**
	 * Allocates the buffer with {@link GLBuffer#bufferID} with supplied parameters
	 * in the constructor: {@link GLBuffer#GLBuffer(int, int, int)}. NOTE: Does'nt
	 * bind the buffer it needs to be done manually.
	 * 
	 * @param numBytes
	 */
	protected void allocate() {
		GL31.glBufferData(type, size, usage);
	}

	/**
	 * Sets the buffer object in GPU memory to the one supplied. NOTE: The buffer
	 * must be bound using {@link GLBuffer#bind()} before calling. And Unbound using
	 * {@link GLBuffer#unBind()}.
	 * 
	 * @param buffer The buffer of size {@link GLBuffer#size} containing float
	 *               values representing data in the buffer object.
	 */
	public void addData(FloatBuffer buffer) {
		GL31.glBufferData(type, buffer, usage);
	}

	/**
	 * Sets the buffer object in GPU memory to the one supplied. NOTE: The buffer
	 * must be bound using {@link GLBuffer#bind()} before calling. And Unbound using
	 * {@link GLBuffer#unBind()}.
	 * 
	 * @param buffer The buffer of size {@link GLBuffer#size} containing float
	 *               values representing data in the buffer object.
	 */
	public void addData(ByteBuffer buffer) {
		GL31.glBufferData(type, buffer, usage);
	}

	/**
	 * Binds the buffer to its ID, if the ID is
	 * {@link GLBuffer#NULL_BOUND_BUFFER_OBJECT NULL_BOUND_BUFFER_OBJECT} then it
	 * returns and does'nt execute {@link GL31#glBindBuffer(int, int)
	 * GL31.glBindBuffer(int, int)} to bind this buffer, without throwing a error.
	 */
	public void bind() {
		if (bufferID != NULL_BOUND_BUFFER_OBJECT) {
			return;
		}
		GL31.glBindBuffer(type, bufferID);
	}

	/**
	 * Simply calls {@link GL31#glBindBuffer(int, int) GL31.glBindBuffer(int, int)}
	 * with a 0 as the buffer index.
	 */
	public void unBind() {
		GL31.glBindBuffer(type, 0);
	}

	/**
	 * Maps and returns the buffer in client memory, see
	 * {@link GL31#glMapBuffer(int, int, long, ByteBuffer)}.
	 * 
	 * @param access    The way this buffer should be accessed.
	 * @param length    Length of the buffer in bytes.
	 * @param oldBuffer Fall back buffer.
	 * @return Returns a ByteBuffer representing the buffer object.
	 */
	public ByteBuffer mapBuffer(int access, int length, ByteBuffer oldBuffer) {
		return GL31.glMapBuffer(type, access, length, oldBuffer);
	}

	/**
	 * Maps and returns the buffer in client memory, see
	 * {@link GL31#glMapBuffer(int, int)}.
	 * 
	 * @param access The way this buffer should be accessed.
	 * @return Returns a ByteBuffer representing the buffer object.
	 */
	public ByteBuffer mapBuffer(int access) {
		return GL31.glMapBuffer(type, access);
	}

	/**
	 * Unmaps the mapped buffer and returns boolean if there is an error or not. See
	 * {@link GL31#glUnmapBuffer(int)}. If the buffer type is not one of:<br>
	 * <table>
	 * <tr>
	 * <td>{@link GL30C#GL_TRANSFORM_FEEDBACK_BUFFER TRANSFORM_FEEDBACK_BUFFER}</td>
	 * <td>{@link GL31#GL_UNIFORM_BUFFER UNIFORM_BUFFER}</td>
	 * <td>{@link GL42#GL_ATOMIC_COUNTER_BUFFER ATOMIC_COUNTER_BUFFER}</td>
	 * <td>{@link GL43#GL_SHADER_STORAGE_BUFFER SHADER_STORAGE_BUFFER}</td>
	 * </tr>
	 * </table>
	 * 
	 * the program will return a false.
	 * 
	 * @return false if there is an error. Else true if it was successful.
	 */
	public boolean unMapBuffer() {
		if (type == GL45.GL_TRANSFORM_FEEDBACK_BUFFER || type == GL45.GL_UNIFORM_BUFFER
				|| type == GL45.GL_ATOMIC_COUNTER_BUFFER || type == GL45.GL_SHADER_STORAGE_BUFFER) {
			return GL31.glUnmapBuffer(type);
		}
		return false;
	}

	/**
	 * Disposes of the buffer. And returns it to a NULL state.
	 */
	public void dispose() {
		if (bufferID != NULL_BOUND_BUFFER_OBJECT) {
			this.unMapBuffer();
			this.unBind();
			this.delete();
			this.bufferID = NULL_BOUND_BUFFER_OBJECT;
		}
	}

	/**
	 * Deletes the buffer from GL context. And sets bufferID to
	 * {@link GLBuffer#NULL_BOUND_BUFFER_OBJECT}. This function is not the same as
	 * the Object delete function, it should not be used to destroy this Object, use
	 * {@link GLBuffer#dispose()} instead.
	 */
	protected void delete() {
		if (bufferID != NULL_BOUND_BUFFER_OBJECT) {
			GL31.glDeleteBuffers(bufferID);
		}
	}

	/**
	 * Returns the defined size of this block in number of bytes
	 * 
	 * @return size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the ID of this defined block after it is created. If the block is not
	 * yet created then {@link GLBuffer#NULL_BOUND_BUFFER_OBJECT
	 * NULL_BOUND_BUFFER_OBJECT} is returned by default.
	 * 
	 * @return ID The ID of the buffer or {@link GLBuffer#NULL_BOUND_BUFFER_OBJECT
	 *         NULL_BOUND_BUFFER_OBJECT}
	 */
	public int getBufferID() {
		return bufferID;
	}

	/**
	 * @return the type
	 */
	protected int getType() {
		return type;
	}

	/**
	 * @return the usage
	 */
	protected int getUsage() {
		return usage;
	}

}
