import java.sql.*;
import java.util.Scanner;

public class FinanceManagementApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection connection = connectToDatabase();
        if (connection != null) {
            boolean running = true;
            while (running) {
                System.out.println("______________________________________");
                System.out.println("| 1. Регистрация нового пользователя |");
                System.out.println("______________________________________");
                System.out.println();
                System.out.println("______________________________________");
                System.out.println("|         2. Вход в систему          |");
                System.out.println("______________________________________");
                System.out.println();
                System.out.println("______________________________________");
                System.out.println("|        3. \u001B[31mВыйти из программы\u001B[0m       |");
                System.out.println("______________________________________");
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        registerUser(scanner, connection);
                        break;
                    case 2:
                        loginUser(scanner, connection);
                        break;
                    case 3:
                        System.out.println("______________________________________");
                        System.out.println("|        3. \u001B[31mВыйти из программы\u001B[0m       |");
                        System.out.println("______________________________________");
                        running = false;
                        break;
                    default:
                        System.out.println("\u001B[31m✖ Неправильный выбор ✖\u001B[0m");

                }
                System.out.println();
                System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
                System.out.println();
            }
        } else {
            System.out.println("Ошибка sql connection");
        }
    }

    private static Connection connectToDatabase() {
        try {
            return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/university_database", "root", "eldarzh123");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void registerUser(Scanner scanner, Connection connection) {
        System.out.println("Выберите тип пользователя для регистрации:");
        System.out.println("______________________________________");
        System.out.println("|            1. Студент |             ");
        System.out.println("______________________________________");
        System.out.println();
        System.out.println("______________________________________");
        System.out.println("|            2. Родитель             |");
        System.out.println("______________________________________");
        System.out.println();
        System.out.println("______________________________________");
        System.out.println("|    3. Финансовый администратор     |");
        System.out.println("______________________________________");
        System.out.println();
        System.out.println("______________________________________");
        System.out.println("|        3. \u001B[31mСистемный администратор\u001B[0m       |");
        System.out.println("______________________________________");
        int userType = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Введите имя пользователя: ➳");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ➳");
        String password = scanner.nextLine();

        try {
            String sql = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, getUserRole(userType));
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    if (userType == 1) {
                        registerStudentDetails(scanner, connection, userId);
                    } else if (userType == 2) {
                        registerParentDetails(scanner, connection, userId);
                    } else {
                        System.out.println("✔ Пользователь успешно зарегистрирован ✔");
                    }
                }
            } else {
                System.out.println("\u001B[31m✖ Ошибка при регистрации пользователя ✖\u001B[0m");
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                System.out.println("\u001B[31mПользователь с таким именем уже существует. Попробуйте снова.\u001B[0m");
            } else {
                e.printStackTrace();
            }
        }
    }

    private static void registerStudentDetails(Scanner scanner, Connection connection, int userId) {
        System.out.print("Введите полное имя студента: ➳");
        String fullName = scanner.nextLine();
        System.out.print("Введите название группы: ➳");
        String groupName = scanner.nextLine();

        try {
            String sql = "INSERT INTO Students (user_id, name, group_name, balance) VALUES (?, ?, ?, 0.0)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            statement.setString(2, fullName);
            statement.setString(3, groupName);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int studentId = generatedKeys.getInt(1);
                    System.out.println();
                    System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
                    System.out.println();
                    System.out.println("Студент успешно зарегистрирован ✔ ");
                    System.out.println("ID студента: " + studentId);
                    System.out.println("!!! Не потеряйте свой ID студента !!!");
                }
            } else {
                System.out.println("Ошибка при регистрации студента.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void registerParentDetails(Scanner scanner, Connection connection, int userId) {
        System.out.print("Введите ID студента: ➳");
        int studentId = scanner.nextInt();
        scanner.nextLine();

        try {
            String sql = "INSERT INTO Parents (user_id, student_id) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, studentId);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Родитель успешно зарегистрирован ✔ ID студента: " + studentId);
            } else {
                System.out.println("✖ Ошибка при регистрации родителя ✖");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String getUserRole(int userType) {
        return switch (userType) {
            case 1 -> "student";
            case 2 -> "parent";
            case 3 -> "financial_admin";
            case 4 -> "system_admin";
            default -> "";
        };
    }

    private static void loginUser(Scanner scanner, Connection connection) {
        System.out.print("Введите имя пользователя: ➳");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ➳");
        String password = scanner.nextLine();

        try {
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String role = resultSet.getString("role");
                System.out.println("Вход выполнен успешно ✔");
                performRoleActions(userId, role, connection, scanner);
            } else {
                System.out.println("✖ Неправильное имя пользователя или пароль ✖");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void performRoleActions(int userId, String role, Connection connection, Scanner scanner) {
        switch (role) {
            case "system_admin" -> performSystemAdminActions(userId, connection, scanner);
            case "financial_admin" -> performFinancialAdminActions(userId, connection, scanner);
            case "student" -> performStudentActions(userId, connection, scanner);
            case "parent" -> performParentActions(userId, connection, scanner);
            default -> System.out.println("Неизвестная роль пользователя.");
        }
    }

    private static void performSystemAdminActions(int userId, Connection connection, Scanner scanner) {
        System.out.println("Добро пожаловать, \u001B[31mадминистратор системы\u001B[0m");
        boolean running = true;
        while (running) {
            System.out.println("______________________________________");
            System.out.println("| 1. Регистрация нового пользователя |");
            System.out.println("______________________________________");
            System.out.println();
            System.out.println("______________________________________");
            System.out.println("|      2. Удаление пользователя      |");
            System.out.println("______________________________________");
            System.out.println();
            System.out.println("______________________________________");
            System.out.println("| 3. Обновление данных пользователя  |");
            System.out.println("______________________________________");
            System.out.println();
            System.out.println("______________________________________");
            System.out.println("|    4. Изменение группы студента |   ");
            System.out.println("______________________________________");
            System.out.println();
            System.out.println("______________________________________");
            System.out.println("|        5. \u001B[31mВыйти из аккаунта\u001B[0m       |");
            System.out.println("______________________________________");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> registerUser(scanner, connection);
                case 2 -> deleteUser(scanner, connection);
                case 3 -> updateUserDetails(scanner, connection);
                case 4 -> changeStudentGroup(scanner, connection);
                case 5 -> running = false;
                case 6 -> {
                    System.out.println("\u001B[31mВыход из программы\u001B[0m");
                    System.exit(0);
                }
                default -> System.out.println("Неправильный выбор ✖");
            }
        }
    }

    private static void deleteUser(Scanner scanner, Connection connection) {
        System.out.print("Введите ID пользователя для \\u001B[31mудаления\\u001B[0m: ");

        int userId = scanner.nextInt();
        scanner.nextLine();

        try {
            String sql = "DELETE FROM Users WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Пользователь успешно удален ✔");
            } else {
                System.out.println("✖ Пользователь с указанным ID не найден ✖");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateUserDetails(Scanner scanner, Connection connection) {
        System.out.print("Введите ID пользователя для обновления: ➢");
        int userId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Введите новое имя пользователя: ➳");
        String newUsername = scanner.nextLine();
        System.out.print("Введите новый пароль: ➳");
        String newPassword = scanner.nextLine();

        try {
            String sql = "UPDATE Users SET username = ?, password = ? WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newUsername);
            statement.setString(2, newPassword);
            statement.setInt(3, userId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Данные пользователя успешно обновлены ✔");
            } else {
                System.out.println("✖ Пользователь с указанным ID не найден ✖");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void changeStudentGroup(Scanner scanner, Connection connection) {
        System.out.print("Введите ID студента для изменения группы: ➳");
        int studentId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Введите новое название группы: ➳");
        String newGroupName = scanner.nextLine();

        try {
            String sql = "UPDATE Students SET group_name = ? WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newGroupName);
            statement.setInt(2, studentId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Группа студента успешно изменена ✔");
            } else {
                System.out.println("✖ Студент с указанным ID не найден ✖");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void performFinancialAdminActions(int userId, Connection connection, Scanner scanner) {
        System.out.println("Добро пожаловать, \u001B[31mфинансовый администратор\u001B[0m");
        boolean running = true;
        while (running) {
            System.out.println("______________________________________");
            System.out.println("|   1. Просмотреть баланс студента   |");
            System.out.println("______________________________________");
            System.out.println();
            System.out.println("______________________________________");
            System.out.println("|    2. Обновить баланс студента     |");
            System.out.println("______________________________________");
            System.out.println();
            System.out.println("______________________________________");
            System.out.println("|        3. \u001B[31mВыйти из аккаунта\u001B[0m       |");
            System.out.println("______________________________________");
            System.out.println("______________________________________");
            System.out.println("|        4. \u001B[31mВыйти из программы\u001B[0m       |");
            System.out.println("______________________________________");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> viewStudentBalanceByAdmin(scanner, connection);
                case 2 -> updateStudentBalance(scanner, connection);
                case 3 -> running = false;
                case 4 -> {
                    System.out.println("Выход из программы.");
                    System.exit(0);
                }
                default -> System.out.println("✖ Неправильный выбор ✖");
            }
        }
    }

    private static void viewStudentBalanceByAdmin(Scanner scanner, Connection connection) {
        System.out.print("Введите ID студента: ➢");
        int studentId = scanner.nextInt();
        scanner.nextLine();

        try {
            String sql = "SELECT balance FROM Students WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                System.out.println("Баланс студента: $" + balance);
            } else {
                System.out.println("Студент с указанным ID не найден ✖");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateStudentBalance(Scanner scanner, Connection connection) {
        System.out.print("Введите ID студента: ➳");
        int studentId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Введите новый баланс: ➳");
        double newBalance = scanner.nextDouble();
        scanner.nextLine();

        try {
            String sql = "UPDATE Students SET balance = ? WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDouble(1, newBalance);
            statement.setInt(2, studentId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Баланс студента успешно обновлен ✔");
            } else {
                System.out.println("✖ Студент с указанным ID не найден ✖");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void performStudentActions(int userId, Connection connection, Scanner scanner) {
        System.out.println("Добро пожаловать, \u001B[31mстудент\u001B[0m");
        boolean running = true;
        while (running) {
            System.out.println("______________________________________");
            System.out.println("|     1. Просмотреть свой баланс     |");
            System.out.println("______________________________________");
            System.out.println();
            System.out.println("______________________________________");
            System.out.println("|        2. \u001B[31mВыйти из аккаунта\u001B[0m       |");
            System.out.println("______________________________________");
            System.out.println("______________________________________");
            System.out.println("|        3. \u001B[31mВыйти из программы\u001B[0m       |");
            System.out.println("______________________________________");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> viewStudentBalanceForStudent(userId, connection);
                case 2 -> running = false;
                case 3 -> {
                    System.out.println("Выход из программы.");
                    System.exit(0);
                }
                default -> System.out.println("Неправильный выбор.");
            }
        }
    }

    private static void viewStudentBalanceForStudent(int userId, Connection connection) {
        try {
            String sql = "SELECT balance FROM Students WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                System.out.println("Ваш баланс: $" + balance);
            } else {
                System.out.println("Студент с указанным ID не найден.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void performParentActions(int userId, Connection connection, Scanner scanner) {
        System.out.println("Добро пожаловать, \u001B[31mродитель\u001B[0m");
        int studentId = getStudentIdForParent(userId, connection);
        if (studentId == -1) {
            System.out.println("Студент \u001B[31mне найден\u001B[0m для этого родителя.");
            return;
        }
        boolean running = true;
        while (running) {
            System.out.println("______________________________________");
            System.out.println("|   1. Просмотреть баланс студента   |");
            System.out.println("______________________________________");
            System.out.println();
            System.out.println("______________________________________");
            System.out.println("|    2. Оплатить обучение студента   |");
            System.out.println("______________________________________");
            System.out.println();
            System.out.println("______________________________________");
            System.out.println("|        3. \u001B[31mВыйти из аккаунта\u001B[0m       |");
            System.out.println("______________________________________");
            System.out.println("______________________________________");
            System.out.println("|        4. \u001B[31mВыйти из программы\u001B[0m       |");
            System.out.println("______________________________________");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> viewStudentBalanceForParent(studentId, connection);
                case 2 -> payStudentTuition(studentId, connection, scanner);
                case 3 -> running = false;
                case 4 -> {
                    System.out.println("Выход из программы ✖");
                    System.exit(0);
                }
                default -> System.out.println("Неправильный выбор.");
            }
        }
    }

    private static int getStudentIdForParent(int parentId, Connection connection) {
        try {
            String sql = "SELECT student_id FROM Parents WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, parentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("student_id");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void viewStudentBalanceForParent(int studentId, Connection connection) {
        try {
            String sql = "SELECT name, balance FROM Students WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String studentName = resultSet.getString("name");
                double balance = resultSet.getDouble("balance");
                System.out.println("Имя студента: " + studentName);
                System.out.println("Баланс студента: $" + balance);
            } else {
                System.out.println("Студент с указанным ID не найден.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void payStudentTuition(int studentId, Connection connection, Scanner scanner) {
        System.out.print("Введите сумму для оплаты: (●'◡'●) ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        try {
            String sql = "SELECT balance FROM Students WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                double newBalance = balance - amount;
                if (newBalance < -2800) {
                    System.out.println("Оплата превысила контракт обучения (┬┬﹏┬┬) Баланс не может быть ниже -$2800.");
                } else {
                    sql = "UPDATE Students SET balance = ? WHERE student_id = ?";
                    statement = connection.prepareStatement(sql);
                    statement.setDouble(1, newBalance);
                    statement.setInt(2, studentId);
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Оплата успешно произведена ✔ Новый баланс: $" + newBalance);
                    } else {
                        System.out.println("✖ Ошибка при обновлении баланса студента ✖");
                    }
                }
            } else {
                System.out.println("✖ Студент с указанным ID не найден ✖");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}