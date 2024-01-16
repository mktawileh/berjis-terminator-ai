package com.berjis;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Dice {

    Random random = new Random();
    int r = 10000;
    int shakeh = 467;
    int dest = 1866;
    int banj = 369;
    int bara = 41;
    int two = 3110;
    int three = 2765;
    int four = 1382;
    double m = r;
    double[] poss = { shakeh / m, dest / m, banj / m, bara / m, two / m, three / m, four / m };
    int[] pre = { shakeh, dest, banj, bara, two, three, four };
    String[] names = { "shakeh", "dest", "banj", "bara", "two", "three", "four" };
    int[] downs_count = { 0, 1, 5, 6, 2, 3, 4 };
    int[][] values = { { 6 }, { 1, 10 }, { 1, 25 }, { 12 }, { 2 }, { 3 }, { 4 } };
    List<Integer> b = new ArrayList<>();

    public Dice() {
        for (int i = 1; i < 7; i++)
            pre[i] += pre[i - 1];
    }

    public List<String> getNames(List<Integer> throwRes) {
        List<String> ret = new ArrayList<>();
        for (int ind : throwRes)
            ret.add(names[ind]);
        return ret;
    }

    public List<Integer> getValues(List<Integer> throwRes) {
        List<Integer> ret = new ArrayList<>();
        for (int ind : throwRes) {
            for (int i = 0; i < values[ind].length; i++)
                ret.add(values[ind][i]);
        }
        return ret;
    }

    public int THROW() {
        int num = random.nextInt(r);
        int ret = -1;
        for (int i = 0; i < 7; i++) {
            if (num < pre[i]) {
                ret = i;
                break;
            }
        }
        return ret;
    }

    public List<Integer> throwTurn() {
        List<Integer> throwRes = new ArrayList<>();
        int ind = -1;
        do {
            ind = THROW();
            throwRes.add(ind);
        } while (ind < 4);
        return throwRes;
    }

    double sum = 0;

    // this is only called once.. only once!!
    public void gettingThem(int i, double curAcc, double acc, List<Pair> posScenarios) {
        if (i == 7) {
            if (b.size() > 0 && b.get(b.size() - 1) >= 4) {
                posScenarios.add(new Pair(new ArrayList<>(b), curAcc));
                sum += curAcc;
            }
            return;
        }
        if ((curAcc * poss[i] >= acc) && ((b.size() > 0 && b.get(b.size() - 1) < 4) || b.isEmpty())) {
            b.add(i);
            gettingThem(i, curAcc * poss[i], acc, posScenarios);
            b.remove(b.size() - 1);
        }
        gettingThem(i + 1, curAcc, acc, posScenarios);
    }

    public List<Pair> getAllPossibleScenarios(double acc) {
        sum = 0;
        List<Pair> posScenarios = new ArrayList<>();
        gettingThem(0, 1.0, acc, posScenarios);
        for (Pair x : posScenarios)
            x.second /= sum;
        return posScenarios;

    }

    public static void main(String[] args) {
        // List<ArrayList<Integer>> b = new ArrayList<>();
        // b.add(new ArrayList<>());
        // b.get(0).add(1);
        // List<ArrayList<Integer>> c = new ArrayList<>();
        // for (List<Integer> li : b) {
        // c.add(new ArrayList<>(li));
        // }
        // c.get(0).clear();

        // System.out.println(b);
        // System.out.println(c);
        Dice d = new Dice();
        List<Pair> posScenarios = new ArrayList<>(
                d.getAllPossibleScenarios(0.01));
        double sum2 = 0;
        for (Pair x : posScenarios) {
            System.out.print(d.getValues(x.first));
            System.out.println(" : " + x.second);
            sum2 += x.second;
        }
        System.out.println("here");
        System.out.println(d.sum);
        System.out.println("there");
        System.out.println(sum2);
    }

}
