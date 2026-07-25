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
        if (Math.abs(determinant) < 1e-7) {
            System.out.println(determinant);
            return null;
        }
        return new Point((b2 * c1 - b1 * c2) / determinant, (a1 * c2 - a2 * c1) / determinant);
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
