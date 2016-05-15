package es.pacofraggle.fasebonus.salon.model;

import es.pacofraggle.fasebonus.salon.vo.Badges;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BadgePlayerComparatorTest {

  private Set<Player> set;

  private void fill() {
    set.add(new Player("p1", new Badges(1, 2, 3, 4, 5)));
    set.add(new Player("p2", new Badges(5, 4, 3, 2, 1)));
    set.add(new Player("p3", new Badges(3, 3, 3, 3, 3)));
    set.add(new Player("p4", new Badges(1, 1, 3, 3, 2)));
    set.add(new Player("p5", new Badges(3, 3, 3, 3, 3)));
  }

  private void assertOrder(String field, String[] expectedOrder) throws Exception{
    set = new TreeSet<Player>(new BadgePlayerComparator(field));
    fill();

    int i=0;
    for(Player p : set) {
      String expectedName = expectedOrder[i++];
      assertEquals(expectedName, p.getName());
    }
  }

  @Test
  public void testCompareParticipaciones() throws Exception {
    assertOrder("participaciones", new String[]{"p1", "p3", "p5", "p4", "p2"});
  }

  @Test
  public void testCompareMorado() throws Exception{
    assertOrder("morado", new String[]{"p2", "p3", "p5", "p1", "p4"});
  }

  @Test
  public void testCompareAmarillo() throws Exception{
    assertOrder("amarillo", new String[]{"p2", "p3", "p5", "p1", "p4"});
  }

  @Test
  public void testCompareAzul() throws Exception{
    assertOrder("azul", new String[]{"p1", "p2", "p3", "p4", "p5"});
  }

  @Test
  public void testCompareNaranja() throws Exception{
    assertOrder("naranja", new String[]{"p1", "p3", "p4", "p5", "p2"});
  }

  @Test(expected = NoSuchMethodException.class)
  public void testCompareError() throws Exception {
    assertOrder("missingmethod", new String[]{"p1", "p3", "p4", "p5", "p2"});
  }
}
