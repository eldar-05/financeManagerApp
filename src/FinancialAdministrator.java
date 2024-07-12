import java.sql.*;

public class FinancialAdministrator extends User {

    public FinancialAdministrator(int userId, String username, String password) {
        super(userId, username, password, "financial_admin");
    }

    public boolean payScholarship(int studentId, double amount, Connection connection) {
        try {
            String sql = "UPDATE Students SET balance = balance + ? WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDouble(1, amount);
            statement.setInt(2, studentId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void generateReport(Connection connection) {
        try {
            String sql = "SELECT * FROM Students";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("Отчет по всем студентам:");
            while (resultSet.next()) {
                int studentId = resultSet.getInt("student_id");
                String name = resultSet.getString("name");
                String groupName = resultSet.getString("group_name");
                double balance = resultSet.getDouble("balance");
                System.out.printf("ID: %d, Имя: %s, Группа: %s, Баланс: $%.2f%n", studentId, name, groupName, balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
