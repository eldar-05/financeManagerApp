import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SystemAdministrator extends User {

    public SystemAdministrator(int userId, String username, String password) {
        super(userId, username, password, "admin_system");
    }

    public boolean createUser(String username, String password, String role, Connection connection) {
        try {
            String sql = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Пользователь с таким именем уже существует. Попробуйте снова.");
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId, Connection connection) {
        try {
            String sql = "DELETE FROM Users WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(int userId, String newUsername, String newPassword, Connection connection) {
        try {
            String sql = "UPDATE Users SET username = ?, password = ? WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newUsername);
            statement.setString(2, newPassword);
            statement.setInt(3, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStudentGroup(int studentId, String newGroupName, Connection connection) {
        try {
            String sql = "UPDATE Students SET group_name = ? WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newGroupName);
            statement.setInt(2, studentId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
