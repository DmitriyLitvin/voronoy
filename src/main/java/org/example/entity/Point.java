package org.example.entity;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
