package org.example.utils;

import org.example.entity.Point;

import static java.lang.Math.sqrt;

public class PointUtils {

    public static double getLength(Point a, Point b) {
        return sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2));
    }

}
