package org.example.utils;

import org.example.entity.Point;

import static java.lang.Math.sqrt;

public class VectorUtils {

    public static double getLength(Point point, Point other) {
        return sqrt(Math.pow(other.getX() - point.getX(), 2) + Math.pow(other.getY() - point.getY(), 2));
    }

    public static double crossProduct(Point point, Point other) {
        return point.getX() * other.getY() - point.getY() * other.getX();
    }

    public static Point geDirection(Point point, Point other) {
        return new Point(other.getX() - point.getX(), other.getY() - point.getY());
    }
}
