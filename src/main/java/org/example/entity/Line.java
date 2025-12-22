package org.example.entity;

import lombok.*;
import org.example.utils.DeepCopyHelper;

@Getter
@Setter
@NoArgsConstructor
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

    private static DeepCopyHelper<Line> helper = new DeepCopyHelper<>();

    public Line deepCopy() {
        return helper.copy(this);
    }

    public boolean is(Point point, CommonSupportType commonSupportType) {
        switch (commonSupportType) {
            case UPPER -> {
                return point.getY() >= getEquationOfLine(point.getX());
            }
            case LOWER -> {
                return point.getY() <= getEquationOfLine(point.getX());
            }
        }

        return false;
    }

    public double getEquationOfLine(double x) {
        return ((rightPoint.getY() - leftPoint.getY()) * (x - leftPoint.getX())) / (rightPoint.getX() - leftPoint.getX()) + leftPoint.getY();
    }


    public Point getMidPoint() {
        return new Point((leftPoint.getX() + rightPoint.getX()) / 2, (leftPoint.getY() + rightPoint.getY()) / 2);
    }
}
