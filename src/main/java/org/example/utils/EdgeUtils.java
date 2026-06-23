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
        Edge startEdge = edge.getStart();
        Edge lastEdge = edge.getLast();
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

    public static boolean isOnTheSameSide(Point center, Point edgePoint, Point middlePoint) {
        return VectorUtils.dotProduct(VectorUtils.geDirection(middlePoint, center), VectorUtils.geDirection(middlePoint, edgePoint)) >= 0;
    }

    public static boolean isConnected(Cell cell, Edge edge) {
        Edge boundary = cell.getBoundary();
        Edge firstChainEdge = edge.getStart();
        Edge lastChainEdge = edge.getLast();

        if (firstChainEdge != null && lastChainEdge != null) {
            Point firstPoint;
            Point lastPoint;

            if (firstChainEdge.equals(lastChainEdge)) {
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

            return firstEdge != null && (firstEdge.isConnected(firstChainEdge) || firstEdge.isConnected(lastChainEdge)) || (lastEdge != null && (lastEdge.isConnected(lastChainEdge) || lastEdge.isConnected(firstChainEdge)));
        }

        return false;
    }

    public static boolean isOutsideCell(Edge currentEdge, Edge chainEdge, Point chainPoint) {
        if (currentEdge == null || chainEdge == null) {
            return true;
        }

        Point vertex = getVertex(currentEdge, chainEdge);
        if (vertex == null) {
            return  false;
        }

        return !isPointInsideAngle(EdgeUtils.getOtherPoint(currentEdge, vertex), vertex, EdgeUtils.getOtherPoint(chainEdge, vertex), chainPoint);
    }

    public static Point getVertex(Edge a, Edge b) {
        if (a == null || b == null) {
            return null;
        }

        Point startPoint = a.getPoint();
        Point endPoint = a.getTwin().getPoint();

        Point vertex = null;
        if (Objects.equals(startPoint, b.getTwin().getPoint())) {
            vertex = startPoint;
        } else if (Objects.equals(endPoint, b.getPoint())) {
            vertex = endPoint;
        }

        return vertex;
    }

    public static boolean isPointInsideAngle(Point currentPoint, Point vertex, Point chainPoint, Point point) {
        if (vertex == null) {
            return false;
        }

        if (crossProduct(VectorUtils.geDirection(vertex, currentPoint), VectorUtils.geDirection(vertex, chainPoint)) > 0) {
            return crossProduct(VectorUtils.geDirection(vertex, currentPoint), VectorUtils.geDirection(vertex, point)) > 0 && crossProduct(VectorUtils.geDirection(vertex, point), VectorUtils.geDirection(vertex, chainPoint)) > 0;
        } else {
            return crossProduct(VectorUtils.geDirection(vertex, currentPoint), VectorUtils.geDirection(vertex, point)) < 0 && crossProduct(VectorUtils.geDirection(vertex, point), VectorUtils.geDirection(vertex, chainPoint)) < 0;
        }
    }
}
