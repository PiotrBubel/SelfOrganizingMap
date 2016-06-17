import java.util.List;

import algorithms.KMeansAlgorithm;
import points.ClusteredPoint;
import points.Point;
import utils.Utils;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class Main {

    public static void main(String[] args){
        System.out.println("Hello world");


        List<Point> randomized = Utils.rendomizePoints(3, -3, -2, -3, -2);
        randomized.addAll(Utils.rendomizePoints(5, 3, 2, 3, 2));
        randomized.addAll(Utils.rendomizePoints(5, 50, 40, 50, 40));
        randomized.addAll(Utils.rendomizePoints(5, -30, -20, -30, -20));

        for(Point p: randomized){
            System.out.println(p.getX() + " " + p.getY());
        }

        KMeansAlgorithm kmeans = new KMeansAlgorithm();

        List<ClusteredPoint> clustered = kmeans.group(randomized, 4, 10);
        System.out.println("..");

        for(ClusteredPoint p : clustered){
            System.out.println(p.getX() + " " + p.getY() + " group: " + p.getGroup());
        }

    }
}