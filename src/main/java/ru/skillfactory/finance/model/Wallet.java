package ru.skillfactory.finance.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Wallet implements Serializable {
    private UUID userId;
    private List<Transaction> transactions;
    private Map<String, Double> budgets;

    public Wallet(UUID userId) {
        this.userId = userId;
        this.transactions = new ArrayList<>();
        this.budgets = new HashMap<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        checkBudgetLimit(transaction); // Проверка бюджета при добавлении транзакции
    }

    public void setBudget(String category, double amount) {
        budgets.put(category, amount);
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpenses() {
        return transactions.stream()
                .filter(t -> !t.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Map<String, Double> getBudgets() {
        return budgets;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    private void checkBudgetLimit(Transaction transaction) {
        if (!transaction.isIncome()) { // Проверяем только расходы
            double budget = budgets.getOrDefault(transaction.getCategory(), 0.0);
            double totalExpensesForCategory = transactions.stream()
                    .filter(t -> t.getCategory().equals(transaction.getCategory()) && !t.isIncome())
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            if (budget > 0.0 && totalExpensesForCategory > budget) {
                System.out.println("Оповещение: Превышен лимит бюджета для категории '" + transaction.getCategory() + "'.");
            }
        }

        double totalIncome = getTotalIncome();
        double totalExpenses = getTotalExpenses();
        if (totalExpenses > totalIncome) {
            System.out.println("Оповещение: Общие расходы превышают доходы.");
        }
    }
}