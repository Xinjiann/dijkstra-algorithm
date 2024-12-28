package project.view.buttons;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import project.view.Animation;

import java.util.Objects;

public class AnimationButtons
{
	private Button forwardButton;
	private Button backButton;
	private Button stopButton;

	private final Buttons buttons;
	private final Animation animation;

	public AnimationButtons(Buttons buttons, Animation animation)
	{
		this.buttons = buttons;
		this.animation = animation;
		setupButtons();
		setupButtonActions();
	}

	private void setupButtons()
	{
		// Button 1
		forwardButton = new Button("\u200E");
		ImageView forwardIconView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/right.png"))));
		forwardIconView.setFitHeight(12);
		forwardIconView.setFitWidth(12);
		forwardButton.setGraphic(forwardIconView);

		// Button 2
		backButton = new Button("\u200E");
		ImageView backIconView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/left.png"))));
		backIconView.setFitHeight(12);
		backIconView.setFitWidth(12);
		backButton.setGraphic(backIconView);

		// Button 3
		stopButton = new Button("\u200E");
		ImageView stopIconView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/stop.png"))));
		stopIconView.setFitHeight(12);
		stopIconView.setFitWidth(12);
		stopButton.setGraphic(stopIconView);
	}

	private void setupButtonActions()
	{
		// Button 1
		forwardButton.setOnAction(actionEvent -> forward());

		// Button 2
		backButton.setOnAction(actionEvent -> backward());

		// Button 3
		stopButton.setOnAction(actionEvent -> stopAnimation());
	}

	private void forward()
	{
		animation.forward();
	}

	private void backward()
	{
		animation.backward();
	}

	private void stopAnimation()
	{
		buttons.stopAnimation();
	}

	public Button getForwardButton()
	{
		return forwardButton;
	}

	public Button getBackButton()
	{
		return backButton;
	}

	public Button getStopButton()
	{
		return stopButton;
	}
}
