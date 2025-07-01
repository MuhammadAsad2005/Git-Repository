package librarymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginForm extends JFrame {

    public LoginForm() {
        setTitle("Library Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Background Image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/library_bg.jpeg"));
        Image bgImage = bgIcon.getImage().getScaledInstance(900, 600, Image.SCALE_SMOOTH);
        JLabel backgroundLabel = new JLabel(new ImageIcon(bgImage));
        backgroundLabel.setLayout(new GridBagLayout());

        // Login Panel
        JPanel loginPanel = new JPanel();
        loginPanel.setPreferredSize(new Dimension(300, 250));
        loginPanel.setBackground(new Color(255, 255, 255, 180));
        loginPanel.setLayout(new GridLayout(5, 1, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form Fields
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginBtn = new JButton("🔐 Login");

        loginPanel.add(new JLabel("👤 Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("🔑 Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginBtn);

        backgroundLabel.add(loginPanel);
        setContentPane(backgroundLabel);

        // Login Button Action
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password", "Missing Fields", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (authenticateUser(username, password)) {
                dispose(); // Close login window only if successful
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    private boolean authenticateUser(String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "root", "");

            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                String role = rs.getString("role");

                // ✅ Redirection based on role
                if ("admin".equalsIgnoreCase(role)) {
                    new AdminDashboard(name).setVisible(true);
                } else if ("user".equalsIgnoreCase(role)) {
                    new MemberDashboard(userId, name, username).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Unknown user role in database.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                conn.close();
                return true;
            } else {
                conn.close();
                return false;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginForm::new);
    }
}
