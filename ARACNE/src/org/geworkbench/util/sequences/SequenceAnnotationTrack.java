package org.geworkbench.util.sequences;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 * @author not attributable
 * @version 1.0
 */

public class SequenceAnnotationTrack {

    private AnnotationFeature first=null;
    private AnnotationFeature last=null;
    String annotationName=null;
    private int featuresNum=0;
    private int color_num;

    /**
     * Constructors
     */

    /**
     *  Use of this constructor will require separate use of initial function
     * */
    public SequenceAnnotationTrack() {
    }

    private AnnotationFeature getFeatureByNum(int n){
        AnnotationFeature sf=null;
        sf=first;
        int i=0;

        while(i<n){
            if(sf!=last){
                sf = (AnnotationFeature) sf.getNext();
                i++;
            } else {
                return null;
            }
        }
        return sf;
    }

    /**
     * Accessors
     */
    public String getAnnotationName() {
        return annotationName;
    }

    public int getFeatureNum() {
        return featuresNum;
    }

    public int getColorNum() {
        return color_num;
    }

    public int getFeatureHitStart(int n) {
        return (getFeatureByNum(n)).getFeatureHitStart();
    }

    public int getFeatureHitEnd(int n) {
        return (getFeatureByNum(n)).getFeatureHitEnd();
    }

    public int getSequenceHitStart(int n) {
        return (getFeatureByNum(n)).getSequenceHitStart();
    }

    public int getSequenceHitEnd(int n) {
        return (getFeatureByNum(n)).getSequenceHitEnd();
    }

    public int getFeatureLength(int n) {
        return (getFeatureByNum(n)).getFeatureLength();
    }

    public boolean getFeatureDirection(int n) {
        return (getFeatureByNum(n)).getFeatureDirection();
    }

    public boolean getFeatureActive(int n) {
        return (getFeatureByNum(n)).getFeatureActive();
    }

    public double getFeatureEValue(int n) {
        return (getFeatureByNum(n)).getFeatureEValue();
    }

    public String getFeatureName(int n) {
        return (getFeatureByNum(n)).getFeatureName();
    }

    public String getFeatureTag(int n) {
        return (getFeatureByNum(n)).getFeatureTag();
    }

    public String getFeatureURL(int n) {
        if(n>=featuresNum)return null;
        return (getFeatureByNum(n)).getFeatureURL();
    }

    /**
     * Modificators
     */
    public void setAnnotationName(String name) {
        annotationName = name;
    }

    public void setColorNum(int cn) {
        color_num = cn;
    }

    public void setFeatureActive(int n, boolean act) {
        if(act){
            (getFeatureByNum(n)).setActive();
        } else {
            (getFeatureByNum(n)).setInActive();
        }
    }

    public void adjustFeaturePos(int n, int adj){
        if(n>featuresNum)return;

        getFeatureByNum(n).adjustPos(adj);
    }

    public void addFeature(int hit_ffrom, int hit_fto, int hit_sfrom, int hit_sto, int fl, boolean dir, boolean act, double
                           fval, String fn, String ft, String furl ){
        AnnotationFeature naf=new AnnotationFeature(last, null, hit_ffrom, hit_fto, hit_sfrom, hit_sto,
                                                    fl, dir, act, fval, fn, ft, furl  );
        if(last!=null){
            last.setNext(naf);
        }
        last = naf;
        if(first==null){
            first = naf;
        }
        featuresNum++;
    }

    public void delFeatureByNum(int n){
        if(n>featuresNum)return;

        AnnotationFeature prev=(AnnotationFeature)(getFeatureByNum(n)).getPrevious();
        AnnotationFeature nxt=(AnnotationFeature)(getFeatureByNum(n)).getNext();

        if(prev==null){
            if(nxt==null){
                first=last=null;
            }else{
                nxt.setPrevious(null);
            }
        }else{
            if(nxt==null){
                prev.setNext(null);
            } else {
                prev.setNext(nxt);
                nxt.setPrevious(prev);
            }
        }
        featuresNum--;
    }

    public void delFeatureByNum(AnnotationFeature af){
        AnnotationFeature prev=(AnnotationFeature)af.getPrevious();
        AnnotationFeature nxt=(AnnotationFeature)af.getNext();

        if(prev==null){
            if(nxt==null){
                first=last=null;
            }else{
                nxt.setPrevious(null);
            }
        }else{
            if(nxt==null){
                prev.setNext(null);
            } else {
                prev.setNext(nxt);
                nxt.setPrevious(prev);
            }
        }
        featuresNum--;
    }

}
