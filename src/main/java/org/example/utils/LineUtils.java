package org.example.utils;

import org.example.entity.Edge;
import org.example.entity.Line;
import org.example.entity.Point;

import java.util.Objects;

import static org.example.utils.VectorUtils.crossProduct;

public class LineUtils {
    private static final  double SCALE = 1000000;


    public static Point getPointOfIntersection(Line commonSupport, Edge currentEdge) {
        Point p1 = commonSupport.getA();
        Point p2 = commonSupport.getB();
        Point p3 = currentEdge.getCell().getCenter();
        Point p4 = currentEdge.getTwin().getCell().getCenter();

        double a1 = p2.getX() - p1.getX();
        double b1 = p2.getY() - p1.getY();
        double a2 = p4.getX() - p3.getX();
        double b2 = p4.getY() - p3.getY();

        double c1 = a1 * (p1.getX() + p2.getX()) + b1 * (p1.getY() + p2.getY());
        double c2 = a2 * (p3.getX() + p4.getX()) + b2 * (p3.getY() + p4.getY());

        double determinant = a1 * b2 - a2 * b1;
        if (Math.abs(determinant) == 0) {
            System.out.println(55);
            return null;
        }

        return new Point((c1 * b2 - c2 * b1), (a1 * c2 - a2 * c1), 2.0 * determinant);
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

        return new Line(new Point( middlePoint.getX() + y,  middlePoint.getY() - x), new Point(middlePoint.getX() - y, middlePoint.getY() + x));
    }

    public static boolean is(Point point, Line line, boolean isUpper) {
        Point direction = VectorUtils.geDirection(line.getA(), line.getB());
        double cp = crossProduct(direction, VectorUtils.geDirection(line.getA(), point));
        if (isUpper) {
            return cp > 0;
        }
        return cp < 0;
    }
}
