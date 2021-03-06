package es.pacofraggle.fasebonus.salon.model;

import es.pacofraggle.fasebonus.salon.vo.Badges;

import java.util.*;

public final class Event {

  private static Set<Event> table = new HashSet<Event>();

  private String name;
  private Set<Participation> participations = new HashSet<Participation>();
  private Set<Game> games = new HashSet<Game>();

  public Event(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Participation[] getParticipations() {
    return participations.toArray(new Participation[participations.size()]);
  }

  public boolean addParticipation(Participation p) {
    return p.getEvent() == this ? this.participations.add(p) : false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Event event = (Event) o;

    if (name != null ? !name.equals(event.name) : event.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Event{" + "name='" + name + '\'' + '}';
  }

  public static Event[] findAll() {
    return Event.table.toArray(new Event[Event.table.size()]);
  }

  public static Event find(String name) {
    Event result = null;
    for(Event e : Event.table) {
      if (e.getName().equals(name)) {
        result = e;
        break;
      }
    }

    return result;
  }

  public static void clear() {
    Event.table.clear();
  }

  public static Event add(String name) {
    Event e = Event.find(name);
    if (e == null) {
      e = new Event(name);
      Event.table.add(e);
    }

    return e;
  }

  public Badges sumBadges(List<Event> ignore) {
    return Participation.sumBadges(this.participations, ignore);
  }

  public Player[] getPlayers() {
    Set<Player> result = new HashSet<Player>();
    for(Participation p : participations) {
      result.add(p.getPlayer());
    }

    return result.toArray(new Player[result.size()]);
  }

  public Set<Participation> getParticipations(String player) {
    Set<Participation> result = new HashSet<Participation>();
    for(Participation p : participations) {
      if (player.equals(p.getPlayer().getName())) {
        result.add(p);
      }
    }

    return result;
  }

  public void addGame(Game g) {
    games.add(g);
  }

  public Game[] getGames() {
    return games.toArray(new Game[games.size()]);
  }
}
