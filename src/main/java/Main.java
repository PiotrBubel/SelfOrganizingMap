import java.util.ArrayList;
import java.util.List;

import dataset.Dataset;
import maps.KMeans;
import maps.KohonenAlgorithm;
import maps.NeuralGasAlgorithm;
import utils.Utils;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world");

        double max = 1d;
        double min = -1d;
        Dataset.MAX_FIRST_VAL = max;
        Dataset.MIN_FIRST_VAL = min;

        List<Dataset> randomized = new ArrayList<>();
        //randomized.addAll(Utils.randomizePoints(250, 2 * min - 10, 2 * max - 10, 2 * min - 10, 2 * max - 10));
        //randomized.addAll(Utils.randomizePoints(250, 2 * min + 10, 2 * max + 10, 2 * min + 10, 2 * max + 10));
        //randomized.addAll(Utils.randomizePoints(100, min-1, max+1, min-1, max+1));
        randomized.addAll(Utils.randomizePoints(100, min, max, min, max));


        //randomized.addAll(Utils.randomizePoints(500, 0, 8, 0, 8));
        //randomized.addAll(Utils.randomizePoints(50, 40, 50, 40, 50));
        //randomized.addAll(Utils.randomizePoints(5, -30, -20, -30, -20));

        KMeans kmeans = new KMeans();
        kmeans.runAlgorithm(randomized, 40, 9);


        NeuralGasAlgorithm.MIN_LAMBDA = 0.01;
        NeuralGasAlgorithm.START_LEARNING_RATE = 0.5d;
        NeuralGasAlgorithm.MIN_LEARNING_RATE = 0.1d;
        NeuralGasAlgorithm.ENABLE_NEURON_POTENTIAL = true;
        NeuralGasAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

        NeuralGasAlgorithm ngas = new NeuralGasAlgorithm();
        ngas.runAlgorithm(randomized, 100, 150);
        //wzorce, epoki, neurony


        KohonenAlgorithm.MIN_LAMBDA = 0.01;
        KohonenAlgorithm.START_LEARNING_RATE = 0.05d;
        KohonenAlgorithm.MIN_LEARNING_RATE = 0.005d;
        KohonenAlgorithm.ENABLE_NEURON_POTENTIAL = true;
        KohonenAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

        KohonenAlgorithm kohonen = new KohonenAlgorithm();
        kohonen.runAlgorithm(randomized, 75, 150);

        KohonenAlgorithm kohonen1 = new KohonenAlgorithm();
        kohonen1.runTwoPhaseAlgorithm(randomized, 75, 150);


        //List<ClusteredDataset> clustered = kmeans.runAlgorithm(randomized, 16, 80);

        System.out.println("..");
    }
}