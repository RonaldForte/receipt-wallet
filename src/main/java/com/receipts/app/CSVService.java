package com.receipts.app;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class CSVService {

    public void export(List<Receipt> receipts) throws IOException {
        try (FileWriter writer = new FileWriter("receipts.csv")) {
            writer.append("ID,Store,Amount,Date,Category\n");
            for (Receipt r : receipts) {
                writer.append(String.format("%d,%s,%.2f,%s,%s\n",
                    r.getId(),
                    r.getStoreName(),
                    r.getAmount(),
                    r.getDate(),
                    r.getCategory()));
            }
        }
    }
}
