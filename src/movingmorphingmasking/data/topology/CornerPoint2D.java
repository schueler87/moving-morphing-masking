package movingmorphingmasking.data.topology;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The
 * <code>CornerPoint2D</code> class defines a
 * <code>Point2D.Double</code>.
 * <p>
 * This class is only the abstract superclass for all points with unlimited or
 * limited possible 2D coordinates. The actual location settings of the
 * coordinates is left to the subclass.
 *
 * @author julia schueler
 */
public abstract class CornerPoint2D extends Point2D {

    /**
     * The
     * <code>Immovable</code> class defines a
     * <code>CornerPoint2D</code> with unchangeable coordinates.
     *
     */
    public static class Immovable extends CornerPoint2D {

        /**
         * Constructs and initializes a
         * <code>CornerPoint2D</code> with the specified coordinates.
         *
         * @param x the X coordinate of the newly constructed <code>CornerPoint2D</code>
         * @param y the Y coordinate of the newly constructed <code>CornerPoint2D</code>
         * @since 1.2
         */
        public Immovable(double x, double y) {
            super(x, y);
        }

        /**
         * Does not set any other location than the original one. So nothing is
         * happening here ;).
         *
         * @param x desired x-value
         * @param y desired y-value
         */
        @Override
        protected void setClosestToDesiredLocation(double x, double y) {
        }
    }

    /**
     * The
     * <code>Movable</code> class defines a
     * <code>CornerPoint2D</code> with changeable coordinates.
     *
     */
    public static class Movable extends CornerPoint2D {

        /**
         * Constructs and initializes a
         * <code>CornerPoint2D</code> with the specified coordinates.
         *
         * @param x the X coordinate of the newly constructed <code>CornerPoint2D</code>
         * @param y the Y coordinate of the newly constructed <code>CornerPoint2D</code>
         */
        public Movable(double x, double y) {
            super(x, y);
        }

        /**
         * Sets the location of this
         * <code>CornerPoint2D</code> to the specified
         * <code>double</code> coordinates.
         *
         * @param x the new X coordinate of this {@code CornerPoint2D}
         * @param y the new Y coordinate of this {@code CornerPoint2D}
         */
        @Override
        protected void setClosestToDesiredLocation(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * The
     * <code>LineSlider</code> class defines a
     * <code>CornerPoint2D</code> with changable coordinates, but only located
     * on a line.
     *
     */
    public static class LineSlider extends CornerPoint2D {

        private final Line2D.Double line2D;
        private final double dx;
        private final double dy;
        private final double lineLength;

        /**
         * Constructs and initializes a
         * <code>CornerPoint2D</code> with the specified coordinates.
         *
         * @param x the X coordinate of the newly * * * *          * constructed <code>CornerPoint2D</code>
         * @param y the Y coordinate of the newly * * * *          * constructed <code>CornerPoint2D</code>
         * @param line2D the line the point has always located on.
         */
        public LineSlider(double x, double y, Line2D.Double line2D) {
            super(x, y);
            if (line2D == null) {
                line2D = new Line2D.Double(0, 0, 0, 0);
            }
            this.line2D = new Line2D.Double(line2D.getX1(), line2D.getY1(), line2D.getX2(), line2D.getY2());
            dx = this.line2D.getX2() - this.line2D.getX1();
            dy = this.line2D.getY2() - this.line2D.getY1();
            lineLength = Math.sqrt(dx * dx + dy * dy);
        }

        /**
         * Sets the location of this
         * <code>CornerPoint2D</code> to the specified
         * <code>double</code> coordinates that are the closest
         * <code>double</code> coordinates on the current line.
         *
         * @param x the desired X coordinate of this {@code CornerPoint2D}
         * @param y the desired Y coordinate of this {@code CornerPoint2D}
         */
        @Override
        protected void setClosestToDesiredLocation(double x, double y) {
            if (lineLength == 0) {
                return;
            }
            double distToLine = line2D.ptLineDist(x, y);
            double dx = -this.dy * distToLine / lineLength;
            double dy = this.dx * distToLine / lineLength;

            int relativeCCW = line2D.relativeCCW(x, y);
            double nx = x + dx * relativeCCW;
            double ny = y + dy * relativeCCW;

            //check if point(nx, ny) is on the line.
            double distToP1 = line2D.getP1().distance(nx, ny);
            double distToP2 = line2D.getP2().distance(nx, ny);
            if (distToP1 > lineLength || distToP2 > lineLength) {
                if (distToP1 < distToP2) {
                    //set (nx, ny) to the startpoint
                    nx = line2D.x1;
                    ny = line2D.y1;
                } else {
                    //set (nx, ny) to the end point
                    nx = line2D.x2;
                    ny = line2D.y2;
                }
            }


            this.x = nx;
            this.y = ny;
        }
    }
    /**
     * The X coordinate of this
     * <code>CornerPoint2D</code>.
     *
     * @serial
     */
    protected double x;
    /**
     * The Y coordinate of this
     * <code>CornerPoint2D</code>.
     *
     * @serial
     */
    protected double y;
    protected boolean visited;
    private List<CornerPoint2D> incidentCorners;
    private List<PolygonDeformable> associatedPolygons;

    /**
     * This is an abstract class that cannot be instantiated directly.
     * Type-specific implementation subclasses are available for instantiation
     * and provide a number of formats for storing the information necessary to
     * satisfy the various accessor methods below.
     *
     * @see CornerPoint2D.Immovable
     * @see CornerPoint2D.Movable
     * @see CornerPoint2D.LineSlider
     * @see java.awt.geom.Point2D
     */
    protected CornerPoint2D(double x, double y) {
        this.x = x;
        this.y = y;
        
        this.visited = false;
        incidentCorners = new ArrayList<CornerPoint2D>(10);
        associatedPolygons = new ArrayList<PolygonDeformable>(10);
    }

    /**
     * Sets the visited status.
     * @param visited 
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * Returns if the CornerPoint2D is already visited.
     * @return 
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * Returns all the incident
     * <code>CornerPoint2D</code> of this.
     *
     * @return
     */
    public List<CornerPoint2D> getIncidentCorners() {
        return incidentCorners;
    }

    /**
     * Add a incident
     * <code>CornerPoint2D</code> of this.
     *
     * @param corner incident cornerPoint2D
     * @return
     */
    public boolean addIncidentCorner(CornerPoint2D corner) {
        return incidentCorners.add(corner);
    }

    /**
     * Returns all the associated
     * <code>PolygonDeformable</code> of this.
     *
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public List<PolygonDeformable> getAssociatedPolygons() {
        return associatedPolygons;
    }

    /**
     * Adds as an associated
     * <code>PolygonDeformable</code> of this.
     *
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean addAssociatedPolygon(PolygonDeformable polygon) {
        return this.associatedPolygons.add(polygon);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getX() {
        return x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getY() {
        return y;
    }

    /**
     * Sets the location of this
     * <code>CornerPoint2D</code> to the closest possible location of the
     * specified
     * <code>Point2D</code>.
     *
     * @param p the desired <code>Point2D</code> to which to set * * * *
     * this <code>CornerPoint2D</code>.
     */
    @Override
    public void setLocation(Point2D p) {
        setClosestToDesiredLocation(p.getX(), p.getY());
    }

    /**
     * Sets the location of this
     * <code>CornerPoint2D</code> to the closest possible location of the
     * specified
     * <code>double</code> coordinates.
     *
     * @param x the desired X coordinate of this
     * @param y the desired Y coordinate of this
     */
    @Override
    public void setLocation(double x, double y) {
        setClosestToDesiredLocation(x, y);
    }

    /**
     * Sets the location of this
     * <code>CornerPoint2D</code> to the closest possible location coordinates
     * as the specified
     * <code>Point2D</code> object.
     *
     * @param x desired x-value
     * @param y desired y-value
     */
    protected abstract void setClosestToDesiredLocation(double x, double y);
}
