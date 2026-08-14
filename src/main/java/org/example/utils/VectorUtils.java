package org.example.utils;

import org.example.entity.Point;

import static java.lang.Math.sqrt;

public class VectorUtils {

    public static Point geDirection(Point point, Point other) {
        double denominator = point.getDeterminant();
        double otherDenominator = other.getDeterminant();

        return new Point(other.getNumX() * denominator - point.getNumX() * otherDenominator, other.getNumY() * denominator - point.getNumY() * otherDenominator, denominator * otherDenominator);
    }

    public static double dotProduct(Point point, Point other) {
        double numerator = point.getNumX() * other.getNumX() + point.getNumY() * other.getNumY();
        double denominator = point.getDeterminant() * other.getDeterminant();
        return numerator * denominator;
    }

    public static double crossProduct(Point point, Point other) {
        double numerator = point.getNumX() * other.getNumY() - point.getNumY() * other.getNumX();
        double denominator = point.getDeterminant() * other.getDeterminant();
        return numerator * denominator;
    }

    public static double getLength(Point point, Point other) {
        return sqrt(Math.pow(other.getX() - point.getX(), 2) + Math.pow(other.getY() - point.getY(), 2));
    }
}