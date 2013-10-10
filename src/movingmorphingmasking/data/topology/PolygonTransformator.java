package movingmorphingmasking.data.topology;

import java.util.ArrayList;
import java.util.List;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import movingmorphingmasking.data.topology.maskingpseudoregionfactory.PseudoRegionHolder;

/**
 * Transformates <code>PolygonDeformable</code> into a <code>PolygonSimple</code>
 * and the other way round. 
 * @author julia schueler
 */
public class PolygonTransformator {

    /**
     * Transformate a <code>PolygonDeformable</code> into a <code>PolygonSimple</code>.
     * @param polygonDeformable <code>PolygonDeformable</code>
     * @return transformated <code>PolygonSimple</code>
     */
    public static PolygonSimple transformateIntoPolygonSimple(PolygonDeformable polygonDeformable) {
        CornerPoint2D[] corners = polygonDeformable.getCorners();
        double[] xPoints = new double[corners.length];
        double[] yPoints = new double[corners.length];
        for (int i = 0; i < corners.length; i++) {
            CornerPoint2D cornerPoint2D = corners[i];
            xPoints[i] = cornerPoint2D.getX();
            yPoints[i] = cornerPoint2D.getY();
        }
        return new PolygonSimple(xPoints, yPoints);
    }

    /**
     * Transformates on the basis of a pseudo region tesselation region 
     * holds in the <code>PseudoRegionHolder</code> all <code>PolygonSimple</code>s
     * into <code>PolygonDeformable</code>.
     * @param pseudoRegionHolder holds the whole situation of a pseudo region tesselation.
     * @return tesselation of <code>PolygonDeformable</code>.
     */
    public static List<PolygonDeformable> transformatePolygonDeformables(PseudoRegionHolder pseudoRegionHolder) {

        List<PolygonDeformable> deformablePolygons = new ArrayList<PolygonDeformable>();
        for (PolygonSimple polygonSimple : pseudoRegionHolder.getTesselation()) {
            PolygonDeformable polygonDeformable = transformateIntoPolygonDeformable(polygonSimple, pseudoRegionHolder);
            deformablePolygons.add(polygonDeformable);
        }
        return deformablePolygons;
    }

    /**
     * Transformate on the basis of a pseudo region tesselation region 
     * holds in the <code>PseudoRegionHolder</code> a specific <code>PolygonSimple</code>s
     * into a <code>PolygonDeformable</code>.
     * @param polygonSimple specific polygoneSimple to tranformate
     * @param pseudoRegionHolder holds the whole situation of a pseudo region tesselation.
     * @return <code>PolygonDeformable</code>
     */
    public static PolygonDeformable transformateIntoPolygonDeformable(PolygonSimple polygonSimple,
            PseudoRegionHolder pseudoRegionHolder) {

        CornerPoint2D[] points = pseudoRegionHolder.getCornerPoints(polygonSimple);
        return new PolygonDeformable(points, polygonSimple.getArea());
    }
}
