package project.model.dijkstra.po;

public class EdgePO {
	private int id;
	private int frontNodeId;

	private int endNodeId;

	private int weight;

	public EdgePO() {
	}

	public EdgePO(int id, int id1, int id2, int weight) {
		this.id = id;
		this.frontNodeId = id1;
		this.endNodeId = id2;
		this.weight = weight;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFrontNodeId() {
		return frontNodeId;
	}

	public void setFrontNodeId(int frontNodeId) {
		this.frontNodeId = frontNodeId;
	}

	public int getEndNodeId() {
		return endNodeId;
	}

	public void setEndNodeId(int endNodeId) {
		this.endNodeId = endNodeId;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}