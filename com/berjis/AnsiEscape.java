package com.berjis;

public class AnsiEscape {

  private static String ESC = "\033[";
  private static String FOREGROUND = "38;5;";
  private static String BACKGROUND = "48;5;";

  public static String cursorTopLeft = ESC + "H";
  public static String cursorLeft = ESC + "G";
  public static String cursorSavePosition = ESC + "s";
  public static String cursorRestorePosition = ESC + "u";
  public static String cursorNextLine = ESC + "E";
  public static String cursorPrevLine = ESC + "F";
  public static String cursorHide = ESC + "?25l";
  public static String cursorShow = ESC + "?25h";
  public static String eraseEndLine = ESC + "K";
  public static String eraseStartLine = ESC + "1K";
  public static String eraseDown = ESC + "J";
  public static String eraseScreen = ESC + "2J";
  public static String enterAlternativeScreen = ESC + "?1049h";
  public static String exitAlternativeScreen = ESC + "?1049l";

  public static String fDarkGrey = ESC + FOREGROUND + "240m";
  public static String fLightGrey = ESC + FOREGROUND + "245m";
  public static String fWhite = ESC + FOREGROUND + "7m";
  public static String fBlack = ESC + FOREGROUND + "235m";
  public static String fGreen = ESC + FOREGROUND + "22m";

  public static String bDarkGrey = ESC + BACKGROUND + "240m";
  public static String bLightGrey = ESC + BACKGROUND + "245m";
  public static String bDarkYellow = ESC + BACKGROUND + "3m";
  public static String bDarkGreen = ESC + BACKGROUND + "2m";

  public static String OddCellsBg = ESC + BACKGROUND + "52m";
  public static String EvenCellsBg = ESC + BACKGROUND + "88m";
  public static String ProtectedCellBg = ESC + BACKGROUND + "130m";

  public static String reset = ESC + "m";
}
