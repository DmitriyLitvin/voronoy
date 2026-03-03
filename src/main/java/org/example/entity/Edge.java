package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.DeepCopyHelper;
import org.example.utils.VectorUtils;

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

    public Edge getConnectedEdge(Point point) {
        Edge currentEdge = this;
        while (isConnected(point, currentEdge)) {
            currentEdge = currentEdge.getPrev();
            if (currentEdge == null || Objects.equals(new Line(this), new Line(currentEdge))) {
                break;
            }
        }

        if (currentEdge != null) {
            return currentEdge;
        }

        currentEdge = this;
        while (isConnected(point, currentEdge)) {
            currentEdge = currentEdge.getNext();
            if (currentEdge == null || Objects.equals(new Line(this), new Line(currentEdge))) {
                break;
            }
        }

        return currentEdge;
    }

    private boolean isConnected(Point point, Edge currentEdge) {
        return !Objects.equals(point, currentEdge.getRightPoint()) && !Objects.equals(point, currentEdge.getLeftPoint());
    }
}
