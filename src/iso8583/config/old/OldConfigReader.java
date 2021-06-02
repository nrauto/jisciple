package iso8583.config.old;

import java.io.FileInputStream;
import java.io.IOException;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import iso8583.Util;

public class OldConfigReader {

	public static OldIsoConfig readIsoConfig(String filename) throws IOException {

		// Gambi pra ler properties em snake case
		Constructor c = new Constructor(OldIsoConfigStructure.class);
		c.setPropertyUtils(new PropertyUtils() {
			@Override
			public Property getProperty(Class<? extends Object> type, String name) {
				name = Util.toCamelCase(name);
				return super.getProperty(type, name);
			}
		});

		Yaml y = new Yaml(c);
		FileInputStream fis = new FileInputStream(filename);
		OldIsoConfigStructure structure = y.load(fis);
		
		return structure.getIso();
	}
	
}
