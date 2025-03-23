package project.view.buttons;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import project.view.ComparisonChart;
import project.view.util.Util;

import java.util.Objects;

public class ChartButtons {
	private static final Font FONT = new Font(15);
	private static final int MIN_NODES = 2;
	private static final int MAX_NODES = 200;

	private final ComparisonChart comparisonChart;
	private final Spinner<Integer> totalNodesSpinner;
	private final TextField totalNodesTextField;
	private final VBox totalNodesBox;
	private final ComboBox<Integer> stepSizeComboBox;
	private final VBox stepSizeBox;
	private final Button createChartButton;

	public ChartButtons(ComparisonChart comparisonChart) {
		this.comparisonChart = comparisonChart;
		this.totalNodesSpinner = createTotalNodesSpinner();
		this.totalNodesTextField = new TextField("100");
		this.totalNodesBox = createTotalNodesBox();
		this.stepSizeComboBox = createStepSizeComboBox();
		this.stepSizeBox = createStepSizeBox();
		this.createChartButton = createChartButton();

		setupButtonActions();
	}

	private Spinner<Integer> createTotalNodesSpinner() {
		SpinnerValueFactory.IntegerSpinnerValueFactory spinnerValueFactory =
				new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 1000, 100, 10);
		return new Spinner<>(spinnerValueFactory);
	}

	private VBox createTotalNodesBox() {
		Label totalNodesLabel = new Label("Nodes");
		totalNodesLabel.setFont(FONT);
		VBox totalNodesBox = new VBox(totalNodesLabel, totalNodesTextField);
		VBox.setVgrow(totalNodesLabel, Priority.ALWAYS);
		VBox.setVgrow(totalNodesTextField, Priority.ALWAYS);
		return totalNodesBox;
	}

	private ComboBox<Integer> createStepSizeComboBox() {
		ComboBox<Integer> comboBox = new ComboBox<>();
		comboBox.getItems().addAll(5, 10, 20, 50, 100);
		comboBox.setValue(10);
		return comboBox;
	}

	private VBox createStepSizeBox() {
		Label stepSizeLabel = new Label("Step Size\n(5 - 100)");
		stepSizeLabel.setFont(FONT);
		VBox stepSizeBox = new VBox(stepSizeLabel, stepSizeComboBox);
		VBox.setVgrow(stepSizeLabel, Priority.ALWAYS);
		return stepSizeBox;
	}

	private Button createChartButton() {
		Button button = new Button("\u200E");
		ImageView runIconView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/run.png"))));
		runIconView.setFitHeight(12);
		runIconView.setFitWidth(12);
		button.setGraphic(runIconView);
		return button;
	}

	private void setupButtonActions() {
		createChartButton.setOnAction(actionEvent -> createChart());
	}

	private void createChart() {
		int totalNodes = parseTotalNodes();
		int stepSize = stepSizeComboBox.getValue();

		if (totalNodes < MIN_NODES) {
			Util.displayErrorMessage("Invalid number", "Number of nodes must be greater than 1");
			return;
		}

		if (totalNodes > MAX_NODES) {
			if (Util.displayWarningDialog("Warning", "Creating a chart with more than 200 nodes may take a long time. Are you sure you want to continue?")) {
				generateChart(totalNodes, stepSize);
			}
		} else {
			generateChart(totalNodes, stepSize);
		}
	}

	private int parseTotalNodes() {
		try {
			return Integer.parseInt(totalNodesTextField.getText());
		} catch (NumberFormatException e) {
			Util.displayErrorMessage("Invalid Input", "Please enter a valid number for nodes.");
			return -1;  // Indicating an error
		}
	}

	private void generateChart(int totalNodes, int stepSize) {
		comparisonChart.clearChart();
		comparisonChart.startAlgorithm(totalNodes, stepSize);
	}

	public VBox getTotalNodesBox() {
		return totalNodesBox;
	}

	public VBox getStepSizeBox() {
		return stepSizeBox;
	}

	public Button getCreateChartButton() {
		return createChartButton;
	}
}
