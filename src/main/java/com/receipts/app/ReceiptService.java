package com.receipts.app;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    //Constructor injection
    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    //Create or update a receipt
    public Receipt saveReceipt(Receipt receipt) {
        return receiptRepository.save(receipt);
    }

    //Get all receipts
    public List<Receipt> getAllReceipts() {
        return receiptRepository.findAll();
    }

    //Delete a receipt by id
    public void deleteReceipt(Long id) {
        receiptRepository.deleteById(id);
    }

    //Search by store name
    public List<Receipt> searchByStoreName(String storeName) {
        return receiptRepository.findByStoreNameContainingIgnoreCase(storeName);
    }

    //Search by date
    public List<Receipt> searchByDate(String date) {
        return receiptRepository.findByDate(date);
    }
}
