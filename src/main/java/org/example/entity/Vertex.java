package org.example.entity;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Vertex {
    private double x;
    private double y;

    public Vertex(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
