package es.pacofraggle.fasebonus.salon.model;

import es.pacofraggle.fasebonus.salon.TestsHelper;
import es.pacofraggle.fasebonus.salon.vo.Badges;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameTest {

  private Game game;
  private Player player;

  @Before
  public void setUp() {
    TestsHelper.cleanTables();

    player = Player.add("player", new Badges(1, 2, 3, 4, 1));
    game = Game.add("name", player, "category", "system");
  }

  @After
  public void tearDown() {
    TestsHelper.cleanTables();
  }

  @Test
  public void testGet() {
    assertEquals("name", game.getName());
    assertEquals(player, game.getChooser());
    assertTrue(player == game.getChooser());
    assertEquals("category", game.getCategory());
    assertEquals("system", game.getSystem());
  }

  @Test
  public void testAddParticipation() {
    Participation p = new Participation(null, game, null, "5", new Badges(2, 2, 2, 2, 1));
    game.addParticipation(p);

    Participation[] list = game.getParticipations();
    assertEquals(1, list.length);

    Game g2 = new Game("name2", player, "category2", "system2");
    g2.addParticipation(p);

    list =  g2.getParticipations();
    assertEquals(0, list.length);
  }

  @Test
  public void testFindAll() {
    Game.add("name2", player, "category2", "system2");
    Game.add("name3", player, "category3", "system3");

    Game[] list = Game.findAll();
    assertEquals(3, list.length);
  }

  @Test
  public void testFindAllParams() {
    Game g2 = Game.add("name2", player, "category2", "system2");
    Game g3 = Game.add("name", player, "category2", "system3");

    Game[] found = Game.findAll("name", "category", null);
    assertEquals(1, found.length);
    assertTrue(found[0] == this.game);

    found = Game.findAll("name", null, null);
    assertEquals(2, found.length);
    assertTrue(found[0] == this.game || found[0] == g3);
    assertTrue(found[1] == this.game || found[1] == g3);

    found = Game.findAll(null, "category2", null);
    assertEquals(2, found.length);
    assertTrue(found[0] == g2 || found[0] == g3);
    assertTrue(found[1] == g2 || found[1] == g3);

    found = Game.findAll(null, null, "system3");
    assertEquals(1, found.length);
    assertTrue(found[0] == g3);

    found = Game.findAll(null, null, "system4");
    assertEquals(0, found.length);
  }

  @Test
  public void testFind() {
    Game g2 = Game.add("name2", player, "category2", "system2");
    Game.add("name3", player, "category3", "system3");

    Game found = Game.find("name2", "system2");
    assertTrue(found == g2);
    assertEquals(found, g2);

    found = Game.find("name2", "system3");
    assertNull(found);
  }

  @Test
  public void testClear() {
    Game.clear();
    Game[] list = Game.findAll();
    assertEquals(0, list.length);
  }

  @Test
  public void testAddPlayer() {
    Game[] list = Game.findAll();
    assertEquals(1, list.length);
    Game g = list[0];

    assertEquals("name", g.getName());
    assertEquals(player, g.getChooser());
    assertTrue(player == g.getChooser());
    assertEquals("category", g.getCategory());
    assertEquals("system", g.getSystem());

    Game.add("name", player, "category", "system");
    list = Game.findAll();
    assertEquals(1, list.length);
  }

  @Test
  public void testAddString() {
    Game.add("name", "player", "category", "system");
    Game[] list = Game.findAll();
    assertEquals(1, list.length);

    Game.add("name", "player2", "category2", "system");
    list = Game.findAll();
    assertEquals(1, list.length);

    Game.add("name", "player2", "category2", "system2");
    list = Game.findAll();
    assertEquals(2, list.length);
  }

  @Test
  public void testSumBadges() {
    game.addParticipation(new Participation(TestsHelper.createPlayer("0"), game, TestsHelper.createEvent("0"), "5", new Badges(2, 2, 2, 2, 1)));
    game.addParticipation(new Participation(TestsHelper.createPlayer("1"), game, TestsHelper.createEvent("0"), "6", new Badges(2, 3, 1, 5, 2)));
    game.addParticipation(new Participation(TestsHelper.createPlayer("2"), game, TestsHelper.createEvent("2"), "7", new Badges(1, 2, 8, 2, 3)));

    Badges badges = game.sumBadges(null);
    TestsHelper.assertBadges(badges, 5, 7, 11, 9, 2);
  }
}
