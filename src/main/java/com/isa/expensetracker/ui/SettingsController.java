package com.isa.expensetracker.ui;

import com.isa.expensetracker.service.ExportService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class SettingsController {
    @FXML private ComboBox<YearMonth> monthBox;
    @FXML private TextField tfExportPath;
    @FXML private Button btnChooseFile;
    @FXML private Button btnExport;

    private final ExportService exportService = new ExportService();

    @FXML
    public void initialize() {
        // Setup month filter
        List<YearMonth> months = new ArrayList<>();
        YearMonth now = YearMonth.now();
        for (int i = 0; i < 12; i++) months.add(now.minusMonths(i));
        monthBox.setItems(FXCollections.observableArrayList(months));
        monthBox.getSelectionModel().select(0);

        // Add icons
        btnExport.setGraphic(new FontIcon(MaterialDesignF.FILE_EXPORT));

        // Add listeners
        btnChooseFile.setOnAction(e -> chooseFile());
        btnExport.setOnAction(e -> exportData());


        tfExportPath.textProperty().addListener((obs, oldVal, newVal) -> {
            btnExport.setDisable(newVal == null || newVal.trim().isEmpty());
        });
    }

    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Expense Report");


        String userHome = System.getProperty("user.home");
        File downloadsDir = new File(userHome, "Downloads");
        if (downloadsDir.isDirectory()) {
            fileChooser.setInitialDirectory(downloadsDir);
        }

        fileChooser.setInitialFileName("expenses-" + monthBox.getValue() + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File file = fileChooser.showSaveDialog(btnChooseFile.getScene().getWindow());

        if (file != null) {
            tfExportPath.setText(file.getAbsolutePath());
        }
    }

    private void exportData() {
        String pathText = tfExportPath.getText();
        if (pathText == null || pathText.trim().isEmpty() || monthBox.getValue() == null) {
            show(Alert.AlertType.WARNING, "Please select a month and provide a file location first.");
            return;
        }

        // ✅ NEW: Use the path directly from the editable text field
        Path exportPath = Paths.get(pathText);

        try {
            exportService.exportMonthToCsv(monthBox.getValue(), exportPath);
            show(Alert.AlertType.INFORMATION, "Export successful!\n\nFile saved to:\n" + exportPath);
        } catch (Exception e) {
            e.printStackTrace();
            show(Alert.AlertType.ERROR, "Export failed.\n\nError: " + e.getMessage());
        }
    }

    private void show(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle("Export Status");
        a.showAndWait();
    }
}
