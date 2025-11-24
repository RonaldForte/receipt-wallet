package com.receipts.app;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    //Finds receipts by store name
    List<Receipt> findByStoreNameContainingIgnoreCase(String storeName);

    //Finds receipts by exact date
    List<Receipt> findByDate(String date);
}
