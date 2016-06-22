package dataset;

import utils.Utils;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class Dataset {

    private double[] values;

    public Dataset(double[] x) {
        this.values = x;
    }

    public Dataset() {
        values = new double[]{0d, 0d};
    }

    public double[] getValues() {
        return values;
    }

    public double getValue(int index) {
        return values[index];
    }

    public int size() {
        return values.length;
    }

    /**
     * Euclidean distance //i am sure it works while datasets are points
     */
    public double distanceTo(Dataset other) {
        if (this.size() != other.size()) {
            System.out.println("wrong dataset in distanceTo");
            return Double.MAX_VALUE;
        }
        double sum = 0d;
        for (int i = 0; i < size(); i++) {
            sum = sum + Utils.power(this.getValue(i) - other.getValue(i));
        }
        return Math.sqrt(sum);
    }

}
