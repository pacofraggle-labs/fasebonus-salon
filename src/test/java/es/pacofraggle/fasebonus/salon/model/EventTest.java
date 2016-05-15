package es.pacofraggle.fasebonus.salon.model;

import es.pacofraggle.fasebonus.salon.TestsHelper;
import es.pacofraggle.fasebonus.salon.vo.Badges;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class EventTest {

  private Event event;

  @Before
  public void setUp() {
    TestsHelper.cleanTables();

    event = Event.add("name");
  }

  @After
  public void tearDown() {
    TestsHelper.cleanTables();
  }

  @Test
  public void testGetName() {
    assertEquals("name", event.getName());
  }

  @Test
  public void testAddParticipation() {
    Participation p = new Participation(null, null, event, "5", new Badges(2, 2, 2, 2, 1));
    event.addParticipation(p);

    Participation[] list = event.getParticipations();
    assertEquals(1, list.length);

    Event e2 = new Event("name2");
    e2.addParticipation(p);

    list =  e2.getParticipations();
    assertEquals(0, list.length);
  }

  @Test
  public void testFindAll() {
    Event.add("name2");
    Event.add("name3");
    Event[] list = Event.findAll();
    assertEquals(3, list.length);
  }

  @Test
  public void testFind() {
    Event e2 = Event.add("name2");
    Event.add("name3");

    Event found = Event.find("name2");
    assertTrue(found == e2);

    found = Event.find("name4");
    assertNull(found);
  }

  @Test
  public void testClear() {
    Event.clear();

    assertEquals(0, Event.findAll().length);
  }

  @Test
  public void testAdd() {
    Event[] list = Event.findAll();
    assertEquals(1, list.length);
    Event e = list[0];

    assertEquals("name", e.getName());

    Event.add("name");
    list = Event.findAll();
    assertEquals(1, list.length);

  }
}
