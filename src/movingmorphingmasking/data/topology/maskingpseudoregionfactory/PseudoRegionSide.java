package movingmorphingmasking.data.topology.maskingpseudoregionfactory;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import movingmorphingmasking.data.util.comparator.DistanceToPointComparator;

/**
 * A PseudoRegionSide is a side of a
 * <code>PolygonSimple</code> that is tesselated by other
 * <code>PolygonSimple</code>. This side could be a pseudo separator in a
 * tesselation and needed to getting smoothed.
 *
 * @author julia schueler
 */
public class PseudoRegionSide {

    private double x1, x2, y1, y2;
    private double sideLengthSq;
    private List<PolygonSimple> associatedPolygons;
    private List<Point2D> pointsOnSide;

    /**
     * Constructor for a
     * <code>PseudoRegionSide</code> with the start point and end point as
     * <code>Pont2D</code>.
     *
     * @param p1 start point of the side
     * @param p2 end point of the side
     */
    public PseudoRegionSide(Point2D p1, Point2D p2) {
        this(p1.getX(), p1.getY(), p2.getX(), p2.getY());

    }

    /**
     * Constructor for a
     * <code>PseudoRegionSide</code> with the start point (x1,y1) and end point
     * (x2, y2).
     *
     * @param x1 X Coordinate of the start point
     * @param y1 Y Coordinate of the start point
     * @param x2 X Coordinate of the end point.
     * @param y2 Y Coordinate of the end point.
     */
    public PseudoRegionSide(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        this.sideLengthSq = Point2D.distanceSq(x1, y1, x2, y2);

        this.associatedPolygons = new ArrayList<PolygonSimple>(2);
        this.pointsOnSide = new ArrayList<Point2D>();
        this.pointsOnSide.add(new Point2D.Double(x1, y1));
        this.pointsOnSide.add(new Point2D.Double(x2, y2));
    }

    /**
     * Adds a polygon the belongs to this
     * <code>PseudoRegionSide</code>.
     *
     * @param polygon polygon that belongs to this
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean addAssociatedPolygon(PolygonSimple polygon) {
        return associatedPolygons.add(polygon);
    }

    /**
     * Returns the count of polygons that belongs to this
     * <code>PseudoRegionSide</code>.
     *
     * @return count of polygons.
     */
    public int getAssociatedPolygonCount() {
        return associatedPolygons.size();
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
     * Returns if a specific side is on the same straight line as this.
     *
     * @param side specific side to check.
     * @return
     */
    public boolean isOnSameStraightLine(PseudoRegionSide side) {
        return isOnSameStraightLine(side.x1, side.y1, side.x2, side.y2);
    }

    /**
     * Returns if a specific side (defined by (x1,y1)->(x2,y2) is on the same
     * straight line as this.
     *
     * @param x1 the X coordinate of the start point of a specific side.
     * @param y1 the Y coordinate of the start point of a specific side.
     * @param x2 the X coordinate of the end point of a specific side.
     * @param y2 the Y coordinate of the end point of a specific side.
     * @return
     */
    public boolean isOnSameStraightLine(double x1, double y1, double x2, double y2) {
        if (isEqualSide(x1, y1, x2, y2)) {
            return true;
        }
        PseudoRegionSide side = getLongestPseudoRegionSide(new PseudoRegionSide(x1, y1, x2, y2));
        return (side.isOnSide(x1, y1) && side.isOnSide(x2, y2)
                && side.isOnSide(this.x1, this.y1) && side.isOnSide(this.x2, this.y2));
    }

    /**
     * Returns a
     * <code>PeudoRegionSide</code> of this and a specific side. With adding the
     * points on side and this and adding the associated polygons. It this and a
     * specific side are not a the same straight line, then it returns a
     * <code>PeudoRegionSide</code> as a line that connects the points on both
     * sides with longest distance to each other.
     *
     * @param side specific side.
     * @return longest possible side between this and a specific side.
     */
    public PseudoRegionSide getConnectedPseudoRegionSide(PseudoRegionSide side) {
        PseudoRegionSide longestPseudoRegionSide = getLongestPseudoRegionSide(side);
        for (Point2D point2D : pointsOnSide) {
            longestPseudoRegionSide.addPoint(point2D.getX(), point2D.getY());
        }
        for (Point2D point2D : side.pointsOnSide) {
            longestPseudoRegionSide.addPoint(point2D.getX(), point2D.getY());
        }
        for (PolygonSimple polygonSimple : associatedPolygons) {
            if (!longestPseudoRegionSide.associatedPolygons.contains(polygonSimple)) {
                longestPseudoRegionSide.addAssociatedPolygon(polygonSimple);
            }
        }
        for (PolygonSimple polygonSimple : side.associatedPolygons) {
            if (!longestPseudoRegionSide.associatedPolygons.contains(polygonSimple)) {
                longestPseudoRegionSide.addAssociatedPolygon(polygonSimple);
            }
        }
        longestPseudoRegionSide.sortPointsOnSide();
        return longestPseudoRegionSide;
    }

    /**
     * Adds a specific point if it is on this side and not already added.
     *
     * @param x X Coordinate of the specific point.
     * @param y Y Coordinate of the specific point.
     * @return <tt>true</tt>
     */
    public boolean addPoint(double x, double y) {
        System.out.println("Side " + x1 + " " + y1 + " -> " + x2 + " " + y2);
        System.out.println(" look at " + x + " " + y + " " + isOnSide(x, y));
        if (isOnSide(x, y) && indexOfPoint(x, y) < 0) {
            pointsOnSide.add(new Point2D.Double(x, y));
            System.out.println("   can add it");
            return true;
        }
        return false;
    }

    /**
     * Returns if a a specific point (x,y) is on this side. First it checks
     * first the relative CCW is equals 0 (
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
     * Returns if a a specific point (x,y) is on this side (
     *
     * @see as specified by {@link PseudoRegionSide#isOnSide})).
     * @param x1 X Coordinate of the start point of specific side.
     * @param y1 Y Coordinate of the start point of specific side.
     * @param x2 X Coordinate of the end point of specific side.
     * @param y2 Y Coordinate of the end point of specific side.
     * @param x X Coordinate of a specific point.
     * @param y X Coordinate of a specific point.
     * @return <tt>true</tt>
     */
    public static boolean isOnSide(double x1, double y1, double x2, double y2, double x, double y) {
        return new PseudoRegionSide(x1, y1, x2, y2).isOnSide(x, y);
    }

    /**
     * Returns the position of a specific point(x,y) on this side. It returns -1
     * if the point is not an added point (@link PseudoRegionSide#addPoint),
     * or start point or a end point of this.
     * (NOTE: use before @link PseudoRegionSide#sortPointsOnSide).
     *
     * @param x X Coordinate of a specific point.
     * @param y Y Coordinate of a specific point.
     * @return position of a specific point.
     */
    public int indexOfPoint(double x, double y) {
        int n = pointsOnSide.size();
        for (int i = 0; i < n; i++) {
            Point2D point2D = pointsOnSide.get(i);
            if (point2D.getX() == x && point2D.getY() == y) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns all the
     * <code>Point2D</code> included the specific start point (x1, y1) and
     * specific end point (x2, y2) and all added points (@link PseudoRegionSide#addPoint) between them.
     * (NOTE: use before @link PseudoRegionSide#sortPointsOnSide).
     *
     * It returns an empty list, if the specific start point or end point is
     * not an start or end point of this or an added point (@link PseudoRegionSide#addPoint).
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public List<Point2D> getPointsOnSideBetween(double x1, double y1, double x2, double y2) {
        List<Point2D> points = new ArrayList<Point2D>();
        int index_1 = indexOfPoint(x1, y1);
        int index_2 = indexOfPoint(x2, y2);
        int n = pointsOnSide.size();
        System.out.println("index 1 " + index_1 + " index_2 " + index_2 + " n " + n);
        if (index_1 < 0 || index_2 < 0 || index_1 == index_2) {
            return points;
        }
        index_1 = Math.min(n - 1, Math.max(0, index_1));
        index_2 = Math.min(n - 1, Math.max(0, index_2));
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
     * to the squarified distance to the start point of this using the
     * <code>DistanceToPointComparator</code>.
     */
    public void sortPointsOnSide() {
        Collections.sort(pointsOnSide, new DistanceToPointComparator(x1, y1));
    }

    @Override
    public String toString() {
        return x1 + " " + y1 + " --> " + x2 + " " + y2;
    }

    private PseudoRegionSide getLongestPseudoRegionSide(PseudoRegionSide side) {
        double d1 = Point2D.distanceSq(x1, y1, x2, y2);
        double d2 = Point2D.distanceSq(side.x1, side.y1, side.x2, side.y2);
        double d3 = Point2D.distanceSq(side.x1, side.y1, x2, y2);
        double d4 = Point2D.distanceSq(side.x1, side.y1, x1, y1);
        double d5 = Point2D.distanceSq(side.x2, side.y2, x2, y2);
        double d6 = Point2D.distanceSq(side.x2, side.y2, x1, y1);
        if (d1 >= d2 && d1 >= d3 && d1 >= d4 && d1 >= d5 && d1 >= d6) {
            return this;
        }
        if (d2 >= d3 && d2 >= d4 && d2 >= d5 && d2 >= d6) {
            return side;
        }
        if (d3 >= d4 && d3 >= d5 && d3 >= d6) {
            return new PseudoRegionSide(side.x1, side.y1, x2, y2);
        }
        if (d4 >= d5 && d4 >= d6) {
            return new PseudoRegionSide(side.x1, side.y1, x1, y1);
        }
        if (d5 >= d6) {
            return new PseudoRegionSide(side.x2, side.y2, x2, y2);
        }
        return new PseudoRegionSide(side.x2, side.y2, x1, y1);



    }
}
