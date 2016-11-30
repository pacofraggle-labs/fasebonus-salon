package es.pacofraggle.fasebonus.salon.model;

import es.pacofraggle.commons.DataTypeUtils;
import es.pacofraggle.fasebonus.salon.vo.Badges;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Game {
  private static Set<Game> table = new HashSet<Game>();

  private String name;
  private Player chooser;
  private String category;
  private String system;
  private Set<Participation> participations = new HashSet<Participation>();

  public Game(String name, Player chooser, String category, String system) {
    this.name = name;
    this.chooser = chooser;
    this.category = category;
    this.system = system;
  }

  public String getName() {
    return name;
  }

  public Player getChooser() {
    return chooser;
  }

  public String getCategory() {
    return category;
  }

  public String getSystem() {
    return system;
  }

  public Participation[] getParticipations() {
    return participations.toArray(new Participation[participations.size()]);
  }

  public boolean addParticipation(Participation p) {
    return p.getGame() == this ? this.participations.add(p) : false;
  }

  public String getAvatarName() {
    String[] parts = name.split("\\(");
    String result = parts[0].toLowerCase().replaceAll("\\W", DataTypeUtils.EMPTY_STRING);

    return result.length() > 20 ? result.substring(0, 20) : result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Game game = (Game) o;

    if (category != null ? !category.equals(game.category) : game.category != null) return false;
    if (chooser != null ? !chooser.equals(game.chooser) : game.chooser != null) return false;
    if (name != null ? !name.equals(game.name) : game.name != null) return false;
    if (system != null ? !system.equals(game.system) : game.system != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (chooser != null ? chooser.hashCode() : 0);
    result = 31 * result + (category != null ? category.hashCode() : 0);
    result = 31 * result + (system != null ? system.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Game{" + "name='" + name + '\'' + ", chooser=" + chooser + ", category='" + category + '\'' + ", system='" + system + '\'' + '}';
  }

  public static Game[] findAll() {
    return Game.table.toArray(new Game[Game.table.size()]);
  }

  public static Game[] findAll(String name, String category, String system) {
    List<Game> result = new ArrayList<Game>();
    for(Game g : Game.table) {
      boolean nameCond = name == null || g.getName().equals(name);
      boolean systemCond = system == null || g.getSystem().equals(system);
      boolean categoryCond = category == null || g.getCategory().equals(category);
      //System.out.println("findAll("+name+","+system+","+category+")("+g.getName()+","+g.getSystem()+","+g.getCategory()+") = "+"... "+nameCond+"-"+systemCond+"-"+categoryCond);
      if ((nameCond) && (systemCond) && (categoryCond)) {
        result.add(g);
      }
    }

    return result.toArray(new Game[result.size()]);
  }

  public static Game[] findAll(Event event) {
    List<Participation> list = Participation.findAll(null, null, event);
    Set<Game> result = new HashSet<Game>(list.size());
    for(Participation p : list) {
      result.add(p.getGame());
    }

    for(Game game : event.getGames()) {
      result.add(game);
    }

    return result.toArray(new Game[result.size()]);
  }

  public static Game find(String name, String system) {
    Game[] filtered = Game.findAll(name, null, system);

    return filtered.length == 0 ? null : filtered[0];
  }

  public static void clear() {
    Game.table.clear();
  }

  public static Game add(String name, String chooser, String category, String system) {
    Player p = (chooser == null) || (DataTypeUtils.EMPTY_STRING.equals(chooser.trim())) ? null : Player.add(chooser, new Badges());

    return Game.add(name, p, category, system);
  }

  public static Game add(String name, Player chooser, String category, String system) {
    Game g = Game.find(name, system);
    if (g == null) {
      g = new Game(name, chooser, category, system);
      Game.table.add(g);
    }

    return g;
  }

  public Badges sumBadges(List<Event> ignore) {
    return Participation.sumBadges(this.participations, ignore);
  }
}
