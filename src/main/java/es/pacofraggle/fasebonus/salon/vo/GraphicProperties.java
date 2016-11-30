package es.pacofraggle.fasebonus.salon.vo;

import es.pacofraggle.commons.DataTypeUtils;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

public class GraphicProperties {

  public int x = -1;
  public int y = -1;
  public String type = DataTypeUtils.EMPTY_STRING;
  public Color colour = null;

  public String value = DataTypeUtils.EMPTY_STRING;
  public String numberFormat = DataTypeUtils.EMPTY_STRING;
  public int fontWeight = -1;
  public int fontSize = -1;
  public String font = DataTypeUtils.EMPTY_STRING;

  public int boxX = -1;
  public int boxY = -1;

  public int textAlign = 0;

  private static String[] availableFonts = null;

  public static final int TEXT_ALIGN_LEFT = 0;
  public static final int TEXT_ALIGN_RIGHT = 1;

  private GraphicProperties defaultProp;

  public GraphicProperties() {
  }

  public GraphicProperties(GraphicProperties defaultProp) {
    this.defaultProp = defaultProp;
  }

  public GraphicProperties(Map data) {
    this(data, null);
  }

  public GraphicProperties(Map data, GraphicProperties defaultProp) {
    this(defaultProp);

    copy(defaultProp);

    type = GraphicProperties.safeGet(data, "type", "text").toLowerCase();
    x = GraphicProperties.safeGetInt(data, "x", 0);
    y = GraphicProperties.safeGetInt(data, "y", 0);
    value = GraphicProperties.safeGet(data, "value", DataTypeUtils.EMPTY_STRING);

    numberFormat = GraphicProperties.safeGet(data, "number-format", DataTypeUtils.EMPTY_STRING);

    boxX = GraphicProperties.safeGetInt(data, "box-x", 0);
    boxY = GraphicProperties.safeGetInt(data, "box-y", 0);

    textAlign = GraphicProperties.stringToTextAlign(GraphicProperties.safeGet(data, "text-align", DataTypeUtils.EMPTY_STRING));

    if (data.get("font-weight") != null) {
      String weight = GraphicProperties.safeGet(data, "font-weight", "0");
      fontWeight = GraphicProperties.stringToFontWeight(weight);
    }

    if (data.get("font-size") != null) {
      fontSize = GraphicProperties.safeGetInt(data, "font-size", 0);
    }

    if (data.get("font") != null) {
      String fontName = GraphicProperties.safeGet(data, "font", "Arial");
      font = GraphicProperties.stringToFontName(fontName);
    }

    if (data.get("colour") != null) {
      String colourName = GraphicProperties.safeGet(data, "colour", "BLACK");
      colour = GraphicProperties.stringToColor(colourName);
    }
  }

  public static int stringToTextAlign(String descr) {
    int result;
    if (descr == null) {
      result = TEXT_ALIGN_LEFT;
    } else {
      result = "right".equals(descr.trim().toLowerCase()) ? TEXT_ALIGN_RIGHT : TEXT_ALIGN_LEFT;
    }

    return result;
  }

  public static String stringToFontName(String fontName) {
    return Arrays.binarySearch(getAvailableFonts(), fontName) >= 0 ? fontName : "Arial";
  }

  public static int stringToFontWeight(String weight) {
    int style;
    if ("bold".equalsIgnoreCase(weight)) {
      style = Font.BOLD;
    } else if ("italic".equalsIgnoreCase(weight)) {
      style = Font.ITALIC;
    } else {
      style = Font.PLAIN;
    }

    return style;
  }

  public static Color stringToColor(String str) {
      Color color;
      color = Color.getColor(str.toUpperCase());
      if (color == null){
        try {
          Field field = Class.forName("java.awt.Color").getField(str.toUpperCase());
          color = (Color) field.get(null);
        } catch (Exception ex) {
          if ("violet".equals(str)) {
            color = new Color(148, 0, 211);
          }
        }
      }

      return color == null ? Color.BLACK : color;
    }

  public static String[] getAvailableFonts() {
    if (availableFonts == null) {
      availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
      Arrays.sort(availableFonts);
    }

    return availableFonts;
  }

  public void copy(GraphicProperties prop) {
    if (prop != null) {
      this.x = prop.x;
      this.y = prop.y;
      this.type = prop.type;
      this.colour = prop.colour;
      this.value = prop.value;
      this.numberFormat = prop.numberFormat;
      this.font = prop.font;
      this.fontSize = prop.fontSize;
      this.fontWeight = prop.fontWeight;
      this.boxX = prop.boxX;
      this.boxY = prop.boxY;
      this.textAlign = prop.textAlign;
    }
  }

  private static String safeGet(Map map, String key, String base) {
    Object value = map.get(key);

    return value == null ? base : value.toString();
  }

  private static int safeGetInt(Map map, String key, int base) {
    String str = safeGet(map, key, Integer.toString(base));
    int result;
    try {
      result = Integer.parseInt(str);
    } catch(Exception e) {
      result = base;
    }

    return result;
  }

  @Override
  public String toString() {
    return "GraphicProperties{" +
      "x=" + x + ", y=" + y + ", type='" + type + '\'' + ", colour=" + colour +
      ", value='" + value + '\'' + ", fontWeight=" + fontWeight + ", fontSize=" + fontSize + ", font='" + font + '\'' +
      ", number-format=" + numberFormat + ", boxX=" + boxX + ", boxY=" + boxY + ", textAlign=" + textAlign +
      '}';
  }
}
