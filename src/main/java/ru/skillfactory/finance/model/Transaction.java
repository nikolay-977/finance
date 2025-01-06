package ru.skillfactory.finance.model;

import java.util.UUID;

public class Transaction {
    private UUID id;
    private String category;
    private double amount;
    private boolean isIncome;

    public Transaction(UUID id, String category, double amount, boolean isIncome) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.isIncome = isIncome;
    }

    public UUID getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isIncome() {
        return isIncome;
    }
}
