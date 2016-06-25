package dataset.comparators;

import java.util.Comparator;

import dataset.Dataset;

/**
 * Created by Piotrek on 25.06.2016.
 */
public class DatasetIndexComparator implements Comparator<Dataset> {


    @Override
    public int compare(Dataset o1, Dataset o2) {
        return Integer.compare(o1.getIndex(), o2.getIndex());
    }
}
