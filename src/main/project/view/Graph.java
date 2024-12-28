package project.view;

import javafx.animation.ScaleTransition;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import project.controller.Controller;
import project.view.buttons.MainButtons;
import project.view.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Graph
{
	private Pane graphPane;
	private ContextMenu contextMenu;
	private SeparatorMenuItem separator;
	private MenuItem addNodeMenuItem;
	private MenuItem removeNodeMenuItem;
	private MenuItem addEdgeMenuItem;
	private MenuItem removeEdgeMenuItem;
	private MenuItem editEdgeMenuItem;
	private MenuItem editNodeMenuItem;
	private MenuItem runMenuItem;
	private MenuItem clearMenuItem;
	private MenuItem loadDataMenuItem;
	private MenuItem saveDataMenuItem;
	private MenuItem selectStartNodeMenuItem;
	private final TextField inputBox;
	private final Stage popupStage;
	private final List<Text> nodeLabels;
	private final List<Circle> selectedNodes;
	private Line selectedEdge;
	private Circle selectedStartNode;
	private MainButtons mainButtons;
	private boolean isAnimationActive = false;
	private static final Color selectedNodeColour = Color.DARKSEAGREEN;
	private static final Color unselectedNodeColour = Color.LIGHTBLUE;
	private static final Color selectedStartNodeColour = Color.GREEN;
	private static final Color PressedNodeColour = Color.POWDERBLUE;
	private static final Color selectedEdgeColour = Color.PALEVIOLETRED;
	private static final Color unselectedEdgeColour = Color.GRAY;
	private static final Color activeColour = Color.CORNFLOWERBLUE;
	private Object source;
	private final Controller controller;
	public Graph(Controller controller)
	{
		this.controller = controller;
		nodeLabels = new ArrayList<>();
		selectedNodes = new ArrayList<>();
		inputBox = new TextField();
		popupStage = new Stage();
		popupStage.setScene(new Scene(inputBox, 300, 50));

		setupContextMenu();
		setupGraphPane();
	}

	public void setMainButtons(MainButtons mainButtons){
		this.mainButtons = mainButtons;
	}

	// Right-click menu
	private void setupContextMenu()
	{
		contextMenu = new ContextMenu();
		separator = new SeparatorMenuItem();

		addNodeMenuItem = new MenuItem("Add Node");
		removeNodeMenuItem = new MenuItem("Remove Node");
		addEdgeMenuItem = new MenuItem("Add Edge");
		removeEdgeMenuItem = new MenuItem("Remove Edge");
		selectStartNodeMenuItem = new MenuItem("Set As Start Node");
		editEdgeMenuItem = new MenuItem("Edit Edge");
		editNodeMenuItem = new MenuItem("Edit Node");
		runMenuItem = new MenuItem("Run");
		clearMenuItem = new MenuItem("Clear All Nodes");
		loadDataMenuItem = new MenuItem("Load Data");
		saveDataMenuItem = new MenuItem("Save Data");
	}

	private void setupMenuItemActions(Object source)
	{
		addNodeMenuItem.setOnAction(actionEvent ->
		{
			if (controller.getNodes().size() < 20)
			{
				addNode();
			}
			else
			{
				Util.displayErrorMessage("MAX LIMIT Reached",
						"You can only have up to 20 nodes.");
			}
		});
		removeNodeMenuItem.setOnAction(actionEvent -> removeNode((Circle) source));
		addEdgeMenuItem.setOnAction(actionEvent -> addEdge());
		removeEdgeMenuItem.setOnAction(actionEvent -> removeEdge((Line) source));
		selectStartNodeMenuItem.setOnAction(actionEvent -> selectStartNode((Circle) source));
		editEdgeMenuItem.setOnAction(actionEvent -> editEdge((Line) source));
		editNodeMenuItem.setOnAction(actionEvent -> editNode((Circle) source));
		runMenuItem.setOnAction(actionEvent -> mainButtons.run());
		clearMenuItem.setOnAction(actionEvent -> mainButtons.reset());
		loadDataMenuItem.setOnAction(actionEvent -> mainButtons.load());
		saveDataMenuItem.setOnAction(actionEvent -> mainButtons.save());
	}

	private void editNode(Circle source) {
		popupStage.setTitle("Edit Node");
		popupStage.show();

		inputBox.setOnAction(event ->
		{
			controller.updateNodeLabel(source, inputBox.getText());
			popupStage.close();
			inputBox.clear();
		});
	}

	private void editEdge(Line source) {

		popupStage.setTitle("Edit Edge");
		popupStage.show();

		inputBox.setOnAction(event ->
		{
			controller.updateLineWight(source, inputBox.getText());
			popupStage.close();
			inputBox.clear();
		});
	}

	private void setupLeftClick()
	{
		graphPane.setOnMouseClicked(mouseEvent ->
		{
			if (mouseEvent.getButton() == MouseButton.PRIMARY) {
				contextMenu.hide();
			}

			if (isAnimationActive)
			{
				return;
			}

			source = mouseEvent.getPickResult().getIntersectedNode();

			// Clear selections
			if (!(source instanceof Circle) && !(source instanceof Line))
			{
				clearSelectedNodes();

				if (selectedEdge != null)
				{
					clearSelectedEdge();
				}
			}

			handleDoubleClick(mouseEvent);
		});
	}

	private void setupRightClick() {
		graphPane.setOnContextMenuRequested(event ->
		{
			if (isAnimationActive)
			{
				return;
			}

			source = event.getPickResult().getIntersectedNode();
			setupMenuItemActions(source);
			contextMenu.getItems().clear();
			addBounceEffect();
			if (source instanceof Circle)
			{
				if (selectedStartNode != source)
				{
					contextMenu.getItems().add(selectStartNodeMenuItem);
				}

				contextMenu.getItems().add(removeNodeMenuItem);

				if (selectedNodes.size() == 2)
				{
					contextMenu.getItems().add(separator);
					contextMenu.getItems().add(addEdgeMenuItem);
				}
				contextMenu.getItems().add(editNodeMenuItem);
			}
			else if (source instanceof Line)
			{
				contextMenu.getItems().add(removeEdgeMenuItem);
				contextMenu.getItems().add(editEdgeMenuItem);
			}
			else
			{
				contextMenu.getItems().add(addNodeMenuItem);
				contextMenu.getItems().add(runMenuItem);
				contextMenu.getItems().add(clearMenuItem);
				contextMenu.getItems().add(loadDataMenuItem);
				contextMenu.getItems().add(saveDataMenuItem);
			}

			contextMenu.show(graphPane, event.getScreenX(), event.getScreenY());
		});
	}

	private void addBounceEffect() {
		ScaleTransition st = new ScaleTransition(Duration.millis(200), contextMenu.getScene().getRoot());
		st.setFromX(0.9);
		st.setFromY(0.9);
		st.setToX(1.0);
		st.setToY(1.0);
		st.setCycleCount(1);
		st.setAutoReverse(false);
		st.play();
	}

	// Handles events in the graph pane
	private void setupGraphPane()
	{
		graphPane = new Pane();

		graphPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
				BorderWidths.DEFAULT)));

		// Left click
		setupLeftClick();

		// Right click
		setupRightClick();
	}

	private void handleDoubleClick(MouseEvent mouseEvent)
	{
		if (isAnimationActive) {
			return;
		}

		if (mouseEvent.getClickCount() == 2)
		{
			if (source instanceof Circle) {
				Circle circle = (Circle) source;
				if (selectedStartNode == circle) {
					return;
				}
				if (selectedNodes.contains(circle)) {
					circle.setFill(unselectedNodeColour);
					selectedNodes.remove(circle);
					circle.setStroke(null);
				} else {
					circle.setStroke(Color.DARKGRAY);
					circle.setStrokeWidth(2);
					selectNode((Circle) source);
				}
			}
			else if (source instanceof Text)
			{
				popupStage.setTitle("Edit Label");
				popupStage.show();

				inputBox.setOnAction(event ->
				{
					controller.updateLabel((Text) source, inputBox.getText());
					popupStage.close();
					inputBox.clear();
				});
			}
		}
	}

	private void addNode()
	{

		Circle node = new Circle(graphPane.getWidth()/2 + controller.getNodes().size() * 15, graphPane.getHeight()/2 - controller.getNodes().size() * 15, 50);
		node.setFill(unselectedNodeColour);

		node.setOnMouseEntered(event -> {
			node.setStroke(Color.GRAY);
			node.setStrokeWidth(1);
		});

		node.setOnMouseExited(event -> {
			if (!selectedNodes.contains(node)) {
				node.setStroke(null);
			}
		});

		Text label = new Text("Node" + (controller.getExistingNodeNum() == 0 ? "" : "" + controller.getExistingNodeNum()));
		label.setFont(Font.font(24));

		label.setMouseTransparent(true);


		label.xProperty().bind(node.centerXProperty().subtract(label.getLayoutBounds().getWidth()/2 - 1));
		label.yProperty().bind(node.centerYProperty().add(label.getLayoutBounds().getHeight()/4));

		setupNodeListeners(node);

		graphPane.getChildren().add(label);
		graphPane.getChildren().add(node);
		controller.addNode(label, node);
		nodeLabels.add(label);

		label.toFront();
	}

	public void refresh()
	{
		clearGraph();

		addEdgesFromController();
		addNodesFromController();
	}

	private void addNodesFromController()
	{
		Map<Circle, Text> nodesAndLabels = controller.getNodesAndLabels();

		for (Map.Entry<Circle, Text> entry : nodesAndLabels.entrySet())
		{
			setupNodeListeners(entry.getKey());

			graphPane.getChildren().add(entry.getValue());
			graphPane.getChildren().add(entry.getKey());
			nodeLabels.add(entry.getValue());

			entry.getValue().toFront();
		}
	}

	private void addEdgesFromController()
	{
		Map<Line, Text> edgesAndLabels = controller.getEdgesAndLabels();

		for (Map.Entry<Line, Text> entry : edgesAndLabels.entrySet())
		{
			setupEdgeListeners(entry.getKey());

			graphPane.getChildren().add(entry.getValue());
			graphPane.getChildren().add(entry.getKey());

			entry.getValue().toFront();
		}
	}

	private void setupNodeListeners(Circle node)
	{
		setupDraggableNode(node);

		// Setup more event listeners for node...
	}

	private void setupDraggableNode(Circle node)
	{
		AtomicReference<Point2D> mouseLocation = new AtomicReference<>();

		node.setOnMousePressed(mouseEvent ->
		{
			// Record the current mouse position
			mouseLocation.set(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
			if (!selectedNodes.contains(node)) {
				node.setFill(PressedNodeColour);
			}
			node.toFront();
			displayNodeLabels();
		});

		node.setOnMouseDragged(mouseEvent ->
		{
			// Calculate the distance the mouse was dragged
			double x = node.getCenterX() + mouseEvent.getSceneX() - mouseLocation.get().getX();
			double y = node.getCenterY() + mouseEvent.getSceneY() - mouseLocation.get().getY();

			// Check if the circle is within the bounds of the pane
			if (x - node.getRadius() > 0 && x + node.getRadius() < graphPane.getWidth() &&
					y - node.getRadius() > 0 && y + node.getRadius() < graphPane.getHeight())
			{
				// Move the node by the drag distance
				node.setCenterX(x);
				node.setCenterY(y);

				// Update the current mouse position
				mouseLocation.set(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY()));
			}
		});

		node.setOnMouseReleased(mouseEvent ->
		{
			if (selectedNodes.contains(node))
			{
				node.setFill(selectedNodeColour);
			}
			else
			{
				node.setFill(unselectedNodeColour);
			}
		});
	}

	private void removeNode(Circle node)
	{
		try
		{
			Text label = controller.findLabel(node);
			graphPane.getChildren().remove(label);
		}
		catch (NullPointerException e)
		{
			throw new NullPointerException("Node does not have a label");
		}

		removeAttachedEdges(node);

		graphPane.getChildren().remove(node);
		controller.removeNode(node);
	}

	private void removeAttachedEdges(Circle node)
	{
		List<Line> attachedEdges = controller.getAttachedEdges(node);

		for (Line edge : attachedEdges)
		{
			removeEdge(edge);
		}
	}

	private void addEdge()
	{
		Circle node1 = selectedNodes.get(0);
		Circle node2 = selectedNodes.get(1);

		if (controller.edgeExists(node1, node2))
		{
			Util.displayErrorMessage("Edge exists", "An edge already exists between the selected nodes");
			return;
		}

		Line edge = new Line();
		edge.setStrokeWidth(3);
		edge.setStroke(unselectedEdgeColour);

		setupEdgeListeners(edge);

		edge.startXProperty().bind(node1.centerXProperty());
		edge.startYProperty().bind(node1.centerYProperty());
		edge.endXProperty().bind(node2.centerXProperty());
		edge.endYProperty().bind(node2.centerYProperty());

		Text label = new Text("1");
		label.setFont(Font.font(24));

		label.xProperty().bind(edge.startXProperty().add(edge.endXProperty()).divide(2));
		label.yProperty().bind(edge.startYProperty().add(edge.endYProperty()).divide(2));

		graphPane.getChildren().add(label);
		graphPane.getChildren().add(edge);

		controller.addEdge(node1, node2, label, edge);

		label.toFront();
		node1.toFront();
		node2.toFront();
		displayNodeLabels();
	}

	private void setupEdgeListeners(Line edge)
	{
		edge.setOnMouseClicked(event ->
		{
			if (event.getClickCount() == 2)
			{
				// The circle has been double-clicked
				selectEdge(edge);
			}
		});
	}

	private void removeEdge(Line edge)
	{
		try
		{
			Text label = controller.findLabel(edge);
			graphPane.getChildren().remove(label);
		}
		catch (NullPointerException e)
		{
			throw new NullPointerException("Edge does not have a label");
		}

		graphPane.getChildren().remove(edge);
		controller.removeEdge(edge);
	}

	// Selects a node by changing colour and adding to list
	private void selectNode(Circle node)
	{
		if (!controller.isActive(node)) {
			// Unselects first node selected. Only 2 nodes can be selected at once
			if (selectedNodes.size() == 2)
			{
				selectedNodes.get(0).setFill(unselectedNodeColour);
				selectedNodes.remove(0);
			}

			node.setFill(selectedNodeColour);
			selectedNodes.add(node);
		}
	}

	public Map<Circle, Line> highlight(Circle node, Map<Circle, Line> adjacentNodesAndEdges)
	{
		Map<Circle, Line> toggledEdges = new HashMap<>();

		setActive(node);

		for (Map.Entry<Circle, Line> entry : adjacentNodesAndEdges.entrySet())
		{
			Circle adjacentNode = entry.getKey();
			Line edge = entry.getValue();

			if (!controller.isActive(edge))
			{
				setActive(edge);
				toggledEdges.put(adjacentNode, edge);
			}
		}

		return toggledEdges;
	}

	public void unhighlight(Map<Circle, Line> toggledEdges)
	{
		for (Map.Entry<Circle, Line> entry : toggledEdges.entrySet())
		{
			Circle node = entry.getKey();
			Line edge = entry.getValue();

			setInactive(node);
			setInactive(edge);
		}
	}

	public void unhighlightAll(List<Map<Circle, Line>> nodesAndEdges)
	{
		for (Map<Circle, Line> nodeAndEdges : nodesAndEdges)
		{
			for (Circle node : nodeAndEdges.keySet())
			{
				setInactive(node);
			}

			for (Line edge : nodeAndEdges.values())
			{
				setInactive(edge);
			}
		}
	}

	public void setActive(Circle node)
	{
		controller.setActive(node);
		node.setFill(activeColour);
	}

	private void setActive(Line edge)
	{
		controller.setActive(edge);
		edge.setStroke(activeColour);
	}

	private void setInactive(Circle node)
	{
		controller.setInactive(node);
		node.setFill(unselectedNodeColour);
	}

	private void setInactive(Line edge)
	{
		if (edge == null)
		{
			return;
		}

		controller.setInactive(edge);
		edge.setStroke(unselectedEdgeColour);
	}

	// Deselects all nodes by changing colours and emptying list
	private void clearSelectedNodes()
	{
		// This avoids ConcurrentModificationException being thrown
		List<Circle> tempList = new ArrayList<>(selectedNodes);

		for (Circle node : tempList)
		{
			node.setFill(unselectedNodeColour);
			selectedNodes.remove(node);
		}
	}

	// Selects an edge by changing colour and adding to variable
	private void selectEdge(Line edge)
	{
		edge.setStroke(selectedEdgeColour);
		selectedEdge = edge;
	}

	// Deselects edge by changing colour and emptying variable
	private void clearSelectedEdge()
	{
		selectedEdge.setStroke(unselectedEdgeColour);
		selectedEdge = null;
	}

	// Selects a starting node by changing colour and adding to variable
	private void selectStartNode(Circle node)
	{
		if (selectedStartNode != null)
		{
			clearSelectedStartNode();
		}

		controller.setStartNode(node);
		node.setStroke(selectedStartNodeColour);
		node.setStrokeWidth(3);
		selectedStartNode = node;
	}

	// Deselects starting node by changing colour and emptying variable
	private void clearSelectedStartNode()
	{
		selectedStartNode.setStroke(null);
		selectedStartNode = null;
	}

	private void displayNodeLabels()
	{
		for (Text label: nodeLabels)
		{
			label.toFront();
		}
	}

	public Pane getPane()
	{
		return graphPane;
	}

	public void clearAll()
	{
		clearGraph();
		controller.clear();
	}

	private void clearGraph()
	{
		nodeLabels.clear();
		graphPane.getChildren().clear();
	}

	public void setAnimationStatus(boolean animationStatus)
	{
		this.isAnimationActive = animationStatus;
	}
}
