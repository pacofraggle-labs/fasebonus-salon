package es.pacofraggle.fasebonus.salon;

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
import java.util.HashMap;
import java.util.Map;

public class CSVReader {
  public void read(String filename) throws Exception {
    File file = new File(filename);
    CSVParser parser = CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.RFC4180);

    Map<String, Integer> players = null; // Name, column_idx
    int dataCol = -1;
    for (CSVRecord record : parser) {
      long row = record.getRecordNumber();
      if (row == 1) {
        dataCol = findPlayers1stColumn(record);
        players = retrievePlayerColumns(dataCol, record);
        for(String p : players.keySet()) {
          Player.add(p, new Badges());
        }
      } else if (CSVReader.safeGet(record, 0).equals(DataTypeUtils.EMPTY_STRING)) {
        if (players != null) {
          String badge = record.get(4).trim().toLowerCase();
          for(String name : players.keySet()) {
            int idx = players.get(name);
            int value = Integer.parseInt(safeGet(record, idx));
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
          String game = CSVReader.safeGet(record, 0);
          String system = CSVReader.safeGet(record, 1);
          String category = CSVReader.safeGet(record, 2);
          String chooser = CSVReader.safeGet(record, 3);
          String event = CSVReader.safeGet(record, 4);

          Event e = Event.add(event);
          Game g = Game.add(game, chooser, category, system);
          for(String name : players.keySet()) {
            Player p = Player.find(name);
            int idx = players.get(name);
            String score = CSVReader.safeGet(record, idx);
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

  private static String safeGet(CSVRecord record, int column) {
    return record.size() > column ? record.get(column).trim() : DataTypeUtils.EMPTY_STRING;
  }

  private int findPlayers1stColumn(CSVRecord record) {
    int ini = Integer.MAX_VALUE;
    for(int i=0; i<record.size(); i++) {
      if ("Salón".equals(record.get(i))) {
        ini = i+1;
        break;
      }
    }
    return ini;
  }

  private Map<String, Integer> retrievePlayerColumns(int ini, CSVRecord record) {

    Map<String, Integer> result = new HashMap<String, Integer>();
    for(int i=ini; i<record.size(); i+=2) {
      result.put(record.get(i), i);
    }

    return result;
  }
}
