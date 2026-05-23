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
import org.example.entity.Vertex;
import org.example.utils.EdgeUtils;
import org.example.utils.VectorUtils;

import java.util.*;
import java.util.List;

import static java.lang.Math.*;
import static org.example.utils.EdgeUtils.*;
import static org.example.utils.VectorUtils.crossProduct;

@Slf4j
public class Main extends Application {
    private final Pane pane = new Pane();
    private final BorderPane borderPane = new BorderPane();
    private final Map<Cell, Edge> idleEdges = new HashMap<>();

    public void start(Stage stage) {
        final Set<Vertex> vertices = new LinkedHashSet<>();

        borderPane.setCenter(pane);

        Button button = new Button("Voronoy diagram");
        button.setLayoutX(10); // X координата
        button.setLayoutY(950);
        borderPane.setBottom(button);
        pane.getChildren().add(button);

       // vertices.add(new Vertex(541.0, 322.0));
        vertices.add(new Vertex(651.0, 156.0));
        vertices.add(new Vertex(686.0, 277.0));
        vertices.add(new Vertex(705.0, 395.0));
        vertices.add(new Vertex(738.0, 474.0));
        vertices.add(new Vertex(762.0, 195.0));
        vertices.add(new Vertex(783.0, 306.0));
        vertices.add(new Vertex(883.0, 406.0));
        vertices.add(new Vertex(911.0, 339.0));


        vertices.forEach(p -> {
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
            vertices.add(new Vertex(x, y));

            // Вивід координат у консоль
            System.out.println("Клік: x=" + x + ", y=" + y);
        });

        button.setOnAction(e -> {
            drawVoronoyDiagram(vertices);
            vertices.clear();
        });

        stage.setScene(scene);
        stage.setTitle("Voronoy");
        stage.show();
    }

    public void drawVoronoyDiagram(Set<Vertex> polygon) {
        System.out.println("Start drawing ");

//        buildVoronoyDiagram(polygon.stream().sorted(Comparator.comparingDouble(Vertex::getX).thenComparingDouble(Vertex::getY)).toList()).values().forEach(cell -> {
//            Edge boundary = cell.getBoundary();
//            Edge nextEdge = cell.getBoundary();
//            if (nextEdge != null) {
//                do {
//                    Vertex vertex = nextEdge.getVertex();
//                    Vertex twinVertex = nextEdge.getTwin().getVertex();
//                    javafx.scene.shape.Line line = new javafx.scene.shape.Line(vertex.getX(), vertex.getY(), twinVertex.getX(), twinVertex.getY());
//                    line.setStroke(Color.BLUE);
//                    line.setStrokeWidth(1);
//                    pane.getChildren().add(line);
//                    nextEdge = nextEdge.getNext();
//                } while (nextEdge != null && !isEquals(boundary, nextEdge));
//            }
//
//            Edge prevEdge = cell.getBoundary();
//            if (prevEdge != null) {
//                do {
//                    Vertex vertex = prevEdge.getVertex();
//                    Vertex twinVertex = prevEdge.getTwin().getVertex();
//                    javafx.scene.shape.Line line = new javafx.scene.shape.Line(vertex.getX(), vertex.getY(), twinVertex.getX(), twinVertex.getY());
//                    line.setStroke(Color.BLUE);
//                    line.setStrokeWidth(1);
//                    pane.getChildren().add(line);
//                    prevEdge = prevEdge.getPrev();
//                } while (prevEdge != null && !isEquals(boundary, prevEdge));
//            }
//        });

        List<Cell> cells = new ArrayList<>(buildVoronoyDiagram(polygon.stream().sorted(Comparator.comparingDouble(Vertex::getX).thenComparingDouble(Vertex::getY)).toList()).values());

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
                            Vertex v1 = edge.getVertex();
                            Vertex v2 = edge.getTwin().getVertex();

                            javafx.scene.shape.Line line =
                                    new javafx.scene.shape.Line(
                                            v1.getX(), v1.getY(),
                                            v2.getX(), v2.getY()
                                    );

                            line.setStroke(Color.BLUE);
                            line.setStrokeWidth(1);

                            pane.getChildren().add(line);

                            edge = edge.getNext();

                        } while (edge != null && !isEquals(boundary, edge));

                        edge = boundary;
                        do {
                            Vertex v1 = edge.getVertex();
                            Vertex v2 = edge.getTwin().getVertex();

                            javafx.scene.shape.Line line =
                                    new javafx.scene.shape.Line(
                                            v1.getX(), v1.getY(),
                                            v2.getX(), v2.getY()
                                    );

                            line.setStroke(Color.BLUE);
                            line.setStrokeWidth(1);

                            pane.getChildren().add(line);

                            edge = edge.getPrev();

                        } while (edge != null && !isEquals(boundary, edge));
                    } else {
                        System.out.println(cell.getCenter());
                    }
                })
        );

        timeline.play();

        System.out.println("End drawing");
    }

    private Set<Vertex> buildConvexHull(List<Vertex> vertices) {
        if (vertices.size() <= 2) {
            return new HashSet<>(vertices);
        }
        Vertex vertex = vertices.stream().min((v1, v2) -> {
            if (v1.getY() != v2.getY()) {
                return Double.compare(v1.getY(), v2.getY());
            }

            return Double.compare(v1.getX(), v2.getX());
        }).orElse(null);

        List<Vertex> sortedVertices = vertices.stream().filter(p -> !p.equals(vertex)).sorted((v1, v2) -> {
            int compared = Double.compare(atan2(v1.getY() - vertex.getY(), v1.getX() - vertex.getX()), atan2(v2.getY() - vertex.getY(), v2.getX() - vertex.getX()));
            if (compared == 0) {
                return Double.compare(sqrt(pow(v1.getY() - vertex.getY(), 2) + pow(v1.getX() - vertex.getX(), 2)), sqrt(pow(v2.getY() - vertex.getY(), 2) + pow(v2.getX() - vertex.getX(), 2)));
            }
            return compared;
        }).toList();

        Stack<Vertex> convexHull = new Stack<>();
        convexHull.push(sortedVertices.get(0));
        convexHull.push(vertex);

        sortedVertices.forEach(v -> {
            Vertex v1 = convexHull.get(convexHull.size() - 2);
            double x1 = v1.getX();
            double y1 = v1.getY();

            Vertex v2 = convexHull.peek();
            double x2 = v2.getX();
            double y2 = v2.getY();

            double x3 = v.getX();
            double y3 = v.getY();

            while (convexHull.size() > 2 && (x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2) < 0) {
                convexHull.pop();

                v1 = convexHull.get(convexHull.size() - 2);
                x1 = v1.getX();
                y1 = v1.getY();

                v2 = convexHull.peek();
                x2 = v2.getX();
                y2 = v2.getY();
            }

            convexHull.push(v);
        });

        return new HashSet<>(convexHull);
    }

    private Line getCommonSupport(Set<Vertex> leftPolygon, Set<Vertex> rightPolygon, boolean isUpper) {
        Vertex maxVertex = leftPolygon.stream().max(Comparator.comparingDouble(Vertex::getX).thenComparing(Vertex::getY)).orElse(null);
        Vertex minVertex = rightPolygon.stream().min(Comparator.comparingDouble(Vertex::getX).thenComparing(Vertex::getY)).orElse(null);
        Line line = new Line(maxVertex, minVertex);

        for (int i = 0; i < 2; i++) {
            Vertex leftVertex = maxVertex;
            Vertex rightVertex = minVertex;

            Iterator<Vertex> leftConvexPolygonIterator = leftPolygon.stream().filter(p -> !p.equals(maxVertex)).iterator();
            Iterator<Vertex> rightConvexPolygonIterator = rightPolygon.stream().filter(p -> !p.equals(minVertex)).iterator();
            while (leftConvexPolygonIterator.hasNext() || rightConvexPolygonIterator.hasNext()) {
                if (leftConvexPolygonIterator.hasNext()) {
                    leftVertex = leftConvexPolygonIterator.next();
                }
                if (rightConvexPolygonIterator.hasNext()) {
                    rightVertex = rightConvexPolygonIterator.next();
                }
                if (is(leftVertex, line, isUpper)) {
                    line.setA(leftVertex);
                    if (is(rightVertex, line, isUpper)) {
                        line.setB(rightVertex);
                    }

                } else if (is(rightVertex, line, isUpper)) {
                    line.setB(rightVertex);
                    if (is(leftVertex, line, isUpper)) {
                        line.setA(leftVertex);
                    }
                }
            }
        }

        return line;
    }


    public boolean is(Vertex v, Line l, boolean isUpper) {
        Vertex v1 = l.getA();
        Vertex v2 = l.getB();
        if (isUpper) {
            return crossProduct(VectorUtils.getDirectionVector(v1, v2), VectorUtils.getDirectionVector(v1, v)) > 0;
        }

        return crossProduct(VectorUtils.getDirectionVector(v1, v2), VectorUtils.getDirectionVector(v1, v)) < 0;
    }

    private Map<Vertex, Cell> buildVoronoyDiagram(List<Vertex> polygon) {
        if (polygon.size() == 1) {
            Map<Vertex, Cell> diagram = new HashMap<>();
            Vertex center = polygon.get(0);
            diagram.put(center, new Cell(center, null));
            return diagram;
        } else if (polygon.size() == 2) {
            Map<Vertex, Cell> diagram = new HashMap<>();
            Vertex leftCenter = polygon.get(0);
            Vertex rightCenter = polygon.get(1);

            Line middlePerpendicular = getMiddlePerpendicular(new Line(leftCenter, rightCenter));
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

    private Map<Vertex, Cell> joinDiagrams(Map<Vertex, Cell> leftDiagram, Map<Vertex, Cell> rightDiagram) {
        Set<Vertex> leftPolygon = buildConvexHull(new ArrayList<>(leftDiagram.keySet()));
        Set<Vertex> rightPolygon = buildConvexHull(new ArrayList<>(rightDiagram.keySet()));

        Line upperCommonSupport = getCommonSupport(leftPolygon, rightPolygon, true);
        Line lowerCommonSupport = getCommonSupport(leftPolygon, rightPolygon, false);

        Vertex chainVertex = null;
        Edge currentEdge = null;
        Edge chainEdge = null;
        Line middlePerpendicular;
        Map<Cell, List<Edge>> excludedEdges = new HashMap<>();
        Map<Cell, Edge> disjunctiveChain = new HashMap<>();


        if (Objects.equals(upperCommonSupport, lowerCommonSupport)) {
            Cell leftCell = leftDiagram.get(upperCommonSupport.getA());
            Cell rightCell = rightDiagram.get(upperCommonSupport.getB());

            middlePerpendicular = getMiddlePerpendicular(new Line(leftCell.getCenter(), rightCell.getCenter()));
            Edge leftEdge = new Edge(middlePerpendicular.getA(), leftCell);
            if (leftCell.getBoundary() == null) {
                leftCell.setBoundary(leftEdge);
            } else {
                idleEdges.put(leftCell, leftEdge);
            }

            Edge rightEdge = new Edge(middlePerpendicular.getB(), rightCell);
            if (rightCell.getBoundary() == null) {
                rightCell.setBoundary(rightEdge);
            } else {
                idleEdges.put(rightCell, rightEdge);
            }

            leftEdge.setTwin(rightEdge);
            rightEdge.setTwin(leftEdge);


            Map<Vertex, Cell> diagram = new HashMap<>();
            diagram.putAll(leftDiagram);
            diagram.putAll(rightDiagram);

            return diagram;
        }

        while (!Objects.equals(upperCommonSupport, lowerCommonSupport)) {
            Cell leftCell = leftDiagram.get(upperCommonSupport.getA());
            Cell rightCell = rightDiagram.get(upperCommonSupport.getB());

            middlePerpendicular = getMiddlePerpendicular(upperCommonSupport);
            Vertex midVertex = upperCommonSupport.getMidVertex();

            boolean isInfinite = false;
            if (chainVertex == null) {
                isInfinite = true;
                Vertex leftVertex = middlePerpendicular.getA();
                if (crossProduct(VectorUtils.getDirectionVector(upperCommonSupport.getA(), upperCommonSupport.getB()), VectorUtils.getDirectionVector(upperCommonSupport.getA(), leftVertex)) > 0) {
                    chainVertex = leftVertex;
                } else {
                    chainVertex = middlePerpendicular.getB();
                }
            }

            double leftDistance = 0;
            Vertex leftVertex = null;

            List<Edge> leftEdges = new ArrayList<>();
            if (leftCell != null) {
                Optional.ofNullable(leftCell.getBoundary()).ifPresent(leftEdges::add);
                Optional.ofNullable(idleEdges.get(leftCell)).ifPresent(leftEdges::add);
                Optional.ofNullable(excludedEdges.get(leftCell)).ifPresent(leftEdges::addAll);
            }

            Edge leftEdge = getClosestEdge(leftEdges, middlePerpendicular, currentEdge, chainEdge, chainVertex);
            if (leftEdge != null) {
                leftVertex = getPointOfIntersection(middlePerpendicular, new Line(leftEdge));
                assert leftVertex != null;
                leftDistance = VectorUtils.getLength(leftVertex, chainVertex);
            }


            double rightDistance = 0;
            Vertex rightVertex = null;

            List<Edge> rightEdges = new ArrayList<>();
            if (rightCell != null) {
                Optional.ofNullable(rightCell.getBoundary()).ifPresent(rightEdges::add);
                Optional.ofNullable(idleEdges.get(rightCell)).ifPresent(rightEdges::add);
                Optional.ofNullable(excludedEdges.get(rightCell)).ifPresent(rightEdges::addAll);
            }

            Edge rightEdge = getClosestEdge(rightEdges, middlePerpendicular, currentEdge, chainEdge, chainVertex);
            if (rightEdge != null) {
                rightVertex = getPointOfIntersection(middlePerpendicular, new Line(rightEdge));
                assert rightVertex != null;
                rightDistance = VectorUtils.getLength(rightVertex, chainVertex);
            }

            if (rightEdge == null && leftEdge == null) {
                throw new RuntimeException("couldn't find the closest edge");
            } else if (leftEdge != null && (rightEdge == null || leftDistance < rightDistance)) {
                Edge leftTwinEdge = leftEdge.getTwin();
                Edge nextLeftEdge;
                Edge nextRightEdge;

                assert leftCell != null;
                if (isOnTheSameSide(leftCell.getCenter(), leftEdge.getVertex(), midVertex)) {
                    Cell leftTwinCell = leftTwinEdge.getCell();
                    Vertex vertex = leftTwinEdge.getVertex();

                    Edge erasedEdge = eraseEdges(leftTwinEdge, vertex);
                    leftTwinEdge.setVertex(leftVertex);
                    leftTwinEdge.setInfinite(false);
                    if (excludedEdges.get(leftTwinCell) == null && idleEdges.get(leftTwinCell) == null) {
                        leftTwinCell.setBoundary(leftTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    eraseEdges(leftEdge, vertex);
                    if (excludedEdges.get(leftCell) == null && idleEdges.get(leftCell) == null) {
                        leftCell.setBoundary(leftEdge);
                    }
                } else if (isOnTheSameSide(leftCell.getCenter(), leftEdge.getTwin().getVertex(), midVertex)) {
                    Cell leftTwinCell = leftTwinEdge.getCell();
                    Vertex vertex = leftEdge.getVertex();

                    Edge erasedEdge = eraseEdges(leftTwinEdge, vertex);
                    if (excludedEdges.get(leftTwinCell) == null && idleEdges.get(leftTwinCell) == null) {
                        leftTwinCell.setBoundary(leftTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    eraseEdges(leftEdge, vertex);
                    leftEdge.setVertex(leftVertex);
                    leftEdge.setInfinite(false);
                    if (excludedEdges.get(leftCell) == null && idleEdges.get(leftCell) == null) {
                        leftCell.setBoundary(leftEdge);
                    }
                }
                nextLeftEdge = new Edge(leftVertex, leftCell);
                nextLeftEdge.setInfinite(false);

                nextRightEdge = new Edge(chainVertex, rightCell);
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
                        System.out.println("1");
                    }
                }

                if (rightCell != null) {
                    edge = disjunctiveChain.get(rightCell);
                    if (edge == null) {
                        disjunctiveChain.put(rightCell, nextRightEdge);
                    } else {
                        Edge startEdge = edge.getStartEdge();
                        if (startEdge != null && isConnected(nextRightEdge, startEdge)) {
                            nextRightEdge.setNext(startEdge);
                            startEdge.setPrev(nextRightEdge);
                        } else {
                            System.out.println("2");
                        }
                    }
                }

                upperCommonSupport.setA(leftTwinEdge.getCell().getCenter());
                chainVertex = leftVertex;
                currentEdge = leftEdge;
                chainEdge = nextLeftEdge;
            } else if (leftEdge == null || leftDistance >= rightDistance) {
                Edge rightTwinEdge = rightEdge.getTwin();
                Edge nextLeftEdge;
                Edge nextRightEdge;

                assert rightCell != null;
                if (isOnTheSameSide(rightCell.getCenter(), rightEdge.getVertex(), midVertex)) {
                    Cell rightTwinCell = rightTwinEdge.getCell();
                    Vertex vertex = rightEdge.getTwin().getVertex();

                    Edge erasedEdge = eraseEdges(rightTwinEdge, vertex);
                    rightTwinEdge.setVertex(rightVertex);
                    rightTwinEdge.setInfinite(false);
                    if (excludedEdges.get(rightTwinCell) == null && idleEdges.get(rightTwinCell) == null) {
                        rightTwinCell.setBoundary(rightTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    eraseEdges(rightEdge, vertex);
                    if (excludedEdges.get(rightCell) == null && idleEdges.get(rightCell) == null) {
                        rightCell.setBoundary(rightEdge);
                    }
                } else if (isOnTheSameSide(rightCell.getCenter(), rightEdge.getTwin().getVertex(), midVertex)) {
                    Cell rightTwinCell = rightTwinEdge.getCell();
                    Vertex vertex = rightEdge.getVertex();

                    Edge erasedEdge = eraseEdges(rightTwinEdge, vertex);
                    if (excludedEdges.get(rightTwinCell) == null && idleEdges.get(rightTwinCell) == null) {
                        rightTwinCell.setBoundary(rightTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    eraseEdges(rightEdge, vertex);
                    rightEdge.setVertex(rightVertex);
                    rightEdge.setInfinite(false);
                    if (excludedEdges.get(rightCell) == null && idleEdges.get(rightCell) == null) {
                        rightCell.setBoundary(rightEdge);
                    }
                }
                assert rightTwinEdge != null;
                nextRightEdge = new Edge(chainVertex, rightCell);
                nextRightEdge.setInfinite(isInfinite);

                nextLeftEdge = new Edge(rightVertex, leftCell);
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
                        System.out.println("3");
                    }
                }

                if (leftCell != null) {
                    edge = disjunctiveChain.get(leftCell);
                    if (edge == null) {
                        disjunctiveChain.put(leftCell, nextLeftEdge);
                    } else {
                        Edge lastEdge = edge.getLastEdge();
                        if (lastEdge != null && isConnected(nextLeftEdge, lastEdge)) {
                            nextLeftEdge.setPrev(lastEdge);
                            lastEdge.setNext(nextLeftEdge);
                        } else {
                            System.out.println("4");
                        }
                    }
                }

                upperCommonSupport.setB(rightTwinEdge.getCell().getCenter());
                chainVertex = rightVertex;
                currentEdge = rightEdge;
                chainEdge = nextRightEdge;
            }
        }

        buildChain(disjunctiveChain, leftDiagram);

        middlePerpendicular = getMiddlePerpendicular(lowerCommonSupport);

        Edge leftEdge;
        Edge rightEdge;
        Cell leftCell = leftDiagram.get(lowerCommonSupport.getA());
        Cell rightCell = rightDiagram.get(lowerCommonSupport.getB());
        Vertex leftVertex = middlePerpendicular.getA();
        assert chainVertex != null;
        if (crossProduct(VectorUtils.getDirectionVector(lowerCommonSupport.getA(), lowerCommonSupport.getB()), VectorUtils.getDirectionVector(lowerCommonSupport.getA(), leftVertex)) < 0) {
            leftEdge = new Edge(leftVertex, leftCell);
            rightEdge = new Edge(chainVertex, rightCell);
        } else {
            Vertex rightVertex = middlePerpendicular.getB();
            leftEdge = new Edge(rightVertex, leftCell);
            rightEdge = new Edge(chainVertex, rightCell);
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
                leftEdge.setNext(startEdge);
                startEdge.setPrev(leftEdge);
            } else if (startEdge != null && isConnected(startEdge, leftEdge)) {
                startEdge.setPrev(leftEdge);
                leftEdge.setNext(startEdge);
            } else if (lastEdge != null && isConnected(lastEdge, leftEdge)) {
                lastEdge.setNext(leftEdge);
                leftEdge.setPrev(lastEdge);
            } else {
                System.out.println(leftDiagram.size());
                System.out.println("5");
            }
        }

        boundary = rightCell.getBoundary();
        if (boundary != null) {
            Edge startEdge = boundary.getStartEdge();
            Edge lastEdge = boundary.getLastEdge();

            if (isIdle(boundary)) {
                rightEdge.setPrev(lastEdge);
                lastEdge.setNext(rightEdge);
            } else if (lastEdge != null && isConnected(lastEdge, rightEdge)) {
                lastEdge.setNext(rightEdge);
                rightEdge.setPrev(lastEdge);
            } else if (startEdge != null && isConnected(startEdge, rightEdge)) {
                startEdge.setPrev(rightEdge);
                rightEdge.setNext(startEdge);
            } else {
                System.out.println(rightDiagram.size());
                System.out.println("6");
            }
        }

        Map<Vertex, Cell> diagram = new HashMap<>();
        diagram.putAll(leftDiagram);
        diagram.putAll(rightDiagram);

        System.out.println("ee: " + excludedEdges.size());

        return diagram;
    }

    private Vertex getVertex(Edge e) {
        if (e.getNext() != null) {
            Edge nextEdge = e.getNext();
            if (contains(nextEdge, e.getVertex())) {
                return e.getTwin().getVertex();
            } else if (contains(nextEdge, e.getTwin().getVertex())) {
                return e.getVertex();
            }
        }
        if (e.getPrev() != null) {
            Edge prevEdge = e.getPrev();
            if (contains(prevEdge, e.getVertex())) {
                return e.getTwin().getVertex();
            } else if (contains(prevEdge, e.getTwin().getVertex())) {
                return e.getVertex();
            }
        }

        return null;
    }

    private void buildChain(Map<Cell, Edge> disjunctiveChain, Map<Vertex, Cell> leftDiagram) {
        for (Map.Entry<Cell, Edge> entry : disjunctiveChain.entrySet()) {
            Cell key = entry.getKey();
            Vertex center = key.getCenter();
            Edge chain = entry.getValue();
            Edge boundary = key.getBoundary();
            Edge firstChainEdge = chain.getStartEdge();
            Edge lastChainEdge = chain.getLastEdge();

            Vertex firstVertex;
            Vertex lastVertex;

            if (isEquals(firstChainEdge, lastChainEdge)) {
                firstVertex = firstChainEdge.getVertex();
                lastVertex = firstChainEdge.getTwin().getVertex();
            } else {
                firstVertex = getVertex(firstChainEdge);
                lastVertex = getVertex(lastChainEdge);
            }

            Edge firstEdge = null;
            if (firstVertex != null) {
                firstEdge = getConnectedEdge(boundary, firstVertex);
            }

            Edge lastEdge = null;
            if (lastVertex != null) {
                lastEdge = getConnectedEdge(boundary, lastVertex);
            }

            if (firstEdge != null) {
                if (isIdle(firstEdge) && isIdle(firstChainEdge)) {
                    if (leftDiagram.get(center) != null) {
                        firstChainEdge.setNext(firstEdge);
                        firstEdge.setPrev(firstChainEdge);
                    } else {
                        firstEdge.setNext(firstChainEdge);
                        firstChainEdge.setPrev(firstEdge);
                    }
                } else if (firstEdge.getNext() == null) {
                    firstEdge.setNext(firstChainEdge);
                    firstChainEdge.setPrev(firstEdge);
                } else if (firstEdge.getPrev() == null) {
                    firstEdge.setPrev(lastChainEdge);
                    lastChainEdge.setNext(firstEdge);
                }
            }

            if (lastEdge != null) {
                if (isIdle(lastEdge) && isIdle(lastChainEdge)) {
                    if (leftDiagram.get(center) != null) {
                        lastChainEdge.setPrev(lastEdge);
                        lastEdge.setNext(lastChainEdge);
                    } else {
                        lastEdge.setPrev(lastChainEdge);
                        lastChainEdge.setNext(lastEdge);
                    }
                } else if (lastEdge.getPrev() == null) {
                    lastEdge.setPrev(lastChainEdge);
                    lastChainEdge.setNext(lastEdge);
                } else if (lastEdge.getNext() == null) {
                    lastEdge.setNext(firstChainEdge);
                    firstChainEdge.setPrev(lastEdge);
                }
            }
        }
    }

    private boolean isIdle(Edge e) {
        return e.getPrev() == null && e.getNext() == null;
    }

    private Edge getConnectedEdge(Edge boundary, Vertex v) {
        Edge startEdge = boundary.getStartEdge();
        Edge lastEdge = boundary.getLastEdge();
        if (startEdge == null || lastEdge == null) {
            return null;
        }

        if (contains(startEdge, v)) {
            return startEdge;
        } else if (contains(lastEdge, v)) {
            return lastEdge;
        }

        return null;
    }

    private boolean contains(Edge edge, Vertex v) {
        return Objects.equals(v, edge.getVertex()) || Objects.equals(v, edge.getTwin().getVertex());
    }


    private Edge eraseEdges(Edge e, Vertex v) {
        Edge nextEdge = e.getNext();
        if (nextEdge != null && contains(nextEdge, v)) {
            e.setNext(null);
            nextEdge.setPrev(null);

            if (!e.getCell().isClosed()) {
                return nextEdge;
            }
        }

        Edge prevEdge = e.getPrev();
        if (prevEdge != null && contains(prevEdge, v)) {
            e.setPrev(null);
            prevEdge.setNext(null);

            if (!e.getCell().isClosed()) {
                return prevEdge;
            }
        }

        return null;
    }

    private boolean isOutsideCell(Edge e1, Edge e2, Vertex v) {
        if (e1 == null || e2 == null) {
            return true;
        }

        Vertex vertexOfTangency = EdgeUtils.getVertexOfTangency(e1, e2);
        if (vertexOfTangency == null) {
            return true;
        }

        return !isPointInsideAngle(EdgeUtils.getOtherVertex(e1, vertexOfTangency), vertexOfTangency, EdgeUtils.getOtherVertex(e2, vertexOfTangency), v);
    }

    private boolean isPointInsideAngle(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
        if (v2 == null) {
            return false;
        }

        if (crossProduct(VectorUtils.getDirectionVector(v2, v1), VectorUtils.getDirectionVector(v2, v3)) > 0) {
            return crossProduct(VectorUtils.getDirectionVector(v2, v1), VectorUtils.getDirectionVector(v2, v4)) > 0 && crossProduct(VectorUtils.getDirectionVector(v2, v4), VectorUtils.getDirectionVector(v2, v3)) > 0;
        } else {
            return crossProduct(VectorUtils.getDirectionVector(v2, v1), VectorUtils.getDirectionVector(v2, v4)) < 0 && crossProduct(VectorUtils.getDirectionVector(v2, v4), VectorUtils.getDirectionVector(v2, v3)) < 0;
        }
    }

    private Edge getClosestEdge(List<Edge> edges, Line middlePerpendicular, Edge currentEdge, Edge chainEdge, Vertex chainVertex) {
        if (edges == null || edges.isEmpty()) {
            return null;
        }

        Edge intersectedEdge = null;
        double distance = 0;
        for (Edge edge : edges) {
            Edge nextEdge = edge;
            do {
                if ((chainEdge == null || !isEquals(chainEdge, nextEdge)) && (currentEdge == null || !isEquals(currentEdge, nextEdge))) {
                    Vertex intersectVertex = getPointOfIntersection(middlePerpendicular, new Line(nextEdge));
                    if (intersectVertex != null && isIntersected(intersectVertex, nextEdge) && isOutsideCell(currentEdge, chainEdge, intersectVertex)) {
                        double currentDistance = VectorUtils.getLength(intersectVertex, chainVertex);
                        if (distance == 0 || currentDistance < distance) {
                            distance = currentDistance;
                            intersectedEdge = nextEdge;
                        }
                    }
                }
                nextEdge = nextEdge.getNext();
            } while (nextEdge != null && !isEquals(edge, nextEdge));

            Edge prevEdge = edge;
            do {
                if ((chainEdge == null || !isEquals(chainEdge, prevEdge)) && (currentEdge == null || !isEquals(currentEdge, prevEdge))) {
                    Vertex intersectVertex = getPointOfIntersection(middlePerpendicular, new Line(prevEdge));
                    if (intersectVertex != null && isIntersected(intersectVertex, prevEdge) && isOutsideCell(currentEdge, chainEdge, intersectVertex)) {
                        double currentDistance = VectorUtils.getLength(intersectVertex, chainVertex);
                        if (distance == 0 || currentDistance < distance) {
                            distance = currentDistance;
                            intersectedEdge = prevEdge;
                        }
                    }
                }
                prevEdge = prevEdge.getPrev();
            } while (prevEdge != null && !isEquals(edge, prevEdge));
        }

        return intersectedEdge;
    }

    public boolean isIntersected(Vertex v, Edge e) {
        boolean isInfinite = e.isInfinite();
        boolean isTwinInfinite = e.getTwin().isInfinite();
        Vertex v1 = e.getVertex();
        Vertex v2 = e.getTwin().getVertex();

        if (v == null) {
            return false;
        } else if (isInfinite && isTwinInfinite) {
            return true;
        } else if (!isInfinite && !isTwinInfinite) {
            return VectorUtils.dotProduct(VectorUtils.getDirectionVector(v, v1), VectorUtils.getDirectionVector(v, v2)) < 0;
        } else if (isInfinite) {
            return VectorUtils.dotProduct(VectorUtils.getDirectionVector(v, v2), VectorUtils.getDirectionVector(v1, v2)) > 0;
        }

        return VectorUtils.dotProduct(VectorUtils.getDirectionVector(v, v1), VectorUtils.getDirectionVector(v2, v1)) > 0;
    }

    private Line getMiddlePerpendicular(Line l) {
        double height = 1000000;
        double width = 1000000;

        Vertex v1 = l.getMidVertex();
        double x = v1.getX();
        double y = v1.getY();

        Vertex vector = VectorUtils.getDirectionVector(l.getA(), l.getB());
        if (VectorUtils.dotProduct(vector, new Vertex(1, 0)) == 0) {
            return new Line(new Vertex(-width, y), new Vertex(width, y));
        } else if (VectorUtils.dotProduct(vector, new Vertex(0, 1)) == 0) {
            return new Line(new Vertex(x, -height), new Vertex(x, height));
        } else {
            if (vector.getX() == 0) {
                return new Line(new Vertex(x, -height), new Vertex(x, height));
            }
            return new Line(new Vertex(((y + height) * vector.getY()) / vector.getX() + x, -height), new Vertex((-(height - y) * vector.getY()) / vector.getX() + x, height));
        }
    }

    private boolean isOnTheSameSide(Vertex v1, Vertex v2, Vertex v3) {
        return VectorUtils.dotProduct(VectorUtils.getDirectionVector(v3, v1), VectorUtils.getDirectionVector(v3, v2)) >= 0;
    }

    private Vertex getPointOfIntersection(Line l1, Line l2) {
        Vertex v1 = l1.getA();
        Vertex v2 = l1.getB();

        Vertex v3 = l2.getA();
        Vertex v4 = l2.getB();

        double d1 = v2.getX() - v1.getX();
        double d2 = v2.getY() - v1.getY();

        double d3 = v4.getX() - v3.getX();
        double d4 = v4.getY() - v3.getY();

        if (d1 == 0) {
            return new Vertex(v1.getX(), l2.getY(v1.getX()));
        } else if (d3 == 0) {
            return new Vertex(v3.getX(), l1.getY(v3.getX()));
        }

        double s1 = d2 / d1;
        double s2 = d4 / d3;

        if (d2 * d3 - d4 * d1 == 0) {
            return null;
        }

        double x = (v3.getY() - v1.getY() + v1.getX() * s1 - v3.getX() * s2) / (s1 - s2);
        return new Vertex(x, l1.getY(x));
    }


    public static void main(String[] args) {
        launch(args);
    }
}