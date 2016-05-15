package es.pacofraggle.fasebonus.salon;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class YAMLReader {

  public Map parse(String filename) throws FileNotFoundException {
    InputStream fis = null;
    Map data = null;
    try {
      fis = new FileInputStream(new File(filename));
      Yaml yaml = new Yaml();
      data = (Map) yaml.load(fis);
    } finally {
      try {
        if (fis != null) {
          fis.close();
        }
      } catch(Exception ex) {}
    }

    return data;
  }
}
