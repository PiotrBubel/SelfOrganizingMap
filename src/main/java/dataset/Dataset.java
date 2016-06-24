package dataset;

import java.util.Random;

import utils.Utils;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class Dataset {

    public static double MIN_FIRST_VAL = -15d;
    public static double MAX_FIRST_VAL = 15d;

    protected double[] weights;

    public Dataset(double[] x) {
        this.weights = x;
    }

    public Dataset() {
        weights = new double[]{0d, 0d};
    }

    public Dataset(int x) {
        weights = new double[x];
        Random r = new Random();
        for (int i = 0; i < size(); i++) {
            weights[i] = Dataset.MIN_FIRST_VAL + (Dataset.MAX_FIRST_VAL - Dataset.MIN_FIRST_VAL) * r.nextDouble();
        }
    }

    public double[] getWeights() {
        return weights;
    }

    public double getValue(int index) {
        return weights[index];
    }

    public int size() {
        return weights.length;
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

    public Neuron toNeuron() {
        return new Neuron(this.weights);
    }

}
