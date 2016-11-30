package es.pacofraggle.commons;

import java.io.File;

public class FileUtils {
  private static String[] imageExtensions = new String[]{DataTypeUtils.EMPTY_STRING, ".jpg", ".jpeg", ".png", ".gif"};

  public static File findImage(String filename) {
    File image = new File(filename);
    for(String ext : imageExtensions) {
      File f = new File(filename+ext);
      if (f.exists()) {
        image = f;
        break;
      }
    }

    return image;
  }

  public static void safeMkdir(String folder){
      File f = new File(folder);
      if (!f.exists()) {
        f.mkdir();
      }
  }
}
