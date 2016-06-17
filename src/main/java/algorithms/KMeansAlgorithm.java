package algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import points.ClusteredPoint;
import points.Point;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class KMeansAlgorithm {

    public static double minX, minY = -2d;
    public static double maxX, maxY = 2d;

    private ClusteredPoint[] groupCenters; //centroids

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

    private List<ClusteredPoint> processClustered(List<ClusteredPoint> input, int groups, int iterations) {
        List<ClusteredPoint> clustered = new ArrayList<>();
        this.groupCenters = randomizeCentroids(groups);

        for (int it = 0; it < iterations; it++) {
            //przypisujemy punkty do srodkow skupien na podstawie odleglosci
            for (ClusteredPoint p : input) {
                p.setGroup(belongsTo(p));
            }
            //ustalamy nowe srodki skupien (srednia wspolrzednych)
            for (int i = 0; i < groupCenters.length; i++) {
                groupCenters[i] = findNewCentroid(clustered, i);
            }
        }
        return clustered;
    }

    private ClusteredPoint findNewCentroid(List<ClusteredPoint> points, int group) {
        double howMuch = 0d;
        double sumX = 0d;
        double sumY = 0d;
        for (ClusteredPoint p : points) {
            if (p.getGroup() == group) {
                howMuch = howMuch + 1d;
                sumX = +p.getX();
                sumY = +p.getY();
            }
        }
        return new ClusteredPoint(sumX / howMuch, sumY / howMuch, group);
    }

    public List<ClusteredPoint> process(List<Point> input, int groups, int iterations) {
        List<ClusteredPoint> clustered = new ArrayList<>();
        for (Point p : input) {
            clustered.add(new ClusteredPoint(p.getX(), p.getY(), 0));
        }
        return processClustered(clustered, groups, iterations);
    }

}
