package org.example.entity;

import lombok.*;

import java.util.Objects;

import static org.example.utils.EdgeUtils.getVertexOfTangency;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cell {
    @EqualsAndHashCode.Include
    private Vertex center;
    private Edge boundary;
}
