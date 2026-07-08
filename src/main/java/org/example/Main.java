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
import org.example.entity.Cell;
import org.example.entity.Edge;
import org.example.entity.Line;
import org.example.entity.Point;
import org.example.utils.EdgeUtils;
import org.example.utils.VectorUtils;

import java.util.*;

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

//        points.add(new Point(350.0, 500.0));
//        points.add(new Point(450.0, 500.0));
//        points.add(new Point(500.0, 350.0));
//        points.add(new Point(500.0, 450.0));
//        points.add(new Point(500.0, 550.0));
//        points.add(new Point(500.0, 650.0));
//        points.add(new Point(550.0, 500.0));
//        points.add(new Point(650.0, 500.0));

        points.forEach(p -> {
            Circle circle = new Circle(p.getX(), p.getY(), 2, Color.RED);
            Label label = new Label(+circle.getCenterX() + ", " + circle.getCenterY());
            label.relocate(circle.getCenterX(), circle.getCenterY());
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

    public void drawVoronoyDiagram(Set<Point> polygon) {
        System.out.println("Start drawing ");

        List<Cell> cells = new ArrayList<>(buildVoronoyDiagram(polygon.stream().sorted(Comparator.comparingDouble(Point::getX).thenComparingDouble(Point::getY)).toList()).values());

        Timeline timeline = new Timeline();
        timeline.setCycleCount(cells.size());

        final int[] index = {0};

        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(2), event -> {
            pane.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Line);

            Cell cell = cells.get(index[0]++);
            Edge boundary = cell.getBoundary();

            Edge edge = boundary;
            if (edge != null) {
                do {
                    Point p1 = edge.getPoint();
                    Point p2 = edge.getTwin().getPoint();

                    javafx.scene.shape.Line line = new javafx.scene.shape.Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());

                    line.setStroke(Color.BLUE);
                    line.setStrokeWidth(1);

                    pane.getChildren().add(line);

                    edge = edge.getNext();

                } while (edge != null && !boundary.equals(edge));

                edge = boundary;
                do {
                    Point p1 = edge.getPoint();
                    Point p2 = edge.getTwin().getPoint();

                    javafx.scene.shape.Line line = new javafx.scene.shape.Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());

                    line.setStroke(Color.BLUE);
                    line.setStrokeWidth(1);

                    pane.getChildren().add(line);

                    edge = edge.getPrev();

                } while (edge != null && !boundary.equals(edge));
            } else {
                System.out.println(cell.getCenter());
            }
        }));

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

    private Point getCommonVertex(Edge edge) {
        Edge nextEdge = edge.getNext();
        if (nextEdge != null) {
            Point vertex = edge.getCommonVertex(nextEdge);
            if (vertex != null) {
                return vertex;
            }
        }

        Edge prevEdge = edge.getPrev();
        if (prevEdge != null) {
            Point vertex = edge.getCommonVertex(prevEdge);
            if (vertex != null) {
                return vertex;
            }
        }

        return null;
    }

    private Map<Point, Cell> joinDiagrams(Map<Point, Cell> leftDiagram, Map<Point, Cell> rightDiagram) {
        Set<Point> leftPolygon = buildConvexHull(new ArrayList<>(leftDiagram.keySet()));
        Set<Point> rightPolygon = buildConvexHull(new ArrayList<>(rightDiagram.keySet()));

        Line upperCommonSupport = getCommonSupport(leftPolygon, rightPolygon, true);
        Line lowerCommonSupport = getCommonSupport(leftPolygon, rightPolygon, false);

        Point chainPoint = null;
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

        List<Edge> prevEdges = new ArrayList<>();
        while (!Objects.equals(upperCommonSupport, lowerCommonSupport)) {
            System.out.println(upperCommonSupport);
            System.out.println(lowerCommonSupport);
            Cell leftCell = leftDiagram.get(upperCommonSupport.getA());
            Cell rightCell = rightDiagram.get(upperCommonSupport.getB());

            perpendicular = getPerpendicular(upperCommonSupport);
            Point middlePoint = upperCommonSupport.getMidPoint();

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

            Edge leftEdge = getClosestEdge(leftEdges, perpendicular, prevEdges, chainEdge, chainPoint);
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

            Edge rightEdge = getClosestEdge(rightEdges, perpendicular, prevEdges, chainEdge, chainPoint);
            if (rightEdge != null) {
                rightPoint = getPointOfIntersection(perpendicular, new Line(rightEdge));
                assert rightPoint != null;
                rightDistance = VectorUtils.getLength(rightPoint, chainPoint);
            }

            System.out.println(rightEdge);
            System.out.println(leftEdge);

            prevEdges.clear();

            if (rightEdge == null && leftEdge == null) {
                throw new RuntimeException("couldn't find the closest edge");
            } else if (rightEdge != null && leftEdge != null && abs(leftDistance - rightDistance) <= 0.01) {
                leftEdges = new ArrayList<>();
                Point leftVertex = getCommonVertex(leftEdge);
                if (leftVertex != null && VectorUtils.getLength(leftVertex, leftPoint) < 1) {
                    Edge currentEdge = leftEdge;
                    do {
                        Edge nextLeftEdge = currentEdge.getNext();
                        if (nextLeftEdge == null) {
                            nextLeftEdge = currentEdge;
                        }
                        leftEdges.add(nextLeftEdge);

                        Edge leftTwinEdge = nextLeftEdge.getTwin();
                        leftEdges.add(leftTwinEdge);
                        currentEdge = leftTwinEdge;
                        if (!Objects.equals(currentEdge, leftEdge)) {
                            upperCommonSupport.setA(nextLeftEdge.getCell().getCenter());
                        }
                    } while (!Objects.equals(currentEdge, leftEdge));
                } else {
                    upperCommonSupport.setA(leftEdge.getTwin().getCell().getCenter());
                }

                rightEdges = new ArrayList<>();
                Point rightVertex = getCommonVertex(rightEdge);
                if (rightVertex != null && VectorUtils.getLength(rightVertex, rightPoint) < 1) {
                    Edge currentEdge = rightEdge;
                    do {
                        Edge nextRightEdge = currentEdge.getNext();
                        if (nextRightEdge == null) {
                            nextRightEdge = currentEdge;
                        }
                        rightEdges.add(nextRightEdge);

                        Edge rightTwinEdge = nextRightEdge.getTwin();
                        rightEdges.add(rightTwinEdge);
                        currentEdge = rightTwinEdge;
                        if (!Objects.equals(currentEdge, rightEdge)) {
                            upperCommonSupport.setB(nextRightEdge.getCell().getCenter());
                        }
                    } while (!Objects.equals(currentEdge, rightEdge));
                } else {
                    upperCommonSupport.setB(rightEdge.getTwin().getCell().getCenter());
                }

                Point vertex = leftVertex != null ? leftVertex : rightVertex;

                Edge nextLeftEdge;
                if (vertex != null && VectorUtils.getLength(vertex, leftPoint) < 1) {
                    nextLeftEdge = new Edge(vertex, leftCell);
                    nextLeftEdge.setInfinite(false);
                } else {
                    nextLeftEdge = new Edge(leftPoint, leftCell);
                    nextLeftEdge.setInfinite(false);
                }

                Edge nextRightEdge = new Edge(chainPoint, rightCell);
                nextRightEdge.setInfinite(isInfinite);

                nextRightEdge.setTwin(nextLeftEdge);
                nextLeftEdge.setTwin(nextRightEdge);

                Edge chinaEdge = disjunctiveChain.get(leftCell);
                if (chinaEdge == null) {
                    disjunctiveChain.put(leftCell, nextLeftEdge);
                } else {
                    Edge lastEdge = chinaEdge.getLast();
                    if (lastEdge != null && nextLeftEdge.isConnected(lastEdge)) {
                        nextLeftEdge.connect(lastEdge);
                    } else {
                        excludedEdges.computeIfAbsent(leftCell, k -> new ArrayList<>()).add(nextLeftEdge);
                    }
                }

                if (rightCell != null) {
                    chinaEdge = disjunctiveChain.get(rightCell);
                    if (chinaEdge == null) {
                        Edge boundary = rightCell.getBoundary();
                        if (boundary == null) {
                            rightCell.setBoundary(nextRightEdge);
                        } else {
                            disjunctiveChain.put(rightCell, nextRightEdge);
                        }
                    } else {
                        Edge startEdge = chinaEdge.getStart();
                        if (startEdge != null && nextRightEdge.isConnected(startEdge)) {
                            nextRightEdge.connect(startEdge);
                        } else {
                            excludedEdges.computeIfAbsent(rightCell, k -> new ArrayList<>()).add(nextRightEdge);
                        }
                    }
                }

                if (leftVertex != null && VectorUtils.getLength(leftVertex, leftPoint) < 1) {
                    leftEdges.forEach(e -> eraseEdges(e, vertex, middlePoint, excludedEdges));
                    eraseEdges(leftEdge, middlePoint, excludedEdges, vertex);
                } else {
                    eraseEdges(leftEdge, middlePoint, excludedEdges, leftPoint);
                }


                if (rightVertex != null && VectorUtils.getLength(rightVertex, rightPoint) < 1) {
                    rightEdges.forEach(e -> eraseEdges(e, vertex, middlePoint, excludedEdges));
                    eraseEdges(leftEdge, middlePoint, excludedEdges, vertex);
                } else {
                    eraseEdges(rightEdge, middlePoint, excludedEdges, leftPoint);
                }


                if (vertex != null && VectorUtils.getLength(vertex, leftPoint) < 1) {
                    chainPoint = vertex;
                } else {
                    chainPoint = leftPoint;
                }

                prevEdges.add(leftEdge);
                prevEdges.add(rightEdge);
            } else if (leftEdge != null && (rightEdge == null || leftDistance < rightDistance)) {
                Edge leftTwinEdge = leftEdge.getTwin();
                Edge nextLeftEdge;
                Edge nextRightEdge;

                assert leftCell != null;
                eraseEdges(leftEdge, middlePoint, excludedEdges, leftPoint);
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
                    Edge lastEdge = edge.getLast();
                    if (lastEdge != null && nextLeftEdge.isConnected(lastEdge)) {
                        nextLeftEdge.connect(lastEdge);
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
                        Edge startEdge = edge.getStart();
                        if (startEdge != null && nextRightEdge.isConnected(startEdge)) {
                            nextRightEdge.connect(startEdge);
                        } else {
                            excludedEdges.computeIfAbsent(rightCell, k -> new ArrayList<>()).add(nextRightEdge);
                        }
                    }
                }

                upperCommonSupport.setA(leftTwinEdge.getCell().getCenter());
                chainPoint = leftPoint;
                chainEdge = nextLeftEdge;
                prevEdges.add(leftEdge);
            } else if (leftEdge == null || leftDistance > rightDistance) {
                Edge rightTwinEdge = rightEdge.getTwin();
                Edge nextLeftEdge;
                Edge nextRightEdge;

                assert rightCell != null;
                eraseEdges(rightEdge, middlePoint, excludedEdges, rightPoint);
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
                    Edge startEdge = edge.getStart();
                    if (startEdge != null && nextRightEdge.isConnected(startEdge)) {
                        nextRightEdge.connect(startEdge);
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
                        Edge lastEdge = edge.getLast();
                        if (lastEdge != null && nextLeftEdge.isConnected(lastEdge)) {
                            nextLeftEdge.connect(lastEdge);
                        } else {
                            excludedEdges.computeIfAbsent(leftCell, k -> new ArrayList<>()).add(nextLeftEdge);
                        }
                    }
                }

                upperCommonSupport.setB(rightTwinEdge.getCell().getCenter());
                chainPoint = rightPoint;
                chainEdge = nextRightEdge;
                prevEdges.add(rightEdge);
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

            while (!edges.isEmpty()) {
                List<Edge> connectedEdges = new ArrayList<>();
                for (Edge edge : edges) {
                    if (isConnected(cell, edge)) {
                        addEdge(cell, edge);
                        connectedEdges.add(edge);
                    }
                }
                if (connectedEdges.isEmpty()) {
                    break;
                }
                edges.removeAll(connectedEdges);
            }
        }

        List<Edge> edgesToDelete = new ArrayList<>();
        for (var entry : idleEdges.entrySet()) {
            Cell cell = entry.getKey();
            Edge edge = entry.getValue();
            if (isConnected(cell, edge)) {
                addEdge(cell, edge);
                edgesToDelete.add(edge);
            }
        }

        edgesToDelete.forEach(e -> idleEdges.remove(e.getCell()));


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
            Edge startEdge = boundary.getStart();
            Edge lastEdge = boundary.getLast();

            if (isIdle(boundary)) {
                leftEdge.connect(startEdge);
            } else if (lastEdge != null && lastEdge.isConnected(leftEdge)) {
                lastEdge.connect(leftEdge);
            } else if (startEdge != null && startEdge.isConnected(leftEdge)) {
                startEdge.connect(leftEdge);
            }
        }

        boundary = rightCell.getBoundary();
        if (boundary != null) {
            Edge startEdge = boundary.getStart();
            Edge lastEdge = boundary.getLast();

            if (isIdle(boundary)) {
                rightEdge.connect(lastEdge);
            } else if (startEdge != null && startEdge.isConnected(rightEdge)) {
                startEdge.connect(rightEdge);
            } else if (lastEdge != null && lastEdge.isConnected(rightEdge)) {
                lastEdge.connect(rightEdge);
            }
        }

        Map<Point, Cell> diagram = new HashMap<>();
        diagram.putAll(leftDiagram);
        diagram.putAll(rightDiagram);

        return diagram;
    }

    private void eraseEdges(Edge edge, Point vertex, Point middlePoint, Map<Cell, List<Edge>> excludedEdges) {
        Cell cell = edge.getCell();
        Edge nextEdge = edge.getNext();
        Edge prevEdge = edge.getPrev();
        if (nextEdge != null && !(isOnTheSameSide(cell.getCenter(), getOtherPoint(edge, vertex), middlePoint) && isOnTheSameSide(cell.getCenter(), getOtherPoint(nextEdge, vertex), middlePoint))) {
            if (isOnTheSameSide(cell.getCenter(), getOtherPoint(edge, vertex), middlePoint)) {
                EdgeUtils.eraseEdges(excludedEdges, edge, vertex);
                if (excludedEdges.get(cell) == null && idleEdges.get(cell) == null) {
                    cell.setBoundary(edge);
                }

            } else if (isOnTheSameSide(cell.getCenter(), getOtherPoint(nextEdge, vertex), middlePoint)) {
                EdgeUtils.eraseEdges(excludedEdges, nextEdge, vertex);
                nextEdge.setInfinite(false);
                if (excludedEdges.get(cell) == null && idleEdges.get(cell) == null) {
                    cell.setBoundary(nextEdge);
                }
            }
        } else if (prevEdge != null && !(isOnTheSameSide(cell.getCenter(), getOtherPoint(edge, vertex), middlePoint) && isOnTheSameSide(cell.getCenter(), getOtherPoint(prevEdge, vertex), middlePoint))) {
            if (isOnTheSameSide(cell.getCenter(), getOtherPoint(edge, vertex), middlePoint)) {
                EdgeUtils.eraseEdges(excludedEdges, edge, vertex);
                if (excludedEdges.get(cell) == null && idleEdges.get(cell) == null) {
                    cell.setBoundary(edge);
                }

            } else if (isOnTheSameSide(cell.getCenter(), getOtherPoint(prevEdge, vertex), middlePoint)) {
                EdgeUtils.eraseEdges(excludedEdges, prevEdge, vertex);
                prevEdge.setInfinite(false);
                if (excludedEdges.get(cell) == null && idleEdges.get(cell) == null) {
                    cell.setBoundary(prevEdge);
                }
            }
        }
    }

    private void eraseEdges(Edge edge, Point middlePoint, Map<Cell, List<Edge>> excludedEdges, Point point) {
        Cell cell = edge.getCell();
        Edge twinEdge = edge.getTwin();
        if (isOnTheSameSide(cell.getCenter(), edge.getPoint(), middlePoint)) {
            Cell leftTwinCell = twinEdge.getCell();
            Point twinPoint = twinEdge.getPoint();

            Edge erasedEdge = EdgeUtils.eraseEdges(excludedEdges, twinEdge, twinPoint);
            if (excludedEdges.get(leftTwinCell) == null && idleEdges.get(leftTwinCell) == null) {
                leftTwinCell.setBoundary(twinEdge);
            }
            if (erasedEdge != null) {
                excludedEdges.computeIfAbsent(leftTwinCell, k -> new ArrayList<>()).add(erasedEdge);
            }

            EdgeUtils.eraseEdges(excludedEdges, edge, twinPoint);
            if (excludedEdges.get(cell) == null && idleEdges.get(cell) == null) {
                cell.setBoundary(edge);
            }

            twinEdge.setPoint(point);
            twinEdge.setInfinite(false);
        } else if (isOnTheSameSide(cell.getCenter(), edge.getTwin().getPoint(), middlePoint)) {
            Cell leftTwinCell = twinEdge.getCell();
            Point twinPoint = edge.getPoint();

            Edge erasedEdge = EdgeUtils.eraseEdges(excludedEdges, twinEdge, twinPoint);
            if (excludedEdges.get(leftTwinCell) == null && idleEdges.get(leftTwinCell) == null) {
                leftTwinCell.setBoundary(twinEdge);
            }
            if (erasedEdge != null) {
                excludedEdges.computeIfAbsent(leftTwinCell, k -> new ArrayList<>()).add(erasedEdge);
            }

            EdgeUtils.eraseEdges(excludedEdges, edge, twinPoint);
            edge.setPoint(point);
            edge.setInfinite(false);
            if (excludedEdges.get(cell) == null && idleEdges.get(cell) == null) {
                cell.setBoundary(edge);
            }
        }
    }


    private void addEdge(Cell cell, Edge edge) {
        Edge boundary = cell.getBoundary();
        Edge firstChainEdge = edge.getStart();
        Edge lastChainEdge = edge.getLast();

        if (firstChainEdge != null && lastChainEdge != null) {
            Point firstPoint;
            Point lastPoint;

            if (firstChainEdge.equals(lastChainEdge)) {
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
                    firstChainEdge.connect(firstEdge);
                } else if (firstEdge.getNext() == null && firstEdge.isConnected(firstChainEdge)) {
                    firstEdge.connect(firstChainEdge);
                } else if (firstEdge.getPrev() == null && firstEdge.isConnected(lastChainEdge)) {
                    firstEdge.connect(lastChainEdge);
                } else {
                    System.out.println(firstEdge.isConnected(firstChainEdge));
                    System.out.println(firstEdge.isConnected(lastChainEdge));
                    System.out.println("edge is not connected");
                }
            }

            if (lastEdge != null) {
                if (isIdle(lastEdge) && isIdle(lastChainEdge)) {
                    lastChainEdge.connect(lastEdge);
                } else if (lastEdge.getPrev() == null && lastEdge.isConnected(lastChainEdge)) {
                    lastEdge.connect(lastChainEdge);
                } else if (lastEdge.getNext() == null && lastEdge.isConnected(firstChainEdge)) {
                    lastEdge.connect(firstChainEdge);
                } else {
                    System.out.println(lastEdge.isConnected(lastChainEdge));
                    System.out.println(lastEdge.isConnected(firstChainEdge));
                    System.out.println("edge is not connected");
                }
            }
        }
    }

    private Edge getClosestEdge(List<Edge> edges, Line perpendicular, List<Edge> prevEdges, Edge chainEdge, Point chainPoint) {
        if (edges == null || edges.isEmpty()) {
            return null;
        }

        Edge intersectedEdge = null;
        double distance = 0;
        for (Edge edge : edges) {
            Edge nextEdge = edge;
            do {
                if ((chainEdge == null || !chainEdge.equals(nextEdge)) && (prevEdges == null || !prevEdges.contains(nextEdge))) {
                    Point intersectPoint = getPointOfIntersection(perpendicular, new Line(nextEdge));
                    if (intersectPoint != null && prevEdges.stream().allMatch(e -> isOutsideCell(e, chainEdge, intersectPoint))) {
                        double currentDistance = VectorUtils.getLength(intersectPoint, chainPoint);
                        if (distance == 0 || currentDistance < distance) {
                            distance = currentDistance;
                            intersectedEdge = nextEdge;
                        }
                    }
                }
                nextEdge = nextEdge.getNext();
            } while (nextEdge != null && !edge.equals(nextEdge));

            Edge prevEdge = edge;
            do {
                if ((chainEdge == null || !chainEdge.equals(prevEdge)) && (prevEdges == null || !prevEdges.contains(prevEdge))) {
                    Point intersectPoint = getPointOfIntersection(perpendicular, new Line(prevEdge));
                    if (intersectPoint != null && prevEdges.stream().allMatch(e -> isOutsideCell(e, chainEdge, intersectPoint))) {
                        double currentDistance = VectorUtils.getLength(intersectPoint, chainPoint);
                        if (distance == 0 || currentDistance < distance) {
                            distance = currentDistance;
                            intersectedEdge = prevEdge;
                        }
                    }
                }
                prevEdge = prevEdge.getPrev();
            } while (prevEdge != null && !edge.equals(prevEdge));
        }

        return intersectedEdge;
    }

    public static void main(String[] args) {
        launch(args);
    }
}