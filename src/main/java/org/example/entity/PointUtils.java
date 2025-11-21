package org.example.entity;

import static java.lang.Math.sqrt;

public class PointUtils {

    public static double getLength(Point a, Point b) {
        return sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2));
    }

}
