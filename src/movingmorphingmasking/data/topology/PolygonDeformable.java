package movingmorphingmasking.data.topology;

/**
 * The
 * <code>PolygonDeformable</code> class defines a polygon with movable corners.
 *
 * @author julia schueler
 */
public class PolygonDeformable {

    private CornerPoint2D[] corners;
    private double targetArea;

    /**
     * The
     * <code>PolygonDeformable</code> class defines a polygon with movable
     * <code>CornerPoint2D</code>
     *
     * @param corners movable corners of this.
     * @param targetArea the polygon should have the surface area.
     */
    public PolygonDeformable(CornerPoint2D[] corners, double targetArea) {
        this.corners = corners;
        this.targetArea = targetArea;
        connectCornersAndPolygon();
    }

    /**
     * Returns the targetArea of this.
     *
     * @return target area.
     */
    public double getTagetArea() {
        return targetArea;
    }

    /**
     * Returns the current area of the
     * <code>PolygonDeformable</code>. It uses the Shoelace formula. 2*Area =
     * |sum from i= 1 to n (y_i+y_(i+i)*(x_i+x_(i+i))| It is assumed that the
     * polygon is simple.
     *
     * @return area of this.
     */
    public double getCurrentArea() {
        if (corners == null) {
            return 0;
        }
        double sum = 0;
        int n = corners.length;
        for (int i = 0; i < n; i++) {
            double xi = corners[i].getX();
            double yi = corners[i].getY();
            double xi1 = corners[(i + 1) % n].getX();
            double yi1 = corners[(i + 1) % n].getY();
            sum += (yi + yi1) * (xi - xi1);
        }
        return Math.abs(sum * 0.5);
    }

    /**
     * Returns the corners of the
     * <code>PolygonDeformable</code>.
     *
     * @return corners of this.
     */
    public CornerPoint2D[] getCorners() {
        return corners;
    }

    private void connectCornersAndPolygon() {
        int n = corners.length;
        for (int i = 0; i < corners.length; i++) {
            CornerPoint2D cornerPoint2D = corners[i];
            cornerPoint2D.addAssociatedPolygon(this);
            CornerPoint2D neighbor = corners[(i + 1) % n];
            cornerPoint2D.addIncidentCorner(neighbor);
            neighbor.addIncidentCorner(cornerPoint2D);
        }
    }
}
