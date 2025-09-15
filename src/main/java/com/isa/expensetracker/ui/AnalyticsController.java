package com.isa.expensetracker.ui;

import com.isa.expensetracker.service.AnalyticsService;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyticsController {
    @FXML private TilePane chartArea;
    @FXML private ComboBox<YearMonth> monthBox;
    @FXML private ToggleButton btnPieChart, btnBarChart, btnLineChart;

    private final AnalyticsService analytics = new AnalyticsService();
    private final ToggleGroup chartToggleGroup = new ToggleGroup();

    @FXML
    public void initialize() {
        // Setup month filter
        List<YearMonth> months = new ArrayList<>();
        YearMonth now = YearMonth.now();
        for (int i = 0; i < 12; i++) months.add(now.minusMonths(i));
        monthBox.setItems(FXCollections.observableArrayList(months));
        monthBox.getSelectionModel().select(0);

        // Setup chart type toggle
        btnPieChart.setToggleGroup(chartToggleGroup);
        btnBarChart.setToggleGroup(chartToggleGroup);
        btnLineChart.setToggleGroup(chartToggleGroup);
        btnPieChart.setSelected(true);

        // Add listeners to update charts when filters change
        monthBox.setOnAction(e -> renderCharts());
        chartToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) renderCharts();
        });

        renderCharts();
    }

    private void renderCharts() {
        YearMonth ym = monthBox.getValue();
        if (ym == null) return;

        chartArea.getChildren().clear();
        Node chartNode = null;

        if (btnPieChart.isSelected() || btnBarChart.isSelected()) {
            Map<String, BigDecimal> byCat = analytics.sumByCategoryForMonth(ym);
            if (byCat.values().stream().allMatch(v -> v.compareTo(BigDecimal.ZERO) == 0)) {
                showNoDataMessage();
                return;
            }
            if (btnPieChart.isSelected()) chartNode = createPieChart(byCat, ym);
            else chartNode = createBarChart(byCat, ym);

        } else if (btnLineChart.isSelected()) {
            Map<LocalDate, BigDecimal> byDay = analytics.sumByDayForMonth(ym);
            if (byDay.values().stream().allMatch(v -> v.compareTo(BigDecimal.ZERO) == 0)) {
                showNoDataMessage();
                return;
            }
            chartNode = createLineChart(byDay, ym);
        }

        if (chartNode != null) {
            VBox chartCard = new VBox(chartNode);
            chartCard.getStyleClass().add("dashboard-card");
            VBox.setVgrow(chartNode, javafx.scene.layout.Priority.ALWAYS);
            chartArea.getChildren().add(chartCard);

            FadeTransition ft = new FadeTransition(Duration.millis(300), chartCard);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

    private void showNoDataMessage() {
        Label noDataLabel = new Label("No expense data available for the selected period.");
        noDataLabel.getStyleClass().add("h2");
        chartArea.getChildren().add(noDataLabel);
    }

    private PieChart createPieChart(Map<String, BigDecimal> data, YearMonth ym) {
        PieChart pie = new PieChart();
        pie.setTitle("Share by Category — " + ym);
        data.forEach((name, amt) -> {
            if (amt != null && amt.signum() > 0) {
                pie.getData().add(new PieChart.Data(name, amt.doubleValue()));
            }
        });
        return pie;
    }

    private BarChart<String, Number> createBarChart(Map<String, BigDecimal> data, YearMonth ym) {

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount (INR)");

        BarChart<String, Number> bar = new BarChart<>(xAxis, yAxis);
        bar.setTitle("Totals by Category — " + ym);
        bar.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        data.forEach((name, amt) -> {
            if (amt != null && amt.signum() > 0) {
                series.getData().add(new XYChart.Data<>(name, amt));
            }
        });
        bar.getData().add(series);
        return bar;
    }

    private LineChart<String, Number> createLineChart(Map<LocalDate, BigDecimal> data, YearMonth ym) {

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Day of Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount (INR)");

        LineChart<String, Number> line = new LineChart<>(xAxis, yAxis);
        line.setTitle("Daily Spending Trend — " + ym);
        line.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d");
        data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey().format(formatter), entry.getValue()));
                });

        line.getData().add(series);
        return line;
    }
}
