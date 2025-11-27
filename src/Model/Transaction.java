package Model;

import enums.ETypeFile;
import enums.ETypePayment;

public class Transaction {


    private int id;
    private int clientId;
    private double amount;
    private String date;
    private ETypePayment paymentMethod;
    
    
    public Transaction(int id, int clientId, double amount, String date, ETypePayment paymentMethod) {
        this.id = id;
        this.clientId = clientId;
        this.amount = amount;
        this.date = date;
        this.paymentMethod = paymentMethod;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getClientId() {
        return clientId;
    }
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public ETypePayment getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(ETypePayment paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    @Override
    public String toString() {
        return "Transacion [id=" + id + ", clientId=" + clientId + ", amount=" + amount + ", date=" + date
                + ", paymentMethod=" + paymentMethod + "]";
    }

    
}
