package net.abi.abisEngine.rendering.shaderManagement.compiler.parsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.rendering.shaderManagement.compiler.fileTypes.yaml.AEShaderFileYAML;

public class AEShaderParserYAML {

	public AEShaderFileYAML parse(PathHandle file) {
		Yaml yaml = new Yaml(new Constructor(AEShaderFileYAML.class));
		AEShaderFileYAML parsedFile = null;
		try {
			parsedFile = yaml.load(Files.newInputStream(Paths.get(file.getFileInstance().getAbsolutePath())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parsedFile;
	}

}
