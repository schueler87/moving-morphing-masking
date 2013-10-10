package movingmorphingmasking.data.topology.maskingpseudoregionfactory;

import java.util.ArrayList;
import java.util.List;
import kn.uni.voronoitreemap.j2d.PolygonSimple;

/**
 * Factory for a pseudo region sides detection. It detects possible pseudo
 * region side of a specific tesselation with pseudo region structures.
 *
 * @author julia schueler
 */
public class PseudoRegionSideFactory {

    private List<PolygonSimple> pseudoRegionTesselation;
    private List<PolygonSimple> tesselation;
    private List<PseudoRegionSide> pseudoRegionSides;

    /**
     * Constructor for a
     * <code>PseudoRegionSideFactory</code>. It detects the pseudo region sides
     * of a specific tesselation and a pseudo region tesselation.
     *
     * @param pseudoRegionTesselation tesselation with the unwanted pseudo
     * regions.
     * @param tesselation specific tesselation
     */
    public PseudoRegionSideFactory(List<PolygonSimple> pseudoRegionTesselation, List<PolygonSimple> tesselation) {
        this.pseudoRegionTesselation = pseudoRegionTesselation;
        this.tesselation = tesselation;
        this.pseudoRegionSides = null;
    }

    /**
     * Returns all possible
     * <code>PseudoRegionSide</code>s. During the first call of this method it
     * creates and detects all
     * <code>PseudoRegionSide</code>.
     *
     * @return
     */
    public List<PseudoRegionSide> getPseudoRegionSides() {
        if (pseudoRegionSides == null) {
            detectAllPossiblePseudoRegionSides();
            removeSidesWithOnlyOneAssociatedPolygon();
            addPointsOnPseudoRegionSideAndSortThem();
        }
        return pseudoRegionSides;
    }

    private void detectAllPossiblePseudoRegionSides() {
        pseudoRegionSides = new ArrayList<PseudoRegionSide>();
        for (PolygonSimple polygonSimple : pseudoRegionTesselation) {
            createPseudoRegionSides(polygonSimple);
        }
    }

    private void createPseudoRegionSides(PolygonSimple polygonSimple) {
        double[] xi = polygonSimple.getXPoints();
        double[] yi = polygonSimple.getYPoints();
        int n = xi.length;
        for (int i = 0; i < n; i++) {
            double x1 = xi[i];
            double y1 = yi[i];
            double x2 = xi[(i + 1) % n];
            double y2 = yi[(i + 1) % n];
            PseudoRegionSide pseudoRegionSide = getPseudoRegionSideForPolygonSide(x1, y1, x2, y2);
            pseudoRegionSide.addAssociatedPolygon(polygonSimple);
        }
    }

    private PseudoRegionSide getPseudoRegionSideForPolygonSide(double x1, double y1, double x2, double y2) {
        PseudoRegionSide pseudoRegionSide = getPseudoRegionSide(x1, y1, x2, y2);
        if (pseudoRegionSide == null) {
            pseudoRegionSide = new PseudoRegionSide(x1, y1, x2, y2);
            pseudoRegionSides.add(pseudoRegionSide);
        }
        return pseudoRegionSide;
    }

    private PseudoRegionSide getPseudoRegionSide(double x1, double y1, double x2, double y2) {
        for (PseudoRegionSide pseudoRegionSide : pseudoRegionSides) {
            if (pseudoRegionSide.isEqualSide(x1, y1, x2, y2)) {
                return pseudoRegionSide;
            }
        }
        return null;
    }

    private void removeSidesWithOnlyOneAssociatedPolygon() {
        for (int i = pseudoRegionSides.size() - 1; i >= 0; i--) {
            PseudoRegionSide pseudoRegionSide = pseudoRegionSides.get(i);
            if (pseudoRegionSide.getAssociatedPolygonCount() != 2) {
                pseudoRegionSides.remove(i);
            }
        }
    }

    private void addPointsOnPseudoRegionSideAndSortThem() {
        for (PseudoRegionSide pseudoRegionSide : pseudoRegionSides) {
            addPointsOnPseudoRegion(pseudoRegionSide);
            pseudoRegionSide.sortPointsOnSide();
        }
    }

    private void addPointsOnPseudoRegion(PseudoRegionSide pseudoRegionSide) {
        for (PolygonSimple polygonSimple : tesselation) {
            addPseudoRegionPoints(pseudoRegionSide, polygonSimple);
        }
    }

    private void addPseudoRegionPoints(PseudoRegionSide pseudoRegionSide, PolygonSimple polygonSimple) {
        double[] xi = polygonSimple.getXPoints();
        double[] yi = polygonSimple.getYPoints();
        int n = xi.length;
        for (int i = 0; i < n; i++) {
            double x1 = xi[i];
            double y1 = yi[i];
            pseudoRegionSide.addPoint(x1, y1);
        }
    }
}
