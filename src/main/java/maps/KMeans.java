package maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dataset.ClusteredDataset;
import dataset.Dataset;
import dataset.Neuron;
import utils.FileUtils;
import utils.ImageUtils;
import utils.Utils;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class KMeans {

    public static double minX, minY = -2d;
    public static double maxX, maxY = 2d;
    private String outputFilePrefix = "_K-Means";
    private ClusteredDataset[] groupCenters; //centroids

    public void setOutputFilePrefix(String prefix) {
        this.outputFilePrefix = prefix;
    }

    private ClusteredDataset[] randomizeCentroids(int groups) { //FIXME wiecej wymiarow centroidow
        ClusteredDataset[] randomized = new ClusteredDataset[groups];
        Random random = new Random();
        for (int i = 0; i < randomized.length; i++) {
            double x = minX + (maxX - minX) * random.nextDouble();
            double y = minY + (maxY - minY) * random.nextDouble();
            randomized[i] = new ClusteredDataset(new double[]{x, y}, i);
        }
        return randomized;
    }

    private ClusteredDataset[] randomizeCentroidsFromInputs(int groups, List<ClusteredDataset> inputs) {
        ClusteredDataset[] randomized = new ClusteredDataset[groups];
        List<ClusteredDataset> tmp = new ArrayList<>();
        tmp.addAll(inputs);

        Random random = new Random();
        for (int i = 0; i < randomized.length; i++) {
            int index = random.nextInt(tmp.size());
            randomized[i] = new ClusteredDataset(tmp.get(index).getWeights(), i);
            tmp.remove(index);
        }
        return randomized;
    }

    /**
     * @return runAlgorithm index where given point belongs (is closest to runAlgorithm center)
     */
    private int belongsTo(Dataset p) {
        int group = 0;
        double minDistance = Double.MAX_VALUE;
        for (ClusteredDataset centroid : groupCenters) {
            if (p.distanceTo(centroid) < minDistance) {
                group = centroid.getGroup();
                minDistance = p.distanceTo(centroid);
            }
        }
        return group;
    }

    private ClusteredDataset findNewCentroid(List<ClusteredDataset> points, int group) {
        int howMuch = 0;
        double[] sums = new double[points.get(0).size()];
        for (ClusteredDataset p : points) {
            if (p.getGroup() == group) {
                howMuch++;
                for (int i = 0; i < sums.length; i++) {
                    sums[i] = sums[i] + p.getValue(i);
                }
            }
        }
        for (int i = 0; i < sums.length; i++) {
            sums[i] = sums[i] / howMuch;
        }
        return new ClusteredDataset(sums, group);
    }

    private List<ClusteredDataset> groupClustered(List<ClusteredDataset> input, int groups, int iterations) {
        this.groupCenters = randomizeCentroidsFromInputs(groups, input);
        double lastError = Double.MAX_VALUE;
        int realIterations = 0;

        //File f = new File(outputFilePrefix + "_errors");
        //f.delete();

        for (int it = 0; it < iterations; it++) {
            //przypisujemy punkty do srodkow skupien na podstawie odleglosci
            for (ClusteredDataset p : input) {
                p.setGroup(belongsTo(p));
            }
            //ustalamy nowe srodki skupien (srednia wspolrzednych)
            List<List<ClusteredDataset>> lists = Utils.splitClusteredPoints2(input);
            for (int i = 0; i < groupCenters.length; i++) {
                //groupCenters[i] = findNewCentroid2(lists.get(i), i);
                groupCenters[i] = findNewCentroid(input, i);
            }

            saveGroupsToFiles(input, it);
            List<Dataset> list = new ArrayList<>();
            for (ClusteredDataset p : groupCenters) {
                list.add(p);
            }
            FileUtils.saveDatasetList("_centroids" + it, list);

            double currentError = countError(input, groups);
            //System.out.println(currentError);
            //FileUtils.addDataset(outputFilePrefix + "_errors", new Dataset(new double[]{it, currentError}));

            realIterations++;
            if (currentError == lastError) {
                System.out.println("error does not changed, algorithm ended");
                System.out.println("with error: " + currentError);
                break;
            } else {
                lastError = currentError;
            }
        }

        String plotFile = "_KMeans_plot";
        savePlotCommand(groups, realIterations, plotFile);
        try {
            Utils.runGnuplotScript(plotFile);
        } catch (IOException e) {
            System.out.println("Blad podczas rysowania animacji k-srednich");
        }

        //saveErrorPlotCommand(outputFilePrefix + "_error_plot", outputFilePrefix + "_errors");
        //try {
        //    Utils.runGnuplotScript(outputFilePrefix + "_error_plot");
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        deleteFiles(iterations, groups);

        return input;
    }

    private List<ClusteredDataset> groupClusteredWithoutGraph(List<ClusteredDataset> input, int groups, int iterations) {
        this.groupCenters = randomizeCentroidsFromInputs(groups, input);
        double lastError = Double.MAX_VALUE;

        for (int it = 0; it < iterations; it++) {
            //przypisujemy punkty do srodkow skupien na podstawie odleglosci
            for (ClusteredDataset p : input) {
                p.setGroup(belongsTo(p));
            }
            //ustalamy nowe srodki skupien (srednia wspolrzednych)
            List<List<ClusteredDataset>> lists = Utils.splitClusteredPoints2(input);
            for (int i = 0; i < groupCenters.length; i++) {
                //groupCenters[i] = findNewCentroid2(lists.get(i), i);
                groupCenters[i] = findNewCentroid(input, i);
            }

            List<Dataset> list = new ArrayList<>();
            for (ClusteredDataset p : groupCenters) {
                list.add(p);
            }

            double currentError = countError(input, groups);
            //System.out.println(currentError);
            //FileUtils.addDataset(outputFilePrefix + "_errors", new Dataset(new double[]{it, currentError}));
            if (currentError == lastError) {
                System.out.println("error does not changed, algorithm ended");
                System.out.println("with error: " + currentError);
                break;
            } else {
                lastError = currentError;
            }
        }
        return input;
    }

    private double countError(List<ClusteredDataset> points, int groups) {
        double[] errors = new double[groups];
        double[] counters = new double[groups];
        for (int i = 0; i < groups; i++) {
            errors[i] = 0d;
            counters[i] = 0d;
        }
        for (ClusteredDataset p : points) {
            int group = p.getGroup();
            errors[group] = errors[group] + p.distanceTo(groupCenters[group]);
            counters[group] = counters[group] + 1;
        }
        double globalError = 0d;
        for (int i = 0; i < groups; i++) {
            globalError = globalError + (errors[i] / counters[i]);
        }

        return globalError / groups;
    }

    private void deleteFiles(int iterations, int groups) {
        for (int it = 0; it < iterations; it++) {
            File f;
            f = new File("_centroids" + it);
            f.delete();
            for (int g = 0; g < groups; g++) {
                f = new File(outputFilePrefix + "_group" + g + "_it" + it);
                f.delete();
            }

        }
        File f = new File("_KMeans_plot");
        f.delete();
    }

    private void saveGroupsToFiles(List<ClusteredDataset> clustered, int iteration) {
        List<List<Dataset>> l = Utils.splitClusteredPoints(clustered);
        for (int i = 0; i < l.size(); i++) {
            FileUtils.saveDatasetList(this.outputFilePrefix + "_group" + i + "_it" + iteration, l.get(i));
        }
    }

    public List<ClusteredDataset> runAlgorithm(List<Dataset> input, int iterations, int groups) {
        List<ClusteredDataset> clustered = new ArrayList<>();
        for (Dataset p : input) {
            clustered.add(new ClusteredDataset(p.getWeights(), 0));
        }
        return groupClustered(clustered, groups, iterations);
    }


    public void runAlgorithmOnImage(String inImage, String outImage, int iterations, int rows, int columns) {
        List<Dataset> d = ImageUtils.datasetsFromImage(inImage, rows, columns);

        List<ClusteredDataset> clustered = new ArrayList<>();
        for (Dataset p : d) {
            clustered.add(new ClusteredDataset(p.getWeights(), 0));
        }

        this.groupClusteredWithoutGraph(clustered, rows * columns, iterations);

        List<Neuron> neurons = new ArrayList<>();
        for (int i = 0; i < groupCenters.length; i++) {
            neurons.add(new Neuron(groupCenters[i].getWeights()));
        }
        ImageUtils.neuronsToImage(neurons, d, outImage);

    }

    private void savePlotCommand(int groups, int iterations, String plotFilePath) {
        try (PrintStream out = new PrintStream(new FileOutputStream(plotFilePath))) {
            out.println("set terminal gif animate delay 20");
            out.println("set output '" + outputFilePrefix + "_animation.gif" + "'");
            out.println("set key outside");
            iterations = iterations - 1;
            out.println("do for [i=0:" + iterations + "] {");
            out.println("set title \'k-średnich, iteracja \'.i");
            out.print("plot \'" + this.outputFilePrefix + "_group" + 0 + "_it\'.i title \'Grupa " + 0 + "\'");
            for (int i = 1; i < groups; i++) {
                out.print(", \'" + this.outputFilePrefix + "_group" + i + "_it\'.i title \'Grupa " + i + "\'");
            }
            out.print(", \'_centroids\'.i title \'Centroidy\' pt 26 ps 3 lc rgb \"blue\"");

            out.println();
            out.println("}");


            out.println("do for [i=0:15] {");
            out.println("set title \'k-średnich, iteracja " + iterations + "\'");
            out.print("plot \'" + this.outputFilePrefix + "_group" + 0 + "_it" + iterations + "\' title \'Grupa " + 0 + "\'");
            for (int i = 1; i < groups; i++) {
                out.print(", \'" + this.outputFilePrefix + "_group" + i + "_it" + iterations + "\' title \'Grupa " + i + "\'");
            }
            out.print(", \'_centroids" + iterations + "\' title \'Centroidy\' pt 26 ps 3 lc rgb \"blue\"");

            out.println();
            out.println("}");


            out.println();
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void saveErrorPlotCommand(String plotFilePath, String pointsPathT) {

        try (PrintStream out = new PrintStream(new FileOutputStream(plotFilePath))) {
            out.println("set terminal png size 640,480");
            out.println("set yrange [0:15]");
            out.println("set ylabel \'Wartosc bledu\'");
            out.println("set xlabel \'Epoki\'");

            out.println("set output '" + outputFilePrefix + "_error.png'");
            out.println("set title \"Algorytm k-srednich\"");
            //out.println("set key outside");
            out.println("set style data lines");
            out.println("plot \"" + pointsPathT + "\" title \"Sredni blad kwantyzacji\", \\");
            out.println();
        } catch (FileNotFoundException ex) {
        }
    }
}