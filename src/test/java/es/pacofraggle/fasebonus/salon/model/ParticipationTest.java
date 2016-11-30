package es.pacofraggle.fasebonus.salon.model;

import es.pacofraggle.fasebonus.salon.TestsHelper;
import es.pacofraggle.fasebonus.salon.vo.Badges;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ParticipationTest {

  private Player player;
  private Game game;
  private Event event;
  private Participation participation;

  @Before
  public void setUp()  {
    TestsHelper.cleanTables();

    player = TestsHelper.createPlayer("1");
    game = TestsHelper.createGame(null, "1");
    event = TestsHelper.createEvent("1");

    participation = TestsHelper.createParticipation(player, game, event, "1");
    Participation.add(participation);
  }

  @After
  public void tearDown()  {
    TestsHelper.cleanTables();
  }

  @Test
  public void testGet()  {
    assertTrue(player == participation.getPlayer());
    assertTrue(game == participation.getGame());
    assertTrue(event == participation.getEvent());
    assertNotNull(participation.getBadges());
  }

  @Test
  public void testAddBadges()  {
    Badges original = (Badges) participation.getBadges().clone();
    participation.addBadges(TestsHelper.createBadges());

    TestsHelper.assertBadges(participation.getBadges(),
      original.getMorado()+1,
      original.getAmarillo()+1,
      original.getAzul()+1,
      original.getNaranja()+1,
      original.getParticipaciones()+1);
  }

  @Test
  public void testFindAllString()  {
    Participation.add("player2", "game2", "chooser2", "category2", "system2", "event2", "record2", TestsHelper.createBadges());
    Participation.add(player.getName(), game.getName(), game.getChooser().getName(), game.getCategory(), game.getSystem(), event.getName(), "record2", TestsHelper.createBadges());

    Game[] list = Game.findAll();
    assertEquals(2, list.length);
  }

  @Test
  public void testFindAllObjects()  {
    Participation.add(player, game, event, "record2", TestsHelper.createBadges());
    Participation.add(TestsHelper.createPlayer("3"), TestsHelper.createGame(null, "3"), TestsHelper.createEvent("3"), "record3", TestsHelper.createBadges());

    assertEquals(2, Participation.findAll().length);
  }

  @Test
  public void testFind()  {
    Participation.add(TestsHelper.createPlayer("2"), TestsHelper.createGame(null, "2"), TestsHelper.createEvent("2"), "record2", TestsHelper.createBadges());
    Participation.add(TestsHelper.createPlayer("3"), TestsHelper.createGame(null, "3"), TestsHelper.createEvent("3"), "record3", TestsHelper.createBadges());

    Participation found = Participation.find(player, game, event);
    assertTrue(found == participation);
    assertEquals(found, participation);

    found = Participation.find(player, game, TestsHelper.createEvent("4"));
    assertNull(found);
  }

  @Test
  public void testClear()  {
    Participation.clear();
    Participation[] list = Participation.findAll();
    assertEquals(0, list.length);
  }

  @Test
  public void testAdd()  {
    Participation.add(TestsHelper.createPlayer("2"), TestsHelper.createGame(null, "2"), TestsHelper.createEvent("2"), "record2", TestsHelper.createBadges());
    Participation.add(player, game, event, "record3", TestsHelper.createBadges());

    assertEquals(2, Participation.findAll().length);
  }

  @Test
  public void testSumBadges() {
    Participation.add(TestsHelper.createPlayer("2"), TestsHelper.createGame(null, "2"), TestsHelper.createEvent("2"), "record2", TestsHelper.createBadges());
    Participation.add(TestsHelper.createPlayer("3"), TestsHelper.createGame(null, "3"), TestsHelper.createEvent("3"), "record3", TestsHelper.createBadges());

    int m=0, a=0, z=0, n=0, p = 0;
    for(Participation part : Participation.findAll()) {
      m += part.getBadges().getMorado();
      a += part.getBadges().getAmarillo();
      z += part.getBadges().getAzul();
      n += part.getBadges().getNaranja();
      p += part.getBadges().getParticipaciones();
    }
    Badges badges = Participation.sumBadges(null);
    TestsHelper.assertBadges(badges, m, a, z, n, p);
  }

  @Test
  public void testOrderByRecord() {
    Participation.clear();
    Participation.add(TestsHelper.createPlayer("1"), TestsHelper.createGame(null, "2"), TestsHelper.createEvent("2"), "100", TestsHelper.createBadges());
    Participation.add(TestsHelper.createPlayer("2"), TestsHelper.createGame(null, "2"), TestsHelper.createEvent("2"), "200", TestsHelper.createBadges());
    Participation.add(TestsHelper.createPlayer("3"), TestsHelper.createGame(null, "2"), TestsHelper.createEvent("2"), "300", TestsHelper.createBadges());
    Participation.add(TestsHelper.createPlayer("4"), TestsHelper.createGame(null, "2"), TestsHelper.createEvent("2"), "100", TestsHelper.createBadges());

    List<Participation> list = Participation.findAll(null, null, null);
    Participation[] result = Participation.orderByRecord(list);

    int i=0;
    String[] expectedOrder = new String[]{"player3", "player2", "player1", "player4"};
    for(Participation p : result) {
      assertEquals(expectedOrder[i], p.getPlayer().getName());
      i++;
    }
  }
}
