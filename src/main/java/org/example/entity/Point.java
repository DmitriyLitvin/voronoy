package org.example.entity;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Point {
    private double x;
    private double y;
    private double determinant = 1;

    public Point(double x, double y, double determinant) {
        this.x = x;
        this.y = y;
        this.determinant = determinant;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x / determinant;
    }

    public double getY() {
        return y / determinant;
    }
}

