package com.base.engine.rendering.meshLoading.legacy;

import com.base.engine.math.Vector2f;
import com.base.engine.math.Vector3f;

public class Vertex {
	public static final int SIZE = 11;

	private Vector3f pos, normal, tangent;
	private Vector2f texCoord;

	public Vertex(Vector3f pos) {
		this(pos, new Vector2f(0, 0));
	}

	public Vertex(Vector3f pos, Vector2f texCoord) {
		this(pos, texCoord, new Vector3f(0, 0, 0));
	}

	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal) {
		this(pos, texCoord, normal, new Vector3f(0, 0, 0));
	}

	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal, Vector3f tangent) {
		this.pos = pos;
		this.texCoord = texCoord;
		this.normal = normal;
		this.tangent = tangent;
	}

	public Vector2f getTexCoord() {
		return texCoord;
	}

	public void setTexCoord(Vector2f texCoord) {
		this.texCoord = texCoord;
	}

	public static int getSize() {
		return SIZE;
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}

	public Vector3f getTangent() {
		return tangent;
	}

	public void setTangent(Vector3f tangent) {
		this.tangent = tangent;
	}

}
