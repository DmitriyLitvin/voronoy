package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.DeepCopyHelper;

@Getter
@Setter
@NoArgsConstructor
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

    private static DeepCopyHelper<Edge> helper = new DeepCopyHelper<>();

    public Edge deepCopy() {
        return helper.copy(this);
    }

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
