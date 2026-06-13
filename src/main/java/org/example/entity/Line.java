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

    public Line(Edge edge) {
        this.a = edge.getPoint();
        this.b = edge.getTwin().getPoint();
    }

    public double getY(double x) {
        return ((b.getY() - a.getY()) * (x - a.getX())) / (b.getX() - a.getX()) + a.getY();
    }


    public Point getMidPoint() {
        return new Point((a.getX() + b.getX()) / 2, (a.getY() + b.getY()) / 2);
    }
}
