package jisciple.iso8583.util;

import java.io.FileInputStream;
import java.io.IOException;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class YamlReader {

	public static <T> T readConfig(String filename, Class<T> clazz) throws IOException {

		// Gambi pra ler properties em snake case
		Constructor c = new Constructor(clazz);
		c.setPropertyUtils(new PropertyUtils() {
			@Override
			public Property getProperty(Class<? extends Object> type, String name) {
				name = Util.toCamelCase(name);
				return super.getProperty(type, name);
			}
		});

		Yaml y = new Yaml(c);
		FileInputStream fis = new FileInputStream(filename);
		return y.load(fis);
		
	}
	
}
