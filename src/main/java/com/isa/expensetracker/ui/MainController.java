package com.isa.expensetracker.ui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainController {
    // --- FXML WIRING ---
    @FXML private BorderPane root;
    @FXML private VBox sidebar;
    @FXML private StackPane content;
    @FXML private ImageView imgToggle;
    @FXML private Label brandLabel, versionLabel, themeLabel;
    @FXML private Button btnDashboard, btnAdd, btnAnalytics, btnSettings;
    @FXML private StackPane themeToggle;

    // --- STATE & CONSTANTS ---
    private boolean isDarkMode = false;
    private boolean isSidebarExpanded = true;
    private List<Node> textNodesToManage;
    private SVGPath sunIcon, moonIcon;
    private static final double EXPANDED_W = 240;
    private static final double COLLAPSED_W = 80;
    private final PseudoClass COLLAPSED = PseudoClass.getPseudoClass("collapsed");
    private static final String SVG_SUN = "M7 3V0H9V3H7Z M9 13V16H7V13H9Z M11 8C11 9.65685 9.65685 11 8 11C6.34315 11 5 9.65685 5 8C5 6.34315 6.34315 5 8 5C9.65685 5 11 6.34315 11 8Z M0 9H3V7H0V9Z M16 7H13V9H16V7Z M3.75735 5.17157L1.63603 3.05025L3.05025 1.63603L5.17157 3.75735L3.75735 5.17157Z M12.2426 10.8284L14.364 12.9497L12.9497 14.364L10.8284 12.2426L12.2426 10.8284Z M3.05025 14.364L5.17157 12.2426L3.75735 10.8284L1.63603 12.9498L3.05025 14.364Z M12.9497 1.63604L10.8284 3.75736L12.2426 5.17158L14.364 3.05026L12.9497 1.63604Z";
    private static final String SVG_MOON = "M13 6V3M18.5 12V7M14.5 4.5H11.5M21 9.5H16M15.5548 16.8151C16.7829 16.8151 17.9493 16.5506 19 16.0754C17.6867 18.9794 14.7642 21 11.3698 21C6.74731 21 3 17.2527 3 12.6302C3 9.23576 5.02061 6.31331 7.92462 5C7.44944 6.05072 7.18492 7.21708 7.18492 8.44523C7.18492 13.0678 10.9322 16.8151 15.5548 16.8151Z";

    @FXML
    public void initialize() {
        sidebar.setPrefWidth(EXPANDED_W);
        sidebar.setMinWidth(EXPANDED_W);
        sidebar.setMaxWidth(EXPANDED_W);

        setupIcons();
        setupNavigation();
        setupAnimatedThemeSwitch();

        Platform.runLater(() -> {
            collectTextNodes();
            loadView("/com/isa/expensetracker/dashboard.fxml");
        });
    }

    private void setupIcons() {
        imgToggle.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/app.png"))));
        btnDashboard.setGraphic(new FontIcon(MaterialDesignC.CHART_PIE));
        btnAdd.setGraphic(new FontIcon(MaterialDesignP.PLUS_CIRCLE));
        btnAnalytics.setGraphic(new FontIcon(MaterialDesignC.CHART_BAR));

        btnSettings.setGraphic(new FontIcon(MaterialDesignC.COG));
    }

    private void setupNavigation() {
        imgToggle.setOnMouseClicked(e -> animateSidebar());
        btnDashboard.setOnAction(e -> loadView("/com/isa/expensetracker/dashboard.fxml"));
        btnAdd.setOnAction(e -> loadView("/com/isa/expensetracker/expense_form.fxml"));
        btnAnalytics.setOnAction(e -> loadView("/com/isa/expensetracker/analytics.fxml"));

        btnSettings.setDisable(false);
        btnSettings.setOnAction(e -> loadView("/com/isa/expensetracker/settings.fxml"));
    }

    private void setupAnimatedThemeSwitch() {
        sunIcon = new SVGPath();
        sunIcon.setContent(SVG_SUN);
        sunIcon.getStyleClass().add("theme-icon");
        moonIcon = new SVGPath();
        moonIcon.setContent(SVG_MOON);
        moonIcon.getStyleClass().add("theme-icon");
        moonIcon.setOpacity(0);
        themeToggle.getChildren().addAll(sunIcon, moonIcon);
        themeToggle.setOnMouseClicked(e -> toggleTheme());
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        Node iconToShow = isDarkMode ? moonIcon : sunIcon;
        Node iconToHide = isDarkMode ? sunIcon : moonIcon;
        RotateTransition rotateOut = new RotateTransition(Duration.millis(300), iconToHide);
        rotateOut.setByAngle(-90);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), iconToHide);
        fadeOut.setToValue(0);
        iconToShow.setRotate(-90);
        RotateTransition rotateIn = new RotateTransition(Duration.millis(300), iconToShow);
        rotateIn.setByAngle(90);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), iconToShow);
        fadeIn.setToValue(1);
        new ParallelTransition(rotateOut, fadeOut, rotateIn, fadeIn).play();
        if (isDarkMode) {
            root.getStyleClass().add("dark");
            themeLabel.setText("Dark Mode");
        } else {
            root.getStyleClass().remove("dark");
            themeLabel.setText("Light Mode");
        }
    }

    private void animateSidebar() {
        isSidebarExpanded = !isSidebarExpanded;
        double targetWidth = isSidebarExpanded ? EXPANDED_W : COLLAPSED_W;
        Timeline widthAnimation = new Timeline(
                new KeyFrame(Duration.millis(250),
                        new KeyValue(sidebar.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(sidebar.minWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(sidebar.maxWidthProperty(), targetWidth, Interpolator.EASE_BOTH)
                )
        );
        ParallelTransition fadeAnimation = new ParallelTransition();
        for (Node textNode : textNodesToManage) {
            FadeTransition ft = new FadeTransition(Duration.millis(200), textNode);
            ft.setToValue(isSidebarExpanded ? 1.0 : 0.0);
            fadeAnimation.getChildren().add(ft);
        }
        if (isSidebarExpanded) {
            textNodesToManage.forEach(node -> node.setManaged(true));
        } else {
            fadeAnimation.setOnFinished(e -> textNodesToManage.forEach(node -> node.setManaged(false)));
        }
        sidebar.pseudoClassStateChanged(COLLAPSED, !isSidebarExpanded);
        new ParallelTransition(widthAnimation, fadeAnimation).play();
    }

    private void collectTextNodes() {
        textNodesToManage = new ArrayList<>(List.of(brandLabel, versionLabel, themeLabel));
        Button[] navButtons = {btnDashboard, btnAdd, btnAnalytics, btnSettings};
        for (Button btn : navButtons) {
            Node textNode = btn.lookup(".text");
            if (textNode != null) {
                textNodesToManage.add(textNode);
            }
        }
    }

    private void loadView(String path) {
        try {
            Node page = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(path)));
            page.setOpacity(0);
            content.getChildren().setAll(page);
            FadeTransition ft = new FadeTransition(Duration.millis(250), page);
            ft.setToValue(1.0);
            ft.play();
        } catch (Exception ex) {
            System.err.println("Failed to load FXML view: " + path);
            ex.printStackTrace();
        }
    }
}
