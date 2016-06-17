package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import points.ClusteredPoint;
import points.Point;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class Utils {

    public static double power(double x){
        return x * x;
    }

    public static List<Point> randomizePoints(int howMuch, double minX, double maxX, double minY, double maxY){
        List<Point> pointList = new ArrayList<Point>();
        Random random = new Random();
        for(int i = 0; i < howMuch; i++){
            double x = minX + (maxX - minX) * random.nextDouble();
            double y = minY + (maxY - minY) * random.nextDouble();
            pointList.add(new Point(x, y));
        }

        return pointList;
    }

    public static List<List<Point>> splitClusteredPoints(List<ClusteredPoint> clustered){
        List<List<Point>> splitted = new ArrayList<>();
        int groups = 0;
        for(ClusteredPoint cpoint : clustered){
            if(groups < cpoint.getGroup()){
                groups = cpoint.getGroup();
            }
        }
        for(int i = 0; i <= groups; i++){
            splitted.add(new ArrayList<>());
        }
        for(ClusteredPoint cpoint : clustered){
            splitted.get(cpoint.getGroup()).add(cpoint);
        }

        return splitted;
    }

    public static List<List<ClusteredPoint>> splitClusteredPoints2(List<ClusteredPoint> clustered){
        List<List<ClusteredPoint>> splitted = new ArrayList<>();
        int groups = 0;
        for(ClusteredPoint cpoint : clustered){
            if(groups < cpoint.getGroup()){
                groups = cpoint.getGroup();
            }
        }
        for(int i = 0; i <= groups; i++){
            splitted.add(new ArrayList<>());
        }
        for(ClusteredPoint cpoint : clustered){
            splitted.get(cpoint.getGroup()).add(cpoint);
        }

        return splitted;
    }
    public static void runGnuplotScript(String scriptName) throws IOException {
        Process gnuplot;
        gnuplot = Runtime.getRuntime().exec("gnuplot " + scriptName);
        try {
            gnuplot.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
