package es.pacofraggle.fasebonus.salon.vo;

public final class Badges {

  public static final int MORADO = 'M';
  public static final int AMARILLO = 'A';
  public static final int AZUL = 'Z';
  public static final int NARANJA = 'N';

  private int morado = 0;
  private int amarillo = 0;
  private int azul = 0;
  private int naranja = 0;
  private int participaciones;

  public Badges() {
    this(0, 0, 0, 0, 0);
  }

  public Badges(int morado, int amarillo, int azul, int naranja, int participaciones) {
    this.morado = morado;
    this.amarillo = amarillo;
    this.azul = azul;
    this.naranja = naranja;
    this.participaciones = participaciones;
  }

  public static Badges parseBadges(String label) {
    int morado = 0;
    int amarillo = 0;
    int azul = 0;
    int naranja = 0;

    for(int i=0; i<label.length(); i++) {
      switch (label.charAt(i)) {
        case Badges.MORADO: morado = 1; break;
        case Badges.AMARILLO: amarillo = 1; break;
        case Badges.AZUL: azul = 1; break;
        case Badges.NARANJA: naranja = 1; break;
      }
    }

    return new Badges(morado, amarillo, azul, naranja, 1);
  }

  public void addBadges(Badges badges) {
    this.morado += badges.getMorado();
    this.amarillo += badges.getAmarillo();
    this.azul += badges.getAzul();
    this.naranja += badges.getNaranja();
    this.participaciones += badges.getParticipaciones();
  }

  public void addParticipaciones(int n) {
    this.participaciones += n;
  }

  public void addMorados(int n) {
    this.morado += n;
  }

  public void addAmarillos(int n) {
    this.amarillo += n;
  }

  public void addAzules(int n) {
    this.azul += n;
  }

  public void addNaranjas(int n) {
    this.naranja += n;
  }

  public int getMorado() {
    return morado;
  }

  public void setMorado(int morado) {
    this.morado = morado;
  }

  public int getAmarillo() {
    return amarillo;
  }

  public void setAmarillo(int amarillo) {
    this.amarillo = amarillo;
  }

  public int getAzul() {
    return azul;
  }

  public void setAzul(int azul) {
    this.azul = azul;
  }

  public int getNaranja() {
    return naranja;
  }

  public void setNaranja(int naranja) {
    this.naranja = naranja;
  }

  public int getParticipaciones() {
    return participaciones;
  }

  public void setParticipaciones(int participaciones) {
    this.participaciones = participaciones;
  }

  public int get(String name) {
    int result;
    if ("morado".equals(name)) {
      result = morado;
    } else if ("amarillo".equals(name)) {
      result = amarillo;
    } else if ("azul".equals(name)) {
      result = azul;
    } else if ("naranja".equals(name)) {
      result = naranja;
    } else if ("participaciones".equals(name)) {
      result = participaciones;
    } else {
      result = -1;
    }

    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Badges badges = (Badges) o;

    if (amarillo != badges.getAmarillo()) return false;
    if (azul != badges.getAzul()) return false;
    if (morado != badges.getMorado()) return false;
    if (naranja != badges.getNaranja()) return false;
    if (participaciones != badges.getParticipaciones()) return false;

    return true;
  }

  public boolean isEmpty() {
    return morado == 0 && amarillo == 0 && azul == 0 && naranja == 0 && participaciones == 0;
  }

  @Override
  public int hashCode() {
    int result = morado;
    result = 31 * result + amarillo;
    result = 31 * result + azul;
    result = 31 * result + naranja;
    result = 31 * result + participaciones;
    return result;
  }

  @Override
  public Object clone() {
    return new Badges(morado, amarillo, azul, naranja, participaciones);
  }

  @Override
  public String toString() {
    return "Badges{" + "M=" + morado + ", A=" + amarillo + ", Z=" + azul + ", N=" + naranja + ", P=" + participaciones + '}';
  }
}
