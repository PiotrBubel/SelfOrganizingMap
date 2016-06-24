package utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dataset.ClusteredDataset;
import dataset.Dataset;
import dataset.Neuron;

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

    public static double[] byteArrayToDouble(byte[] bytes) {
        double[] d = new double[bytes.length / 8];
        for (int i = 0; i < d.length; i++) {
            byte[] b = new byte[8];
            for (int j = 0; j < 8; j++) {
                b[j] = bytes[(i * 8) + j];
            }

            d[i] = ByteBuffer.wrap(b).getDouble();
        }
        return d;
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static byte[] doubleArrayToBytes(double[] doubles) {
        byte[] d = new byte[doubles.length * 8];
        for (int i = 0; i < doubles.length; i++) {
            byte[] bytes = new byte[8];
            ByteBuffer.wrap(bytes).putDouble(doubles[i]);
            int x = 0;
            for (int j = i * 8; j < (i * 8) + 8; j++) {
                d[j] = bytes[x];
                x++;
            }
        }
        return d;
    }

    public static List<Dataset> doubleArrayToDatasets(double[] doubles) {
        List<Dataset> list = new ArrayList<>();
        for (int i = 0; i < doubles.length; i++) {
            list.add(new Dataset(new double[]{doubles[i]}));
        }
        return list;
    }

    public static double[] neuronsToDoubleArray(List<Neuron> list) {
        double[] d = new double[list.size()];
        for (int i = 0; i < d.length; i++) {
            d[i] = list.get(i).getValue(0);
        }
        return d;
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
