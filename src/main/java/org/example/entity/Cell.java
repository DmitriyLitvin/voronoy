package org.example.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    private Point center;
    private Edge boundary;

    boolean isConnected() {
        Edge startEdge = boundary.getStartEdge();
        Edge lastEdge = boundary.getLastEdge();
        return startEdge.getA() == lastEdge.getB() || startEdge.getB() == lastEdge.getA();
    }
}
