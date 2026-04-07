package org.example.utils;

import org.example.entity.Point;

import static java.lang.Math.sqrt;

public class VectorUtils {

    public static double getLength(Point p1, Point p2) {
        return sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
    }

    public static double crossProduct(Point p1, Point p2) {
        return p1.getX() * p2.getY() - p1.getY() * p2.getX();
    }

    public static double dotProduct(Point p1, Point p2) {
        return p1.getX() * p2.getX() + p1.getY() * p2.getY();
    }

    public static Point getDirectionPoint(Point p1, Point p2) {
        return new Point(p2.getX() - p1.getX(), p2.getY() - p1.getY());
    }
}
