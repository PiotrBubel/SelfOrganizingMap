package algorithms;

import java.util.ArrayList;
import java.util.List;

import dataset.Dataset;
import utils.Utils;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class KohonenAlgorithm {

    private List<Dataset> neurons;

    public KohonenAlgorithm(int howMuch) {
        this.neurons = new ArrayList<>();
        this.neurons = Utils.randomizePoints(howMuch, 0, 5, 0, 5);
    }

    public void process(List<Dataset> inputs){

        for(Dataset p : inputs){

        }
    }
}
