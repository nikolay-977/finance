package ru.skillfactory.finance.service;

import ru.skillfactory.finance.model.Wallet;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WalletService {
    private Map<UUID, Wallet> wallets;

    public WalletService() {
        wallets = new HashMap<>();
    }

    public Wallet getWallet(UUID userId) {
        return wallets.computeIfAbsent(userId, Wallet::new);
    }

    public void saveWalletsToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(wallets);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadWalletsFromFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
                wallets = (Map<UUID, Wallet>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Файл " + filePath + " не найден. Будет создан новый кошелек.");
        }
    }
}