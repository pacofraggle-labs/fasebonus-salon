package es.pacofraggle.fasebonus.salon;

import es.pacofraggle.fasebonus.salon.vo.GraphicProperties;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

public class ImageWriter implements ImageObserver {

  public void processImage(String filename, List actions, GraphicProperties defaultProp, String output) {
    final BufferedImage image;
    try {
      image = ImageIO.read(new File(filename));
      Graphics g = image.getGraphics();
      Iterator it = actions.iterator();
      while(it.hasNext()) {
        Map data = (Map) it.next();
        String key = (String) data.keySet().iterator().next();
        apply(g, defaultProp, (Map) data.get(key));
      }
      g.dispose();

      ImageIO.write(image, "png", new File(output));

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void apply(Graphics g, GraphicProperties defaultProp, Map data) {
    //System.out.println(data);
    GraphicProperties p = new GraphicProperties(data, defaultProp);
    //System.out.println(p);
    Color prev = g.getColor();
    g.setColor(p.colour);
    if ("text".equals(p.type)) {
      applyText(g, p.x, p.y, p.value, p.font, p.fontWeight, p.fontSize, p.boxX, p.numberFormat, p.textAlign);
    } else if ("image".equals(p.type)) {
      applyImage(g, p.x, p.y, p.boxX, p.boxY, p.value);
    } else if ("rectangle".equals(p.type)) {
      applyRectangle(g, p.x, p.y, p.boxX, p.boxY);
    }
    g.setColor(prev);
  }

  public void applyRectangle(Graphics g, int x, int y, int boxX, int boxY) {
    g.fillRect(x, boxY, boxX-x, y-boxY);
  }

  public void applyImage(Graphics g, int x, int y, int boxX, int boxY, String file) {
    try {
      BufferedImage image2 = ImageIO.read(new File(file));

      g.drawImage(image2, x, boxY, boxX-x, y-boxY, this);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void applyText(Graphics g, int x, int y, String value, String fontName, int fontWeight, int fontSize, int chars, String format, int textAlign) {
    if (!("".equals(format)) && !(format == null)) {
      DecimalFormatSymbols symbols = new DecimalFormatSymbols();
      symbols.setGroupingSeparator('.');
      symbols.setDecimalSeparator(',');
      NumberFormat formatter = new DecimalFormat(format, symbols);
      value = formatter.format(Integer.parseInt(value));
    }
    if (chars > 0) {
      value = value.length() < chars ? value : value.substring(0, chars-1)+".";
      if (textAlign == GraphicProperties.TEXT_ALIGN_RIGHT) {
        StringBuilder sb = new StringBuilder(value);
        while(sb.length() < chars) {
          sb.insert(0, " ");
        }
        value = sb.toString();
      }
    }

    Font font = new Font(fontName, fontWeight, fontSize);
    g.setFont(font);

    g.drawString(value, x, y);
  }

  public boolean imageUpdate(Image image, int i, int i1, int i2, int i3, int i4) {
    return false;
  }
}
