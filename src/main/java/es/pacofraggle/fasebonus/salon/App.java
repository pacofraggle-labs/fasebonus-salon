package es.pacofraggle.fasebonus.salon;

import es.pacofraggle.commons.*;
import es.pacofraggle.fasebonus.salon.model.Event;
import es.pacofraggle.fasebonus.salon.model.Game;
import es.pacofraggle.fasebonus.salon.model.Participation;
import es.pacofraggle.fasebonus.salon.model.Player;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class App {
  private GoogleSheetsFacade google;
  private YAMLReader yaml;
  private Properties properties;
  private ImageWriter img;

  public static Msgs log = new Msgs(Msgs.DEBUG, true);


  public App() {
    google = new GoogleSheetsFacade();
    CSVDataLoader csvr = new CSVDataLoader();
    CSVReporting csvw = new CSVReporting();
    yaml = new YAMLReader();
    img = new ImageWriter();


    try {
      readProperties();
      ScoresGenerator gen = new ScoresGenerator(properties);

      String outputFolder ="tmp";
      FileUtils.safeMkdir(outputFolder);

      String suffix = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
      String filename = "salon-"+suffix+".csv";

      App.log.info("Reading Google Drive into " + filename);
      filename = google.getSpreadsheet((String) properties.get("googlesheet"), (String) properties.get("historico-gid"), outputFolder, filename);
      App.log.info("Loading " + filename + " into the database");
      csvr.readHistorico(outputFolder + File.separator + filename);

      String currenEvent = (String) properties.get("current-edition");

      Event event = Event.find(currenEvent);
      App.log.debug(event);

      List<Event> ignore = new ArrayList<Event>(1);
      //ignore.add(event);

      // Players
      gen.playersScores(ignore, outputFolder);

      // Game rankings
      gen.eventGamesScores(event, outputFolder);

      suffix = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

      GraphReporting stats = new GraphReporting();
      stats.top10All(suffix, outputFolder);


      String[] badges = new String[]{ "morado" };
      csvw.eventBadgesReport(badges, outputFolder, "event_badges-" + suffix + ".csv");
      csvw.gameBadgesReport(badges, outputFolder, "game_badges-" + suffix + ".csv");

      badges = new String[]{ "morado", "amarillo", "azul", "naranja" };
      csvw.playerBadgesReport(badges, outputFolder, "player_badges-" + suffix + ".csv");

      filename = "salon-"+suffix+"-puntuaciones.csv";
      filename = google.getSpreadsheet((String) properties.get("googlesheet"), (String) properties.get("puntuaciones-gid"), outputFolder, filename);
      App.log.info("Loading " + filename + " individual scores");
      List<Participation> scores = csvr.readPuntuaciones(outputFolder + File.separator + filename, event);
      stats.eventProgress(event, scores, (String) properties.get("current-edition-from"), (String) properties.get("current-edition-to"), suffix, outputFolder);

      csvw.gamesRankingReports(Game.findAll(), "ranking.yaml", outputFolder);

    } catch (Exception e) {
      App.log.error(e);
    }

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

  public static void main( String[] args ) {
    new App();
  }
}
