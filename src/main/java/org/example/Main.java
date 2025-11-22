package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.example.entity.*;

import java.util.*;

import static java.lang.Math.*;
import static org.example.entity.CommonSupportType.LOWER;
import static org.example.entity.CommonSupportType.UPPER;


public class Main extends Application {
    private final List<Point> points = new LinkedList<>();

    private final int width = 1500;
    private final int height = 1000;
    private final Pane pane = new Pane();
    private final BorderPane borderPane = new BorderPane();

    public void start(Stage stage) {

        borderPane.setCenter(pane);

        Button button = new Button("Voronoy diagram");
        button.setLayoutX(10); // X координата
        button.setLayoutY(950);
        borderPane.setBottom(button);
        pane.getChildren().add(button);

        Scene scene = new Scene(borderPane, width, height);
        scene.setOnMouseClicked((MouseEvent event) -> {
            double x = event.getX();
            double y = event.getY();

            // Додаємо точку на екран
            Circle circle = new Circle(x, y, 3, Color.RED);
            borderPane.getChildren().add(circle);

            // Зберігаємо координати
            points.add(new Point(x, y));

            // Вивід координат у консоль
            System.out.println("Клік: x=" + x + ", y=" + y);
        });

        button.setOnAction(e -> {


            drawVoronoyDiagram(points);
            points.clear();
        });


        stage.setScene(scene);
        stage.setTitle("Voronoy");
        stage.show();
    }

    private List<Point> buildConvexHull(List<Point> points) {
        if (points.size() <= 2) {
            return points;
        }

        Point point = points.stream().min((p1, p2) -> {
            if (p1.getY() != p2.getY()) {
                return Double.compare(p1.getY(), p2.getY());
            }

            return Double.compare(p1.getX(), p2.getX());
        }).orElse(null);

        List<Point> sortedPoints = points.stream()
                .filter(p -> !p.equals(point))
                .sorted((p1, p2) -> {
                    int compared = Double.compare(atan2(p1.getY() - point.getY(), p1.getX() - point.getX()), atan2(p2.getY() - point.getY(), p2.getX() - point.getX()));
                    if (compared == 0) {
                        return Double.compare(sqrt(pow(p1.getY() - point.getY(), 2) + pow(p1.getX() - point.getX(), 2)), sqrt(pow(p2.getY() - point.getY(), 2) + pow(p2.getX() - point.getX(), 2)));
                    }
                    return compared;
                }).toList();

        Stack<Point> convexHull = new Stack<>();
        convexHull.push(sortedPoints.get(0));
        convexHull.push(point);

        sortedPoints.forEach(p -> {
            Point point1 = convexHull.get(convexHull.size() - 2);
            double x1 = point1.getX();
            double y1 = point1.getY();

            Point point2 = convexHull.peek();
            double x2 = point2.getX();
            double y2 = point2.getY();

            double x3 = p.getX();
            double y3 = p.getY();

            while (convexHull.size() > 2 && (x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2) < 0) {
                convexHull.pop();

                point1 = convexHull.get(convexHull.size() - 2);
                x1 = point1.getX();
                y1 = point1.getY();

                point2 = convexHull.peek();
                x2 = point2.getX();
                y2 = point2.getY();
            }

            convexHull.push(p);
        });

        return new ArrayList<>(convexHull);
    }

    private Line getCommonSupport(List<Point> leftConvexPolygon, List<Point> rightConvexPolygon, CommonSupportType commonSupportType) {
        Point maxXpoint = leftConvexPolygon.stream().max(Comparator.comparingDouble(Point::getX)).orElse(null);
        Point minXPoint = rightConvexPolygon.stream().min(Comparator.comparingDouble(Point::getX)).orElse(null);
        Line line = new Line(maxXpoint, minXPoint);

        for (int i = 0; i < 2; i++) {
            Point leftPoint = maxXpoint;
            Point rightPoint = minXPoint;

            Iterator<Point> leftConvexPolygonIterator = leftConvexPolygon.stream().filter(p -> !p.equals(maxXpoint)).iterator();
            Iterator<Point> rightConvexPolygonIterator = rightConvexPolygon.stream().filter(p -> !p.equals(minXPoint)).iterator();
            while (leftConvexPolygonIterator.hasNext() || rightConvexPolygonIterator.hasNext()) {
                if (leftConvexPolygonIterator.hasNext()) {
                    leftPoint = leftConvexPolygonIterator.next();
                }
                if (rightConvexPolygonIterator.hasNext()) {
                    rightPoint = rightConvexPolygonIterator.next();
                }

                if (line.is(leftPoint, commonSupportType)) {
                    line.setLeftPoint(leftPoint);
                    if (line.is(rightPoint, commonSupportType)) {
                        line.setRightPoint(rightPoint);
                    }

                } else if (line.is(rightPoint, commonSupportType)) {
                    line.setRightPoint(rightPoint);

                    if (line.is(leftPoint, commonSupportType)) {
                        line.setLeftPoint(leftPoint);
                    }
                }
            }
        }

        return line;
    }

    private Map<Point, Cell> buildVoronoyDiagram(List<Point> polygon) {
        if (polygon.size() == 1) {
            Map<Point, Cell> diagram = new HashMap<>();
            Point center = polygon.get(0);

            Edge edge = new Edge(new Point(center.getX() - 100, center.getY()), new Point(center.getX() + 100, center.getY()));
            edge.setTwin(edge);

            Cell cell = new Cell(center, edge);
            edge.setCell(cell);
            diagram.put(center, cell);

            return diagram;
        } else if (polygon.size() == 2) {
            Map<Point, Cell> diagram = new HashMap<>();
            Point leftCenter = polygon.get(0);
            Point rightCenter = polygon.get(1);

            Line middlePerpendicular = getMiddlePerpendicular(new Line(leftCenter, rightCenter));
            Edge leftEdge = new Edge(middlePerpendicular.getLeftPoint(), middlePerpendicular.getRightPoint());

            Edge rightEdge = new Edge(middlePerpendicular.getLeftPoint(), middlePerpendicular.getRightPoint());

            leftEdge.setTwin(rightEdge);
            rightEdge.setTwin(leftEdge);

            Cell leftCell = new Cell(leftCenter, leftEdge);
            Cell rightCell = new Cell(rightCenter, rightEdge);

            leftEdge.setCell(leftCell);
            rightEdge.setCell(rightCell);

            diagram.put(leftCenter, leftCell);
            diagram.put(rightCenter, rightCell);
            return diagram;
        }
        return joinDiagrams(buildVoronoyDiagram(polygon.subList(0, polygon.size() / 2)), buildVoronoyDiagram(polygon.subList(polygon.size() / 2, polygon.size())));
    }

    private Map<Point, Cell> joinDiagrams(Map<Point, Cell> leftDiagram, Map<Point, Cell> rightDiagram) {
        List<Point> leftPolygon = buildConvexHull(new ArrayList<>(leftDiagram.keySet()));
        List<Point> rightPolygon = buildConvexHull(new ArrayList<>(rightDiagram.keySet()));

        Line upperCommonSupport = getCommonSupport(new ArrayList<>(leftPolygon), new ArrayList<>(rightPolygon), UPPER);
        Line lowerCommonSupport = getCommonSupport(new ArrayList<>(leftPolygon), new ArrayList<>(rightPolygon), LOWER);


        Point prevPoint = null;
        Edge leftEdge = null;
        Edge rightEdge = null;
        Line middlePerpendicular;
        Map<Cell, List<Edge>> disjunctiveChain = new HashMap<>();
        while (!Objects.equals(upperCommonSupport, lowerCommonSupport)) {
            middlePerpendicular = getMiddlePerpendicular(upperCommonSupport);

            double leftDistance = 0;
            Point leftPoint = null;
            Cell leftCell = leftDiagram.get(upperCommonSupport.getLeftPoint());
            leftEdge = getClosestEdge(leftCell, middlePerpendicular, leftEdge);
            if (leftEdge != null) {
                leftPoint = intersectionOfLines(middlePerpendicular, new Line(leftEdge));
                assert leftPoint != null;
                leftDistance = PointUtils.getLength(leftPoint, prevPoint == null ? middlePerpendicular.getRightPoint() : prevPoint);
            }

            double rightDistance = 0;
            Point rightPoint = null;
            Cell rightCell = rightDiagram.get(upperCommonSupport.getRightPoint());
            rightEdge = getClosestEdge(rightCell, middlePerpendicular, rightEdge);
            if (rightEdge != null) {
                rightPoint = intersectionOfLines(middlePerpendicular, new Line(rightEdge));
                assert rightPoint != null;
                rightDistance = PointUtils.getLength(rightPoint, prevPoint == null ? middlePerpendicular.getRightPoint() : prevPoint);
            }

            if (rightEdge == null || (leftEdge != null && leftDistance < rightDistance)) {
                Edge leftTwinEdge = null;
                Line leftLine = new Line(leftEdge);
                if (isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getLeftPoint()) && isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getRightPoint())) {
                    if (PointUtils.getLength(leftEdge.getLeftPoint(), leftPoint) > PointUtils.getLength(leftEdge.getRightPoint(), leftPoint)) {
                        leftEdge.setRightPoint(leftPoint);
                        leftEdge.setInfiniteRightEnd(false);
                        leftTwinEdge = leftEdge.getTwin();
                        leftTwinEdge.setRightPoint(leftPoint);
                    } else if (PointUtils.getLength(leftEdge.getLeftPoint(), leftPoint) < PointUtils.getLength(leftEdge.getRightPoint(), leftPoint)) {
                        leftEdge.setLeftPoint(leftPoint);
                        leftEdge.setInfiniteLeftEnd(false);
                        leftTwinEdge = leftEdge.getTwin();
                        leftTwinEdge.setLeftPoint(leftPoint);
                    }
                } else if (isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getLeftPoint())) {
                    leftEdge.setRightPoint(leftPoint);
                    leftEdge.setInfiniteRightEnd(false);
                    leftTwinEdge = leftEdge.getTwin();
                    leftTwinEdge.setRightPoint(leftPoint);
                } else if (isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getRightPoint())) {
                    leftEdge.setLeftPoint(leftPoint);
                    leftEdge.setInfiniteLeftEnd(false);
                    leftTwinEdge = leftEdge.getTwin();
                    leftTwinEdge.setLeftPoint(leftPoint);
                }

                assert leftTwinEdge != null;

                Edge nextLeftEdge = new Edge((prevPoint == null ? middlePerpendicular.getRightPoint() : prevPoint), leftPoint, leftCell);
                nextLeftEdge.setInfiniteLeftEnd(prevPoint == null);
                nextLeftEdge.setInfiniteRightEnd(false);

                List<Edge> leftChain = disjunctiveChain.get(leftCell);
                if (leftChain == null || leftChain.isEmpty()) {
                    nextLeftEdge.setPrev(leftEdge);
                    disjunctiveChain.put(leftCell, new ArrayList<>(List.of(nextLeftEdge)));
                } else {
                    Edge lastEdge = leftChain.get(leftChain.size() - 1);
                    nextLeftEdge.setPrev(leftEdge);
                    lastEdge.setNext(nextLeftEdge);
                    leftChain.add(nextLeftEdge);
                }

                Edge nextRightEdge = new Edge((prevPoint == null ? middlePerpendicular.getRightPoint() : prevPoint), leftPoint, rightCell);
                nextRightEdge.setInfiniteLeftEnd(prevPoint == null);
                nextRightEdge.setInfiniteRightEnd(false);
                nextRightEdge.setTwin(nextLeftEdge);
                List<Edge> rightChain = disjunctiveChain.get(rightCell);
                if (rightChain == null || rightChain.isEmpty()) {
                    disjunctiveChain.put(rightCell, new ArrayList<>(List.of(nextRightEdge)));
                } else {
                    Edge lastEdge = rightChain.get(rightChain.size() - 1);
                    nextRightEdge.setPrev(lastEdge);
                    lastEdge.setNext(nextRightEdge);
                    rightChain.add(nextRightEdge);
                }

                upperCommonSupport.setLeftPoint(leftTwinEdge.getCell().getCenter());
                nextLeftEdge.setTwin(nextRightEdge);
                prevPoint = leftPoint;
                rightEdge = null;

            } else if (leftEdge == null || leftDistance >= rightDistance) {
                Edge rightTwinEdge = null;
                Line rightLine = new Line(rightEdge);
                if (isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getLeftPoint()) && isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getRightPoint())) {
                    if (PointUtils.getLength(rightEdge.getLeftPoint(), rightPoint) > PointUtils.getLength(rightEdge.getRightPoint(), rightPoint)) {
                        rightEdge.setRightPoint(rightPoint);
                        rightEdge.setInfiniteRightEnd(false);
                        rightTwinEdge = rightEdge.getTwin();
                        rightTwinEdge.setRightPoint(rightPoint);
                    } else if (PointUtils.getLength(rightEdge.getLeftPoint(), rightPoint) < PointUtils.getLength(rightEdge.getRightPoint(), rightPoint)) {
                        rightEdge.setLeftPoint(rightPoint);
                        rightEdge.setInfiniteLeftEnd(false);
                        rightTwinEdge = rightEdge.getTwin();
                        rightTwinEdge.setLeftPoint(rightPoint);
                    }
                } else if (isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getLeftPoint())) {
                    rightEdge.setRightPoint(rightPoint);
                    rightEdge.setInfiniteRightEnd(false);
                    rightTwinEdge = rightEdge.getTwin();
                    rightTwinEdge.setRightPoint(rightPoint);
                } else if (isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getRightPoint())) {
                    rightEdge.setLeftPoint(rightPoint);
                    rightEdge.setInfiniteLeftEnd(false);
                    rightTwinEdge = rightEdge.getTwin();
                    rightTwinEdge.setLeftPoint(rightPoint);
                }


                assert rightTwinEdge != null;

                Edge nextRightEdge = new Edge((prevPoint == null ? middlePerpendicular.getRightPoint() : prevPoint), rightPoint, rightCell);
                nextRightEdge.setInfiniteLeftEnd(prevPoint == null);
                nextRightEdge.setInfiniteRightEnd(false);

                List<Edge> rightChain = disjunctiveChain.get(rightCell);
                if (rightChain == null || rightChain.isEmpty()) {
                    nextRightEdge.setPrev(rightEdge);
                    disjunctiveChain.put(rightCell, new ArrayList<>(List.of(nextRightEdge)));
                } else {
                    Edge lastEdge = rightChain.get(rightChain.size() - 1);
                    nextRightEdge.setPrev(rightEdge);
                    lastEdge.setNext(nextRightEdge);
                    rightChain.add(nextRightEdge);
                }

                Edge nextLeftEdge = new Edge((prevPoint == null ? middlePerpendicular.getRightPoint() : prevPoint), rightPoint, leftCell);
                nextLeftEdge.setInfiniteLeftEnd(prevPoint == null);
                nextLeftEdge.setInfiniteRightEnd(false);
                nextLeftEdge.setTwin(nextRightEdge);
                List<Edge> leftChain = disjunctiveChain.get(leftCell);
                if (leftChain == null || leftChain.isEmpty()) {
                    disjunctiveChain.put(leftCell, new ArrayList<>(List.of(nextLeftEdge)));
                } else {
                    Edge lastEdge = leftChain.get(leftChain.size() - 1);
                    nextLeftEdge.setPrev(lastEdge);
                    lastEdge.setNext(nextLeftEdge);
                    leftChain.add(nextLeftEdge);
                }

                upperCommonSupport.setRightPoint(rightTwinEdge.getCell().getCenter());
                nextRightEdge.setTwin(nextLeftEdge);
                prevPoint = rightPoint;
                leftEdge = null;
            }
        }

        middlePerpendicular = getMiddlePerpendicular(lowerCommonSupport);

        Cell leftCell = leftDiagram.get(lowerCommonSupport.getLeftPoint());
        Cell rightCell = rightDiagram.get(lowerCommonSupport.getRightPoint());

        leftEdge = new Edge(prevPoint, middlePerpendicular.getLeftPoint(), leftCell);
        leftEdge.setInfiniteLeftEnd(false);
        rightEdge = new Edge(prevPoint, middlePerpendicular.getLeftPoint(), rightCell);
        rightEdge.setInfiniteLeftEnd(false);

        leftEdge.setTwin(rightEdge);
        rightEdge.setTwin(leftEdge);

        List<Edge> leftChain = disjunctiveChain.get(leftEdge.getCell());
        if (leftChain == null || leftChain.isEmpty()) {
            disjunctiveChain.put(leftEdge.getCell(), new ArrayList<>(List.of(leftEdge)));
        } else {
            Edge lastEdge = leftChain.get(leftChain.size() - 1);
            leftEdge.setPrev(lastEdge);
            lastEdge.setNext(leftEdge);
            leftChain.add(leftEdge);
        }

        List<Edge> rightChain = disjunctiveChain.get(rightEdge.getCell());
        if (rightChain == null || rightChain.isEmpty()) {
            disjunctiveChain.put(rightEdge.getCell(), new ArrayList<>(List.of(rightEdge)));
        } else {
            Edge lastEdge = rightChain.get(rightChain.size() - 1);
            rightEdge.setPrev(lastEdge);
            lastEdge.setNext(rightEdge);
            rightChain.add(rightEdge);
        }

        disjunctiveChain.forEach((cell, chain) -> {
            Edge firstEdge = chain.get(0);
            Edge firstLeftEdge = cell.getConnectedEdge(firstEdge.getLeftPoint());
            Edge firstRightEdge = cell.getConnectedEdge(firstEdge.getRightPoint());

            Edge lastEdge = chain.get(chain.size() - 1);
            Edge lastLeftEdge = cell.getConnectedEdge(lastEdge.getLeftPoint());
            Edge lastRightEdge = cell.getConnectedEdge(lastEdge.getRightPoint());

            if (!firstEdge.isInfiniteLeftEnd() && !firstEdge.isInfiniteRightEnd() && chain.size() == 1) {
                if (firstLeftEdge != null) {
                    firstEdge.setPrev(firstLeftEdge);
                    firstLeftEdge.setNext(firstEdge);
                }
                if (firstRightEdge != null) {
                    firstEdge.setNext(firstRightEdge);
                    firstRightEdge.setPrev(firstEdge);
                }
            } else {
                if (!firstEdge.isInfiniteLeftEnd() && firstLeftEdge != null) {
                    firstEdge.setPrev(firstLeftEdge);
                    firstLeftEdge.setNext(firstEdge);
                } else if (!firstEdge.isInfiniteRightEnd() && firstRightEdge != null) {
                    firstEdge.setPrev(firstRightEdge);
                    firstRightEdge.setNext(firstEdge);
                }
            }

            if (chain.size() >= 2) {
                if (!lastEdge.isInfiniteLeftEnd() && lastLeftEdge != null) {
                    lastEdge.setNext(lastLeftEdge);
                    lastLeftEdge.setPrev(lastEdge);
                } else if (!lastEdge.isInfiniteRightEnd() && lastRightEdge != null) {
                    lastEdge.setNext(lastRightEdge);
                    lastRightEdge.setPrev(lastEdge);
                }
            }
        });

        Map<Point, Cell> diagram = new HashMap<>();
        diagram.putAll(leftDiagram);
        diagram.putAll(rightDiagram);

        return diagram;
    }

    public void drawVoronoyDiagram(List<Point> polygon) {
        List<Cell> voronoyCells = buildVoronoyDiagram(polygon.stream().sorted(Comparator.comparingDouble(Point::getX).thenComparing(Point::getY)).toList()).values().stream().toList();
        System.out.println("Started drawing");
        for (Cell voronoyCell : voronoyCells) {
            Edge edge = voronoyCell.getBoundary();
            Edge nextEdge = voronoyCell.getBoundary();
            do {
                javafx.scene.shape.Line line = new javafx.scene.shape.Line(nextEdge.getLeftPoint().getX(), nextEdge.getLeftPoint().getY(), nextEdge.getRightPoint().getX(), nextEdge.getRightPoint().getY());
                line.setStroke(Color.BLUE);
                line.setStrokeWidth(1);
                pane.getChildren().add(line);
                //System.out.println(new Line(edge) + " " + new Line(nextEdge));
                nextEdge = nextEdge.getNext();
            } while (nextEdge != null && !Objects.equals(new Line(edge), new Line(nextEdge)));

            Edge prevEdge = voronoyCell.getBoundary();
            do {
                javafx.scene.shape.Line line = new javafx.scene.shape.Line(prevEdge.getLeftPoint().getX(), prevEdge.getLeftPoint().getY(), prevEdge.getRightPoint().getX(), prevEdge.getRightPoint().getY());
                line.setStroke(Color.BLUE);
                line.setStrokeWidth(1);
                pane.getChildren().add(line);
                //System.out.println(new Line(edge) + " " + new Line(prevEdge));
                prevEdge = prevEdge.getPrev();
            } while (prevEdge != null && !Objects.equals(new Line(edge), new Line(prevEdge)));
        }

        System.out.println("Ended drawing");
    }

    private Edge getClosestEdge(Cell cell, Line middlePerpendicular, Edge excludedEdge) {
        Edge edge = cell.getBoundary();
        Edge nextEdge = cell.getBoundary();

        Edge intersectedEdge = null;
        double distance = -1;
        do {
            if (excludedEdge == null || !Objects.equals(new Line(excludedEdge), new Line(nextEdge))) {
                Point point = intersectionOfLines(middlePerpendicular, new Line(nextEdge));
                if (point != null && isIntersected(point, nextEdge)) {
                    double currentDistance = PointUtils.getLength(point, middlePerpendicular.getRightPoint());
                    if (distance == -1 || distance < currentDistance) {
                        distance = currentDistance;
                        intersectedEdge = nextEdge;
                    }
                }
            }
            nextEdge = nextEdge.getNext();
        } while (nextEdge != null && !Objects.equals(new Line(edge), new Line(nextEdge)));

        Edge prevEdge = cell.getBoundary();
        do {
            if (excludedEdge == null || !Objects.equals(new Line(excludedEdge), new Line(prevEdge))) {
                Point point = intersectionOfLines(middlePerpendicular, new Line(prevEdge));
                if (point != null && isIntersected(point, prevEdge)) {
                    double currentDistance = PointUtils.getLength(point, middlePerpendicular.getRightPoint());
                    if (distance == -1 || distance < currentDistance) {
                        distance = currentDistance;
                        intersectedEdge = prevEdge;
                    }
                }
            }
            prevEdge = prevEdge.getPrev();
        } while (prevEdge != null && !Objects.equals(new Line(edge), new Line(prevEdge)));

        return intersectedEdge;
    }

    public boolean isIntersected(Point point, Edge edge) {
        double x1 = edge.getLeftPoint().getX();
        double y1 = edge.getLeftPoint().getY();

        double x2 = point.getX();
        double y2 = point.getY();

        double x3 = edge.getRightPoint().getX();
        double y3 = edge.getRightPoint().getY();

        boolean isInfiniteLeftPoint = edge.isInfiniteLeftEnd();
        boolean isInfiniteRightPoint = edge.isInfiniteRightEnd();

        if (x1 == x3) {
            if (y1 < y3) {
                return (isInfiniteLeftPoint || y2 > y1) && (isInfiniteRightPoint || y2 < y3);
            } else if (y1 > y3) {
                return (isInfiniteLeftPoint || y2 < y1) && (isInfiniteRightPoint || y2 > y3);
            }
        } else if (y1 == y3) {
            if (x1 < x3) {
                return (isInfiniteLeftPoint || x2 > x1) && (isInfiniteRightPoint || x2 < x3);
            } else if (x1 > x3) {
                return (isInfiniteLeftPoint || x2 < x1) && (isInfiniteRightPoint || x2 > x3);
            }
        } else if (x1 < x3 && y1 < y3) {
            return (isInfiniteLeftPoint || (x2 > x1 && y2 > y1)) && (isInfiniteRightPoint || (x2 < x3 && y2 < y3));
        } else if (x1 > x3 && y1 > y3) {
            return (isInfiniteLeftPoint || (x2 < x1 && y2 < y1)) && (isInfiniteRightPoint || (x2 > x3 && y2 > y3));
        } else if (x1 < x3 && y1 > y3) {
            return (isInfiniteLeftPoint || (x2 > x1 && y2 < y1)) && (isInfiniteRightPoint || (x2 < x3 && y2 > y3));
        } else if (x1 > x3 && y1 < y3) {
            return (isInfiniteLeftPoint || (x2 < x1 && y2 > y1)) && (isInfiniteRightPoint || (x2 > x3 && y2 < y3));
        }

        return false;
    }

    private Line getMiddlePerpendicular(Line line) {
        int width = 3 * this.width;
        int height = 3 * this.height;
        Point point = line.getMidPoint();
        double x1 = point.getX();
        double y1 = point.getY();

        double x2 = line.getLeftPoint().getX();
        double y2 = line.getLeftPoint().getY();

        double x3 = line.getRightPoint().getX();
        double y3 = line.getRightPoint().getY();

        if (y2 == y3) {
            return new Line(new Point(0, y2), new Point(width, y2));
        }
        if (x2 == x3) {
            return new Line(new Point(x2, 0), new Point(x2, height));
        }

        return new Line(new Point((pow(y2, 2) - pow(y1, 2) - pow(y2 - y1, 2) - pow(x2 - x1, 2) - pow(x1, 2) + pow(x2, 2)) / (2 * (x2 - x1)), 0), new Point((pow(y2 - height, 2) - pow(y1 - height, 2) - pow(y2 - y1, 2) - pow(x2 - x1, 2) - pow(x1, 2) + pow(x2, 2)) / (2 * (x2 - x1)), height));
    }

    private boolean isPointUpper(Line line, Point point) {
        if (line.getLeftPoint().getX() == line.getRightPoint().getX()) {
            return point.getX() > line.getLeftPoint().getX();
        }
        return point.getY() > line.getEquationOfLine(point.getX());
    }

    private boolean isPointLower(Line line, Point point) {
        if (line.getLeftPoint().getX() == line.getRightPoint().getX()) {
            return point.getX() < line.getLeftPoint().getX();
        }
        return point.getY() < line.getEquationOfLine(point.getX());
    }

    private boolean isOnTheSameSide(Line line, Point a, Point b) {
        return (isPointLower(line, a) && isPointLower(line, b)) || (isPointUpper(line, a) && isPointUpper(line, b));
    }

    private Point intersectionOfLines(Line a, Line b) {
        Point a1 = a.getLeftPoint();
        Point b1 = a.getRightPoint();

        Point a2 = b.getLeftPoint();
        Point b2 = b.getRightPoint();

        double deltaXa = b1.getX() - a1.getX();
        double deltaYa = b1.getY() - a1.getY();

        double deltaXb = b2.getX() - a2.getX();
        double deltaYb = b2.getY() - a2.getY();

        if (deltaXa == 0) {
            return new Point(a1.getX(), b.getEquationOfLine(a1.getX()));
        } else if (deltaXb == 0) {
            return new Point(a2.getX(), a.getEquationOfLine(a2.getX()));
        } else {
            double fracA = deltaYa / deltaXa;
            double fracB = deltaYb / deltaXb;

            double c = a2.getY() - a1.getY() + a1.getX() * fracA - a2.getX() * fracB;

            if (deltaYa * deltaXb - deltaYb * deltaXa == 0) {
                if (c == 0) {
                    return new Point(1, b.getEquationOfLine(1));
                }

                return null;
            }
            double x = c / (fracA - fracB);

            return new Point(x, a.getEquationOfLine(x));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}