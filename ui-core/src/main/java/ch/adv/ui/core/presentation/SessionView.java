package ch.adv.ui.core.presentation;

import ch.adv.ui.core.presentation.sessionviewmodel.*;
import ch.adv.ui.core.presentation.util.ReplaySliderStringConverter;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * The JavaFX Controller class for session-view.fxml. Initializes the view
 * and holds bindings to the {@link StateViewModel}.
 */
public class SessionView {

    private static final double NO_MARGIN_ANCHOR = 0.0;
    private static final Logger logger = LoggerFactory.getLogger(SessionView
            .class);
    private final FontAwesomeIconView pauseIcon;
    private final FontAwesomeIconView playIcon;
    private final SteppingViewModel steppingViewModel;
    private final ReplayViewModel replayViewModel;
    private final StateViewModel stateViewModel;
    @FXML
    private Button replayButton;
    @FXML
    private Button cancelReplayButton;
    @FXML
    private Button stepFirstButton;
    @FXML
    private Button stepBackwardButton;
    @FXML
    private Button stepForwardButton;
    @FXML
    private Button stepLastButton;
    @FXML
    private Slider replaySpeedSlider;
    @FXML
    private ProgressBar stepProgressBar;
    @FXML
    private Label currentIndex;
    @FXML
    private Label maxIndex;
    @FXML
    private AnchorPane contentPane;
    @FXML
    private TextArea snapshotDescription;
    @FXML
    private Label replaySpeedSliderLabel;
    @Inject
    private ReplayController replayController;
    @Inject
    private ReplaySliderStringConverter replaySliderStringConverter;

    @Inject
    public SessionView(SteppingViewModelFactory steppingViewModelFactory,
                       ReplayViewModelFactory replayViewModelFactory,
                       StateViewModel stateViewModel,
                       FontAwesomeIconView fontAwesomePauseView,
                       FontAwesomeIconView fontAwesomePlayView) {
        logger.debug("Construct SessionView");
        this.stateViewModel = stateViewModel;
        this.steppingViewModel = steppingViewModelFactory.create(
                stateViewModel);
        this.replayViewModel = replayViewModelFactory.create(
                stateViewModel, steppingViewModel);

        this.pauseIcon = fontAwesomePauseView;
        pauseIcon.setIcon(FontAwesomeIcon.PAUSE);

        this.playIcon = fontAwesomePlayView;
        playIcon.setIcon(FontAwesomeIcon.PLAY);

    }

    /**
     * Will be called once on a controller when the content of
     * its associated document has been completely loaded
     */
    @FXML
    public void initialize() {
        logger.debug("Initialize SessionView");
        setButtonActions();
        bindButtonDisableProperties();
        bindReplayIcons();
        bindI18nStrings();
        setTooltips();

        replaySpeedSlider.disableProperty().bind(stateViewModel
                .getSpeedSliderDisableProperty());

        replayController.getReplaySpeedProperty()
                .bindBidirectional(replaySpeedSlider.valueProperty());
        replaySpeedSlider.setLabelFormatter(replaySliderStringConverter);
        //TODO: manage to change strings when changing language
        I18n.localeProperty().addListener((e, o, n) -> replaySpeedSlider
                .setLabelFormatter(new ReplaySliderStringConverter()));

        stepProgressBar.progressProperty().bind(stateViewModel
                .getProgressProperty());

        currentIndex.textProperty().bind(stateViewModel
                .getCurrentIndexStringProperty());
        maxIndex.textProperty().bind(stateViewModel
                .getMaxIndexStringProperty());

        setCurrentSnapshotAsContent();
        stateViewModel.getCurrentSnapshotPaneProperty().addListener(
                (event, oldV, newV) -> setCurrentSnapshotAsContent());

        this.snapshotDescription.textProperty()
                .bindBidirectional(stateViewModel
                        .getCurrentSnapshotDescriptionProperty());
    }

    private void setTooltips() {
        stepFirstButton.setTooltip(I18n
                .tooltipForKey("tooltip.snapshot-bar.step_first"));
        stepBackwardButton.setTooltip(I18n
                .tooltipForKey("tooltip.snapshot-bar.step_backward"));
        stepForwardButton.setTooltip(I18n
                .tooltipForKey("tooltip.snapshot-bar.step_forward"));
        stepLastButton.setTooltip(I18n
                .tooltipForKey("tooltip.snapshot-bar.step_last"));
        cancelReplayButton
                .setTooltip(I18n.tooltipForKey("tooltip.snapshot-bar.cancel"));
        replayButton
                .setTooltip(I18n.tooltipForKey("tooltip.snapshot-bar.play"));
    }

    private void bindI18nStrings() {
        replaySpeedSliderLabel.textProperty()
                .bind(I18n.createStringBinding("title.speed"));
    }

    private void setButtonActions() {
        replayButton.setOnAction(e -> handleReplayButtonClicked());
        cancelReplayButton.setOnAction(e -> handleCancelReplayButtonClicked());
        stepFirstButton.setOnAction(e -> handleStepFirstButtonClicked());
        stepBackwardButton.setOnAction(e -> handleStepBackwardButtonClicked());
        stepForwardButton.setOnAction(e -> handleStepForwardButtonClicked());
        stepLastButton.setOnAction(e -> handleStepLastButtonClicked());
    }

    private void bindReplayIcons() {
        this.cancelReplayButton.disableProperty().bind(
                stateViewModel.getReplayingProperty().not());

        stateViewModel.getReplayingProperty().addListener(
                (ObservableValue<? extends Boolean> observable,
                 Boolean oldValue, Boolean newValue) -> {
                    if (newValue) {
                        replayButton.setGraphic(pauseIcon);
                        replayButton.setTooltip(I18n
                                .tooltipForKey("tooltip.snapshot-bar.pause"));
                    } else {
                        replayButton.setGraphic(playIcon);
                        replayButton.setTooltip(I18n
                                .tooltipForKey("tooltip.snapshot-bar.play"));
                    }
                });
    }

    private void bindButtonDisableProperties() {
        stepFirstButton.disableProperty().bind(stateViewModel
                .getStepButtonState().getStepFirstBtnDisableProperty());
        stepBackwardButton.disableProperty().bind(stateViewModel
                .getStepButtonState().getStepBackwardBtnDisableProperty());
        stepForwardButton.disableProperty().bind(stateViewModel
                .getStepButtonState().getStepForwardBtnDisableProperty());
        stepLastButton.disableProperty().bind(stateViewModel
                .getStepButtonState().getStepLastBtnDisableProperty());
    }

    private void setCurrentSnapshotAsContent() {
        Pane currentSnapshot = stateViewModel.getCurrentSnapshotPaneProperty()
                .get();
        this.contentPane.getChildren().clear();
        this.contentPane.getChildren().add(currentSnapshot);
        setAnchors(currentSnapshot);
    }

    private void setAnchors(final Pane currentSnapshot) {
        AnchorPane.setBottomAnchor(currentSnapshot, NO_MARGIN_ANCHOR);
        AnchorPane.setTopAnchor(currentSnapshot, NO_MARGIN_ANCHOR);
        AnchorPane.setLeftAnchor(currentSnapshot, NO_MARGIN_ANCHOR);
        AnchorPane.setRightAnchor(currentSnapshot, NO_MARGIN_ANCHOR);
    }

    private void handleReplayButtonClicked() {
        if (stateViewModel.getReplayingProperty().get()) {
            replayViewModel.pauseReplay();
        } else {
            replayViewModel.replay();
        }
    }

    private void handleCancelReplayButtonClicked() {
        replayViewModel.cancelReplay();
    }

    private void handleStepFirstButtonClicked() {
        steppingViewModel.navigateSnapshot(Navigate.FIRST);
    }

    private void handleStepBackwardButtonClicked() {
        steppingViewModel.navigateSnapshot(Navigate.BACKWARD);
    }

    private void handleStepForwardButtonClicked() {
        steppingViewModel.navigateSnapshot(Navigate.FORWARD);
    }

    private void handleStepLastButtonClicked() {
        steppingViewModel.navigateSnapshot(Navigate.LAST);
    }

}
