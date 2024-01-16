package com.berjis;

import java.io.IOException;

public class Main {
    
    static GameTermControl control;

    public static void main(String[] args) throws IOException {
        Term.writef(AnsiEscape.cursorHide);
        Term.clearScreen();
        Term.flush();
        Term.writef(AnsiEscape.enterAlternativeScreen);
        
        control = new GameTermControl();
        try {
            while (true){

                Term.clearBuffer();
                Term.write(AnsiEscape.cursorTopLeft);
                
                control.Loop();

                Term.flush();
                int key = Term.read();
                Boolean result = handleKey(key);
                if (result == false) break;
            }
        } finally {
            Term.writef(AnsiEscape.cursorTopLeft);
            Term.writef(AnsiEscape.eraseScreen);
            Term.writef(AnsiEscape.exitAlternativeScreen);
            Term.writef(AnsiEscape.cursorShow);
            Term.resetTerminal();
        }
        
    }


    private static Boolean handleKey(int key) {
        if (key == 'q') {
            return false;
        } else if (key == 39 || key == '+') {
            control.AdvanceCursor(1);
        } else if (key == 37 || key == '-') {
            control.AdvanceCursor(-1);
        } else if (key == 13 || key == 10) {
            control.ToggleCursorState();
        } else if(key == '*') {
            control.HumanThrowTurn();
        } else if (key == ' ') {
            control.HumanDone();
        } else if (key == '0') {
            control.MakeMove();
        }
        return true;
    }

}
