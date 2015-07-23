package org.geworkbench.util.promoter.pattern;

import org.geworkbench.bison.datastructure.complex.pattern.DSPattern;

public class PatternDisplay <T,R> {
    DSPattern<T, R> pt = null;
    Display dis = null;

    public PatternDisplay(DSPattern<T, R> pt, Display dis) {
        this.pt = pt;
        this.dis = dis;
    }

    public DSPattern<T, R> getPt() {
        return pt;
    }

    public void setDis(Display dis) {
        this.dis = dis;
    }

    public void setPt(DSPattern<T, R> pt) {
        this.pt = pt;
    }

    public Display getDis() {
        return dis;
    }

    public String toString() {
        return pt.toString();
    }
}
