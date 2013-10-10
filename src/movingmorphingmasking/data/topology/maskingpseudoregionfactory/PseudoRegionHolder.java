package movingmorphingmasking.data.topology.maskingpseudoregionfactory;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import movingmorphingmasking.data.topology.CornerPoint2D;

/**
 * Holder for geometric details and structures of a tesselation with 
 * pseudo region.
 * @author julia schueler
 */
public final class PseudoRegionHolder {

    private PolygonSimple basePolygon;
    private List<PolygonSimple> pseudoRegionTesselation;
    private List<PolygonSimple> tesselation;
    private HashMap<Point2D, CornerPoint2D> mapPointToCorner;
    private List<PseudoRegionSide> pseudoRegionSides;

    /**
     * Constructor for a specific tesselation with pseudo regions.
     * It based on the basePolygon that contains the pseudo regions tesselation.
     * @param basePolygon  base polygon of the tesselation
     * @param pseudoRegionTesselation polygones the shows the pseudo region structure.
     * @param tesselation  tesselation of the basePolygon
     */
    public PseudoRegionHolder(PolygonSimple basePolygon,
            List<PolygonSimple> pseudoRegionTesselation, List<PolygonSimple> tesselation) {

        this.basePolygon = basePolygon;
        this.pseudoRegionTesselation = pseudoRegionTesselation;
        this.tesselation = tesselation;
        this.mapPointToCorner = getPointToCornerMap();
        this.pseudoRegionSides = createPseudoRegionSides();
    }

    /**
     * Returns the base polygon of the tesselation with pseudo regions.
     * @return 
     */
    public PolygonSimple getBasePolygon() {
        return basePolygon;
    }

    /**
     * Returns a <code>HashMap</code> that maps all corners of the polygons
     * of the tesselation to a <code>CornerPoint2D</code>.
     * 
     * @return map of polygon corners to <code>CornerPoint2D</code>.
     */
    public HashMap<Point2D, CornerPoint2D> getMapPointToCorner() {
        return mapPointToCorner;
    }

    /**
     * Returns the tesselation of the base polygon.
     * @return tesselation
     */
    public List<PolygonSimple> getTesselation() {
        return tesselation;
    }

    /**
     * Returns the pseudo region structure as a tesselation.
     * @return pseudo region structure tesselation.
     */
    public List<PolygonSimple> getPseudoRegionPolygones() {
        return pseudoRegionTesselation;
    }

    /**
     * Returns the <code>PseudoRegionSide</code>s of this tesselation.
     * @return 
     */
    public List<PseudoRegionSide> getPseudoRegionSides() {
        return pseudoRegionSides;
    }

    /**
     * Returns all <code>CornerPoint2D</code> of a specific polygon of this
     * tesselation. It might be more <code>CornerPoint2D</code>s the the polygon has
     * corners. The reasons are the pseudo region sides.
     * @param tesselationPolygon polygon of the tesselation.
     * @return <code>CornerPoint2D</code> associated to a specific polygon.
     */
    public CornerPoint2D[] getCornerPoints(PolygonSimple tesselationPolygon) {
        List<CornerPoint2D> cornerPoint2Ds = new ArrayList<CornerPoint2D>();

        double[] xi = tesselationPolygon.getXPoints();
        double[] yi = tesselationPolygon.getYPoints();
        int n = xi.length;

        for (int i = 0; i < n; i++) {
            double x1 = xi[i];
            double y1 = yi[i];
            double x2 = xi[(i + 1) % n];
            double y2 = yi[(i + 1) % n];
            CornerPoint2D[] cornerPointOnSide = createCornerPointOnSide(x1, y1, x2, y2);
            cornerPoint2Ds.addAll(Arrays.asList(cornerPointOnSide));
        }
        return cornerPoint2Ds.toArray(new CornerPoint2D[cornerPoint2Ds.size()]);
    }

    private CornerPoint2D[] createCornerPointOnSide(double x1, double y1, double x2, double y2) {
        List<Point2D> pointsOnSide = getPointsOnSide(x1, y1, x2, y2);
        CornerPoint2D[] corners = new CornerPoint2D[pointsOnSide.size()];
        for (int i = 1; i < pointsOnSide.size(); i++) {
            Point2D point2D = pointsOnSide.get(i);
            corners[i] = mapPointToCorner.get(point2D);
        }
        return corners;
    }

    private List<Point2D> getPointsOnSide(double x1, double y1, double x2, double y2) {
        for (PseudoRegionSide punnetSide : pseudoRegionSides) {
            List<Point2D> pointsOnSideBetween = punnetSide.getPointsOnSideBetween(x1, y1, x2, y2);
            if (!pointsOnSideBetween.isEmpty()) {
                return pointsOnSideBetween;
            }
        }
        return Arrays.asList(new Point2D[]{new Point2D.Double(x1, y1), new Point2D.Double(x2, y2)});
    }
    
    private HashMap<Point2D, CornerPoint2D> getPointToCornerMap() {
        MapPointToCornerFactory mapPointToCornerFactory = new MapPointToCornerFactory(basePolygon);
        return mapPointToCornerFactory.createPointToCornerMap(pseudoRegionTesselation, tesselation);
    }

    private List<PseudoRegionSide> createPseudoRegionSides() {
        PseudoRegionSideFactory punnetSideFactory = new PseudoRegionSideFactory(pseudoRegionTesselation, tesselation);
        return punnetSideFactory.getPseudoRegionSides();
    }
}
