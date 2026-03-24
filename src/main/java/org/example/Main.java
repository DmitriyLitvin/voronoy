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
import org.example.utils.VectorUtils;

import java.util.*;
import java.util.List;;

import static java.lang.Math.*;
import static org.example.entity.CommonSupportType.LOWER;
import static org.example.entity.CommonSupportType.UPPER;
import static org.example.utils.VectorUtils.crossProduct;

@Slf4j
public class Main extends Application {
    private final List<Point> points = new LinkedList<>();

    private final Pane pane = new Pane();
    private final BorderPane borderPane = new BorderPane();

    public void start(Stage stage) {

        borderPane.setCenter(pane);

        Button button = new Button("Voronoy diagram");
        button.setLayoutX(10); // X координата
        button.setLayoutY(950);
        borderPane.setBottom(button);
        pane.getChildren().add(button);


//        points.add(new Point(264.0, 613.0));
//        points.add(new Point(428.0, 861.0));
//        points.add(new Point(498.0, 629.0));
//        points.add(new Point(454.0, 292.0));

        points.add(new Point(697.0, 269.0));
        points.add(new Point(526.0, 479.0));
        points.add(new Point(567.0, 504.0));
        points.add(new Point(600.0, 746.0));


//        points.add(new Point(334.0, 866.0));
//        points.add(new Point(337.0, 688.0));
//        points.add(new Point(380.0, 454.0));
//        points.add(new Point(423.0, 567.0));
//        points.add(new Point(447.0, 441.0));
//        points.add(new Point(451.0, 654.0));
//        points.add(new Point(453.0, 367.0));
//        points.add(new Point(454.0, 700.0));
//
//        points.add(new Point(454.0, 670.0));
//        points.add(new Point(478.0, 233.0));
//        points.add(new Point(478.0, 750.0));
//        points.add(new Point(479.0, 471.0));
//        points.add(new Point(482.0, 374.0));
//        points.add(new Point(482.0, 680.0));
//        points.add(new Point(500.0, 313.0));
//        points.add(new Point(521.0, 614.0));


        points.forEach(p -> {
            Circle circle = new Circle(p.getX(), p.getY(), 2, Color.RED);
            Label label = new Label(+circle.getCenterX() + ", " + circle.getCenterY());

            label.relocate(circle.getCenterX() + 1, circle.getCenterY() + 1);
            pane.getChildren().addAll(label, circle);
        });


        int width = 1500;
        int height = 1000;
        Scene scene = new Scene(borderPane, width, height);
        scene.setOnMouseClicked((MouseEvent event) -> {
            double x = event.getX();
            double y = event.getY();

            // Додаємо точку на екран
            Circle circle = new Circle(x, y, 2, Color.RED);
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

    public void drawVoronoyDiagram(List<Point> polygon) {
        log.info("Start drawing ");
        buildVoronoyDiagram(polygon.stream().sorted(Comparator.comparingDouble(Point::getX)).toList()).values().stream().filter(a -> a.getCenter().equals(new Point(526.0, 479.0))).forEach(voronoyCell -> {
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

    private Set<Point> buildConvexHull(List<Point> points) {
        if (points.size() <= 2) {
            return new HashSet<>(points);
        }
        Point point = points.stream().min((p1, p2) -> {
            if (p1.getY() != p2.getY()) {
                return Double.compare(p1.getY(), p2.getY());
            }

            return Double.compare(p1.getX(), p2.getX());
        }).orElse(null);

        List<Point> sortedPoints = points.stream().filter(p -> !p.equals(point)).sorted((p1, p2) -> {
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

        return new HashSet<>(convexHull);
    }

    private Line getCommonSupport(Set<Point> leftPolygon, Set<Point> rightPolygon, CommonSupportType commonSupportType) {
        Point maxXpoint = leftPolygon.stream().max(Comparator.comparingDouble(Point::getX)).orElse(null);
        Point minXPoint = rightPolygon.stream().min(Comparator.comparingDouble(Point::getX)).orElse(null);
        Line line = new Line(maxXpoint, minXPoint);

        for (int i = 0; i < 2; i++) {
            Point leftPoint = maxXpoint;
            Point rightPoint = minXPoint;

            Iterator<Point> leftConvexPolygonIterator = leftPolygon.stream().filter(p -> !p.equals(maxXpoint)).iterator();
            Iterator<Point> rightConvexPolygonIterator = rightPolygon.stream().filter(p -> !p.equals(minXPoint)).iterator();
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
        Set<Point> leftPolygon = buildConvexHull(new ArrayList<>(leftDiagram.keySet()));
        Set<Point> rightPolygon = buildConvexHull(new ArrayList<>(rightDiagram.keySet()));

        Line upperCommonSupport = getCommonSupport(leftPolygon, rightPolygon, UPPER);
        Line lowerCommonSupport = getCommonSupport(leftPolygon, rightPolygon, LOWER);

        Point currentChainPoint = null;
        Edge currentEdge = null;
        Edge currentChainEdge = null;
        Line middlePerpendicular;
        Map<Cell, List<Edge>> excludedEdges = new HashMap<>();
        Map<Cell, Edge> disjunctiveChain = new HashMap<>();

        while (!Objects.equals(upperCommonSupport, lowerCommonSupport)) {
            Cell leftCell = leftDiagram.get(upperCommonSupport.getLeftPoint());
            Cell rightCell = rightDiagram.get(upperCommonSupport.getRightPoint());

            middlePerpendicular = getMiddlePerpendicular(upperCommonSupport);
            Point midPoint = upperCommonSupport.getMidPoint();

            boolean isInfiniteLeftEnd = false;
            if (currentChainPoint == null) {
                isInfiniteLeftEnd = true;
                currentChainPoint = middlePerpendicular.getRightPoint();
            }

            boolean isLeftExcludedEdge = false;
            double leftDistance = 0;
            Point leftPoint = null;
            Edge leftExcludedEdge = getClosestEdge(excludedEdges.get(leftCell), middlePerpendicular, currentEdge, currentChainEdge, currentChainPoint);
            Edge leftEdge = getClosestEdge(new ArrayList<>(List.of(leftCell.getBoundary())), middlePerpendicular, currentEdge, currentChainEdge, currentChainPoint);
            if (leftEdge != null) {
                leftPoint = getPointOfIntersection(middlePerpendicular, new Line(leftEdge));
                assert leftPoint != null;
                leftDistance = VectorUtils.getLength(leftPoint, currentChainPoint);
            }
            if (leftExcludedEdge != null) {
                Point leftIntersectPoint = getPointOfIntersection(middlePerpendicular, new Line(leftExcludedEdge));
                if (leftIntersectPoint != null) {
                    double currentDistance = VectorUtils.getLength(leftIntersectPoint, currentChainPoint);
                    if (currentDistance < leftDistance || leftEdge == null) {
                        leftDistance = currentDistance;
                        leftPoint = leftIntersectPoint;
                        leftEdge = leftExcludedEdge;
                        isLeftExcludedEdge = true;
                    }
                }
            }

            boolean isRightExcludedEdge = false;
            double rightDistance = 0;
            Point rightPoint = null;
            Edge rightExcludedEdge = getClosestEdge(excludedEdges.get(rightCell), middlePerpendicular, currentEdge, currentChainEdge, currentChainPoint);
            Edge rightEdge = getClosestEdge(new ArrayList<>(List.of(rightCell.getBoundary())), middlePerpendicular, currentEdge, currentChainEdge, currentChainPoint);
            if (rightEdge != null) {
                rightPoint = getPointOfIntersection(middlePerpendicular, new Line(rightEdge));
                assert rightPoint != null;
                rightDistance = VectorUtils.getLength(rightPoint, currentChainPoint);
            }
            if (rightExcludedEdge != null) {
                Point rightIntersectPoint = getPointOfIntersection(middlePerpendicular, new Line(rightExcludedEdge));
                if (rightIntersectPoint != null) {
                    double currentDistance = VectorUtils.getLength(rightIntersectPoint, currentChainPoint);
                    if (currentDistance < rightDistance || rightEdge == null) {
                        rightDistance = currentDistance;
                        rightPoint = rightIntersectPoint;
                        rightEdge = rightExcludedEdge;
                        isRightExcludedEdge = true;
                    }
                }
            }

            if (rightEdge == null && leftEdge == null) {
                System.out.println("couldn't find the closest edge");
                break;
            } else if ((leftEdge != null && rightEdge == null) || (leftEdge != null && leftDistance < rightDistance)) {
                Edge leftTwinEdge = leftEdge.getTwin();
                Line leftLine = new Line(leftEdge);
                if (isOnTheSameSide(leftCell.getCenter(), leftLine.getLeftPoint(), midPoint) && isOnTheSameSide(leftCell.getCenter(), leftLine.getRightPoint(), midPoint)) {
                    if (VectorUtils.getLength(leftEdge.getLeftPoint(), leftPoint) > VectorUtils.getLength(leftEdge.getRightPoint(), leftPoint)) {
                        Cell leftTwinCell = leftTwinEdge.getCell();

                        Edge erasedEdge = eraseEdges(leftTwinEdge, leftTwinEdge.getRightPoint());
                        leftTwinEdge.setRightPoint(leftPoint);
                        leftTwinEdge.setInfiniteRightEnd(false);
                        List<Edge> leftTwinExcludedEdges = excludedEdges.get(leftTwinCell);
                        if (leftTwinExcludedEdges == null || leftTwinExcludedEdges.isEmpty()) {
                            leftTwinCell.setBoundary(leftTwinEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(leftTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                        }

                        erasedEdge = eraseEdges(leftEdge, leftEdge.getRightPoint());
                        leftEdge.setRightPoint(leftPoint);
                        leftEdge.setInfiniteRightEnd(false);
                        List<Edge> leftExcludedEdges = excludedEdges.get(leftCell);
                        if (leftExcludedEdges == null || leftExcludedEdges.isEmpty()) {
                            leftCell.setBoundary(leftEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(leftCell, k -> new ArrayList<>()).add(erasedEdge);
                        }
                    } else if (VectorUtils.getLength(leftEdge.getLeftPoint(), leftPoint) < VectorUtils.getLength(leftEdge.getRightPoint(), leftPoint)) {
                        Cell leftTwinCell = leftTwinEdge.getCell();

                        Edge erasedEdge = eraseEdges(leftTwinEdge, leftTwinEdge.getLeftPoint());
                        leftTwinEdge.setLeftPoint(leftPoint);
                        leftTwinEdge.setInfiniteLeftEnd(false);
                        List<Edge> leftTwinExcludedEdges = excludedEdges.get(leftTwinCell);
                        if (leftTwinExcludedEdges == null || leftTwinExcludedEdges.isEmpty()) {
                            leftTwinCell.setBoundary(leftTwinEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(leftTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                        }

                        erasedEdge = eraseEdges(leftEdge, leftEdge.getLeftPoint());
                        leftEdge.setLeftPoint(leftPoint);
                        leftEdge.setInfiniteLeftEnd(false);
                        List<Edge> leftExcludedEdges = excludedEdges.get(leftCell);
                        if (leftExcludedEdges == null || leftExcludedEdges.isEmpty()) {
                            leftCell.setBoundary(leftEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(leftCell, k -> new ArrayList<>()).add(erasedEdge);
                        }
                    }
                } else if (isOnTheSameSide(leftCell.getCenter(), leftLine.getLeftPoint(), midPoint)) {
                    Cell leftTwinCell = leftTwinEdge.getCell();

                    Edge erasedEdge = eraseEdges(leftTwinEdge, leftTwinEdge.getRightPoint());
                    leftTwinEdge.setRightPoint(leftPoint);
                    leftTwinEdge.setInfiniteRightEnd(false);
                    List<Edge> leftTwinExcludedEdges = excludedEdges.get(leftTwinCell);
                    if (leftTwinExcludedEdges == null || leftTwinExcludedEdges.isEmpty()) {
                        leftTwinCell.setBoundary(leftTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    erasedEdge = eraseEdges(leftEdge, leftEdge.getRightPoint());
                    leftEdge.setRightPoint(leftPoint);
                    leftEdge.setInfiniteRightEnd(false);
                    List<Edge> leftExcludedEdges = excludedEdges.get(leftCell);
                    if (leftExcludedEdges == null || leftExcludedEdges.isEmpty()) {
                        leftCell.setBoundary(leftEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftCell, k -> new ArrayList<>()).add(erasedEdge);
                    }
                } else if (isOnTheSameSide(leftCell.getCenter(), leftLine.getRightPoint(), midPoint)) {
                    Cell leftTwinCell = leftTwinEdge.getCell();

                    Edge erasedEdge = eraseEdges(leftTwinEdge, leftTwinEdge.getLeftPoint());
                    leftTwinEdge.setLeftPoint(leftPoint);
                    leftTwinEdge.setInfiniteLeftEnd(false);
                    List<Edge> leftTwinExcludedEdges = excludedEdges.get(leftTwinCell);
                    if (leftTwinExcludedEdges == null || leftTwinExcludedEdges.isEmpty()) {
                        leftTwinCell.setBoundary(leftTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    erasedEdge = eraseEdges(leftEdge, leftEdge.getLeftPoint());
                    leftEdge.setLeftPoint(leftPoint);
                    leftEdge.setInfiniteLeftEnd(false);
                    List<Edge> leftExcludedEdges = excludedEdges.get(leftCell);
                    if (leftExcludedEdges == null || leftExcludedEdges.isEmpty()) {
                        leftCell.setBoundary(leftEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftCell, k -> new ArrayList<>()).add(erasedEdge);
                    }
                }
                assert leftTwinEdge != null;
                Edge nextLeftEdge = new Edge(currentChainPoint, leftPoint, leftCell);
                nextLeftEdge.setInfiniteLeftEnd(isInfiniteLeftEnd);
                nextLeftEdge.setInfiniteRightEnd(false);
                nextLeftEdge.setNext(leftEdge);
                leftEdge.setPrev(nextLeftEdge);

                Edge edge = disjunctiveChain.get(leftCell);
                if (edge == null) {
                    if (isLeftExcludedEdge) {
                        Edge lastEdge = leftCell.getBoundary().getLastEdge();
                        if (lastEdge != null && isConnected(lastEdge, nextLeftEdge)) {
                            nextLeftEdge.setPrev(lastEdge);
                            lastEdge.setNext(nextLeftEdge);
                        }
                    } else {
                        disjunctiveChain.put(leftCell, nextLeftEdge);
                    }
                } else {
                    Edge lastEdge = edge.getLastEdge();
                    if (lastEdge != null && isConnected(lastEdge, nextLeftEdge)) {
                        nextLeftEdge.setPrev(lastEdge);
                        lastEdge.setNext(nextLeftEdge);

                        disjunctiveChain.remove(leftCell);
                    }
                }

                Edge nextRightEdge = new Edge(currentChainPoint, leftPoint, rightCell);
                nextRightEdge.setInfiniteLeftEnd(isInfiniteLeftEnd);
                nextRightEdge.setInfiniteRightEnd(false);
                nextRightEdge.setTwin(nextLeftEdge);

                edge = disjunctiveChain.get(rightCell);
                if (edge == null) {
                    Edge startEdge = rightCell.getBoundary().getStartEdge();
                    if (startEdge != null && isConnected(startEdge, nextRightEdge)) {
                        nextRightEdge.setNext(startEdge);
                        startEdge.setPrev(nextRightEdge);
                    } else {
                        disjunctiveChain.put(rightCell, nextRightEdge);
                    }
                } else {
                    Edge startEdge = edge.getStartEdge();
                    if (startEdge != null && isConnected(startEdge, nextRightEdge)) {
                        nextRightEdge.setNext(startEdge);
                        startEdge.setPrev(nextRightEdge);

                        disjunctiveChain.remove(rightCell);
                    }
                }

                upperCommonSupport.setLeftPoint(leftTwinEdge.getCell().getCenter());
                nextLeftEdge.setTwin(nextRightEdge);
                currentChainPoint = leftPoint;
                currentEdge = leftEdge;
                currentChainEdge = nextLeftEdge;
            } else if (leftEdge == null || leftDistance >= rightDistance) {
                Line rightLine = new Line(rightEdge);
                Edge rightTwinEdge = rightEdge.getTwin();
                if (isOnTheSameSide(rightCell.getCenter(), rightLine.getLeftPoint(), midPoint) && isOnTheSameSide(rightCell.getCenter(), rightLine.getRightPoint(), midPoint)) {
                    if (VectorUtils.getLength(rightEdge.getLeftPoint(), rightPoint) > VectorUtils.getLength(rightEdge.getRightPoint(), rightPoint)) {
                        Cell rightTwinCell = rightTwinEdge.getCell();

                        Edge erasedEdge = eraseEdges(rightTwinEdge, rightTwinEdge.getRightPoint());
                        rightTwinEdge.setRightPoint(rightPoint);
                        rightTwinEdge.setInfiniteRightEnd(false);
                        List<Edge> rightTwinExcludedEdges = excludedEdges.get(rightTwinCell);
                        if (rightTwinExcludedEdges == null || rightTwinExcludedEdges.isEmpty()) {
                            rightTwinCell.setBoundary(rightTwinEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(rightTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                        }

                        erasedEdge = eraseEdges(rightEdge, rightEdge.getRightPoint());
                        rightEdge.setRightPoint(rightPoint);
                        rightEdge.setInfiniteRightEnd(false);
                        List<Edge> rightExcludedEdges = excludedEdges.get(rightCell);
                        if (rightExcludedEdges == null || rightExcludedEdges.isEmpty()) {
                            rightCell.setBoundary(rightEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(rightCell, k -> new ArrayList<>()).add(erasedEdge);
                        }
                    } else if (VectorUtils.getLength(rightEdge.getLeftPoint(), rightPoint) < VectorUtils.getLength(rightEdge.getRightPoint(), rightPoint)) {
                        Cell rightTwinCell = rightTwinEdge.getCell();

                        Edge erasedEdge = eraseEdges(rightTwinEdge, rightTwinEdge.getLeftPoint());
                        rightTwinEdge.setLeftPoint(rightPoint);
                        rightTwinEdge.setInfiniteLeftEnd(false);
                        List<Edge> rightTwinExcludedEdges = excludedEdges.get(rightTwinCell);
                        if (rightTwinExcludedEdges == null || rightTwinExcludedEdges.isEmpty()) {
                            rightTwinCell.setBoundary(rightTwinEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(rightTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                        }

                        erasedEdge = eraseEdges(rightEdge, rightEdge.getLeftPoint());
                        rightEdge.setLeftPoint(rightPoint);
                        rightEdge.setInfiniteLeftEnd(false);
                        List<Edge> rightExcludedEdges = excludedEdges.get(rightCell);
                        if (rightExcludedEdges == null || rightExcludedEdges.isEmpty()) {
                            rightCell.setBoundary(rightEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(rightCell, k -> new ArrayList<>()).add(erasedEdge);
                        }
                    }
                } else if (isOnTheSameSide(rightCell.getCenter(), rightLine.getLeftPoint(), midPoint)) {
                    Cell rightTwinCell = rightTwinEdge.getCell();

                    Edge erasedEdge = eraseEdges(rightTwinEdge, rightTwinEdge.getRightPoint());
                    rightTwinEdge.setRightPoint(rightPoint);
                    rightTwinEdge.setInfiniteRightEnd(false);
                    List<Edge> rightTwinExcludedEdges = excludedEdges.get(rightTwinCell);
                    if (rightTwinExcludedEdges == null || rightTwinExcludedEdges.isEmpty()) {
                        rightTwinCell.setBoundary(rightTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    erasedEdge = eraseEdges(rightEdge, rightEdge.getRightPoint());
                    rightEdge.setRightPoint(rightPoint);
                    rightEdge.setInfiniteRightEnd(false);
                    List<Edge> rightExcludedEdges = excludedEdges.get(rightCell);
                    if (rightExcludedEdges == null || rightExcludedEdges.isEmpty()) {
                        rightCell.setBoundary(rightEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightCell, k -> new ArrayList<>()).add(erasedEdge);
                    }
                } else if (isOnTheSameSide(rightCell.getCenter(), rightLine.getRightPoint(), midPoint)) {
                    Cell rightTwinCell = rightTwinEdge.getCell();

                    Edge erasedEdge = eraseEdges(rightTwinEdge, rightTwinEdge.getLeftPoint());
                    rightTwinEdge.setLeftPoint(rightPoint);
                    rightTwinEdge.setInfiniteLeftEnd(false);
                    List<Edge> rightTwinExcludedEdges = excludedEdges.get(rightTwinCell);
                    if (rightTwinExcludedEdges == null || rightTwinExcludedEdges.isEmpty()) {
                        rightTwinCell.setBoundary(rightTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    erasedEdge = eraseEdges(rightEdge, rightEdge.getLeftPoint());
                    rightEdge.setLeftPoint(rightPoint);
                    rightEdge.setInfiniteLeftEnd(false);
                    List<Edge> rightExcludedEdges = excludedEdges.get(rightCell);
                    if (rightExcludedEdges == null || rightExcludedEdges.isEmpty()) {
                        rightCell.setBoundary(rightEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightCell, k -> new ArrayList<>()).add(erasedEdge);
                    }
                }
                assert rightTwinEdge != null;
                Edge nextRightEdge = new Edge(currentChainPoint, rightPoint, rightCell);
                nextRightEdge.setInfiniteLeftEnd(isInfiniteLeftEnd);
                nextRightEdge.setInfiniteRightEnd(false);
                nextRightEdge.setPrev(rightEdge);
                rightEdge.setNext(nextRightEdge);

                Edge edge = disjunctiveChain.get(rightCell);
                if (edge == null) {
                    if (isRightExcludedEdge) {
                        Edge startEdge = rightCell.getBoundary().getStartEdge();
                        if (startEdge != null && isConnected(startEdge, nextRightEdge)) {
                            nextRightEdge.setNext(startEdge);
                            startEdge.setPrev(nextRightEdge);
                        }
                    } else {
                        disjunctiveChain.put(rightCell, nextRightEdge);
                    }
                } else {
                    Edge startEdge = edge.getStartEdge();
                    if (startEdge != null && isConnected(startEdge, nextRightEdge)) {
                        nextRightEdge.setNext(startEdge);
                        startEdge.setPrev(nextRightEdge);

                        disjunctiveChain.remove(rightCell);
                    }
                }

                Edge nextLeftEdge = new Edge(currentChainPoint, rightPoint, leftCell);
                nextLeftEdge.setInfiniteLeftEnd(isInfiniteLeftEnd);
                nextLeftEdge.setInfiniteRightEnd(false);
                nextLeftEdge.setTwin(nextRightEdge);

                edge = disjunctiveChain.get(leftCell);
                if (edge == null) {
                    Edge lastEdge = leftCell.getBoundary().getLastEdge();
                    if (lastEdge != null && isConnected(lastEdge, nextLeftEdge)) {
                        nextLeftEdge.setPrev(lastEdge);
                        lastEdge.setNext(nextLeftEdge);
                    } else {
                        disjunctiveChain.put(leftCell, nextLeftEdge);
                    }
                } else {
                    Edge lastEdge = edge.getLastEdge();
                    if (lastEdge != null && isConnected(lastEdge, nextLeftEdge)) {
                        nextLeftEdge.setPrev(lastEdge);
                        lastEdge.setNext(nextLeftEdge);

                        disjunctiveChain.remove(leftCell);
                    }
                }

                upperCommonSupport.setRightPoint(rightTwinEdge.getCell().getCenter());
                nextRightEdge.setTwin(nextLeftEdge);
                currentChainPoint = rightPoint;
                currentEdge = rightEdge;
                currentChainEdge = nextRightEdge;
            }
        }

        middlePerpendicular = getMiddlePerpendicular(lowerCommonSupport);

        Cell leftCell = leftDiagram.get(lowerCommonSupport.getLeftPoint());
        Cell rightCell = rightDiagram.get(lowerCommonSupport.getRightPoint());

        Edge leftEdge = new Edge(currentChainPoint, middlePerpendicular.getLeftPoint(), leftCell);
        leftEdge.setInfiniteLeftEnd(false);
        Edge rightEdge = new Edge(currentChainPoint, middlePerpendicular.getLeftPoint(), rightCell);
        rightEdge.setInfiniteLeftEnd(false);

        leftEdge.setTwin(rightEdge);
        rightEdge.setTwin(leftEdge);

        Edge edge = disjunctiveChain.get(leftCell);
        if (edge == null) {
            addLeftEdge(leftCell, leftEdge);
        } else {
            addLeftEdge(leftCell, edge);
            addLeftEdge(leftCell, leftEdge);
        }

        edge = disjunctiveChain.get(rightCell);
        if (edge == null) {
            addRightEdge(rightCell, rightEdge);
        } else {
            addRightEdge(rightCell, edge);
            addRightEdge(rightCell, rightEdge);
        }

        Map<Point, Cell> diagram = new HashMap<>();
        diagram.putAll(leftDiagram);
        diagram.putAll(rightDiagram);

        return diagram;
    }

    private void addLeftEdge(Cell leftCell, Edge leftEdge) {
        Edge boundary = leftCell.getBoundary();
        Edge startEdge = boundary.getStartEdge();
        Edge lastEdge = boundary.getLastEdge();
        if (startEdge != null && isConnected(startEdge, leftEdge) && !Objects.equals(new Line(startEdge), new Line(leftEdge))) {
            if (startEdge.getNext() == null) {
                startEdge.setNext(leftEdge);
                leftEdge.setPrev(startEdge);
            } else if (startEdge.getPrev() == null) {
                startEdge.setPrev(leftEdge);
                leftEdge.setNext(startEdge);
            }
        } else if (lastEdge != null && isConnected(lastEdge, leftEdge) && !Objects.equals(new Line(lastEdge), new Line(leftEdge))) {
            if (lastEdge.getNext() == null) {
                lastEdge.setNext(leftEdge);
                leftEdge.setPrev(lastEdge);
            } else if (lastEdge.getPrev() == null) {
                lastEdge.setPrev(leftEdge);
                leftEdge.setNext(lastEdge);
            }
        }
    }

    private void addRightEdge(Cell rightCell, Edge rightEdge) {
        Edge boundary = rightCell.getBoundary();
        Edge startEdge = boundary.getStartEdge();
        Edge lastEdge = boundary.getLastEdge();
        if (startEdge != null && isConnected(startEdge, rightEdge) && !Objects.equals(new Line(startEdge), new Line(rightEdge))) {
            if (startEdge.getPrev() == null) {
                startEdge.setPrev(rightEdge);
                rightEdge.setNext(startEdge);
            } else if (startEdge.getNext() == null) {
                startEdge.setNext(rightEdge);
                rightEdge.setPrev(startEdge);
            }
        } else if (lastEdge != null && isConnected(lastEdge, rightEdge) && !Objects.equals(new Line(lastEdge), new Line(rightEdge))) {
            if (lastEdge.getPrev() == null) {
                lastEdge.setPrev(rightEdge);
                rightEdge.setNext(lastEdge);
            } else if (lastEdge.getNext() == null) {
                lastEdge.setNext(rightEdge);
                rightEdge.setPrev(lastEdge);
            }
        }
    }


    private Edge eraseEdges(Edge edge, Point point) {
        Edge nextEdge = edge.getNext();
        if (nextEdge != null && (Objects.equals(point, nextEdge.getRightPoint()) || Objects.equals(point, nextEdge.getLeftPoint()))) {
            edge.setNext(null);
            nextEdge.setPrev(null);

            return nextEdge;
        }

        Edge prevEdge = edge.getPrev();
        if (prevEdge != null && (Objects.equals(point, prevEdge.getRightPoint()) || Objects.equals(point, prevEdge.getLeftPoint()))) {
            edge.setPrev(null);
            prevEdge.setNext(null);

            return prevEdge;
        }

        return null;
    }

    private boolean isConnected(Edge firstEdge, Edge secondEdge) {
        return Objects.equals(firstEdge.getLeftPoint(), secondEdge.getRightPoint()) || Objects.equals(firstEdge.getRightPoint(), secondEdge.getLeftPoint()) || Objects.equals(firstEdge.getLeftPoint(), secondEdge.getLeftPoint()) || Objects.equals(firstEdge.getRightPoint(), secondEdge.getRightPoint());
    }

    private boolean isOutsideCell(Edge currentEdge, Point currentPoint, Point intersectPoint) {
        if (currentEdge == null) {
            return true;
        }
        Edge prevEdge = currentEdge.getPrev();
        Edge nextEdge = currentEdge.getNext();

        Point nextPoint = null;
        if (prevEdge != null) {
            if (Objects.equals(prevEdge.getLeftPoint(), currentPoint)) {
                nextPoint = prevEdge.getRightPoint();
            } else if (Objects.equals(prevEdge.getRightPoint(), currentPoint)) {
                nextPoint = prevEdge.getLeftPoint();
            }
        }
        if (nextEdge != null) {
            if (Objects.equals(nextEdge.getLeftPoint(), currentPoint)) {
                nextPoint = nextEdge.getRightPoint();
            } else if (Objects.equals(nextEdge.getRightPoint(), currentPoint)) {
                nextPoint = nextEdge.getLeftPoint();
            }
        }

        Point prevPoint = null;
        if (Objects.equals(currentEdge.getLeftPoint(), currentPoint)) {
            prevPoint = currentEdge.getRightPoint();
        } else if (Objects.equals(currentEdge.getRightPoint(), currentPoint)) {
            prevPoint = currentEdge.getLeftPoint();
        }

        if (nextPoint == null || prevPoint == null) {
            return true;
        }

        return !isPointInsideAngle(prevPoint, currentPoint, nextPoint, intersectPoint);
    }

    private boolean isPointInsideAngle(Point prevPoint, Point currentPoint, Point nextPoint, Point intersectPoint) {
        if (currentPoint == null) {
            return false;
        }
        Point prevDirectionPoint = VectorUtils.getDirectionPoint(currentPoint, prevPoint);
        Point currentDirectionPoint = VectorUtils.getDirectionPoint(currentPoint, intersectPoint);
        Point nextDirectionPoint = VectorUtils.getDirectionPoint(currentPoint, nextPoint);

        double crossProduct = crossProduct(prevDirectionPoint, nextDirectionPoint);
        if (crossProduct > 0) {
            return crossProduct(prevDirectionPoint, currentDirectionPoint) > 0 && crossProduct(currentDirectionPoint, nextDirectionPoint) > 0;
        } else {
            return crossProduct(prevDirectionPoint, currentDirectionPoint) < 0 && crossProduct(currentDirectionPoint, nextDirectionPoint) < 0;
        }
    }

    private Edge getClosestEdge(List<Edge> edges, Line middlePerpendicular, Edge currentEdge, Edge currentChainEdge, Point currentChainPoint) {
        if (edges == null || edges.isEmpty()) {
            return null;
        }

        Edge intersectedEdge = null;
        for (Edge edge : edges) {
            Edge nextEdge = edge;
            double distance = 0;
            do {
                if ((currentChainEdge == null || !Objects.equals(new Line(currentChainEdge), new Line(nextEdge))) && (currentEdge == null || !Objects.equals(new Line(currentEdge), new Line(nextEdge)))) {
                    Point intersectPoint = getPointOfIntersection(middlePerpendicular, new Line(nextEdge));
                    if (intersectPoint != null && isIntersected(intersectPoint, new Line(nextEdge)) && isOutsideCell(currentEdge, currentChainPoint, intersectPoint)) {
                        double currentDistance = VectorUtils.getLength(intersectPoint, middlePerpendicular.getRightPoint());
                        if (distance == 0 || currentDistance < distance) {
                            distance = currentDistance;
                            intersectedEdge = nextEdge;
                        }
                    }
                }
                nextEdge = nextEdge.getNext();
            } while (nextEdge != null && !Objects.equals(new Line(edge), new Line(nextEdge)));

            Edge prevEdge = edge;
            do {
                if ((currentChainEdge == null || !Objects.equals(new Line(currentChainEdge), new Line(prevEdge))) && (currentEdge == null || !Objects.equals(new Line(currentEdge), new Line(prevEdge)))) {
                    Point intersectPoint = getPointOfIntersection(middlePerpendicular, new Line(prevEdge));
                    if (intersectPoint != null && isIntersected(intersectPoint, new Line(prevEdge)) && isOutsideCell(currentEdge, currentChainPoint, intersectPoint)) {
                        double currentDistance = VectorUtils.getLength(intersectPoint, middlePerpendicular.getRightPoint());
                        if (distance == 0 || currentDistance < distance) {
                            distance = currentDistance;
                            intersectedEdge = prevEdge;
                        }
                    }
                }
                prevEdge = prevEdge.getPrev();
            } while (prevEdge != null && !Objects.equals(new Line(edge), new Line(prevEdge)));
        }

        return intersectedEdge;
    }

    public boolean isIntersected(Point point, Line line) {
        if (point == null) {
            return false;
        } else if (line.isInfiniteRightEnd() && line.isInfiniteLeftEnd()) {
            return true;
        } else if (!line.isInfiniteRightEnd() && !line.isInfiniteLeftEnd()) {
            Point leftPoint = line.getLeftPoint();
            Point rightPoint = line.getRightPoint();
            return VectorUtils.dotProduct(VectorUtils.getDirectionPoint(point, leftPoint), VectorUtils.getDirectionPoint(point, rightPoint)) <= 0;
        } else if (line.isInfiniteLeftEnd()) {
            Point rightPoint = line.getRightPoint();
            Point leftPoint = line.getLeftPoint();
            return VectorUtils.dotProduct(VectorUtils.getDirectionPoint(point, rightPoint), VectorUtils.getDirectionPoint(leftPoint, rightPoint)) >= 0;
        }

        Point leftPoint = line.getLeftPoint();
        Point rightPoint = line.getRightPoint();
        return VectorUtils.dotProduct(VectorUtils.getDirectionPoint(point, leftPoint), VectorUtils.getDirectionPoint(rightPoint, leftPoint)) >= 0;
    }

    private Line getMiddlePerpendicular(Line line) {
        int height = 1_000_000;
        int width = 1_000_000;

        Point point = line.getMidPoint();
        double x = point.getX();
        double y = point.getY();

        Point directionPoint = VectorUtils.getDirectionPoint(line.getLeftPoint(), line.getRightPoint());
        if (VectorUtils.dotProduct(directionPoint, new Point(1, 0)) == 0) {
            return new Line(new Point(-width, y), new Point(width, y));
        } else if (VectorUtils.dotProduct(directionPoint, new Point(0, 1)) == 0) {
            return new Line(new Point(x, -height), new Point(x, height));
        } else {
            if (directionPoint.getX() == 0) {
                return new Line(new Point(x, -height), new Point(x, height));
            }
            return new Line(new Point((y * directionPoint.getY()) / directionPoint.getX() + x, 0), new Point((-(height - y) * directionPoint.getY()) / directionPoint.getX() + x, height));
        }
    }


    private boolean isOnTheSameSide(Point firstPoint, Point secondPoint, Point midPoint) {
        return VectorUtils.dotProduct(VectorUtils.getDirectionPoint(midPoint, firstPoint), VectorUtils.getDirectionPoint(midPoint, secondPoint)) >= 0;
    }

    private Point getPointOfIntersection(Line firstLine, Line secondLine) {
        Point firstLineLeftPoint = firstLine.getLeftPoint();
        Point firstLineRightPoint = firstLine.getRightPoint();

        Point secondLineLeftPoint = secondLine.getLeftPoint();
        Point secondLineRightPoint = secondLine.getRightPoint();

        double firstDelta = firstLineRightPoint.getX() - firstLineLeftPoint.getX();
        double secondDelta = firstLineRightPoint.getY() - firstLineLeftPoint.getY();

        double thirdDelta = secondLineRightPoint.getX() - secondLineLeftPoint.getX();
        double forthDelta = secondLineRightPoint.getY() - secondLineLeftPoint.getY();

        if (firstDelta == 0) {
            return new Point(firstLineLeftPoint.getX(), secondLine.getEquationOfLine(firstLineLeftPoint.getX()));
        } else if (thirdDelta == 0) {
            return new Point(secondLineLeftPoint.getX(), firstLine.getEquationOfLine(secondLineLeftPoint.getX()));
        }

        double firstSlope = secondDelta / firstDelta;
        double secondSlope = forthDelta / thirdDelta;

        if (secondDelta * thirdDelta - forthDelta * firstDelta == 0) {
            return null;
        }

        double x = (secondLineLeftPoint.getY() - firstLineLeftPoint.getY() + firstLineLeftPoint.getX() * firstSlope - secondLineLeftPoint.getX() * secondSlope) / (firstSlope - secondSlope);
        return new Point(x, firstLine.getEquationOfLine(x));
    }


    public static void main(String[] args) {
        launch(args);
    }
}