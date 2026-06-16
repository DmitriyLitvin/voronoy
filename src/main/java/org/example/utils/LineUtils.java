package org.example.utils;

import org.example.entity.Line;
import org.example.entity.Point;

import static org.example.utils.VectorUtils.crossProduct;

public class LineUtils {
    private static final  double HEIGHT = 1000000;
    private static final double WIDTH = 1000000;


    public static Point getPointOfIntersection(Line perpendicular, Line line) {
        Point p1 = perpendicular.getA();
        Point p2 = perpendicular.getB();

        Point p3 = line.getA();
        Point p4 = line.getB();

        double d1 = p2.getX() - p1.getX();
        double d2 = p2.getY() - p1.getY();

        double d3 = p4.getX() - p3.getX();
        double d4 = p4.getY() - p3.getY();

        if (d1 == 0) {
            return new Point(p1.getX(), line.getY(p1.getX()));
        } else if (d3 == 0) {
            return new Point(p3.getX(), perpendicular.getY(p3.getX()));
        }

        double s1 = d2 / d1;
        double s2 = d4 / d3;

        if (d2 * d3 - d4 * d1 == 0) {
            return null;
        }

        double x = (p3.getY() - p1.getY() + p1.getX() * s1 - p3.getX() * s2) / (s1 - s2);
        return new Point(x, perpendicular.getY(x));
    }

    public static Line getPerpendicular(Line line) {
        Point middlePoint = line.getMidPoint();
        double x = middlePoint.getX();
        double y = middlePoint.getY();

        Point direction = VectorUtils.geDirection(line.getA(), line.getB());
        if (VectorUtils.dotProduct(direction, new Point(1, 0)) == 0) {
            return new Line(new Point(WIDTH, y), new Point(-WIDTH, y));
        } else if (VectorUtils.dotProduct(direction, new Point(0, 1)) == 0) {
            return new Line(new Point(x, -HEIGHT), new Point(x, HEIGHT));
        } else {
            return new Line(new Point(((y + HEIGHT) * direction.getY()) / direction.getX() + x, -HEIGHT), new Point((-(HEIGHT - y) * direction.getY()) / direction.getX() + x, HEIGHT));
        }
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
