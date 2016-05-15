package es.pacofraggle.fasebonus.salon.vo;

import es.pacofraggle.fasebonus.salon.TestsHelper;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BadgesTest {
  private Badges badges;

  @Before
  public void setUp() {
    badges = new Badges(3,2,1,0,5);
  }

  @Test
  public void testNew() {
    TestsHelper.assertBadges(badges, 3, 2, 1, 0, 5);
  }

  @Test
  public void testParseBadges() {
    Badges b = Badges.parseBadges("M");
    TestsHelper.assertBadges(b, 1, 0, 0, 0, 1);

    b = Badges.parseBadges("A");
    TestsHelper.assertBadges(b, 0, 1, 0, 0, 1);

    b = Badges.parseBadges("Z");
    TestsHelper.assertBadges(b, 0, 0, 1, 0, 1);

    b = Badges.parseBadges("N");
    TestsHelper.assertBadges(b, 0, 0, 0, 1, 1);

    b = Badges.parseBadges("MA");
    TestsHelper.assertBadges(b, 1, 1, 0, 0, 1);

    b = Badges.parseBadges("MZ");
    TestsHelper.assertBadges(b, 1, 0, 1, 0, 1);

    b = Badges.parseBadges("MN");
    TestsHelper.assertBadges(b, 1, 0, 0, 1, 1);

    b = Badges.parseBadges("MM");
    TestsHelper.assertBadges(b, 1, 0, 0, 0, 1);

    b = Badges.parseBadges("XX");
    TestsHelper.assertBadges(b, 0, 0, 0, 0, 1);
  }

  @Test
  public void testAddBadges() {
    Badges b = new Badges(3,3,0,8,1);
    badges.addBadges(b);

    TestsHelper.assertBadges(badges, 6, 5, 1, 8, 6);
  }
  
  @Test
  public void testIsEmpty() {
    Badges b = new Badges(0, 0, 0, 0, 0);
    assertTrue(b.isEmpty());
  }

  @Test
  public void testIsEmptyFalse() {
    assertFalse(badges.isEmpty());
  }
}
