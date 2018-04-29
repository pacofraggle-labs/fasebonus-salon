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

      App.log.info("Reading Google Drive Totals into " + filename);
      filename = google.getSpreadsheet((String) properties.get("googlesheet"), (String) properties.get("historico-gid"), outputFolder, filename);
      App.log.info("-----");
      App.log.info("Loading " + filename + " into the database");
      csvr.readHistorico(outputFolder + File.separator + filename);
      App.log.info("-----");

      String currenEvent = (String) properties.get("current-edition");

      Event event = Event.find(currenEvent);
      App.log.debug("Current event: "+event);
      gen.checkRequiredFiles(event);
      App.log.info("-----");

      List<Event> ignore = new ArrayList<Event>(1);
      //ignore.add(event);

      // Players
      App.log.info("Generating player scores:");
      gen.playersScores(ignore, outputFolder, (String) properties.get("player-bar"));
      App.log.info("-----");

      // Game rankings
      App.log.info("Generating event "+ event.getName()+" game rankings:");
      gen.eventGamesScores(event, outputFolder);
      App.log.info("-----");

      suffix = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

      App.log.info("Generating statistics:");
      GraphReporting stats = new GraphReporting();
      App.log.info("  Top 10 graphs");
      stats.top10All(suffix, outputFolder);


      String[] badges = new String[]{ "morado" };
      String eventBadgesFile = "event_badges-" + suffix + ".csv";
      App.log.info("  Event ended game badges: " + eventBadgesFile);
      csvw.eventBadgesReport(badges, outputFolder, eventBadgesFile);
      String gameBadgesFile = "game_badges-" + suffix + ".csv";
      App.log.info("  Ended game badges "+gameBadgesFile);
      csvw.gameBadgesReport(badges, outputFolder, gameBadgesFile);

      badges = new String[]{ "morado", "amarillo", "azul", "naranja" };
      String playerBadgesFile = "player_badges-" + suffix + ".csv";
      App.log.info("  Player badges "+playerBadgesFile);
      csvw.playerBadgesReport(badges, outputFolder, playerBadgesFile);

      App.log.info("-----");

      filename = "salon-"+suffix+"-puntuaciones.csv";
      filename = google.getSpreadsheet((String) properties.get("googlesheet"), (String) properties.get("puntuaciones-gid"), outputFolder, filename);
      App.log.info("Loading " + filename + " individual scores");
      List<Participation> scores = csvr.readPuntuaciones(outputFolder + File.separator + filename, event);
      App.log.info("Generating progress graphs:");
      stats.eventProgress(event, scores, (String) properties.get("current-edition-from"), (String) properties.get("current-edition-to"), suffix, outputFolder);
      App.log.info("-----");

      App.log.info("Generating Game rankings:");
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

      App.log.debug("Config: "+properties);
      App.log.debug("-----");
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
