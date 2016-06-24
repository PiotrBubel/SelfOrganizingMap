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

        double max = 15d;
        double min = -15d;
        Dataset.MAX_FIRST_VAL = max;
        Dataset.MIN_FIRST_VAL = min;

        List<Dataset> randomized = Utils.randomizePoints(100, min, max, min, max);
        //randomized.addAll(Utils.randomizePoints(500, -10, -15, -10, -15));
        //randomized.addAll(Utils.randomizePoints(500, 0, 8, 0, 8));
        //randomized.addAll(Utils.randomizePoints(50, 40, 50, 40, 50));
        //randomized.addAll(Utils.randomizePoints(5, -30, -20, -30, -20));

        KMeans kmeans = new KMeans();

        NeuralGasAlgorithm ngas = new NeuralGasAlgorithm();
        ngas.runAlgorithm(randomized, 40, 60);
        //wzorce, epoki, neurony

        KohonenAlgorithm kohonen = new KohonenAlgorithm();
        kohonen.runAlgorithm(randomized, 40, 60);

        KohonenAlgorithm kohonen1 = new KohonenAlgorithm();
        kohonen1.runTwoPhaseAlgorithm(randomized, 40, 60);
        //List<ClusteredDataset> clustered = kmeans.group(randomized, 16, 80);

        System.out.println("..");
    }
}