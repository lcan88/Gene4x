package org.geworkbench.util.pathwaydecoder.mutualinformation;

import distributions.NormalDistribution;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class MutualInformation {
    boolean[] bins = null;
    static int xx = 330;
    static double sigma = 21;
    static double nf = 0.0;
    static int step = 1;
    int xi = 0;
    int yi = 0;
    double[][] xyMap = null;
    NormalDistribution normal = new NormalDistribution(0, 3);

    public MutualInformation(int xMax, int yMax) {
        xi = xMax;
        yi = yMax;
        xyMap = new double[xi][];
        bins = new boolean[xi];
        for (int i = 0; i < xi; i++) {
            xyMap[i] = new double[yi];
            bins[i] = false;
        }
        getNorm();
    }

    private void getNorm() {
        nf = 0.0;
        nf += normal.getDensity(0);
        for (int dx = 1; dx < sigma * 3; dx++) {
            for (int dy = 1; dy < sigma * 3; dy++) {
                double r = Math.sqrt(dx * dx + dy * dy);
                nf += 4.0 * normal.getDensity(r);
            }
        }
    }

    public void add(int x, int y) {
        double p = normal.getDensity(0) / nf;
        xyMap[x][y] += p;
        for (int dx = 1; dx < Math.min(sigma * 3, xi); dx++) {
            for (int dy = 1; dy < Math.min(sigma * 3, yi); dy++) {
                double r = Math.sqrt(dx * dx + dy * dy);
                p = normal.getDensity(r) / nf;
                int x0 = (x + dx) % xi;
                int y0 = (y + dy) % yi;
                xyMap[x0][y0] += p;
                y0 = (y - dy + yi) % yi;
                xyMap[x0][y0] += p;
                x0 = (x - dx + xi) % xi;
                xyMap[x0][y0] += p;
                y0 = (y + dy) % yi;
                xyMap[x0][y0] += p;
            }
        }
    }

    public double norm() {
        double norm = 0.0;
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < yi; j++) {
                norm += xyMap[i][j];
            }
        }
        return norm;
    }

    public double getHx(int n) {
        double hx = 0;
        double q = 0;
        for (int i = 0; i < xi - step; i += step) {
            double p = 0.0;
            for (int di = 0; di < step; di++) {
                for (int j = 0; j < yi; j++) {
                    p += (double) xi * xyMap[i + di][j] / (double) n;
                }
            }
            if (p > 0) {
                hx += -p * Math.log(p) / (double) xi;
                q += p / (double) xi;
            }
        }
        return hx;
    }

    public double getHy(int n) {
        double hy = 0;
        double q = 0;
        for (int j = 0; j < yi - step; j += step) {
            double p = 0.0;
            for (int dj = 0; dj < step; dj++) {
                for (int i = 0; i < xi; i++) {
                    p += (double) yi * xyMap[i][j + dj] / (double) n;
                }
            }
            if (p > 0) {
                hy += -p * Math.log(p) / (double) yi;
                q += p / (double) yi;
            }
        }
        return hy;
    }

    public double getHxy(int n) {
        double hxy = 0;
        double q = 0;
        for (int i = 0; i < xi - step; i += step) {
            for (int j = 0; j < yi - step; j += step) {
                double p = 0;
                for (int di = 0; di < step; di++) {
                    for (int dj = 0; dj < step; dj++) {
                        p += xi * yi * xyMap[i + di][j + dj] / (double) n;
                    }
                }
                if (p > 0) {
                    hxy += -p * Math.log(p) / (double) yi / (double) xi;
                    q += p / (double) yi / (double) xi;
                }
            }
        }
        return hxy;
    }

    public static void main(String[] args) {
        MutualInformation m = new MutualInformation(xx, xx);
        for (int i = 0; i < xx; i++) {
            int j = (int) (Math.random() * xx);
            while (m.bins[j]) {
                j = (int) (Math.random() * xx);
            }
            m.bins[j] = true;
            m.add(i, j);
        }
        double norm = m.norm();
        double hx = m.getHx(xx);
        double hy = m.getHy(xx);
        double hxy = m.getHxy(xx);
        System.out.println("Norm: " + norm);
        System.out.println("H(x): " + hx);
        System.out.println("H(y): " + hy);
        System.out.println("H(xy): " + hxy);
        System.out.println("MI: " + (hx + hy - hxy));
        double mi0 = (hx + hy - hxy) / (hx + hy);
        System.out.println("MI: " + mi0);


        m = new MutualInformation(xx, xx);
        for (int i = 0; i < xx; i++) {
            m.add(i, i);
        }
        norm = m.norm();
        hx = m.getHx(xx);
        hy = m.getHy(xx);
        hxy = m.getHxy(xx);
        System.out.println("Norm: " + norm);
        System.out.println("H(x): " + hx);
        System.out.println("H(y): " + hy);
        System.out.println("H(xy): " + hxy);
        System.out.println("MI: " + (hx + hy - hxy));
        double mi1 = (hx + hy - hxy) / (hx + hy);
        System.out.println("MI: " + mi1);
        System.out.println("Ratio: " + (mi1 / mi0));
    }
}