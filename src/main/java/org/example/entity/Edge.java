package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Edge {
    private Point leftPoint;
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
}
