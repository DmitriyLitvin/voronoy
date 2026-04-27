package org.example.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Line {
    private Vertex a;
    private Vertex b;

    public Line(Vertex a, Vertex b) {
        this.a = a;
        this.b = b;
    }

    public Line(Edge edge) {
        this.a = edge.getVertex();
        this.b = edge.getTwin().getVertex();
    }

    public double getY(double x) {
        return ((b.getY() - a.getY()) * (x - a.getX())) / (b.getX() - a.getX()) + a.getY();
    }


    public Vertex getMidVertex() {
        return new Vertex((a.getX() + b.getX()) / 2, (a.getY() + b.getY()) / 2);
    }
}
