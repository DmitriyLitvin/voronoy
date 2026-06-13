package org.example.utils;

import org.example.entity.Cell;
import org.example.entity.Edge;
import org.example.entity.Point;

import java.util.Objects;

import static org.example.utils.VectorUtils.crossProduct;

public class EdgeUtils {


    public static boolean isConnected(Edge e1, Edge e2) {
        return Objects.equals(e1.getPoint(), e2.getTwin().getPoint()) || Objects.equals(e1.getTwin().getPoint(), e2.getPoint());
    }

    public static boolean contains(Edge e, Point v) {
        return Objects.equals(v, e.getPoint()) || Objects.equals(v, e.getTwin().getPoint());
    }

    public static boolean isEquals(Edge e1, Edge e2) {
        Point p1 = e1.getPoint();
        Point p2 = e1.getTwin().getPoint();
        Point p3 = e2.getPoint();
        Point p4 = e2.getTwin().getPoint();

        return (Objects.equals(p1, p3) && Objects.equals(p2, p4)) || (Objects.equals(p1, p4) && Objects.equals(p2, p3));
    }

    public static Point getPoint(Edge e1, Edge e2) {
        Point p1 = e1.getPoint();
        Point p2 = e1.getTwin().getPoint();
        Point p3 = e2.getPoint();
        Point p4 = e2.getTwin().getPoint();

        if (Objects.equals(p1, p3) || Objects.equals(p1, p4)) {
            return p1;
        } else if (Objects.equals(p2, p3) || Objects.equals(p2, p4)) {
            return p2;
        }

        return null;
    }

    public static Point getOtherPoint(Edge e, Point v) {
        Point p1 = e.getPoint();
        Point p2 = e.getTwin().getPoint();

        if (Objects.equals(p1, v)) {
            return p2;
        } else if (Objects.equals(p2, v)) {
            return p1;
        }

        return null;
    }

    public static Edge eraseEdges(Edge e, Point v) {
        Edge nextEdge = e.getNext();
        if (nextEdge != null && contains(nextEdge, v)) {
            e.setNext(null);
            nextEdge.setPrev(null);

            if (!e.getCell().isClosed()) {
                return nextEdge;
            }
        }

        Edge prevEdge = e.getPrev();
        if (prevEdge != null && contains(prevEdge, v)) {
            e.setPrev(null);
            prevEdge.setNext(null);

            if (!e.getCell().isClosed()) {
                return prevEdge;
            }
        }

        return null;
    }

    public static Edge getConnectedEdge(Edge e, Point v) {
        Edge startEdge = e.getStartEdge();
        Edge lastEdge = e.getLastEdge();
        if (startEdge == null || lastEdge == null) {
            return null;
        }

        if (contains(startEdge, v)) {
            return startEdge;
        } else if (contains(lastEdge, v)) {
            return lastEdge;
        }

        return null;
    }

    public static Point getPoint(Edge e) {
        if (e.getNext() != null) {
            Edge nextEdge = e.getNext();
            if (contains(nextEdge, e.getPoint())) {
                return e.getTwin().getPoint();
            } else if (contains(nextEdge, e.getTwin().getPoint())) {
                return e.getPoint();
            }
        }
        if (e.getPrev() != null) {
            Edge prevEdge = e.getPrev();
            if (contains(prevEdge, e.getPoint())) {
                return e.getTwin().getPoint();
            } else if (contains(prevEdge, e.getTwin().getPoint())) {
                return e.getPoint();
            }
        }

        return null;
    }

    public static void connectEdges(Edge e1, Edge e2) {
        if (Objects.equals(e1.getTwin().getPoint(), e2.getPoint())) {
            e1.setPrev(e2);
            e2.setNext(e1);
        } else if (Objects.equals(e1.getPoint(), e2.getTwin().getPoint())) {
            e1.setNext(e2);
            e2.setPrev(e1);
        }
    }

    public static boolean isIdle(Edge e) {
        return e.getPrev() == null && e.getNext() == null;
    }

    public static boolean isPointInsideAngle(Point p1, Point p2, Point p3, Point p4) {
        if (p2 == null) {
            return false;
        }

        if (crossProduct(VectorUtils.geDirection(p2, p1), VectorUtils.geDirection(p2, p3)) > 0) {
            return crossProduct(VectorUtils.geDirection(p2, p1), VectorUtils.geDirection(p2, p4)) > 0 && crossProduct(VectorUtils.geDirection(p2, p4), VectorUtils.geDirection(p2, p3)) > 0;
        } else {
            return crossProduct(VectorUtils.geDirection(p2, p1), VectorUtils.geDirection(p2, p4)) < 0 && crossProduct(VectorUtils.geDirection(p2, p4), VectorUtils.geDirection(p2, p3)) < 0;
        }
    }

    public static boolean isOutsideCell(Edge e1, Edge e2, Point p) {
        if (e1 == null || e2 == null) {
            return true;
        }

        Point vertex = EdgeUtils.getPoint(e1, e2);
        if (vertex == null) {
            return true;
        }

        return !isPointInsideAngle(EdgeUtils.getOtherPoint(e1, vertex), vertex, EdgeUtils.getOtherPoint(e2, vertex), p);
    }

    public static boolean isIntersected(Point p, Edge e) {
        boolean isInfinite = e.isInfinite();
        boolean isTwinInfinite = e.getTwin().isInfinite();
        Point p1 = e.getPoint();
        Point p2 = e.getTwin().getPoint();

        if (p == null) {
            return false;
        } else if (isInfinite && isTwinInfinite) {
            return true;
        } else if (!isInfinite && !isTwinInfinite) {
            return VectorUtils.dotProduct(VectorUtils.geDirection(p, p1), VectorUtils.geDirection(p, p2)) < 0;
        } else if (isInfinite) {
            return VectorUtils.dotProduct(VectorUtils.geDirection(p, p2), VectorUtils.geDirection(p1, p2)) > 0;
        }

        return VectorUtils.dotProduct(VectorUtils.geDirection(p, p1), VectorUtils.geDirection(p2, p1)) > 0;
    }

    public static boolean isOnTheSameSide(Point p1, Point p2, Point p3) {
        return VectorUtils.dotProduct(VectorUtils.geDirection(p3, p1), VectorUtils.geDirection(p3, p2)) >= 0;
    }

    public static boolean isConnected(Cell cell, Edge edge) {
        Edge boundary = cell.getBoundary();
        Edge firstChainEdge = edge.getStartEdge();
        Edge lastChainEdge = edge.getLastEdge();

        if (firstChainEdge != null && lastChainEdge != null) {
            Point firstPoint;
            Point lastPoint;

            if (isEquals(firstChainEdge, lastChainEdge)) {
                firstPoint = firstChainEdge.getPoint();
                lastPoint = firstChainEdge.getTwin().getPoint();
            } else {
                firstPoint = getPoint(firstChainEdge);
                lastPoint = getPoint(lastChainEdge);
            }

            Edge firstEdge = null;
            if (firstPoint != null) {
                firstEdge = getConnectedEdge(boundary, firstPoint);
            }

            Edge lastEdge = null;
            if (lastPoint != null) {
                lastEdge = getConnectedEdge(boundary, lastPoint);
            }

            return (firstEdge != null && (isConnected(firstEdge, firstChainEdge) || isConnected(firstEdge, lastChainEdge))) || (lastEdge != null && (isConnected(lastEdge, lastChainEdge) || isConnected(lastEdge, firstChainEdge)));
        }

        return false;
    }

}

