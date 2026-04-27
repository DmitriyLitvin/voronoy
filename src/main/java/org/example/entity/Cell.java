package org.example.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    private Vertex center;
    private Edge boundary;

    public boolean isConnected() {
        Edge startEdge = boundary.getStartEdge();
        Edge lastEdge = boundary.getLastEdge();
        if (startEdge == null || lastEdge == null) {
            return false;
        }

        return startEdge.getTwin().getVertex() == lastEdge.getVertex();
    }
}
