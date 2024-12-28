package project.model.dijkstra.po;

public class NodePO {
	private int id;
	private String name;

	private double xCoord;
	private double yCoord;

	public NodePO() {
	}

	public NodePO(int id, String name, double xCoord, double yCoord) {
		this.id = id;
		this.name = name;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getxCoord() {
		return xCoord;
	}

	public void setxCoord(double xCoord) {
		this.xCoord = xCoord;
	}

	public double getyCoord() {
		return yCoord;
	}

	public void setyCoord(double yCoord) {
		this.yCoord = yCoord;
	}
}