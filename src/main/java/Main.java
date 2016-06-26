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

            NeuralGasAlgorithm.MIN_LAMBDA = 0.01;
            NeuralGasAlgorithm.START_LEARNING_RATE = 0.8d;
            NeuralGasAlgorithm.MIN_LEARNING_RATE = 0.03d;
            NeuralGasAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            NeuralGasAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

            KohonenAlgorithm.MIN_LAMBDA = 0.01;
            KohonenAlgorithm.START_LEARNING_RATE = 0.08d;
            KohonenAlgorithm.MIN_LEARNING_RATE = 0.03d;
            KohonenAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            KohonenAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

            Kohonen2DAlgorithm.MIN_LAMBDA = 0.01;
            Kohonen2DAlgorithm.START_LEARNING_RATE = 0.08d;
            Kohonen2DAlgorithm.MIN_LEARNING_RATE = 0.03d;
            Kohonen2DAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            Kohonen2DAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

            int epochs = 100;
            int neurons = 750;
            runAllAlgorithmsOnDatasets(randomized, epochs, neurons);
        }

        if (obrazki) {
            //ImageUtils.test();

            KohonenAlgorithm.MIN_LAMBDA = 0.01;
            KohonenAlgorithm.START_LEARNING_RATE = 0.08d;
            KohonenAlgorithm.MIN_LEARNING_RATE = 0.03d;
            KohonenAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            KohonenAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

            NeuralGasAlgorithm.MIN_LAMBDA = 0.01;
            NeuralGasAlgorithm.START_LEARNING_RATE = 0.8d;
            NeuralGasAlgorithm.MIN_LEARNING_RATE = 0.03d;
            NeuralGasAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            NeuralGasAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

            Kohonen2DAlgorithm.MIN_LAMBDA = 0.01;
            Kohonen2DAlgorithm.START_LEARNING_RATE = 0.08d;
            Kohonen2DAlgorithm.MIN_LEARNING_RATE = 0.03d;
            Kohonen2DAlgorithm.ENABLE_NEURON_POTENTIAL = false;
            Kohonen2DAlgorithm.POTENTIAL_DECRASE_RATE = 0.9d;

            String inImage = "photo.jpg";
            //na ile kolumn i wierszy ma byc podzielony obrazek
            int x = 32;
            int y = 64;
            int epochs = 1000;
            int neurons = 1000;
            runAllAlgorithmsOnPicture(inImage, epochs, neurons, x, y);

            x = 32;
            y = 64;
            epochs = 1000;
            neurons = 250;
            runAllAlgorithmsOnPicture(inImage, epochs, neurons, x, y);

            x = 16;
            y = 32;
            epochs = 1000;
            neurons = 1000;
            runAllAlgorithmsOnPicture(inImage, epochs, neurons, x, y);

        }
        System.out.println("..");
    }

    private static void runAllAlgorithmsOnPicture(String inImage, int epochs, int neurons, int x, int y) {
        KMeans kmeans = new KMeans();
        kmeans.runAlgorithmOnImage(inImage, "_kmeans__e" + epochs + "_n" + neurons + "_x" + x + "_y" + y + ".jpg", epochs, x, y, neurons);
        System.out.println("Finished _kmeans__e" + epochs + "_n" + neurons + "_x" + x + "_y" + y + ".jpg");

        KohonenAlgorithm koh = new KohonenAlgorithm();
        koh.runAlgorithmOnImage(inImage, "_koh_e" + epochs + "_n" + neurons + "_x" + x + "_y" + y + ".jpg", epochs, x, y, neurons);
        System.out.println("Finished _koh_e" + epochs + "_n" + neurons + "_x" + x + "_y" + y + ".jpg");

        NeuralGasAlgorithm gas = new NeuralGasAlgorithm();
        gas.runAlgorithmOnImage(inImage, "_gas__e" + epochs + "_n" + neurons + "_x" + x + "_y" + y + ".jpg", epochs, x, y, neurons);
        System.out.println("Finished _gas__e" + epochs + "_n" + neurons + "_x" + x + "_y" + y + ".jpg");

        Kohonen2DAlgorithm koh2d = new Kohonen2DAlgorithm();
        int n = (int) Math.sqrt(neurons);
        koh2d.runAlgorithmOnImage(inImage, "_koh2d_e" + epochs + "_n" + neurons + "_x" + x + "_y" + y + ".jpg", epochs, x, y, n, n);
        System.out.println("Finished _koh2d_e" + epochs + "_n" + neurons + "_x" + x + "_y" + y + ".jpg");
    }


    private static void runAllAlgorithmsOnDatasets(List<Dataset> randomized, int epochs, int neurons) {
        KMeans kmeans = new KMeans();
        kmeans.runAlgorithm(randomized, epochs, neurons);

        NeuralGasAlgorithm ngas = new NeuralGasAlgorithm();
        ngas.runAlgorithm(randomized, epochs, neurons);
        //wzorce, epoki, neurony


        KohonenAlgorithm kohonen = new KohonenAlgorithm();
        kohonen.runAlgorithm(randomized, epochs, neurons);

        KohonenAlgorithm kohonen1 = new KohonenAlgorithm();
        kohonen1.runTwoPhaseAlgorithm(randomized, epochs, neurons);

        int n = (int) Math.sqrt(neurons);

        Kohonen2DAlgorithm kohonen2d = new Kohonen2DAlgorithm();
        kohonen2d.runAlgorithm(randomized, epochs, n, n);

        Kohonen2DAlgorithm kohonen2d2 = new Kohonen2DAlgorithm();
        kohonen2d2.runTwoPhaseAlgorithm(randomized, epochs, n, n);
    }

}