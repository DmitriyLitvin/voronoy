package org.example.entity;

import lombok.*;
import org.example.utils.EdgeUtils;
import org.example.utils.VectorUtils;

import java.util.Objects;

import static org.example.utils.EdgeUtils.getVertexOfTangency;
import static org.example.utils.EdgeUtils.isEquals;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cell {
    @EqualsAndHashCode.Include
    private Vertex center;
    private Edge boundary;

    public boolean isClosed() {
        Edge nextEdge = boundary;
        do {
            nextEdge = nextEdge.getNext();
            if (nextEdge == null) {
                return false;
            }
            if(EdgeUtils.isEquals(boundary, nextEdge)) {
                return true;
            }
        } while (true);
    }
}
