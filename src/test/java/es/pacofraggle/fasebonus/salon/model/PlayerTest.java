package es.pacofraggle.fasebonus.salon.model;

import es.pacofraggle.fasebonus.salon.TestsHelper;
import es.pacofraggle.fasebonus.salon.vo.Badges;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest {

  private Player player;
  private Badges badges = new Badges(1, 2, 3, 4, 3);

  @Before
  public void setUp() {
    TestsHelper.cleanTables();

    player = Player.add("name", badges);
  }

  @After
  public void tearDown() {
    TestsHelper.cleanTables();
  }

  @Test
  public void testAddBadges() {
    player.addBadges("MN");
    TestsHelper.assertBadges(player.getBadges(), 2, 2, 3, 5, 4);
  }

  @Test
  public void testAddParticipation() {
    Participation p = new Participation(player, null, null, "5", new Badges(2, 2, 2, 2, 1));
    player.addParticipation(p);

    Participation[] list = player.getParticipations();
    assertEquals(1, list.length);

    Player p2 = new Player("name2", badges);
    p2.addParticipation(p);

    list = p2.getParticipations();
    assertEquals(0, list.length);
  }

  @Test
  public void testFindAll() {
    Player.add("name2", badges);
    Player.add("name3", badges);
    Player[] list = Player.findAll();
    assertEquals(3, list.length);
  }

  @Test
  public void testFind() {
    Player.add("name2", badges);
    Player p3 = Player.add("name3", badges);

    Player found = Player.find("name3");
    assertTrue(found == p3);

    found = Player.find("name4");
    assertNull(found);
  }

  @Test
  public void testClear() {
    Player.clear();
    Player[] list = Player.findAll();
    assertEquals(0, list.length);
  }

  @Test
  public void testAdd() {
    Player[] list = Player.findAll();
    assertEquals(1, list.length);
    Player p = list[0];

    assertEquals("name", p.getName());
    assertTrue(p.getBadges() == badges);

    Player.add("name", badges);
    list = Player.findAll();
    assertEquals(1, list.length);
  }

  @Test
  public void testSumBadges() {
    player.addParticipation(new Participation(player, TestsHelper.createGame(player, "0"), TestsHelper.createEvent("0"), "5", new Badges(2, 2, 2, 2, 1)));
    player.addParticipation(new Participation(player, TestsHelper.createGame(player, "1"), TestsHelper.createEvent("1"), "6", new Badges(2, 3, 1, 5, 2)));
    player.addParticipation(new Participation(player, TestsHelper.createGame(player, "2"), TestsHelper.createEvent("2"), "7", new Badges(1, 2, 8, 2, 3)));

    Badges badges = player.sumBadges(null);
    TestsHelper.assertBadges(badges, 5, 7, 11, 9, 3);
  }
}
