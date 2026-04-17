package org.example.entity;

import lombok.*;
import org.example.utils.VectorUtils;

import java.util.*;
import java.util.stream.Collectors;

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
        return startEdge.getLeftPoint() == lastEdge.getRightPoint() || startEdge.getRightPoint() == lastEdge.getLeftPoint();
    }
}
