package es.pacofraggle.fasebonus.salon;

import es.pacofraggle.commons.CSVUtils;
import es.pacofraggle.commons.DataTypeUtils;
import es.pacofraggle.fasebonus.salon.model.Event;
import es.pacofraggle.fasebonus.salon.model.Game;
import es.pacofraggle.fasebonus.salon.model.Participation;
import es.pacofraggle.fasebonus.salon.model.Player;
import es.pacofraggle.fasebonus.salon.vo.Badges;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVDataLoader {
  public void readHistorico(String filename) throws Exception {
    File file = new File(filename);
    CSVParser parser = CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.RFC4180);

    Map<String, Integer> players = null; // Name, column_idx
    int dataCol = -1;
    for (CSVRecord record : parser) {
      long row = record.getRecordNumber();
      if (row == 1) { // Header row
        dataCol = CSVUtils.findColumnNumber("Salón", record)+1;
        players = CSVUtils.retrieveColumnNumbers(dataCol, 2, record);
        for(String p : players.keySet()) {
          Player.add(p, new Badges());
        }
      } else if (CSVUtils.safeGet(record, 0).equals(DataTypeUtils.EMPTY_STRING)) { // Summary rows
        if (players != null) {
          String badge = record.get(4).trim().toLowerCase();
          for(String name : players.keySet()) {
            int idx = players.get(name);
            String val = CSVUtils.safeGet(record, idx);
            int value = DataTypeUtils.EMPTY_STRING.equals(val) ? 0 : Integer.parseInt(val);
            Player p = Player.find(name);
            Badges b = p.getBadges();
            if (badge.equals("participaciones")) {
              b.addParticipaciones(value);
            } else if (badge.equals("morados")) {
              b.addMorados(value);
            } else if (badge.equals("amarillos")) {
              b.addAmarillos(value);
            } else if (badge.equals("azules")) {
              b.addAzules(value);
            } else if (badge.equals("naranjas")) {
              b.addNaranjas(value);
            }
          }
        }
      } else {
        if (players != null) {
          String game = CSVUtils.safeGet(record, 0);
          String system = CSVUtils.safeGet(record, 1);
          String category = CSVUtils.safeGet(record, 2);
          String chooser = CSVUtils.safeGet(record, 3);
          String event = CSVUtils.safeGet(record, 4);

          Event e = Event.add(event);
          Game g = Game.add(game, chooser, category, system);
          e.addGame(g);
          for(String name : players.keySet()) {
            Player p = Player.find(name);
            int idx = players.get(name);
            String score = CSVUtils.safeGet(record, idx);
            Badges badges = Badges.parseBadges(record.get(idx + 1));
            if ((!score.equals(DataTypeUtils.EMPTY_STRING)) && (!score.equals("0")) && (!badges.isEmpty())) {
              Participation part = Participation.add(p, g, e, score, badges);
            }
          }
        }
      }
      //System.out.println(record);
    }
  }

  public List<Participation> readPuntuaciones(String filename, Event ev) throws Exception {
    File file = new File(filename);
    CSVParser parser = CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.RFC4180);

    List<Participation> participations = new ArrayList<Participation>();
    int eventCol = -1;
    int playerCol = -1;
    int gameCol = -1;
    int scoreCol = -1;
    int dateCol = -1;

    for (CSVRecord record : parser) {
      long row = record.getRecordNumber();
      if (row == 1) { // Header row
        eventCol = CSVUtils.findColumnNumber("Salón", record);
        playerCol = CSVUtils.findColumnNumber("Jugador", record);
        gameCol = CSVUtils.findColumnNumber("Juego", record);
        scoreCol = CSVUtils.findColumnNumber("Puntuación", record);
        dateCol = CSVUtils.findColumnNumber("Fecha", record);
      } else {
        String event = CSVUtils.safeGet(record, eventCol);
        Event e = Event.find(event);
        if (ev != e) {
          continue;
        }

        String game = CSVUtils.safeGet(record, gameCol);
        String player = CSVUtils.safeGet(record, playerCol);
        String score = CSVUtils.safeGet(record, scoreCol);
        String date = CSVUtils.safeGet(record, dateCol);
        Player p = Player.find(player);
        Game g = null;
        for(Game gm : e.getGames()) {
          if (game.equals(gm.getName())) {
            g = gm;
            break;
          }
        }

        Participation prt = new Participation(p, g, e, score, null, date);
        participations.add(prt);
      }
    }

    return participations;
  }
}
