package dataset.comparators;

import java.util.Comparator;

import dataset.Dataset;

/**
 * Created by Piotrek on 22.06.2016.
 */
public class DatasetDistanceComparator implements Comparator<Dataset> {

    Dataset destination = null;

    public DatasetDistanceComparator(Dataset x) {
        destination = x;
    }

    @Override
    public int compare(Dataset o1, Dataset o2) {
        return Double.compare(o1.distanceTo(destination), o2.distanceTo(destination));
    }
}
