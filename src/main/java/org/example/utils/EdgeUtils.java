package org.example.utils;

import org.example.entity.Cell;
import org.example.entity.Edge;
import org.example.entity.Line;
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
        if (point == null) {
            return false;
        }

        boolean isInfinite = edge.isInfinite();
        boolean isTwinInfinite = edge.getTwin().isInfinite();
        Point startPoint = edge.getPoint();
        Point endPoint = edge.getTwin().getPoint();

        if (isInfinite && isTwinInfinite) {
            return true; // Раз точка на прямой, она автоматически принадлежит бесконечной прямой
        } else if (!isInfinite && !isTwinInfinite) {
            return VectorUtils.dotProduct(VectorUtils.geDirection(point, startPoint), VectorUtils.geDirection(point, endPoint)) <= 0;
        } else if (isInfinite) {
            return VectorUtils.dotProduct(VectorUtils.geDirection(endPoint, point), VectorUtils.geDirection(endPoint, startPoint)) >= 0;
        }

        return VectorUtils.dotProduct(VectorUtils.geDirection(startPoint, point), VectorUtils.geDirection(startPoint, endPoint)) >= 0;
    }

    public static boolean isOnTheSameSide(Point point, Point other, Line line) {
        Point startPoint = line.getA();
        Point endPoint = line.getB();
        return ((endPoint.getX() - startPoint.getX()) * (point.getY() - startPoint.getY()) - (endPoint.getY() - startPoint.getY()) * (point.getX() - startPoint.getX())) * ((endPoint.getX() - startPoint.getX()) * (other.getY() - startPoint.getY()) - (endPoint.getY() - startPoint.getY()) * (other.getX() - startPoint.getX())) > 0;
    }

    public static boolean isOutsideCell(Edge currentEdge, Edge chainEdge, Point chainPoint) {
        if (currentEdge == null || chainEdge == null) {
            return true;
        }

        Point vertex = currentEdge.getCommonVertex(chainEdge);
        if (vertex == null) {
            return false;
        }

        return !isPointInsideAngle(EdgeUtils.getOtherPoint(currentEdge, vertex), vertex, EdgeUtils.getOtherPoint(chainEdge, vertex), chainPoint);
    }

    public static boolean isPointInsideAngle(Point currentPoint, Point vertex, Point chainPoint, Point point) {

        if (crossProduct(VectorUtils.geDirection(vertex, currentPoint), VectorUtils.geDirection(vertex, chainPoint)) > 0) {
            return crossProduct(VectorUtils.geDirection(vertex, currentPoint), VectorUtils.geDirection(vertex, point)) >= 0 && crossProduct(VectorUtils.geDirection(vertex, point), VectorUtils.geDirection(vertex, chainPoint)) >= 0;
        } else {
            return crossProduct(VectorUtils.geDirection(vertex, currentPoint), VectorUtils.geDirection(vertex, point)) <= 0 && crossProduct(VectorUtils.geDirection(vertex, point), VectorUtils.geDirection(vertex, chainPoint)) <= 0;
        }
    }
}
