package org.geworkbench.components.pathwaydecoder.networks;

public class GeneInfo implements Comparable {
    String accession;
    String geneName;

    public GeneInfo() {
    }

    public GeneInfo(String geneName) {
        this.geneName = geneName;
    }

    public String getGeneName() {
        return geneName;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public String getAccession() {
        return accession;
    }

    public int hashCode() {
        return 1;
    }

    public boolean equals(Object obj) {
        if (obj instanceof GeneInfo) {
            GeneInfo gi = (GeneInfo) obj;
            if (geneName != null) {
                if (geneName.equalsIgnoreCase(gi.getGeneName())) {
                    return true;
                }
            }

            if (accession != null) {
                if (accession.equalsIgnoreCase(gi.getAccession())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * compareTo
     *
     * @param o Object
     * @return int
     */
    public int compareTo(Object o) {
        if (equals(o)) {
            return 1;
        } else {
            return 0;
        }
    }

}
