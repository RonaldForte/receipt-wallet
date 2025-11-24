package com.receipts.app;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {
    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
      this.receiptService = receiptService;
    }

    //Get all receipts
    @GetMapping
    public List<Receipt> getAllReceipts() {
      return receiptService.getAllReceipts();
    }

    //Add a new receipt
    @PostMapping
    public Receipt addReceipt(@RequestBody Receipt receipt) {
      return receiptService.saveReceipt(receipt);
    }

    //Delete a receipt by ID
    @DeleteMapping("/{id}")
    public void deleteReceipt(@PathVariable Long id) {
      receiptService.deleteReceipt(id);
    }

    // Search by store name
    @GetMapping("/search/store")
    public List<Receipt> searchByStore(@RequestParam String storeName) {
      return receiptService.searchByStoreName(storeName);
    }

    //Search by date
    @GetMapping("/search/date")
    public List<Receipt> searchByDate(@RequestParam String date) {
      return receiptService.searchByDate(date);
    }
}
