package org.example.entity;

import lombok.*;

import java.util.Objects;

import static org.example.utils.EdgeUtils.getVertexOfTangency;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    private Vertex center;
    private Edge boundary;

    public boolean isClosed() {
        Edge startEdge = boundary.getStartEdge();
        Edge lastEdge = boundary.getLastEdge();
        if (startEdge == null || lastEdge == null) {
            return false;
        }

        return Objects.equals(Objects.equals(getVertexOfTangency(startEdge, startEdge.getNext()), startEdge.getVertex()) ? startEdge.getTwin().getVertex() : startEdge.getVertex(), Objects.equals(getVertexOfTangency(lastEdge, lastEdge.getPrev()), lastEdge.getVertex()) ? lastEdge.getTwin().getVertex() : lastEdge.getVertex());
    }
}
