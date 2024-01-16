package com.berjis;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.TreeSet;

public class BerjisGame implements Comparable<BerjisGame> {
    // computer pieces:[0,4,22,71]
    // human pieces:[38,18,2,-1][]
    // _____Computer Turn______
    boolean[] commonRoad = new boolean[68];
    int[] protectedCells = { 5, 16, 22, 33, 39, 50, 56, 67 };
    // int[][] P = { { 0, 4, 22, 71 }, { 38, 18, 2, -1 } }; // from 0 to 83 is
    // the winning place
    int[][] P = { { -1, -1, -1, -1 }, { -1, -1, -1, -1 } }; // from 0 to 83 is
    int[][] PLM = { { -1, -1, -1, -1 }, { -1, -1, -1, -1 } }; // PLM stands for player last moves
    List<ArrayList<Integer>> b = new ArrayList<>();
    boolean enough = false;

    public BerjisGame() {
        for (int i = 0; i < 4; i++) {
            b.add(new ArrayList<Integer>());
        }
        for (int ind : protectedCells)
            commonRoad[ind] = true;
    }

    public BerjisGame(BerjisGame game) {
        this();
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 4; j++) {
                PLM[i][j] = game.P[i][j];
                P[i][j] = game.P[i][j];
            }

    }

    @Override
    public int compareTo(BerjisGame otherGame) {
        String thisListString = Arrays.deepToString(P);
        String otherListString = Arrays.deepToString(otherGame.P);

        return thisListString.compareTo(otherListString);
    }

    public boolean isWin(char type) {
        int ind = type - 'A';
        boolean win = true;
        for (int i = 0; i < 4; i++)
            if (P[ind][i] < 83)
                win = false;
        return win;
    }

    public boolean isFinished() {
        return isWin('A') || isWin('B');
    }

    public int fromPosToRoad(int type, int pos) {
        if (pos < 7 || pos > 75)
            return -1;
        if (type == 0)
            return (pos - 5) % 68;
        else
            return (pos + 29) % 68;
    }

    public boolean canMove(char type, int i, int step) {
        int ind = type - 'A';
        boolean can = true;
        if ((P[ind][i] + step) > 83)
            can = false;
        if ((P[ind][i] == -1) && (step != 1))
            can = false;
        int roadInd = fromPosToRoad(ind, P[ind][i] + step);
        if ((roadInd != -1) && commonRoad[roadInd])
            for (int ii = 0; ii < 2; ii++)
                if (ii != ind)
                    for (int j = 0; j < 4; j++) {
                        int val = fromPosToRoad(ii, P[ii][j]);
                        if (val == roadInd)
                            can = false;
                    }

        return can;
    }

    public void move(char type, int i, int step) {
        int ind = type - 'A';
        P[ind][i] += step;
        int roadInd = fromPosToRoad(ind, P[ind][i]);
        if (roadInd != -1)
            for (int ii = 0; ii < 2; ii++)
                if (ii != ind)
                    for (int j = 0; j < 4; j++) {
                        int val = fromPosToRoad(ii, P[ii][j]);
                        if (val == roadInd && !commonRoad[roadInd]) {
                            P[ii][j] = -1; // kill it!
                        }
                    }
    }

    public void workingOnIt(char type, List<Integer> throwRes, TreeSet<BerjisGame> nextBoards) {

        BerjisGame nextGame = new BerjisGame(this);
        boolean imp = false;
        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < b.get(j).size(); k++) {
                if (nextGame.canMove(type, j, b.get(j).get(k))) {
                    nextGame.move(type, j, b.get(j).get(k));
                } else
                    imp = true;
            }
        }
        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < throwRes.size(); k++) {
                if (nextGame.canMove(type, j, throwRes.get(k)))
                    imp = true;
            }
        }
        if (!imp) {
            Arrays.sort(nextGame.P[type - 'A']);
            nextBoards.add(nextGame);
            if (Timing.passedTime() >= Timing.threshold && !nextBoards.isEmpty()) {
                enough = true;
            }
        }
        if (!enough)
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < throwRes.size(); k++) {
                    List<Integer> temp = new ArrayList<>(throwRes);
                    b.get(j).add(throwRes.get(k));
                    temp.remove(k);
                    workingOnIt(type, temp, nextBoards);
                    b.get(j).remove(b.get(j).size() - 1);

                }
            }

    }

    public TreeSet<BerjisGame> getAllNextMoves(List<Integer> throwRes, char type) {
        TreeSet<BerjisGame> nextBoards = new TreeSet<>();
        workingOnIt(type, throwRes, nextBoards);
        // System.out.println("important:");
        // System.out.println(nextBoards.size());
        // for (BerjisGame bg : nextBoards)
        // System.out.println(Arrays.toString(bg.P[0]));
        return nextBoards;
    }

    public int evaluate(char type) {
        int ind = type - 'A';
        int mine = 0;
        int sumMine = 0;
        int his = 0;
        int sumHis = 0;
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 4; j++) {
                if (ind == i && P[i][j] > -1) {
                    sumMine += P[i][j];
                    mine++;
                } else if (P[i][j] > -1) {
                    sumHis += P[i][j];
                    his++;
                }
            }
        // return mine * sumMine - his * sumHis;
        // return mine - his;
        // return sumMine - sumHis;
        return sumMine + 10 * mine - (sumHis + 10 * his);
    }

    public static void main(String[] args) {

    }
}
