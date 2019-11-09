package net.abi.abisEngine.rendering.resourceManagement;

import java.util.HashMap;

public class AssetStore {

	/*
	 * Saves An Unnecessary Allocation Of Resources If The Mesh Has Been Loaded In A
	 * Different Call.
	 */
	private HashMap<String, MeshResource> loadedModels = new HashMap<String, MeshResource>();

}
