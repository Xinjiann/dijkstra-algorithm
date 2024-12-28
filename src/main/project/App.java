package project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import project.controller.Controller;
import project.view.ComparisonChart;
import project.view.Graph;
import project.view.Table;
import project.view.buttons.Buttons;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class App extends Application
{
	private final Graph graph;
	private final Table table;
	private final ComparisonChart comparisonChart;
	private Controller controller;
	private Scene scene;

    public App()
	{
		try
		{
			controller = new Controller();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}

		graph = new Graph(controller);
		table = new Table();
		comparisonChart = new ComparisonChart(controller);

		setupPanes();
	}

	private void setupPanes()
	{
		// Graph
		Pane graphPane = graph.getPane();
		graphPane.setPrefSize(1000, 1000);

		// Main Pane
		Pane mainPane = new Pane();

		// Animation Box
		HBox animationBox = new HBox(graphPane);
		HBox.setHgrow(graphPane, Priority.ALWAYS);
		animationBox.prefHeightProperty().bind(mainPane.heightProperty());
		animationBox.prefWidthProperty().bind(mainPane.widthProperty());

		// Chart
		Pane chartPane = comparisonChart.getPane();
		chartPane.prefHeightProperty().bind(mainPane.heightProperty());
		chartPane.prefWidthProperty().bind(mainPane.widthProperty());

		mainPane.getChildren().addAll(animationBox);
		mainPane.setPrefSize(1600, 1200);

		// Buttons
		Buttons buttons = new Buttons(controller, graph, table, comparisonChart, mainPane, animationBox);
		Pane buttonsPane = buttons.getPane();
		buttonsPane.setPrefSize(10, 1200);
		buttonsPane.setMaxHeight(100);
		buttonsPane.setMinHeight(100);

		// Full Pane
		HBox fullPane = new HBox(buttonsPane, mainPane);
		HBox.setHgrow(mainPane, Priority.ALWAYS);
		HBox.setHgrow(buttonsPane, Priority.ALWAYS);

		// Scene & CSS
		scene = new Scene(fullPane, 1600, 800);
		String cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
		scene.getStylesheets().add(cssPath);

		buttons.setFullPane(fullPane);
	}

	@Override
	public void start(Stage mainStage)
	{
		// Main Stage
		mainStage.setTitle("Dijkstra's Animation");
		mainStage.setScene(scene);

		mainStage.show();
		mainStage.setOnCloseRequest(event -> System.exit(0));
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}