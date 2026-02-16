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


//        points.add(new Point(69.0, 530.0));
//        points.add(new Point(149.0, 369.0));
//        points.add(new Point(153.0, 270.0));
//        points.add(new Point(165.0, 347.0));
//        points.add(new Point(180.0, 431.0));
//        points.add(new Point(179.0, 399.0));
//        points.add(new Point(248.0, 653.0));
//        points.add(new Point(227.0, 856.0));


        points.add(new Point(780.0, 490.0));
        points.add(new Point(866.0, 351.0));
        points.add(new Point(874.0, 377.0));
        points.add(new Point(843.0, 434.0));
        points.add(new Point(895.0, 504.0));
        points.add(new Point(918.0, 784.0));
        points.add(new Point(889.0, 235.0));
        points.add(new Point(898.0, 343.0));

        points.forEach(p -> {
            Circle circle = new Circle(p.getX(), p.getY(), 3, Color.RED);
            Label label = new Label(+circle.getCenterX() + ", " + circle.getCenterY());

            label.relocate(circle.getCenterX() + 1, circle.getCenterY() + 1);
            pane.getChildren().addAll(label, circle);
        });

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

    public void drawVoronoyDiagram(List<Point> polygon) {
        log.info("Start drawing ");
        buildVoronoyDiagram(polygon.stream().sorted(Comparator.comparingDouble(Point::getX)).toList()).values().stream().filter(a -> a.getCenter().equals(new Point(895, 504))).forEach(voronoyCell -> {
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
        Line middlePerpendicular;
        Map<Point, List<Edge>> excludedEdges = new HashMap<>();
        Map<Cell, List<Edge>> disjunctiveChain = new HashMap<>();

        Point directionPoint = null;
        Line lowerPerpendicular = getMiddlePerpendicular(lowerCommonSupport);
        Point midPoint = upperCommonSupport.getMidPoint();
        Line upperPerpendicular = getMiddlePerpendicular(upperCommonSupport);
        Point currentPoint = getPointOfIntersection(upperCommonSupport, lowerCommonSupport);
        if (currentPoint != null) {
            Point intersectPoint = getPointOfIntersection(upperPerpendicular, lowerPerpendicular);
            assert intersectPoint != null;
            if (isPointInsideAngle(upperCommonSupport.getLeftPoint(), currentPoint, lowerCommonSupport.getLeftPoint(), intersectPoint) || isPointInsideAngle(upperCommonSupport.getRightPoint(), currentPoint, lowerCommonSupport.getRightPoint(), intersectPoint)) {
                directionPoint = VectorUtils.getDirectionPoint(midPoint, intersectPoint);
            } else {
                Point lowerPoint = getPointOfIntersection(upperPerpendicular, lowerCommonSupport);
                if (lowerPoint != null && isIntersected(lowerPoint, new Line(midPoint, intersectPoint))) {
                    directionPoint = VectorUtils.getDirectionPoint(midPoint, intersectPoint);
                } else {
                    directionPoint = VectorUtils.getDirectionPoint(intersectPoint, midPoint);
                }
            }
        } else {
            Point lowerPoint = getPointOfIntersection(upperPerpendicular, lowerCommonSupport);
            if (lowerPoint != null) {
                directionPoint = VectorUtils.getDirectionPoint(midPoint, lowerPoint);
            }
        }

        while (!Objects.equals(upperCommonSupport, lowerCommonSupport)) {
            Point leftPointOfCommonSupport = upperCommonSupport.getLeftPoint();
            Cell leftCell = leftDiagram.get(leftPointOfCommonSupport);

            Point rightPointOfCommonSupport = upperCommonSupport.getRightPoint();
            Cell rightCell = rightDiagram.get(rightPointOfCommonSupport);

            middlePerpendicular = getMiddlePerpendicular(upperCommonSupport);

            if (rightDiagram.size() == 4) {
                javafx.scene.shape.Line line = new javafx.scene.shape.Line(upperCommonSupport.getLeftPoint().getX(), upperCommonSupport.getLeftPoint().getY(), upperCommonSupport.getRightPoint().getX(), upperCommonSupport.getRightPoint().getY());
                line.setStroke(Color.RED);
                line.setStrokeWidth(5);
                pane.getChildren().add(line);
            }

            boolean isInfiniteLeftEnd = false;
            if (currentChainPoint == null) {
                isInfiniteLeftEnd = true;
                Point leftUpperPoint = middlePerpendicular.getLeftPoint();
                assert directionPoint != null;
                if (VectorUtils.dotProduct(VectorUtils.getDirectionPoint(leftUpperPoint, midPoint), directionPoint) > 0) {
                    currentChainPoint = leftUpperPoint;
                } else {
                    currentChainPoint = middlePerpendicular.getRightPoint();
                }
            }

            double leftDistance = 0;
            Point leftPoint = null;
            Edge leftExcludedEdge = getClosestEdge(excludedEdges.get(leftPointOfCommonSupport), middlePerpendicular, currentEdge, currentChainPoint);
            Edge leftEdge = getClosestEdge(new ArrayList<>(List.of(leftCell.getBoundary())), middlePerpendicular, currentEdge, currentChainPoint);
            if (leftEdge != null) {
                leftPoint = getPointOfIntersection(middlePerpendicular, new Line(leftEdge));
                assert leftPoint != null;
                leftDistance = VectorUtils.getLength(leftPoint, currentChainPoint);
            }
            if (leftExcludedEdge != null) {
                Point leftDeletedEdgeIntersectionPoint = getPointOfIntersection(middlePerpendicular, new Line(leftExcludedEdge));
                if (leftDeletedEdgeIntersectionPoint != null) {
                    double leftDeletedIntersectionDistance = VectorUtils.getLength(leftDeletedEdgeIntersectionPoint, currentChainPoint);
                    if (leftDeletedIntersectionDistance < leftDistance || leftEdge == null) {
                        leftDistance = leftDeletedIntersectionDistance;
                        leftPoint = leftDeletedEdgeIntersectionPoint;
                        leftEdge = leftExcludedEdge;
                    }
                }
            }

            double rightDistance = 0;
            Point rightPoint = null;
            Edge rightExcludedEdge = getClosestEdge(excludedEdges.get(rightPointOfCommonSupport), middlePerpendicular, currentEdge, currentChainPoint);
            Edge rightEdge = getClosestEdge(new ArrayList<>(List.of(rightCell.getBoundary())), middlePerpendicular, currentEdge, currentChainPoint);
            if (rightEdge != null) {
                rightPoint = getPointOfIntersection(middlePerpendicular, new Line(rightEdge));
                assert rightPoint != null;
                rightDistance = VectorUtils.getLength(rightPoint, currentChainPoint);
            }
            if (rightExcludedEdge != null) {
                Point rightDeletedEdgeIntersectionPoint = getPointOfIntersection(middlePerpendicular, new Line(rightExcludedEdge));
                if (rightDeletedEdgeIntersectionPoint != null) {
                    double rightDeletedIntersectionDistance = VectorUtils.getLength(rightDeletedEdgeIntersectionPoint, currentChainPoint);
                    if (rightDeletedIntersectionDistance < rightDistance || rightEdge == null) {
                        rightDistance = rightDeletedIntersectionDistance;
                        rightPoint = rightDeletedEdgeIntersectionPoint;
                        rightEdge = rightExcludedEdge;
                    }
                }
            }

            if (rightEdge == null && leftEdge == null) {
                System.out.println("2");
                break;
            } else if ((leftEdge != null && rightEdge == null) || (leftEdge != null && leftDistance < rightDistance)) {
                Edge leftTwinEdge = null;
                Line leftLine = new Line(leftEdge);
                Point leftCenter = leftCell.getCenter();
                if (isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getLeftPoint()) && isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getRightPoint())) {
                    if (VectorUtils.getLength(leftEdge.getLeftPoint(), leftPoint) > VectorUtils.getLength(leftEdge.getRightPoint(), leftPoint)) {
                        leftTwinEdge = leftEdge.getTwin();
                        Cell leftTwinCell = leftTwinEdge.getCell();

                        Edge erasedEdge = eraseEdges(leftTwinEdge, leftTwinEdge.getRightPoint());
                        leftTwinEdge.setRightPoint(leftPoint);
                        leftTwinEdge.setInfiniteRightEnd(false);
                        Point leftTwinCenter = leftTwinCell.getCenter();
                        List<Edge> leftTwinExcludedEdges = excludedEdges.get(leftTwinCenter);
                        if (leftTwinExcludedEdges == null || leftTwinExcludedEdges.isEmpty()) {
                            leftTwinCell.setBoundary(leftTwinEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(leftTwinCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                        }

                        erasedEdge = eraseEdges(leftEdge, leftEdge.getRightPoint());
                        leftEdge.setRightPoint(leftPoint);
                        leftEdge.setInfiniteRightEnd(false);
                        List<Edge> leftExcludedEdges = excludedEdges.get(leftCenter);
                        if (leftExcludedEdges == null || leftExcludedEdges.isEmpty()) {
                            leftCell.setBoundary(leftEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(leftCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                        }
                    } else if (VectorUtils.getLength(leftEdge.getLeftPoint(), leftPoint) < VectorUtils.getLength(leftEdge.getRightPoint(), leftPoint)) {
                        leftTwinEdge = leftEdge.getTwin();
                        Cell leftTwinCell = leftTwinEdge.getCell();

                        Edge erasedEdge = eraseEdges(leftTwinEdge, leftTwinEdge.getLeftPoint());
                        leftTwinEdge.setLeftPoint(leftPoint);
                        leftTwinEdge.setInfiniteLeftEnd(false);
                        Point leftTwinCenter = leftTwinCell.getCenter();
                        List<Edge> leftTwinExcludedEdges = excludedEdges.get(leftTwinCenter);
                        if (leftTwinExcludedEdges == null || leftTwinExcludedEdges.isEmpty()) {
                            leftTwinCell.setBoundary(leftTwinEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(leftTwinCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                        }

                        erasedEdge = eraseEdges(leftEdge, leftEdge.getLeftPoint());
                        leftEdge.setLeftPoint(leftPoint);
                        leftEdge.setInfiniteLeftEnd(false);
                        List<Edge> leftExcludedEdges = excludedEdges.get(leftCenter);
                        if (leftExcludedEdges == null || leftExcludedEdges.isEmpty()) {
                            leftCell.setBoundary(leftEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(leftCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                        }
                    }
                } else if (isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getLeftPoint())) {
                    leftTwinEdge = leftEdge.getTwin();
                    Cell leftTwinCell = leftTwinEdge.getCell();

                    Edge erasedEdge = eraseEdges(leftTwinEdge, leftTwinEdge.getRightPoint());
                    leftTwinEdge.setRightPoint(leftPoint);
                    leftTwinEdge.setInfiniteRightEnd(false);
                    Point leftTwinCenter = leftTwinCell.getCenter();
                    List<Edge> leftTwinExcludedEdges = excludedEdges.get(leftTwinCenter);
                    if (leftTwinExcludedEdges == null || leftTwinExcludedEdges.isEmpty()) {
                        leftTwinCell.setBoundary(leftTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftTwinCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                    }

                    erasedEdge = eraseEdges(leftEdge, leftEdge.getRightPoint());
                    leftEdge.setRightPoint(leftPoint);
                    leftEdge.setInfiniteRightEnd(false);
                    List<Edge> leftExcludedEdges = excludedEdges.get(leftCenter);
                    if (leftExcludedEdges == null || leftExcludedEdges.isEmpty()) {
                        leftCell.setBoundary(leftEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                    }
                } else if (isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getRightPoint())) {
                    leftTwinEdge = leftEdge.getTwin();
                    Cell leftTwinCell = leftTwinEdge.getCell();

                    Edge erasedEdge = eraseEdges(leftTwinEdge, leftTwinEdge.getLeftPoint());
                    leftTwinEdge.setLeftPoint(leftPoint);
                    leftTwinEdge.setInfiniteLeftEnd(false);
                    Point leftTwinCenter = leftTwinCell.getCenter();
                    List<Edge> leftTwinExcludedEdges = excludedEdges.get(leftTwinCenter);
                    if (leftTwinExcludedEdges == null || leftTwinExcludedEdges.isEmpty()) {
                        leftTwinCell.setBoundary(leftTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftTwinCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                    }

                    erasedEdge = eraseEdges(leftEdge, leftEdge.getLeftPoint());
                    leftEdge.setLeftPoint(leftPoint);
                    leftEdge.setInfiniteLeftEnd(false);
                    List<Edge> leftExcludedEdges = excludedEdges.get(leftCenter);
                    if (leftExcludedEdges == null || leftExcludedEdges.isEmpty()) {
                        leftCell.setBoundary(leftEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                    }
                }
                assert leftTwinEdge != null;
                Edge nextLeftEdge = new Edge(currentChainPoint, leftPoint, leftCell);
                nextLeftEdge.setInfiniteLeftEnd(isInfiniteLeftEnd);
                nextLeftEdge.setInfiniteRightEnd(false);

                List<Edge> leftChain = disjunctiveChain.get(leftCell);
                if (leftChain == null || leftChain.isEmpty()) {
                    //nextLeftEdge.setNext(leftEdge);
                    disjunctiveChain.put(leftCell, new ArrayList<>(List.of(nextLeftEdge)));
                } else {
                    Edge firstEdge = leftChain.stream().findFirst().get();

                    Edge startEdge = firstEdge.getStartEdge();
                    if (isConnected(startEdge, nextLeftEdge)) {
                        nextLeftEdge.setPrev(startEdge);
                        startEdge.setNext(nextLeftEdge);
                        //nextLeftEdge.setNext(leftEdge);
                    } else {
                        Edge lastEdge = firstEdge.getLastEdge();
                        if (isConnected(lastEdge, nextLeftEdge)) {
                            nextLeftEdge.setPrev(lastEdge);
                            lastEdge.setNext(nextLeftEdge);
                            //nextLeftEdge.setNext(leftEdge);
                        } else {
                            log.error("can't be connected");
                        }
                    }
                }

                Edge nextRightEdge = new Edge(currentChainPoint, leftPoint, rightCell);
                nextRightEdge.setInfiniteLeftEnd(isInfiniteLeftEnd);
                nextRightEdge.setInfiniteRightEnd(false);
                nextRightEdge.setTwin(nextLeftEdge);
                List<Edge> rightChain = disjunctiveChain.get(rightCell);
                if (rightChain == null || rightChain.isEmpty()) {
                    disjunctiveChain.put(rightCell, new ArrayList<>(List.of(nextRightEdge)));
                } else {
                    Edge firstEdge = rightChain.stream().findFirst().get();

                    Edge startEdge = firstEdge.getStartEdge();
                    if (isConnected(startEdge, nextRightEdge)) {
                        nextRightEdge.setPrev(startEdge);
                        startEdge.setNext(nextRightEdge);
                    } else {
                        Edge lastEdge = firstEdge.getLastEdge();
                        if (isConnected(lastEdge, nextRightEdge)) {
                            nextRightEdge.setPrev(lastEdge);
                            lastEdge.setNext(nextRightEdge);
                        } else {
                            log.error("can't be connected");
                        }
                    }
                }

                upperCommonSupport.setLeftPoint(leftTwinEdge.getCell().getCenter());
                nextLeftEdge.setTwin(nextRightEdge);
                currentChainPoint = leftPoint;
                currentEdge = leftEdge;
            } else if (leftEdge == null || leftDistance >= rightDistance) {
                Edge rightTwinEdge = null;
                Line rightLine = new Line(rightEdge);
                Point rightCenter = rightCell.getCenter();
                if (isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getLeftPoint()) && isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getRightPoint())) {
                    if (VectorUtils.getLength(rightEdge.getLeftPoint(), rightPoint) > VectorUtils.getLength(rightEdge.getRightPoint(), rightPoint)) {
                        rightTwinEdge = rightEdge.getTwin();
                        Cell rightTwinCell = rightTwinEdge.getCell();

                        Edge erasedEdge = eraseEdges(rightTwinEdge, rightTwinEdge.getRightPoint());
                        rightTwinEdge.setRightPoint(rightPoint);
                        rightTwinEdge.setInfiniteRightEnd(false);
                        Point rightTwinCenter = rightTwinCell.getCenter();
                        List<Edge> rightTwinExcludedEdges = excludedEdges.get(rightTwinCenter);
                        if (rightTwinExcludedEdges == null || rightTwinExcludedEdges.isEmpty()) {
                            rightTwinCell.setBoundary(rightTwinEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(rightTwinCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                        }

                        erasedEdge = eraseEdges(rightEdge, rightEdge.getRightPoint());
                        rightEdge.setRightPoint(rightPoint);
                        rightEdge.setInfiniteRightEnd(false);
                        List<Edge> rightExcludedEdges = excludedEdges.get(rightCenter);
                        if (rightExcludedEdges == null || rightExcludedEdges.isEmpty()) {
                            rightCell.setBoundary(rightEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(rightCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                        }
                    } else if (VectorUtils.getLength(rightEdge.getLeftPoint(), rightPoint) < VectorUtils.getLength(rightEdge.getRightPoint(), rightPoint)) {
                        rightTwinEdge = rightEdge.getTwin();
                        Cell rightTwinCell = rightTwinEdge.getCell();

                        Edge erasedEdge = eraseEdges(rightTwinEdge, rightTwinEdge.getLeftPoint());
                        rightTwinEdge.setLeftPoint(rightPoint);
                        rightTwinEdge.setInfiniteLeftEnd(false);
                        Point rightTwinCenter = rightTwinCell.getCenter();
                        List<Edge> rightTwinExcludedEdges = excludedEdges.get(rightTwinCenter);
                        if (rightTwinExcludedEdges == null || rightTwinExcludedEdges.isEmpty()) {
                            rightTwinCell.setBoundary(rightTwinEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(rightTwinCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                        }

                        erasedEdge = eraseEdges(rightEdge, rightEdge.getLeftPoint());
                        rightEdge.setLeftPoint(rightPoint);
                        rightEdge.setInfiniteLeftEnd(false);
                        List<Edge> rightExcludedEdges = excludedEdges.get(rightCenter);
                        if (rightExcludedEdges == null || rightExcludedEdges.isEmpty()) {
                            rightCell.setBoundary(rightEdge);
                        }
                        if (erasedEdge != null) {
                            excludedEdges.computeIfAbsent(rightCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                        }
                    }
                } else if (isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getLeftPoint())) {
                    rightTwinEdge = rightEdge.getTwin();
                    Cell rightTwinCell = rightTwinEdge.getCell();

                    Edge erasedEdge = eraseEdges(rightTwinEdge, rightTwinEdge.getRightPoint());
                    rightTwinEdge.setRightPoint(rightPoint);
                    rightTwinEdge.setInfiniteRightEnd(false);
                    Point rightTwinCenter = rightTwinCell.getCenter();
                    List<Edge> rightTwinExcludedEdges = excludedEdges.get(rightTwinCenter);
                    if (rightTwinExcludedEdges == null || rightTwinExcludedEdges.isEmpty()) {
                        rightTwinCell.setBoundary(rightTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightTwinCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                    }

                    erasedEdge = eraseEdges(rightEdge, rightEdge.getRightPoint());
                    rightEdge.setRightPoint(rightPoint);
                    rightEdge.setInfiniteRightEnd(false);
                    List<Edge> rightExcludedEdges = excludedEdges.get(rightCenter);
                    if (rightExcludedEdges == null || rightExcludedEdges.isEmpty()) {
                        rightCell.setBoundary(rightEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                    }
                } else if (isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getRightPoint())) {
                    rightTwinEdge = rightEdge.getTwin();
                    Cell rightTwinCell = rightTwinEdge.getCell();

                    Edge erasedEdge = eraseEdges(rightTwinEdge, rightTwinEdge.getLeftPoint());
                    rightTwinEdge.setLeftPoint(rightPoint);
                    rightTwinEdge.setInfiniteLeftEnd(false);
                    Point rightTwinCenter = rightTwinCell.getCenter();
                    List<Edge> rightTwinExcludedEdges = excludedEdges.get(rightTwinCenter);
                    if (rightTwinExcludedEdges == null || rightTwinExcludedEdges.isEmpty()) {
                        rightTwinCell.setBoundary(rightTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightTwinCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                    }

                    erasedEdge = eraseEdges(rightEdge, rightEdge.getLeftPoint());
                    rightEdge.setLeftPoint(rightPoint);
                    rightEdge.setInfiniteLeftEnd(false);
                    List<Edge> rightExcludedEdges = excludedEdges.get(rightCenter);
                    if (rightExcludedEdges == null || rightExcludedEdges.isEmpty()) {
                        rightCell.setBoundary(rightEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightCell.getCenter(), k -> new ArrayList<>()).add(erasedEdge);
                    }
                }
                assert rightTwinEdge != null;
                Edge nextRightEdge = new Edge(currentChainPoint, rightPoint, rightCell);
                nextRightEdge.setInfiniteLeftEnd(isInfiniteLeftEnd);
                nextRightEdge.setInfiniteRightEnd(false);

                List<Edge> rightChain = disjunctiveChain.get(rightCell);
                if (rightChain == null || rightChain.isEmpty()) {
                    //nextRightEdge.setNext(rightEdge);
                    disjunctiveChain.put(rightCell, new ArrayList<>(List.of(nextRightEdge)));
                } else {
                    Edge firstEdge = rightChain.stream().findFirst().get();

                    Edge startEdge = firstEdge.getStartEdge();
                    if (isConnected(startEdge, nextRightEdge)) {
                        nextRightEdge.setPrev(startEdge);
                        startEdge.setNext(nextRightEdge);
                        //nextRightEdge.setNext(rightEdge);
                    } else {
                        Edge lastEdge = firstEdge.getLastEdge();
                        if (isConnected(lastEdge, nextRightEdge)) {
                            nextRightEdge.setPrev(lastEdge);
                            lastEdge.setNext(nextRightEdge);
                        } else {
                            log.error("can't be connected");
                        }
                    }
                }

                Edge nextLeftEdge = new Edge(currentChainPoint, rightPoint, leftCell);
                nextLeftEdge.setInfiniteLeftEnd(isInfiniteLeftEnd);
                nextLeftEdge.setInfiniteRightEnd(false);
                nextLeftEdge.setTwin(nextRightEdge);
                List<Edge> leftChain = disjunctiveChain.get(leftCell);
                if (leftChain == null || leftChain.isEmpty()) {
                    disjunctiveChain.put(leftCell, new ArrayList<>(List.of(nextLeftEdge)));
                } else {
                    Edge firstEdge = leftChain.stream().findFirst().get();

                    Edge startEdge = firstEdge.getStartEdge();
                    if (isConnected(startEdge, nextLeftEdge)) {
                        nextLeftEdge.setPrev(startEdge);
                        startEdge.setNext(nextLeftEdge);
                    } else {
                        Edge lastEdge = firstEdge.getLastEdge();
                        if (isConnected(lastEdge, nextLeftEdge)) {
                            nextLeftEdge.setPrev(lastEdge);
                            lastEdge.setNext(nextLeftEdge);
                        } else {
                            log.error("can't be connected");
                        }
                    }
                }

                upperCommonSupport.setRightPoint(rightTwinEdge.getCell().getCenter());
                nextRightEdge.setTwin(nextLeftEdge);
                currentChainPoint = rightPoint;
                currentEdge = rightEdge;
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

        List<Edge> leftChain = disjunctiveChain.get(leftEdge.getCell());
        if (leftChain == null || leftChain.isEmpty()) {
            disjunctiveChain.put(leftEdge.getCell(), new ArrayList<>(List.of(leftEdge)));
        } else {
            boolean isConnected = false;
            Edge lastLeftEdge = leftChain.stream().findFirst().get();
            Edge startEdge = lastLeftEdge.getStartEdge();
            if (isConnected(startEdge, leftEdge)) {
                startEdge.setNext(leftEdge);
                leftEdge.setPrev(startEdge);
                isConnected = true;
            } else {
                Edge lastEdge = lastLeftEdge.getLastEdge();
                if (isConnected(lastEdge, leftEdge)) {
                    lastEdge.setNext(leftEdge);
                    leftEdge.setPrev(lastEdge);
                    isConnected = true;
                }
            }
            if (!isConnected) {
                leftChain.add(leftEdge);
            }
        }

        List<Edge> rightChain = disjunctiveChain.get(rightEdge.getCell());
        if (rightChain == null || rightChain.isEmpty()) {
            disjunctiveChain.put(rightEdge.getCell(), new ArrayList<>(List.of(rightEdge)));
        } else {
            boolean isConnected = false;
            Edge lastRightEdge = rightChain.stream().findFirst().get();
            Edge startEdge = lastRightEdge.getStartEdge();
            if (isConnected(startEdge, rightEdge)) {
                startEdge.setNext(rightEdge);
                rightEdge.setPrev(startEdge);
                isConnected = true;
            } else {
                Edge lastEdge = lastRightEdge.getLastEdge();
                if (isConnected(lastEdge, rightEdge)) {
                    lastEdge.setNext(rightEdge);
                    rightEdge.setPrev(lastEdge);
                    isConnected = true;
                }
            }
            if (!isConnected) {
                rightChain.add(rightEdge);
            }
        }

        disjunctiveChain.forEach((cell, edges) -> {
            edges.forEach(edge -> {

                Edge firstChainEdge = edge.getStartEdge();
                Edge firstLeftEdge = cell.getConnectedEdge(firstChainEdge.getLeftPoint());
                Edge fistRightEdge = cell.getConnectedEdge(firstChainEdge.getRightPoint());

                Edge lastChainEdge = edge.getLastEdge();
                Edge lastRightEdge = cell.getConnectedEdge(lastChainEdge.getRightPoint());
                if (lastRightEdge != null && ((firstLeftEdge != null && Objects.equals(new Line(firstLeftEdge), new Line(lastRightEdge))) || (fistRightEdge != null && Objects.equals(new Line(fistRightEdge), new Line(lastRightEdge))))) {
                    lastRightEdge = null;
                }

                Edge lastLeftEdge = cell.getConnectedEdge(lastChainEdge.getLeftPoint());
                if (lastLeftEdge != null && ((firstLeftEdge != null && Objects.equals(new Line(firstLeftEdge), new Line(lastLeftEdge))) || (fistRightEdge != null && Objects.equals(new Line(fistRightEdge), new Line(lastLeftEdge))))) {
                    lastLeftEdge = null;
                }

                if (firstLeftEdge != null) {
                    if (firstLeftEdge.getNext() == null) {
                        firstLeftEdge.setNext(firstChainEdge);
                        if (firstChainEdge.getPrev() == null) {
                            firstChainEdge.setPrev(firstLeftEdge);
                        }
                    } else if (firstLeftEdge.getPrev() == null) {
                        firstLeftEdge.setPrev(firstChainEdge);
                        if (firstChainEdge.getNext() == null) {
                            firstChainEdge.setNext(firstLeftEdge);
                        }
                    }
                } else if (fistRightEdge != null) {
                    if (fistRightEdge.getNext() == null) {
                        fistRightEdge.setNext(firstChainEdge);
                        if (firstChainEdge.getPrev() == null) {
                            firstChainEdge.setPrev(fistRightEdge);
                        }
                    } else if (fistRightEdge.getPrev() == null) {
                        fistRightEdge.setPrev(firstChainEdge);
                        if (firstChainEdge.getNext() == null) {
                            firstChainEdge.setNext(fistRightEdge);
                        }
                    }
                }

                if (lastRightEdge != null) {
                    if (lastRightEdge.getPrev() == null) {
                        lastRightEdge.setPrev(lastChainEdge);
                        if (lastChainEdge.getNext() == null) {
                            lastChainEdge.setNext(lastRightEdge);
                        }
                    } else if (lastRightEdge.getNext() == null) {
                        lastRightEdge.setNext(lastChainEdge);
                        if (lastChainEdge.getPrev() == null) {
                            lastChainEdge.setPrev(lastRightEdge);
                        }
                    }
                } else if (lastLeftEdge != null) {
                    if (lastLeftEdge.getPrev() == null) {
                        lastLeftEdge.setPrev(lastChainEdge);
                        if (lastChainEdge.getNext() == null) {
                            lastChainEdge.setNext(lastLeftEdge);
                        }
                    } else if (lastLeftEdge.getNext() == null) {
                        lastLeftEdge.setNext(lastChainEdge);
                        if (lastChainEdge.getPrev() == null) {
                            lastChainEdge.setPrev(lastLeftEdge);
                        }
                    }
                }
            });
        });

        Map<Point, Cell> diagram = new HashMap<>();
        diagram.putAll(leftDiagram);
        diagram.putAll(rightDiagram);

        return diagram;
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

    private Edge getClosestEdge(List<Edge> edges, Line middlePerpendicular, Edge currentEdge, Point currentChainPoint) {
        if (edges == null || edges.isEmpty()) {
            return null;
        }

        Edge intersectedEdge = null;
        for (Edge edge : edges) {
            Edge nextEdge = edge;
            double distance = -1;
            do {
                if (currentEdge == null || !Objects.equals(new Line(currentEdge), new Line(nextEdge))) {
                    Point intersectPoint = getPointOfIntersection(middlePerpendicular, new Line(nextEdge));
                    if (intersectPoint != null && isIntersected(intersectPoint, new Line(nextEdge)) && isOutsideCell(currentEdge, currentChainPoint, intersectPoint)) {
                        double currentDistance = VectorUtils.getLength(intersectPoint, middlePerpendicular.getRightPoint());
                        if (distance == -1 || currentDistance < distance) {
                            distance = currentDistance;
                            intersectedEdge = nextEdge;
                        }
                    }
                }
                nextEdge = nextEdge.getNext();
            } while (nextEdge != null && !Objects.equals(new Line(edge), new Line(nextEdge)));

            Edge prevEdge = edge;
            do {
                if (currentEdge == null || !Objects.equals(new Line(currentEdge), new Line(prevEdge))) {
                    Point intersectPoint = getPointOfIntersection(middlePerpendicular, new Line(prevEdge));
                    if (intersectPoint != null && isIntersected(intersectPoint, new Line(prevEdge)) && isOutsideCell(currentEdge, currentChainPoint, intersectPoint)) {
                        double currentDistance = VectorUtils.getLength(intersectPoint, middlePerpendicular.getRightPoint());
                        if (distance == -1 || currentDistance < distance) {
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

        Point point = line.getMidPoint();
        double x = point.getX();
        double y = point.getY();

        Point directionPoint = VectorUtils.getDirectionPoint(line.getLeftPoint(), line.getRightPoint());
        if (VectorUtils.dotProduct(directionPoint, new Point(1, 0)) == 0) {
            return new Line(new Point(x, y), new Point(0, y));
        } else if (VectorUtils.dotProduct(directionPoint, new Point(0, 1)) == 0) {
            return new Line(new Point(x, y), new Point(x, 0));
        } else {
            if (directionPoint.getX() == 0) {
                return new Line(new Point(x, -height), new Point(x, height));
            }
            return new Line(new Point((y * directionPoint.getY()) / directionPoint.getX() + x, 0), new Point((-(height - y) * directionPoint.getY()) / directionPoint.getX() + x, height));
        }
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

    private boolean isOnTheSameSide(Line line, Point firstPoint, Point secondPoint) {
        return (isPointLower(line, firstPoint) && isPointLower(line, secondPoint)) || (isPointUpper(line, firstPoint) && isPointUpper(line, secondPoint));
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