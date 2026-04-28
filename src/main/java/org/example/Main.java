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
import org.example.entity.Vertex;
import org.example.utils.EdgeUtils;
import org.example.utils.VectorUtils;

import java.util.*;
import java.util.List;;

import static java.lang.Math.*;
import static org.example.utils.EdgeUtils.isConnected;
import static org.example.utils.EdgeUtils.isEquals;
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

        vertices.add(new Vertex(119, 210));
        vertices.add(new Vertex(122, 233));
        vertices.add(new Vertex(123, 263));
        vertices.add(new Vertex(129, 246));
        vertices.add(new Vertex(138, 168));
        vertices.add(new Vertex(138, 254));
        vertices.add(new Vertex(139, 219));
        vertices.add(new Vertex(140, 267));
        vertices.add(new Vertex(141, 234));
        vertices.add(new Vertex(154, 290));
        vertices.add(new Vertex(163, 261));
        vertices.add(new Vertex(177, 277));
        vertices.add(new Vertex(179, 223));
        vertices.add(new Vertex(181, 259));
        vertices.add(new Vertex(182, 326));
        vertices.add(new Vertex(193, 259));
        vertices.add(new Vertex(196, 276));
        vertices.add(new Vertex(199, 228));
        vertices.add(new Vertex(222, 180));
        vertices.add(new Vertex(230, 241));
        vertices.add(new Vertex(231, 307));
        vertices.add(new Vertex(235, 249));
        vertices.add(new Vertex(243, 249));
        vertices.add(new Vertex(247, 260));
        vertices.add(new Vertex(248, 238));
        vertices.add(new Vertex(254, 353));
        vertices.add(new Vertex(268, 401));
        vertices.add(new Vertex(271, 253));
        vertices.add(new Vertex(272, 337));
        vertices.add(new Vertex(275, 647));
        vertices.add(new Vertex(283, 356));
        vertices.add(new Vertex(287, 292));
        vertices.add(new Vertex(296, 376));
        vertices.add(new Vertex(297, 330));
        vertices.add(new Vertex(304, 410));
        vertices.add(new Vertex(324, 420));
        vertices.add(new Vertex(326, 400));
        vertices.add(new Vertex(333, 364));
        vertices.add(new Vertex(334, 383));
        vertices.add(new Vertex(340, 374));
        vertices.add(new Vertex(343, 385));
        vertices.add(new Vertex(347, 374));
        vertices.add(new Vertex(352, 597));
        vertices.add(new Vertex(353, 362));
        vertices.add(new Vertex(356, 452));
        vertices.add(new Vertex(357, 385));
        vertices.add(new Vertex(363, 338));
        vertices.add(new Vertex(369, 614));
        vertices.add(new Vertex(369, 636));
        vertices.add(new Vertex(372, 342));
        vertices.add(new Vertex(373, 646));
        vertices.add(new Vertex(374, 358));
        vertices.add(new Vertex(377, 372));
        vertices.add(new Vertex(380, 341));
        vertices.add(new Vertex(380, 640));
        vertices.add(new Vertex(381, 325));
        vertices.add(new Vertex(382, 350));
        vertices.add(new Vertex(382, 670));
        vertices.add(new Vertex(382, 728));
        vertices.add(new Vertex(386, 652));
        vertices.add(new Vertex(390, 332));
        vertices.add(new Vertex(392, 468));
        vertices.add(new Vertex(392, 565));
        vertices.add(new Vertex(392, 633));
        vertices.add(new Vertex(392, 695));
        vertices.add(new Vertex(393, 358));
        vertices.add(new Vertex(402, 411));
        vertices.add(new Vertex(407, 521));
        vertices.add(new Vertex(412, 451));
        vertices.add(new Vertex(413, 674));
        vertices.add(new Vertex(420, 173));
        vertices.add(new Vertex(423, 289));
        vertices.add(new Vertex(426, 595));
        vertices.add(new Vertex(427, 259));
        vertices.add(new Vertex(436, 358));
        vertices.add(new Vertex(440, 726));
        vertices.add(new Vertex(445, 689));
        vertices.add(new Vertex(452, 395));
        vertices.add(new Vertex(461, 474));
        vertices.add(new Vertex(474, 353));
        vertices.add(new Vertex(550, 346));
        vertices.add(new Vertex(569, 481));
        vertices.add(new Vertex(583, 554));
        vertices.add(new Vertex(587, 502));
        vertices.add(new Vertex(588, 486));
        vertices.add(new Vertex(593, 523));
        vertices.add(new Vertex(596, 443));
        vertices.add(new Vertex(596, 497));
        vertices.add(new Vertex(596, 506));
        vertices.add(new Vertex(597, 513));
        vertices.add(new Vertex(598, 475));
        vertices.add(new Vertex(606, 519));
        vertices.add(new Vertex(606, 528));
        vertices.add(new Vertex(607, 504));
        vertices.add(new Vertex(619, 514));
        vertices.add(new Vertex(642, 544));
        vertices.add(new Vertex(653, 492));
        vertices.add(new Vertex(688, 632));
        vertices.add(new Vertex(693, 484));
        vertices.add(new Vertex(694, 602));
        vertices.add(new Vertex(714, 633));
        vertices.add(new Vertex(716, 554));
        vertices.add(new Vertex(717, 612));
        vertices.add(new Vertex(719, 628));
        vertices.add(new Vertex(722, 615));
        vertices.add(new Vertex(726, 584));
        vertices.add(new Vertex(727, 646));
        vertices.add(new Vertex(732, 697));
        vertices.add(new Vertex(737, 291));
        vertices.add(new Vertex(743, 278));
        vertices.add(new Vertex(743, 290));
        vertices.add(new Vertex(744, 261));
        vertices.add(new Vertex(749, 280));
        vertices.add(new Vertex(750, 254));
        vertices.add(new Vertex(751, 263));
        vertices.add(new Vertex(752, 222));
        vertices.add(new Vertex(754, 301));
        vertices.add(new Vertex(758, 257));
        vertices.add(new Vertex(758, 269));
        vertices.add(new Vertex(764, 246));
        vertices.add(new Vertex(766, 285));
        vertices.add(new Vertex(768, 593));
        vertices.add(new Vertex(784, 241));
        vertices.add(new Vertex(797, 416));
        vertices.add(new Vertex(857, 510));
        vertices.add(new Vertex(891, 302));

        vertices.forEach(p -> {
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
        log.info("Start drawing ");
        buildVoronoyDiagram(polygon.stream().sorted(Comparator.comparingDouble(Vertex::getX).thenComparingDouble(Vertex::getY)).toList()).values().stream().forEach(voronoyCell -> {
            Edge boundary = voronoyCell.getBoundary();
            Edge nextEdge = voronoyCell.getBoundary();
            if (nextEdge != null) {
                do {
                    Vertex vertex = nextEdge.getVertex();
                    Vertex twinVertex = nextEdge.getTwin().getVertex();
                    javafx.scene.shape.Line line = new javafx.scene.shape.Line(vertex.getX(), vertex.getY(), twinVertex.getX(), twinVertex.getY());
                    line.setStroke(Color.BLUE);
                    line.setStrokeWidth(1);
                    pane.getChildren().add(line);
                    nextEdge = nextEdge.getNext();
                } while (nextEdge != null && !isEquals(boundary, nextEdge));
            }

            Edge prevEdge = voronoyCell.getBoundary();
            if (prevEdge != null) {
                do {
                    Vertex vertex = prevEdge.getVertex();
                    Vertex twinVertex = prevEdge.getTwin().getVertex();
                    javafx.scene.shape.Line line = new javafx.scene.shape.Line(vertex.getX(), vertex.getY(), twinVertex.getX(), twinVertex.getY());
                    line.setStroke(Color.BLUE);
                    line.setStrokeWidth(1);
                    pane.getChildren().add(line);
                    prevEdge = prevEdge.getPrev();
                } while (prevEdge != null && !isEquals(boundary, prevEdge));
            }
        });

        log.info("End drawing");
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
            Vertex vertex1 = convexHull.get(convexHull.size() - 2);
            double x1 = vertex1.getX();
            double y1 = vertex1.getY();

            Vertex vertex2 = convexHull.peek();
            double x2 = vertex2.getX();
            double y2 = vertex2.getY();

            double x3 = v.getX();
            double y3 = v.getY();

            while (convexHull.size() > 2 && (x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2) < 0) {
                convexHull.pop();

                vertex1 = convexHull.get(convexHull.size() - 2);
                x1 = vertex1.getX();
                y1 = vertex1.getY();

                vertex2 = convexHull.peek();
                x2 = vertex2.getX();
                y2 = vertex2.getY();
            }

            convexHull.push(v);
        });

        return new HashSet<>(convexHull);
    }

    private Line getCommonSupport(Set<Vertex> leftPolygon, Set<Vertex> rightPolygon, boolean isUpper) {
        Vertex maxXpoint = leftPolygon.stream().max(Comparator.comparingDouble(Vertex::getX).thenComparing(Vertex::getY)).orElse(null);
        Vertex minXVertex = rightPolygon.stream().min(Comparator.comparingDouble(Vertex::getX).thenComparing(Vertex::getY)).orElse(null);
        Line line = new Line(maxXpoint, minXVertex);

        for (int i = 0; i < 2; i++) {
            Vertex leftVertex = maxXpoint;
            Vertex rightVertex = minXVertex;

            Iterator<Vertex> leftConvexPolygonIterator = leftPolygon.stream().filter(p -> !p.equals(maxXpoint)).iterator();
            Iterator<Vertex> rightConvexPolygonIterator = rightPolygon.stream().filter(p -> !p.equals(minXVertex)).iterator();
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


    public boolean is(Vertex vertex, Line line, boolean isUpper) {
        Vertex leftVertex = line.getA();
        Vertex rightVertex = line.getB();
        if (isUpper) {
            return crossProduct(VectorUtils.getDirectionPoint(leftVertex, rightVertex), VectorUtils.getDirectionPoint(leftVertex, vertex)) > 0;
        }

        return crossProduct(VectorUtils.getDirectionPoint(leftVertex, rightVertex), VectorUtils.getDirectionPoint(leftVertex, vertex)) < 0;
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
            Edge rightEdge = new Edge(middlePerpendicular.getB(), rightCell);

            leftEdge.setTwin(rightEdge);
            rightEdge.setTwin(leftEdge);

            idleEdges.put(leftCell, leftEdge);
            idleEdges.put(rightCell, rightEdge);

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
                if (crossProduct(VectorUtils.getDirectionPoint(upperCommonSupport.getA(), upperCommonSupport.getB()), VectorUtils.getDirectionPoint(upperCommonSupport.getA(), leftVertex)) > 0) {
                    chainVertex = leftVertex;
                } else {
                    chainVertex = middlePerpendicular.getB();
                }
            }

            boolean isLeftExcluded = false;
            double leftDistance = 0;
            Vertex leftVertex = null;

            List<Edge> leftEdges = new ArrayList<>();
            if (leftCell != null) {
                Edge boundary = leftCell.getBoundary();
                if (boundary != null) {
                    leftEdges.add(boundary);
                }
                Edge idle = idleEdges.get(leftCell);
                if (idle != null) {
                    leftEdges.add(idle);
                }
            }
            Edge leftEdge = getClosestEdge(leftEdges, middlePerpendicular, currentEdge, chainEdge, chainVertex);
            if (leftEdge != null) {
                leftVertex = getPointOfIntersection(middlePerpendicular, new Line(leftEdge));
                assert leftVertex != null;
                leftDistance = VectorUtils.getLength(leftVertex, chainVertex);
            }

            Edge leftExcludedEdge = getClosestEdge(leftCell == null ? null : excludedEdges.get(leftCell), middlePerpendicular, currentEdge, chainEdge, chainVertex);
            if (leftExcludedEdge != null) {
                Vertex currentVertex = getPointOfIntersection(middlePerpendicular, new Line(leftExcludedEdge));
                if (currentVertex != null) {
                    double currentDistance = VectorUtils.getLength(currentVertex, chainVertex);
                    if (currentDistance < leftDistance || leftEdge == null) {
                        leftDistance = currentDistance;
                        leftVertex = currentVertex;
                        leftEdge = leftExcludedEdge;
                        isLeftExcluded = true;
                    }
                }
            }

            boolean isRightExcluded = false;
            double rightDistance = 0;
            Vertex rightVertex = null;

            List<Edge> rightEdges = new ArrayList<>();
            if (rightCell != null) {
                Edge boundary = rightCell.getBoundary();
                if (boundary != null) {
                    rightEdges.add(boundary);
                }
                Edge idle = idleEdges.get(rightCell);
                if (idle != null) {
                    rightEdges.add(idle);
                }
            }
            Edge rightEdge = getClosestEdge(rightEdges, middlePerpendicular, currentEdge, chainEdge, chainVertex);
            if (rightEdge != null) {
                rightVertex = getPointOfIntersection(middlePerpendicular, new Line(rightEdge));
                assert rightVertex != null;
                rightDistance = VectorUtils.getLength(rightVertex, chainVertex);
            }

            Edge rightExcludedEdge = getClosestEdge(rightCell == null ? null : excludedEdges.get(rightCell), middlePerpendicular, currentEdge, chainEdge, chainVertex);
            if (rightExcludedEdge != null) {
                Vertex currentVertex = getPointOfIntersection(middlePerpendicular, new Line(rightExcludedEdge));
                if (currentVertex != null) {
                    double currentDistance = VectorUtils.getLength(currentVertex, chainVertex);
                    if (currentDistance < rightDistance || rightEdge == null) {
                        rightDistance = currentDistance;
                        rightVertex = currentVertex;
                        rightEdge = rightExcludedEdge;
                        isRightExcluded = true;
                    }
                }
            }

            if (rightEdge == null && leftEdge == null) {
                System.out.println("couldn't find the closest edge");
                break;
            } else if ((leftEdge != null && rightEdge == null) || (leftEdge != null && leftDistance < rightDistance)) {
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
                    List<Edge> leftTwinExcludedEdges = excludedEdges.get(leftTwinCell);
                    if (leftTwinExcludedEdges == null && idleEdges.get(leftTwinCell) == null) {
                        leftTwinCell.setBoundary(leftTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    erasedEdge = eraseEdges(leftEdge, vertex);
                    List<Edge> leftExcludedEdges = excludedEdges.get(leftCell);
                    if (leftExcludedEdges == null && idleEdges.get(leftCell) == null) {
                        leftCell.setBoundary(leftEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftCell, k -> new ArrayList<>()).add(erasedEdge);
                    }
                } else if (isOnTheSameSide(leftCell.getCenter(), leftEdge.getTwin().getVertex(), midVertex)) {
                    Cell leftTwinCell = leftTwinEdge.getCell();
                    Vertex vertex = leftEdge.getVertex();

                    Edge erasedEdge = eraseEdges(leftTwinEdge, vertex);
                    List<Edge> leftTwinExcludedEdges = excludedEdges.get(leftTwinCell);
                    if (leftTwinExcludedEdges == null && idleEdges.get(leftTwinCell) == null) {
                        leftTwinCell.setBoundary(leftTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    erasedEdge = eraseEdges(leftEdge, vertex);
                    leftEdge.setVertex(leftVertex);
                    leftEdge.setInfinite(false);
                    List<Edge> leftExcludedEdges = excludedEdges.get(leftCell);
                    if (leftExcludedEdges == null && idleEdges.get(leftCell) == null) {
                        leftCell.setBoundary(leftEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(leftCell, k -> new ArrayList<>()).add(erasedEdge);
                    }
                }
                nextLeftEdge = new Edge(leftVertex, leftCell);
                nextLeftEdge.setInfinite(false);
                nextLeftEdge.setNext(leftEdge);
                leftEdge.setPrev(nextLeftEdge);

                nextRightEdge = new Edge(chainVertex, rightCell);
                nextRightEdge.setInfinite(isInfinite);

                nextRightEdge.setTwin(nextLeftEdge);
                nextLeftEdge.setTwin(nextRightEdge);

                Edge edge = disjunctiveChain.get(leftCell);
                if (edge == null) {
                    Edge lastEdge = leftCell.getBoundary().getLastEdge();
                    if ((isLeftExcluded || idleEdges.get(leftCell) != null || leftCell.isConnected()) && lastEdge != null && isConnected(lastEdge, nextLeftEdge)) {
                        nextLeftEdge.setPrev(lastEdge);
                        lastEdge.setNext(nextLeftEdge);
                    }
                } else {
                    Edge lastEdge = edge.getLastEdge();
                    if (lastEdge != null && isConnected(lastEdge, nextLeftEdge)) {
                        nextLeftEdge.setPrev(lastEdge);
                        lastEdge.setNext(nextLeftEdge);
                    }
                }

                if (rightCell != null) {
                    edge = disjunctiveChain.get(rightCell);
                    if (edge == null) {
                        Edge boundary = rightCell.getBoundary();
                        if (boundary == null) {
                            rightCell.setBoundary(nextRightEdge);
                        } else {
                            Edge startEdge = boundary.getStartEdge();
                            if (startEdge != null && isConnected(startEdge, nextRightEdge)) {
                                nextRightEdge.setNext(startEdge);
                                startEdge.setPrev(nextRightEdge);
                            } else {
                                startEdge = idleEdges.get(rightCell);
                                if (startEdge != null && isConnected(startEdge, nextRightEdge)) {
                                    nextRightEdge.setNext(startEdge);
                                    startEdge.setPrev(nextRightEdge);
                                }
                                disjunctiveChain.put(rightCell, nextRightEdge);
                            }
                        }
                    } else {
                        Edge startEdge = edge.getStartEdge();
                        if (startEdge != null && isConnected(startEdge, nextRightEdge)) {
                            nextRightEdge.setNext(startEdge);
                            startEdge.setPrev(nextRightEdge);
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
                    List<Edge> rightTwinExcludedEdges = excludedEdges.get(rightTwinCell);
                    if (rightTwinExcludedEdges == null && idleEdges.get(rightTwinCell) == null) {
                        rightTwinCell.setBoundary(rightTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    erasedEdge = eraseEdges(rightEdge, vertex);
                    List<Edge> rightExcludedEdges = excludedEdges.get(rightCell);
                    if (rightExcludedEdges == null && idleEdges.get(rightCell) == null) {
                        rightCell.setBoundary(rightEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                } else if (isOnTheSameSide(rightCell.getCenter(), rightEdge.getTwin().getVertex(), midVertex)) {
                    Cell rightTwinCell = rightTwinEdge.getCell();
                    Vertex vertex = rightEdge.getVertex();

                    Edge erasedEdge = eraseEdges(rightTwinEdge, vertex);
                    List<Edge> rightTwinExcludedEdges = excludedEdges.get(rightTwinCell);
                    if (rightTwinExcludedEdges == null && idleEdges.get(rightTwinCell) == null) {
                        rightTwinCell.setBoundary(rightTwinEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightTwinCell, k -> new ArrayList<>()).add(erasedEdge);
                    }

                    erasedEdge = eraseEdges(rightEdge, vertex);
                    rightEdge.setVertex(rightVertex);
                    rightEdge.setInfinite(false);
                    List<Edge> rightExcludedEdges = excludedEdges.get(rightCell);
                    if (rightExcludedEdges == null && idleEdges.get(rightCell) == null) {
                        rightCell.setBoundary(rightEdge);
                    }
                    if (erasedEdge != null) {
                        excludedEdges.computeIfAbsent(rightCell, k -> new ArrayList<>()).add(erasedEdge);
                    }
                }
                assert rightTwinEdge != null;
                nextRightEdge = new Edge(chainVertex, rightCell);
                nextRightEdge.setInfinite(isInfinite);
                nextRightEdge.setPrev(rightEdge);
                rightEdge.setNext(nextRightEdge);

                nextLeftEdge = new Edge(rightVertex, leftCell);
                nextLeftEdge.setInfinite(false);

                nextLeftEdge.setTwin(nextRightEdge);
                nextRightEdge.setTwin(nextLeftEdge);

                Edge edge = disjunctiveChain.get(rightCell);
                if (edge == null) {
                    Edge startEdge = rightCell.getBoundary().getStartEdge();
                    if ((isRightExcluded || idleEdges.get(rightCell) != null || rightCell.isConnected()) && startEdge != null && isConnected(startEdge, nextRightEdge)) {
                        nextRightEdge.setNext(startEdge);
                        startEdge.setPrev(nextRightEdge);
                    }
                } else {
                    Edge startEdge = edge.getStartEdge();
                    if (startEdge != null && isConnected(startEdge, nextRightEdge)) {
                        nextRightEdge.setNext(startEdge);
                        startEdge.setPrev(nextRightEdge);
                    }
                }

                if (leftCell != null) {
                    edge = disjunctiveChain.get(leftCell);
                    if (edge == null) {
                        Edge boundary = leftCell.getBoundary();
                        if (boundary == null) {
                            leftCell.setBoundary(nextLeftEdge);
                        } else {
                            Edge lastEdge = boundary.getLastEdge();
                            if (lastEdge != null && isConnected(lastEdge, nextLeftEdge)) {
                                nextLeftEdge.setPrev(lastEdge);
                                lastEdge.setNext(nextLeftEdge);
                            } else {
                                lastEdge = idleEdges.get(leftCell);
                                if (lastEdge != null && isConnected(lastEdge, nextLeftEdge)) {
                                    nextLeftEdge.setPrev(lastEdge);
                                    lastEdge.setNext(nextLeftEdge);
                                }

                                disjunctiveChain.put(leftCell, nextLeftEdge);
                            }
                        }
                    } else {
                        Edge lastEdge = edge.getLastEdge();
                        if (lastEdge != null && isConnected(lastEdge, nextLeftEdge)) {
                            nextLeftEdge.setPrev(lastEdge);
                            lastEdge.setNext(nextLeftEdge);
                        }
                    }
                }

                upperCommonSupport.setB(rightTwinEdge.getCell().getCenter());
                chainVertex = rightVertex;
                currentEdge = rightEdge;
                chainEdge = nextRightEdge;
            }
        }

        middlePerpendicular = getMiddlePerpendicular(lowerCommonSupport);

        Edge leftEdge;
        Edge rightEdge;
        Cell leftCell = leftDiagram.get(lowerCommonSupport.getA());
        Cell rightCell = rightDiagram.get(lowerCommonSupport.getB());
        Vertex leftVertex = middlePerpendicular.getA();
        assert chainVertex != null;
        if (crossProduct(VectorUtils.getDirectionPoint(lowerCommonSupport.getA(), lowerCommonSupport.getB()), VectorUtils.getDirectionPoint(lowerCommonSupport.getA(), leftVertex)) < 0) {
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
            if (startEdge != null && isConnected(startEdge, leftEdge)) {
                startEdge.setNext(leftEdge);
                leftEdge.setPrev(startEdge);
            } else if (lastEdge != null && isConnected(lastEdge, leftEdge)) {
                lastEdge.setNext(leftEdge);
                leftEdge.setPrev(lastEdge);
            }
        }

        boundary = rightCell.getBoundary();
        if (boundary != null) {
            Edge startEdge = boundary.getStartEdge();
            Edge lastEdge = boundary.getLastEdge();
            if (startEdge != null && isConnected(startEdge, rightEdge)) {
                startEdge.setPrev(rightEdge);
                rightEdge.setNext(startEdge);
            } else if (lastEdge != null && isConnected(lastEdge, rightEdge)) {
                lastEdge.setPrev(rightEdge);
                rightEdge.setNext(lastEdge);
            }
        }

        Map<Vertex, Cell> diagram = new HashMap<>();
        diagram.putAll(leftDiagram);
        diagram.putAll(rightDiagram);

        return diagram;
    }

    private Edge eraseEdges(Edge edge, Vertex vertex) {
        Edge nextEdge = edge.getNext();
        if (nextEdge != null && (Objects.equals(vertex, nextEdge.getVertex()) || Objects.equals(vertex, nextEdge.getTwin().getVertex()))) {
            edge.setNext(null);
            nextEdge.setPrev(null);

            return nextEdge;
        }

        Edge prevEdge = edge.getPrev();
        if (prevEdge != null && (Objects.equals(vertex, prevEdge.getVertex()) || Objects.equals(vertex, prevEdge.getTwin().getVertex()))) {
            edge.setPrev(null);
            prevEdge.setNext(null);

            return prevEdge;
        }

        return null;
    }


    private boolean isOutsideCell(Edge edge, Vertex v1, Vertex v2) {
        if (edge == null) {
            return true;
        }
        Edge prevEdge = edge.getPrev();
        Edge nextEdge = edge.getNext();

        Vertex nextVertex = null;
        if (prevEdge != null) {
            Vertex leftVertex = prevEdge.getVertex();
            Vertex rightVertex = prevEdge.getTwin().getVertex();
            if (Objects.equals(leftVertex, v1)) {
                nextVertex = rightVertex;
            } else if (Objects.equals(rightVertex, v1)) {
                nextVertex = leftVertex;
            }
        }
        if (nextEdge != null) {
            Vertex leftVertex = nextEdge.getVertex();
            Vertex rightVertex = nextEdge.getTwin().getVertex();
            if (Objects.equals(leftVertex, v1)) {
                nextVertex = rightVertex;
            } else if (Objects.equals(rightVertex, v1)) {
                nextVertex = leftVertex;
            }
        }

        Vertex prevVertex = null;
        Vertex leftVertex = edge.getVertex();
        Vertex rightVertex = edge.getTwin().getVertex();
        if (Objects.equals(leftVertex, v1)) {
            prevVertex = rightVertex;
        } else if (Objects.equals(rightVertex, v1)) {
            prevVertex = leftVertex;
        }

        if (nextVertex == null || prevVertex == null) {
            return true;
        }

        return !isPointInsideAngle(prevVertex, v1, nextVertex, v2);
    }

    private boolean isPointInsideAngle(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
        if (v2 == null) {
            return false;
        }
        Vertex prevDirectionVertex = VectorUtils.getDirectionPoint(v2, v1);
        Vertex currentDirectionVertex = VectorUtils.getDirectionPoint(v2, v4);
        Vertex nextDirectionVertex = VectorUtils.getDirectionPoint(v2, v3);

        double crossProduct = crossProduct(prevDirectionVertex, nextDirectionVertex);
        if (crossProduct > 0) {
            return crossProduct(prevDirectionVertex, currentDirectionVertex) > 0 && crossProduct(currentDirectionVertex, nextDirectionVertex) > 0;
        } else {
            return crossProduct(prevDirectionVertex, currentDirectionVertex) < 0 && crossProduct(currentDirectionVertex, nextDirectionVertex) < 0;
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
                    if (intersectVertex != null && isIntersected(intersectVertex, nextEdge) && isOutsideCell(currentEdge, chainVertex, intersectVertex)) {
                        double currentDistance = VectorUtils.getLength(intersectVertex, middlePerpendicular.getB());
                        if (distance == 0 || currentDistance < distance) {
                            distance = currentDistance;
                            intersectedEdge = nextEdge;
                        }
                    }
                }
                nextEdge = nextEdge.getNext();
            } while (nextEdge != null && !EdgeUtils.isEquals(edge, nextEdge));

            Edge prevEdge = edge;
            do {
                if ((chainEdge == null || !isEquals(chainEdge, prevEdge)) && (currentEdge == null || !isEquals(currentEdge, prevEdge))) {
                    Vertex intersectVertex = getPointOfIntersection(middlePerpendicular, new Line(prevEdge));
                    if (intersectVertex != null && isIntersected(intersectVertex, prevEdge) && isOutsideCell(currentEdge, chainVertex, intersectVertex)) {
                        double currentDistance = VectorUtils.getLength(intersectVertex, middlePerpendicular.getB());
                        if (distance == 0 || currentDistance < distance) {
                            distance = currentDistance;
                            intersectedEdge = prevEdge;
                        }
                    }
                }
                prevEdge = prevEdge.getPrev();
            } while (prevEdge != null && !EdgeUtils.isEquals(edge, prevEdge));
        }

        return intersectedEdge;
    }

    public boolean isIntersected(Vertex vertex, Edge edge) {
        boolean isInfinite = edge.isInfinite();
        boolean isTwinInfinite = edge.getTwin().isInfinite();
        Vertex a = edge.getVertex();
        Vertex b = edge.getTwin().getVertex();

        if (vertex == null) {
            return false;
        } else if (isInfinite && isTwinInfinite) {
            return true;
        } else if (!isInfinite && !isTwinInfinite) {
            return VectorUtils.dotProduct(VectorUtils.getDirectionPoint(vertex, a), VectorUtils.getDirectionPoint(vertex, b)) <= 0;
        } else if (isInfinite) {
            return VectorUtils.dotProduct(VectorUtils.getDirectionPoint(vertex, b), VectorUtils.getDirectionPoint(a, b)) >= 0;
        }

        return VectorUtils.dotProduct(VectorUtils.getDirectionPoint(vertex, a), VectorUtils.getDirectionPoint(b, a)) >= 0;
    }

    private Line getMiddlePerpendicular(Line line) {
        int height = 1_000_000;
        int width = 1_000_000;

        Vertex vertex = line.getMidVertex();
        double x = vertex.getX();
        double y = vertex.getY();

        Vertex directionVertex = VectorUtils.getDirectionPoint(line.getA(), line.getB());
        if (VectorUtils.dotProduct(directionVertex, new Vertex(1, 0)) == 0) {
            return new Line(new Vertex(-width, y), new Vertex(width, y));
        } else if (VectorUtils.dotProduct(directionVertex, new Vertex(0, 1)) == 0) {
            return new Line(new Vertex(x, -height), new Vertex(x, height));
        } else {
            if (directionVertex.getX() == 0) {
                return new Line(new Vertex(x, -height), new Vertex(x, height));
            }
            return new Line(new Vertex(((y + height) * directionVertex.getY()) / directionVertex.getX() + x, -height), new Vertex((-(height - y) * directionVertex.getY()) / directionVertex.getX() + x, height));
        }
    }

    private boolean isOnTheSameSide(Vertex p1, Vertex p2, Vertex midVertex) {
        return VectorUtils.dotProduct(VectorUtils.getDirectionPoint(midVertex, p1), VectorUtils.getDirectionPoint(midVertex, p2)) >= 0;
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