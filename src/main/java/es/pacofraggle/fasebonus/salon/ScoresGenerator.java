package es.pacofraggle.fasebonus.salon;

import es.pacofraggle.commons.DataTypeUtils;
import es.pacofraggle.commons.FileUtils;
import es.pacofraggle.commons.YAMLReader;
import es.pacofraggle.fasebonus.salon.model.Event;
import es.pacofraggle.fasebonus.salon.model.Game;
import es.pacofraggle.fasebonus.salon.model.Participation;
import es.pacofraggle.fasebonus.salon.model.Player;
import es.pacofraggle.fasebonus.salon.vo.Badges;
import es.pacofraggle.fasebonus.salon.vo.GraphicProperties;

import java.io.File;
import java.util.*;

public class ScoresGenerator {

  private YAMLReader yaml;
  private ImageWriter img;
  private Properties properties;

  public ScoresGenerator(Properties properties) {
    yaml = new YAMLReader();
    img = new ImageWriter();

    this.properties = properties;
  }

  public void playersScores(List<Event> ignore, String outputFolder) {
      Player[] players = Player.findAll();
      for(Player p : players) {
        playerScore(p, true, ignore, "player-score-mario-stars.yaml", outputFolder);
      }
  }

  public String playerScore(Player player, boolean calculateBadges, List<Event> ignore, String yml, String outputFolder) {
    Badges badges = calculateBadges ? player.sumBadges(ignore) : player.getBadges();

    if (badges.isEmpty()) {
      App.log.info("playerScore for "+player+" empty");
      return null;
    }

    String output;
    try {
      Map template = yaml.parse(yml);
      //System.out.println(template);

      String docType = (String) template.get("doc-type");
      if (!("marcador".equals(docType))) {
        return null;
      }

      String outputSuffix = (String) template.get("output-suffix");
      if ((outputSuffix == null) || (DataTypeUtils.EMPTY_STRING.equals(outputSuffix))) {
        outputSuffix = "-bar";
      }
      output = outputFolder+ File.separator+player.getName().toLowerCase()+outputSuffix+".png";

      Map<String, String> data = new HashMap<String, String>();
      data.put("name", player.getName().toUpperCase());
      data.put("morado", (badges.getMorado() < 10 ? DataTypeUtils.SPACE : DataTypeUtils.EMPTY_STRING)+Integer.toString(badges.getMorado()));
      data.put("amarillo", (badges.getAmarillo() < 10 ? DataTypeUtils.SPACE : DataTypeUtils.EMPTY_STRING)+Integer.toString(badges.getAmarillo()));
      data.put("azul", (badges.getAzul() < 10 ? DataTypeUtils.SPACE : DataTypeUtils.EMPTY_STRING)+Integer.toString(badges.getAzul()));
      data.put("naranja", (badges.getNaranja() < 10 ? DataTypeUtils.SPACE : DataTypeUtils.EMPTY_STRING)+Integer.toString(badges.getNaranja()));
      data.put("participaciones", (badges.getParticipaciones() < 10 ? DataTypeUtils.SPACE : DataTypeUtils.EMPTY_STRING)+Integer.toString(badges.getParticipaciones()));

      Game[] torneos = Game.findAll("--Torneo 1on1", null, null);
      int stars = 0;
      if (torneos.length > 0) {
        List<Participation> ps = Participation.findAll(player, torneos[0], null);
        for(Participation p : ps) {
          if ("1".equals(p.getRecord())) {
            stars++;
          }
        }
        if (stars > 0) {
          data.put("extra", "x"+Integer.toString(stars));
        } else {
          data.put("extra", null);
        }
      }

      String source = (String) template.get("template");
      List elements = (List) template.get("element");
      GraphicProperties defaultProp = new GraphicProperties(template);
      Object starElm = null;
      for(Object element : elements) {
        Map elm = (Map) element;
        String key = (String) elm.keySet().iterator().next();
        if ("star".equals(key)) {
          starElm = element;
        }
        if (data.get(key) != null) {
          Map values = (Map) elm.get(key);

          values.put("type", "text");
          values.put("value", data.get(key));
        }
      }

      if (stars == 0) {
        elements.remove(starElm);
      }

      //System.out.println(template);

      img.processImage(source, (List) template.get("element"), defaultProp, output);

      if (player.getBadges().equals(badges)) {
        App.log.info("playerScore for "+player);
      } else {
        App.log.info("playerScore for "+player+" => "+badges);
      }
    } catch (Exception e) {
      e.printStackTrace();
      output = null;
    }

    return output;
  }

  public void eventGamesScores(Event event, String outputFolder) {
    // Game rankings
    Game[] list = event.getGames();
    for(Game game : list) {
      if (!game.isSpecial()) {
        App.log.debug("  " + game.getName() + " " + game.getSystem() + " " + game.getCategory());
        eventGameScore(game, event, "ranking.yaml", outputFolder);
      }
    }
  }

  public String eventGameScore(Game game, Event event, String yml, String outputFolder) {
    String output;
    try {
      Map template = yaml.parse(yml);
      //System.out.println(template);

      String docType = (String) template.get("doc-type");
      if (!("ranking".equals(docType))) {
        return null;
      }
      String ignoreStr = (String) template.get("ignore");
      String[] ignore = ignoreStr == null ? new String[0] : ignoreStr.split(",");
      for(int i = 0; i<ignore.length; i++) {
        ignore[i] = ignore[i].trim().toLowerCase();
      }
      Arrays.sort(ignore);

      String outputSuffix = (String) template.get("output-suffix");
      if ((outputSuffix == null) || (DataTypeUtils.EMPTY_STRING.equals(outputSuffix))) {
        outputSuffix = "-ranking";
      }
      output = outputFolder+File.separator+game.getAvatarName()+outputSuffix+".png";

      List<Participation> list = Participation.findAll(null, game, event);
      for(int i=0; i<list.size(); i++) {
        Participation p = list.get(i);
        if (p.getPlayer() != null) {
          String find = p.getPlayer().getName();
          if (Arrays.binarySearch(ignore, find.toLowerCase()) >= 0) {
            list.remove(i);
          }
        }
      }
      Participation[] ranking = Participation.orderByRecord(list);

      int i=1;
      for(Participation p : ranking) {
        System.out.println("    "+i+". "+p.getPlayer().getName()+" "+p.getRecord());
        i++;
      }

      String source = (String) template.get("template");
      List elements = (List) template.get("element");
      GraphicProperties defaultProp = new GraphicProperties(template);
      for(Object element : elements) {
        Map elm = (Map) element;
        String key = (String) elm.keySet().iterator().next();
        Map data = (Map) elm.get(key);
        if ("banner".equals(key)) {
          data.put("value", FileUtils.findImage(properties.get("games-folder") + File.separator + game.getAvatarName()));
          data.put("type", "image");
        } else if (key.startsWith("avatar")) {
          int idx = Integer.parseInt(key.replaceFirst("avatar", DataTypeUtils.EMPTY_STRING))-1;
          if (idx < ranking.length) {
            Participation p = ranking[idx];
            data.put("type", "image");
            data.put("value", FileUtils.findImage(properties.get("avatar-folder")+File.separator+p.getPlayer().getAvatarName()));
          } else {
            data.put("type", "discard");
          }
        } else if (key.startsWith("name")) {
          int idx = Integer.parseInt(key.replaceFirst("name", DataTypeUtils.EMPTY_STRING))-1;
          if (idx < ranking.length) {
            Participation p = ranking[idx];
            data.put("type", "text");
            data.put("value", p.getPlayer().getName().toUpperCase());
          } else {
            data.put("type", "discard");
          }
        } else if (key.startsWith("score")) {
          int idx = Integer.parseInt(key.replaceFirst("score", DataTypeUtils.EMPTY_STRING))-1;
          if (idx < ranking.length) {
            Participation p = ranking[idx];
            data.put("type", "text");
            data.put("value", p.getRecord());
          } else {
            data.put("type", "discard");
          }
        } else if (key.startsWith("morado")) {
          int idx = Integer.parseInt(key.replaceFirst("morado", DataTypeUtils.EMPTY_STRING))-1;
          if (idx < ranking.length) {
            Participation p = ranking[idx];
            data.put("type", p.getBadges().getMorado() > 0 ? "rectangle" : "discard");
          } else {
            data.put("type", "discard");
          }
        }
      }
      //System.out.println(template);

      img.processImage(source, (List) template.get("element"), defaultProp, output);
    } catch (Exception e) {
      e.printStackTrace();
      output = null;
    }

    return output;
  }
}
