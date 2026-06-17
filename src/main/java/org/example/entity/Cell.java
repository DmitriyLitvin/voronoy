package org.example.entity;

import lombok.*;
import org.example.utils.EdgeUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cell {
    @EqualsAndHashCode.Include
    private Point center;
    private Edge boundary;

    public boolean isClosed() {
        Edge nextEdge = boundary;
        do {
            nextEdge = nextEdge.getNext();
            if (nextEdge == null) {
                return false;
            }
            if (boundary.equals(nextEdge)) {
                return true;
            }
        } while (true);
    }
}
