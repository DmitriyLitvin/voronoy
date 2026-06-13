package org.example.entity;

import lombok.*;
import org.example.utils.EdgeUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Edge {
    @EqualsAndHashCode.Include
    private Point point;
    private Edge next;
    private Edge prev;
    private Edge twin;
    private Cell cell;
    private boolean isInfinite = true;

    public Edge(Point point) {
        this.point = point;
    }

    public Edge(Point point, Cell cell) {
        this.point = point;
        this.cell = cell;
    }


    public Edge getStartEdge() {
        Edge prevEdge = this;
        while (true) {
            Edge currentEdge = prevEdge.getPrev();
            if (currentEdge == null) {
                return prevEdge;
            } else if (EdgeUtils.isEquals(this, currentEdge)) {
                return null;
            }
            prevEdge = currentEdge;
        }
    }

    public Edge getLastEdge() {
        Edge nextEdge = this;
        while (true) {
            Edge currentEdge = nextEdge.getNext();
            if (currentEdge == null) {
                return nextEdge;
            } else if (EdgeUtils.isEquals(this, currentEdge)) {
                return null;
            }
            nextEdge = currentEdge;
        }
    }
}
