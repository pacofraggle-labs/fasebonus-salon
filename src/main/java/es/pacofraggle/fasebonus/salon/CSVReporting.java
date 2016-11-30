package es.pacofraggle.fasebonus.salon;

import es.pacofraggle.fasebonus.salon.model.Event;
import es.pacofraggle.fasebonus.salon.model.Game;
import es.pacofraggle.fasebonus.salon.model.Participation;
import es.pacofraggle.fasebonus.salon.model.Player;
import es.pacofraggle.fasebonus.salon.vo.Badges;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

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

}
