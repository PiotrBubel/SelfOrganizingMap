package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dataset.ClusteredDataset;
import dataset.Dataset;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class Utils {

    public static double power(double x) {
        return x * x;
    }

    public static List<Dataset> randomizePoints(int howMuch, double minX, double maxX, double minY, double maxY) {
        List<Dataset> datasetList = new ArrayList<Dataset>();
        Random random = new Random();
        for (int i = 0; i < howMuch; i++) {
            double x = minX + (maxX - minX) * random.nextDouble();
            double y = minY + (maxY - minY) * random.nextDouble();
            datasetList.add(new Dataset(new double[]{x, y}));
        }

        return datasetList;
    }

    public static List<List<Dataset>> splitClusteredPoints(List<ClusteredDataset> clustered) {
        List<List<Dataset>> splitted = new ArrayList<>();
        int groups = 0;
        for (ClusteredDataset cpoint : clustered) {
            if (groups < cpoint.getGroup()) {
                groups = cpoint.getGroup();
            }
        }
        for (int i = 0; i <= groups; i++) {
            splitted.add(new ArrayList<>());
        }
        for (ClusteredDataset cpoint : clustered) {
            splitted.get(cpoint.getGroup()).add(cpoint);
        }

        return splitted;
    }

    public static List<List<ClusteredDataset>> splitClusteredPoints2(List<ClusteredDataset> clustered) {
        List<List<ClusteredDataset>> splitted = new ArrayList<>();
        int groups = 0;
        for (ClusteredDataset cpoint : clustered) {
            if (groups < cpoint.getGroup()) {
                groups = cpoint.getGroup();
            }
        }
        for (int i = 0; i <= groups; i++) {
            splitted.add(new ArrayList<>());
        }
        for (ClusteredDataset cpoint : clustered) {
            splitted.get(cpoint.getGroup()).add(cpoint);
        }

        return splitted;
    }


    public static void runGnuplotScript(String scriptName) throws IOException {
        Process gnuplot;
        gnuplot = Runtime.getRuntime().exec("gnuplot " + scriptName);
        try {
            gnuplot.waitFor();
            System.out.println("executed script " + scriptName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
