package movingmorphingmasking.data.topology.punnetresolvefactory;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import movingmorphingmasking.data.util.comparator.DistanceToPointComparator;

/**
 * A PunnetSide is a side of a
 * <code>PolygonSimple</code> that is tesselated by other
 * <code>PolygonSimple</code>. This side could be a pseudo separator in a
 * tesselation and needed to getting smoothed.
 *
 * @author julia schueler
 */
public class PunnetSide {

    private double x1, x2, y1, y2;
    private double sideLengthSq;
    private List<PolygonSimple> connectedPolygons;
    private List<Point2D> pointsOnSide;

    /**
     * Constructor for a
     * <code>PunnetSide</code> with the start point and end point as
     * <code>Pont2D</code>.
     *
     * @param p1 start point of the side
     * @param p2 end point of the side
     */
    public PunnetSide(Point2D p1, Point2D p2) {
        this(p1.getX(), p1.getY(), p2.getX(), p2.getY());

    }

    /**
     * Constructor for a
     * <code>PunnetSide</code> with the start point (x1,y1) and end point (x2,
     * y2).
     *
     * @param x1 X Coordinate of the start point
     * @param y1 Y Coordinate of the start point
     * @param x2 X Coordinate of the end point.
     * @param y2 Y Coordinate of the end point.
     */
    public PunnetSide(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        this.sideLengthSq = Point2D.distanceSq(x1, y1, x2, y2);

        this.connectedPolygons = new ArrayList<PolygonSimple>(2);
        this.pointsOnSide = new ArrayList<Point2D>();
        this.pointsOnSide.add(new Point2D.Double(x1, y1));
        this.pointsOnSide.add(new Point2D.Double(x2, y2));
    }

    /**
     * Adds a polygon the belongs to this
     * <code>PunnetSide</code>.
     *
     * @param polygon polygon that belongs to this
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean addConnectedPolygon(PolygonSimple polygon) {
        return connectedPolygons.add(polygon);
    }

    /**
     * Returns the count of polygons that belongs to this
     * <code>PunnetSide</code>.
     *
     * @return count of polygons.
     */
    public int getConnectedPolygonCount() {
        return connectedPolygons.size();
    }

    /**
     * Returns if a specific side (x1,y1)->(x2,y2) is equal to this.
     *
     * @param x1 X Coordinate of the start point of the specific side.
     * @param y1 Y Coordinate of the start point of the specific side.
     * @param x2 X Coordinate of the end point of the specific side.
     * @param y2 Y Coordinate of the end point of the specific side.
     * @return <tt>true</tt> if the side (x1,y1)->(x2,y2) equals this.
     */
    public boolean isEqualSide(double x1, double y1, double x2, double y2) {
        if (x1 == this.x1 && x2 == this.x2 && y1 == this.y1 && y2 == this.y2) {
            return true;
        }
        if (x2 == this.x1 && x1 == this.x2 && y2 == this.y1 && y1 == this.y2) {
            return true;
        }
        return false;
    }

    /**
     * Adds a specific point if it is on this side and not already added.
     *
     * @param x X Coordinate of the specific point.
     * @param y Y Coordinate of the specific point.
     * @return <tt>true</tt>
     */
    public boolean addPoint(double x, double y) {
        if (isOnSide(x, y) && indexOfPoint(x, y) < 0) {
            pointsOnSide.add(new Point2D.Double(x, y));
            return true;
        }
        return false;
    }

    /**
     * Returns if a a specific point (x,y) is on this side. First it checks
     * first the relative CCW is equals 0
     * (
     *
     * @see as specified by {@link Line2D#relativeCCW})). Then it checks, if the
     * specific point is between the start point and end point of this.
     * @param x X Coordinate of a specific point.
     * @param y Y Coordinate of a specific point.
     * @return <tt>true</tt>
     */
    public boolean isOnSide(double x, double y) {
        int relativeCCW = Line2D.relativeCCW(x1, y1, x2, y2, x, y);
        if (relativeCCW == 0) {
            double d1 = Point2D.distanceSq(x1, y1, x, y);
            if (d1 <= sideLengthSq) {
                return Point2D.distanceSq(x2, y2, x, y) <= sideLengthSq;
            }
            return false;

        }
        return false;
    }

    /**
     * Returns if a a specific point (x,y) is on this side 
     * (
     *
     * @see as specified by {@link PunnetSide#isOnSide})).
     * @param x1 X Coordinate of the start point of specific side.
     * @param y1 Y Coordinate of the start point of specific side.
     * @param x2 X Coordinate of the end point of specific side.
     * @param y2 Y Coordinate of the end point of specific side.
     * @param x X Coordinate of a specific point.
     * @param y X Coordinate of a specific point.
     * @return <tt>true</tt>
     */
    public static boolean isOnSide(double x1, double y1, double x2, double y2, double x, double y) {
        return new PunnetSide(x1, y1, x2, y2).isOnSide(x, y);
    }

    /**
     * Returns the position of a specific point(x,y) on this side. It returns -1
     * if the point is not an added point (@link PunnetSide#addPoint), 
     * or start point or a end point of this.
     * (NOTE: use before @link PunnetSide#sortPointsOnSide).
     *
     * @param x X Coordinate of a specific point.
     * @param y Y Coordinate of a specific point.
     * @return position of a specific point.
     */
    public int indexOfPoint(double x, double y) {
        for (int i = 0; i < pointsOnSide.size(); i++) {
            Point2D point2D = pointsOnSide.get(i);
            if (point2D.getX() == x && point2D.getY() == y) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns all the <code>Point2D</code> included the specific
     * start point (x1, y1) and specific end point (x2, y2) and all
     * added points (@link PunnetSide#addPoint) between them.
     * (NOTE: use before @link PunnetSide#sortPointsOnSide).
     * 
     * It returns an empty list, if the specific start point or end point is
     * not an start or end point of this or an added point (@link PunnetSide#addPoint).
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return 
     */
    public List<Point2D> getPointsOnSideBetween(double x1, double y1, double x2, double y2) {
        ArrayList<Point2D> points = new ArrayList<Point2D>();
        int index_1 = indexOfPoint(x1, y1);
        int index_2 = indexOfPoint(x2, y2);
        if (index_1 < 0 || index_2 < 0 || index_1 == index_2) {
            return points;
        }
        if (index_1 < index_2) {
            for (int i = index_1; i <= index_2; i++) {
                points.add(pointsOnSide.get(i));
            }

        } else {
            for (int i = index_1; i >= index_2; i--) {
                points.add(pointsOnSide.get(i));
            }
        }
        return points;
    }

    /**
     * Sorted the added point, including start and end point of this, according
     * to the squarified distance to the start point of this
     * using the <code>DistanceToPointComparator</code>.
     */
    public void sortPointsOnSide() {
        Collections.sort(pointsOnSide, new DistanceToPointComparator(x1, y1));
    }
}
