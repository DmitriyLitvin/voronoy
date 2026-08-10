package org.example.entity;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Edge {
    private Point point;
    private Edge next;
    private Edge prev;
    private Edge twin;
    private Cell cell;
    private boolean isInfinite = true;

    public Edge(Point point) {
        this.point = point;
    }

    public Edge(Point point, Cell cell) {
        this.point = point;
        this.cell = cell;
    }


    public Edge getStart() {
        Edge prevEdge = this;
        while (true) {
            Edge currentEdge = prevEdge.getPrev();
            if (currentEdge == null) {
                return prevEdge;
            } else if (equals(currentEdge)) {
                return null;
            }
            prevEdge = currentEdge;
        }
    }

    public Edge getLast() {
        Edge nextEdge = this;
        while (true) {
            Edge currentEdge = nextEdge.getNext();
            if (currentEdge == null) {
                return nextEdge;
            } else if (equals(currentEdge)) {
                return null;
            }
            nextEdge = currentEdge;
        }
    }

    public boolean isConnected(Edge edge) {
        return Objects.equals(edge.getPoint(), this.getTwin().getPoint()) || Objects.equals(edge.getTwin().getPoint(), this.getPoint());
    }

    public  void connect(Edge edge) {
        if (Objects.equals(edge.getTwin().getPoint(), this.getPoint())) {
            edge.setPrev(this);
            this.setNext(edge);
        } else if (Objects.equals(edge.getPoint(), this.getTwin().getPoint())) {
            edge.setNext(this);
            this.setPrev(edge);
        }
    }

    public Point getCommonVertex(Edge edge) {
        if (edge == null) {
            return null;
        }

        Point startPoint = this.getPoint();
        Point endPoint = this.getTwin().getPoint();

        Point vertex = null;
        if (Objects.equals(startPoint, edge.getTwin().getPoint())) {
            vertex = startPoint;
        } else if (Objects.equals(endPoint, edge.getPoint())) {
            vertex = endPoint;
        }

        return vertex;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Edge other)) {
            return false;
        }

        return (Objects.equals(getPoint(), other.getPoint()) && Objects.equals(getTwin().getPoint(), other.getTwin().getPoint()));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getPoint());
    }
}
