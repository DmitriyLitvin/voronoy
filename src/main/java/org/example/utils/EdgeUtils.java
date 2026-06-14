package org.example.utils;

import org.example.entity.Cell;
import org.example.entity.Edge;
import org.example.entity.Point;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.example.utils.VectorUtils.crossProduct;

public class EdgeUtils {


    public static boolean contains(Edge edge, Point point) {
        return Objects.equals(point, edge.getPoint()) || Objects.equals(point, edge.getTwin().getPoint());
    }

    public static Point getOtherPoint(Edge edge, Point point) {
        Point startPoint = edge.getPoint();
        Point endPoint = edge.getTwin().getPoint();

        if (Objects.equals(startPoint, point)) {
            return endPoint;
        } else if (Objects.equals(endPoint, point)) {
            return startPoint;
        }

        return null;
    }

    private static boolean containsEdge(Edge startEdge, Edge targetEdge) {
        Edge currentEdge = startEdge;
        do {
            if (Objects.equals(currentEdge, targetEdge)) {
                return true;
            }
            currentEdge = currentEdge.getNext();
        } while (currentEdge != null && !Objects.equals(currentEdge, startEdge));

        currentEdge = startEdge;
        do {
            if (Objects.equals(currentEdge, targetEdge)) {
                return true;
            }
            currentEdge = currentEdge.getPrev();
        } while (currentEdge != null && !Objects.equals(currentEdge, startEdge));

        return false;
    }


    public static Edge eraseEdges(Map<Cell, List<Edge>> excludedEdges, Edge edge, Point point) {
        List<Edge> edges = excludedEdges.get(edge.getCell());
        if (edges != null) {
            edges.replaceAll(e -> containsEdge(e, edge) ? edge : e);
        }

        Edge nextEdge = edge.getNext();
        if (nextEdge != null && contains(nextEdge, point)) {
            edge.setNext(null);
            nextEdge.setPrev(null);

            if (!edge.getCell().isClosed()) {
                return nextEdge;
            }
        }

        Edge prevEdge = edge.getPrev();
        if (prevEdge != null && contains(prevEdge, point)) {
            edge.setPrev(null);
            prevEdge.setNext(null);

            if (!edge.getCell().isClosed()) {
                return prevEdge;
            }
        }

        return null;
    }

    public static Edge getConnectedEdge(Edge edge, Point point) {
        Edge startEdge = edge.getStartEdge();
        Edge lastEdge = edge.getLastEdge();
        if (startEdge == null || lastEdge == null) {
            return null;
        }

        if (contains(startEdge, point)) {
            return startEdge;
        } else if (contains(lastEdge, point)) {
            return lastEdge;
        }

        return null;
    }

    public static Point getPoint(Edge edge) {
        if (edge.getNext() != null) {
            Edge nextEdge = edge.getNext();
            if (contains(nextEdge, edge.getPoint())) {
                return edge.getTwin().getPoint();
            } else if (contains(nextEdge, edge.getTwin().getPoint())) {
                return edge.getPoint();
            }
        }
        if (edge.getPrev() != null) {
            Edge prevEdge = edge.getPrev();
            if (contains(prevEdge, edge.getPoint())) {
                return edge.getTwin().getPoint();
            } else if (contains(prevEdge, edge.getTwin().getPoint())) {
                return edge.getPoint();
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

    public static boolean isIdle(Edge edge) {
        return edge.getPrev() == null && edge.getNext() == null;
    }

    public static boolean isIntersected(Point point, Edge edge) {
        boolean isInfinite = edge.isInfinite();
        boolean isTwinInfinite = edge.getTwin().isInfinite();
        Point startPoint = edge.getPoint();
        Point endPoint = edge.getTwin().getPoint();

        if (point == null) {
            return false;
        } else if (isInfinite && isTwinInfinite) {
            return true;
        } else if (!isInfinite && !isTwinInfinite) {
            return VectorUtils.dotProduct(VectorUtils.geDirection(point, startPoint), VectorUtils.geDirection(point, endPoint)) < 0;
        } else if (isInfinite) {
            return VectorUtils.dotProduct(VectorUtils.geDirection(point, endPoint), VectorUtils.geDirection(startPoint, endPoint)) > 0;
        }

        return VectorUtils.dotProduct(VectorUtils.geDirection(point, startPoint), VectorUtils.geDirection(endPoint, startPoint)) > 0;
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

            if (equals(firstChainEdge, lastChainEdge)) {
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

    public static boolean isConnected(Edge e1, Edge e2) {
        return Objects.equals(e1.getPoint(), e2.getTwin().getPoint()) || Objects.equals(e1.getTwin().getPoint(), e2.getPoint());
    }

    public static boolean equals(Edge e1, Edge e2) {
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

    public static boolean isOutsideCell(Edge e1, Edge e2, Point point) {
        if (e1 == null || e2 == null) {
            return true;
        }

        Point vertex = EdgeUtils.getPoint(e1, e2);
        if (vertex == null) {
            return true;
        }

        return !isPointInsideAngle(EdgeUtils.getOtherPoint(e1, vertex), vertex, EdgeUtils.getOtherPoint(e2, vertex), point);
    }


}

