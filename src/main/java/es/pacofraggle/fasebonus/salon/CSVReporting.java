package es.pacofraggle.fasebonus.salon;

import es.pacofraggle.fasebonus.salon.model.Event;
import es.pacofraggle.fasebonus.salon.model.Game;
import es.pacofraggle.fasebonus.salon.model.Participation;
import es.pacofraggle.fasebonus.salon.model.Player;
import es.pacofraggle.fasebonus.salon.vo.Badges;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jfree.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import es.pacofraggle.commons.YAMLReader;

public class CSVReporting {

  public void eventBadgesReport(String[] badges, String outputFolder, String filename) {
    FileWriter file = null;
    CSVPrinter csv = null;
    try {
      file = new FileWriter(outputFolder+"/"+filename);
      csv = new CSVPrinter(file, CSVFormat.RFC4180);

      csv.print("Event");
      csv.print("Num. games");
      for(String b : badges) {
        csv.print(b);
      }
      csv.println();

      for(Event event : Event.findAll()) {
        Badges total = new Badges();

        int games = event.getGames().length;
        Participation[] participations = event.getParticipations();
        for(Participation p : participations) {
          total.addBadges(p.getBadges());
        }

        csv.print(event.getName());
        csv.print(games);
        for(String b : badges) {
          csv.print(total.get(b));
        }
        csv.println();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (file != null) {
        try { file.close(); } catch (IOException e) {}
      }
      if (csv != null) {
        try { csv.close(); } catch (IOException e) {}
      }
    }
  }

  public void gameBadgesReport(String[] badges, String outputFolder, String filename) {
    FileWriter file = null;
    CSVPrinter csv = null;
    try {
      file = new FileWriter(outputFolder+"/"+filename);
      csv = new CSVPrinter(file, CSVFormat.RFC4180);

      csv.print("Game");

      for(String b : badges) {
        csv.print(b);
      }
      csv.println();

      for(Game g : Game.findAll()) {
        Badges bdg = g.sumBadges(null);

        csv.print(g.getName()+" "+g.getSystem());
        for(String b : badges) {
          csv.print(bdg.get(b));
        }
        csv.println();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (file != null) {
        try { file.close(); } catch (IOException e) {}
      }
      if (csv != null) {
        try { csv.close(); } catch (IOException e) {}
      }
    }
  }

  public void gameRankingReport(Game game, String outputFolder, String filename, String[] ignore) {
    FileWriter file = null;
    CSVPrinter csv = null;
    try {
      file = new FileWriter(outputFolder+"/"+filename);
      csv = new CSVPrinter(file, CSVFormat.RFC4180);

      csv.print(game.getName()+" "+game.getSystem());
      csv.println();
      csv.print("Pos");
      csv.print("Punt.");
      csv.print("Jugador");
      csv.print("Salón");
      csv.print("Terminado");
      csv.println();

      Participation[] ranking = Participation.orderByRecord(Arrays.asList(game.getParticipations()));
      for(int i=0, pos=0; i<ranking.length; i++) {
        String find = ranking[i].getPlayer().getName();
        if (Arrays.binarySearch(ignore, find.toLowerCase()) >= 0) {
         continue;
        }
        csv.print(pos+1);
        csv.print(ranking[i].getRecord());
        csv.print(ranking[i].getPlayer().getName());
        csv.print(ranking[i].getEvent().getName());
        csv.print(ranking[i].getBadges().getMorado() > 0 ? "Sí" : "");
        csv.println();
        pos++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (file != null) {
        try { file.close(); } catch (IOException e) {}
      }
      if (csv != null) {
        try { csv.close(); } catch (IOException e) {}
      }
    }
  }

  public void playerBadgesReport(String[] badges, String outputFolder, String filename) {
    FileWriter file = null;
    CSVPrinter csv = null;
    try {
      file = new FileWriter(outputFolder+"/"+filename);
      csv = new CSVPrinter(file, CSVFormat.RFC4180);

      csv.print("Event");
      csv.print("Num. games");
      csv.print("Player");

      for(String b : badges) {
        csv.print(b);
      }
      csv.println();

      for(Event event : Event.findAll()) {
        Badges total = new Badges();

        int games = event.getGames().length;
        Player[] players = event.getPlayers();

        for(Player p : players) {
          Set<Participation> participations = event.getParticipations(p.getName());
          Badges bdg = Participation.sumBadges(participations, null);
          csv.print(event.getName());
          csv.print(games);
          csv.print(p.getName());
          for(String b : badges) {
            csv.print(bdg.get(b));
          }
          csv.println();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (file != null) {
        try { file.close(); } catch (IOException e) {}
      }
      if (csv != null) {
        try { csv.close(); } catch (IOException e) {}
      }
    }
  }

  public void gamesRankingReports(Game[] all, String config, String outputFolder) {
    YAMLReader yaml = new YAMLReader();
    try {
      Map cfg = yaml.parse(config);
      String ignoreStr = (String) cfg.get("ignore");
      String[] ignore = ignoreStr == null ? new String[0] : ignoreStr.split(",");
      for(int i = 0; i<ignore.length; i++) {
        ignore[i] = ignore[i].trim().toLowerCase();
      }
      Arrays.sort(ignore);

      for(Game g : Game.findAll()) {
        System.out.println("Ranking for "+g.getName()+" - "+g.getSystem());
        gameRankingReport(g, outputFolder, "ranking-" + g.getFileName() + ".csv", ignore);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
