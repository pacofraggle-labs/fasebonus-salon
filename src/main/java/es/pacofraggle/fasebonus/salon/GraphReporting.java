package es.pacofraggle.fasebonus.salon;

import es.pacofraggle.fasebonus.salon.model.Event;
import es.pacofraggle.fasebonus.salon.model.Game;
import es.pacofraggle.fasebonus.salon.model.Participation;
import es.pacofraggle.fasebonus.salon.model.Player;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class GraphReporting {

  private String buildFileName(String outputFolder, String filename) {
    return outputFolder+File.separator+filename;
  }

  public void top10All(String suffix, String outputFolder) {
    String[] badges = new String[]{ "participaciones", "morado", "amarillo", "azul", "naranja" };
    for(String badge : badges) {
      String filename = "top10-"+badge+"-"+suffix+".jpg";
      top10Badge("Salón Recreativo FaseBonus Top Ten " + badge + " - " + suffix, outputFolder, filename, badge);
    }
  }

  public String top10Badge(String title, String outputFolder, String filename, String badge) {

    Player[] players = Player.orderByBadge(badge);

    int rows = players.length > 10 ? 10 : players.length;
    String[] group = new String[rows];
    int[] value = new int[rows];
    for(int i = 0; i< rows; i++) {
      value[i] = players[i].getBadges().get(badge);
      group[i] = players[i].getName();
      //group[i] = FileUtils.findImage(properties.get("avatar-folder") + File.separator + players[i].getAvatarName()).getPath();
    }

    DefaultCategoryDataset ds = new DefaultCategoryDataset();
    for(int i = 0; i < value.length; i++) {
      ds.addValue((double) value[i], group[i], "");
    }
    JFreeChart chart = ChartFactory.createBarChart(title, "Jugador", badge, ds);
    chart.setBackgroundPaint(Color.WHITE);

    String result;
    try {
      String file = buildFileName(outputFolder, filename);
      ChartUtilities.saveChartAsJPEG(new File(file), chart, 1000, 500);

      result = file;
    } catch (IOException e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  public void eventProgress(Event event, List<Participation> scores, String from, String to, String suffix, String outputFolder) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    try {
      Map<Date, Integer> totalData = new TreeMap<Date, Integer>();
      Map<String, Map<String, TimeSeries>> gamesData = new HashMap<String, Map<String, TimeSeries>>();

      for(Participation p : scores) {
        Date d = df.parse(p.getDate());
        int value = totalData.containsKey(d) ? totalData.get(d) : 0;
        totalData.put(d, value+1);

        String game = p.getGame().getName();
        String player = p.getPlayer().getName();
        int score = Integer.parseInt(p.getRecord());

        Map<String, TimeSeries> gameData =
          gamesData.containsKey(game) ? gamesData.get(game) : new HashMap<String, TimeSeries>();

        TimeSeries playerData =
          gameData.containsKey(player) ? gameData.get(player) : new TimeSeries(player);


        int prevScore = playerData.getMaxY() == Double.NaN ? -1 : (int) playerData.getMaxY();
        if (score > prevScore) {
          playerData.addOrUpdate(new Day(d), score);
          gameData.put(player, playerData);
          gamesData.put(game, gameData);
        }
      }


      Date ini = df.parse(from);
      Date end = df.parse(to);

      TimeSeries totalSeries = new TimeSeries("Puntuaciones publicadas");
      Iterator<Date> it = totalData.keySet().iterator();
      while(it.hasNext()) {
        Date d = it.next();
        totalSeries.add(new Day(d), (int) totalData.get(d));
      }

      TimeSeriesCollection ds = new TimeSeriesCollection();
      ds.addSeries(totalSeries);
      JFreeChart chart = ChartFactory.createTimeSeriesChart("Puntuaciones publicadas", "Fecha", "Records", ds);
      chart.getPlot().setBackgroundPaint(Color.WHITE);
      ((XYPlot) chart.getPlot()).setRenderer(new XYStepRenderer());
      String file = buildFileName(outputFolder, "records-"+event.getName()+"-"+suffix)+".jpg";
      ChartUtilities.saveChartAsJPEG(new File(file), chart, 800, 500);


      for(Game g : event.getGames()) {
        Map<String, TimeSeries> playersData = gamesData.get(g.getName());
        if (playersData == null) {
          continue;
        }
        ds = new TimeSeriesCollection();
        boolean hasData = false;
        System.out.println(g.getName());
        Iterator<String> players = playersData.keySet().iterator();
        int numSeries = 0;
        while(players.hasNext()) {
          String player = players.next();
          TimeSeries series = playersData.get(player);
          if (!series.isEmpty()) {
            series.addOrUpdate(new Day(ini), 0);
            series.addOrUpdate(new Day(end), series.getMaxY());
            ds.addSeries(series);
            numSeries++;
            System.out.println("  " + player + ": " + GraphReporting.timeSeriesToString(series));
            hasData = true;
          }
        }
        if (hasData) {
          Stroke stroke = new BasicStroke(3f);
          JFreeChart gameChart = ChartFactory.createTimeSeriesChart("Salón "+event.getName()+". "+g.getName(), "Fecha", "Records", ds);
          gameChart.getPlot().setBackgroundPaint(Color.WHITE);
          ((XYPlot) gameChart.getPlot()).setRenderer(new XYStepRenderer());
          for(int i=0; i<numSeries; i++) {
            ((XYPlot) gameChart.getPlot()).getRenderer().setSeriesStroke(i, stroke);
          }
          String gameFile = buildFileName(outputFolder, "records-"+event.getName()+"-"+g.getAvatarName())+".jpg";
          ChartUtilities.saveChartAsJPEG(new File(gameFile), gameChart, 800, 500);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static String timeSeriesToString(TimeSeries series) {
    String result = "";
    for(int i=0; i<series.getItemCount(); i++) {
      TimeSeriesDataItem item = series.getDataItem(i);
      result += "["+item.getPeriod()+"="+item.getValue()+"]";
    }

    return result;
  }
}
