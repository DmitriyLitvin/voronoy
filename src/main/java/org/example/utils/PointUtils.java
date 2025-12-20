package org.example.utils;

import org.example.entity.Point;

import static java.lang.Math.sqrt;

public class PointUtils {

    public static double getLength(Point a, Point b) {
        return sqrt(getSumOfSquares(a, b));
    }

    public static double getSumOfSquares(Point a, Point b) {
        return Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2);
    }

    public static double crossProduct(Point a, Point b) {
        return a.getX() * b.getY() - a.getY() * b.getX();
    }

    public static double dotProduct(Point a, Point b) {
        return a.getX() * b.getX() + a.getY() * b.getY();
    }
}
