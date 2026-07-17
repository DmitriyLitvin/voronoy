package org.example.utils;

import org.example.entity.Line;
import org.example.entity.Point;

import static org.example.utils.VectorUtils.crossProduct;

public class LineUtils {
    private static final  double SCALE = 1000000;


    public static Point getPointOfIntersection(Line line, Line other) {
        Point p1 = line.getA();
        Point p2 = line.getB();
        Point p3 = other.getA();
        Point p4 = other.getB();

        // Коэффициенты первой линии: A1*x + B1*y = C1
        double a1 = p2.getY() - p1.getY();
        double b1 = p1.getX() - p2.getX();
        double c1 = a1 * p1.getX() + b1 * p1.getY();

        // Коэффициенты второй линии: A2*x + B2*y = C2
        double a2 = p4.getY() - p3.getY();
        double b2 = p3.getX() - p4.getX();
        double c2 = a2 * p3.getX() + b2 * p3.getY();

        // Главный определитель матрицы (determinant)
        double determinant = a1 * b2 - a2 * b1;

        // Если определитель равен 0, линии параллельны или совпадают
        if (Math.abs(determinant) < 1e-9) {
            return null;
        }

        // Находим координаты X и Y по правилу Крамера
        double x = (b2 * c1 - b1 * c2) / determinant;
        double y = (a1 * c2 - a2 * c1) / determinant;

        return new Point(x, y);
    }

    public static Line getPerpendicular(Line line) {
        Point middlePoint = line.getMidPoint();
        double x = middlePoint.getX();
        double y = middlePoint.getY();

        Point direction = VectorUtils.geDirection(line.getA(), line.getB());
        if (VectorUtils.dotProduct(direction, new Point(1, 0)) == 0) {
            return new Line(new Point(SCALE, y), new Point(-SCALE, y));
        } else if (VectorUtils.dotProduct(direction, new Point(0, 1)) == 0) {
            return new Line(new Point(x, -SCALE), new Point(x, SCALE));
        } else {
            return new Line(new Point(((y + SCALE) * direction.getY()) / direction.getX() + x, -SCALE), new Point((-(SCALE - y) * direction.getY()) / direction.getX() + x, SCALE));
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
