package org.geworkbench.components.pathwaydecoder.networks;

public class GeneTarget extends GeneInfo {
    float value;

    public GeneTarget() {
        super();
    }

    public GeneTarget(String geneName) {
        super(geneName);
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
