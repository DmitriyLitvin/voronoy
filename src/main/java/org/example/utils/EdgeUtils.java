package org.example.utils;

import org.example.entity.Cell;
import org.example.entity.Edge;
import org.example.entity.Vertex;

import java.util.Objects;

public class EdgeUtils {


    public static boolean isConnected(Edge e1, Edge e2) {
        return Objects.equals(e1.getVertex(), e2.getTwin().getVertex()) || Objects.equals(e1.getTwin().getVertex(), e2.getVertex());
    }

    public static boolean contains(Edge edge, Vertex v) {
        return Objects.equals(v, edge.getVertex()) || Objects.equals(v, edge.getTwin().getVertex());
    }

    public static boolean isEquals(Edge e1, Edge e2) {
        Vertex v1 = e1.getVertex();
        Vertex v2 = e1.getTwin().getVertex();
        Vertex v3 = e2.getVertex();
        Vertex v4 = e2.getTwin().getVertex();

        return (Objects.equals(v1, v3) && Objects.equals(v2, v4)) || (Objects.equals(v1, v4) && Objects.equals(v2, v3));
    }

    public static Vertex getVertexOfTangency(Edge e1, Edge e2) {
        Vertex v1 = e1.getVertex();
        Vertex v2 = e1.getTwin().getVertex();
        Vertex v3 = e2.getVertex();
        Vertex v4 = e2.getTwin().getVertex();

        if (Objects.equals(v1, v3) || Objects.equals(v1, v4)) {
            return v1;
        } else if (Objects.equals(v2, v3) || Objects.equals(v2, v4)) {
            return v2;
        }

        return null;
    }

    public static Vertex getOtherVertex(Edge e, Vertex v) {
        Vertex v1 = e.getVertex();
        Vertex v2 = e.getTwin().getVertex();

        if (Objects.equals(v1, v)) {
            return v2;
        } else if (Objects.equals(v2, v)) {
            return v1;
        }

        return null;
    }

    public static Edge eraseEdges(Edge e, Vertex v) {
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

    public static Edge getConnectedEdge(Edge boundary, Vertex v) {
        Edge startEdge = boundary.getStartEdge();
        Edge lastEdge = boundary.getLastEdge();
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

    public static Vertex getVertex(Edge e) {
        if (e.getNext() != null) {
            Edge nextEdge = e.getNext();
            if (contains(nextEdge, e.getVertex())) {
                return e.getTwin().getVertex();
            } else if (contains(nextEdge, e.getTwin().getVertex())) {
                return e.getVertex();
            }
        }
        if (e.getPrev() != null) {
            Edge prevEdge = e.getPrev();
            if (contains(prevEdge, e.getVertex())) {
                return e.getTwin().getVertex();
            } else if (contains(prevEdge, e.getTwin().getVertex())) {
                return e.getVertex();
            }
        }

        return null;
    }


    public static boolean isConnected(Cell cell, Edge e) {
        Edge boundary = cell.getBoundary();
        Edge firstChainEdge = e.getStartEdge();
        Edge lastChainEdge = e.getLastEdge();

        if (firstChainEdge != null && lastChainEdge != null) {
            Vertex firstVertex;
            Vertex lastVertex;

            if (isEquals(firstChainEdge, lastChainEdge)) {
                firstVertex = firstChainEdge.getVertex();
                lastVertex = firstChainEdge.getTwin().getVertex();
            } else {
                firstVertex = getVertex(firstChainEdge);
                lastVertex = getVertex(lastChainEdge);
            }

            Edge firstEdge = null;
            if (firstVertex != null) {
                firstEdge = getConnectedEdge(boundary, firstVertex);
            }

            Edge lastEdge = null;
            if (lastVertex != null) {
                lastEdge = getConnectedEdge(boundary, lastVertex);
            }


            return (firstEdge != null && (isConnected(firstEdge, firstChainEdge) || isConnected(firstEdge, lastChainEdge))) || (lastEdge != null && (isConnected(lastEdge, lastChainEdge) || isConnected(lastEdge, firstChainEdge)));
        }

        return false;
    }
}

