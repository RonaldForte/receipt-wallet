package com.receipts.app;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Dashboard {

    private List<Receipt> receipts;

    public Dashboard(List<Receipt> receipts) {
        this.receipts = receipts;
    }

    public void show() {
        Stage dashboardStage = new Stage();
        dashboardStage.setTitle("Spending Dashboard");

        //bar
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Spent");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Spending");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Spending");

        Map<String, Double> monthlyTotals = new TreeMap<>();
        for (Receipt r : receipts) {
          String month = r.getDate().getMonth().toString() + " " + r.getDate().getYear();
          monthlyTotals.put(month, monthlyTotals.getOrDefault(month, 0.0) + r.getAmount());
        }
        monthlyTotals.forEach((month, total) -> series.getData().add(new XYChart.Data<>(month, total)));
        barChart.getData().add(series);

        //pie
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Spending by Store");
        Map<String, Double> storeTotals = new HashMap<>();
        for (Receipt r : receipts) {
          storeTotals.put(r.getStoreName(), storeTotals.getOrDefault(r.getStoreName(), 0.0) + r.getAmount());
        }
        storeTotals.forEach((store, total) -> pieChart.getData().add(new PieChart.Data(store, total)));

        HBox charts = new HBox(20, barChart, pieChart);
        charts.setPadding(new Insets(10));

        Scene scene = new Scene(charts, 800, 400);
        dashboardStage.setScene(scene);
        dashboardStage.show();
    }
}
