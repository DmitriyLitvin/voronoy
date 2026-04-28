package org.example.utils;

import org.example.entity.Vertex;

import static java.lang.Math.sqrt;

public class VectorUtils {

    public static double getLength(Vertex v1, Vertex v2) {
        return sqrt(Math.pow(v2.getX() - v1.getX(), 2) + Math.pow(v2.getY() - v1.getY(), 2));
    }

    public static double crossProduct(Vertex v1, Vertex v2) {
        return v1.getX() * v2.getY() - v1.getY() * v2.getX();
    }

    public static double dotProduct(Vertex v1, Vertex v2) {
        return v1.getX() * v2.getX() + v1.getY() * v2.getY();
    }

    public static Vertex getDirectionPoint(Vertex v1, Vertex v2) {
        return new Vertex(v2.getX() - v1.getX(), v2.getY() - v1.getY());
    }

}
