package ru.skillfactory.finance.service;

import ru.skillfactory.finance.model.Wallet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WalletService {
    private List<Wallet> walletList = new ArrayList<>();

    public WalletService() {
        walletList = new ArrayList<>();
    }

    public Optional<Wallet> getWalletByUserId(UUID userId) {
        return walletList.stream()
                .filter(wallet -> wallet.getUserId().equals(userId))
                .findFirst();
    }

    public boolean addWallet(Wallet wallet) {
        return walletList.add(wallet);
    }

    public void saveWalletsToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(walletList);
            System.out.println("Количество сохраненных кошельков: " + walletList.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadWalletsFromFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
                walletList = (List<Wallet>) ois.readObject();
                System.out.println("Количество загруженных кошельков: " + walletList.size());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Файл " + filePath + " не найден. Будет создан новый кошелек.");
        }
    }
}