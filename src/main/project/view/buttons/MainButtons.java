package project.view.buttons;

import javafx.scene.control.Button;
import project.controller.Controller;
import project.view.Graph;
import project.view.Table;
import project.view.util.Util;


public class MainButtons
{
	private final Buttons buttons;
	private final Controller controller;
	private final Graph graph;
	private final Table table;

	private Button runButton;
	private Button resetButton;
	private Button saveButton;
	private Button loadButton;

	public MainButtons(Buttons buttons, Controller controller, Graph graph, Table table)
	{
		this.buttons = buttons;
		this.controller = controller;
		this.graph = graph;
		this.table = table;

		setupButtons();
		setupButtonActions();

		this.graph.setMainButtons(this);
	}

	public void setupButtons()
	{
		// Button 1
		runButton = new Button("\u200E");

		// Button 2
		resetButton = new Button("\u200E");

		// Button 3
		saveButton = new Button("\u200E");

		// Button 4
		loadButton = new Button("\u200E");
	}

	public void setupButtonActions()
	{
		// Button 1
		runButton.setOnAction(actionEvent -> run());

		// Button 2
		resetButton.setOnAction(actionEvent -> reset());

		// Button 3
		saveButton.setOnAction(actionEvent -> save());

		// Button 4
		loadButton.setOnAction(actionEvent -> load());
	}

	public void run()
	{
		if (controller.isReady())
		{
			startAnimation();
		}
		else
		{
			Util.displayErrorMessage("No start node selected!",
					"Right click on a node to select it as the start node.");
		}
	}

	private void startAnimation()
	{
		buttons.startAnimation();
	}

	public void reset()
	{
		if (Util.displayOptionDialog("Reset", "Are you sure you want to reset?"))
		{
			table.clearAll();
			graph.clearAll();
		}
	}

	public void save() {
		controller.saveNodes();
		controller.saveEdges();
		Util.displayInfoMessage("Success", "Saved to node_database.json and edge_database.json");
	}

	public void load()
	{
		controller.loadNodes();
		controller.loadEdges();
		table.clearAll();
		graph.refresh();
	}

	public Button getRunButton()
	{
		return runButton;
	}

	public Button getResetButton()
	{
		return resetButton;
	}

	public Button getSaveButton()
	{
		return saveButton;
	}

	public Button getLoadButton()
	{
		return loadButton;
	}
}
