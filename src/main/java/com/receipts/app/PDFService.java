package com.receipts.app;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;

@Service
public class PDFService {
    public void generate(List<Receipt> receipts) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter("receipts.pdf");
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Receipt Tracker Report"));

        float[] columnWidths = {50, 150, 80, 100, 100};
        Table table = new Table(columnWidths);
        table.addCell("ID");
        table.addCell("Store");
        table.addCell("Amount");
        table.addCell("Date");
        table.addCell("Category");

        for (Receipt r : receipts) {
            table.addCell(String.valueOf(r.getId()));
            table.addCell(r.getStoreName());
            table.addCell(String.format("%.2f", r.getAmount()));
            table.addCell(r.getDate().toString());
            table.addCell(r.getCategory());
        }

        document.add(table);
        document.close();
    }
}
