package org.example.entity;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Edge {
    @EqualsAndHashCode.Include
    private Point a;
    @EqualsAndHashCode.Include
    private Point b;
    private Edge next;
    private Edge prev;
    private Edge twin;
    private Cell cell;
    private boolean isLeft = true;
    private boolean isRight = true;

    public Edge(Point a, Point b, Cell cell) {
        this.a = a;
        this.b = b;
        this.cell = cell;
    }

    public Edge(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    public Edge getStartEdge() {
        Edge prevEdge = this;
        while (true) {
            Edge currentEdge = prevEdge.getPrev();
            if (currentEdge == null) {
                return prevEdge;
            } else if (Objects.equals(new Line(this), new Line(currentEdge))) {
                return null;
            }
            prevEdge = currentEdge;
        }
    }

    public Edge getLastEdge() {
        Edge nextEdge = this;
        while (true) {
            Edge currentEdge = nextEdge.getNext();
            if (currentEdge == null) {
                return nextEdge;
            } else if (Objects.equals(new Line(this), new Line(currentEdge))) {
                return null;
            }
            nextEdge = currentEdge;
        }
    }
}
