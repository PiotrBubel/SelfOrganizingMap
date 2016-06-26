import java.util.ArrayList;
import java.util.List;

import dataset.Dataset;
import maps.KMeans;
import maps.Kohonen2DAlgorithm;
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
        randomized.addAll(Utils.randomizePoints(100, 2 * min - 10, 2 * max - 10, 2 * min - 10, 2 * max - 10));
        randomized.addAll(Utils.randomizePoints(100, 2 * min + 10, 2 * max + 10, 2 * min + 10, 2 * max + 10));
        randomized.addAll(Utils.randomizePoints(100, 2 * min - 10, 2 * max + 10, 2 * min - 10, 2 * max + 10));

        boolean grafy = false;
        boolean obrazki = true;

        if (grafy) {
            KMeans kmeans = new KMeans();
            kmeans.runAlgorithm(randomized, 40, 9);


            NeuralGasAlgorithm.MIN_LAMBDA = 0.01;
            NeuralGasAlgorithm.START_LEARNING_RATE = 0.8d;
            NeuralGasAlgorithm.MIN_LEARNING_RATE = 0.03d;
            NeuralGasAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            NeuralGasAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

            NeuralGasAlgorithm ngas = new NeuralGasAlgorithm();
            ngas.runAlgorithm(randomized, 200, 200);
            //wzorce, epoki, neurony


            KohonenAlgorithm.MIN_LAMBDA = 0.01;
            KohonenAlgorithm.START_LEARNING_RATE = 0.08d;
            KohonenAlgorithm.MIN_LEARNING_RATE = 0.03d;
            KohonenAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            KohonenAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

            KohonenAlgorithm kohonen = new KohonenAlgorithm();
            kohonen.runAlgorithm(randomized, 200, 200);

            KohonenAlgorithm kohonen1 = new KohonenAlgorithm();
            kohonen1.runTwoPhaseAlgorithm(randomized, 140, 200);


            Kohonen2DAlgorithm.MIN_LAMBDA = 0.01;
            Kohonen2DAlgorithm.START_LEARNING_RATE = 0.08d;
            Kohonen2DAlgorithm.MIN_LEARNING_RATE = 0.03d;
            Kohonen2DAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            Kohonen2DAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

            Kohonen2DAlgorithm kohonen2d = new Kohonen2DAlgorithm();
            kohonen2d.runAlgorithm(randomized, 200, 20, 10);

            Kohonen2DAlgorithm kohonen2d2 = new Kohonen2DAlgorithm();
            kohonen2d2.runTwoPhaseAlgorithm(randomized, 140, 20, 10);
        }

        if (obrazki) {
            //ImageUtils.test();


            String inImage = "lena.jpg";
            int x = 40;
            int y = x;
            int epochs = 750;
            int neurons = 750;

            KMeans kmeans = new KMeans();
            kmeans.runAlgorithmOnImage(inImage, "_kmeans__e" + epochs + "_n" + neurons + ".jpg", epochs, x, y, neurons);


            KohonenAlgorithm.MIN_LAMBDA = 0.01;
            KohonenAlgorithm.START_LEARNING_RATE = 0.08d;
            KohonenAlgorithm.MIN_LEARNING_RATE = 0.03d;
            KohonenAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            KohonenAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;
            KohonenAlgorithm koh = new KohonenAlgorithm();
            koh.runAlgorithmOnImage(inImage, "_koh_e" + epochs + "_n" + neurons + ".jpg", epochs, x, y, neurons);
            System.out.println("koh");


            NeuralGasAlgorithm.MIN_LAMBDA = 0.01;
            NeuralGasAlgorithm.START_LEARNING_RATE = 0.8d;
            NeuralGasAlgorithm.MIN_LEARNING_RATE = 0.03d;
            NeuralGasAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            NeuralGasAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;
            NeuralGasAlgorithm gas = new NeuralGasAlgorithm();
            gas.runAlgorithmOnImage(inImage, "_gas__e" + epochs + "_n" + neurons + ".jpg", epochs, x, y, neurons);
            System.out.println("gas");


            Kohonen2DAlgorithm.MIN_LAMBDA = 0.01;
            Kohonen2DAlgorithm.START_LEARNING_RATE = 0.08d;
            Kohonen2DAlgorithm.MIN_LEARNING_RATE = 0.03d;
            Kohonen2DAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            Kohonen2DAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;
            Kohonen2DAlgorithm koh2d = new Kohonen2DAlgorithm();
            int n = (int) Math.sqrt(neurons);
            koh2d.runAlgorithmOnImage(inImage, "_koh2d_e" + epochs + "_n" + neurons + ".jpg", epochs, x, y, n, n);
            System.out.println("koh2");

        }
        System.out.println("..");
    }


}