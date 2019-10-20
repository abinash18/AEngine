package com.base.engine.rendering.meshLoading;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

import com.base.engine.handlers.logging.LogManager;
import com.base.engine.handlers.logging.Logger;
import com.sun.corba.se.impl.ior.ByteBuffer;

public class TestAOI {
	private static final Logger l = LogManager.getLogger(TestAOI.class.getName());

	public TestAOI(File assimpFile) {
		AIScene modelScene = Assimp.aiImportFile(assimpFile.getAbsolutePath(),
				Assimp.aiProcessPreset_TargetRealtime_MaxQuality);
		if (modelScene == null)
			throw new NullPointerException("The loaded file did not contain a transformable mesh");
		final String modelFileName = assimpFile.getName();
		long sTime = System.nanoTime();
		int numMeshes = modelScene.mNumMeshes();
		PointerBuffer aiMeshes = modelScene.mMeshes();
		AIMesh[] meshes = new AIMesh[numMeshes];
		for (int i = 0; i < numMeshes; i++) {
			meshes[i] = AIMesh.create(aiMeshes.get(i));
		}
		ArrayList<VAO3D> meshList = new ArrayList<>();
		int numMaterials = modelScene.mNumMaterials();
		PointerBuffer mats = modelScene.mMaterials();
		AIMaterial[] materials = new AIMaterial[numMaterials];
		for (int i = 0; i < numMaterials; ++i) {
			materials[i] = AIMaterial.create(mats.get(i));
		}
		ArrayList<AITexture> texList = new ArrayList<>();
		PointerBuffer textures = modelScene.mTextures();
		if (textures != null) {
			l.trace("Texture {} has {} textures", modelFileName, textures.capacity());
			for (int i = 0; i < textures.capacity(); i++) {
				texList.add(AITexture.create(textures.get(i)));
			}
		} else {
			l.warn("Model {} has no textures!", modelFileName);
		}
		l.info("model has {} meshes, {} meshes found", modelScene.mNumMeshes(), meshes.length);
		ArrayList<GraphicsThread> waiters = new ArrayList<>();
		for (int i = 0; i < meshes.length; i++) {
			l.info("mesh {}", i);
			waiters.add(initMesh(meshes[i], materials, texList)); // Add the processing thread back into list.
																	// Allows for other meshes to be processed without
																	// waiting for graphics thread.
		}
		for (GraphicsThread gt : waiters) {
			meshList.add((VAO3D) gt.waitForCompletion()); // wait for graphics trheads to complete
		}
		vaos = meshList.toArray(new VAO3D[] {});
		long tTime = System.nanoTime() - sTime;
		l.info("Took {} nanos to load {} ({} seconds)", tTime, modelFileName, tTime / 1e9);
	}

	private GraphicsThread initMesh(AIMesh mesh, AIMaterial[] materials, ArrayList<AITexture> texList) {
		// ------ VERTICES ------
		ByteBuffer vertexArrayBufferData = BufferUtils.createByteBuffer(mesh.mNumVertices() * 4 * Float.BYTES);
		AIVector3D.Buffer vertices = mesh.mVertices();

		ByteBuffer normalArrayBufferData = BufferUtils.createByteBuffer(mesh.mNumVertices() * 3 * Float.BYTES);
		AIVector3D.Buffer normals = mesh.mNormals();

		ByteBuffer texArrayBufferData = BufferUtils.createByteBuffer(mesh.mNumVertices() * 2 * Float.BYTES);

		l.trace("Mesh {} has {} vertices", new String(mesh.mName().dataString()), mesh.mNumVertices());
		for (int i = 0; i < mesh.mNumVertices(); ++i) {
			AIVector3D vert = vertices.get(i);
			vertexArrayBufferData.putFloat(vert.x());
			vertexArrayBufferData.putFloat(vert.y());
			vertexArrayBufferData.putFloat(vert.z());
			vertexArrayBufferData.putFloat(1f);

			AIVector3D norm = normals.get(i);
			normalArrayBufferData.putFloat(norm.x());
			normalArrayBufferData.putFloat(norm.y());
			normalArrayBufferData.putFloat(norm.z());

			if (mesh.mNumUVComponents().get(0) != 0) {
				AIVector3D texture = mesh.mTextureCoords(0).get(i);
				texArrayBufferData.putFloat(texture.x()).putFloat(texture.y());
			} else {
				texArrayBufferData.putFloat(0).putFloat(0);
			}
		}

		vertexArrayBufferData.flip();
		normalArrayBufferData.flip();
		texArrayBufferData.flip();

		int faceCount = mesh.mNumFaces();
		int elementCount = faceCount * 3;
		IntBuffer elementArrayBufferData = BufferUtils.createIntBuffer(elementCount);
		AIFace.Buffer facesBuffer = mesh.mFaces();
		for (int i = 0; i < faceCount; ++i) {
			AIFace face = facesBuffer.get(i);
			if (face.mNumIndices() != 3) {
				throw new IllegalStateException("AIFace.mNumIndices() != 3");
			}
			elementArrayBufferData.put(face.mIndices());
		}
		elementArrayBufferData.flip();

		AIMaterial mat = materials[mesh.mMaterialIndex()];
		int numProps = mat.mNumProperties();
		AIMaterialProperty[] mprops = new AIMaterialProperty[numProps];
		for (int i = 0; i < numProps; ++i) {
			mprops[i] = AIMaterialProperty.create(mat.mProperties().get(i));
		}

		// ------ ELEMENTS ------*/
		GraphicsThread d = new GraphicsThread() {
			@Override
			public void function() {
				VAO3D vao = new VAO3D();
				vao.vao = GLRenderer.genVAO();
				glBindVertexArray(vao.vao);
				vao.vbos = new int[4];

				int vab = GLRenderer.createVBO(vertexArrayBufferData.asFloatBuffer());
				GLRenderer.addVBO(vao.vao, vab, 4, 0);
				vao.vbos[0] = vab;
				vao.vertices = vertexArrayBufferData.capacity() / 4;

				int normalArrayBuffer = GLRenderer.createVBO(normalArrayBufferData.asFloatBuffer());
				GLRenderer.addVBO(vao.vao, normalArrayBuffer, 1, 3);
				vao.vbos[1] = normalArrayBuffer;

				int texArrayBuffer = GLRenderer.createVBO(texArrayBufferData.asFloatBuffer());
				GLRenderer.addVBO(vao.vao, texArrayBuffer, 2, 2);
				vao.vbos[2] = texArrayBuffer;

				int elementArrayBuffer = GLRenderer.createEBO(elementArrayBufferData);
				AssimpModelWrapper.this.ibo = elementArrayBuffer;
				vao.vbos[3] = elementArrayBuffer;

				setReturn(vao);
			}
		};
		GraphicsProvider.addNeedsGraphicsThread(d);
		return d;
	}
}
