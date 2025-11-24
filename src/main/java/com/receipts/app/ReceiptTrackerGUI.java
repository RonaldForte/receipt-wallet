package com.receipts.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;



public class ReceiptTrackerGUI extends Application {

    //create tableview accessible throughout entire class
    TableView<Receipt> table = new TableView<>();

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage primaryStage) {

        //bootstrap spring
        ConfigurableApplicationContext context = new SpringApplicationBuilder(ReceiptTrackerApplication.class)
        .headless(false) //allowing JavaFX GUI
        .run();

        // Get repository bean
        ReceiptRepository receiptRepository = context.getBean(ReceiptRepository.class);

        // Fetch all receipts from postgre DB
        List<Receipt> receipts = receiptRepository.findAll();
        ObservableList<Receipt> data = FXCollections.observableArrayList(receipts);

        //Send the data to table
        table.setItems(data);
    
        //columns
        TableColumn<Receipt, String> storeCol = new TableColumn<>("Store");
        storeCol.setCellValueFactory(new PropertyValueFactory<>("storeName"));
    
        TableColumn<Receipt, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
    
        TableColumn<Receipt, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
    
        //adding to table
        table.getColumns().addAll(storeCol, amountCol, dateCol);
    
        table.setItems(data);
    
        //layout
        VBox vbox = new VBox(table);
        Scene scene = new Scene(vbox, 600, 400);
    
        primaryStage.setTitle("Receipt Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}
