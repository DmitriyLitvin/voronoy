package org.example.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.Point;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointDto {
    private Point intersectedPoint;
    private Point cellCenterPoint;
    private Double distance;
}
