package org.example.utils;

import org.example.entity.Point;

import static java.lang.Math.sqrt;

public class VectorUtils {

    public static double getLength(Point start, Point end) {
        return sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2));
    }

    public static double crossProduct(Point leftVector, Point rightVector) {
        return leftVector.getX() * rightVector.getY() - leftVector.getY() * rightVector.getX();
    }

    public static double dotProduct(Point leftVector, Point rightVector) {
        return leftVector.getX() * rightVector.getX() + leftVector.getY() * rightVector.getY();
    }

    public static Point geDirection(Point start, Point end) {
        return new Point(end.getX() - start.getX(), end.getY() - start.getY());
    }
}
