package es.pacofraggle.commons;

import org.apache.commons.csv.CSVRecord;

import java.util.HashMap;
import java.util.Map;

public class CSVUtils {

  public static String safeGet(CSVRecord record, int column) {
    return record.size() > column ? record.get(column).trim() : DataTypeUtils.EMPTY_STRING;
  }

  public static int findColumnNumber(String value, CSVRecord record) {
    int ini = Integer.MIN_VALUE;
    for(int i=0; i<record.size(); i++) {
      if ("Salón".equals(record.get(i))) {
        ini = i;
        break;
      }
    }
    return ini;
  }

  public static Map<String, Integer> retrieveColumnNumbers(int ini, int jump, CSVRecord record) {

    Map<String, Integer> result = new HashMap<String, Integer>();
    for(int i=ini; i<record.size(); i+=jump) {
      result.put(record.get(i), i);
    }

    return result;
  }
}
