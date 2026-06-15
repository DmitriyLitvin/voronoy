package org.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.*;
import org.example.entity.Point;
import org.example.utils.EdgeUtils;
import org.example.utils.VectorUtils;

import java.util.*;
import java.util.List;

import static java.lang.Math.*;
import static org.example.utils.EdgeUtils.*;
import static org.example.utils.LineUtils.*;
import static org.example.utils.VectorUtils.crossProduct;

@Slf4j
public class Main extends Application {
    private final Pane pane = new Pane();
    private final BorderPane borderPane = new BorderPane();
    private final Map<Cell, Edge> idleEdges = new HashMap<>();

    public void start(Stage stage) {
        final Set<Point> points = new LinkedHashSet<>();

        borderPane.setCenter(pane);

        Button button = new Button("Voronoy diagram");
        button.setLayoutX(10); // X координата
        button.setLayoutY(950);
        borderPane.setBottom(button);
        pane.getChildren().add(button);


        points.forEach(p -> {
            Circle circle = new Circle(p.getX(), p.getY(), 2, Color.RED);
            Label label = new Label(+circle.getCenterX() + ", " + circle.getCenterY());
            // label.relocate(circle.getCenterX(), circle.getCenterY());
            pane.getChildren().addAll(circle);
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

    public void drawVoronoyDiagram(Set<Point> polygon) {
        System.out.println("Start drawing ");

        List<Cell> cells = new ArrayList<>(buildVoronoyDiagram(polygon.stream().sorted(Comparator.comparingDouble(Point::getX).thenComparingDouble(Point::getY)).toList()).values());

        Timeline timeline = new Timeline();
        timeline.setCycleCount(cells.size());

        final int[] index = {0};

        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(2), event -> {
                    pane.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Line);

                    Cell cell = cells.get(index[0]++);
                    Edge boundary = cell.getBoundary();

                    Edge edge = boundary;
                    if (edge != null) {
                        do {
                            Point p1 = edge.getPoint();
                            Point p2 = edge.getTwin().getPoint();

                            javafx.scene.shape.Line line =
                                    new javafx.scene.shape.Line(
                                            p1.getX(), p1.getY(),
                                            p2.getX(), p2.getY()
                                    );

                            line.setStroke(Color.BLUE);
                            line.setStrokeWidth(1);

                            pane.getChildren().add(line);

                            edge = edge.getNext();

                        } while (edge != null && !EdgeUtils.equals(boundary, edge));

                        edge = boundary;
                        do {
                            Point p1 = edge.getPoint();
                            Point p2 = edge.getTwin().getPoint();

                            javafx.scene.shape.Line line =
                                    new javafx.scene.shape.Line(
                                            p1.getX(), p1.getY(),
                                            p2.getX(), p2.getY()
                                    );

                            line.setStroke(Color.BLUE);
                            line.setStrokeWidth(1);

                            pane.getChildren().add(line);

                            edge = edge.getPrev();

                        } while (edge != null && !EdgeUtils.equals(boundary, edge));
                    } else {
                        System.out.println(cell.getCenter());
                    }
                })
        );

        timeline.play();

        System.out.println("End drawing");
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

        List<Point> sortedVertices = points.stream().filter(p -> !p.equals(point)).sorted((p1, p2) -> {
            int compared = Double.compare(atan2(p1.getY() - point.getY(), p1.getX() - point.getX()), atan2(p2.getY() - point.getY(), p2.getX() - point.getX()));
            if (compared == 0) {
                return Double.compare(sqrt(pow(p1.getY() - point.getY(), 2) + pow(p1.getX() - point.getX(), 2)), sqrt(pow(p2.getY() - point.getY(), 2) + pow(p2.getX() - point.getX(), 2)));
            }
            return compared;
        }).toList();

        Stack<Point> convexHull = new Stack<>();
        convexHull.push(sortedVertices.get(0));
        convexHull.push(point);

        sortedVertices.forEach(v -> {
            Point p1 = convexHull.get(convexHull.size() - 2);
            double x1 = p1.getX();
            double y1 = p1.getY();

            Point p2 = convexHull.peek();
            double x2 = p2.getX();
            double y2 = p2.getY();

            double x3 = v.getX();
            double y3 = v.getY();

            while (convexHull.size() > 2 && (x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2) < 0) {
                convexHull.pop();

                p1 = convexHull.get(convexHull.size() - 2);
                x1 = p1.getX();
                y1 = p1.getY();

                p2 = convexHull.peek();
                x2 = p2.getX();
                y2 = p2.getY();
            }

            convexHull.push(v);
        });

        return new HashSet<>(convexHull);
    }

    private Line getCommonSupport(Set<Point> leftPolygon, Set<Point> rightPolygon, boolean isUpper) {
        Point maxPoint = leftPolygon.stream().max(Comparator.comparingDouble(Point::getX).thenComparing(Point::getY)).orElse(null);
        Point minPoint = rightPolygon.stream().min(Comparator.comparingDouble(Point::getX).thenComparing(Point::getY)).orElse(null);
        Line line = new Line(maxPoint, minPoint);

        for (int i = 0; i < 2; i++) {
            Point leftPoint = maxPoint;
            Point rightPoint = minPoint;

            Iterator<Point> leftConvexPolygonIterator = leftPolygon.stream().filter(p -> !p.equals(maxPoint)).iterator();
            Iterator<Point> rightConvexPolygonIterator = rightPolygon.stream().filter(p -> !p.equals(minPoint)).iterator();
            while (leftConvexPolygonIterator.hasNext() || rightConvexPolygonIterator.hasNext()) {
                if (leftConvexPolygonIterator.hasNext()) {
                    leftPoint = leftConvexPolygonIterator.next();
                }
                if (rightConvexPolygonIterator.hasNext()) {
                    rightPoint = rightConvexPolygonIterator.next();
                }
                if (is(leftPoint, line, isUpper)) {
                    line.setA(leftPoint);
                    if (is(rightPoint, line, isUpper)) {
                        line.setB(rightPoint);
                    }

                } else if (is(rightPoint, line, isUpper)) {
                    line.setB(rightPoint);
                    if (is(leftPoint, line, isUpper)) {
                        line.setA(leftPoint);
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

            Line middlePerpendicular = getPerpendicular(new Line(leftCenter, rightCenter));
            Edge leftEdge = new Edge(middlePerpendicular.getA());
            Edge rightEdge = new Edge(middlePerpendicular.getB());

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

        Line upperCommonSupport = getCommonSupport(leftPolygon, rightPolygon, true);
        Line lowerCommonSupport = getCommonSupport(leftPolygon, rightPolygon, false);

        Point chainPoint = null;
        Edge currentEdge = null;
        Edge chainEdge = null;
        Line perpendicular;
        Map<Cell, List<Edge>> excludedEdges = new HashMap<>();
        Map<Cell, Edge> disjunctiveChain = new HashMap<>();


        if (Objects.equals(upperCommonSupport, lowerCommonSupport)) {
            Cell leftCell = leftDiagram.get(upperCommonSupport.getA());
            Cell rightCell = rightDiagram.get(upperCommonSupport.getB());

            perpendicular = getPerpendicular(new Line(leftCell.getCenter(), rightCell.getCenter()));
            Edge leftEdge = new Edge(perpendicular.getA(), leftCell);
            if (leftCell.getBoundary() == null) {
                leftCell.setBoundary(leftEdge);
            } else {
                idleEdges.put(leftCell, leftEdge);
            }

            Edge rightEdge = new Edge(perpendicular.getB(), rightCell);
            if (rightCell.getBoundary() == null) {
                rightCell.setBoundary(rightEdge);
            } else {
                idleEdges.put(rightCell, rightEdge);
            }

            leftEdge.setTwin(rightEdge);
            rightEdge.setTwin(leftEdge);


            Map<Point, Cell> diagram = new HashMap<>();
            diagram.putAll(leftDiagram);
            diagram.putAll(rightDiagram);

            return diagram;
        }

        while (!Objects.equals(upperCommonSupport, lowerCommonSupport)) {
            Cell leftCell = leftDiagram.get(upperCommonSupport.getA());
            Cell rightCell = rightDiagram.get(upperCommonSupport.getB());

            perpendicular = getPerpendicular(upperCommonSupport);
            Point midPoint = upperCommonSupport.getMidPoint();

            boolean isInfinite = false;
            if (chainPoint == null) {
                isInfinite = true;
                Point leftPoint = perpendicular.getA();
                if (crossProduct(VectorUtils.geDirection(upperCommonSupport.getA(), upperCommonSupport.getB()), VectorUtils.geDirection(upperCommonSupport.getA(), leftPoint)) > 0) {
                    chainPoint = leftPoint;
                } else {
                    chainPoint = perpendicular.getB();
                }
            }

            double leftDistance = 0;
            Point leftPoint = null;

            List<Edge> leftEdges = new ArrayList<>();
            if (leftCell != null) {
                Optional.ofNullable(leftCell.getBoundary()).ifPresent(leftEdges::add);
                Optional.ofNullable(idleEdges.get(leftCell)).ifPresent(leftEdges::add);
                Optional.ofNullable(excludedEdges.get(leftCell)).ifPresent(leftEdges::addAll);
            }

            Edge leftEdge = getClosestEdge(leftEdges, perpendicular, currentEdge, chainEdge, chainPoint);
            if (leftEdge != null) {
                leftPoint = getPointOfIntersection(perpendicular, new Line(leftEdge));
                assert leftPoint != null;
                leftDistance = VectorUtils.getLength(leftPoint, chainPoint);
            }


            double rightDistance = 0;
            Point rightPoint = null;

            List<Edge> rightEdges = new ArrayList<>();
            if (rightCell != null) {
                Optional.ofNullable(rightCell.getBoundary()).ifPresent(rightEdges::add);
                Optional.ofNullable(idleEdges.get(rightCell)).ifPresent(rightEdges::add);
                Optional.ofNullable(excludedEdges.get(rightCell)).ifPresent(rightEdges::addAll);
            }

            Edge rightEdge = getClosestEdge(rightEdges, perpendicular, currentEdge, chainEdge, chainPoint);
            if (rightEdge != null) {
                rightPoint = getPointOfIntersection(perpendicular, new Line(rightEdge));
                assert rightPoint != null;
                rightDistance = VectorUtils.getLength(rightPoint, chainPoint);
            }

            System.out.println(rightDistance + " " + leftDistance);

            if (rightEdge == null && leftEdge == null) {


                throw new RuntimeException("couldn't find the closest edge");
            } else if (Math.abs(leftDistance - rightDistance) <= 0.001) {


                throw new RuntimeException("leftDistance - rightDistance are equals");
            } else if (leftEdge != null && (rightEdge == null || leftDistance < rightDistance)) {
                Edge leftTwinEdge = leftEdge.getTwin();
                Edge nextLeftEdge;
                Edge nextRightEdge;

                assert leftCell != null;
                if (isOnTheSameSide(leftCell.getCenter(), leftEdge.getPoint(), midPoint)) {
                    Cell leftTwinCell = leftTwinEdge.getCell();
                    Point point = leftTwinEdge.getPoint();

                    Edge erasedEdge = eraseEdges(excludedEdges, leftTwinEdge, point);
                    if (excludedEdges.get(leftTwinCell) == null && idleEdges.get(leftTwinCell) == null) {
                        leftTwinCell.setBoundary(leftTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    eraseEdges(excludedEdges, leftEdge, point);
                    if (excludedEdges.get(leftCell) == null && idleEdges.get(leftCell) == null) {
                        leftCell.setBoundary(leftEdge);
                    }

                    leftTwinEdge.setPoint(leftPoint);
                    leftTwinEdge.setInfinite(false);
                } else if (isOnTheSameSide(leftCell.getCenter(), leftEdge.getTwin().getPoint(), midPoint)) {
                    Cell leftTwinCell = leftTwinEdge.getCell();
                    Point point = leftEdge.getPoint();

                    Edge erasedEdge = eraseEdges(excludedEdges, leftTwinEdge, point);
                    if (excludedEdges.get(leftTwinCell) == null && idleEdges.get(leftTwinCell) == null) {
                        leftTwinCell.setBoundary(leftTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    eraseEdges(excludedEdges, leftEdge, point);
                    leftEdge.setPoint(leftPoint);
                    leftEdge.setInfinite(false);
                    if (excludedEdges.get(leftCell) == null && idleEdges.get(leftCell) == null) {
                        leftCell.setBoundary(leftEdge);
                    }
                }
                nextLeftEdge = new Edge(leftPoint, leftCell);
                nextLeftEdge.setInfinite(false);

                nextRightEdge = new Edge(chainPoint, rightCell);
                nextRightEdge.setInfinite(isInfinite);

                nextRightEdge.setTwin(nextLeftEdge);
                nextLeftEdge.setTwin(nextRightEdge);

                Edge edge = disjunctiveChain.get(leftCell);
                if (edge == null) {
                    disjunctiveChain.put(leftCell, nextLeftEdge);
                } else {
                    Edge lastEdge = edge.getLastEdge();
                    if (lastEdge != null && isConnected(nextLeftEdge, lastEdge)) {
                        nextLeftEdge.setPrev(lastEdge);
                        lastEdge.setNext(nextLeftEdge);
                    } else {
                        excludedEdges.computeIfAbsent(leftCell, k -> new ArrayList<>()).add(nextLeftEdge);
                    }
                }

                if (rightCell != null) {
                    edge = disjunctiveChain.get(rightCell);
                    if (edge == null) {
                        Edge boundary = rightCell.getBoundary();
                        if (boundary == null) {
                            rightCell.setBoundary(nextRightEdge);
                        } else {
                            disjunctiveChain.put(rightCell, nextRightEdge);
                        }
                    } else {
                        Edge startEdge = edge.getStartEdge();
                        if (startEdge != null && isConnected(nextRightEdge, startEdge)) {
                            nextRightEdge.setNext(startEdge);
                            startEdge.setPrev(nextRightEdge);
                        } else {
                            excludedEdges.computeIfAbsent(rightCell, k -> new ArrayList<>()).add(nextRightEdge);
                        }
                    }
                }

                upperCommonSupport.setA(leftTwinEdge.getCell().getCenter());
                chainPoint = leftPoint;
                currentEdge = leftEdge;
                chainEdge = nextLeftEdge;
            } else if (leftEdge == null || leftDistance > rightDistance) {
                Edge rightTwinEdge = rightEdge.getTwin();
                Edge nextLeftEdge;
                Edge nextRightEdge;

                assert rightCell != null;
                if (isOnTheSameSide(rightCell.getCenter(), rightEdge.getPoint(), midPoint)) {
                    Cell rightTwinCell = rightTwinEdge.getCell();
                    Point point = rightEdge.getTwin().getPoint();

                    Edge erasedEdge = eraseEdges(excludedEdges, rightTwinEdge, point);
                    if (excludedEdges.get(rightTwinCell) == null && idleEdges.get(rightTwinCell) == null) {
                        rightTwinCell.setBoundary(rightTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    eraseEdges(excludedEdges, rightEdge, point);
                    if (excludedEdges.get(rightCell) == null && idleEdges.get(rightCell) == null) {
                        rightCell.setBoundary(rightEdge);
                    }

                    rightTwinEdge.setPoint(rightPoint);
                    rightTwinEdge.setInfinite(false);
                } else if (isOnTheSameSide(rightCell.getCenter(), rightEdge.getTwin().getPoint(), midPoint)) {
                    Cell rightTwinCell = rightTwinEdge.getCell();
                    Point point = rightEdge.getPoint();

                    Edge erasedEdge = eraseEdges(excludedEdges, rightTwinEdge, point);
                    if (excludedEdges.get(rightTwinCell) == null && idleEdges.get(rightTwinCell) == null) {
                        rightTwinCell.setBoundary(rightTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    eraseEdges(excludedEdges, rightEdge, point);
                    rightEdge.setPoint(rightPoint);
                    rightEdge.setInfinite(false);
                    if (excludedEdges.get(rightCell) == null && idleEdges.get(rightCell) == null) {
                        rightCell.setBoundary(rightEdge);
                    }
                }
                assert rightTwinEdge != null;
                nextRightEdge = new Edge(chainPoint, rightCell);
                nextRightEdge.setInfinite(isInfinite);

                nextLeftEdge = new Edge(rightPoint, leftCell);
                nextLeftEdge.setInfinite(false);

                nextLeftEdge.setTwin(nextRightEdge);
                nextRightEdge.setTwin(nextLeftEdge);

                Edge edge = disjunctiveChain.get(rightCell);
                if (edge == null) {
                    disjunctiveChain.put(rightCell, nextRightEdge);
                } else {
                    Edge startEdge = edge.getStartEdge();
                    if (startEdge != null && isConnected(nextRightEdge, startEdge)) {
                        nextRightEdge.setNext(startEdge);
                        startEdge.setPrev(nextRightEdge);
                    } else {
                        excludedEdges.computeIfAbsent(rightCell, k -> new ArrayList<>()).add(nextRightEdge);
                    }
                }

                if (leftCell != null) {
                    edge = disjunctiveChain.get(leftCell);
                    if (edge == null) {
                        Edge boundary = leftCell.getBoundary();
                        if (boundary == null) {
                            leftCell.setBoundary(nextLeftEdge);
                        } else {
                            disjunctiveChain.put(leftCell, nextLeftEdge);
                        }
                    } else {
                        Edge lastEdge = edge.getLastEdge();
                        if (lastEdge != null && isConnected(nextLeftEdge, lastEdge)) {
                            nextLeftEdge.setPrev(lastEdge);
                            lastEdge.setNext(nextLeftEdge);
                        } else {
                            excludedEdges.computeIfAbsent(leftCell, k -> new ArrayList<>()).add(nextLeftEdge);
                        }
                    }
                }

                upperCommonSupport.setB(rightTwinEdge.getCell().getCenter());
                chainPoint = rightPoint;
                currentEdge = rightEdge;
                chainEdge = nextRightEdge;
            }
        }

        for (var entry : disjunctiveChain.entrySet()) {
            Cell cell = entry.getKey();
            Edge edge = entry.getValue();
            addEdge(cell, edge);
        }

        for (var entry : excludedEdges.entrySet()) {
            Cell cell = entry.getKey();
            List<Edge> edges = entry.getValue();

            List<Edge> connectedEdges;
            while (!edges.isEmpty() && !(connectedEdges = findConnectedEdges(cell, edges)).isEmpty()) {
                for (Edge edge : connectedEdges) {
                    addEdge(cell, edge);
                }
                edges.removeAll(connectedEdges);
            }
        }

        for (var entry : idleEdges.entrySet()) {
            Cell cell = entry.getKey();
            Edge edge = entry.getValue();
            if (isIdle(edge) && isConnected(cell, edge)) {
                addEdge(cell, edge);
                idleEdges.remove(cell);
            }
        }

        perpendicular = getPerpendicular(lowerCommonSupport);

        Edge leftEdge;
        Edge rightEdge;
        Cell leftCell = leftDiagram.get(lowerCommonSupport.getA());
        Cell rightCell = rightDiagram.get(lowerCommonSupport.getB());
        Point leftPoint = perpendicular.getA();
        assert chainPoint != null;
        if (crossProduct(VectorUtils.geDirection(lowerCommonSupport.getA(), lowerCommonSupport.getB()), VectorUtils.geDirection(lowerCommonSupport.getA(), leftPoint)) < 0) {
            leftEdge = new Edge(leftPoint, leftCell);
            rightEdge = new Edge(chainPoint, rightCell);
        } else {
            Point rightPoint = perpendicular.getB();
            leftEdge = new Edge(rightPoint, leftCell);
            rightEdge = new Edge(chainPoint, rightCell);
        }


        rightEdge.setInfinite(false);
        leftEdge.setInfinite(false);
        leftEdge.setTwin(rightEdge);
        rightEdge.setTwin(leftEdge);

        Edge boundary = leftCell.getBoundary();
        if (boundary != null) {
            Edge startEdge = boundary.getStartEdge();
            Edge lastEdge = boundary.getLastEdge();

            if (isIdle(boundary)) {
                connectEdges(leftEdge, startEdge);
            } else if (lastEdge != null && isConnected(lastEdge, leftEdge)) {
                lastEdge.setNext(leftEdge);
                leftEdge.setPrev(lastEdge);
            } else if (startEdge != null && isConnected(startEdge, leftEdge)) {
                startEdge.setPrev(leftEdge);
                leftEdge.setNext(startEdge);
            }
        }

        boundary = rightCell.getBoundary();
        if (boundary != null) {
            Edge startEdge = boundary.getStartEdge();
            Edge lastEdge = boundary.getLastEdge();

            if (isIdle(boundary)) {
                connectEdges(rightEdge, lastEdge);
            } else if (startEdge != null && isConnected(startEdge, rightEdge)) {
                startEdge.setPrev(rightEdge);
                rightEdge.setNext(startEdge);
            } else if (lastEdge != null && isConnected(lastEdge, rightEdge)) {
                lastEdge.setNext(rightEdge);
                rightEdge.setPrev(lastEdge);
            }
        }

        Map<Point, Cell> diagram = new HashMap<>();
        diagram.putAll(leftDiagram);
        diagram.putAll(rightDiagram);

        return diagram;
    }

    private List<Edge> findConnectedEdges(Cell cell, List<Edge> edges) {
        List<Edge> connectedEdges = new ArrayList<>();

        for (Edge edge : edges) {
            if (isConnected(cell, edge)) {
                connectedEdges.add(edge);
            }
        }

        return connectedEdges;
    }


    private void addEdge(Cell cell, Edge edge) {
        Edge boundary = cell.getBoundary();
        Edge firstChainEdge = edge.getStartEdge();
        Edge lastChainEdge = edge.getLastEdge();

        if (firstChainEdge != null && lastChainEdge != null) {
            Point firstPoint;
            Point lastPoint;

            if (EdgeUtils.equals(firstChainEdge, lastChainEdge)) {
                firstPoint = firstChainEdge.getPoint();
                lastPoint = firstChainEdge.getTwin().getPoint();
            } else {
                firstPoint = getPoint(firstChainEdge);
                lastPoint = getPoint(lastChainEdge);
            }

            Edge firstEdge = null;
            if (firstPoint != null) {
                firstEdge = getConnectedEdge(boundary, firstPoint);
            }

            Edge lastEdge = null;
            if (lastPoint != null) {
                lastEdge = getConnectedEdge(boundary, lastPoint);
            }

            if (firstEdge != null) {
                if (isIdle(firstEdge) && isIdle(firstChainEdge)) {
                    connectEdges(firstChainEdge, firstEdge);
                } else if (firstEdge.getNext() == null && isConnected(firstEdge, firstChainEdge)) {
                    firstEdge.setNext(firstChainEdge);
                    firstChainEdge.setPrev(firstEdge);
                } else if (firstEdge.getPrev() == null && isConnected(firstEdge, lastChainEdge)) {
                    firstEdge.setPrev(lastChainEdge);
                    lastChainEdge.setNext(firstEdge);
                } else {
                    System.out.println(isConnected(firstEdge, firstChainEdge));
                    System.out.println(isConnected(firstEdge, lastChainEdge));
                    System.out.println("edge is not connected");
                }
            }

            if (lastEdge != null) {
                if (isIdle(lastEdge) && isIdle(lastChainEdge)) {
                    connectEdges(lastChainEdge, lastEdge);
                } else if (lastEdge.getPrev() == null && isConnected(lastEdge, lastChainEdge)) {
                    lastEdge.setPrev(lastChainEdge);
                    lastChainEdge.setNext(lastEdge);
                } else if (lastEdge.getNext() == null && isConnected(lastEdge, firstChainEdge)) {
                    lastEdge.setNext(firstChainEdge);
                    firstChainEdge.setPrev(lastEdge);
                } else {
                    System.out.println(isConnected(lastEdge, lastChainEdge));
                    System.out.println(isConnected(lastEdge, firstChainEdge));
                    System.out.println("edge is not connected");
                }
            }
        }
    }

    private Edge getClosestEdge(List<Edge> edges, Line perpendicular, Edge currentEdge, Edge chainEdge, Point chainPoint) {
        if (edges == null || edges.isEmpty()) {
            return null;
        }

        Edge intersectedEdge = null;
        double distance = 0;
        for (Edge edge : edges) {
            Edge nextEdge = edge;
            do {
                if ((chainEdge == null || !EdgeUtils.equals(chainEdge, nextEdge)) && (currentEdge == null || !EdgeUtils.equals(currentEdge, nextEdge))) {
                    Point intersectPoint = getPointOfIntersection(perpendicular, new Line(nextEdge));
                    if (isIntersected(intersectPoint, nextEdge) && isOutsideCell(currentEdge, chainEdge, intersectPoint)) {
                        double currentDistance = VectorUtils.getLength(intersectPoint, chainPoint);
                        if (distance == 0 || currentDistance < distance) {
                            distance = currentDistance;
                            intersectedEdge = nextEdge;
                        }
                    }
                }
                nextEdge = nextEdge.getNext();
            } while (nextEdge != null && !EdgeUtils.equals(edge, nextEdge));

            Edge prevEdge = edge;
            do {
                if ((chainEdge == null || !EdgeUtils.equals(chainEdge, prevEdge)) && (currentEdge == null || !EdgeUtils.equals(currentEdge, prevEdge))) {
                    Point intersectPoint = getPointOfIntersection(perpendicular, new Line(prevEdge));
                    if (isIntersected(intersectPoint, prevEdge) && isOutsideCell(currentEdge, chainEdge, intersectPoint)) {
                        double currentDistance = VectorUtils.getLength(intersectPoint, chainPoint);
                        if (distance == 0 || currentDistance < distance) {
                            distance = currentDistance;
                            intersectedEdge = prevEdge;
                        }
                    }
                }
                prevEdge = prevEdge.getPrev();
            } while (prevEdge != null && !EdgeUtils.equals(edge, prevEdge));
        }

        return intersectedEdge;
    }

    public static void main(String[] args) {
        launch(args);
    }
}