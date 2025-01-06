package ru.skillfactory.finance.service;

import ru.skillfactory.finance.model.User;
import ru.skillfactory.finance.model.Wallet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private List<User> userList = new ArrayList<>();
    private WalletService walletService;

    public UserService() {
        this.walletService = new WalletService();
    }

    public void register(String userName, String password) {
        UUID id = UUID.randomUUID();
        User user = new User(id, userName, password);
        userList.add(user);
        walletService.getWallet(id);
    }

    public Optional<User> getUserByLoginAndPassword(String login, String password) {
        return userList.stream()
                .filter(user -> user.getLogin().equals(login))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public Optional<User> getUserById(UUID id) {
        return userList.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public Optional<User> getUserByLogin(String login) {
        return userList.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }

    public Wallet getUserWallet(UUID userId) {
        return walletService.getWallet(userId);
    }

    public void saveUsersToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(userList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadUsersFromFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
                userList = (List<User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Файл " + filePath + " не найден. Будет создан новый пользователь.");
        }
    }
}