package es.pacofraggle.fasebonus.salon;

import es.pacofraggle.commons.GoogleSheetsFacade;
import es.pacofraggle.commons.Msgs;
import es.pacofraggle.commons.YAMLReader;
import es.pacofraggle.fasebonus.salon.model.Event;
import es.pacofraggle.fasebonus.salon.model.Game;
import es.pacofraggle.fasebonus.salon.model.Participation;
import es.pacofraggle.fasebonus.salon.model.Player;
import es.pacofraggle.fasebonus.salon.vo.Badges;
import es.pacofraggle.fasebonus.salon.vo.GraphicProperties;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class App {
  private GoogleSheetsFacade google;
  private CSVReader csv;
  private YAMLReader yaml;
  private Properties properties;
  private ImageWriter img;

  public static Msgs log = new Msgs(Msgs.DEBUG, true);

  private static String[] imageExtensions = new String[]{"", ".jpg", ".jpeg", ".png", ".gif"};

  public App() {
    google = new GoogleSheetsFacade();
    csv = new CSVReader();
    yaml = new YAMLReader();
    img = new ImageWriter();


    try {
      readProperties();

      String outputFolder ="tmp";
      File f = new File(outputFolder);
      if (!f.exists()) {
        f.mkdir();
      }

      String suffix = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
      String filename = "salon-"+suffix+".csv";

      App.log.info("Reading Google Drive into " + filename);
      filename = /*"salon-20160515_0822.csv";*/ google.getSpreadsheet((String) properties.get("googlesheet"), outputFolder, filename);

      App.log.info("Loading " + filename + " into the database");
      csv.read(outputFolder+"/"+filename);

      Player[] players = Player.findAll();
      for(Player p : players) {
        playerScore(p, false, "player-score-mario.yaml", outputFolder);
      }

      //Player player = Player.find("Pacofraggle");
      //System.out.println(player);
      //System.out.println(player.sumBadges());
      //playerScore(player, false, "player-score-tron.yaml", outputFolder);

      Event event = Event.find("21");
      App.log.debug(event);
      Game[] list = Game.findAll(event);
      for(Game game : list) {
        App.log.debug("  " + game.getName() + " " + game.getSystem() + " " + game.getCategory());
        eventGameScore(game, event, "ranking.yaml", outputFolder);
      }

      suffix = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
      String[] badges = new String[]{ "participaciones", "morado", "amarillo", "azul", "naranja" };
      for(String badge : badges) {
        filename = "top10-"+badge+"-"+suffix+".png";
        statisticsImage(badge, "Salón Recreativo FaseBonus Top Ten "+badge+" - "+suffix, outputFolder, filename);
      }
    } catch (Exception e) {
      App.log.error(e);
    }

  }

  private String statisticsImage(String badge, String title, String outputFolder, String filename) {
    String output;

    Player[] players = Player.orderByBadge(badge);

    int rows = players.length > 10 ? 10 : players.length;
    String[] group = new String[rows];
    int[] value = new int[rows];
    for(int i = 0; i< rows; i++) {
      value[i] = players[i].getBadges().get(badge);
      group[i] = App.findImage(properties.get("avatar-folder")+File.separator+players[i].getAvatarName()).getAbsolutePath();
    }

    StatsWriter stats = new StatsWriter(title, outputFolder, filename);
    output = stats.top(group, value);
    return output;
  }

  public void readProperties() throws IOException {
    properties = new Properties();
    InputStream fis = null;
    try {
      fis = new FileInputStream("config.properties");
      properties.load(fis);
      String prop = (String) properties.get("avatar-folder");
      properties.put("avatar-folder",
        prop == null ? "images" + File.separator + "avatar" : prop.replaceAll("/", File.separator).replaceAll("\\\\", File.separator));
      prop = (String) properties.get("games-folder");
      properties.put("games-folder",
        prop == null ? "images"+File.separator+"games" : prop.replaceAll("/", File.separator).replaceAll("\\\\", File.separator));

      App.log.debug(properties);
    } finally {
      try {
        if (fis != null) {
          fis.close();
        }
      } catch(Exception ex) {}
    }
  }

  public String playerScore(Player player, boolean calculateBadges, String yml, String outputFolder) {
    App.log.info("playerScore for "+player);
    Badges badges = calculateBadges ? player.sumBadges() : player.getBadges();

    if (badges.isEmpty()) {
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
      if ((outputSuffix == null) || ("".equals(outputSuffix))) {
        outputSuffix = "-Bar";
      }
      output = outputFolder+"/"+player.getName().toLowerCase()+outputSuffix+".png";

      Map<String, String> data = new HashMap<String, String>();
      data.put("name", player.getName().toUpperCase());
      data.put("morado", (badges.getMorado() < 10 ? " " : "")+Integer.toString(badges.getMorado()));
      data.put("amarillo", (badges.getAmarillo() < 10 ? " " : "")+Integer.toString(badges.getAmarillo()));
      data.put("azul", (badges.getAzul() < 10 ? " " : "")+Integer.toString(badges.getAzul()));
      data.put("naranja", (badges.getNaranja() < 10 ? " " : "")+Integer.toString(badges.getNaranja()));
      data.put("participaciones", (badges.getParticipaciones() < 10 ? " " : "")+Integer.toString(badges.getParticipaciones()));

      String source = (String) template.get("template");
      List elements = (List) template.get("element");
      GraphicProperties defaultProp = new GraphicProperties(template);
      for(Object element : elements) {
        Map elm = (Map) element;
        String key = (String) elm.keySet().iterator().next();
        if (data.get(key) != null) {
          Map values = (Map) elm.get(key);

          values.put("type", "text");
          values.put("value", data.get(key));
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
      if ((outputSuffix == null) || ("".equals(outputSuffix))) {
        outputSuffix = "-ranking";
      }
      output = outputFolder+"/"+game.getAvatarName()+outputSuffix+".jpg";

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

      String source = (String) template.get("template");
      List elements = (List) template.get("element");
      GraphicProperties defaultProp = new GraphicProperties(template);
      for(Object element : elements) {
        Map elm = (Map) element;
        String key = (String) elm.keySet().iterator().next();
        Map data = (Map) elm.get(key);
        if ("banner".equals(key)) {
          data.put("value", App.findImage(properties.get("games-folder")+File.separator+game.getAvatarName()));
          data.put("type", "image");
        } else if (key.startsWith("avatar")) {
          int idx = Integer.parseInt(key.replaceFirst("avatar", ""))-1;
          if (idx < ranking.length) {
            Participation p = ranking[idx];
            data.put("type", "image");
            data.put("value", App.findImage(properties.get("avatar-folder")+File.separator+p.getPlayer().getAvatarName()));
          } else {
            data.put("type", "discard");
          }
        } else if (key.startsWith("name")) {
          int idx = Integer.parseInt(key.replaceFirst("name", ""))-1;
          if (idx < ranking.length) {
            Participation p = ranking[idx];
            data.put("type", "text");
            data.put("value", p.getPlayer().getName().toUpperCase());
          } else {
            data.put("type", "discard");
          }
        } else if (key.startsWith("score")) {
          int idx = Integer.parseInt(key.replaceFirst("score", ""))-1;
          if (idx < ranking.length) {
            Participation p = ranking[idx];
            data.put("type", "text");
            data.put("value", p.getRecord());
          } else {
            data.put("type", "discard");
          }
        } else if (key.startsWith("morado")) {
          int idx = Integer.parseInt(key.replaceFirst("morado", ""))-1;
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

  private static File findImage(String filename) {
    File image = new File(filename);
    for(String ext : imageExtensions) {
      File f = new File(filename+ext);
      if (f.exists()) {
        image = f;
        break;
      }
    }

    return image;
  }

  public static void main( String[] args ) {
    App app = new App();
  }
}
