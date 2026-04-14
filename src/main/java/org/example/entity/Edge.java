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
    private Point leftPoint;
    @EqualsAndHashCode.Include
    private Point rightPoint;
    private Edge next;
    private Edge prev;
    private Edge twin;
    private Cell cell;
    private boolean isInfiniteLeftEnd = true;
    private boolean isInfiniteRightEnd = true;

    public Edge(Point leftPoint, Point rightPoint, Cell cell) {
        this.leftPoint = leftPoint;
        this.rightPoint = rightPoint;
        this.cell = cell;
    }

    public Edge(Point leftPoint, Point rightPoint) {
        this.leftPoint = leftPoint;
        this.rightPoint = rightPoint;
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
