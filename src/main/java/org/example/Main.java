package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.*;
import org.example.entity.Point;

import java.util.*;
import java.util.List;

import static java.lang.Math.*;
import static org.example.entity.CommonSupportType.LOWER;
import static org.example.entity.CommonSupportType.UPPER;

@Slf4j
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


        points.add(new Point(520.0, 290.0));
        points.add(new Point(630.0, 193.0));
        points.add(new Point(714.0, 326.0));
        points.add(new Point(763.0, 565.0));
        points.add(new Point(670.0, 465.0));
        points.add(new Point(560.0, 664.0));
        points.add(new Point(667.0, 838.0));
        points.add(new Point(693.0, 730.0));

//        points.add(new Point(275.0, 508.0));
//        points.add(new Point(284.0, 650.0));
//        points.add(new Point(441.0, 585.0));
//        points.add(new Point(287.0, 534.0));
//        points.add(new Point(660.0, 840.0));
//        points.add(new Point(678.0, 936.0));
//        points.add(new Point(792.0, 900.0));
//        points.add(new Point(675.0, 870.0));

        points.forEach(p -> {
            Circle circle = new Circle(p.getX(), p.getY(), 3, Color.RED);
            Label label = new Label(+circle.getCenterX() + ", " + circle.getCenterY());

            label.relocate(circle.getCenterX() + 1, circle.getCenterY() + 1);
            pane.getChildren().addAll(label, circle);
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

        for (int i = 0; i < 5; i++) {
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

            diagram.put(center, new Cell(center, null));

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

        Line upperCommonSupport = getCommonSupport(leftPolygon, rightPolygon, UPPER);
        Line lowerCommonSupport = getCommonSupport(leftPolygon, rightPolygon, LOWER);

        Point prevPoint = null;
        Edge leftEdge = null;
        Edge rightEdge = null;
        Line middlePerpendicular;
        Map<Point, Edge> excludedEdges = new HashMap<>();
        Map<Cell, List<Edge>> disjunctiveChain = new HashMap<>();
        int k = 0;
        while (!Objects.equals(upperCommonSupport, lowerCommonSupport)) {
            middlePerpendicular = getMiddlePerpendicular(upperCommonSupport);
            if (k > 15) {
                break;
            }
            k++;

            double leftDistance = 0;
            Point leftPoint = null;
            Point leftPointOfCommonSupport = upperCommonSupport.getLeftPoint();
            Cell leftCell = leftDiagram.get(leftPointOfCommonSupport);
            Edge leftExcludedEdge = getClosestEdge(excludedEdges.get(leftPointOfCommonSupport), middlePerpendicular, leftEdge);
            leftEdge = getClosestEdge(leftCell.getBoundary(), middlePerpendicular, leftEdge);

            if (leftEdge != null) {
                leftPoint = intersectionOfLines(middlePerpendicular, new Line(leftEdge));
                assert leftPoint != null;
                leftDistance = PointUtils.getLength(leftPoint, prevPoint == null ? middlePerpendicular.getRightPoint() : prevPoint);
            }
            if (leftExcludedEdge != null) {
                Point leftDeletedEdgeIntersectionPoint = intersectionOfLines(middlePerpendicular, new Line(leftExcludedEdge));
                if (leftDeletedEdgeIntersectionPoint != null) {
                    double leftDeletedIntersectionDistance = PointUtils.getLength(leftDeletedEdgeIntersectionPoint, prevPoint == null ? middlePerpendicular.getRightPoint() : prevPoint);
                    if (leftDeletedIntersectionDistance < leftDistance || leftEdge == null) {
                        leftDistance = leftDeletedIntersectionDistance;
                        leftPoint = leftDeletedEdgeIntersectionPoint;
                        leftEdge = leftExcludedEdge;
                    }
                }
            }

            double rightDistance = 0;
            Point rightPoint = null;
            Point rightPointOfCommonSupport = upperCommonSupport.getRightPoint();
            Cell rightCell = rightDiagram.get(rightPointOfCommonSupport);
            Edge rightExcludedEdge = getClosestEdge(excludedEdges.get(rightPointOfCommonSupport), middlePerpendicular, rightEdge);
            rightEdge = getClosestEdge(rightCell.getBoundary(), middlePerpendicular, rightEdge);

            if (rightEdge != null) {
                rightPoint = intersectionOfLines(middlePerpendicular, new Line(rightEdge));
                assert rightPoint != null;
                rightDistance = PointUtils.getLength(rightPoint, prevPoint == null ? middlePerpendicular.getRightPoint() : prevPoint);
            }
            if (rightExcludedEdge != null) {
                Point rightDeletedEdgeIntersectionPoint = intersectionOfLines(middlePerpendicular, new Line(rightExcludedEdge));
                if (rightDeletedEdgeIntersectionPoint != null) {
                    double rightDeletedIntersectionDistance = PointUtils.getLength(rightDeletedEdgeIntersectionPoint, prevPoint == null ? middlePerpendicular.getRightPoint() : prevPoint);
                    if (rightDeletedIntersectionDistance < rightDistance || rightEdge == null) {
                        rightDistance = rightDeletedIntersectionDistance;
                        rightPoint = rightDeletedEdgeIntersectionPoint;
                        rightEdge = rightExcludedEdge;
                    }
                }
            }

            if ((leftEdge != null && rightEdge == null) || (leftEdge != null && leftDistance < rightDistance)) {
                Edge leftTwinEdge = null;
                Line leftLine = new Line(leftEdge);
                if (isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getLeftPoint()) && isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getRightPoint())) {
                    if (PointUtils.getLength(leftEdge.getLeftPoint(), leftPoint) > PointUtils.getLength(leftEdge.getRightPoint(), leftPoint)) {
                        eraseEdges(leftEdge, leftEdge.getRightPoint());
                        leftEdge.setRightPoint(leftPoint);
                        leftEdge.setInfiniteRightEnd(false);
                        leftCell.setBoundary(leftEdge);

                        leftTwinEdge = leftEdge.getTwin();
                        eraseEdges(leftTwinEdge, leftTwinEdge.getRightPoint());
                        leftTwinEdge.setRightPoint(leftPoint);
                        leftTwinEdge.setInfiniteRightEnd(false);
                    } else if (PointUtils.getLength(leftEdge.getLeftPoint(), leftPoint) < PointUtils.getLength(leftEdge.getRightPoint(), leftPoint)) {
                        eraseEdges(leftEdge, leftEdge.getLeftPoint());
                        leftEdge.setLeftPoint(leftPoint);
                        leftEdge.setInfiniteLeftEnd(false);
                        leftCell.setBoundary(leftEdge);

                        leftTwinEdge = leftEdge.getTwin();
                        eraseEdges(leftTwinEdge, leftTwinEdge.getLeftPoint());
                        leftTwinEdge.setLeftPoint(leftPoint);
                        leftTwinEdge.setInfiniteLeftEnd(false);
                    }
                } else if (isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getLeftPoint())) {
                    eraseEdges(leftEdge, leftEdge.getRightPoint());
                    leftEdge.setRightPoint(leftPoint);
                    leftEdge.setInfiniteRightEnd(false);
                    leftCell.setBoundary(leftEdge);

                    leftTwinEdge = leftEdge.getTwin();
                    eraseEdges(leftTwinEdge, leftTwinEdge.getRightPoint());
                    leftTwinEdge.setRightPoint(leftPoint);
                    leftTwinEdge.setInfiniteRightEnd(false);
                } else if (isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getRightPoint())) {
                    eraseEdges(leftEdge, leftEdge.getLeftPoint());
                    leftEdge.setLeftPoint(leftPoint);
                    leftEdge.setInfiniteLeftEnd(false);
                    leftCell.setBoundary(leftEdge);

                    leftTwinEdge = leftEdge.getTwin();
                    eraseEdges(leftTwinEdge, leftTwinEdge.getLeftPoint());
                    leftTwinEdge.setLeftPoint(leftPoint);
                    leftTwinEdge.setInfiniteLeftEnd(false);
                }
                assert leftTwinEdge != null;
                Cell leftTwinCell = leftTwinEdge.getCell();
                excludedEdges.put(leftTwinCell.getCenter(), leftTwinCell.getBoundary());
                leftTwinCell.setBoundary(leftTwinEdge);

                Edge nextLeftEdge = new Edge((prevPoint == null ? middlePerpendicular.getRightPoint() : prevPoint), leftPoint, leftCell);
                nextLeftEdge.setInfiniteLeftEnd(prevPoint == null);
                nextLeftEdge.setInfiniteRightEnd(false);

//                if (leftDiagram.size() == 4) {
//                    javafx.scene.shape.Line line = new javafx.scene.shape.Line(nextLeftEdge.getLeftPoint().getX(), nextLeftEdge.getLeftPoint().getY(), nextLeftEdge.getRightPoint().getX(), nextLeftEdge.getRightPoint().getY());
//                    line.setStroke(Color.BLACK);
//                    line.setStrokeWidth(5);
//                    pane.getChildren().add(line);
//                }

                List<Edge> leftChain = disjunctiveChain.get(leftCell);
                if (leftChain == null || leftChain.isEmpty()) {
                    nextLeftEdge.setNext(leftEdge);
                    disjunctiveChain.put(leftCell, new ArrayList<>(List.of(nextLeftEdge)));
                } else {
                    Edge lastEdge = leftChain.get(leftChain.size() - 1);
                    if (isConnected(lastEdge, nextLeftEdge)) {
                        nextLeftEdge.setPrev(lastEdge);
                        nextLeftEdge.setNext(leftEdge);
                        lastEdge.setNext(nextLeftEdge);
                    }
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
                    if (isConnected(lastEdge, nextRightEdge)) {
                        nextRightEdge.setPrev(lastEdge);
                        lastEdge.setNext(nextRightEdge);
                    }
                    rightChain.add(nextRightEdge);
                }

                upperCommonSupport.setLeftPoint(leftTwinCell.getCenter());
                nextLeftEdge.setTwin(nextRightEdge);
                prevPoint = leftPoint;
                rightEdge = null;
            } else if ((leftEdge == null && rightEdge != null) || (rightEdge != null && leftDistance >= rightDistance)) {
                Edge rightTwinEdge = null;
                Line rightLine = new Line(rightEdge);
                if (isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getLeftPoint()) && isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getRightPoint())) {
                    if (PointUtils.getLength(rightEdge.getLeftPoint(), rightPoint) > PointUtils.getLength(rightEdge.getRightPoint(), rightPoint)) {
                        eraseEdges(rightEdge, rightEdge.getRightPoint());
                        rightEdge.setRightPoint(rightPoint);
                        rightEdge.setInfiniteRightEnd(false);
                        rightCell.setBoundary(rightEdge);

                        rightTwinEdge = rightEdge.getTwin();
                        eraseEdges(rightTwinEdge, rightTwinEdge.getRightPoint());
                        rightTwinEdge.setRightPoint(rightPoint);
                        rightTwinEdge.setInfiniteRightEnd(false);
                    } else if (PointUtils.getLength(rightEdge.getLeftPoint(), rightPoint) < PointUtils.getLength(rightEdge.getRightPoint(), rightPoint)) {
                        eraseEdges(rightEdge, rightEdge.getLeftPoint());
                        rightEdge.setLeftPoint(rightPoint);
                        rightEdge.setInfiniteLeftEnd(false);
                        rightCell.setBoundary(rightEdge);

                        rightTwinEdge = rightEdge.getTwin();
                        eraseEdges(rightTwinEdge, rightTwinEdge.getLeftPoint());
                        rightTwinEdge.setLeftPoint(rightPoint);
                        rightTwinEdge.setInfiniteLeftEnd(false);
                    }
                } else if (isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getLeftPoint())) {
                    eraseEdges(rightEdge, rightEdge.getRightPoint());
                    rightEdge.setRightPoint(rightPoint);
                    rightEdge.setInfiniteRightEnd(false);
                    rightCell.setBoundary(rightEdge);

                    rightTwinEdge = rightEdge.getTwin();
                    eraseEdges(rightTwinEdge, rightTwinEdge.getRightPoint());
                    rightTwinEdge.setRightPoint(rightPoint);
                    rightTwinEdge.setInfiniteRightEnd(false);
                } else if (isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getRightPoint())) {
                    eraseEdges(rightEdge, rightEdge.getLeftPoint());
                    rightEdge.setLeftPoint(rightPoint);
                    rightEdge.setInfiniteLeftEnd(false);
                    rightCell.setBoundary(rightEdge);

                    rightTwinEdge = rightEdge.getTwin();
                    eraseEdges(rightTwinEdge, rightTwinEdge.getLeftPoint());
                    rightTwinEdge.setLeftPoint(rightPoint);
                    rightTwinEdge.setInfiniteLeftEnd(false);
                }
                assert rightTwinEdge != null;
                Cell rightTwinCell = rightTwinEdge.getCell();
                excludedEdges.put(rightTwinCell.getCenter(), rightTwinCell.getBoundary());
                rightTwinCell.setBoundary(rightTwinEdge);

                Edge nextRightEdge = new Edge((prevPoint == null ? middlePerpendicular.getRightPoint() : prevPoint), rightPoint, rightCell);
                nextRightEdge.setInfiniteLeftEnd(prevPoint == null);
                nextRightEdge.setInfiniteRightEnd(false);

//                if (leftDiagram.size() == 4) {
//                    javafx.scene.shape.Line line = new javafx.scene.shape.Line(nextRightEdge.getLeftPoint().getX(), nextRightEdge.getLeftPoint().getY(), nextRightEdge.getRightPoint().getX(), nextRightEdge.getRightPoint().getY());
//                    line.setStroke(Color.BLACK);
//                    line.setStrokeWidth(5);
//                    pane.getChildren().add(line);
//                }

                List<Edge> rightChain = disjunctiveChain.get(rightCell);
                if (rightChain == null || rightChain.isEmpty()) {
                    nextRightEdge.setNext(rightEdge);
                    disjunctiveChain.put(rightCell, new ArrayList<>(List.of(nextRightEdge)));
                } else {
                    Edge lastEdge = rightChain.get(rightChain.size() - 1);
                    if (isConnected(lastEdge, nextRightEdge)) {
                        nextRightEdge.setPrev(lastEdge);
                        nextRightEdge.setNext(rightEdge);
                        lastEdge.setNext(nextRightEdge);
                    }
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
                    if (isConnected(lastEdge, nextLeftEdge)) {
                        nextLeftEdge.setPrev(lastEdge);
                        lastEdge.setNext(nextLeftEdge);
                    }
                    leftChain.add(nextLeftEdge);
                }

                upperCommonSupport.setRightPoint(rightTwinCell.getCenter());
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
            if (isConnected(lastEdge, leftEdge)) {
                leftEdge.setPrev(lastEdge);
                lastEdge.setNext(leftEdge);
            }
            leftChain.add(leftEdge);
        }

        List<Edge> rightChain = disjunctiveChain.get(rightEdge.getCell());
        if (rightChain == null || rightChain.isEmpty()) {
            disjunctiveChain.put(rightEdge.getCell(), new ArrayList<>(List.of(rightEdge)));
        } else {
            Edge lastEdge = rightChain.get(rightChain.size() - 1);
            if (isConnected(lastEdge, rightEdge)) {
                rightEdge.setPrev(lastEdge);
                lastEdge.setNext(rightEdge);
            }
            rightChain.add(rightEdge);
        }

        disjunctiveChain.forEach((cell, chain) -> {
            Edge firstEdge = chain.get(0);
            Edge firstLeftEdge = cell.getConnectedEdge(firstEdge.getLeftPoint());

            Edge lastEdge = chain.get(chain.size() - 1);
            Edge lastRightEdge = cell.getConnectedEdge(lastEdge.getRightPoint());

            if (!firstEdge.isInfiniteLeftEnd() && firstLeftEdge != null) {
                firstEdge.setPrev(firstLeftEdge);
                firstLeftEdge.setNext(firstEdge);
            }

            for (int i = 0; i < chain.size() - 1; i++) {
                Edge prevEdge = chain.get(i);
                Edge nextEdge = chain.get(i + 1);
                if (nextEdge.getPrev() == null) {
                    Edge connectedEdge = prevEdge.getNext();
                    connectedEdge.setPrev(prevEdge);
                    connectedEdge.setNext(nextEdge);
                    nextEdge.setPrev(connectedEdge);
                }
            }

            if (!lastEdge.isInfiniteRightEnd() && lastRightEdge != null) {
                lastEdge.setNext(lastRightEdge);
                lastRightEdge.setPrev(lastEdge);
            }
        });

        Map<Point, Cell> diagram = new HashMap<>();
        diagram.putAll(leftDiagram);
        diagram.putAll(rightDiagram);
        return diagram;
    }

    private void eraseEdges(Edge edge, Point point) {
        Edge nextEdge = edge.getNext();
        if (nextEdge != null && (Objects.equals(point, nextEdge.getRightPoint()) || Objects.equals(point, nextEdge.getLeftPoint()))) {
            edge.setNext(null);
            nextEdge.setPrev(null);
        }

        Edge prevEdge = edge.getPrev();
        if (prevEdge != null && (Objects.equals(point, prevEdge.getRightPoint()) || Objects.equals(point, prevEdge.getLeftPoint()))) {
            edge.setPrev(null);
            prevEdge.setNext(null);
        }
    }

    private boolean isConnected(Edge a, Edge b) {
        return Objects.equals(a.getLeftPoint(), b.getRightPoint()) || Objects.equals(a.getRightPoint(), b.getLeftPoint()) || Objects.equals(a.getLeftPoint(), b.getLeftPoint()) || Objects.equals(a.getRightPoint(), b.getRightPoint());
    }

    public void drawVoronoyDiagram(List<Point> polygon) {
        log.info("Start drawing ");

        //       buildVoronoyDiagram(polygon.stream().sorted(Comparator.comparingDouble(Point::getX).thenComparing(Point::getY)).toList());
        buildVoronoyDiagram(polygon.stream().sorted(Comparator.comparingDouble(Point::getX).thenComparing(Point::getY)).toList())
                .values()
                .forEach(voronoyCell -> {
                    Edge edge = voronoyCell.getBoundary();
                    Edge nextEdge = voronoyCell.getBoundary();
                    do {
                        javafx.scene.shape.Line line = new javafx.scene.shape.Line(nextEdge.getLeftPoint().getX(), nextEdge.getLeftPoint().getY(), nextEdge.getRightPoint().getX(), nextEdge.getRightPoint().getY());
                        line.setStroke(Color.BLUE);
                        line.setStrokeWidth(1);
                        pane.getChildren().add(line);
                        nextEdge = nextEdge.getNext();
                    } while (nextEdge != null && !Objects.equals(new Line(edge), new Line(nextEdge)));

                    Edge prevEdge = voronoyCell.getBoundary();
                    do {
                        javafx.scene.shape.Line line = new javafx.scene.shape.Line(prevEdge.getLeftPoint().getX(), prevEdge.getLeftPoint().getY(), prevEdge.getRightPoint().getX(), prevEdge.getRightPoint().getY());
                        line.setStroke(Color.BLUE);
                        line.setStrokeWidth(1);
                        pane.getChildren().add(line);
                        prevEdge = prevEdge.getPrev();
                    } while (prevEdge != null && !Objects.equals(new Line(edge), new Line(prevEdge)));
                });

        log.info("End drawing");
    }

    private Edge getClosestEdge(Edge edge, Line middlePerpendicular, Edge excludedEdge) {
        if (edge == null) {
            return null;
        }

        Edge nextEdge = edge;
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

        Edge prevEdge = edge;
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
            double frackA = deltaYa / deltaXa;
            double frackB = deltaYb / deltaXb;

            double c = a2.getY() - a1.getY() + a1.getX() * frackA - a2.getX() * frackB;

            if (deltaYa * deltaXb - deltaYb * deltaXa == 0) {
                if (c == 0) {
                    return new Point(1, b.getEquationOfLine(1));
                }

                return null;
            }
            double x = c / (frackA - frackB);

            return new Point(x, a.getEquationOfLine(x));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}