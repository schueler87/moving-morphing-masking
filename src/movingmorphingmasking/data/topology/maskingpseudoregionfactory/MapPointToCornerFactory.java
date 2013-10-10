package movingmorphingmasking.data.topology.maskingpseudoregionfactory;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import movingmorphingmasking.data.topology.CornerPoint2D;

/**
 * Factory for a <code>HashMap</code> the creates for each point for a given tesselation of a
 * base polygon a
 * <code>CornerPoint2D</code> and save them in a <code>HashMap</code>.
 *
 * @author julia schueler
 */
public class MapPointToCornerFactory {

    private PolygonSimple basePolygon;

    private static enum PolygonMode {

        base, pseudo, normal
    };

    /**
     * Constructor with the base polygon of a tesselation.
     *
     * @param basePolygon
     */
    public MapPointToCornerFactory(PolygonSimple basePolygon) {
        this.basePolygon = basePolygon;
    }

    /**
     * Returns a <code>HashMap</code> of a given tesselation and a 
     * tesselation of the base polygon as pseudo region polygons.
     * 
     * The mapped <code>CornerPoint2D</code> is moveable 
     * (@see specified by {@link CornerPoint2D#MOVABLE}), if it lays on a side
     * of a pseudo region polygon, if it lays in addition on a base polygon side,
     * but is not a corner of the base polygon, it is a line slider
     * (@see specified by {@link CornerPoint2D#LineSlider}). 
     * Otherwise the <code>CornerPoint2D</code> is immovable
     * (@see specified by {@link CornerPoint2D#IMMOVABLE}).
     * 
     * @param pseudoRegionPolygones tesselation of the base polygon in pseudo
     * regions.
     * @param tesselation tesselation of the base polygon.
     * @return map of a corner of all polygons in the tesselation to a <code>CornerPoint2D</code>.
     */
    public HashMap<Point2D, CornerPoint2D> createPointToCornerMap(List<PolygonSimple> pseudoRegionPolygones, List<PolygonSimple> tesselation) {
        HashMap<Point2D, CornerPoint2D> mapPointToCorner = new HashMap<Point2D, CornerPoint2D>();

        addPolygonCorners(basePolygon, mapPointToCorner, PolygonMode.base);

        for (PolygonSimple polygonSimple : pseudoRegionPolygones) {
            addPolygonCorners(polygonSimple, mapPointToCorner, PolygonMode.pseudo);
        }

        for (PolygonSimple polygonSimple : tesselation) {
            addPolygonCorners(polygonSimple, mapPointToCorner, PolygonMode.normal);
        }

        return mapPointToCorner;
    }

    private void addPolygonCorners(PolygonSimple polygon,
            HashMap<Point2D, CornerPoint2D> mapPointToCorner, PolygonMode mode) {

        double[] x = polygon.getXPoints();
        double[] y = polygon.getYPoints();
        int n = x.length;
        for (int i = 0; i < n; i++) {
            Point2D point = new Point2D.Double(x[i], y[i]);
            addCornerPoint(point, mode, mapPointToCorner);

        }
    }

    private void addCornerPoint(Point2D point, PolygonMode mode, HashMap<Point2D, CornerPoint2D> mapPointToCorner) {
        double x = point.getX();
        double y = point.getY();
        switch (mode) {
            case base:
                mapPointToCorner.put(point, new CornerPoint2D.Immovable(x, y));
                break;
            case pseudo:
                if (!mapPointToCorner.containsKey(point)) {
                    CornerPoint2D cornerPoint2D = getLineSliderOnBasePolygonSide(x, y);
                    if (cornerPoint2D == null) {
                        cornerPoint2D = new CornerPoint2D.Movable(x, y);
                    }
                    mapPointToCorner.put(point, cornerPoint2D);
                }
                break;
            case normal:
                if (!mapPointToCorner.containsKey(point)) {
                    CornerPoint2D cornerPoint2D = new CornerPoint2D.Immovable(x, y);
                    mapPointToCorner.put(point, cornerPoint2D);
                }
                break;
        }
    }

    private CornerPoint2D getLineSliderOnBasePolygonSide(double x, double y) {
        double[] xi = basePolygon.getXPoints();
        double[] yi = basePolygon.getYPoints();
        int n = xi.length;
        for (int i = 0; i < n; i++) {
            double x1 = xi[i];
            double y1 = yi[i];
            double x2 = xi[(i + 1) % n];
            double y2 = yi[(i + 1) % n];
            if (PseudoRegionSide.isOnSide(x1, y1, x2, y2, x, y)) {
                return new CornerPoint2D.LineSlider(x, y, new Line2D.Double(x1, y1, x2, y2));
            }
        }
        return null;
    }
}
