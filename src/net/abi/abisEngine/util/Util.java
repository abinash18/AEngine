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
package net.abi.abisEngine.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import net.abi.abisEngine.math.matrix.Matrix4f;
import net.abi.abisEngine.math.vector.Vector2f;
import net.abi.abisEngine.math.vector.Vector3f;
import net.abi.abisEngine.rendering.mesh.Vertex;

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

	/**
	 * Allocates a direct native-ordered {@code ByteBuffer} with the specified
	 * capacity.
	 *
	 * @param capacity the capacity, in bytes
	 *
	 * @return a {@code ByteBuffer}
	 */
	public static ByteBuffer createByteBuffer(int capacity) {
		return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
	}

	public static FloatBuffer createFlippedBuffer(Vertex[] vertices) {
		FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.SIZE);

		for (int i = 0; i < vertices.length; i++) {
			buffer.put(vertices[i].getPos().x());
			buffer.put(vertices[i].getPos().y());
			buffer.put(vertices[i].getPos().z());
			buffer.put(vertices[i].getTexCoord().x());
			buffer.put(vertices[i].getTexCoord().y());
			buffer.put(vertices[i].getNormal().x());
			buffer.put(vertices[i].getNormal().y());
			buffer.put(vertices[i].getNormal().z());
			buffer.put(vertices[i].getTangent().x());
			buffer.put(vertices[i].getTangent().y());
			buffer.put(vertices[i].getTangent().z());
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
		FloatBuffer buffer = createFloatBuffer(values.size() * 3);

		for (int i = 0; i < values.size(); i++) {
			buffer.put(values.get(i).x());
			buffer.put(values.get(i).y());
			buffer.put(values.get(i).z());
		}

		buffer.flip();

		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(Vector3f[][] values) {
		FloatBuffer buffer = createFloatBuffer(values.length * 9);

		for (int i = 0; i < values.length; i++) {
			buffer.put(values[i][0].x());
			buffer.put(values[i][0].y());
			buffer.put(values[i][0].z());
			buffer.put(values[i][1].x());
			buffer.put(values[i][1].y());
			buffer.put(values[i][1].z());
			buffer.put(values[i][2].x());
			buffer.put(values[i][2].y());
			buffer.put(values[i][2].z());
		}

		buffer.flip();

		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(ArrayList<Vector2f> values) {
		FloatBuffer buffer = createFloatBuffer(values.size() * Vertex.SIZE);

		for (int i = 0; i < values.size(); i++) {
			buffer.put(values.get(i).x());
			buffer.put(values.get(i).y());
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
