package org.example.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Line {
    private Point leftPoint;
    private Point rightPoint;

    public Line(Line line) {
        this.leftPoint = line.getLeftPoint();
        this.rightPoint = line.getRightPoint();
    }

    public Line(Edge edge) {
        this.leftPoint = edge.getLeftPoint();
        this.rightPoint = edge.getRightPoint();
    }

    public boolean is(Point point, CommonSupportType commonSupportType) {
        switch (commonSupportType) {
            case UPPER -> {
                return point.getY() > getEquationOfLine(point.getX());
            }
            case LOWER -> {
                return point.getY() < getEquationOfLine(point.getX());
            }
        }

        return false;
    }

    public double getEquationOfLine(double x) {
        return ((rightPoint.getY() - this.leftPoint.getY()) * (x - this.leftPoint.getX())) / (rightPoint.getX() - this.leftPoint.getX()) + this.leftPoint.getY();
    }


    public Point getMidPoint() {
        return new Point((leftPoint.getX() + rightPoint.getX()) / 2, (leftPoint.getY() + rightPoint.getY()) / 2);
    }
}
