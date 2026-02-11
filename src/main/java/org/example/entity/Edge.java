package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.DeepCopyHelper;

import java.util.Objects;

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

    public Edge getStartEdge() {
        Edge currentEdge = this;
        while (true) {
            Edge prevEdge = currentEdge.deepCopy();
            currentEdge = currentEdge.getPrev();
            if (currentEdge == null) {
                return prevEdge;
            } else if (Objects.equals(new Line(this), new Line(currentEdge))) {
                return null;
            }
        }
    }

    public Edge getLastEdge() {
        Edge currentEdge = this;
        while (true) {
            Edge prevEdge = currentEdge.deepCopy();
            currentEdge = currentEdge.getNext();
            if (currentEdge == null) {
                return prevEdge;
            } else if (Objects.equals(new Line(this), new Line(currentEdge))) {
                return null;
            }
        }
    }
}
