package com.receipts.app;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReceiptTrackerGUI extends Application {
    private TableView<Receipt> table;
    private ObservableList<Receipt> data;
    private ReceiptService receiptService;
    private CSVService csvService;
    private PDFService pdfService;
    private Scene mainScene;
    public static ConfigurableApplicationContext context;

    @Override
    public void start(Stage primaryStage) {
        context = new SpringApplicationBuilder(ReceiptTrackerApplication.class)
            .headless(false)
            .run();
        receiptService = context.getBean(ReceiptService.class);
        csvService = context.getBean(CSVService.class);
        pdfService = context.getBean(PDFService.class);

        table = new TableView<>();
        setupTableColumns();
        loadReceipts();

        TextField searchField = new TextField();
        searchField.setPromptText("Search by store...");
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) loadReceipts();
            else {
                List<Receipt> filtered = receiptService.searchByStoreName(newText);
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

        Button exportCSVButton = new Button("Export CSV");
        exportCSVButton.setOnAction(e -> {
            try {
                csvService.export(new ArrayList<>(data));
                showInfoAlert("CSV Exported", "receipts.csv was created successfully!");
            } catch (IOException ex) {
                showErrorAlert("CSV Export Error", ex.getMessage());
            }
        });

        Button exportPDFButton = new Button("Export PDF");
        exportPDFButton.setOnAction(e -> {
            try {
                pdfService.generate(new ArrayList<>(data));
                showInfoAlert("PDF Exported", "receipts.pdf was created successfully!");
            } catch (Exception ex) {
                showErrorAlert("PDF Export Error", ex.getMessage());
            }
        });

        HBox buttons = new HBox(10, addButton, editButton, deleteButton, dashboardButton, exportCSVButton, exportPDFButton);
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
        data.setAll(receiptService.getAllReceipts());
    }

    private void deleteSelectedReceipt() {
        Receipt selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            receiptService.deleteReceipt(selected.getId());
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
                    receiptService.saveReceipt(newReceipt);
                    data.add(newReceipt);
                } else {
                    receiptToEdit.setStoreName(store);
                    receiptToEdit.setAmount(amount);
                    receiptToEdit.setDate(date);
                    receiptToEdit.setCategory(category);
                    receiptService.saveReceipt(receiptToEdit);
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

    private void showInfoAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showDashboard(Stage stage) {
        Dashboard dashboard = new Dashboard(receiptService.getAllReceipts());
        dashboard.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
