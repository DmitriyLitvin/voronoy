package org.example.utils;

import org.example.entity.Edge;
import org.example.entity.Line;
import org.example.entity.Point;

import java.util.Objects;

import static org.example.utils.VectorUtils.crossProduct;

public class LineUtils {
    private static final double SCALE = 10000000;


    public static Point getPointOfIntersection(Line commonSupport, Edge currentEdge) {
        Point lineStart = commonSupport.getA(), lineEnd = commonSupport.getB();
        Point cellCenter = currentEdge.getCell().getCenter(), twinCenter = currentEdge.getTwin().getCell().getCenter();

        double lineWidth  = lineEnd.getX() - lineStart.getX();
        double lineHeight = lineEnd.getY() - lineStart.getY();
        double edgeWidth  = twinCenter.getX() - cellCenter.getX();
        double edgeHeight = twinCenter.getY() - cellCenter.getY();

        double determinant = lineWidth * edgeHeight - edgeWidth * lineHeight;
        if (Math.abs(determinant) < 1e-9) {
            return null;
        }

        return new Point(
                (lineWidth * (lineStart.getX() + lineEnd.getX()) + lineHeight * (lineStart.getY() + lineEnd.getY())) * edgeHeight -
                        (edgeWidth * (cellCenter.getX() + twinCenter.getX()) + edgeHeight * (cellCenter.getY() + twinCenter.getY())) * lineHeight,

                lineWidth * (edgeWidth * (cellCenter.getX() + twinCenter.getX()) + edgeHeight * (cellCenter.getY() + twinCenter.getY())) -
                        edgeWidth * (lineWidth * (lineStart.getX() + lineEnd.getX()) + lineHeight * (lineStart.getY() + lineEnd.getY())),

                2.0 * determinant
        );
    }


    public static Line getPerpendicular(Line line) {
        Point middlePoint = line.getMidPoint();
        Point direction = VectorUtils.geDirection(line.getA(), line.getB());

        double x = direction.getX();
        double y = direction.getY();

        double length = Math.sqrt(x * x + y * y);
        if (length < 1.0) {
            length = 1.0;
        }

        double k = Math.floor(SCALE / length);
        if (k == 0) {
            k = 1;
        }
        x = x * k;
        y = y * k;

        return new Line(new Point(middlePoint.getX() + y, middlePoint.getY() - x), new Point(middlePoint.getX() - y, middlePoint.getY() + x));
    }

    public static boolean is(Point point, Line line, boolean isUpper) {
        Point direction = VectorUtils.geDirection(line.getA(), line.getB());
        double crossProduct = crossProduct(direction, VectorUtils.geDirection(line.getA(), point));
        if (isUpper) {
            return crossProduct > 0;
        }
        return crossProduct < 0;
    }
}
