package es.pacofraggle.fasebonus.salon.model;

import es.pacofraggle.commons.DataTypeUtils;
import es.pacofraggle.fasebonus.salon.vo.Badges;

import java.util.*;

public final class Participation {
  private static Set<Participation> table = new HashSet<Participation>();

  private Player player;
  private Game game;
  private Event event;
  private String record;
  private Badges badges;
  private String date;


  public Participation(Player player, Game game, Event event, String record, Badges badges, String date) {
    this.player = player;
    this.game = game;
    this.event = event;
    this.record = record;
    this.badges = badges;
    this.date = date;
  }

  public Participation(Player player, Game game, Event event, String record, Badges badges) {
    this(player, game, event, record, badges, null);
  }

  public Player getPlayer() {
    return player;
  }

  public Game getGame() {
    return game;
  }

  public Event getEvent() {
    return event;
  }

  public String getRecord() {
    return record;
  }

  public Badges getBadges() {
    return badges;
  }

  public void addBadges(Badges badges) {
    if (this.badges == null) {
      this.badges = badges;
    } else {
      this.badges.addBadges(badges);
    }
  }

  public String getDate() {
    return date;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Participation that = (Participation) o;

    if (event != null ? !event.equals(that.event) : that.event != null) return false;
    if (game != null ? !game.equals(that.game) : that.game != null) return false;
    if (player != null ? !player.equals(that.player) : that.player != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = player != null ? player.hashCode() : 0;
    result = 31 * result + (game != null ? game.hashCode() : 0);
    result = 31 * result + (event != null ? event.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Participation{" +
      "player=" + player +
      ", game=" + game +
      ", event=" + event +
      ", record='" + record + '\'' +
      ", badges=" + badges +
      ", date=" + date +
      '}';
  }

  public static Participation[] findAll() {
    return Participation.table.toArray(new Participation[Participation.table.size()]);
  }

  public static List<Participation> findAll(Player player, Game game, Event event) {
    List<Participation> result = new ArrayList<Participation>();
    for(Participation p : Participation.table) {
      boolean playerCond = player == null || p.getPlayer().equals(player);
      boolean gameCond = game == null || p.getGame().equals(game);
      boolean eventCond = event == null || p.getEvent().equals(event);
      if ((playerCond) && (gameCond) && (eventCond)) {
        result.add(p);
      }
    }

    return result;
  }

  public static Participation find(Player player, Game game, Event event) {
    List<Participation> filtered = Participation.findAll(player, game, event);

    return filtered.size() == 0 ? null : filtered.get(0);
  }

  public static void clear() {
    Participation.table.clear();
  }

  public static Participation add(String player, String game, String chooser, String category, String system, String event, String record, Badges badges) {
    Player p = Player.add(player, null);
    Game g = Game.add(game, chooser, category, system);
    Event e = Event.add(event);
    return Participation.add(p, g, e, record, badges);
  }

  public static Participation add(Player player, Game game, Event event, String record, Badges badges) {
    if ((record == null) || (record.trim().equals("0")) || (record.trim().equals(DataTypeUtils.EMPTY_STRING))) {
      return null;
    }

    Participation p = Participation.find(player, game, event);
    if (p == null) {
      p = new Participation(player, game, event, record, badges);
      Participation.table.add(p);
      player.addParticipation(p);
      game.addParticipation(p);
      event.addParticipation(p);
    }

    return p;
  }

  public static boolean add(Participation p) {
    boolean result;
    Participation prev = Participation.find(p.getPlayer(), p.getGame(), p.getEvent());
    if (prev == null) {
      Participation.table.add(p);
      p.getPlayer().addParticipation(p);
      p.getGame().addParticipation(p);
      p.getEvent().addParticipation(p);

      result = true;
    } else {
      result = false;
    }

    return result;
  }

  public static Badges sumBadges(List<Event> ignore) {
    return Participation.sumBadges(Participation.table, ignore);
  }

  public static Badges sumBadges(Set<Participation> participations, List<Event> ignore) {
    Badges result = new Badges();
    Set<String> events = new HashSet<String>();
    for(Participation participation : participations) {
      if ((ignore == null) || (!ignore.contains(participation.getEvent()))) {
        events.add(participation.getEvent().getName());
        result.addBadges(participation.getBadges());
      }
    }

    result.setParticipaciones(events.size());
    return result;
  }

  public static Participation[] orderByRecord(List<Participation> list) {
    Set<Participation> set;
    try {
      set = new TreeSet<Participation>(new ParticipationRecordComparator());
      for(Participation p : list) {
        set.add(p);
      }
    } catch(Exception e) {
      set = new TreeSet<Participation>();
    }

    return set.toArray(new Participation[set.size()]);
  }
}

class ParticipationRecordComparator implements Comparator<Participation> {
  public int compare(Participation p1, Participation p2) {
    int result;
    try {
      if ((p1.getRecord() == null) && (p2.getRecord() == null)) {
        result = compareUsingPlayerName(p1, p2);
      } else if (p1.getRecord() == null) {
        result = 1;
      } else if (p2.getRecord() == null) {
        result = -1;
      } else {
        int value1 = Integer.parseInt(p1.getRecord());
        int value2 = Integer.parseInt(p2.getRecord());
        if (value1 == value2) {
          result = compareUsingPlayerName(p1, p2);
        } else {
          result = value2 - value1;
        }
      }
    }catch(Exception e) {
       throw new ClassCastException(e.getMessage());
    }

    return result;
  }

  private int compareUsingPlayerName(Participation p1, Participation p2) {
      int value1 = p1.getPlayer().getName() == null ? 0 : p1.getPlayer().getName().hashCode();
      int value2 = p2.getPlayer().getName() == null ? 0 : p2.getPlayer().getName().hashCode();

    return value1 - value2;
  }
}
