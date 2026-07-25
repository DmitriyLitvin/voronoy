package org.example.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Line {
    private Point a;
    private Point b;

    public Line(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    public Point getMidPoint() {
        return new Point((a.getX() + b.getX()) / 2, (a.getY() + b.getY()) / 2);
    }
}
