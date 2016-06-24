package dataset;

/**
 * Created by Piotrek on 22.06.2016.
 */
public class Neuron extends Dataset {

    private double potential;

    public Neuron(int x) {
        super(x);
        potential = 1d;
    }

    public Neuron(double[] a) {
        super(a);
        potential = 1d;
    }

    public void decreasePotential(double i) {
        this.potential = potential + i;
    }

    public void rest(double i) {
        this.potential = potential - i;
    }

    public double potential() {
        return this.potential;
    }

    public void moveForward(Dataset destination, double step) {
        if (this.size() != destination.size()) {
            System.out.println("wrong destination in moveForward method, wrong dataset sizes");
            return;
        }
        for (int i = 0; i < size(); i++) {
            this.weights[i] = weights[i] + step * (destination.getValue(i) - weights[i]);
        }
    }

}
