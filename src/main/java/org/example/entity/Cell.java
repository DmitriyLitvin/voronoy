package org.example.entity;

import lombok.*;
import org.example.utils.VectorUtils;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "boundary")
public class Cell {
    private Point center;
    private Edge boundary;


    public Edge getConnectedEdge(Point point) {
        Edge currentEdge = boundary;
        while (isConnected(point, currentEdge)) {
            currentEdge = currentEdge.getPrev();
            if (currentEdge == null || Objects.equals(new Line(boundary), new Line(currentEdge))) {
                break;
            }
        }

        if (currentEdge != null) {
            return currentEdge;
        }

        currentEdge = getBoundary();
        while (isConnected(point, currentEdge)) {
            currentEdge = currentEdge.getNext();
            if (currentEdge == null || Objects.equals(new Line(boundary), new Line(currentEdge))) {
                break;
            }
        }

        return currentEdge;
    }

    public Set<Edge> getConnectedEdge(Set<Point> points) {
        return points.stream().map(this::getConnectedEdge).collect(Collectors.toSet());
    }


    private boolean isConnected(Point point, Edge currentEdge) {
        return VectorUtils.getLength(point, currentEdge.getRightPoint()) != 0 && VectorUtils.getLength(point, currentEdge.getLeftPoint()) != 0;
    }
}
