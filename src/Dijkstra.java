
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Dijkstra extends Graph {
    private final NodeMap allNodes;
    private final NodeMap visitedNodes;
    private final Map<String[], String[]> steps = new LinkedHashMap<>();
    private int numOfNodes;

    public Dijkstra(List<Node> nodes, List<Edge> edges) {
        super(nodes, edges);
        this.allNodes = new NodeMap(nodes);
        this.visitedNodes = new NodeMap();
        this.numOfNodes = nodes.size();
    }

    public Map<String[], String[]> run(Node startingNode) {
        if (startingNode == null) {
            throw new IllegalArgumentException("Starting node cannot be null.");
        }

        if (!allNodes.contains(startingNode)) {
            throw new IllegalArgumentException("Starting node is not in the graph.");
        }

        if (!steps.isEmpty()) {
            steps.clear();
        }

        //输出的第一行，展示所有node
        instantiateSteps();

        //第一次遍历，和首节点相连的node的距离 = 连接edge的weight，和首节点不相连的node的距离 = -1
        findInitialLValues(startingNode);


        // while循环，判断遍历过的node的数量，如果都遍历完了，则退出循环
        while (visitedNodes.size() < allNodes.size()) {
            //更新step，把上一次计算出来的每个节点的最新值放到step中，用于输出
            updateSteps();

            //计算下一次的node（最近的node）
            Node nextNode = findNodeWithSmallestLValue();

            if (nextNode == null) {
                break;
            }

            //计算和下一个node相连的node，更新值（取最小值），或者如果下一个节点的值是-1（没遍历过），则赋最新的值
            findSubsequentLValues(nextNode);
        }

        //每次循环后都要更新一下step，由于while循环最后一次没有更新，所以出循环后再手动更新一次
        updateSteps();

        return steps;
    }

    private void instantiateSteps() {
        int index = 0;

        String[] nodeNames = new String[numOfNodes];

        for (Node node : allNodes.getNodes()) {
            nodeNames[index] = node.getName();
            index++;
        }

        steps.put(new String[]{"Tv"}, nodeNames);
    }

    private void updateSteps() {
        String[] nodeNames = new String[visitedNodes.size()];
        String[] lValues = new String[numOfNodes];

        //这一步是组装输出的左半部分 即遍历过的node
        int index = 0;
        for (Node node : visitedNodes.getNodes()) {
            nodeNames[index++] = String.valueOf(node.getName());
        }

        //这一步是组装输出的右半部分 即当前步骤中，每个节点的值，遍历过的=weight，没遍历过的=-1
        index = 0;
        for (Node node : allNodes.getNodes()) {
            lValues[index++] = allNodes.getLValueByNodeId(node.getId()).toString();
        }

        //每遍历一次 都往steos里添加一次，用于最后的输出
        steps.put(nodeNames, lValues);
    }

    private void findInitialLValues(Node startingNode) {
        for (Node node : allNodes.getNodes()) {
            Edge edge = findEdge(startingNode, node);

            if (node.equals(startingNode)) {
                allNodes.setLValueByNodeId(node.getId(), 0);
            } else if (edge != null) {
                allNodes.setLValueByNodeId(node.getId(), edge.getWeight());
            } else {
                allNodes.setLValueByNodeId(node.getId(), -1);
            }
        }

        flagNodeAsVisited(startingNode);
    }

    private void findSubsequentLValues(Node nextNode) {
        for (Node node : allNodes.getNodes()) {
            Edge edge = findEdge(nextNode, node);

            if (edge != null) {
                int oldLValue = allNodes.getLValueByNodeId(node.getId());
                int newLValue = allNodes.getLValueByNodeId(nextNode.getId()) + edge.getWeight();

                if (oldLValue > newLValue || oldLValue == -1) {
                    allNodes.setLValueByNodeId(node.getId(), newLValue);
                }
            }
        }

        flagNodeAsVisited(nextNode);
    }

    private void flagNodeAsVisited(Node node) {
        int lValue = allNodes.getLValueByNodeId(node.getId());
        visitedNodes.setLValueByNodeId(node.getId(), lValue);

        visitedNodes.addNode(node);
    }

    private Node findNodeWithSmallestLValue() {
        int smallestLValue = Integer.MAX_VALUE;

        Node node = null;
        int lValue;

        for (Node currentNode : allNodes.getNodes()) {
            if (visitedNodes.contains(currentNode)) {
                continue;
            }

            lValue = allNodes.getLValueByNodeId(currentNode.getId());

            if (lValue > 0 && lValue < smallestLValue) {
                smallestLValue = lValue;
                node = currentNode;
            }
        }

        return node;
    }

    public static void main(String[] args) {
        int count = 0;
        List<Node> nodeList = new ArrayList<>();
        List<Edge> edgeList = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        System.out.println("please define all the nodes, separate each name with a space(e.g. A B C D E)：");
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if ("0".equals(line)) {
                //创建Dijkstra实例，传入组装好的node和edge
                Dijkstra dijkstraAlgo = new Dijkstra(nodeList, edgeList);
                Map<String[], String[]> steps = dijkstraAlgo.run(nodeList.get(0));
                //定义输出文本（用于terminal显示）
                StringBuilder sb = new StringBuilder();
                sb.append("Dijkstra's Algorithm\n\n");

                for (Map.Entry<String[], String[]> entry : steps.entrySet()) {
                    sb.append(Arrays.toString(entry.getKey()))
                            .append(" : ")
                            .append(Arrays.toString(entry.getValue()))
                            .append("\n");
                }
                String output = sb.toString();
                Logger logger = Logger.getLogger(Dijkstra.class.getName());
                logger.info(output);
                break;
            }
            String[] arr = line.split(" ");
            if (count == 0) {
                for (String s : arr) {
                    nodeList.add(new Node(s));
                }
                System.out.println("all node: " + nodeList);
                System.out.println("please define all the nodes, you need to input three element, the name of the two nodes you need to connect, and the weight of the edge, separate each name with a space(e.g. A E 3)");
            } else {
                if (arr.length < 3) {
                    System.out.println("wrong input format");
                    continue;
                }
                String nameA = arr[0];
                if (!nodeList.stream().anyMatch(i -> i.getName().equals(nameA))) {
                    System.out.println("there is no node named " + nameA);
                    continue;
                }
                String nameB = arr[1];
                if (!nodeList.stream().anyMatch(i -> i.getName().equals(nameB))) {
                    System.out.println("there is no node named " + nameB);
                    continue;
                }
                String valueStr = arr[2];
                try {
                    edgeList.add(new Edge(getNdeByName(nodeList, nameA), getNdeByName(nodeList, nameB), Integer.parseInt(valueStr)));
                } catch (NumberFormatException e) {
                    System.out.println("Worong number format " + valueStr);
                    continue;
                }

                System.out.println("add edge success, please continue. when you finished, type 0 to tun the algorithm");
            }
            count ++;
        }
        scan.close();
    }

    private static Node getNdeByName(List<Node> nodeList, String name) {
        for (Node node : nodeList) {
            if (node.getName().equals(name)){
                return node;
            }
        }
        return null;
    }




    public static void main2(String[] args) {






        //定义node
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");
        Node nodeE = new Node("E");

        LinkedList<Node> testNodes = new LinkedList<>(Arrays.asList(
                nodeC, nodeB, nodeA, nodeE, nodeD));

        //定义edge
        LinkedList<Edge> testEdges = new LinkedList<>(Arrays.asList(
                new Edge(nodeA, nodeE, 2),
                new Edge(nodeE, nodeC, 3),
                new Edge(nodeE, nodeD, 7),
                new Edge(nodeC, nodeD, 3),
                new Edge(nodeB, nodeC, 4)
        ));

        //创建Dijkstra实例，传入组装好的node和edge
        Dijkstra dijkstraAlgo = new Dijkstra(testNodes, testEdges);

        Map<String[], String[]> steps = dijkstraAlgo.run(nodeA);

        //定义输出文本（用于terminal显示）
        StringBuilder sb = new StringBuilder();
        sb.append("Dijkstra's Algorithm\n\n");

        for (Map.Entry<String[], String[]> entry : steps.entrySet()) {
            sb.append(Arrays.toString(entry.getKey()))
                    .append(" : ")
                    .append(Arrays.toString(entry.getValue()))
                    .append("\n");
        }

        String output = sb.toString();
        Logger logger = Logger.getLogger(Dijkstra.class.getName());
        logger.info(output);
    }
}

