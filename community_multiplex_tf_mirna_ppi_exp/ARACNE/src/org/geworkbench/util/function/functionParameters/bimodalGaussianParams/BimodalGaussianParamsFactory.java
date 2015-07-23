package org.geworkbench.util.function.functionParameters.bimodalGaussianParams;

public class BimodalGaussianParamsFactory {
    public BimodalGaussianParamsFactory() {
    }

    public static BimodalGaussianParamsBase getParams(String name) {
        if ("BimodalGaussianParams1".equals(name)) {
            return new BimodalGaussianParams1();
        } else if ("BimodalGaussianParams2".equals(name)) {
            return new BimodalGaussianParams2();
        } else if ("BimodalGaussianParams3".equals(name)) {
            return new BimodalGaussianParams3();
        } else if ("BimodalGaussianParams4".equals(name)) {
            return new BimodalGaussianParams4();
        } else {
            return null;
        }
    }
}
