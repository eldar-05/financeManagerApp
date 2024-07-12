import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Parent {
    private int parentId;
    private String username;
    private int studentId;

    public Parent(int parentId, String username, int studentId) {
        this.parentId = parentId;
        this.username = username;
        this.studentId = studentId;
    }

    public int getParentId() {
        return parentId;
    }

    public String getUsername() {
        return username;
    }

    public int getStudentId() {
        return studentId;
    }

    public static Parent getParentByUserId(int userId, Connection connection) {
        try {
            String sql = "SELECT * FROM Users WHERE user_id = ? AND role = 'parent'";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int parentId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                int studentId = resultSet.getInt("student_id");
                return new Parent(parentId, username, studentId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void viewStudentBalance(Connection connection) {
        try {
            String sql = "SELECT balance FROM Students WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, this.studentId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                System.out.println("Баланс студента: $" + balance);
                if (balance >= 2800) {
                    System.out.println("Оплачено на этот год.");
                }
            } else {
                System.out.println("Студент с указанным ID не найден.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void payTuition(double amount, Connection connection) {
        try {
            String sql = "SELECT balance FROM Students WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, this.studentId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                balance -= amount;
                sql = "UPDATE Students SET balance = ? WHERE student_id = ?";
                statement = connection.prepareStatement(sql);
                statement.setDouble(1, balance);
                statement.setInt(2, this.studentId);
                statement.executeUpdate();
                System.out.println("Оплата за обучение успешно произведена. Новый баланс студента: $" + balance);
            } else {
                System.out.println("Студент с указанным ID не найден.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка при оплате за обучение.");
        }
    }
}
