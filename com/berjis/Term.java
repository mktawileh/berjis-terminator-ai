package com.berjis;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import java.io.IOException;
import java.util.Arrays;

public class Term {
  private static StringBuilder buffer = new StringBuilder();

  public enum Color {
    DarkYellow,
    DarkGreen,
    DarkGrey,
    LightGrey,
    White,
    Black,
    OddCellsBg,
    EvenCellsBg,
    ProtectedCell,
    Green
  }

  public static void clearScreen() {
    Term.clearBuffer();
    Term.write(AnsiEscape.cursorTopLeft);
    Term.write(AnsiEscape.eraseScreen);
  }

  public static void clearBuffer() {
    buffer.setLength(0);
  }

  public static <T> void color(Color color, T s) {
    switch (color) {
      case DarkGrey:
        {
          Term.write(AnsiEscape.fDarkGrey);
          Term.write(s);
          Term.write(AnsiEscape.reset);
        }
        break;
      case LightGrey:
        {
          Term.write(AnsiEscape.fLightGrey);
          Term.write(s);
          Term.write(AnsiEscape.reset);
        }
        break;
      case White:
        {
          Term.write(AnsiEscape.fWhite);
          Term.write(s);
          Term.write(AnsiEscape.reset);
        }
        break;
      case Black:
        {
          Term.write(AnsiEscape.fBlack);
          Term.write(s);
          Term.write(AnsiEscape.reset);
        }
        break;
      case Green: 
        {
          Term.write(AnsiEscape.fGreen);
          Term.write(s);
          Term.write(AnsiEscape.reset);
        }
        break;
      default:
        {}
    }
  }

  public static <T> void bg(Color color, T s) {
    switch (color) {
      case DarkGrey:
        {
          Term.write(AnsiEscape.bDarkGrey);
          Term.write(s);
          Term.write(AnsiEscape.reset);
        }
        break;
      case LightGrey:
        {
          Term.write(AnsiEscape.fBlack);
          Term.write(AnsiEscape.bLightGrey);
          Term.write(s);
          Term.write(AnsiEscape.reset);
        }
        break;
      case DarkYellow:
        {
          Term.write(AnsiEscape.bDarkYellow);
          Term.write(s);
          Term.write(AnsiEscape.reset);
        }
        break;
      case DarkGreen:
        {
          Term.write(AnsiEscape.bDarkGreen);
          Term.write(s);
          Term.write(AnsiEscape.reset);
        }
        break;
      case OddCellsBg:
        {
          Term.write(AnsiEscape.OddCellsBg);
          Term.write(s);
          Term.write(AnsiEscape.reset);
        }
        break;
      case EvenCellsBg:
        {
          Term.write(AnsiEscape.EvenCellsBg);
          Term.write(s);
          Term.write(AnsiEscape.reset);
        }
        break;
      case ProtectedCell:
        {
          Term.write(AnsiEscape.ProtectedCellBg);
          Term.write(s);
          Term.write(AnsiEscape.reset);
        }
        break;
      default:
        {}
    }
  }

  public static <T> void write(T s) {
    if (s instanceof char[]) {
      buffer.append(new String((char[]) s));
    } else {
      buffer.append(String.valueOf(s));
    }
  }

  public static void writef(String s) {
    Term.write(s);
    Term.flush();
  }

  public static int read() throws IOException {
    return RawConsoleInput.read(true);
  }

  public static void flush() {
    System.out.print(buffer);
  }

  public static void resetTerminal() throws IOException {
    RawConsoleInput.resetConsoleMode();
  }

  private static void moveCursorToTopLeft(StringBuilder builder) {
    builder.append("\033[H");
  }

}
