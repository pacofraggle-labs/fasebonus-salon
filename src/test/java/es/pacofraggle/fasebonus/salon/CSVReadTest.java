package es.pacofraggle.fasebonus.salon;

import es.pacofraggle.fasebonus.salon.model.Event;
import es.pacofraggle.fasebonus.salon.model.Game;
import es.pacofraggle.fasebonus.salon.model.Participation;
import es.pacofraggle.fasebonus.salon.model.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CSVReadTest {

  private CSVReader csv;
  private String[] exisitingPlayers;

  @Before
  public void setUp() {
    Participation.clear();
    Player.clear();
    Game.clear();
    Event.clear();

    csv = new CSVReader();

    exisitingPlayers = "Araubi,Autorithy,Balthier,Batorre3,Beaches,Beornida,Bubu,Carlosblansa,Ciberchuso,CrankyKong,Cratero,DanySnowman,DemolitionMan,DolphLundgren,Elbitxo,Falsworth,Franelillas,FranFormol,Gamemaster,GarcesEs,Gaunt,Hellboy,Hipnosapo,Hoz3,HyLian,Imanolea,Jarrylsusio,Jauma80,JaviBonus,Kalzakalth,Kanirasta,Karakandao,Leia83,Logaran,LocoMJ,Luckpro,MaeseThreepwood,Magneto,MarcMax,Marcostegui,Martian,Masticador,Mikes,Moncara,MorbidMe,NandiusC,Nekopaki,Nuvalo,Pacofraggle,Periko,Perrosoy,PocketLucho,Premutor,PsychoFox,Puskax,RackProject,Raiders,Ramonth,Ricco,r0n1n,RobieInie,RudoStyle,Saigononindou,Salariasa,Setne,Sir.Arthur,Sito5sas,Srpresley,Tamariz,Terkai,Teyume,Tocandolospixeles,Wolfcult,Zael".split(",");
    Arrays.sort(exisitingPlayers);
  }

  @After
  public void tearDown() {
    Participation.clear();
    Player.clear();
    Game.clear();
    Event.clear();
  }

  @Test
  public void testRead() {
    try {
      csv.read("src/test/data/salon-20160501_1006.csv");

      for(String name : exisitingPlayers) {
        Player p = Player.find(name);
        assertTrue("["+p.getName()+"] not found", Arrays.binarySearch(exisitingPlayers, p.getName()) >= 0);
      }

      Event[] events = Event.findAll();
      assertEquals(22, events.length);
      for(Event event : events) {
        String name = event.getName();
        if (!"D".equals(name)) {
          int ev = Integer.parseInt(name);
          assertTrue(ev >= 1 && ev <= 21);
        }
      }
    } catch(Exception e) {
      fail(e.getMessage());
    }
  }
}
