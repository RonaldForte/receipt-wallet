package com.receipts.app;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ReceiptForm {

    public static void showForm(Receipt receipt, ReceiptService service, ObservableList<Receipt> data) {
        Stage stage = new Stage();
        stage.setTitle(receipt == null ? "Add Receipt" : "Edit Receipt");

        TextField storeField = new TextField();
        storeField.setPromptText("Store Name");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        DatePicker datePicker = new DatePicker();

        ComboBox<String> categoryField = new ComboBox<>();
        categoryField.getItems().addAll("Food", "Utilities", "Entertainment", "Transport", "Other");
        categoryField.setPromptText("Select Category");

        if (receipt != null) {
            storeField.setText(receipt.getStoreName());
            amountField.setText(String.valueOf(receipt.getAmount()));
            datePicker.setValue(receipt.getDate());
            categoryField.setValue(receipt.getCategory());
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

                if (receipt == null) {
                    Receipt newReceipt = new Receipt(store, amount, date, category);
                    service.saveReceipt(newReceipt);
                    data.add(newReceipt);
                } else {
                    receipt.setStoreName(store);
                    receipt.setAmount(amount);
                    receipt.setDate(date);
                    receipt.setCategory(category);
                    service.saveReceipt(receipt);
                }
                stage.close();
            } catch (Exception ex) {
                showErrorAlert("Invalid Input", ex.getMessage());
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.close());

        HBox buttons = new HBox(10, saveButton, cancelButton);
        VBox layout = new VBox(10, storeField, amountField, datePicker, categoryField, buttons);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 350, 250);
        stage.setScene(scene);
        stage.show();
    }

    private static void showErrorAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
