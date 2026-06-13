package org.example.utils;

import org.example.entity.Line;
import org.example.entity.Point;

import static org.example.utils.VectorUtils.crossProduct;

public class LineUtils {

    public static Point getPointOfIntersection(Line l1, Line l2) {
        Point p1 = l1.getA();
        Point p2 = l1.getB();

        Point p3 = l2.getA();
        Point p4 = l2.getB();

        double d1 = p2.getX() - p1.getX();
        double d2 = p2.getY() - p1.getY();

        double d3 = p4.getX() - p3.getX();
        double d4 = p4.getY() - p3.getY();

        if (d1 == 0) {
            return new Point(p1.getX(), l2.getY(p1.getX()));
        } else if (d3 == 0) {
            return new Point(p3.getX(), l1.getY(p3.getX()));
        }

        double s1 = d2 / d1;
        double s2 = d4 / d3;

        if (d2 * d3 - d4 * d1 == 0) {
            return null;
        }

        double x = (p3.getY() - p1.getY() + p1.getX() * s1 - p3.getX() * s2) / (s1 - s2);
        return new Point(x, l1.getY(x));
    }

    public static Line getPerpendicular(Line l) {
        double height = 1000000;
        double width = 1000000;

        Point midPoint = l.getMidPoint();
        double x = midPoint.getX();
        double y = midPoint.getY();

        Point direction = VectorUtils.geDirection(l.getA(), l.getB());
        if (VectorUtils.dotProduct(direction, new Point(1, 0)) == 0) {
            return new Line(new Point(width, y), new Point(-width, y));
        } else if (VectorUtils.dotProduct(direction, new Point(0, 1)) == 0) {
            return new Line(new Point(x, -height), new Point(x, height));
        } else {
            return new Line(new Point(((y + height) * direction.getY()) / direction.getX() + x, -height), new Point((-(height - y) * direction.getY()) / direction.getX() + x, height));
        }
    }

    public static boolean is(Point p, Line l, boolean isUpper) {
        Point direction = VectorUtils.geDirection(l.getA(), l.getB());
        double cp = crossProduct(direction, VectorUtils.geDirection(l.getA(), p));
        if (isUpper) {
            return cp > 0;
        }
        return cp < 0;
    }
}
