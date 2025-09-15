package com.isa.expensetracker.ui;

import com.isa.expensetracker.service.AnalyticsService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

public class DashboardController {
    @FXML private VBox root;
    @FXML private Label lblMonth;
    @FXML private Button btnAdd;
    @FXML private Button btnAnalytics;
    @FXML private Label lblTotalSpent, lblTopCategory, lblDailyAverage;

    private final AnalyticsService analyticsService = new AnalyticsService();
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    @FXML
    public void initialize() {
        YearMonth currentMonth = YearMonth.now();

        // Header text like "August 2025"
        String monthName = currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        lblMonth.setText(monthName + " " + currentMonth.getYear());

        // Set icons on the action buttons
        btnAdd.setGraphic(new FontIcon(MaterialDesignP.PLUS));
        btnAnalytics.setGraphic(new FontIcon(MaterialDesignA.ARROW_RIGHT_BOLD_CIRCLE_OUTLINE));


        // Navigation handlers
        btnAdd.setOnAction(e -> navigate("/com/isa/expensetracker/expense_form.fxml"));
        btnAnalytics.setOnAction(e -> navigate("/com/isa/expensetracker/analytics.fxml"));

        // Load and display the dashboard metrics
        loadDashboardMetrics(currentMonth);
    }

    private void loadDashboardMetrics(YearMonth month) {
        // Fetch data from the service
        BigDecimal totalSpent = analyticsService.getTotalSpentForMonth(month);
        String topCategory = analyticsService.getTopCategoryForMonth(month).orElse("-");
        BigDecimal dailyAverage = analyticsService.getDailyAverageForMonth(month);

        // Update the UI labels with formatted data
        lblTotalSpent.setText(currencyFormatter.format(totalSpent));
        lblTopCategory.setText(topCategory);
        lblDailyAverage.setText(currencyFormatter.format(dailyAverage));
    }

    private void navigate(String fxmlAbsolutePath) {
        try {
            // This line was causing the error because getResource was returning null
            Node page = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlAbsolutePath)));
            StackPane content = (StackPane) root.getScene().lookup("#content");
            if (content == null) {
                throw new IllegalStateException("Cannot find #content StackPane in the scene.");
            }
            content.getChildren().setAll(page);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load FXML: " + fxmlAbsolutePath, ex);
        }
    }
}
