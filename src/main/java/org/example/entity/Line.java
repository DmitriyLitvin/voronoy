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

    public Line(Edge edge) {
        this.leftPoint = edge.getLeftPoint();
        this.rightPoint = edge.getRightPoint();
    }

    public boolean is(Point c, CommonSupportLine commonSupportLine) {
        switch (commonSupportLine) {
            case UPPER -> {
                return c.getY() > getEquationOfLine(c.getX());
            }
            case LOWER -> {
                return c.getY() < getEquationOfLine(c.getX());
            }
            case ON -> {
                return c.getY() == getEquationOfLine(c.getX());
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
