package ru.skillfactory.finance;

import ru.skillfactory.finance.model.Transaction;
import ru.skillfactory.finance.model.User;
import ru.skillfactory.finance.model.Wallet;
import ru.skillfactory.finance.service.UserService;
import ru.skillfactory.finance.service.WalletService;

import java.util.*;

public class FinanceApp {

    private Scanner scanner;
    private UserService userService = new UserService();
    private WalletService walletService = new WalletService();
    private UUID userId;
    private boolean isActive;

    public FinanceApp() {
        // Загрузка пользователей и кошельков из файлов при старте приложения
        userService.loadUsersFromFile("users.ser");
        walletService.loadWalletsFromFile("wallets.ser");
    }

    public static void main(String[] args) {
        FinanceApp app = new FinanceApp();
        app.start();
    }

    public void start() {
        this.scanner = new Scanner(System.in);
        mainMenu();
    }

    private void mainMenu() {
        System.out.println("1. Регистрация");
        System.out.println("2. Авторизация");
        System.out.println("3. Выйти из программы");

        boolean isActive = true;

        while (isActive) {
            int choice = getIntInput();

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    enter();
                    break;
                case 3:
                    exit();
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
                    continue;
            }
            break;
        }
    }

    private void register() {
        System.out.print("Введите логин:");
        String userName = getValidUsername();
        System.out.print("Введите пароль:");
        String password = getValidPassword();
        Optional<User> userOpt = userService.getUserByLoginAndPassword(userName, password);
        if (userOpt.isPresent()) {
            System.out.println("Пользователь уже зарегистрирован.");
        } else {
            userService.register(userName, password);
            System.out.println("Пользователь успешно зарегистрирован.");
        }
        mainMenu();
    }

    private void enter() {
        System.out.print("Введите логин:");
        String userName = getValidUsername();
        System.out.print("Введите пароль: ");
        String password = getValidPassword();
        Optional<User> userOpt = userService.getUserByLoginAndPassword(userName, password);
        if (userOpt.isPresent()) {
            System.out.println("Пользователь " + userName + " успешно авторизован.");
            userId = userOpt.get().getId();
            Optional<Wallet> walletOpt = walletService.getWalletByUserId(userId);

            if (walletOpt.isPresent()) {
                System.out.println("Кошелек найднен.");
            } else {
                Wallet wallet = new Wallet(userId);
                walletService.addWallet(wallet);
                System.out.println("Кошелек создан.");
            }
            financialManagement();
        } else {
            System.out.println("Пользователь не найден.");
            mainMenu();
        }
    }

    private void exit() {
        System.out.println("Выход из программы.");
        // Сохранение кошельков пользователей перед выходом
        walletService.saveWalletsToFile("wallets.ser");
        System.out.println("Кошельки пользователей сохранены.");
        // Сохранение пользователей перед выходом
        userService.saveUsersToFile("users.ser");
        System.out.println("Пользователи сохранены.");
    }

    private void financialManagement() {
        System.out.println("1. Добавить транзакцию");
        System.out.println("2. Установить бюджет");
        System.out.println("3. Показать статистику");
        System.out.println("4. Показать статистику по выбранным категориям");
        System.out.println("5. Перевести средства");
        System.out.println("6. Главное меню");
        System.out.println("7. Выйти из программы");

        boolean isActive = true;

        while (isActive) {
            int choice = getIntInput();

            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    setBudget();
                    break;
                case 3:
                    showStatistics();
                    break;
                case 4:
                    showStatisticsByCategories();
                    break;
                case 5:
                    transferMoney();
                    break;
                case 6:
                    mainMenu();
                    break;
                case 7:
                    exit();
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
                    continue;
            }
            break;
        }
    }

    private void addTransaction() {
        System.out.print("Введите категорию: ");
        String category = scanner.nextLine();
        System.out.print("Введите сумму: ");
        double amount = getDoubleInput();
        System.out.println("Введите тип: ");
        System.out.println("1. Доход");
        System.out.println("2. Расход");

        boolean isIncome;
        while (true) {
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    isIncome = true;
                    break;
                case 2:
                    isIncome = false;
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, выберите 1 для дохода или 2 для расхода.");
                    continue;
            }
            break;
        }

        Optional<Wallet> walletOpt = walletService.getWalletByUserId(userId);

        if (walletOpt.isPresent()) {
            Wallet wallet = walletOpt.get();
            Transaction transaction = new Transaction(UUID.randomUUID(), category, amount, isIncome);
            wallet.addTransaction(transaction);
            System.out.println("Транзакция добавлена.");
        } else {
            System.out.println("Кошелек не найден");
        }

        financialManagement();
    }

    private void setBudget() {
        System.out.print("Введите категорию: ");
        String category = scanner.nextLine();
        System.out.print("Введите бюджет: ");
        double budget = getDoubleInput();

        Optional<Wallet> walletOpt = walletService.getWalletByUserId(userId);

        if (walletOpt.isPresent()) {
            Wallet wallet = walletOpt.get();
            wallet.setBudget(category, budget);
            System.out.println("Бюджет установлен.");
        } else {
            System.out.println("Кошелек не найден");
        }

        financialManagement();
    }

    private void showStatistics() {
        Optional<Wallet> walletOpt = walletService.getWalletByUserId(userId);

        if (walletOpt.isPresent()) {
            Wallet wallet = walletOpt.get();
            double totalIncome = 0;
            double totalExpenses = 0;
            System.out.println("Статистика:");
            // Получаем уникальные категории из транзакций
            Map<String, Double> incomeByCategory = new HashMap<>();
            Map<String, Double> expensesByCategory = new HashMap<>();

            for (Transaction transaction : wallet.getTransactions()) {
                String category = transaction.getCategory();
                if (transaction.isIncome()) {
                    incomeByCategory.put(category, incomeByCategory.getOrDefault(category, 0.0) + transaction.getAmount());
                } else {
                    expensesByCategory.put(category, expensesByCategory.getOrDefault(category, 0.0) + transaction.getAmount());
                }
            }

            // Суммируем общий доход
            totalIncome = incomeByCategory.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();

            System.out.println("Общий доход: " + totalIncome);

            // Выводим доходы по категориям
            System.out.println("Доходы по категориям:");
            for (Map.Entry<String, Double> entry : incomeByCategory.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }

            // Суммируем общие расходы
            totalExpenses = expensesByCategory.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();

            System.out.println("Общие расходы: " + totalExpenses);

            // Выводим расходы по категориям
            System.out.println("Расходы по категориям:");
            for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }

            // Выводим бюджет по категориям и оставшийся бюджет
            System.out.println("Бюджет по категориям:");
            for (Map.Entry<String, Double> entry : wallet.getBudgets().entrySet()) {
                String category = entry.getKey();
                double budget = entry.getValue();
                double spent = expensesByCategory.getOrDefault(category, 0.0);
                double remainingBudget = budget - spent;

                System.out.println("  " + category + ": " + budget + ", Оставшийся бюджет: " + remainingBudget);
            }
        } else {
            System.out.println("Кошелек не найден");
        }

        financialManagement();
    }

    private void showStatisticsByCategories() {
        System.out.print("Введите категории через запятую: ");
        String input = scanner.nextLine();
        String[] categories = input.split(",");

        Optional<Wallet> walletOpt = walletService.getWalletByUserId(userId);

        if (walletOpt.isPresent()) {
            Wallet wallet = walletOpt.get();
            double totalIncome = 0;
            double totalExpenses = 0;
            System.out.println("Статистика по выбранным категориям:");

            for (String category : categories) {
                category = category.trim(); // Удаляем лишние пробелы
                String finalCategory = category;
                double incomeForCategory = wallet.getTransactions().stream()
                        .filter(t -> t.getCategory().equalsIgnoreCase(finalCategory) && t.isIncome())
                        .mapToDouble(Transaction::getAmount)
                        .sum();

                double expensesForCategory = wallet.getTransactions().stream()
                        .filter(t -> t.getCategory().equalsIgnoreCase(finalCategory) && !t.isIncome())
                        .mapToDouble(Transaction::getAmount)
                        .sum();

                if (incomeForCategory == 0 && expensesForCategory == 0) {
                    System.out.println("Категория '" + category + "' не найдена.");
                } else {
                    System.out.println("Категория: " + category);
                    System.out.println("  Доход: " + incomeForCategory);
                    System.out.println("  Расход: " + expensesForCategory);
                    totalIncome += incomeForCategory;
                    totalExpenses += expensesForCategory;
                }
            }

            System.out.println("Общий доход по выбранным категориям: " + totalIncome);
            System.out.println("Общие расходы по выбранным категориям: " + totalExpenses);
        } else {
            System.out.println("Кошелек не найден");
        }

        financialManagement();
    }

    private void transferMoney() {
        System.out.print("Введите логин получателя: ");
        String recipientLogin = getValidUsername();
        Optional<User> recipientOpt = userService.getUserByLogin(recipientLogin);

        if (!recipientOpt.isPresent()) {
            System.out.println("Пользователь с логином '" + recipientLogin + "' не найден.");
            financialManagement();
            return;
        }

        System.out.print("Введите сумму для перевода: ");
        double amount = getDoubleInput();

        Optional<Wallet> senderWalletOpt = walletService.getWalletByUserId(userId);

        if (senderWalletOpt.isPresent()) {
            Wallet senderWallet = senderWalletOpt.get();
            double senderBalance = senderWallet.getTotalIncome() - senderWallet.getTotalExpenses();

            if (amount > senderBalance) {
                System.out.println("Недостаточно средств для перевода.");
                financialManagement();
                return;
            }

            // Создаем транзакцию для отправителя
            Transaction senderTransaction = new Transaction(UUID.randomUUID(), "Перевод пользователю " + recipientLogin, amount, false);
            senderWallet.addTransaction(senderTransaction);
        } else {
            System.out.println("Кошелек не найден");
        }

        Optional<Wallet> recipientWalletOpt = walletService.getWalletByUserId(userId);

        if (recipientWalletOpt.isPresent()) {

            // Создаем транзакцию для получателя
            Wallet recipientWallet = recipientWalletOpt.get();
            Transaction recipientTransaction = new Transaction(UUID.randomUUID(), "Перевод от пользователя " + userService.getUserById(userId).get().getLogin(), amount, true);
            recipientWallet.addTransaction(recipientTransaction);

            System.out.println("Перевод успешно выполнен.");
        } else {
            System.out.println("Кошелек не найден");
        }
        financialManagement();
    }

    private double getDoubleInput() {
        while (true) {
            if (scanner.hasNextDouble()) {
                double value = scanner.nextDouble();
                scanner.nextLine(); // Очистка буфера
                return value;
            } else {
                System.out.println("Пожалуйста, введите число.");
                scanner.nextLine(); // Очистка некорректного ввода
            }
        }
    }

    public int getIntInput() {
        while (true) {
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine(); // Очистка буфера
                return value;
            } else {
                System.out.println("Пожалуйста, введите число.");
                scanner.nextLine(); // Очистка некорректного ввода
            }
        }
    }

    private String getValidUsername() {
        String usernameRegex = "^[а-яА-Яa-zA-Z0-9_\\-\\s]+$"; // Регулярное выражение для проверки имени пользователя
        while (true) {
            String username = scanner.nextLine();
            if (username != null && !username.trim().isEmpty() && username.matches(usernameRegex)) {
                return username;
            } else {
                System.out.println("Имя пользователя не может быть пустым и должно содержать только буквы, цифры, тире, нижние подчеркивания и пробелы. Пожалуйста, введите имя пользователя:");
            }
        }
    }

    private String getValidPassword() {
        String passwordRegex = "^[а-яА-Яa-zA-Z0-9_\\-]+$"; // Регулярное выражение для проверки пароля
        while (true) {
            String password = scanner.nextLine();
            if (password != null && !password.trim().isEmpty() && password.matches(passwordRegex)) {
                return password;
            } else {
                System.out.println("Пароль не может быть пустым и должен содержать только буквы, цифры, тире, нижние подчеркивания. Пожалуйста, введите пароль:");
            }
        }
    }
}