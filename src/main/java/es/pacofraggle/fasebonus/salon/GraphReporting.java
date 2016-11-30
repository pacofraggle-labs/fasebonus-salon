package es.pacofraggle.fasebonus.salon;

import es.pacofraggle.commons.DataTypeUtils;
import es.pacofraggle.commons.FileUtils;
import es.pacofraggle.fasebonus.salon.model.Player;
import es.pacofraggle.fasebonus.salon.vo.GraphicProperties;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GraphReporting {

  private String buildFileName(String outputFolder, String filename) {
    return outputFolder+File.separator+filename;
  }

  public void top10All(String suffix, String outputFolder) {
    String[] badges = new String[]{ "participaciones", "morado", "amarillo", "azul", "naranja" };
    for(String badge : badges) {
      String filename = "top10-"+badge+"-"+suffix+".png";
      top10Badge("Salón Recreativo FaseBonus Top Ten "+badge+" - "+suffix, outputFolder, filename, badge);
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

    String result;
    try {
      String file = buildFileName(outputFolder, filename);
      ChartUtilities.saveChartAsJPEG(new File(file), chart, 800, 600);

      result = file;
    } catch (IOException e) {
      e.printStackTrace();
      result = null;
    }



    return result;
  }
}
