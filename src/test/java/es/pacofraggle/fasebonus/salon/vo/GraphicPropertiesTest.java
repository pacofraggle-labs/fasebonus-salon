package es.pacofraggle.fasebonus.salon.vo;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GraphicPropertiesTest {
  
  GraphicProperties prop;
  
  @Before
  public void setUp() {
    prop = new GraphicProperties(createMap());
  }

  public Map<String, String> createMap() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("x", "1");
    map.put("y", "2");
    map.put("box-x", "3");
    map.put("box-y", "4");
    map.put("font-weight", "plain");
    map.put("font-size", "12");
    map.put("font", "Verdana");
    map.put("colour", "blue");
    map.put("type", "text");
    map.put("value", "Text");
    map.put("text-align", "0");

    return map;
  }

  @Test
  public void testNew() {
    assertEquals(1, prop.x);
    assertEquals(2, prop.y);
    assertEquals(3, prop.boxX);
    assertEquals(4, prop.boxY);
    assertEquals(Font.PLAIN, prop.fontWeight);
    assertEquals(12, prop.fontSize);
    assertEquals("Verdana", prop.font);
    assertEquals("text", prop.type);
    assertEquals("Text", prop.value);
    assertEquals(0, prop.textAlign);
    assertEquals(Color.BLUE, prop.colour);
  }
  @Test
  public void testStringToFontNameNotFound() {
    assertEquals("Arial", GraphicProperties.stringToFontName("missing font"));
  }

  @Test
  public void testStringToFontWeight() {
    assertEquals(Font.BOLD, GraphicProperties.stringToFontWeight("Bold"));
    assertEquals(Font.ITALIC, GraphicProperties.stringToFontWeight("ITALIC"));
    assertEquals(Font.PLAIN, GraphicProperties.stringToFontWeight("anything else"));
  }

  @Test
  public void testStringToColor() {
    assertEquals(Color.WHITE, GraphicProperties.stringToColor("White"));
    assertEquals(Color.RED, GraphicProperties.stringToColor("RED"));
    assertEquals(Color.YELLOW, GraphicProperties.stringToColor("yellow"));
    assertEquals(Color.BLACK, GraphicProperties.stringToColor("BlacK"));
  }

  @Test
  public void testStringToTextAlign() {
    assertEquals(GraphicProperties.TEXT_ALIGN_LEFT, GraphicProperties.stringToTextAlign(""));
    assertEquals(GraphicProperties.TEXT_ALIGN_LEFT, GraphicProperties.stringToTextAlign(null));
    assertEquals(GraphicProperties.TEXT_ALIGN_LEFT, GraphicProperties.stringToTextAlign(" LEFT "));
    assertEquals(GraphicProperties.TEXT_ALIGN_RIGHT, GraphicProperties.stringToTextAlign("    righT"));
  }
}
