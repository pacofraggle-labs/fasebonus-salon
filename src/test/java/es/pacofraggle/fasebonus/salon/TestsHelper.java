package es.pacofraggle.fasebonus.salon;

import es.pacofraggle.fasebonus.salon.model.Event;
import es.pacofraggle.fasebonus.salon.model.Game;
import es.pacofraggle.fasebonus.salon.model.Participation;
import es.pacofraggle.fasebonus.salon.model.Player;
import es.pacofraggle.fasebonus.salon.vo.Badges;

import static org.junit.Assert.*;

public class TestsHelper {
  public static void assertBadges(Badges b, int m, int a, int z, int n, int p) {
    assertEquals(m, b.getMorado());
    assertEquals(a, b.getAmarillo());
    assertEquals(z, b.getAzul());
    assertEquals(n, b.getNaranja());
    assertEquals(p, b.getParticipaciones());
  }

  public static Badges createBadges() {
    return new Badges(1, 1, 1, 1, 1);
  }

  public static Player createPlayer(String stamp) {
    return new Player("player"+stamp, createBadges());
  }

  public static Event createEvent(String stamp) {
    return new Event("event"+stamp);
  }

  public static Game createGame(Player player, String stamp) {
    return new Game("game"+stamp, player == null ? createPlayer(stamp) : player, "category"+stamp, "system"+stamp);
  }

  public static Participation createParticipation(String stamp) {
    return createParticipation(createPlayer(stamp), createGame(null, stamp), createEvent(stamp), stamp);
  }

  public static Participation createParticipation(Player player, Game game, Event event, String stamp) {
    return new Participation(player, game, event, "record"+stamp, createBadges());
  }

  public static void cleanTables() {
    Participation.clear();
    Player.clear();
    Game.clear();
    Event.clear();
  }
}
