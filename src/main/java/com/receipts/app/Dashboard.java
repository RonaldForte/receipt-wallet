package com.receipts.app;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Dashboard {

    private final List<Receipt> receipts;

    public Dashboard(List<Receipt> receipts) {
        this.receipts = receipts;
    }

    public void show() {
        Stage dashboardStage = new Stage();
        dashboardStage.setTitle("Spending Dashboard");

        ComboBox<String> storeFilter = new ComboBox<>();
        storeFilter.getItems().add("All Stores");
        storeFilter.getItems().addAll(receipts.stream()
            .map(Receipt::getStoreName)
            .distinct()
            .sorted()
            .toList());
        storeFilter.setValue("All Stores");

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().add("All Categories");
        categoryFilter.getItems().addAll(receipts.stream()
            .map(Receipt::getCategory)
            .distinct()
            .sorted()
            .toList());
        categoryFilter.setValue("All Categories");

        DatePicker fromDate = new DatePicker();
        fromDate.setPromptText("From Date");
        DatePicker toDate = new DatePicker();
        toDate.setPromptText("To Date");

        Button applyFilter = new Button("Apply Filters");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Spent");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Spending");

        PieChart storePie = new PieChart();
        storePie.setTitle("Spending by Store");

        PieChart categoryPie = new PieChart();
        categoryPie.setTitle("Spending by Category");

        Runnable updateCharts = () -> {
            String selectedStore = storeFilter.getValue();
            String selectedCategory = categoryFilter.getValue();
            LocalDate from = fromDate.getValue();
            LocalDate to = toDate.getValue();

            List<Receipt> filtered = receipts.stream()
                .filter(r -> (selectedStore.equals("All Stores") || r.getStoreName().equals(selectedStore)) &&
                    (selectedCategory.equals("All Categories") || r.getCategory().equals(selectedCategory)) &&
                    (from == null || !r.getDate().isBefore(from)) &&
                    (to == null || !r.getDate().isAfter(to)))
                .collect(Collectors.toList());

            //bar
            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Spending");
            Map<String, Double> monthlyTotals = new TreeMap<>();
            for (Receipt r : filtered) {
                String month = r.getDate().getMonth() + " " + r.getDate().getYear();
                monthlyTotals.put(month, monthlyTotals.getOrDefault(month, 0.0) + r.getAmount());
            }
            monthlyTotals.forEach((month, total) -> series.getData().add(new XYChart.Data<>(month, total)));
            barChart.getData().add(series);

            //pie
            storePie.getData().clear();
            Map<String, Double> storeTotals = new HashMap<>();
            for (Receipt r : filtered) {
                storeTotals.put(r.getStoreName(), storeTotals.getOrDefault(r.getStoreName(), 0.0) + r.getAmount());
            }
            storeTotals.forEach((store, total) -> storePie.getData().add(new PieChart.Data(store, total)));

            //pie totals
            categoryPie.getData().clear();
            Map<String, Double> categoryTotals = new HashMap<>();
            for (Receipt r : filtered) {
                categoryTotals.put(r.getCategory(), categoryTotals.getOrDefault(r.getCategory(), 0.0) + r.getAmount());
            }
            categoryTotals.forEach((cat, total) -> categoryPie.getData().add(new PieChart.Data(cat, total)));
        };

        applyFilter.setOnAction(e -> updateCharts.run());
        updateCharts.run();

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> dashboardStage.close());

        HBox filterBox = new HBox(10,
            new Label("Store:"), storeFilter,
            new Label("Category:"), categoryFilter,
            new Label("From:"), fromDate,
            new Label("To:"), toDate,
            applyFilter);
        filterBox.setPadding(new Insets(5));

        VBox layout = new VBox(10, filterBox, new HBox(20, barChart, storePie, categoryPie), backButton);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 1000, 600);
        dashboardStage.setScene(scene);
        dashboardStage.show();
    }
}
