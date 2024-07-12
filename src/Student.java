import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Student {
    private int studentId;
    private int userId;
    private String name;
    private String groupName;
    private double balance;

    public Student(int studentId, int userId, String name, String groupName, double balance) {
        this.studentId = studentId;
        this.userId = userId;
        this.name = name;
        this.groupName = groupName;
        this.balance = balance;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getGroupName() {
        return groupName;
    }

    public double getBalance() {
        return balance;
    }

    public static Student getStudentByUserId(int userId, Connection connection) {
        try {
            String sql = "SELECT * FROM Students WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int studentId = resultSet.getInt("student_id");
                String name = resultSet.getString("name");
                String groupName = resultSet.getString("group_name");
                double balance = resultSet.getDouble("balance");
                return new Student(studentId, userId, name, groupName, balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void payTuition(double amount, Connection connection) {
        this.balance -= amount;
        try {
            String sql = "UPDATE Students SET balance = ? WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDouble(1, this.balance);
            statement.setInt(2, this.studentId);
            statement.executeUpdate();
            System.out.println("Оплата за обучение успешно произведена. Ваш новый баланс: $" + this.balance);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка при оплате за обучение.");
        }
    }
}
