package es.pacofraggle.commons;

import sun.rmi.transport.ObjectTable;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Msgs {
  public final static int DEBUG = 0;
  public final static int INFO = 1;
  public final static int WARN = 2;
  public final static int ERROR = 3;
  public final static int NONE = 4;

  private List<String> msgs = new ArrayList<String>(100);
  private PrintStream[] streams = new PrintStream[4];

  private int level = Msgs.NONE;

  public Msgs(int level, boolean on) {
    setLevel(level);
    setStream(Msgs.DEBUG, on ? System.out : null);
    setStream(Msgs.INFO, on ? System.out : null);
    setStream(Msgs.WARN, on ? System.err : null);
    setStream(Msgs.ERROR, on ? System.err : null);
  }

  public Msgs(int level, PrintStream debug, PrintStream info, PrintStream warn, PrintStream error) {
    setLevel(level);
    setStream(Msgs.DEBUG, debug);
    setStream(Msgs.INFO, info);
    setStream(Msgs.WARN, warn);
    setStream(Msgs.ERROR, error);
  }

  public void setStream(int level, PrintStream stream) {
    if ((level>=Msgs.DEBUG) && (level<Msgs.NONE)) {
      streams[level] = stream;
    }
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = (level >= Msgs.DEBUG) && (level <Msgs.NONE) ? level : Msgs.NONE;
  }

  public void close(int level) {
    if ((level>=Msgs.DEBUG) && (level<Msgs.NONE)) {
      if (streams[level] != null) {
        streams[level].close();
        streams[level] = null;
      }
    }
  }

  public void flush(int level) {
    if ((level>=Msgs.DEBUG) && (level<Msgs.NONE)) {
      if (streams[level] != null) {
        streams[level].flush();
      }
    }
  }

  public List<String> getMessages() {
    return msgs;
  }

  public String[] extractMessages() {
    String[] result = msgs.toArray(new String[msgs.size()]);
    msgs.clear();

    return result;
  }

  public void println(int level, Object msg) {
    if ((level>=Msgs.DEBUG) && (level<Msgs.NONE) && (level>=this.level)) {
      String m;
      if (msg instanceof Exception) {
        if ((level == Msgs.DEBUG) || (level == Msgs.ERROR)) {
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw);
          ((Exception) msg).printStackTrace(pw);
          m = sw.toString();
        } else {
          m = ((Exception) msg).getMessage();
        }
      } else {
        m = msg.toString();
      }
      msgs.add(m);
      if (streams[level] != null) {
        streams[level].println(m);
      }
    }
  }

  public void debug(Object msg) {
    println(Msgs.DEBUG, msg);
  }

  public void info(Object msg) {
    println(Msgs.INFO, msg);
  }

  public void warn(Object msg) {
    println(Msgs.WARN, msg);
  }

  public void error(Object msg) {
    println(Msgs.ERROR, msg);
  }
}
