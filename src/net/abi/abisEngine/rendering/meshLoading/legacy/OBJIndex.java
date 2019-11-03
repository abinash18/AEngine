package net.abi.abisEngine.rendering.meshLoading.legacy;

public class OBJIndex {

	public int vertexIndex, texCoordIndex, normalIndex;

	@Override
	public boolean equals(Object other) {
		OBJIndex index = (OBJIndex) other;

		return (vertexIndex == index.vertexIndex && texCoordIndex == index.texCoordIndex
				&& normalIndex == index.normalIndex);
	}

	@Override
	public int hashCode() {

		final int BASE = 17, MULTIPLIER = 31;

		int result = BASE;

		result = MULTIPLIER * result + vertexIndex;
		result = MULTIPLIER * result + texCoordIndex;
		result = MULTIPLIER * result + normalIndex;

		return result;

	}

}
