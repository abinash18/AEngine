package net.abi.abisEngine.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import net.abi.abisEngine.math.Matrix4f;
import net.abi.abisEngine.math.Vector2f;
import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.meshLoading.Vertex;

public class Util {
	public static FloatBuffer createFloatBuffer(int size) {
		return BufferUtils.createFloatBuffer(size);
	}

	public static IntBuffer createIntBuffer(int size) {
		return BufferUtils.createIntBuffer(size);
	}

	public static IntBuffer createIntBuffer(List<Integer> val) {
		IntBuffer buffer = createIntBuffer(val.size());

		buffer.put(listIntToArray(val));

		buffer.flip();

		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(Vertex[] vertices) {
		FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.SIZE);

		for (int i = 0; i < vertices.length; i++) {
			buffer.put(vertices[i].getPos().getX());
			buffer.put(vertices[i].getPos().getY());
			buffer.put(vertices[i].getPos().getZ());
			buffer.put(vertices[i].getTexCoord().getX());
			buffer.put(vertices[i].getTexCoord().getY());
			buffer.put(vertices[i].getNormal().getX());
			buffer.put(vertices[i].getNormal().getY());
			buffer.put(vertices[i].getNormal().getZ());
			buffer.put(vertices[i].getTangent().getX());
			buffer.put(vertices[i].getTangent().getY());
			buffer.put(vertices[i].getTangent().getZ());
		}

		buffer.flip();

		return buffer;
	}

	public static IntBuffer createFlippedBuffer(int... values) {
		IntBuffer buffer = createIntBuffer(values.length);

		buffer.put(values);

		buffer.flip();

		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(List<Vector3f> values) {
		FloatBuffer buffer = createFloatBuffer(values.size() * Vertex.SIZE);

		for (int i = 0; i < values.size(); i++) {
			buffer.put(values.get(i).getX());
			buffer.put(values.get(i).getY());
			buffer.put(values.get(i).getZ());
		}

		buffer.flip();

		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(ArrayList<Vector2f> values) {
		FloatBuffer buffer = createFloatBuffer(values.size() * Vertex.SIZE);

		for (int i = 0; i < values.size(); i++) {
			buffer.put(values.get(i).getX());
			buffer.put(values.get(i).getY());
		}

		buffer.flip();

		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(Matrix4f value) {
		FloatBuffer buffer = createFloatBuffer(4 * 4);

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				buffer.put(value.get(i, j));

		buffer.flip();

		return buffer;
	}

	public static String[] removeEmptyStrings(String[] data) {

		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < data.length; i++) {

			if (!data[i].equals("")) {
				result.add(data[i]);
			}
		}
		String[] res = new String[result.size()];
		result.toArray(res);

		return (res);
	}

	public static int[] toIntArray(Integer[] array) {

		int[] result = new int[array.length];

		for (int i = 0; i < array.length; i++) {
			result[i] = array[i].intValue();
		}

		return (result);

	}

	public static int[] listIntToArray(List<Integer> list) {
		int[] result = list.stream().mapToInt((Integer v) -> v).toArray();
		return result;
	}

	public static ByteBuffer createByteBuffer(int size) {
		return BufferUtils.createByteBuffer(size);
	}

	/* https://dzone.com/articles/how-to-write-a-c-like-sizeof-function-in-java */

	/**
	 * Java method to return size of primitive data type based on hard coded values
	 * valid but provided by developer
	 */
	public static int sizeof(Class dataType) {
		if (dataType == null) {
			throw new NullPointerException();
		}
		if (dataType == byte.class || dataType == Byte.class) {
			return 1;
		}
		if (dataType == short.class || dataType == Short.class) {
			return 2;
		}
		if (dataType == char.class || dataType == Character.class) {
			return 2;
		}
		if (dataType == int.class || dataType == Integer.class) {
			return 4;
		}
		if (dataType == long.class || dataType == Long.class) {
			return 8;
		}
		if (dataType == float.class || dataType == Float.class) {
			return 4;
		}
		if (dataType == double.class || dataType == Double.class) {
			return 8;
		}
		return 4; // default for 32-bit memory pointer
	}

	/**
	 * A perfect way of creating confusing method name, sizeof and sizeOf this
	 * method take advantage of SIZE constant from wrapper class
	 */
	public static int sizeOf(Class dataType) {
		if (dataType == null) {
			throw new NullPointerException();
		}
		if (dataType == byte.class || dataType == Byte.class) {
			return Byte.SIZE;
		}
		if (dataType == short.class || dataType == Short.class) {
			return Short.SIZE;
		}
		if (dataType == char.class || dataType == Character.class) {
			return Character.SIZE;
		}
		if (dataType == int.class || dataType == Integer.class) {
			return Integer.SIZE;
		}
		if (dataType == long.class || dataType == Long.class) {
			return Long.SIZE;
		}
		if (dataType == float.class || dataType == Float.class) {
			return Float.SIZE;
		}
		if (dataType == double.class || dataType == Double.class) {
			return Double.SIZE;
		}
		return 4; // default for 32-bit memory pointer
	}

	public static Vertex[] toVertexArray(ArrayList<Vector3f> positions, ArrayList<Vector3f> normals,
			ArrayList<Vector2f> texCoords, ArrayList<Vector3f> tangents) {

		Vertex[] verts = new Vertex[positions.size()];

		for (int i = 0; i < verts.length; i++) {
			Vertex vertex = new Vertex(positions.get(i), texCoords.get(i), normals.get(i), tangents.get(i));
			verts[i] = vertex;
		}

		return verts;
	}

}
