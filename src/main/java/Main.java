import java.util.ArrayList;
import java.util.List;

import dataset.Dataset;
import maps.KMeans;
import maps.Kohonen2DAlgorithm;
import maps.KohonenAlgorithm;
import maps.NeuralGasAlgorithm;
import utils.ImageUtils;
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
        randomized.addAll(Utils.randomizePoints(1, 2 * min - 10, 2 * max - 10, 2 * min - 10, 2 * max - 10));
        randomized.addAll(Utils.randomizePoints(1, 2 * min + 10, 2 * max + 10, 2 * min + 10, 2 * max + 10));
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
            ngas.runAlgorithm(randomized, 200, 150);
            //wzorce, epoki, neurony


            KohonenAlgorithm.MIN_LAMBDA = 0.01;
            KohonenAlgorithm.START_LEARNING_RATE = 0.08d;
            KohonenAlgorithm.MIN_LEARNING_RATE = 0.03d;
            KohonenAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            KohonenAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

            KohonenAlgorithm kohonen = new KohonenAlgorithm();
            kohonen.runAlgorithm(randomized, 200, 150);

            KohonenAlgorithm kohonen1 = new KohonenAlgorithm();
            kohonen1.runTwoPhaseAlgorithm(randomized, 80, 150);


            Kohonen2DAlgorithm.MIN_LAMBDA = 0.01;
            Kohonen2DAlgorithm.START_LEARNING_RATE = 0.08d;
            Kohonen2DAlgorithm.MIN_LEARNING_RATE = 0.03d;
            Kohonen2DAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            Kohonen2DAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

            Kohonen2DAlgorithm kohonen2d = new Kohonen2DAlgorithm();
            kohonen2d.runAlgorithm(randomized, 200, 15, 15);

            Kohonen2DAlgorithm kohonen2d2 = new Kohonen2DAlgorithm();
            kohonen2d2.runTwoPhaseAlgorithm(randomized, 140, 15, 15);
        }

        if (obrazki) {
            ImageUtils.test();

            String inImage = "image.jpg";
            int x = 20;
            int y = x;
            int epochs = 2000;

            KohonenAlgorithm.MIN_LAMBDA = 0.01;
            KohonenAlgorithm.START_LEARNING_RATE = 0.5d;
            KohonenAlgorithm.MIN_LEARNING_RATE = 0.05d;
            KohonenAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            KohonenAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;
            KohonenAlgorithm koh = new KohonenAlgorithm();
            koh.runAlgorithmOnImage(inImage, "_koh_chunks.png", epochs, x, y);
            System.out.println("koh");


            NeuralGasAlgorithm.MIN_LAMBDA = 0.01;
            NeuralGasAlgorithm.START_LEARNING_RATE = 0.5d;
            NeuralGasAlgorithm.MIN_LEARNING_RATE = 0.01d;
            NeuralGasAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            NeuralGasAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;
            NeuralGasAlgorithm gas = new NeuralGasAlgorithm();
            gas.runAlgorithmOnImage(inImage, "_gas_chunks.png", epochs, x, y);
            //gas.runAlgorithmOnImage("image2.jpg", "_gas_chunks2.png", 2000, 5, 5);

            System.out.println("gas");


            Kohonen2DAlgorithm.MIN_LAMBDA = 0.01;
            Kohonen2DAlgorithm.START_LEARNING_RATE = 0.5d;
            Kohonen2DAlgorithm.MIN_LEARNING_RATE = 0.05d;
            Kohonen2DAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            Kohonen2DAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;
            Kohonen2DAlgorithm koh2d = new Kohonen2DAlgorithm();
            koh2d.runAlgorithmOnImage(inImage, "_koh2d_chunks.png", epochs, x, y);
            System.out.println("koh2");

        }
        System.out.println("..");
    }


}