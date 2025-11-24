package com.receipts.app;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class ReceiptTrackerGUI extends Application {

    private TableView<Receipt> table;
    private ObservableList<Receipt> data;
    private ReceiptRepository receiptRepository;
    private Scene mainScene;
    public static ConfigurableApplicationContext context;

    @Override
    public void start(Stage primaryStage) {
        context = new SpringApplicationBuilder(ReceiptTrackerApplication.class)
            .headless(false)
            .run();
        receiptRepository = context.getBean(ReceiptRepository.class);

        table = new TableView<>();
        setupTableColumns();
        loadReceipts();

        TextField searchField = new TextField();
        searchField.setPromptText("Search by store...");
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) {
                loadReceipts();
            } else {
                List<Receipt> filtered = receiptRepository.findByStoreNameContainingIgnoreCase(newText);
                data.setAll(filtered);
            }
        });

        Button addButton = new Button("Add Receipt");
        addButton.setOnAction(e -> showAddEditReceiptForm(null));

        Button editButton = new Button("Edit Selected");
        editButton.setOnAction(e -> {
            Receipt selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showAddEditReceiptForm(selected);
        });

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setOnAction(e -> deleteSelectedReceipt());

        Button dashboardButton = new Button("Show Dashboard");
        dashboardButton.setOnAction(e -> showDashboard(primaryStage));

        HBox buttons = new HBox(10, addButton, editButton, deleteButton, dashboardButton);
        buttons.setPadding(new Insets(5));

        VBox root = new VBox(10, searchField, table, buttons);
        root.setPadding(new Insets(10));

        mainScene = new Scene(root, 1000, 500);
        primaryStage.setTitle("Receipt Tracker");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    @SuppressWarnings("unchecked")
    private void setupTableColumns() {
        TableColumn<Receipt, String> storeCol = new TableColumn<>("Store");
        storeCol.setCellValueFactory(new PropertyValueFactory<>("storeName"));
        storeCol.setPrefWidth(200);

        TableColumn<Receipt, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(100);

        TableColumn<Receipt, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(150);
        dateCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? "" : date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            }
        });

        TableColumn<Receipt, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(150);


        table.getColumns().setAll(storeCol, amountCol, dateCol, categoryCol);
    }

    private void loadReceipts() {

        if (data == null) {
            data = FXCollections.observableArrayList();
            table.setItems(data);
        }
        List<Receipt> receipts = receiptRepository.findAll();
        data.setAll(receipts);
    }

    private void deleteSelectedReceipt() {
        Receipt selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            receiptRepository.delete(selected);
            data.remove(selected);
        }
    }

    private void showAddEditReceiptForm(Receipt receiptToEdit) {
        Stage formStage = new Stage();
        formStage.setTitle(receiptToEdit == null ? "Add Receipt" : "Edit Receipt");

        TextField storeField = new TextField();
        storeField.setPromptText("Store Name");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        DatePicker datePicker = new DatePicker();

        ComboBox<String> categoryField = new ComboBox<>();
        categoryField.getItems().addAll("Food", "Utilities", "Entertainment", "Transport", "Other");
        categoryField.setPromptText("Select Category");

        if (receiptToEdit != null) {
            storeField.setText(receiptToEdit.getStoreName());
            amountField.setText(String.valueOf(receiptToEdit.getAmount()));
            datePicker.setValue(receiptToEdit.getDate());
            categoryField.setValue(receiptToEdit.getCategory());
        }

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            try {
                String store = storeField.getText();
                double amount = Double.parseDouble(amountField.getText());
                LocalDate date = datePicker.getValue();
                String category = categoryField.getValue();

                if (store.isEmpty() || date == null || category == null) 
                    throw new IllegalArgumentException("All fields are required");

                if (receiptToEdit == null) {
                    Receipt newReceipt = new Receipt(store, amount, date, category);
                    receiptRepository.save(newReceipt);
                    data.add(newReceipt);
                } else {
                    receiptToEdit.setStoreName(store);
                    receiptToEdit.setAmount(amount);
                    receiptToEdit.setDate(date);
                    receiptToEdit.setCategory(category);
                    receiptRepository.save(receiptToEdit);
                    table.refresh();
                }
                formStage.close();
            } catch (Exception ex) {
                showErrorAlert("Invalid Input", ex.getMessage());
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> formStage.close());

        HBox buttons = new HBox(10, saveButton, cancelButton);
        VBox layout = new VBox(10, storeField, amountField, datePicker, categoryField, buttons);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 350, 250);
        formStage.setScene(scene);
        formStage.show();
    }

    private void showErrorAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showDashboard(Stage stage) {
        List<Receipt> allReceipts = receiptRepository.findAll();


        ComboBox<String> storeFilter = new ComboBox<>();
        storeFilter.getItems().add("All Stores");
        storeFilter.getItems().addAll(allReceipts.stream().map(Receipt::getStoreName).distinct().sorted().toList());
        storeFilter.setValue("All Stores");

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().add("All Categories");
        categoryFilter.getItems().addAll(allReceipts.stream().map(Receipt::getCategory).distinct().sorted().toList());
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

            List<Receipt> filtered = allReceipts.stream()
                .filter(r -> (selectedStore.equals("All Stores") || r.getStoreName().equals(selectedStore)) &&
                    (selectedCategory.equals("All Categories") || r.getCategory().equals(selectedCategory)) &&
                    (from == null || !r.getDate().isBefore(from)) &&
                    (to == null || !r.getDate().isAfter(to)))
                .toList();

            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Spending");
            Map<String, Double> monthlyTotals = new TreeMap<>();
            for (Receipt r : filtered) {
                String month = r.getDate().getMonth().toString() + " " + r.getDate().getYear();
                monthlyTotals.put(month, monthlyTotals.getOrDefault(month, 0.0) + r.getAmount());
            }
            monthlyTotals.forEach((month, total) -> series.getData().add(new XYChart.Data<>(month, total)));
            barChart.getData().add(series);

            storePie.getData().clear();
            Map<String, Double> storeTotals = new HashMap<>();
            for (Receipt r : filtered) {
                storeTotals.put(r.getStoreName(), storeTotals.getOrDefault(r.getStoreName(), 0.0) + r.getAmount());
            }
            storeTotals.forEach((store, total) -> storePie.getData().add(new PieChart.Data(store, total)));

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
        backButton.setOnAction(e -> stage.setScene(mainScene));

        HBox filterBox = new HBox(10,
            new Label("Store:"), storeFilter,
            new Label("Category:"), categoryFilter,
            new Label("From:"), fromDate,
            new Label("To:"), toDate,
            applyFilter);
        filterBox.setPadding(new Insets(5));

        VBox layout = new VBox(10, filterBox, new HBox(20, barChart, storePie, categoryPie), backButton);
        layout.setPadding(new Insets(10));

        Scene dashboardScene = new Scene(layout, 1000, 600);
        stage.setScene(dashboardScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
