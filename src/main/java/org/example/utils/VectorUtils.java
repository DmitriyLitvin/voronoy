package org.example.utils;

import org.example.entity.Point;

import static java.lang.Math.sqrt;

public class VectorUtils {

    public static double getLength(Point firstPoint, Point secondPoint) {
        return sqrt(Math.pow(secondPoint.getX() - firstPoint.getX(), 2) + Math.pow(secondPoint.getY() - firstPoint.getY(), 2));
    }

    public static double crossProduct(Point firstPoint, Point secondPoint) {
        return firstPoint.getX() * secondPoint.getY() - firstPoint.getY() * secondPoint.getX();
    }

    public static double dotProduct(Point firstPoint, Point secondPoint) {
        return firstPoint.getX() * secondPoint.getX() + firstPoint.getY() * secondPoint.getY();
    }

    public static Point getDirectionPoint(Point firstPoint, Point secondPoint) {
        return new Point(secondPoint.getX() - firstPoint.getX(), secondPoint.getY() - firstPoint.getY());
    }
}
