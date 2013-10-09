package movingmorphingmasking.data.util.comparator;

import java.awt.geom.Point2D;
import java.util.Comparator;

/**
 * Comparator for
 * <code>Point2D</code> with the distance between a basic
 * <code>Point2D<\code>.
 *
 * @author julia schueler
 */
public class DistanceToPointComparator implements Comparator<Point2D> {

    private Point2D basicPoint;

    /**
     * Constructor with the point of origin as basic point for the distance
     * comparisons.
     */
    public DistanceToPointComparator() {
        this(0, 0);
    }

    /**
     * Construtor for a comparator of <Point2D> with their distances to the
     * basic point.
     *
     * @param x x coordinate of the basic point
     * @param y y coordinate of the basic point.
     */
    public DistanceToPointComparator(double x, double y) {
        this(new Point2D.Double(x, y));
    }

    /**
     * Construtor for a comparator of <Point2D> with their distances to the
     * basic point.
     *
     * @param basicPoint point for distances.
     */
    public DistanceToPointComparator(Point2D basicPoint) {
        this.basicPoint = basicPoint;
    }

    @Override
    public int compare(Point2D p, Point2D q) {
        double dist1 = p.distanceSq(basicPoint);
        double dist2 = q.distanceSq(basicPoint);
        if (dist1 < dist2) {
            return -1;
        } else if (dist1 > dist2) {
            return +1;
        } else {
            return 0;
        }
    }
}
