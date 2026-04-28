package org.example.utils;

import org.example.entity.Edge;
import org.example.entity.Vertex;

import java.util.Objects;

public class EdgeUtils {


    public  static boolean isEquals(Edge e1, Edge e2) {
        Vertex v1 = e1.getVertex();
        Vertex v2 = e1.getTwin().getVertex();
        Vertex v3 = e2.getVertex();
        Vertex v4 = e2.getTwin().getVertex();

        return (Objects.equals(v1, v3) && Objects.equals(v2, v4)) || (Objects.equals(v1, v4) && Objects.equals(v2, v3));
    }

    public static boolean isConnected(Edge e1, Edge e2) {
        return Objects.equals(e1.getVertex(), e2.getTwin().getVertex()) || Objects.equals(e1.getTwin().getVertex(), e2.getVertex()) || Objects.equals(e1.getTwin().getVertex(), e2.getTwin().getVertex()) || Objects.equals(e1.getVertex(), e2.getVertex());
    }

}
