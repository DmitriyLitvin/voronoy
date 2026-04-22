package org.example.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Line {
    private Point leftPoint;
    private Point rightPoint;
    private boolean isInfiniteLeftEnd = false;
    private boolean isInfiniteRightEnd = false;


    public Line(Point leftPoint, Point rightPoint) {
        this.leftPoint = leftPoint;
        this.rightPoint = rightPoint;
    }


    public Line(Edge edge) {
        this.leftPoint = edge.getLeftPoint();
        this.rightPoint = edge.getRightPoint();
        this.isInfiniteLeftEnd = edge.isLeft();
        this.isInfiniteRightEnd = edge.isRight();
    }

    public double getY(double x) {
        return ((rightPoint.getY() - leftPoint.getY()) * (x - leftPoint.getX())) / (rightPoint.getX() - leftPoint.getX()) + leftPoint.getY();
    }


    public Point getMidPoint() {
        return new Point((leftPoint.getX() + rightPoint.getX()) / 2, (leftPoint.getY() + rightPoint.getY()) / 2);
    }
}
