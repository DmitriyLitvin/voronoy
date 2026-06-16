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


    public static Edge eraseEdges(Map<Cell, List<Edge>> excludedEdges, Edge edge, Point point) {
        List<Edge> edges = excludedEdges.get(edge.getCell());
        if (edges != null) {
            for (int i = 0; i < edges.size(); i++) {
                Edge currentEdge = edges.get(i);
                do {
                    if (Objects.equals(currentEdge, edge)) {
                        edges.set(i, edge);
                        break;
                    }
                    currentEdge = currentEdge.getNext();
                } while (currentEdge != null);

                currentEdge = edges.get(i);
                do {
                    if (Objects.equals(currentEdge, edge)) {
                        edges.set(i, edge);
                        break;
                    }
                    currentEdge = currentEdge.getPrev();
                } while (currentEdge != null);
            }
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

    public static void connectEdges(Edge leftEdge, Edge rightEdge) {
        if (Objects.equals(leftEdge.getTwin().getPoint(), rightEdge.getPoint())) {
            leftEdge.setPrev(rightEdge);
            rightEdge.setNext(leftEdge);
        } else if (Objects.equals(leftEdge.getPoint(), rightEdge.getTwin().getPoint())) {
            leftEdge.setNext(rightEdge);
            rightEdge.setPrev(leftEdge);
        }
    }

    public static boolean isIdle(Edge edge) {
        return edge.getPrev() == null && edge.getNext() == null;
    }

    public static boolean isIntersected(Point point, Edge edge) {
        boolean isInfinite = edge.isInfinite();
        boolean isTwinInfinite = edge.getTwin().isInfinite();
        Point start = edge.getPoint();
        Point end = edge.getTwin().getPoint();

        if (point == null) {
            return false;
        } else if (isInfinite && isTwinInfinite) {
            return true;
        } else if (!isInfinite && !isTwinInfinite) {
            return VectorUtils.dotProduct(VectorUtils.geDirection(point, start), VectorUtils.geDirection(point, end)) < 0;
        } else if (isInfinite) {
            return VectorUtils.dotProduct(VectorUtils.geDirection(point, end), VectorUtils.geDirection(start, end)) > 0;
        }

        return VectorUtils.dotProduct(VectorUtils.geDirection(point, start), VectorUtils.geDirection(end, start)) > 0;
    }

    public static boolean isOnTheSameSide(Point centerPoint, Point edgePoint, Point middlePoint) {
        return VectorUtils.dotProduct(VectorUtils.geDirection(middlePoint, centerPoint), VectorUtils.geDirection(middlePoint, edgePoint)) >= 0;
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

    public static boolean isConnected(Edge leftEdge, Edge rightEdge) {
        return Objects.equals(leftEdge.getPoint(), rightEdge.getTwin().getPoint()) || Objects.equals(leftEdge.getTwin().getPoint(), rightEdge.getPoint());
    }

    public static boolean equals(Edge leftEdge, Edge rightEdge) {
        Point leftStart = leftEdge.getPoint();
        Point leftEnd = leftEdge.getTwin().getPoint();
        Point rightStart = rightEdge.getPoint();
        Point rightEnd = rightEdge.getTwin().getPoint();

        return (Objects.equals(leftStart, rightStart) && Objects.equals(leftEnd, rightEnd)) || (Objects.equals(leftStart, rightEnd) && Objects.equals(leftEnd, rightStart));
    }

    public static Point getPoint(Edge leftEdge, Edge rightEdge) {
        Point leftStart = leftEdge.getPoint();
        Point leftEnd = leftEdge.getTwin().getPoint();
        Point rightStart = rightEdge.getPoint();
        Point rightEnd = rightEdge.getTwin().getPoint();

        if (Objects.equals(leftStart, rightStart) || Objects.equals(leftStart, rightEnd)) {
            return leftStart;
        } else if (Objects.equals(leftEnd, rightStart) || Objects.equals(leftEnd, rightEnd)) {
            return leftEnd;
        }

        return null;
    }

    public static boolean isPointInsideAngle(Point leftPoint, Point vertex, Point rightPoint, Point point) {
        if (vertex == null) {
            return false;
        }

        if (crossProduct(VectorUtils.geDirection(vertex, leftPoint), VectorUtils.geDirection(vertex, rightPoint)) > 0) {
            return crossProduct(VectorUtils.geDirection(vertex, leftPoint), VectorUtils.geDirection(vertex, point)) > 0 && crossProduct(VectorUtils.geDirection(vertex, point), VectorUtils.geDirection(vertex, rightPoint)) > 0;
        } else {
            return crossProduct(VectorUtils.geDirection(vertex, leftPoint), VectorUtils.geDirection(vertex, point)) < 0 && crossProduct(VectorUtils.geDirection(vertex, point), VectorUtils.geDirection(vertex, rightPoint)) < 0;
        }
    }

    public static boolean isOutsideCell(Edge leftEdge, Edge rightEdge, Point point) {
        if (leftEdge == null || rightEdge == null) {
            return true;
        }

        Point vertex = EdgeUtils.getPoint(leftEdge, rightEdge);
        if (vertex == null) {
            return true;
        }

        return !isPointInsideAngle(EdgeUtils.getOtherPoint(leftEdge, vertex), vertex, EdgeUtils.getOtherPoint(rightEdge, vertex), point);
    }


}

