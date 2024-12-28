package project.database;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import project.exception.EdgeNotFoundException;
import project.exception.NodeNotFoundException;
import project.model.dijkstra.Dijkstra;
import project.model.dijkstra.Edge;
import project.model.dijkstra.Node;
import project.model.dijkstra.po.EdgePO;
import project.model.dijkstra.po.NodePO;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Database {
    private final Dijkstra dijkstra;
    private final Map<Integer, Set<Integer>> adjacencyMap;
    private final LinkedList<Node> nodes;
    private final LinkedList<Circle> nodeShapes;
    private Node startNode = null;
    private final LinkedList<Edge> edges;
    private final LinkedList<Line> edgeShapes;
    public Database() {
        nodes = new LinkedList<>();
        edges = new LinkedList<>();
        nodeShapes = new LinkedList<>();
        edgeShapes = new LinkedList<>();
        adjacencyMap = new HashMap<>();
        dijkstra = new Dijkstra(nodes, edges);
    }

    public List<Circle> getNodes() {
        return nodeShapes;
    }

    public List<Line> getEdges() {
        return edgeShapes;
    }

    public boolean isReady() {
        return startNode != null;
    }

    public void setStartNode(Circle nodeShape) {
        try {
            startNode = findNode(nodeShape);
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setActive(Circle nodeShape) {
        try {
            Node node = findNode(nodeShape);
            node.setActive();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setActive(Line edgeShape) {
        try {
            Edge edge = findEdge(edgeShape);
            edge.setActive();
        } catch (EdgeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setInactive(Circle nodeShape) {
        try {
            Node node = findNode(nodeShape);
            node.setInactive();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setInactive(Line edgeShape) {
        try {
            Edge edge = findEdge(edgeShape);
            edge.setInactive();
        } catch (EdgeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Map<Circle, Line> getAdjacentNodesAndEdges(Circle nodeShape) {
        try {
            Node node = findNode(nodeShape);
            Map<Circle, Line> adjacentNodesAndEdges = new HashMap<>();

            for (Edge edge : edges) {
                if (edge.getNode1().equals(node)) {
                    adjacentNodesAndEdges.put(edge.getNode2().getShape(), edge.getShape());
                } else if (edge.getNode2().equals(node)) {
                    adjacentNodesAndEdges.put(edge.getNode1().getShape(), edge.getShape());
                }
            }

            return adjacentNodesAndEdges;
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public void addNode(Text label, Circle node) {

        if (label == null) {
            label = new Text("Node");
        }

        nodeShapes.add(node);
        nodes.add(new Node(label, node));
    }

    public void addEdge(Circle nodeShape1, Circle nodeShape2, Text label, Line edge) {
        try {
            Node node1 = findNode(nodeShape1);
            Node node2 = findNode(nodeShape2);

            edgeShapes.add(edge);
            edges.add(new Edge(node1, node2, label, edge));

            adjacencyMap.computeIfAbsent(node1.getId(), k -> new HashSet<>()).add(node2.getId());
            adjacencyMap.computeIfAbsent(node2.getId(), k -> new HashSet<>()).add(node1.getId());
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void removeNode(Circle nodeShape) {
        try {
            Node node = findNode(nodeShape);
            nodes.remove(node);
            deleteConnectedEdges(node);
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void removeEdge(Line edgeShape) {
        try {
            Edge edge = findEdge(edgeShape);
            edges.remove(edge);
        } catch (EdgeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Text findLabel(Circle nodeShape) {
        try {
            Node node = findNode(nodeShape);
            return node.getLabel();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Text findLabel(Line edgeShape) {
        try {
            Edge edge = findEdge(edgeShape);
            return edge.getLabel();
        } catch (EdgeNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Node findNode(Circle nodeShape) throws NodeNotFoundException {
        for (Node node : nodes) {
            if (nodeShape == node.getShape()) {
                return node;
            }
        }
        throw new NodeNotFoundException();
    }

    private Node findNode(int nodeId) throws NodeNotFoundException {
        for (Node node : nodes) {
            if (node.getId() == nodeId) {
                return node;
            }
        }

        throw new NodeNotFoundException();
    }

    public String getNodeName(int nodeId) {
        try {
            return findNode(nodeId).getName();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Circle getNodeShape(int nodeId) {
        try {
            return findNode(nodeId).getShape();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Edge findEdge(Line edgeShape) throws EdgeNotFoundException {
        for (Edge edge : edges) {
            if (edgeShape == edge.getShape()) {
                return edge;
            }
        }

        throw new EdgeNotFoundException();
    }

    public List<Line> getAttachedEdges(Circle nodeShape) {
        try {
            List<Line> attachedEdges = new LinkedList<>();

            for (Edge edge : edges) {
                if (edge.getNodes().contains(findNode(nodeShape))) {
                    attachedEdges.add(edge.getShape());
                }
            }

            return attachedEdges;
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public boolean edgeExists(Circle nodeShape1, Circle nodeShape2) {
        try {
            int node1Id = findNode(nodeShape1).getId();
            int node2Id = findNode(nodeShape2).getId();

            Set<Integer> adjacentNodes = adjacencyMap.get(node1Id);

            return adjacentNodes != null && adjacentNodes.contains(node2Id);
        } catch (NodeNotFoundException e) {
            return false;
        }
    }

    public void updateLabel(Text label, String newText) {
        if (newText == null) {
            newText = "Node";
        }

        for (Edge edge : edges) {
            if (label == edge.getLabel()) {
                edge.setWeight(newText);
                return;
            }
        }

        for (Node node : nodes) {
            if (label == node.getLabel()) {
                node.setName(newText);
                return;
            }
        }
    }

    private void deleteConnectedEdges(Node node) {
        edges.removeIf(edge -> node == edge.getNode1() || node == edge.getNode2());
    }

    public Map<String[], String[]> runDijkstra() {
        if (startNode != null) {
            dijkstra.updateNodes(nodes);

            return dijkstra.run(startNode);
        }

        return Collections.emptyMap();
    }

    public void clear() {
        nodes.clear();
        edges.clear();
        nodeShapes.clear();
        edgeShapes.clear();
        startNode = null;
    }

    public void saveNodes() {
        List<NodePO> nodeList = nodes.stream()
                .map(node -> new NodePO(node.getId(), node.getName(), node.getShape().getCenterX(), node.getShape().getCenterY()))
                .collect(Collectors.toList());
        JSONArray jsonArray = new JSONArray(new ArrayList<>(nodeList));
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter("node_database.json"));
            String s = JSON.toJSONString(jsonArray, SerializerFeature.IgnoreNonFieldGetter);
            out.write(s);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveEdges() {

        List<EdgePO> edgePOList = edges.stream()
                .map(edge -> new EdgePO(edge.getId(), edge.getNode1().getId(), edge.getNode2().getId(), edge.getWeight()))
                .collect(Collectors.toList());

        JSONArray jsonArray = new JSONArray(new ArrayList<>(edgePOList));
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter("edge_database.json"));
            out.write(jsonArray.toString());
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadNodes() {
        nodes.clear();
        try {
            String s = Files.readString(Paths.get("node_database.json"));
            List<NodePO> nodeList = JSON.parseArray(s, NodePO.class);
            for (NodePO nodePO : nodeList) {
                Circle nodeShape = new Circle(nodePO.getxCoord(), nodePO.getyCoord(), 50);
                nodeShape.setFill(Color.LIGHTBLUE);

                nodeShape.setOnMouseEntered(event -> {
                    nodeShape.setStroke(Color.GRAY);
                    nodeShape.setStrokeWidth(1);
                });

                nodeShape.setOnMouseExited(event -> {
                    nodeShape.setStroke(null);
                });

                Text label = new Text(nodePO.getName());
                label.setFont(Font.font(24));
                label.setMouseTransparent(true);

                label.xProperty().bind(nodeShape.centerXProperty().subtract(nodeShape.getRadius() / 2));
                label.yProperty().bind(nodeShape.centerYProperty().add(nodeShape.getRadius() / 8));

                nodes.add(new Node(nodePO.getId(), label, nodeShape));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadEdges() {
        edges.clear();
        try {
            String s = Files.readString(Paths.get("edge_database.json"));
            List<EdgePO> edgePOS = JSON.parseArray(s, EdgePO.class);
            for (EdgePO edgePO : edgePOS) {
                Node edgeNode1 = new Node();
                Node edgeNode2 = new Node();
                try {
                    edgeNode1 = findNode(edgePO.getFrontNodeId());
                    edgeNode2 = findNode(edgePO.getEndNodeId());
                } catch (NodeNotFoundException e) {
                    e.printStackTrace();
                }
                Line edgeShape = new Line();
                edgeShape.setStrokeWidth(3);
                edgeShape.setStroke(Color.GRAY);

                edgeShape.startXProperty().bind(edgeNode1.getShape().centerXProperty());
                edgeShape.startYProperty().bind(edgeNode1.getShape().centerYProperty());
                edgeShape.endXProperty().bind(edgeNode2.getShape().centerXProperty());
                edgeShape.endYProperty().bind(edgeNode2.getShape().centerYProperty());

                Text label = new Text(String.valueOf(edgePO.getWeight()));
                label.setFont(Font.font(24));

                label.xProperty().bind(edgeShape.startXProperty().add(edgeShape.endXProperty()).divide(2));
                label.yProperty().bind(edgeShape.startYProperty().add(edgeShape.endYProperty()).divide(2));

                edges.add(new Edge(edgePO.getId(), edgeNode1, edgeNode2, label, edgeShape));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Circle, Text> getNodesAndLabels() {
        Map<Circle, Text> nodesAndLabels = new HashMap<>();

        for (Node node : nodes) {
            nodesAndLabels.put(node.getShape(), node.getLabel());
        }

        return nodesAndLabels;
    }

    public Map<Line, Text> getEdgesAndLabels() {
        Map<Line, Text> edgesAndLabels = new HashMap<>();

        for (Edge edge : edges) {
            edgesAndLabels.put(edge.getShape(), edge.getLabel());
        }

        return edgesAndLabels;
    }

    public boolean isActive(Circle nodeShape) {
        try {
            Node node = findNode(nodeShape);
            return node.isActive();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isActive(Line edgeShape) {
        try {
            Edge edge = findEdge(edgeShape);
            return edge.isActive();
        } catch (EdgeNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateEdgeWeight(Line source, String text) {
        try {
            Edge edge = findEdge(source);
            edge.setWeight(text);
        } catch (EdgeNotFoundException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void updateNodeLabel(Circle source, String text) {
        try {
            Node node = findNode(source);
            node.setName(text);
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public double getRightmostPosition() {
        double maxX = Double.MIN_VALUE;
        for (Node node : nodes) {
            Circle nodeShape = node.getShape();
            if (nodeShape.getCenterX() > maxX) {
                maxX = nodeShape.getCenterX();
            }
        }
        return maxX;
    }
}
