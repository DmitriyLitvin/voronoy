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
import lombok.extern.slf4j.Slf4j;
import org.example.entity.*;
import org.example.entity.Point;
import org.example.utils.VectorUtils;

import java.util.*;
import java.util.List;;

import static java.lang.Math.*;
import static org.example.utils.VectorUtils.crossProduct;

@Slf4j
public class Main extends Application {
    private final Set<Point> points = new LinkedHashSet<>();

    private final Pane pane = new Pane();
    private final BorderPane borderPane = new BorderPane();

    public void start(Stage stage) {

        borderPane.setCenter(pane);

        Button button = new Button("Voronoy diagram");
        button.setLayoutX(10); // X координата
        button.setLayoutY(950);
        borderPane.setBottom(button);
        pane.getChildren().add(button);


//        points.add(new Point(119, 210));
//        points.add(new Point(122, 233));
//        points.add(new Point(123, 263));
//        points.add(new Point(129, 246));
//        points.add(new Point(138, 168));
//        points.add(new Point(138, 254));
//        points.add(new Point(139, 219));
//        points.add(new Point(140, 267));
//        points.add(new Point(141, 234));
//        points.add(new Point(154, 290));
//        points.add(new Point(163, 261));
//        points.add(new Point(177, 277));
//        points.add(new Point(179, 223));
//        points.add(new Point(181, 259));
//        points.add(new Point(182, 326));
//        points.add(new Point(193, 259));
//        points.add(new Point(196, 276));
//        points.add(new Point(199, 228));
//        points.add(new Point(222, 180));
//        points.add(new Point(230, 241));
//        points.add(new Point(231, 307));
//        points.add(new Point(235, 249));
//        points.add(new Point(243, 249));
//        points.add(new Point(247, 260));
//        points.add(new Point(248, 238));
//        points.add(new Point(254, 353));
//        points.add(new Point(268, 401));
//        points.add(new Point(271, 253));
//        points.add(new Point(272, 337));
//        points.add(new Point(275, 647));
//        points.add(new Point(283, 356));
//        points.add(new Point(287, 292));
//        points.add(new Point(296, 376));
//        points.add(new Point(297, 330));
//        points.add(new Point(304, 410));
//        points.add(new Point(324, 420));
//        points.add(new Point(326, 400));
//        points.add(new Point(333, 364));
//        points.add(new Point(334, 383));
//        points.add(new Point(340, 374));
//        points.add(new Point(343, 385));
//        points.add(new Point(347, 374));
//        points.add(new Point(352, 597));
//        points.add(new Point(353, 362));
//        points.add(new Point(356, 452));
//        points.add(new Point(357, 385));
//        points.add(new Point(363, 338));
//        points.add(new Point(369, 614));
//        points.add(new Point(369, 636));
//        points.add(new Point(372, 342));
//        points.add(new Point(373, 646));
//        points.add(new Point(374, 358));
//        points.add(new Point(377, 372));
//        points.add(new Point(380, 341));
//        points.add(new Point(380, 640));
//        points.add(new Point(381, 325));
//        points.add(new Point(382, 350));
//        points.add(new Point(382, 670));
//        points.add(new Point(382, 728));
//        points.add(new Point(386, 652));
//        points.add(new Point(390, 332));
//        points.add(new Point(392, 468));
//        points.add(new Point(392, 565));
//        points.add(new Point(392, 633));
//        points.add(new Point(392, 695));
//        points.add(new Point(393, 358));
//        points.add(new Point(402, 411));
//        points.add(new Point(407, 521));
//        points.add(new Point(412, 451));
//        points.add(new Point(413, 674));
//        points.add(new Point(420, 173));
//        points.add(new Point(423, 289));
//        points.add(new Point(426, 595));
//        points.add(new Point(427, 259));
//        points.add(new Point(436, 358));
//        points.add(new Point(440, 726));
//        points.add(new Point(445, 689));
//        points.add(new Point(452, 395));
//        points.add(new Point(461, 474));
//        points.add(new Point(474, 353));
//        points.add(new Point(550, 346));
//        points.add(new Point(569, 481));
//        points.add(new Point(583, 554));
//        points.add(new Point(587, 502));
//        points.add(new Point(588, 486));
//        points.add(new Point(593, 523));
//        points.add(new Point(596, 443));
//        points.add(new Point(596, 497));
//        points.add(new Point(596, 506));
//        points.add(new Point(597, 513));
//        points.add(new Point(598, 475));
//        points.add(new Point(606, 519));
//        points.add(new Point(606, 528));
//        points.add(new Point(607, 504));
//        points.add(new Point(619, 514));
//        points.add(new Point(642, 544));
//        points.add(new Point(653, 492));
//        points.add(new Point(688, 632));
//        points.add(new Point(693, 484));
//        points.add(new Point(694, 602));
//        points.add(new Point(714, 633));
//        points.add(new Point(716, 554));
//        points.add(new Point(717, 612));
//        points.add(new Point(719, 628));
//        points.add(new Point(722, 615));
//        points.add(new Point(726, 584));
//        points.add(new Point(727, 646));
//        points.add(new Point(732, 697));
//        points.add(new Point(737, 291));
//        points.add(new Point(743, 278));
//        points.add(new Point(743, 290));
//        points.add(new Point(744, 261));
//        points.add(new Point(749, 280));
//        points.add(new Point(750, 254));
//        points.add(new Point(751, 263));
//        points.add(new Point(752, 222));
//        points.add(new Point(754, 301));
//        points.add(new Point(758, 257));
//        points.add(new Point(758, 269));
//        points.add(new Point(764, 246));
//        points.add(new Point(766, 285));
//        points.add(new Point(768, 593));
//        points.add(new Point(784, 241));
//        points.add(new Point(797, 416));
//        points.add(new Point(857, 510));
//        points.add(new Point(891, 302));

        points.forEach(p -> {
            Circle circle = new Circle(p.getX(), p.getY(), 2, Color.RED);
            //  Label label = new Label(+circle.getCenterX() + ", " + circle.getCenterY());
            // label.relocate(circle.getCenterX() + 1, circle.getCenterY() + 1);
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
        log.info("Start drawing ");
        buildVoronoyDiagram(polygon.stream().sorted(Comparator.comparingDouble(Point::getX).thenComparingDouble(Point::getY)).toList()).values().forEach(voronoyCell -> {
            Edge edge = voronoyCell.getBoundary();
            Edge nextEdge = voronoyCell.getBoundary();
            if (nextEdge != null) {
                do {
                    javafx.scene.shape.Line line = new javafx.scene.shape.Line(nextEdge.getLeftPoint().getX(), nextEdge.getLeftPoint().getY(), nextEdge.getRightPoint().getX(), nextEdge.getRightPoint().getY());
                    line.setStroke(Color.BLUE);
                    line.setStrokeWidth(1);
                    pane.getChildren().add(line);
                    nextEdge = nextEdge.getNext();
                } while (nextEdge != null && !Objects.equals(new Line(edge), new Line(nextEdge)));
            }

            Edge prevEdge = voronoyCell.getBoundary();
            if (prevEdge != null) {
                do {
                    javafx.scene.shape.Line line = new javafx.scene.shape.Line(prevEdge.getLeftPoint().getX(), prevEdge.getLeftPoint().getY(), prevEdge.getRightPoint().getX(), prevEdge.getRightPoint().getY());
                    line.setStroke(Color.BLUE);
                    line.setStrokeWidth(1);
                    pane.getChildren().add(line);
                    prevEdge = prevEdge.getPrev();
                } while (prevEdge != null && !Objects.equals(new Line(edge), new Line(prevEdge)));
            }
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

    private Line getCommonSupport(Set<Point> leftPolygon, Set<Point> rightPolygon, boolean isUpper) {
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
                if (is(leftPoint, line, isUpper)) {
                    line.setLeftPoint(leftPoint);
                    if (is(rightPoint, line, isUpper)) {
                        line.setRightPoint(rightPoint);
                    }

                } else if (is(rightPoint, line, isUpper)) {
                    line.setRightPoint(rightPoint);
                    if (is(leftPoint, line, isUpper)) {
                        line.setLeftPoint(leftPoint);
                    }
                }
            }
        }

        return line;
    }


    public boolean is(Point point, Line line, boolean isUpper) {
        Point leftPoint = line.getLeftPoint();
        Point rightPoint = line.getRightPoint();
        if (isUpper) {
            return VectorUtils.crossProduct(VectorUtils.getDirectionPoint(leftPoint, rightPoint), VectorUtils.getDirectionPoint(leftPoint, point)) > 0;
        }

        return VectorUtils.crossProduct(VectorUtils.getDirectionPoint(leftPoint, rightPoint), VectorUtils.getDirectionPoint(leftPoint, point)) < 0;
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

        Line upperCommonSupport = getCommonSupport(leftPolygon, rightPolygon, true);
        Line lowerCommonSupport = getCommonSupport(leftPolygon, rightPolygon, false);

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
                Point leftPoint = middlePerpendicular.getLeftPoint();
                if (VectorUtils.crossProduct(VectorUtils.getDirectionPoint(upperCommonSupport.getLeftPoint(), upperCommonSupport.getRightPoint()), VectorUtils.getDirectionPoint(upperCommonSupport.getLeftPoint(), leftPoint)) > 0) {
                    currentChainPoint = leftPoint;
                } else {
                    currentChainPoint = middlePerpendicular.getRightPoint();
                }
            }

            boolean isLeftExcludedEdge = false;
            double leftDistance = 0;
            Point leftPoint = null;
            Edge leftExcludedEdge = getClosestEdge(leftCell == null ? null : excludedEdges.get(leftCell), middlePerpendicular, currentEdge, currentChainEdge, currentChainPoint);
            Edge leftEdge = getClosestEdge((leftCell == null || leftCell.getBoundary() == null) ? null : List.of(leftCell.getBoundary()), middlePerpendicular, currentEdge, currentChainEdge, currentChainPoint);
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
            Edge rightExcludedEdge = getClosestEdge(rightCell == null ? null : excludedEdges.get(rightCell), middlePerpendicular, currentEdge, currentChainEdge, currentChainPoint);
            Edge rightEdge = getClosestEdge((rightCell == null || rightCell.getBoundary() == null) ? null : List.of(rightCell.getBoundary()), middlePerpendicular, currentEdge, currentChainEdge, currentChainPoint);
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
                    Edge lastEdge = leftCell.getBoundary().getLastEdge();
                    if (isLeftExcludedEdge && lastEdge != null && isConnected(lastEdge, nextLeftEdge)) {
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

                Edge nextRightEdge = new Edge(currentChainPoint, leftPoint, rightCell);
                nextRightEdge.setInfiniteLeftEnd(isInfiniteLeftEnd);
                nextRightEdge.setInfiniteRightEnd(false);
                nextRightEdge.setTwin(nextLeftEdge);

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
                    Edge startEdge = rightCell.getBoundary().getStartEdge();
                    if (isRightExcludedEdge && startEdge != null && isConnected(startEdge, nextRightEdge)) {
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

                Edge nextLeftEdge = new Edge(currentChainPoint, rightPoint, leftCell);
                nextLeftEdge.setInfiniteLeftEnd(isInfiniteLeftEnd);
                nextLeftEdge.setInfiniteRightEnd(false);
                nextLeftEdge.setTwin(nextRightEdge);

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

                upperCommonSupport.setRightPoint(rightTwinEdge.getCell().getCenter());
                nextRightEdge.setTwin(nextLeftEdge);
                currentChainPoint = rightPoint;
                currentEdge = rightEdge;
                currentChainEdge = nextRightEdge;
            }
        }

        middlePerpendicular = getMiddlePerpendicular(lowerCommonSupport);

        Edge leftEdge;
        Edge rightEdge;
        Cell leftCell = leftDiagram.get(lowerCommonSupport.getLeftPoint());
        Cell rightCell = rightDiagram.get(lowerCommonSupport.getRightPoint());
        Point leftPoint = middlePerpendicular.getLeftPoint();
        assert currentChainPoint != null;
        if (VectorUtils.crossProduct(VectorUtils.getDirectionPoint(lowerCommonSupport.getLeftPoint(), lowerCommonSupport.getRightPoint()), VectorUtils.getDirectionPoint(lowerCommonSupport.getLeftPoint(), leftPoint)) < 0) {
            leftEdge = new Edge(currentChainPoint, leftPoint, leftCell);
            rightEdge = new Edge(currentChainPoint, leftPoint, rightCell);
        } else {
            Point rightPoint = middlePerpendicular.getRightPoint();
            leftEdge = new Edge(currentChainPoint, rightPoint, leftCell);
            rightEdge = new Edge(currentChainPoint, rightPoint, rightCell);
        }

        rightEdge.setInfiniteLeftEnd(false);
        leftEdge.setInfiniteLeftEnd(false);
        leftEdge.setTwin(rightEdge);
        rightEdge.setTwin(leftEdge);

        Edge boundary = leftCell.getBoundary();
        if (boundary != null) {
            Edge startEdge = boundary.getStartEdge();
            Edge lastEdge = boundary.getLastEdge();
            if (startEdge != null && isConnected(startEdge, leftEdge)) {
                if (startEdge.getNext() == null) {
                    startEdge.setNext(leftEdge);
                    leftEdge.setPrev(startEdge);
                } else if (startEdge.getPrev() == null) {
                    startEdge.setPrev(leftEdge);
                    leftEdge.setNext(startEdge);
                }
            } else if (lastEdge != null && isConnected(lastEdge, leftEdge)) {
                if (lastEdge.getNext() == null) {
                    lastEdge.setNext(leftEdge);
                    leftEdge.setPrev(lastEdge);
                } else if (lastEdge.getPrev() == null) {
                    lastEdge.setPrev(leftEdge);
                    leftEdge.setNext(lastEdge);
                }
            }
        }

        boundary = rightCell.getBoundary();
        if (boundary != null) {
            Edge startEdge = boundary.getStartEdge();
            Edge lastEdge = boundary.getLastEdge();
            if (startEdge != null && isConnected(startEdge, rightEdge)) {
                if (startEdge.getPrev() == null) {
                    startEdge.setPrev(rightEdge);
                    rightEdge.setNext(startEdge);
                } else if (startEdge.getNext() == null) {
                    startEdge.setNext(rightEdge);
                    rightEdge.setPrev(startEdge);
                }
            } else if (lastEdge != null && isConnected(lastEdge, rightEdge)) {
                if (lastEdge.getPrev() == null) {
                    lastEdge.setPrev(rightEdge);
                    rightEdge.setNext(lastEdge);
                } else if (lastEdge.getNext() == null) {
                    lastEdge.setNext(rightEdge);
                    rightEdge.setPrev(lastEdge);
                }
            }
        }

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
            return new Line(new Point(((y + height) * directionPoint.getY()) / directionPoint.getX() + x, -height), new Point((-(height - y) * directionPoint.getY()) / directionPoint.getX() + x, height));
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
            return new Point(firstLineLeftPoint.getX(), secondLine.getY(firstLineLeftPoint.getX()));
        } else if (thirdDelta == 0) {
            return new Point(secondLineLeftPoint.getX(), firstLine.getY(secondLineLeftPoint.getX()));
        }

        double firstSlope = secondDelta / firstDelta;
        double secondSlope = forthDelta / thirdDelta;

        if (secondDelta * thirdDelta - forthDelta * firstDelta == 0) {
            return null;
        }

        double x = (secondLineLeftPoint.getY() - firstLineLeftPoint.getY() + firstLineLeftPoint.getX() * firstSlope - secondLineLeftPoint.getX() * secondSlope) / (firstSlope - secondSlope);
        return new Point(x, firstLine.getY(x));
    }


    public static void main(String[] args) {
        launch(args);
    }
}