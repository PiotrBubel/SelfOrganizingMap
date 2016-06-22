import java.util.List;

import algorithms.KMeansAlgorithm;
import dataset.ClusteredDataset;
import dataset.Dataset;
import utils.Utils;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class Main {

    public static void main(String[] args){
        System.out.println("Hello world");

        List<Dataset> randomized = Utils.randomizePoints(5000, 0, 15, 0, 15);
        //randomized.addAll(Utils.randomizePoints(500, -10, -15, -10, -15));
        //randomized.addAll(Utils.randomizePoints(500, 0, 8, 0, 8));
        //randomized.addAll(Utils.randomizePoints(50, 40, 50, 40, 50));
        //randomized.addAll(Utils.randomizePoints(5, -30, -20, -30, -20));

        KMeansAlgorithm kmeans = new KMeansAlgorithm();

        List<ClusteredDataset> clustered = kmeans.group(randomized, 16, 80);
        System.out.println("..");
    }
}