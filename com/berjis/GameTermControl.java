package com.berjis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collections;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class Pos {
    int x, y;
    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class GameTermControl {

    static int CELL_WIDTH = 3;
    static int CELL_HEIGHT = 1;

    static int SIDE_WIDTH = 3;
    static int SIDE_LENGTH = 8;
    static int BOARD_SIDE_LENGTH = SIDE_LENGTH * 2 + SIDE_WIDTH;

    static char CURSOR_NORMAL_LEFT = '(';
    static char CURSOR_NORMAL_RIGHT = ')';
    static char CURSOR_GRAB_LEFT = '{';
    static char CURSOR_GRAB_RIGHT = '}';
    static char CURSOR_HIDDEN_LEFT = ' ';
    static char CURSOR_HIDDEN_RIGHT = ' ';
    static char OLD_MOVE_MARKER = '#';
    static char NEW_MOVE_MARKER = '$';
    static char PROTECTED_CELL_MARK = '!';

    static char DICE_UP = 'i';
    static char DICE_DOWN = '@';

    static char HUMAN_PIECE = 'I';
    static char COMPUTER_PIECE = 'C';

    static int[] protected_cells = { 10, 21, 27, 38, 44, 55, 61, 72 };

    private enum CursorState {
        Grab,
        Normal,
        Hidden,
        HoldingPiece,
    }

    BerjisGame game;
    Dice dice;
    int depth;
    List<Pair> posScenarios;
    List<Integer> computer_moves;
    List<Integer> all_moves; // indices
    List<Integer> moves; // values
    List<Integer> umoves; // values

    char computer;
    char human;


    char[][] buf;
    int buf_width;
    int buf_height;

    CursorState cursor_state;
    boolean human_turn;
    boolean human_throwing;
    int current_piece;
    int cursor_position; // from 0 to 83
    int cursor_current_move;

    public GameTermControl() {
        game = new BerjisGame();
        dice = new Dice();

        buf_height = CELL_HEIGHT * (BOARD_SIDE_LENGTH);
        buf_width = CELL_WIDTH * (BOARD_SIDE_LENGTH);
        buf = new char[buf_height][buf_width];

        cursor_position = 0;
        current_piece = 0;
        cursor_state = CursorState.Normal;

        human_turn = true;
        human_throwing = false;
        computer = 'A';
        human = 'B';

        for (int i = 0; i < buf_height; i++) {
            for (int j = 0; j < buf_width; j++) {
                buf[i][j] = ' ';
            }
        }

        all_moves = new ArrayList<>();
        moves = new ArrayList<>();
        depth = 3;
        umoves = new ArrayList<>();

        posScenarios = new ArrayList<>(dice.getAllPossibleScenarios(0.01));
        // posScenariosArr.add(new ArrayList<>(dice.getAllPossibleScenarios(0.01)));
        // posScenariosArr.add(new ArrayList<>(dice.getAllPossibleScenarios(0.014)));
        // posScenariosArr.add(new ArrayList<>(dice.getAllPossibleScenarios(0.04)));
    }

    public void Loop() {
        this.clearBuf();
        if (!this.human_turn) {
            this.human_turn = true;
            this.human_throwing = true;
            for (int i = 0; i < 4; i++) {
                this.game.PLM[0][i] = this.game.P[0][i];
            }
            this.computerPlay();
        }
        // Human playing
        this.umoves.clear();
        if (!this.human_throwing && this.all_moves.size() > 0) {
            Set<Integer> st = new TreeSet<>();
            for (int move : this.moves) {
                st.add(move);
            }
            for (int move : st) {
                this.umoves.add(move);
            }
        }

        this.printUsage();

        // Term.write("Hello world!\n\r");
        this.PrintBoard();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("logs", true))) {
            writer.append("Computer Pieces: ");
            writer.newLine();
            writer.append(Arrays.toString(game.P[0]));
            writer.newLine();
            if (this.computer_moves != null) {
                writer.append(dice.getValues(this.computer_moves).toString());
                writer.newLine();
            }        
            writer.append("Human Pieces: ");
            writer.newLine();
            if (this.all_moves != null) {
                writer.append(dice.getValues(this.all_moves).toString());
                writer.newLine();
            }
            writer.append(Arrays.toString(game.P[1]));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void HumanThrowTurn() {
        if (this.all_moves.size() == 0) {
            this.human_throwing = true;
        } else if (this.all_moves.get(this.all_moves.size() - 1) >= 4) {
            this.human_throwing = false;
        }
        if (this.human_throwing) {
            int ind = this.dice.THROW();
            this.all_moves.add(ind);
            this.moves = dice.getValues(this.all_moves);
            if (ind >= 4) {
                this.human_throwing = false;
            }
        }
    }

    public void HumanDone() {
        if (this.isHumanDone()) {
            this.all_moves.clear();
            this.umoves.clear();
            this.moves.clear();
            this.human_turn = false;
        }
    }

    public void AdvanceCursor(int dir) {
        if (cursor_state == CursorState.Normal) {
            List<Integer> pieces = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                pieces.add(game.P[1][i]);
            }
            if (dir < 0) Collections.reverse(pieces);
            boolean passed = false;
            for (int j = 0; j < 2 * pieces.size(); j++) {
                int i = (j % pieces.size());
                int reali = dir < 0 ? (pieces.size() - i - 1) : i;
                if (pieces.get(i) != 83 && passed) {
                    current_piece = reali;
                    break;
                }
                if (current_piece == reali) passed = true;
            }
        } else {
            if (umoves.size() > 0) {
                if (dir < 0) Collections.reverse(umoves);
                boolean passed = false;
                for (int j = 0; j < 2 * umoves.size(); j++) {
                    int i = (j % umoves.size());
                    int reali = (dir < 0 ? umoves.size() - i - 1 : i);
                    if (this.isMovePlayable(this.current_piece, umoves.get(i)) && passed) {
                        cursor_current_move = reali;
                        break;
                    }
                    if (this.cursor_current_move == reali) passed = true;
                }
                if (dir < 0) Collections.reverse(umoves);
            }
        }
    }

    public void ToggleCursorState() {
        if (this.cursor_state == CursorState.Normal && this.current_piece != -1 && !this.human_throwing) {
            this.cursor_state = CursorState.Grab;
            this.cursor_current_move = -1;
            for (int i = 0; i < this.umoves.size(); i++) {
                if (this.current_piece != -1 && this.isMovePlayable(this.current_piece, this.umoves.get(i))) {
                    this.cursor_current_move = i;
                    break;
                }
            }
        } else {
            this.cursor_state = CursorState.Normal;
        }
    }

    public void MakeMove() {
        if (!this.human_throwing && cursor_state == CursorState.Grab && this.cursor_current_move != -1 && this.current_piece != -1) {
            int value = this.umoves.get(cursor_current_move);
            this.game.move('B', this.current_piece, value);
            this.moves.remove(Integer.valueOf(value));
            this.cursor_state = CursorState.Normal;
        }
    }

    public void PrintBoard() {
        // Adjust the cursor positioin depeding on the selected piece
        this.findCursorPosition(this.game.P[1]);
        Pos current_cursor = mapPositionToBoard(cursor_position);

        // Place the proteced cells
        this.placeProtectedCells();

        // Placing the computer pieces
        this.placePlayerPieces(this.game.P[0], this.game.PLM[0], COMPUTER_PIECE);
        // Placing the human pieces
        this.placePlayerPieces(this.game.P[1], this.game.PLM[1], HUMAN_PIECE);

        // Placing the possible moves for the human
        this.placePossibleMoves();

        if (this.moves.size() > 0) {
            int res = this.all_moves.get(this.all_moves.size() - 1);
            this.randomDicePositions(res);
        }

        // Placing the computer last moves list
        this.placeMovesList(
            this.computer_moves,
            "Computer got: ",
            0,
            this.buf_width - (CELL_WIDTH * SIDE_LENGTH)
        );
        // Placing the human last moves list
        this.placeMovesList(
            this.all_moves,
            "You got: ",
            this.buf_height - (CELL_HEIGHT * SIDE_LENGTH),
            0
        );

        if (this.isHumanDone() && !this.human_throwing && this.all_moves.size() > 0) {
            this.placeStringInBuffer("No more moves!", 4, 0);
        }

        // Place cursor position
        StringBuilder cursor_position_title = new StringBuilder("Index: ");
        if (this.cursor_position == 83) {
            cursor_position_title.append("Kitchen");
        } else {
            cursor_position_title.append(cursor_position);
        }
        this.placeStringInBuffer(String.valueOf(cursor_position_title), 0, 0);
        this.placeStringInBuffer("Current Piece: " + (current_piece + 1), 1, 0);


        // Procces what we placed and print them
        Term.write("\t");
        for (int i = 0; i < buf_width + 2; i++) {
            Term.color(Term.Color.DarkGrey, "_");
        }
        Term.write("\n\r");
        for (int i = 0; i < BOARD_SIDE_LENGTH; i++) {
            Term.color(Term.Color.DarkGrey, "\t|");
            for (int j = 0; j < BOARD_SIDE_LENGTH; j++) {
                int x = i * CELL_HEIGHT;
                int y = j * CELL_WIDTH;
                char[] cell = Arrays.copyOfRange(buf[x], y, y + CELL_WIDTH);

                if (current_cursor.x == i && current_cursor.y == j) {
                    switch (cursor_state) {
                    case Grab:
                        {
                        cell[0] = CURSOR_GRAB_LEFT;
                        cell[2] = CURSOR_GRAB_RIGHT;
                        }
                        break;
                    case Normal:
                        {
                        cell[0] = CURSOR_NORMAL_LEFT;
                        cell[2] = CURSOR_NORMAL_RIGHT;
                        }
                        break;
                    case Hidden:
                        {
                        cell[0] = CURSOR_HIDDEN_LEFT;
                        cell[2] = CURSOR_HIDDEN_RIGHT;
                        }
                        break;
                    default:
                        {}
                    }
                }

                if (cell[0] == OLD_MOVE_MARKER) {
                    cell[0] = ' ';
                    Term.bg(Term.Color.DarkGrey, cell);
                } else if (cell[0] == NEW_MOVE_MARKER) {
                    cell[0] = ' ';
                    Term.bg(Term.Color.DarkGreen, cell);
                } else if (cell[0] == PROTECTED_CELL_MARK) {
                    cell[0] = ' ';
                    Term.bg(Term.Color.ProtectedCell, cell);
                } else if (this.isValidCell(i, j)) {
                    if ((i + j) % 2 == 0) {
                        Term.bg(Term.Color.EvenCellsBg, cell);
                    } else {
                        Term.bg(Term.Color.OddCellsBg, cell);
                    }
                } else if (this.isCellInMiddle(i, j) && this.cursor_position == 83) {
                    if (this.cursor_state == CursorState.Normal) {
                        Term.bg(Term.Color.DarkGrey, cell);
                    } else {
                        Term.bg(Term.Color.LightGrey, cell);
                    }
                } else if (cell[1] == DICE_UP) {
                    Term.bg(Term.Color.LightGrey, cell);
                } else if (cell[1] == DICE_DOWN) {
                    Term.bg(Term.Color.DarkGrey, cell);
                } else {
                    Term.color(Term.Color.LightGrey, cell);
                }
            }
            Term.color(Term.Color.DarkGrey, "|\n\r");
        }
        Term.write("\t");
        for (int i = 0; i < buf_width + 2; i++) {
            Term.color(Term.Color.DarkGrey, "¯");
        }
    }



    // TODO: pass the list of dice
    private void randomDicePositions(int result_index) {
        int count = dice.downs_count[result_index];

        // Randomize the order
        List<Boolean> dices = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            dices.add(count-- > 0);
        }
        Collections.shuffle(dices);

        // Randomize the postiions
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        Set<Integer> inds = new HashSet<>();
        while (inds.size() < 6) {
            inds.add(random.nextInt((SIDE_LENGTH - 1) * (SIDE_LENGTH - 1)));
        }
        int dice_index = 5;
        for (int i = 1; i < SIDE_LENGTH; i++) {
            for (int j = 1; j < SIDE_LENGTH; j++) {
                int x = (i + SIDE_LENGTH + SIDE_WIDTH) * CELL_HEIGHT;
                int y = (j + SIDE_LENGTH + SIDE_WIDTH) * CELL_WIDTH;
                buf[x][y + 1] = ' ';
                if (inds.contains((i - 1) * (SIDE_LENGTH - 1) + (j - 1))) {
                    buf[x][y + 1] = dices.get(dice_index--) ? DICE_UP : DICE_DOWN;
                }
            }
        }
    }

    private void findCursorPosition(int [] pieces) {
        if (current_piece != -1) {
            cursor_position = (pieces[current_piece] + 84) % 84;
        }
    }

    private Pos mapPositionToBoard(int pos) {
        Pos res = new Pos(-1, -1);
        if (pos < 0) return res;
        if (pos < 8) {
        res.x = 11 + pos;
        res.y = 9;
        } else if (pos < 16) {
        res.x = 18 - (pos - 8);
        res.y = 10;
        } else if (pos < 24) {
        res.x = 10;
        res.y = 11 + (pos - 16);
        } else if (pos < 25) {
        res.x = 9;
        res.y = 18;
        } else if (pos < 33) {
        res.x = 8;
        res.y = 18 - (pos - 25);
        } else if (pos < 41) {
        res.x = 7 - (pos - 33);
        res.y = 10;
        } else if (pos < 42) {
        res.x = 0;
        res.y = 9;
        } else if (pos < 50) {
        res.x = pos - 42;
        res.y = 8;
        } else if (pos < 58) {
        res.x = 8;
        res.y = 57 - pos;
        } else if (pos < 59) {
        res.x = 9;
        res.y = 0;
        } else if (pos < 67) {
        res.x = 10;
        res.y = pos - 59;
        } else if (pos < 75) {
        res.x = pos - 56;
        res.y = 8;
        } else if (pos < 76) {
        res.x = 18;
        res.y = 9;
        } else if (pos < 84) {
        res.x = 93 - pos;
        res.y = 9;
        }
        return res;
    }

    private boolean isValidCell(int x, int y) {
        return (
        (y >= SIDE_LENGTH && y < SIDE_LENGTH + SIDE_WIDTH) ^
        (x >= SIDE_LENGTH && x < SIDE_LENGTH + SIDE_WIDTH)
        );
    }

    private boolean isCellInMiddle(int x, int y) {
        return (
        (y >= SIDE_LENGTH && y < SIDE_LENGTH + SIDE_WIDTH) &&
        (x >= SIDE_LENGTH && x < SIDE_LENGTH + SIDE_WIDTH)
        );
    }

    private void placeProtectedCells() {
        
        for (int cell = 0; cell < 83; cell++) {
            boolean isProteced = false;
            Pos cur = this.mapPositionToBoard(cell);
            int nx = cur.x * CELL_HEIGHT;
            int ny = cur.y * CELL_WIDTH + 1; 
            for (int j = 0; j < this.protected_cells.length; j++) {
                // System.out.println(this.protected_cells[j] + " " + pieces[i]);
                if (this.protected_cells[j] == cell) {
                    isProteced = true;
                }
            }
            if (isProteced) {
                this.buf[nx][ny - 1] = PROTECTED_CELL_MARK;
            }
        }
    }

    // TODO: change the starting position of the pieces
    private void placePlayerPieces(int[] pieces, int[] old_pieces, char piece) {
        for (int i = 0; i < pieces.length; i++) {
            Pos cur = this.mapPositionToBoard(pieces[i]);
            Pos old_cur = this.mapPositionToBoard(old_pieces[i]);


            if (piece == COMPUTER_PIECE) {
                if (cur.x != -1 && cur.y != -1) {
                    cur.y = (BOARD_SIDE_LENGTH - cur.y) - 1;
                    cur.x = (BOARD_SIDE_LENGTH - cur.x) - 1;
                }
                if (old_cur.x != -1 && old_cur.y != -1) {
                    old_cur.y = (BOARD_SIDE_LENGTH - old_cur.y) - 1;
                    old_cur.x = (BOARD_SIDE_LENGTH - old_cur.x) - 1;
                }
            }
            if (cur.x != -1 && cur.y != -1) {
                int nx = cur.x * CELL_HEIGHT;
                int ny = cur.y * CELL_WIDTH + 1;
                int ox = old_cur.x * CELL_HEIGHT;
                int oy = old_cur.y * CELL_WIDTH + 1;
                if (pieces[i] == 83) {
                    if (buf[nx][ny] == ' ') {
                        buf[nx][ny] = '1';
                    } else {
                        buf[nx][ny] = (char)(buf[nx][ny] + 1);
                    }
                } else {
                    if (buf[nx][ny] != ' ') {
                        if (buf[nx][ny + 1] == ' ') {
                            buf[nx][ny + 1] = '2';
                        } else if (buf[nx][ny + 1] == '2') {
                            buf[nx][ny + 1] = '3';
                        } else {
                            buf[nx][ny + 1] = '4';
                        }
                    }
                    buf[nx][ny] = piece;
                }
                if (piece == COMPUTER_PIECE && old_cur.x !=  -1 && old_cur.y != -1 && old_pieces[i] != pieces[i]) {
                  
                    this.buf[ox][oy - 1] = OLD_MOVE_MARKER;
                    if (pieces[i] != 83) {
                        this.buf[nx][ny - 1] = NEW_MOVE_MARKER;
                    }
                }
            } else {
                if (piece == HUMAN_PIECE) {
                    this.buf[10][SIDE_LENGTH * CELL_WIDTH + i] = piece;
                } else {
                    this.buf[8][SIDE_LENGTH * CELL_WIDTH + i] = piece;
                }
            }
        }
    }


    private void clearBuf() {
        for (int i = 0; i < buf_height; i++) {
            for (int j = 0; j < buf_width; j++) {
                buf[i][j] = ' ';
            }
        }
    }

    private boolean isMovePlayable(int piece_ind, int move) {
        int cur_piece = this.game.P[1][piece_ind];
        return (
            (cur_piece != -1 && this.game.canMove('B', piece_ind, move)) ||
            (cur_piece == -1 && move == 1)
        );
    }

    private boolean isHumanDone() {
        if (this.human_throwing) return false;
        for (int i = 0; i < this.umoves.size(); i++) {
            for (int piece = 0; piece < 4; piece++) {
                if (this.isMovePlayable(piece, this.umoves.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void placePossibleMoves() {
        if (this.current_piece != -1) {
            for (int i = 0; i < this.umoves.size(); i++) {
                int move_pos = this.cursor_position + this.umoves.get(i);
                int value = this.umoves.get(i);
                if (this.isMovePlayable(this.current_piece, value)) {
                    Pos pos = this.mapPositionToBoard(move_pos % 84);
                    if (pos.x == -1 || pos.y == -1) {
                        throw new Error("Something happend wrong while placing the possible moves of the selected piece.");
                    }
                    int x = pos.x * CELL_HEIGHT;
                    int y = pos.y * CELL_WIDTH;
                    if (
                        this.cursor_current_move == i &&
                        this.cursor_state == CursorState.Grab
                    ) {
                        this.buf[x][y + 1] = 'x';
                    } else {
                        this.buf[x][y + 1] = 'o';
                    }
                }
            }
        }
    }

    private void placeStringInBuffer(String s, int row, int col) {
        for (int i = 0; i < s.length(); i++) {
        buf[row][i + col] = s.charAt(i);
        }
    }

    private void placeMovesList(List<Integer> mvs, String title, int row, int col) {
        if (mvs != null && mvs.size() > 0) {
        this.placeStringInBuffer(title, row, col);
        List<String> names = dice.getNames(mvs);
        for (int i = 1; i <= names.size(); i++) {
            this.placeStringInBuffer("- " + names.get(i - 1), i + row, col);
        }
        }
    }

    public Pair2 maxMove(BerjisGame curGame, List<Integer> throwRes, int level) {
        double mx = -10000;
        TreeSet<BerjisGame> st = new TreeSet<>(curGame.getAllNextMoves(throwRes, computer));

        BerjisGame retGame = new BerjisGame();
        for (BerjisGame bg : st) {
            double val = expectedValueMax(bg, level + 1);
            if (val > mx) {
                mx = val;
                retGame = bg;
            }
        }
        return new Pair2(new BerjisGame(retGame), mx);
    }

    public double expectedValueMax(BerjisGame bg, int level) {
        if (Timing.passedTime() >= Timing.threshold || level >= depth || bg.isFinished())
            return bg.evaluate(computer);

        double ret = 0;
        for (Pair x : posScenarios) {
            ret += minMove(bg, dice.getValues(x.first), level).second * x.second;
        }
        return ret;

    }

    public Pair2 minMove(BerjisGame curGame, List<Integer> throwRes, int level) {

        double mn = 10000;
        TreeSet<BerjisGame> st = new TreeSet<>(curGame.getAllNextMoves(throwRes, human));
        BerjisGame retGame = new BerjisGame();
        for (BerjisGame bg : st) {
            double val = expectedValueMin(bg, level + 1);
            if (val < mn) {
                mn = val;
                retGame = bg;
            }
        }
        return new Pair2(new BerjisGame(retGame), mn);

    }

    public double expectedValueMin(BerjisGame bg, int level) {
        if (Timing.passedTime() >= Timing.threshold || level >= depth || bg.isFinished())
            return bg.evaluate(computer);
        double ret = 0;
        for (Pair x : posScenarios) {
            ret += maxMove(bg, dice.getValues(x.first), level).second * x.second;
        }
        return ret;
    }

    private void computerPlay() {
        // System.out.println("_____Computer Turn______");
        List<Integer> res = dice.throwTurn();
        this.computer_moves = res;
        List<Integer> ret = new ArrayList<>(dice.getValues(res));
        Timing.startTime = System.currentTimeMillis();
        // List<Integer> ret = new ArrayList<>();
        // ret.add(10);
        // depth 0 inf
        // depth 1 maximum(10) checked
        // depth 2 maximum(6)
        // depth 3 maximum(4) checked
        // depth 4 maximum(1)

        if (ret.size() < 2) {
            depth = 3;
        } else if (ret.size() < 6)
            depth = 2;
        else if (ret.size() < 7)
            depth = 1;

        // System.out.println(ret);
        Pair2 pp = maxMove(game, ret, 0);
        game = pp.first;
        // System.out.println("passedTime:");
        // System.out.println(Timing.passedTime());
        // System.out.println("ev:");
        // System.out.println(pp.second);

    }

    private void printUsage() {
        Term.color(Term.Color.Green, "Berjis Terminator AI v1.0\n\r");
        Term.color(Term.Color.DarkGrey, "Usage: \n\r");
        Term.color(Term.Color.DarkGrey, "\t*       : press the star to throw the dices\n\r");
        Term.color(Term.Color.DarkGrey, "\t+/-     : press plus or minus to switch between the pieces\n\r");
        Term.color(Term.Color.DarkGrey, "\t          if you are in the pieces mode, and switch between\n\r");
        Term.color(Term.Color.DarkGrey, "\t          the possible moves if you are in the moving mode\n\r");
        Term.color(Term.Color.DarkGrey, "\t*       : press * in the numpad to throw the dices\n\r");
        Term.color(Term.Color.DarkGrey, "\t[enter] : press enter to switch to the moving mode\n\r");
        Term.color(Term.Color.DarkGrey, "\t[space] : press space to make the computer play\n\r");
    }
}
