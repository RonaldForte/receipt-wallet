package com.receiptwallet.service;

import com.receiptwallet.model.Receipt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReceiptService {
  //temporary file-based H2 database
  private final String url = "jdbc:h2:./data/receiptDB"; //recieptDB is database file
  private final String user = "sa"; //the default H2 username
  private final String password = ""; //the default H2 password

  public ReceiptService() { //constructor
    //making sure the table exists
    try (Connection conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement()) {
        String sql = "CREATE TABLE IF NOT EXISTS receipts (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "storeName VARCHAR(255)," +
                     "amount DOUBLE," +
                     "date VARCHAR(50))";
        stmt.executeUpdate(sql);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

  public void addReceipt(Receipt r){
    String sql = "INSERT INTO receipts (storeName, amount, date) VALUES (?, ?, ?)";
    try (Connection conn = DriverManager.getConnection(url, user, password);
    PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, r.getStoreName());
    ps.setDouble(2, r.getAmount());
    ps.setString(3, r.getDate());

    ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<Receipt> getAllReceipts(){
    List<Receipt> list = new ArrayList<>();
    String sql = "SELECT storeName, amount, date FROM receipts";
    try (Connection conn = DriverManager.getConnection(url, user, password);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
          String store = rs.getString("storeName");
          double amount = rs.getDouble("amount");
          String date = rs.getString("date");

          list.add(new Receipt(store, amount, date));
        }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  public void deleteReceipt(int id){
    String sql = "DELETE FROM receipts WHERE id = ?";
    try (Connection conn = DriverManager.getConnection(url, user, password);
      PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, id);
        
        ps.executeUpdate();
      } catch (SQLException e){
        e.printStackTrace();
      }
  }

  public List<Receipt> searchByStoreName(String storeName){
    List<Receipt> list = new ArrayList<>();
    String sql = "SELECT storeName, amount, date FROM receipts WHERE storeName = ?";
    try (Connection conn = DriverManager.getConnection(url, user, password);
    PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, storeName);
      
      ResultSet rs = ps.executeQuery();

      while(rs.next()) {
        String store = rs.getString("storeName");
        double amount = rs.getDouble("amount");
        String date = rs.getString("date");

        list.add(new Receipt(store, amount, date));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  public List<Receipt> searchByDate(String date){
    List<Receipt> list = new ArrayList<>();
    String sql = "SELECT storeName, amount, date FROM receipts WHERE date = ?";
    try (Connection conn = DriverManager.getConnection(url, user, password);
    PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, date);

      ResultSet rs = ps.executeQuery();

      while(rs.next()){
        String store = rs.getString("storeName");
        double amount = rs.getDouble("amount");
        String recieptDate = rs.getString("date");

        list.add(new Receipt(store, amount, recieptDate));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  public void updateReceipt(int id, String newStore, double newAmount, String newDate) {
    String sql = "UPDATE receipts SET storeName = ?, amount = ?, date = ? WHERE id = ?";
    try (Connection conn = DriverManager.getConnection(url, user, password);
    PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, newStore);
    ps.setDouble(2, newAmount);
    ps.setString(3, newDate);
    ps.setInt(4, id);

    ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
