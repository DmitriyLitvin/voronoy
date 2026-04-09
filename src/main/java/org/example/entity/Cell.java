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
    private Edge idle;
}
