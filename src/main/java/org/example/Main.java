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
import static org.example.utils.VectorUtils.*;

@Slf4j
public class Main extends Application {
    private final Pane pane = new Pane();
    private final BorderPane borderPane = new BorderPane();
    private final Map<Cell, Edge> idleEdges = new HashMap<>();

    public void start(Stage stage) {
        final Set<Point> points = new HashSet<>();

        borderPane.setCenter(pane);

        Button button = new Button("Voronoy diagram");
        button.setLayoutX(10); // X координата
        button.setLayoutY(950);
        borderPane.setBottom(button);
        pane.getChildren().add(button);

        Random random = new Random();

        int count = 1000; // Кількість точок, яку потрібно згенерувати


        for (int i = 0; i < count; i++) {
            // random.nextInt(max - min + 1) + min
            int x = random.nextInt(1000 - 20 + 1) + 20;
            int y = random.nextInt(1000 - 20 + 1) + 20;

            points.add(new Point(x, y));
        }

        points.forEach(p -> {
            Circle circle = new Circle(p.getX(), p.getY(), 2, Color.RED);
            Label label = new Label(+circle.getCenterX() + ", " + circle.getCenterY());
            label.relocate(circle.getCenterX(), circle.getCenterY());
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
            drawVoronoyDiagram(new ArrayList<>(points));
            points.clear();
        });

        stage.setScene(scene);
        stage.setTitle("Voronoy");
        stage.show();
    }

    public void drawVoronoyDiagram(List<Point> polygon) {
        polygon.sort((p1, p2) -> p1.getX() != p2.getX() ? Double.compare(p1.getX(), p2.getX()) : Double.compare(p1.getY(), p2.getY()));


        Set<Edge> visitedEdges = new HashSet<>();
        buildVoronoyDiagram(polygon.stream().toList()).values().forEach(cell -> {
            Edge boundary = cell.getBoundary();
            Edge nextEdge = boundary;

            if (nextEdge != null) {
                do {
                    // Проверяем, не было ли это ребро (или его близнец) уже отрисовано
                    if (!visitedEdges.contains(nextEdge)) {
                        Point startPoint = nextEdge.getPoint();
                        // Безопасная проверка на null для Twin, если диаграмма имеет открытые ребра
                        Edge twin = nextEdge.getTwin();
                        if (twin != null && startPoint != null && twin.getPoint() != null) {
                            Point endPoint = twin.getPoint();

                            javafx.scene.shape.Line line = new javafx.scene.shape.Line(
                                    startPoint.getX(), startPoint.getY(),
                                    endPoint.getX(), endPoint.getY()
                            );
                            line.setStroke(Color.BLUE);
                            line.setStrokeWidth(1);
                            pane.getChildren().add(line);
                        }

                        // Маркируем оба ребра как посещенные
                        visitedEdges.add(nextEdge);
                        if (twin != null) {
                            visitedEdges.add(twin);
                        }
                    }
                    nextEdge = nextEdge.getNext();
                } while (nextEdge != null && !Objects.equals(boundary, nextEdge));
            }
        });
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

            Line perpendicular = getPerpendicular(new Line(leftCenter, rightCenter));
            Edge leftEdge = new Edge(perpendicular.getA());
            Edge rightEdge = new Edge(perpendicular.getB());

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

    private boolean isPointsEqual(Point point, Point other) {
        return (point.getNumX() * other.getDeterminant() == point.getDeterminant() * other.getNumX() && point.getNumY() * other.getDeterminant() == point.getDeterminant() * other.getNumY());
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


        Edge currentEdge = null;
        while (!Objects.equals(upperCommonSupport, lowerCommonSupport)) {
            Cell leftCell = leftDiagram.get(upperCommonSupport.getA());
            Cell rightCell = rightDiagram.get(upperCommonSupport.getB());

            perpendicular = getPerpendicular(upperCommonSupport);

            boolean isInfinite = false;
            if (chainPoint == null) {
                isInfinite = true;
                Point point = perpendicular.getA();
                if (crossProduct(VectorUtils.geDirection(upperCommonSupport.getA(), upperCommonSupport.getB()), VectorUtils.geDirection(upperCommonSupport.getA(), point)) > 0) {
                    chainPoint = point;
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

            Edge leftEdge = getClosestEdge(leftEdges, upperCommonSupport, currentEdge, chainEdge, chainPoint);
            if (leftEdge != null) {
                leftPoint = getPointOfIntersection(upperCommonSupport, leftEdge);
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

            Edge rightEdge = getClosestEdge(rightEdges, upperCommonSupport, currentEdge, chainEdge, chainPoint);
            if (rightEdge != null) {
                rightPoint = getPointOfIntersection(upperCommonSupport, rightEdge);
                assert rightPoint != null;
                rightDistance = VectorUtils.getLength(rightPoint, chainPoint);
            }

            if (rightEdge == null && leftEdge == null) {


                throw new RuntimeException();
            } else if (rightEdge != null && leftEdge != null && leftDistance == rightDistance) {
                Point startPoint = leftEdge.getPoint();
                Point endPoint = leftEdge.getTwin().getPoint();
                if (isPointsEqual(startPoint, leftPoint)) {
                    throw new RuntimeException("edge point");
                } else if (isPointsEqual(endPoint, leftPoint)) {
                    throw new RuntimeException("edge point");
                }

                startPoint = rightEdge.getPoint();
                endPoint = rightEdge.getTwin().getPoint();
                if (isPointsEqual(startPoint, rightPoint)) {
                    throw new RuntimeException("edge point");
                } else if (isPointsEqual(endPoint, rightPoint)) {
                    throw new RuntimeException("edge point");
                } else {
                    throw new RuntimeException("distances are equal");
                }
            } else if (leftEdge != null && (rightEdge == null || leftDistance < rightDistance)) {
                Point startPOint = leftEdge.getPoint();
                Point endPoint = leftEdge.getTwin().getPoint();


                if (isPointsEqual(startPOint, leftPoint)) {
                    throw new RuntimeException("edge point");
                } else if (isPointsEqual(endPoint, leftPoint)) {
                    throw new RuntimeException("edge point");
                }


                Edge leftTwinEdge = leftEdge.getTwin();
                Edge nextLeftEdge;
                Edge nextRightEdge;

                assert leftCell != null;
                eraseEdges(leftEdge, excludedEdges, leftPoint, perpendicular);
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
                currentEdge = leftEdge;
            } else if (leftEdge == null || leftDistance > rightDistance) {
                Point startPOint = rightEdge.getPoint();
                Point endPoint = rightEdge.getTwin().getPoint();

                if (isPointsEqual(startPOint, rightPoint)) {
                    throw new RuntimeException("edge point");
                } else if (isPointsEqual(endPoint, rightPoint)) {
                    throw new RuntimeException("edge point");
                }

                Edge rightTwinEdge = rightEdge.getTwin();
                Edge nextLeftEdge;
                Edge nextRightEdge;

                assert rightCell != null;
                eraseEdges(rightEdge, excludedEdges, rightPoint, perpendicular);
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
                currentEdge = rightEdge;
            }
        }

        addEdges(disjunctiveChain, excludedEdges);
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

    private void addEdges(Map<Cell, Edge> disjunctiveChain, Map<Cell, List<Edge>> excludedEdges) {
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

        for (Edge edge : edgesToDelete) {
            idleEdges.remove(edge.getCell());
        }
    }

    private void eraseEdges(Edge edge, Map<Cell, List<Edge>> excludedEdges, Point point, Line line) {
        Cell cell = edge.getCell();
        Edge twinEdge = edge.getTwin();
        Point center = cell.getCenter();

        if (isOnTheSameSide(center, edge.getPoint(), line) && isOnTheSameSide(center, edge.getTwin().getPoint(), line)) {

        } else if (isOnTheSameSide(center, edge.getPoint(), line)) {
            eraseLightEdges(edge, excludedEdges, point, twinEdge, cell);
        } else if (isOnTheSameSide(center, edge.getTwin().getPoint(), line)) {
            eraseLeftEdges(edge, excludedEdges, point, twinEdge, cell);
        }
    }

    private void eraseLightEdges(Edge edge, Map<Cell, List<Edge>> excludedEdges, Point point, Edge twinEdge, Cell cell) {
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
    }

    private void eraseLeftEdges(Edge edge, Map<Cell, List<Edge>> excludedEdges, Point point, Edge twinEdge, Cell cell) {
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
                }
            }

            if (lastEdge != null) {
                if (isIdle(lastEdge) && isIdle(lastChainEdge)) {
                    lastChainEdge.connect(lastEdge);
                } else if (lastEdge.getPrev() == null && lastEdge.isConnected(lastChainEdge)) {
                    lastEdge.connect(lastChainEdge);
                } else if (lastEdge.getNext() == null && lastEdge.isConnected(firstChainEdge)) {
                    lastEdge.connect(firstChainEdge);

                }
            }
        }
    }

    private Edge getClosestEdge(List<Edge> edges, Line upperCommonSupport, Edge currentEdge, Edge chainEdge, Point chainPoint) {
        if (edges == null || edges.isEmpty()) {
            return null;
        }

        Edge intersectedEdge = null;
        double distance = 0;
        for (Edge edge : edges) {
            Edge nextEdge = edge;
            do {
                if ((chainEdge == null || (!chainEdge.equals(nextEdge) && !chainEdge.getTwin().equals(nextEdge))) && (currentEdge == null || (!currentEdge.equals(nextEdge) && !currentEdge.getTwin().equals(nextEdge)))) {
                    Point intersectPoint = getPointOfIntersection(upperCommonSupport, nextEdge);
                    if (intersectPoint != null && isOutsideCell(currentEdge, chainEdge, intersectPoint) && isIntersected(intersectPoint, nextEdge)) {
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
                if ((chainEdge == null || (!chainEdge.equals(prevEdge) && !chainEdge.getTwin().equals(prevEdge))) && (currentEdge == null || (!currentEdge.equals(prevEdge) && !currentEdge.getTwin().equals(prevEdge)))) {
                    Point intersectPoint = getPointOfIntersection(upperCommonSupport, prevEdge);
                    if (intersectPoint != null && isOutsideCell(currentEdge, chainEdge, intersectPoint) && isIntersected(intersectPoint, prevEdge)) {
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

    public boolean isConnected(Cell cell, Edge edge) {
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

            return firstEdge != null && (firstEdge.isConnected(firstChainEdge) || firstEdge.isConnected(lastChainEdge)) || (lastEdge != null && (lastEdge.isConnected(lastChainEdge) || lastEdge.isConnected(firstChainEdge)));
        }

        return false;
    }


    public static void main(String[] args) {
        launch(args);
    }
}