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

public class ChartButtons
{
	private final ComparisonChart comparisonChart;

	private Spinner<Integer> totalNodesSpinner;

	private TextField textField;
	private VBox totalNodesBox;

	private ComboBox<Integer> stepSizeComboBox;
	private VBox stepSizeBox;

	private Button createChartButton;

	private final Font font = new Font(15);

	public ChartButtons(ComparisonChart comparisonChart)
	{
		this.comparisonChart = comparisonChart;

		setup();
		setupButtonActions();
	}

	public void setup()
	{
		Label totalNodesLabel = new Label("Nodes");
		totalNodesLabel.setFont(font);

		SpinnerValueFactory.IntegerSpinnerValueFactory spinnerValueFactory =
				new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 1000, 100, 10);
		textField = new TextField();
		textField.setText("100");

		totalNodesBox = new VBox(totalNodesLabel, textField);
		VBox.setVgrow(totalNodesLabel, Priority.ALWAYS);
		VBox.setVgrow(textField, Priority.ALWAYS);

		// stepSize
		Label stepSizeLabel = new Label("Step Size\n(5 - 100)");
		stepSizeLabel.setFont(font);

		stepSizeComboBox = new ComboBox<>();
		stepSizeComboBox.getItems().addAll(5, 10, 20, 50, 100);
		stepSizeComboBox.setValue(10);

		stepSizeBox = new VBox(stepSizeLabel, stepSizeComboBox);
		VBox.setVgrow(totalNodesLabel, Priority.ALWAYS);

		createChartButton = new Button("\u200E");
		// Load Icons
		ImageView runIconView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/run.png"))));
		runIconView.setFitHeight(12);
		runIconView.setFitWidth(12);
		createChartButton.setGraphic(runIconView);
	}

	public void setupButtonActions()
	{
		createChartButton.setOnAction(actionEvent -> createChart());
	}

	private void createChart()
	{
		int totalNodes = Integer.parseInt(textField.getText());
		int stepSize = stepSizeComboBox.getValue();

		if (totalNodes < 2)
		{
			Util.displayErrorMessage("Invalid number", "Number of nodes must be greater than 1");
			return;
		}


		if (totalNodes > 200)
		{
			if (Util.displayWarningDialog("Warning", "Creating a chart with more than 200 nodes may take a long time. " +
					"Are you sure you want to continue?"))
			{
				createChart(totalNodes, stepSize);
			}
		}
		else
		{
			createChart(totalNodes, stepSize);
		}
	}

	private void createChart(int totalNodes, int stepSize)
	{
		comparisonChart.clearChart();
		comparisonChart.startAlgorithm(totalNodes, stepSize);
	}

	public VBox getTotalNodesBox()
	{
		return totalNodesBox;
	}

	public VBox getStepSizeBox()
	{
		return stepSizeBox;
	}

	public Button getCreateChartButton()
	{
		return createChartButton;
	}
}
