package algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import points.ClusteredPoint;
import points.Point;
import utils.FileUtils;
import utils.Utils;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class KMeansAlgorithm {

    public static double minX, minY = -2d;
    public static double maxX, maxY = 2d;
    private String outputFilePrefix = "_K-Means";
    private ClusteredPoint[] groupCenters; //centroids

    public void setOutputFilePrefix(String prefix) {
        this.outputFilePrefix = prefix;
    }

    private ClusteredPoint[] randomizeCentroids(int groups) {
        ClusteredPoint[] randomized = new ClusteredPoint[groups];
        Random random = new Random();
        for (int i = 0; i < randomized.length; i++) {
            double x = minX + (maxX - minX) * random.nextDouble();
            double y = minY + (maxY - minY) * random.nextDouble();
            randomized[i] = new ClusteredPoint(x, y, i);
        }
        return randomized;
    }

    private ClusteredPoint[] randomizeCentroidsFromInputs(int groups, List<ClusteredPoint> inputs) {
        ClusteredPoint[] randomized = new ClusteredPoint[groups];
        List<ClusteredPoint> tmp = new ArrayList<>();
        tmp.addAll(inputs);

        Random random = new Random();
        for (int i = 0; i < randomized.length; i++) {
            int index = random.nextInt(tmp.size());
            randomized[i] = new ClusteredPoint(tmp.get(index).getX(), tmp.get(index).getY(), i);
            tmp.remove(index);
        }
        return randomized;
    }

    /**
     * @return group index where given point belongs (is closest to group center)
     */
    private int belongsTo(Point p) {
        int group = 0;
        double minDistance = Double.MAX_VALUE;
        for (ClusteredPoint centroid : groupCenters) {
            if (p.distanceTo(centroid) < minDistance) {
                group = centroid.getGroup();
                minDistance = p.distanceTo(centroid);
            }
        }
        return group;
    }

    private ClusteredPoint findNewCentroid(List<ClusteredPoint> points, int group) {
        int howMuch = 0;
        double sumX = 0d;
        double sumY = 0d;
        for (ClusteredPoint p : points) {
            if (p.getGroup() == group) {
                howMuch++;
                sumX = sumX + p.getX();
                sumY = sumY + p.getY();
            }
        }
        return new ClusteredPoint(sumX / howMuch, sumY / howMuch, group);
    }

    private List<ClusteredPoint> groupClustered(List<ClusteredPoint> input, int groups, int iterations) {
        this.groupCenters = randomizeCentroidsFromInputs(groups, input);
        double lastError = Double.MAX_VALUE;
        int realIterations = 0;

        for (int it = 0; it < iterations; it++) {
            //przypisujemy punkty do srodkow skupien na podstawie odleglosci
            for (ClusteredPoint p : input) {
                p.setGroup(belongsTo(p));
            }
            //ustalamy nowe srodki skupien (srednia wspolrzednych)
            List<List<ClusteredPoint>> lists = Utils.splitClusteredPoints2(input);
            for (int i = 0; i < groupCenters.length; i++) {
                //groupCenters[i] = findNewCentroid2(lists.get(i), i);
                groupCenters[i] = findNewCentroid(input, i);
            }

            saveGroupsToFiles(input, it);
            List<Point> list = new ArrayList<>();
            for (ClusteredPoint p : groupCenters) {
                list.add(p);
            }
            FileUtils.saveListOfPoints("_centroids" + it, list);
            double currentError = countError(input, groups);
            System.out.println("ended " + it + "iteration");
            System.out.println("with error: " + currentError);
            System.out.println();
            realIterations++;
            if(currentError == lastError){
                System.out.println("error does not changed, algorithm ended");
                break;
            } else{
                lastError = currentError;
            }
        }

        String plotFile = "_plotAnimation";
        savePlotCommand(groups, realIterations, plotFile);

        try {
            Utils.runGnuplotScript(plotFile);
        } catch (IOException e) {
            System.out.println("Blad podczas rysowania animacji k-srednich");
        }

        deleteFiles(iterations, groups);

        return input;
    }

    private double countError(List<ClusteredPoint> points, int groups) {
        double[] errors = new double[groups];
        double[] counters = new double[groups];
        for (int i = 0; i < groups; i++) {
            errors[i] = 0d;
            counters[i] = 0d;
        }
        for (ClusteredPoint p : points) {
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
    }

    private void saveGroupsToFiles(List<ClusteredPoint> clustered, int iteration) {
        List<List<Point>> l = Utils.splitClusteredPoints(clustered);
        for (int i = 0; i < l.size(); i++) {
            FileUtils.saveListOfPoints(this.outputFilePrefix + "_group" + i + "_it" + iteration, l.get(i));
        }
    }

    public List<ClusteredPoint> group(List<Point> input, int groups, int iterations) {
        List<ClusteredPoint> clustered = new ArrayList<>();
        for (Point p : input) {
            clustered.add(new ClusteredPoint(p.getX(), p.getY(), 0));
        }
        return groupClustered(clustered, groups, iterations);
    }

    private void savePlotCommand(int groups, int iterations, String plotFilePath) {
        try (PrintStream out = new PrintStream(new FileOutputStream(plotFilePath))) {
            out.println("set terminal gif animate delay 25");
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
            out.println();
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}