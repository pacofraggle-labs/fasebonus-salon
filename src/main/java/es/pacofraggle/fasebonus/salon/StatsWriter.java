package es.pacofraggle.fasebonus.salon;

import es.pacofraggle.fasebonus.salon.vo.GraphicProperties;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class StatsWriter {

  private String title;
  private String file;

  public StatsWriter(String title, String outputFolder, String filename) {
    this.title = title;
    this.file = outputFolder+"/"+filename;
  }

  public String top(String[] group, int[] value) {
    try {
      System.out.println(title);
      int max = -1;
      for(int v : value) {
        if (v > max) {
          max = v;
        }
      }
      BufferedImage image = new BufferedImage(1200, 1600, BufferedImage.TYPE_4BYTE_ABGR);

      Graphics g = image.getGraphics();
      Color darkBlue = new Color(0, 0, 100);
      ImageWriter img = new ImageWriter();
      g.setColor(Color.BLACK);
      img.applyText(g, 20, 40, this.title, "Verdana", Font.BOLD, 30, 0, "", GraphicProperties.TEXT_ALIGN_LEFT);

      for(int i=0, iniY = 80; i<group.length; i++, iniY+=150) {
        img.applyImage(g, 20, iniY+128, 148, iniY, group[i]);
        int pos = Math.round(((float) value[i] / (float) max)*1000.0f);
        System.out.println("  "+group[i]+" "+value[i]+" "+pos);
        g.setColor(darkBlue);
        img.applyRectangle(g, 160, iniY + 90, 160 + pos, iniY + 20);
        g.setColor(Color.BLACK);
        img.applyText(g, 160, iniY+128, Integer.toString(value[i]), "Verdana", Font.BOLD, 18  , 0, "", GraphicProperties.TEXT_ALIGN_LEFT);
      }
      g.dispose();

      ImageIO.write(image, "png", new File(this.file));
    } catch(Exception e) {

    }
    return this.file;
  }
}
