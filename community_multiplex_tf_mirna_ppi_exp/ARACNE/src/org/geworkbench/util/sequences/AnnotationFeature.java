package org.geworkbench.util.sequences;

/**
 * <p>Title: Bioworks</p>
 *
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 *
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 *
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class AnnotationFeature {

    private Object previous;
    private Object next;
    private int featureHitStart;
    private int featureHitEnd;
    private int sequenceHitStart;
    private int sequenceHitEnd;
    private int featureLength;
    private boolean featureDirection;
    private boolean featureActive;
    private double featureEValue;
    private String featureName;
    private String featureTag; /* or short name for displaying purpose */
    private String featureURL;

    public AnnotationFeature(Object prev, Object nxt, int hit_ffrom,
                             int hit_fto, int hit_sfrom, int hit_sto,
                             int fl, boolean dir, boolean act, double
                             fval, String fn, String ft, String furl  ) {
        previous=prev;
        next=nxt;
        featureHitStart=hit_ffrom;
        featureHitEnd=hit_fto;
        sequenceHitStart=hit_sfrom;
        sequenceHitEnd=hit_sto;
        featureLength=fl;
        featureDirection=dir;
        featureActive=act;
        featureEValue=fval;
        featureName=fn;
        featureTag=ft; /* or short name for displaying purpose */
        featureURL=furl;
    }

    /* Accessors */
    public Object getPrevious(){
        return previous;
    }
    public Object getNext(){
        return next;
    }

    public int getFeatureHitStart(){
        return featureHitStart;
    }
    public int getFeatureHitEnd(){
        return featureHitEnd;
    }
    public int getSequenceHitStart(){
        return sequenceHitStart;
    }
    public int getSequenceHitEnd(){
        return sequenceHitEnd;
    }
    public int getFeatureLength(){
        return featureLength;
    }
    public boolean getFeatureDirection(){
        return featureDirection;
    }
    public boolean getFeatureActive(){
        return featureActive;
    }
    public double getFeatureEValue(){
        return featureEValue;
    }
    public String getFeatureName(){
        return featureName;
    }
    public String getFeatureTag(){
        return featureTag;
    }
    public String getFeatureURL(){
        return featureURL;
    }

    /* Modifyers */
    public void setActive(){
        featureActive=true;
    }
    public void setInActive(){
        featureActive=false;
    }
    public void setPrevious(Object prev){
        previous=prev;
    }
    public void setNext(Object nxt){
        next=nxt;
    }
    public void adjustPos(int adj){
        featureHitStart+=adj;
        featureHitEnd+=adj;
        sequenceHitStart+=adj;
        sequenceHitEnd+=adj;
    }
}
