package algorithms;

import java.util.ArrayList;
import java.util.List;

import points.Point;
import utils.Utils;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class KohonenAlgorithm {

    private List<Point> neurons;

    public KohonenAlgorithm(int howMuch) {
        this.neurons = new ArrayList<>();
        this.neurons = Utils.randomizePoints(howMuch, 0, 5, 0, 5);
    }

    public void process(List<Point> inputs){

        for(Point p : inputs){

        }
    }
}
