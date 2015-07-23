package org.geworkbench.util;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;

public abstract class MatrixModel {
    protected DSMicroarraySetView view;

    protected MatrixModel(DSMicroarraySetView data) {
        this.view = data;
    }

    public abstract DoubleIterator getRow(int i);

    public abstract int rows();

    private class ValueIteratorForGene implements DoubleIterator {
        private int cursor = 0;
        private int gene;

        public ValueIteratorForGene(int i) {
            if (i >= view.markers().size()) throw new IndexOutOfBoundsException();
            gene = i;
        }

        public boolean hasNext() {
            return cursor != view.items().size();
        }

        public double next() {
            return view.getValue(gene, cursor++);
        }

    }

    private class ValueIteratorForMicroarray implements DoubleIterator {
        private int cursor = 0;
        private int microarray;

        public ValueIteratorForMicroarray(int i) {
            if (i >= view.items().size()) throw new IndexOutOfBoundsException();
            microarray = i;
        }

        public boolean hasNext() {
            return cursor != view.markers().size();
        }

        public double next() {
            return view.getValue(cursor++, microarray);
        }

    }

    public class Gene extends MatrixModel {
        public Gene(DSMicroarraySetView data) {
            super(data);
        }

        public DoubleIterator getRow(int i) {
            return new ValueIteratorForGene(i);
        }

        public int rows() {
            return view.markers().size();
        }

    }

    public class Microarray extends MatrixModel {
        public Microarray(DSMicroarraySetView data) {
            super(data);
        }

        public DoubleIterator getRow(int i) {
            return new ValueIteratorForMicroarray(i);
        }

        public int rows() {
            return view.items().size();
        }
    }

}
