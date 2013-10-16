package movingmorphingmasking.data.topology.maskingpseudoregionfactory;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import movingmorphingmasking.data.topology.CornerPoint2D;

/**
 * Factory for a
 * <code>HashMap</code> the creates for each point for a given tesselation of a
 * base polygon a
 * <code>CornerPoint2D</code> and save them in a
 * <code>HashMap</code>.
 *
 * @author julia schueler
 */
public class MapPointToCornerFactory {

    private final PseudoRegionCornerPoint2DFactory cornerPoint2DFactory;

    private static enum PolygonMode {

        base, pseudo, normal
    };

    /**
     * Constructor with the base polygon of a tesselation.
     *
     * @param basePolygon base polygon
     * @param pseudoRegionPolygones tesselation of the base polygon in pseudo
     * regions.
     */
    public MapPointToCornerFactory(PolygonSimple basePolygon, List<PolygonSimple> pseudoRegionPolygones) {
        this(new PseudoRegionCornerPoint2DFactory(basePolygon, pseudoRegionPolygones));
    }

    /**
     * Constructor with the
     * <code>CornerPoint2D</code> factory for pseudo region tesselation.
     *
     * @param cornerPoint2DFactory <code>CornerPoint2D</code> factory for pseudo
     * region tesselation.
     */
    public MapPointToCornerFactory(PseudoRegionCornerPoint2DFactory cornerPoint2DFactory) {
        this.cornerPoint2DFactory = cornerPoint2DFactory;
    }

    /**
     * Returns a
     * <code>HashMap</code> of a given tesselation and a tesselation of the base
     * polygon as pseudo region polygons.
     *
     * The mapped
     * <code>CornerPoint2D</code> is moveable (
     *
     * @see specified by {@link CornerPoint2D#MOVABLE}), if it lays on a side of
     * a pseudo region polygon, if it lays in addition on a base polygon side,
     * but is not a corner of the base polygon, it is a line slider (
     * @see specified by {@link CornerPoint2D#LineSlider}). Otherwise *      * the <code>CornerPoint2D</code> is immovable (
     * @see specified by {@link CornerPoint2D#IMMOVABLE}).
     *
     * @param tesselation tesselation of the base polygon.
     * @return map of a corner of all polygons in the tesselation to *      * a <code>CornerPoint2D</code>.
     */
    public HashMap<Point2D, CornerPoint2D> createPointToCornerMap(List<PolygonSimple> tesselation) {
        HashMap<Point2D, CornerPoint2D> mapPointToCorner = new HashMap<Point2D, CornerPoint2D>();

        addPolygonCorners(cornerPoint2DFactory.getBasePolygon(), mapPointToCorner, PolygonMode.base);

        for (PolygonSimple polygonSimple : cornerPoint2DFactory.getPseudoRegionPolygones()) {
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
                    CornerPoint2D cornerPoint2D = cornerPoint2DFactory.getLineSliderOnBasePolygonSide(x, y);
                    if (cornerPoint2D == null) {
                        cornerPoint2D = new CornerPoint2D.Movable(x, y);
                    }
                    mapPointToCorner.put(point, cornerPoint2D);
                }
                break;
            case normal:
                if (!mapPointToCorner.containsKey(point)) {
                    CornerPoint2D cornerPoint2D = cornerPoint2DFactory.getCornerPointOnTesselationSide(x, y);
                    mapPointToCorner.put(point, cornerPoint2D);
                }
                break;
        }
    }
}
