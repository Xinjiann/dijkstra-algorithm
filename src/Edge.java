
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Edge
{
	private static int count = 1;
	private final int id;

	private final Node node1;
	private final Node node2;
	private int weight;
	private final Set<Node> nodes;

	private boolean isActive = false;

	private Edge(Node node1, Node node2)
	{
		this.node1 = node1;
		this.node2 = node2;
		this.nodes = new HashSet<>(Arrays.asList(node1, node2));

		this.id = count;
		count++;
	}

	public Edge(Node node1, Node node2, int weight)
	{
		this(node1, node2);
		this.weight = weight;
	}

	public int getId()
	{
		return id;
	}

	public Node getNode1()
	{
		return node1;
	}

	public Node getNode2()
	{
		return node2;
	}

	public int getWeight()
	{
		return weight;
	}

	public Set<Node> getNodes()
	{
		return nodes;
	}

	public void setActive()
	{
		isActive = true;
	}

	public void setInactive()
	{
		isActive = false;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public static void resetId()
	{
		count = 1;
	}

	public String toString()
	{
		return "{" + id + ": " + node1.toString() + ", " + node2.toString() + ", " + weight + "}";
	}
}
