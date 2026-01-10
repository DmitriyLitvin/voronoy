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
import org.example.utils.PointUtils;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.*;
import static org.example.entity.CommonSupportType.LOWER;
import static org.example.entity.CommonSupportType.UPPER;
import static org.example.utils.PointUtils.crossProduct;

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

//        points.add(new Point(383.0, 859.0));
//        points.add(new Point(642.0, 386.0));
//        points.add(new Point(691.0, 237.0));
//        points.add(new Point(684.0, 313.0));
//        points.add(new Point(673.0, 456.0));
//        points.add(new Point(506.0, 632.0));
//        points.add(new Point(638.0, 324.0));
//        points.add(new Point(692.0, 270.0));

        points.add(new Point(711.0, 216.0));
        points.add(new Point(714.0, 506.0));
        points.add(new Point(720.0, 252.0));
        points.add(new Point(725.0, 376.0));
        points.add(new Point(733.0, 361.0));
        points.add(new Point(759.0, 576.0));
        points.add(new Point(778.0, 773.0));
        points.add(new Point(822.0, 395.0));


//        points.add(new Point(383.0, 859.0));
//        points.add(new Point(642.0, 386.0));
//        points.add(new Point(691.0, 237.0));
//        points.add(new Point(684.0, 313.0));
//        points.add(new Point(822.0, 395.0));
//        points.add(new Point(733.0, 361.0));
//        points.add(new Point(759.0, 576.0));
//        points.add(new Point(714.0, 506.0));


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
        buildVoronoyDiagram(polygon.stream().sorted(Comparator.comparingDouble(Point::getX)).toList()).values().forEach(voronoyCell -> {
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

    private Set<Point> getIncidentCellCenters(Cell cell) {
        Set<Point> incidentCellCenters = new HashSet<>();
        incidentCellCenters.add(cell.getCenter());

        Edge edge = cell.getBoundary();
        Edge nextEdge = cell.getBoundary();
        do {
            incidentCellCenters.add(nextEdge.getTwin().getCell().getCenter());
            nextEdge = nextEdge.getNext();
        } while (nextEdge != null && !Objects.equals(new Line(edge), new Line(nextEdge)));

        Edge prevEdge = cell.getBoundary();
        do {
            incidentCellCenters.add(prevEdge.getTwin().getCell().getCenter());
            prevEdge = prevEdge.getPrev();
        } while (prevEdge != null && !Objects.equals(new Line(edge), new Line(prevEdge)));

        return incidentCellCenters;
    }

    private Line dominanceCheck(Line upperCommonSupport, Line lowerCommonSupport, Map<Point, Cell> leftDiagram, Map<Point, Cell> rightDiagram) {
        List<Point> leftIncidentCellCenters = new ArrayList<>(getIncidentCellCenters(leftDiagram.get(upperCommonSupport.getLeftPoint())));
        List<Point> rightIncidentCellCenters = new ArrayList<>(getIncidentCellCenters(rightDiagram.get(upperCommonSupport.getRightPoint())));

        AtomicReference<Point> directionPoint = new AtomicReference<>();
        Line currentCommonSupport = upperCommonSupport.deepCopy();
        Line lowerPerpendicular = getMiddlePerpendicular(lowerCommonSupport);
        while (true) {
            Point midPoint = currentCommonSupport.getMidPoint();
            Line upperPerpendicular = getMiddlePerpendicular(currentCommonSupport);
            Point currentPoint = getPointOfIntersection(currentCommonSupport, lowerCommonSupport);
            if (currentPoint != null) {
                Point intersectPoint = getPointOfIntersection(upperPerpendicular, lowerPerpendicular);
                assert intersectPoint != null;
                if (isPointInsideAngle(currentCommonSupport.getLeftPoint(), currentPoint, lowerCommonSupport.getLeftPoint(), intersectPoint)) {
                    directionPoint.set(new Point(intersectPoint.getX() - midPoint.getX(), intersectPoint.getY() - midPoint.getY()));
                } else {
                    Point lowerPoint = getPointOfIntersection(upperPerpendicular, lowerCommonSupport);
                    if (lowerPoint != null && isIntersected(lowerPoint, new Line(midPoint, intersectPoint))) {
                        directionPoint.set(new Point(intersectPoint.getX() - midPoint.getX(), intersectPoint.getY() - midPoint.getY()));
                    } else {
                        directionPoint.set(new Point(midPoint.getX() - intersectPoint.getX(), midPoint.getY() - intersectPoint.getY()));
                    }
                }
            } else {
                Point lowerPoint = getPointOfIntersection(upperPerpendicular, lowerCommonSupport);
                assert lowerPoint != null;
                directionPoint.set(new Point(lowerPoint.getX() - midPoint.getX(), lowerPoint.getY() - midPoint.getY()));
            }

            Set<Line> leftLines = new HashSet<>();
            for (Point leftIncidentCellCenter : leftIncidentCellCenters) {
                if (!Objects.equals(leftIncidentCellCenter, currentCommonSupport.getLeftPoint()) && PointUtils.dotProduct(new Point(leftIncidentCellCenter.getX() - midPoint.getX(), leftIncidentCellCenter.getY() - midPoint.getY()), directionPoint.get()) > 0) {
                    Cell leftCell = leftDiagram.get(leftIncidentCellCenter);

                    Edge edge = leftCell.getBoundary();
                    Edge nextEdge = leftCell.getBoundary();
                    do {
                        if (Objects.equals(currentCommonSupport.getLeftPoint(), nextEdge.getTwin().getCell().getCenter())) {
                            Line leftLine = new Line(currentCommonSupport.getLeftPoint(), leftIncidentCellCenter);
                            Line nextLine = new Line(nextEdge);
                            Point anglePoint = getPointOfIntersection(nextLine, upperPerpendicular);
                            if (isIntersected(getPointOfIntersection(leftLine, upperPerpendicular), leftLine) && isIntersected(getPointOfIntersection(leftLine, nextLine), nextLine) && (!isPointInsideAngle(leftLine.getMidPoint(), anglePoint, midPoint, currentCommonSupport.getLeftPoint()) && !isPointInsideAngle(leftLine.getMidPoint(), anglePoint, midPoint, currentCommonSupport.getRightPoint()))) {
                                leftLines.add(leftLine);
                            }
                        }
                        nextEdge = nextEdge.getNext();
                    } while (nextEdge != null && !Objects.equals(new Line(edge), new Line(nextEdge)));

                    Edge prevEdge = leftCell.getBoundary();
                    do {
                        if (Objects.equals(currentCommonSupport.getLeftPoint(), prevEdge.getTwin().getCell().getCenter())) {
                            Line leftLine = new Line(currentCommonSupport.getLeftPoint(), leftIncidentCellCenter);
                            Line prevLine = new Line(prevEdge);
                            Point anglePoint = getPointOfIntersection(prevLine, upperPerpendicular);
                            if (isIntersected(getPointOfIntersection(leftLine, upperPerpendicular), leftLine) && isIntersected(getPointOfIntersection(leftLine, prevLine), prevLine) && (!isPointInsideAngle(leftLine.getMidPoint(), anglePoint, midPoint, currentCommonSupport.getLeftPoint()) && !isPointInsideAngle(leftLine.getMidPoint(), anglePoint, midPoint, currentCommonSupport.getRightPoint()))) {
                                leftLines.add(leftLine);
                            }
                        }
                        prevEdge = prevEdge.getPrev();
                    } while (prevEdge != null && !Objects.equals(new Line(edge), new Line(prevEdge)));
                }
            }

            Set<Line> rightLines = new HashSet<>();
            for (Point rightIncidentCellCenter : rightIncidentCellCenters) {
                if (!Objects.equals(rightIncidentCellCenter, currentCommonSupport.getRightPoint()) && PointUtils.dotProduct(new Point(rightIncidentCellCenter.getX() - midPoint.getX(), rightIncidentCellCenter.getY() - midPoint.getY()), directionPoint.get()) > 0) {
                    Cell rightCell = rightDiagram.get(rightIncidentCellCenter);

                    Edge edge = rightCell.getBoundary();
                    Edge nextEdge = rightCell.getBoundary();
                    do {
                        if (Objects.equals(currentCommonSupport.getRightPoint(), nextEdge.getTwin().getCell().getCenter())) {
                            Line rightLine = new Line(currentCommonSupport.getRightPoint(), rightIncidentCellCenter);
                            Line nextLine = new Line(nextEdge);
                            Point anglePoint = getPointOfIntersection(nextLine, upperPerpendicular);
                            if (isIntersected(getPointOfIntersection(rightLine, upperPerpendicular), rightLine) && isIntersected(getPointOfIntersection(rightLine, nextLine), nextLine) && (!isPointInsideAngle(rightLine.getMidPoint(), anglePoint, midPoint, currentCommonSupport.getLeftPoint()) && !isPointInsideAngle(rightLine.getMidPoint(), anglePoint, midPoint, currentCommonSupport.getRightPoint()))) {
                                rightLines.add(rightLine);
                            }
                        }
                        nextEdge = nextEdge.getNext();
                    } while (nextEdge != null && !Objects.equals(new Line(edge), new Line(nextEdge)));

                    Edge prevEdge = rightCell.getBoundary();
                    do {
                        if (Objects.equals(currentCommonSupport.getRightPoint(), prevEdge.getTwin().getCell().getCenter())) {
                            Line rightLine = new Line(currentCommonSupport.getRightPoint(), rightIncidentCellCenter);
                            Line prevLine = new Line(prevEdge);
                            Point anglePoint = getPointOfIntersection(prevLine, upperPerpendicular);
                            if (isIntersected(getPointOfIntersection(rightLine, upperPerpendicular), rightLine) && isIntersected(getPointOfIntersection(rightLine, prevLine), prevLine) && (!isPointInsideAngle(rightLine.getMidPoint(), anglePoint, midPoint, currentCommonSupport.getLeftPoint()) && !isPointInsideAngle(rightLine.getMidPoint(), anglePoint, midPoint, currentCommonSupport.getRightPoint()))) {
                                rightLines.add(rightLine);
                            }
                        }
                        prevEdge = prevEdge.getPrev();
                    } while (prevEdge != null && !Objects.equals(new Line(edge), new Line(prevEdge)));
                }
            }

            if (leftLines.isEmpty() && rightLines.isEmpty()) {
                return currentCommonSupport;
            }

            Line preSupportLine = currentCommonSupport.deepCopy();
            if (leftLines.isEmpty()) {
                Point rightPoint = currentCommonSupport.getRightPoint();
                rightIncidentCellCenters.stream().filter(p -> Objects.equals(p, rightPoint) || PointUtils.dotProduct(new Point(p.getX() - midPoint.getX(), p.getY() - midPoint.getY()), directionPoint.get()) > 0).map(p -> new AbstractMap.SimpleEntry<>(p, PointUtils.getLength(midPoint, p))).min(Comparator.comparingDouble(Map.Entry::getValue)).ifPresent(p -> preSupportLine.setRightPoint(p.getKey()));
            } else if (rightLines.isEmpty()) {
                Point leftPoint = currentCommonSupport.getLeftPoint();
                leftIncidentCellCenters.stream().filter(p -> Objects.equals(p, leftPoint) || PointUtils.dotProduct(new Point(p.getX() - midPoint.getX(), p.getY() - midPoint.getY()), directionPoint.get()) > 0).map(p -> new AbstractMap.SimpleEntry<>(p, PointUtils.getLength(midPoint, p))).min(Comparator.comparingDouble(Map.Entry::getValue)).ifPresent(p -> preSupportLine.setLeftPoint(p.getKey()));

            } else {
                throw new RuntimeException("right and left lines are intersected, by a current middle perpendicular");
            }

            if (Objects.equals(preSupportLine, currentCommonSupport)) {
                return currentCommonSupport;
            } else {
                currentCommonSupport = preSupportLine;
            }
        }
    }

    private Map<Point, Cell> joinDiagrams(Map<Point, Cell> leftDiagram, Map<Point, Cell> rightDiagram) {
        Set<Point> leftPolygon = buildConvexHull(new ArrayList<>(leftDiagram.keySet()));
        Set<Point> rightPolygon = buildConvexHull(new ArrayList<>(rightDiagram.keySet()));

        Line upperCommonSupport = getCommonSupport(leftPolygon, rightPolygon, UPPER);
        Line lowerCommonSupport = getCommonSupport(leftPolygon, rightPolygon, LOWER);

        Point currentChainPoint = null;
        Edge currentEdge = null;
        Line middlePerpendicular;
        Map<Point, Edge> excludedEdges = new HashMap<>();
        Map<Cell, List<Edge>> disjunctiveChain = new HashMap<>();
        while (!Objects.equals(upperCommonSupport, lowerCommonSupport)) {
            upperCommonSupport = dominanceCheck(upperCommonSupport, lowerCommonSupport, leftDiagram, rightDiagram);


            if (leftDiagram.size() == 4) {
                javafx.scene.shape.Line line = new javafx.scene.shape.Line(upperCommonSupport.getLeftPoint().getX(), upperCommonSupport.getLeftPoint().getY(), upperCommonSupport.getRightPoint().getX(), upperCommonSupport.getRightPoint().getY());
                line.setStroke(Color.RED);
                line.setStrokeWidth(5);
                pane.getChildren().add(line);
            }


            Point leftPointOfCommonSupport = upperCommonSupport.getLeftPoint();
            Cell leftCell = leftDiagram.get(leftPointOfCommonSupport);
            Point rightPointOfCommonSupport = upperCommonSupport.getRightPoint();
            Cell rightCell = rightDiagram.get(rightPointOfCommonSupport);
            middlePerpendicular = getMiddlePerpendicular(upperCommonSupport);

            double leftDistance = 0;
            Point leftPoint = null;
            Edge leftExcludedEdge = getClosestEdge(excludedEdges.get(leftPointOfCommonSupport), middlePerpendicular, currentEdge, currentChainPoint);
            Edge leftEdge = getClosestEdge(leftCell.getBoundary(), middlePerpendicular, currentEdge, currentChainPoint);
            if (leftEdge != null) {
                leftPoint = getPointOfIntersection(middlePerpendicular, new Line(leftEdge));
                assert leftPoint != null;
                leftDistance = PointUtils.getLength(leftPoint, currentChainPoint == null ? middlePerpendicular.getRightPoint() : currentChainPoint);
            }
            if (leftExcludedEdge != null) {
                Point leftDeletedEdgeIntersectionPoint = getPointOfIntersection(middlePerpendicular, new Line(leftExcludedEdge));
                if (leftDeletedEdgeIntersectionPoint != null) {
                    double leftDeletedIntersectionDistance = PointUtils.getLength(leftDeletedEdgeIntersectionPoint, currentChainPoint == null ? middlePerpendicular.getRightPoint() : currentChainPoint);
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
            Edge rightEdge = getClosestEdge(rightCell.getBoundary(), middlePerpendicular, currentEdge, currentChainPoint);
            if (rightEdge != null) {
                rightPoint = getPointOfIntersection(middlePerpendicular, new Line(rightEdge));
                assert rightPoint != null;
                rightDistance = PointUtils.getLength(rightPoint, currentChainPoint == null ? middlePerpendicular.getRightPoint() : currentChainPoint);
            }
            if (rightExcludedEdge != null) {
                Point rightDeletedEdgeIntersectionPoint = getPointOfIntersection(middlePerpendicular, new Line(rightExcludedEdge));
                if (rightDeletedEdgeIntersectionPoint != null) {
                    double rightDeletedIntersectionDistance = PointUtils.getLength(rightDeletedEdgeIntersectionPoint, currentChainPoint == null ? middlePerpendicular.getRightPoint() : currentChainPoint);
                    if (rightDeletedIntersectionDistance < rightDistance || rightEdge == null) {
                        rightDistance = rightDeletedIntersectionDistance;
                        rightPoint = rightDeletedEdgeIntersectionPoint;
                        rightEdge = rightExcludedEdge;
                    }
                }
            }

            if (rightEdge == null && leftEdge == null) {
                break;
            } else if ((leftEdge != null && rightEdge == null) || (leftEdge != null && leftDistance < rightDistance)) {
                Edge leftTwinEdge = null;
                Line leftLine = new Line(leftEdge);
                if (isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getLeftPoint()) && isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getRightPoint())) {
                    if (PointUtils.getLength(leftEdge.getLeftPoint(), leftPoint) > PointUtils.getLength(leftEdge.getRightPoint(), leftPoint)) {
                        eraseEdges(leftEdge, leftEdge.getRightPoint());
                        leftEdge.setRightPoint(leftPoint);
                        leftEdge.setInfiniteRightEnd(false);
                        leftCell.setBoundary(leftEdge);

                        leftTwinEdge = leftEdge.getTwin();
                        Cell leftTwinCell = leftTwinEdge.getCell();
                        Edge erasedEdge = eraseEdges(leftTwinEdge, leftTwinEdge.getRightPoint());
                        if (erasedEdge != null) {
                            excludedEdges.put(leftTwinCell.getCenter(), erasedEdge);
                        }
                        leftTwinEdge.setRightPoint(leftPoint);
                        leftTwinEdge.setInfiniteRightEnd(false);
                    } else if (PointUtils.getLength(leftEdge.getLeftPoint(), leftPoint) < PointUtils.getLength(leftEdge.getRightPoint(), leftPoint)) {
                        eraseEdges(leftEdge, leftEdge.getLeftPoint());
                        leftEdge.setLeftPoint(leftPoint);
                        leftEdge.setInfiniteLeftEnd(false);
                        leftCell.setBoundary(leftEdge);

                        leftTwinEdge = leftEdge.getTwin();
                        Cell leftTwinCell = leftTwinEdge.getCell();
                        Edge erasedEdge = eraseEdges(leftTwinEdge, leftTwinEdge.getLeftPoint());
                        if (erasedEdge != null) {
                            excludedEdges.put(leftTwinCell.getCenter(), erasedEdge);
                        }
                        leftTwinEdge.setLeftPoint(leftPoint);
                        leftTwinEdge.setInfiniteLeftEnd(false);
                    }
                } else if (isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getLeftPoint())) {
                    eraseEdges(leftEdge, leftEdge.getRightPoint());
                    leftEdge.setRightPoint(leftPoint);
                    leftEdge.setInfiniteRightEnd(false);
                    leftCell.setBoundary(leftEdge);

                    leftTwinEdge = leftEdge.getTwin();
                    Cell leftTwinCell = leftTwinEdge.getCell();
                    Edge erasedEdge = eraseEdges(leftTwinEdge, leftTwinEdge.getRightPoint());
                    if (erasedEdge != null) {
                        excludedEdges.put(leftTwinCell.getCenter(), erasedEdge);
                    }
                    leftTwinEdge.setRightPoint(leftPoint);
                    leftTwinEdge.setInfiniteRightEnd(false);
                } else if (isOnTheSameSide(middlePerpendicular, leftCell.getCenter(), leftLine.getRightPoint())) {
                    eraseEdges(leftEdge, leftEdge.getLeftPoint());
                    leftEdge.setLeftPoint(leftPoint);
                    leftEdge.setInfiniteLeftEnd(false);
                    leftCell.setBoundary(leftEdge);

                    leftTwinEdge = leftEdge.getTwin();
                    Cell leftTwinCell = leftTwinEdge.getCell();
                    Edge erasedEdge = eraseEdges(leftTwinEdge, leftTwinEdge.getLeftPoint());
                    if (erasedEdge != null) {
                        excludedEdges.put(leftTwinCell.getCenter(), erasedEdge);
                    }
                    leftTwinEdge.setLeftPoint(leftPoint);
                    leftTwinEdge.setInfiniteLeftEnd(false);
                }
                assert leftTwinEdge != null;
                leftTwinEdge.getCell().setBoundary(leftTwinEdge);

                Edge nextLeftEdge = new Edge((currentChainPoint == null ? middlePerpendicular.getRightPoint() : currentChainPoint), leftPoint, leftCell);
                nextLeftEdge.setInfiniteLeftEnd(currentChainPoint == null);
                nextLeftEdge.setInfiniteRightEnd(false);
                leftEdge.setPrev(nextLeftEdge);

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

                Edge nextRightEdge = new Edge((currentChainPoint == null ? middlePerpendicular.getRightPoint() : currentChainPoint), leftPoint, rightCell);
                nextRightEdge.setInfiniteLeftEnd(currentChainPoint == null);
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

                upperCommonSupport.setLeftPoint(leftTwinEdge.getCell().getCenter());
                nextLeftEdge.setTwin(nextRightEdge);
                currentChainPoint = leftPoint;
                currentEdge = leftEdge;
            } else if (leftEdge == null || leftDistance >= rightDistance) {
                Edge rightTwinEdge = null;
                Line rightLine = new Line(rightEdge);
                if (isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getLeftPoint()) && isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getRightPoint())) {
                    if (PointUtils.getLength(rightEdge.getLeftPoint(), rightPoint) > PointUtils.getLength(rightEdge.getRightPoint(), rightPoint)) {
                        eraseEdges(rightEdge, rightEdge.getRightPoint());
                        rightEdge.setRightPoint(rightPoint);
                        rightEdge.setInfiniteRightEnd(false);
                        rightCell.setBoundary(rightEdge);

                        rightTwinEdge = rightEdge.getTwin();
                        Cell rightTwinCell = rightTwinEdge.getCell();
                        Edge erasedEdge = eraseEdges(rightTwinEdge, rightTwinEdge.getRightPoint());
                        if (erasedEdge != null) {
                            excludedEdges.put(rightTwinCell.getCenter(), erasedEdge);
                        }
                        rightTwinEdge.setRightPoint(rightPoint);
                        rightTwinEdge.setInfiniteRightEnd(false);
                    } else if (PointUtils.getLength(rightEdge.getLeftPoint(), rightPoint) < PointUtils.getLength(rightEdge.getRightPoint(), rightPoint)) {
                        eraseEdges(rightEdge, rightEdge.getLeftPoint());
                        rightEdge.setLeftPoint(rightPoint);
                        rightEdge.setInfiniteLeftEnd(false);
                        rightCell.setBoundary(rightEdge);

                        rightTwinEdge = rightEdge.getTwin();
                        Cell rightTwinCell = rightTwinEdge.getCell();
                        Edge erasedEdge = eraseEdges(rightTwinEdge, rightTwinEdge.getLeftPoint());
                        if (erasedEdge != null) {
                            excludedEdges.put(rightTwinCell.getCenter(), erasedEdge);
                        }
                        rightTwinEdge.setLeftPoint(rightPoint);
                        rightTwinEdge.setInfiniteLeftEnd(false);
                    }
                } else if (isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getLeftPoint())) {
                    eraseEdges(rightEdge, rightEdge.getRightPoint());
                    rightEdge.setRightPoint(rightPoint);
                    rightEdge.setInfiniteRightEnd(false);
                    rightCell.setBoundary(rightEdge);

                    rightTwinEdge = rightEdge.getTwin();
                    Cell rightTwinCell = rightTwinEdge.getCell();
                    Edge erasedEdge = eraseEdges(rightTwinEdge, rightTwinEdge.getRightPoint());
                    if (erasedEdge != null) {
                        excludedEdges.put(rightTwinCell.getCenter(), erasedEdge);
                    }
                    rightTwinEdge.setRightPoint(rightPoint);
                    rightTwinEdge.setInfiniteRightEnd(false);
                } else if (isOnTheSameSide(middlePerpendicular, rightCell.getCenter(), rightLine.getRightPoint())) {
                    eraseEdges(rightEdge, rightEdge.getLeftPoint());
                    rightEdge.setLeftPoint(rightPoint);
                    rightEdge.setInfiniteLeftEnd(false);
                    rightCell.setBoundary(rightEdge);

                    rightTwinEdge = rightEdge.getTwin();
                    Cell rightTwinCell = rightTwinEdge.getCell();
                    Edge erasedEdge = eraseEdges(rightTwinEdge, rightTwinEdge.getLeftPoint());
                    if (erasedEdge != null) {
                        excludedEdges.put(rightTwinCell.getCenter(), erasedEdge);
                    }
                    rightTwinEdge.setLeftPoint(rightPoint);
                    rightTwinEdge.setInfiniteLeftEnd(false);
                }
                assert rightTwinEdge != null;
                rightTwinEdge.getCell().setBoundary(rightTwinEdge);

                Edge nextRightEdge = new Edge((currentChainPoint == null ? middlePerpendicular.getRightPoint() : currentChainPoint), rightPoint, rightCell);
                nextRightEdge.setInfiniteLeftEnd(currentChainPoint == null);
                nextRightEdge.setInfiniteRightEnd(false);
                rightEdge.setPrev(nextRightEdge);

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

                Edge nextLeftEdge = new Edge((currentChainPoint == null ? middlePerpendicular.getRightPoint() : currentChainPoint), rightPoint, leftCell);
                nextLeftEdge.setInfiniteLeftEnd(currentChainPoint == null);
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
            if (firstLeftEdge != null && Objects.equals(firstEdge.getLeftPoint(), firstLeftEdge.getLeftPoint()) && Objects.equals(firstEdge.getRightPoint(), firstLeftEdge.getRightPoint())) {
                firstLeftEdge = null;
            }

            Edge lastEdge = chain.get(chain.size() - 1);
            Edge lastRightEdge = cell.getConnectedEdge(lastEdge.getRightPoint());
            if (lastRightEdge != null && Objects.equals(lastEdge.getLeftPoint(), lastRightEdge.getLeftPoint()) && Objects.equals(lastEdge.getRightPoint(), lastRightEdge.getRightPoint())) {
                lastRightEdge = null;
            }

            if (!firstEdge.isInfiniteLeftEnd() && firstLeftEdge != null) {
                firstEdge.setPrev(firstLeftEdge);
                firstLeftEdge.setNext(firstEdge);
            }

            for (int i = 0; i < chain.size() - 1; i++) {
                Edge prevEdge = chain.get(i);
                Edge nextEdge = chain.get(i + 1);
                if (nextEdge.getPrev() == null) {
                    Edge connectedEdge = prevEdge.getNext();
                    if (connectedEdge != null) {
                        connectedEdge.setPrev(prevEdge);
                        connectedEdge.setNext(nextEdge);
                        nextEdge.setPrev(connectedEdge);
                    }
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

    private boolean isConnected(Edge a, Edge b) {
        return Objects.equals(a.getLeftPoint(), b.getRightPoint()) || Objects.equals(a.getRightPoint(), b.getLeftPoint()) || Objects.equals(a.getLeftPoint(), b.getLeftPoint()) || Objects.equals(a.getRightPoint(), b.getRightPoint());
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

        Point a = new Point(prevPoint.getX() - currentPoint.getX(), prevPoint.getY() - currentPoint.getY());
        Point b = new Point(intersectPoint.getX() - currentPoint.getX(), intersectPoint.getY() - currentPoint.getY());
        Point c = new Point(nextPoint.getX() - currentPoint.getX(), nextPoint.getY() - currentPoint.getY());

        double cp = crossProduct(a, c);

        if (cp > 0) {
            return crossProduct(a, b) >= 0 && crossProduct(b, c) >= 0;
        } else {
            return crossProduct(a, b) <= 0 && crossProduct(b, c) <= 0;
        }
    }

    private Edge getClosestEdge(Edge edge, Line middlePerpendicular, Edge currentEdge, Point currentPoint) {
        if (edge == null) {
            return null;
        }

        Edge nextEdge = edge;
        Edge intersectedEdge = null;
        double distance = -1;
        do {
            if (currentEdge == null || !Objects.equals(new Line(currentEdge), new Line(nextEdge))) {
                Point intersectPoint = getPointOfIntersection(middlePerpendicular, new Line(nextEdge));
                if (intersectPoint != null && isIntersected(intersectPoint, new Line(nextEdge)) && isOutsideCell(currentEdge, currentPoint, intersectPoint)) {
                    double currentDistance = PointUtils.getLength(intersectPoint, middlePerpendicular.getRightPoint());
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
                if (intersectPoint != null && isIntersected(intersectPoint, new Line(prevEdge)) && isOutsideCell(currentEdge, currentPoint, intersectPoint)) {
                    double currentDistance = PointUtils.getLength(intersectPoint, middlePerpendicular.getRightPoint());
                    if (distance == -1 || currentDistance < distance) {
                        distance = currentDistance;
                        intersectedEdge = prevEdge;
                    }
                }
            }
            prevEdge = prevEdge.getPrev();
        } while (prevEdge != null && !Objects.equals(new Line(edge), new Line(prevEdge)));

        return intersectedEdge;
    }

    public boolean isIntersected(Point point, Line line) {
        if (point == null) {
            return false;
        }
        double x1 = line.getLeftPoint().getX();
        double y1 = line.getLeftPoint().getY();

        double x2 = point.getX();
        double y2 = point.getY();

        double x3 = line.getRightPoint().getX();
        double y3 = line.getRightPoint().getY();

        boolean isInfiniteLeftPoint = line.isInfiniteLeftEnd();
        boolean isInfiniteRightPoint = line.isInfiniteRightEnd();

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

    private Point getPointOfIntersection(Line a, Line b) {
        Point a1 = a.getLeftPoint();
        Point b1 = a.getRightPoint();

        Point a2 = b.getLeftPoint();
        Point b2 = b.getRightPoint();

        double d1 = b1.getX() - a1.getX();
        double d2 = b1.getY() - a1.getY();

        double d3 = b2.getX() - a2.getX();
        double d4 = b2.getY() - a2.getY();

        if (d1 == 0) {
            return new Point(a1.getX(), b.getEquationOfLine(a1.getX()));
        } else if (d3 == 0) {
            return new Point(a2.getX(), a.getEquationOfLine(a2.getX()));
        } else {
            double k1 = d2 / d1;
            double k2 = d4 / d3;

            if (d2 * d3 - d4 * d1 == 0) {
                return null;
            }

            double x = (a2.getY() - a1.getY() + a1.getX() * k1 - a2.getX() * k2) / (k1 - k2);
            return new Point(x, a.getEquationOfLine(x));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}