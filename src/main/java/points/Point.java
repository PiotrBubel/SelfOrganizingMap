package points;

import utils.Utils;

/**
 * Created by Piotrek on 16.06.2016.
 */
public class Point {

    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        x = y = 0d;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * Euclidean distance
     */
    public double distanceTo(Point other) {
        return Math.sqrt(
                Utils.power(this.getX() - other.getX())
                        + Utils.power(this.getY() - other.getY()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (Double.compare(point.getX(), getX()) != 0) return false;
        return Double.compare(point.getY(), getY()) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(getX());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getY());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
