package com.receipts.app;

import javafx.scene.control.TableCell;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;

public class DatePickerTableCell extends TableCell<Receipt, LocalDate> {

    private final DatePicker datePicker = new DatePicker();

    public DatePickerTableCell() {
        datePicker.setOnAction(e -> {
            LocalDate date = datePicker.getValue();
            if (date != null && getTableRow().getItem() != null) {
                Receipt receipt = getTableRow().getItem();
                receipt.setDate(date);
            }
        });
    }

    @Override
    protected void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);

        if (empty) {
            setGraphic(null);
        } else {
            datePicker.setValue(date);
            setGraphic(datePicker);
        }
    }
}
