package es.pacofraggle.fasebonus.salon;

import java.io.*;
import java.net.URL;

public class GoogleSheetsFacade {

  public String getSpreadsheet(String id, String outputFolder, String filename) {

    InputStreamReader isr = null;
    BufferedReader in = null;
    Writer ofw = null;
    PrintWriter out = null;
    try {
      URL doc = new URL("https://docs.google.com/spreadsheets/d/"+id+"/export?format=csv&id="+id+"&gid=0");
      isr = new InputStreamReader(doc.openStream());
      in = new BufferedReader(isr);
      ofw = new FileWriter(outputFolder+"/"+filename);
      out = new PrintWriter(ofw);
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        out.println(inputLine);
      }
      out.flush();

    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } finally {
      try {
        if (isr != null) { isr.close(); }
        if (in != null) { isr.close(); }
        if (ofw != null) { isr.close(); }
        if (out != null) { isr.close(); }
      } catch(IOException ex) {}
    }

    return filename;
  }
}
