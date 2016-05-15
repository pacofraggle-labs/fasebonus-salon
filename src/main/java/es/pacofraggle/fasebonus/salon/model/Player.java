package es.pacofraggle.fasebonus.salon.model;

import es.pacofraggle.fasebonus.salon.vo.Badges;

import java.lang.reflect.Method;
import java.util.*;

public final class Player {

  private static Set<Player> table = new HashSet<Player>();

  private String name;
  private Badges badges;
  private Set<Participation> participations = new HashSet<Participation>();

  public Player(String name, Badges badges) {
    this.name = name;
    this.badges = badges;
  }

  public String getName() {
    return this.name;
  }

  public Badges getBadges() {
    return this.badges;
  }

  public Participation[] getParticipations() {
    return participations.toArray(new Participation[participations.size()]);
  }

  public void addBadges(String badges) {
    addBadges(Badges.parseBadges(badges));
  }

  public void addBadges(Badges badges) {
    if (this.badges == null) {
      this.badges = badges;
    } else {
      this.badges.addBadges(badges);
    }
  }

  public boolean addParticipation(Participation p) {
    return p.getPlayer() == this ? this.participations.add(p) : false;
  }

  public String getAvatarName() {
    return name.toLowerCase();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Player player = (Player) o;

    if (name != null ? !name.equals(player.name) : player.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name == null ? 0 : name.hashCode();
  }

  @Override
  public String toString() {
    return "Player{" + "name='" + name + '\'' + ", badges=" + badges + '}';
  }

  public static Player[] findAll() {
    return Player.table.toArray(new Player[Player.table.size()]);
  }

  public static Player find(String name) {
    Player result = null;
    for(Player p : Player.table) {
      if (p.getName().equals(name)) {
        result = p;
        break;
      }
    }

    return result;
  }

  public static void clear() {
    Player.table.clear();
  }

  public static Player add(String name, Badges badges) {
    Player p = Player.find(name);
    if (p == null) {
      p = new Player(name, badges);
      Player.table.add(p);
    }

    return p;
  }

  public Badges sumBadges() {
    return Participation.sumBadges(this.participations);
  }

  public static Player[] orderByBadge(String badge) {
    Set<Player> set;
    try {
      set = new TreeSet<Player>(new BadgePlayerComparator(badge));
      for(Player p : table) {
        set.add(p);
      }
    } catch(Exception e) {
      set = new TreeSet<Player>();
    }

    return set.toArray(new Player[set.size()]);
  }
}

class BadgePlayerComparator implements Comparator<Player> {
  private Method method;

  public BadgePlayerComparator(String badge) throws ClassNotFoundException, NoSuchMethodException {
    String methodName = "get" + badge.substring(0,1).toUpperCase() + badge.substring(1).toLowerCase();
    method = Class.forName("es.pacofraggle.fasebonus.salon.vo.Badges").getMethod(methodName);
  }

  public int compare(Player player1, Player player2) {
    int result;
    try {
      if ((player1.getBadges() == null) && (player2.getBadges() == null)) {
        result = 0;
      } else if (player1.getBadges() == null) {
        result = 1;
      } else if (player2.getBadges() == null) {
        result = -1;
      } else {
        int value1 = (Integer) method.invoke(player1.getBadges());
        int value2 = (Integer) method.invoke(player2.getBadges());
        if (value1 == value2) {
          value1 = player1.getName() == null ? 0 : player1.getName().hashCode();
          value2 = player2.getName() == null ? 0 : player2.getName().hashCode();
          result = value1 - value2;
        } else {
          result = value2 - value1;
        }
      }
    }catch(Exception e) {
       throw new ClassCastException(e.getMessage());
    }

    return result;
  }
}
