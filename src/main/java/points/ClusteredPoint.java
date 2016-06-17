package points;

/**
 * Created by Piotrek on 17.06.2016.
 */
public class ClusteredPoint extends Point {
    private int group = -1;

    public int getGroup() {
        return group;
    }

    public void setGroup(int g) {
        this.group = g;
    }

    public ClusteredPoint(double x, double y, int group){
        super(x,y);
        this.group = group;
    }
}
