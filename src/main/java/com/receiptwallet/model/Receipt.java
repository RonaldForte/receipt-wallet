package com.receiptwallet.model;

public class Receipt {
  private String storeName;
  private double amount;
  private String date;

  public Receipt(String storeName, double amount, String date){
    this.storeName = storeName;
    this.amount = amount;
    this.date = date;
  }

  public String getStoreName() {
    return storeName;
  }

  public double getAmount() {
    return amount;
  }

  public String getDate() {
    return date;
  }

  @Override
  public String toString() {
    return "Receipt [storeName=" + storeName + ", amount=" + amount + ", date=" + date + "]";
  }

}
