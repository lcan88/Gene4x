package org.geworkbench.util.sequences;



/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 * @author not attributable
 * @version 1.0
 */

public class SequenceAnnotation {

  private int seqSegmentStart;
  private int seqSegmentEnd;
  private int TrackNum;
  private int MaxTrackNum=500;
  private SequenceAnnotationTrack[] annoTrack=new SequenceAnnotationTrack[MaxTrackNum];
  private boolean[] activeTrack=new boolean[MaxTrackNum];

  /**
   * Constructors
   */
  public SequenceAnnotation() {
    TrackNum=0;
  }
    /**
     * Accessors
     */
    public int getSeqSegmentStart(){
       return seqSegmentStart;
     }

     public int getSeqSegmentEnd(){
       return seqSegmentEnd;
     }

    public SequenceAnnotationTrack getAnnotationTrack(int n){
      return annoTrack[n]; }

    public int getAnnotationTrackNum(){
      return TrackNum; }

    public int getActiveAnnoTrackNum(){
        int i, ret;

        for(i=0, ret=0;i<TrackNum;i++){
            if(activeTrack[i])ret++;
        }

        return ret;
    }

  public boolean getAnnoTrackActive(int n){
    return activeTrack[n];
  }

    /**
     * Modificators
     */
    public void setSeqSegmentStart(int sss){
      seqSegmentStart=sss;
    }

    public void setSeqSegmentEnd(int sse){
      seqSegmentEnd=sse;
    }

    public void addAnnoTrack(SequenceAnnotationTrack sat){
      annoTrack[TrackNum++]=sat;
  }

  public void setAnnoTrackActive(int n, boolean b){
      activeTrack[n]=b;
  }

  public void addAdjustZeroAnnoTrack(SequenceAnnotationTrack sat){
      int j;

      annoTrack[TrackNum++]=sat;
      for(j=0;j<sat.getFeatureNum();j++){
          sat.adjustFeaturePos(j,seqSegmentStart);
      }
  }

/*********************************************************/
public void SetColors(String[] aKeys, int num){
      int i, j;
      for (i = 0; i < TrackNum; i++) {
          for (j = 0; j < num; j++) {
              if (aKeys[j].compareTo(annoTrack[i].getAnnotationName())!=-1){
                  annoTrack[i].setColorNum(j);
                  break;
              }
          }
      }
  }

    /**
     * checkAnnoKeys
     *
     * @param AnnoKeys String[]
     * @param NumAnnoKeys int
     */
    public int checkAnnoKeys(String[] AnnoKeys, int NumAnnoKeys) {
        int i,j;
        boolean is_new=true;

        for(i=0;i<TrackNum;i++){
            is_new=true;
            for(j=0;j<NumAnnoKeys;j++){
                if ( (annoTrack[i].getAnnotationName()).compareTo(AnnoKeys[j]) ==
                    0) {
                    is_new = false;
                    break;
                }
            }
            if(is_new){
                AnnoKeys[NumAnnoKeys]=new String(annoTrack[i].getAnnotationName());
                NumAnnoKeys++;
            }
        }
        return NumAnnoKeys;
    }

    /**
     * activateAll
     */
    public void activateAll() {
        int i;

        for(i=0;i<TrackNum;i++){
            activeTrack[i]=true;
        }
    }
}


