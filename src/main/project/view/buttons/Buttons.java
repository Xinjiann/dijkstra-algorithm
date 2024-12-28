package project.view.buttons;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import project.controller.Controller;
import project.view.Animation;
import project.view.ComparisonChart;
import project.view.Graph;
import project.view.Table;
import project.view.util.Util;

import java.util.Objects;

public class Buttons
{
	private VBox buttonsPane;
	private final Pane mainPane;
	private final HBox animationBox;
	private final ComparisonChart comparisonChart;
	private final Separator separator = new Separator();
	private final Button runButton;
	private final Button resetButton;
	private final Button saveButton;
	private final Button loadButton;
	private final Button forwardButton;
	private final Button backButton;
	private final Button stopButton;
	private Button swapButton;
	private final VBox totalNodesBox;
	private final VBox stepSizeBox;
	private final Button createChartButton;
	private final Animation animation;
	private boolean isSwapped = false;
	private final Table table;
	private HBox fullPane;
	private final Controller controller;

	public Buttons(Controller controller, Graph graph, Table table, ComparisonChart comparisonChart, Pane mainPane,
	               HBox animationBox)
	{
		this.table = table;
		setupButtonsPane();
		this.controller = controller;
		separator.setOrientation(Orientation.HORIZONTAL);
		animation = new Animation(controller, graph, table);

		this.mainPane = mainPane;
		this.animationBox = animationBox;
		this.comparisonChart = comparisonChart;

		AnimationButtons animationButtons = new AnimationButtons(this, animation);
		MainButtons mainButtons = new MainButtons(this, controller, graph, table);
		ChartButtons chartButtons = new ChartButtons(comparisonChart);

		// Main Buttons
		this.runButton = mainButtons.getRunButton();
		this.resetButton = mainButtons.getResetButton();
		this.saveButton = mainButtons.getSaveButton();
		this.loadButton = mainButtons.getLoadButton();

		// Animation Buttons
		this.forwardButton = animationButtons.getForwardButton();
		this.backButton = animationButtons.getBackButton();
		this.stopButton = animationButtons.getStopButton();

		// Chart Buttons
		this.totalNodesBox = chartButtons.getTotalNodesBox();
		this.stepSizeBox = chartButtons.getStepSizeBox();
		this.createChartButton = chartButtons.getCreateChartButton();

		setupButtons();
		setupButtonsActions();
	}
	private void setupButtonsPane()
	{
		buttonsPane = new VBox();
		buttonsPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
				BorderWidths.FULL)));
	}

	public void setFullPane(HBox fullPane) {
		this.fullPane = fullPane;
	}

	public Pane getPane()
	{
		return buttonsPane;
	}

	private void setupButtons()
	{
		// Load Icons
		ImageView runIconView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/run.png"))));
		runIconView.setFitHeight(10);
		runIconView.setFitWidth(10);
		runButton.setGraphic(runIconView);

		// Load Icons
		ImageView resetIconView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/reset.png"))));
		resetIconView.setFitHeight(14);
		resetIconView.setFitWidth(14);
		resetButton.setGraphic(resetIconView);

		// save Icons
		ImageView saveIconView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/save.png"))));
		saveIconView.setFitHeight(12);
		saveIconView.setFitWidth(12);
		saveButton.setGraphic(saveIconView);

		// Load Icons
		ImageView loadIconView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/load2.png"))));
		loadIconView.setFitHeight(11);
		loadIconView.setFitWidth(11);
		loadButton.setGraphic(loadIconView);

		// Button 1
		swapButton = new Button("\u200E");
		// Load Icons
		ImageView swapIconView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/swap.png"))));
		swapIconView.setFitHeight(14);
		swapIconView.setFitWidth(14);
		swapButton.setGraphic(swapIconView);

		// Add buttons to buttonsPane
		buttonsPane.getChildren().addAll(runButton, resetButton, saveButton, loadButton);

		// Centre buttons
		buttonsPane.setAlignment(Pos.TOP_CENTER);
	}

	private void setupButtonsActions()
	{
		// Button 5
		swapButton.setOnAction(actionEvent -> swap());
	}

	private void swap()
	{
		if (isSwapped)
		{
			// Swap to animation window
			mainPane.getChildren().remove(comparisonChart.getPane());
			mainPane.getChildren().add(animationBox);

			buttonsPane.setSpacing(0);
			mainPane.setPrefSize(1600, 1200);
			buttonsPane.setPrefSize(10, 1200);
			// Swap buttons
			buttonsPane.getChildren().removeAll(totalNodesBox, stepSizeBox, createChartButton, swapButton);
			buttonsPane.getChildren().addAll(runButton, resetButton, saveButton, loadButton, swapButton);

			isSwapped = false;
		}
		else
		{
			// Swap to chart window
			mainPane.getChildren().remove(animationBox);
			mainPane.getChildren().add(comparisonChart.getPane());

			buttonsPane.setSpacing(10);
			mainPane.setPrefSize(1550, 1200);
			buttonsPane.setPrefSize(50, 1200);

			// Swap buttons
			buttonsPane.getChildren().removeAll(runButton, resetButton, saveButton, loadButton, swapButton);
			buttonsPane.getChildren().addAll(totalNodesBox, createChartButton, swapButton);

			isSwapped = true;
		}
	}

	public void startAnimation()
	{
		// Toggle button visibility
		buttonsPane.getChildren().removeAll(runButton, resetButton, saveButton, loadButton, swapButton);
		buttonsPane.getChildren().addAll(forwardButton, backButton, stopButton);

		double rightmostPosition = controller.getRightmostPosition();
		// Table
		ScrollPane tablePane = table.getPane();

		double tableWidth = Math.min(1100, Math.max(100, 1800-rightmostPosition));
		tablePane.setPrefSize(tableWidth, 800);

		fullPane.getChildren().add(tablePane);
		HBox.setHgrow(tablePane, Priority.ALWAYS);
		animation.start();
	}

	public void stopAnimation()
	{
		if (Util.displayOptionDialog("Stop", "Are you sure you want to stop the animation?"))
		{
			// Toggle button visibility
			buttonsPane.getChildren().removeAll(backButton, forwardButton, stopButton);
			buttonsPane.getChildren().addAll(runButton, resetButton, saveButton, loadButton, swapButton);

			fullPane.getChildren().remove(table.getPane());
//			fullPane.setPrefSize(1200, 1200);
			animation.stop();
		}
	}
}
