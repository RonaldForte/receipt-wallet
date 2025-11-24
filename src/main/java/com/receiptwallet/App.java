package com.receiptwallet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.receiptwallet.model.Receipt;
import com.receiptwallet.service.ReceiptService;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
      //temp remove all information from the table

      String url = "jdbc:h2:./receiptsdb";
      String user = "sa";
      String password = "";

      try (Connection conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement()) {
        stmt.executeUpdate("DROP TABLE IF EXISTS receipts");
        stmt.executeUpdate("CREATE TABLE receipts (id INT AUTO_INCREMENT PRIMARY KEY, storeName VARCHAR(255), amount DOUBLE, date VARCHAR(50))");
      } catch (SQLException e) {
        e.printStackTrace();
      }

      ReceiptService service = new ReceiptService();
      Receipt r1 = new Receipt("Walmart", 23.46, "2025-11-23");
      Receipt r2 = new Receipt("Target", 45.99, "2025-11-22");

      service.addReceipt(r1);
      service.addReceipt(r2);

      System.out.println("Added 2 receipts.");

      System.out.println("\nAll receipts:");
      for (Receipt r : service.getAllReceipts()) {
        System.out.println(r);
      }

      System.out.println("\nReceipts from Walmart:");
      for (Receipt r : service.searchByStoreName("Walmart")) {
        System.out.println(r);
      }

      System.out.println("\nReceipts on 2025-11-22:");
      for (Receipt r : service.searchByDate("2025-11-22")) {
        System.out.println(r);
      }

      //update receipt with id = 1
      service.updateReceipt(1, "Walmart Supercenter", 30.00, "2025-11-23");

      System.out.println("\nAfter updating receipt with id 1:");
      for (Receipt r : service.getAllReceipts()) {
        System.out.println(r);
      }

      service.deleteReceipt(2); // delete receipt with id = 2

      System.out.println("\nAfter deleting receipt with id 2:");
      for (Receipt r : service.getAllReceipts()) {
        System.out.println(r);
      }
      
      //print all again to see deletion and updating
      System.out.println("\nAll receipts:");
      for (Receipt r : service.getAllReceipts()) {
        System.out.println(r);
      }

    }

}
