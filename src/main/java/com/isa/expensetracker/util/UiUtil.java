package com.isa.expensetracker.util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.File;

public final class UiUtil {
    private UiUtil() {}

    public static void fadeIn(Node node) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setToValue(1);
        ft.setInterpolator(Interpolator.EASE_BOTH);
        ft.play();
    }

    public static void info(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    public static void error(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    public static File chooseCsvToSave(Window owner, String suggestedName) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fc.setInitialFileName(suggestedName);
        return fc.showSaveDialog(owner);
    }
}
