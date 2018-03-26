package ch.adv.ui.presentation;

import ch.adv.ui.logic.model.Session;
import ch.adv.ui.util.ResourceLocator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.Optional;


public class RootView {

    @FXML
    private MenuItem menuItemClose;

    @FXML
    private Button loadSessionButton;

    @FXML
    private ListView<Session> sessionListView;

    @FXML
    private TabPane sessionTabPane;

    @Inject
    private ResourceLocator resourceLocator;

    private final RootViewModel rootViewModel;
    private final FileChooser fileChooser = new FileChooser();

    private static final Logger logger = LoggerFactory.getLogger(RootView
            .class);

    @Inject
    public RootView(RootViewModel viewModel) {
        this.rootViewModel = viewModel;
        FileChooser.ExtensionFilter extensionFilter = new FileChooser
                .ExtensionFilter
                ("ADV files (*.adv)",
                        "*.adv");
        fileChooser.getExtensionFilters().add(extensionFilter);

    }

    @FXML
    public void initialize() {
        menuItemClose.setOnAction(e -> handleCloseMenuItemClicked());
        loadSessionButton.setOnAction(e -> handleLoadSessionClicked());
        sessionListView.setItems(rootViewModel.getAvailableSessions());
        sessionListView.setCellFactory(lv -> new CustomListCell());

        openNewTab();
    }

    private void handleCloseMenuItemClicked() {
        Platform.exit();
        System.exit(0);
    }

    private void openNewTab() {
        sessionListView.getSelectionModel().selectedItemProperty()
                .addListener(new CreateTabListener().invoke());

        rootViewModel.currentSessionProperty().addListener(new
                CreateTabListener()
                .invoke());
    }

    private void handleRemoveSessionClicked(final Session session) {
        logger.info("Removing session {} ({})", session.getSessionName(),
                session.getSessionId());
        rootViewModel.removeSession(session);
        Optional<Tab> existingTab = sessionTabPane.getTabs()
                .stream()
                .filter(t -> t.getText().equals(session.toString()))
                .findFirst();
        if (existingTab.isPresent()) {
            sessionTabPane.getTabs().remove(existingTab.get());
        }
    }

    private void handleLoadSessionClicked() {
        Window stage = sessionTabPane.getScene().getWindow();
        fileChooser.setTitle("Load Session File");
        File file = fileChooser.showOpenDialog(stage);

        if (file != null && file.exists()) {
            rootViewModel.loadSession(file);
        }
    }

    private void handleSaveSessionClicked(final Session session) {
        Window stage = sessionTabPane.getScene().getWindow();
        fileChooser.setTitle("Save Session File");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            String chosenFilePath = file.getPath();
            if (!chosenFilePath.endsWith(".adv")) {
                File fileWithExtension = new File(file.getPath() + ".adv");
                if (fileWithExtension.exists()) {
                    file = new File(chosenFilePath + "_copy.adv");
                } else {
                    file = fileWithExtension;
                }
            }

            rootViewModel.saveSession(file, session);
        }
    }

    private class CustomListCell extends ListCell<Session> {

        private HBox hbox = new HBox();
        private Label label = new Label("(empty)");
        private Pane pane = new Pane();
        private Label removeButton = new Label();
        private Label saveButton = new Label();

        private final FontAwesomeIconView saveIcon;
        private final FontAwesomeIconView removeIcon;

        private static final int ICON_SIZE = 16;
        private static final int SPACING = 12;

        CustomListCell() {
            super();

            this.removeIcon = new FontAwesomeIconView();
            removeIcon.setIcon(FontAwesomeIcon.TRASH_ALT);
            removeIcon.setGlyphSize(ICON_SIZE);
            removeButton.setGraphic(removeIcon);
            removeButton.setOnMouseClicked(e -> handleRemoveSessionClicked
                    (getItem()));

            this.saveIcon = new FontAwesomeIconView();
            saveIcon.setIcon(FontAwesomeIcon.FLOPPY_ALT);
            saveIcon.setGlyphSize(ICON_SIZE);
            saveButton.setGraphic(saveIcon);
            saveButton.setOnMouseClicked(e -> handleSaveSessionClicked
                    (getItem()));

            hbox.getChildren().addAll(label, pane, saveButton, removeButton);
            hbox.setSpacing(SPACING);
            hbox.setAlignment(Pos.CENTER);
            HBox.setHgrow(pane, Priority.ALWAYS);
        }


        /**
         * {@inheritDoc}
         *
         * @param session the new session to be displayed
         */
        @Override
        protected void updateItem(Session session, boolean empty) {
            super.updateItem(session, empty);
            setText(null);  // No text in label of super class
            if (empty || session == null) {
                label.setText("null");
                setGraphic(null);
            } else {
                label.setText(session.toString());
                setGraphic(hbox);
            }
        }
    }

    private class CreateTabListener {
        public ChangeListener<Session> invoke() {
            return (observable, oldValue, session) -> {

                if (session != null) {
                    Node sessionView = resourceLocator.load(ResourceLocator
                            .Resource.SESSION_VIEW_FXML);

                    Optional<Tab> existingTab = sessionTabPane.getTabs()
                            .stream()
                            .filter(t -> t.getText().equals(session
                                    .toString()))
                            .findFirst();
                    Tab newTab = existingTab.orElse(new Tab(session
                            .toString(), sessionView));

                    if (!existingTab.isPresent()) {
                        sessionTabPane.getTabs().add(newTab);
                    }

                    SingleSelectionModel<Tab> selectionModel =
                            sessionTabPane
                                    .getSelectionModel();
                    selectionModel.select(newTab);
                }
            };
        }
    }
}