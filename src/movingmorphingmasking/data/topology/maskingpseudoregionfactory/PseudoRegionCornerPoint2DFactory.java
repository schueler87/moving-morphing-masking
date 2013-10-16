package movingmorphingmasking.data.topology.maskingpseudoregionfactory;

import java.awt.geom.Line2D;
import java.util.List;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import movingmorphingmasking.data.topology.CornerPoint2D;

/**
 * <code>PseudoRegionCornerPoint2DFactory</code> creates movable CornerPoint2Ds
 * on the boundary of the base polygon or on the boundary of the pseudo region
 * polygon borders.
 *
 * @author julia schueler
 */
public class PseudoRegionCornerPoint2DFactory {

    private final PolygonSimple basePolygon;
    private final List<PolygonSimple> pseudoRegionPolygones;

    /**
     * Constructor with the base polygon of a tesselation.
     *
     * @param basePolygon base polygon
     * @param pseudoRegionPolygones tesselation of the base polygon in pseudo
     * regions.
     */
    public PseudoRegionCornerPoint2DFactory(PolygonSimple basePolygon, List<PolygonSimple> pseudoRegionPolygones) {
        this.basePolygon = basePolygon;
        this.pseudoRegionPolygones = pseudoRegionPolygones;
    }

    /**
     * Returns the base polygon of the pseudo region tesselation
     * @return 
     */
    public PolygonSimple getBasePolygon() {
        return basePolygon;
    }

    /**
     * Returns the pseudo region tesselation.
     * @return 
     */
    public List<PolygonSimple> getPseudoRegionPolygones() {
        return pseudoRegionPolygones;
    }

    /**
     * Reurns a <code>CornerPoint2D.LineSlider</code> on a side of the 
     * base polygon. If the the point (x,y) is not on a base polygon side
     * it returns <code>null</code>.
     * @param x the X coordinate of a specific point.
     * @param y the Y coordinate of a specific point.
     * @return <code>CornerPoint2D</code> with the coordinates x and y.
     */
    public CornerPoint2D getLineSliderOnBasePolygonSide(double x, double y) {
        return getCornerPointOnPolygonSide(basePolygon, x, y, CornerPoint2D.IS_LINESLIDER);

    }

    /**
     * /**
     * Reurns a <code>CornerPoint2D</code> on a side of pseudo tesselation polygon.
     * If the the point (x,y) is not on a pseudo tesselation polygon side
     * it returns <code>null</code>.
     * If the point(x,y) is on a base polygon side, it returns a <code>CornerPoint2D.Immovable</code>.
     * otherwise it returns a <code>CornerPoint2D.Movable</code>.
     * 
     * @param x the X coordinate of a specific point.
     * @param y the Y coordinate of a specific point.
     * @return <code>CornerPoint2D</code> with the coordinates x and y.
     */
    public CornerPoint2D getCornerPointOnTesselationSide(double x, double y) {
        CornerPoint2D cornerPoint = getImmovableOnBasePolygonSide(x, y);
        if (cornerPoint != null) {
            return cornerPoint;
        }
        cornerPoint = getMovableOnPseudoPolygonSide(x, y);
        if (cornerPoint != null) {
            return cornerPoint;
        }
        return new CornerPoint2D.Immovable(x, y);
    }

    private CornerPoint2D getMovableOnPseudoPolygonSide(double x, double y) {
        for (PolygonSimple polygonSimple : pseudoRegionPolygones) {
            CornerPoint2D cornerPoint = getCornerPointOnPolygonSide(polygonSimple, x, y, CornerPoint2D.IS_MOVABLE);
            if (cornerPoint != null) {
                return cornerPoint;
            }
        }
        return null;
    }

    private CornerPoint2D getImmovableOnBasePolygonSide(double x, double y) {
        return getCornerPointOnPolygonSide(basePolygon, x, y, CornerPoint2D.IS_IMMOVABLE);

    }

    private CornerPoint2D getCornerPointOnPolygonSide(PolygonSimple polygon, double x, double y, String cornerMode) {
        double[] xi = polygon.getXPoints();
        double[] yi = polygon.getYPoints();
        int n = xi.length;
        for (int i = 0; i < n; i++) {
            double x1 = xi[i];
            double y1 = yi[i];
            double x2 = xi[(i + 1) % n];
            double y2 = yi[(i + 1) % n];
            if (PseudoRegionSide.isOnSide(x1, y1, x2, y2, x, y)) {
                if (cornerMode.equals(CornerPoint2D.IS_LINESLIDER)) {
                    return new CornerPoint2D.LineSlider(x, y, new Line2D.Double(x1, y1, x2, y2));
                } else if (cornerMode.equals(CornerPoint2D.IS_MOVABLE)) {
                    return new CornerPoint2D.Movable(x, y);
                } else if (cornerMode.equals(CornerPoint2D.IS_IMMOVABLE)) {
                    return new CornerPoint2D.Immovable(x, y);
                }
            }
        }
        return null;
    }
}
