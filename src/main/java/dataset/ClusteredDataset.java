package dataset;

/**
 * Created by Piotrek on 17.06.2016.
 */
public class ClusteredDataset extends Dataset {
    private int group = -1;

    public ClusteredDataset(double[] x, int group) {
        super(x);
        this.group = group;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int g) {
        this.group = g;
    }
}
