package net.abi.abisEngine.util;

import java.util.HashMap;
import java.util.Map;

import net.abi.abisEngine.math.Vector3f;

public abstract class MappedValues {
	private Map<String, Float> floatBinds;
	private Map<String, Vector3f> vec3fBinds;

	public MappedValues() {
		this.floatBinds = new HashMap<>();
		this.vec3fBinds = new HashMap<>();
	}

	public void addFloat(String name, float value) {
		floatBinds.put(name, value);
	}

	public void addVector3f(String name, Vector3f value) {
		vec3fBinds.put(name, value);
	}

	public float getFloat(String name) {
		Float result = floatBinds.get(name);
		if (result != null) {
			return result;
		}

		return 0;
	}

	public Vector3f getVector3f(String name) {
		Vector3f result = vec3fBinds.get(name);
		if (result != null) {
			return result;
		}

		return new Vector3f(0, 0, 0);
	}
}
