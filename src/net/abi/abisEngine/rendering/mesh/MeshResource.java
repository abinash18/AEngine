/**
 * 
 */
package net.abi.abisEngine.rendering.mesh;

import java.util.HashMap;

import net.abi.abisEngine.rendering.asset.AssetI;

/**
 * Contains the reference count to the mesh along with the size of the indices
 * and VAOID and MeshData.
 * 
 * @author abinash
 *
 */
public class MeshResource implements AssetI {
	String name;
	int size, refCount;
	HashMap<String, VertexArrayObject> vaos;
	MeshData meshData;

	boolean initialized = false;

	/**
	 * @param size
	 * @param meshData
	 */
	public MeshResource(String name, MeshData meshData) {
		this.name = name;
		this.size = meshData.model.getIndices().size() - 1;
		this.refCount = 1;
		this.meshData = meshData;
		this.vaos = new HashMap<String, VertexArrayObject>();
	}

	VertexArrayObject addVAO(String name) {
		VertexArrayObject _vao = new VertexArrayObject(name);

		this.vaos.put(name, _vao);

		return _vao;
	}

	VertexArrayObject getVAO(String name) {
		return vaos.get(name);
	}

	public void incRef() {
		refCount++;
	}

	public void decRef() {
		refCount--;
	}

	public int decAndGetRef() {
		refCount--;
		return refCount;
	}

	int getAndDecRef() {
		int _refs = refCount;
		refCount--;
		return _refs;
	}

	public int incAndGetRef() {
		refCount++;
		return refCount;
	}

	int getAndIncRef() {
		int _refs = refCount;
		refCount++;
		return _refs;
	}

	@Override
	public void dispose() {

	}

	@Override
	public int getRefs() {
		return refCount;
	}
}