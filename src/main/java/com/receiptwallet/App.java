package com.receiptwallet;

import java.util.List;

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
      ReceiptService service = new ReceiptService();
      Receipt r1 = new Receipt("Walmart", 23.46, "2025-11-23");
      Receipt r2 = new Receipt("Target", 45.99, "2025-11-22");

      service.addReceipt(r1);
      service.addReceipt(r2);

      List<Receipt> allReceipts = service.getAllReceipts();

      for (Receipt r : allReceipts) {
        System.out.println(r);
      }
    }
}
