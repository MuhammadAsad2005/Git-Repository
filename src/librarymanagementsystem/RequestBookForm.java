package librarymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RequestBookForm extends JFrame {
    public RequestBookForm(int userId) {
        setTitle("📬 Request a New Book");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        JButton submitBtn = new JButton("📨 Submit Request");

        add(new JLabel("📖 Book Title:"));
        add(titleField);
        add(new JLabel("✍️ Author:"));
        add(authorField);
        add(new JLabel("📝 Description:"));
        add(new JScrollPane(descArea));
        add(new JLabel(""));
        add(submitBtn);

        submitBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String description = descArea.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❗ Book title is required.");
                return;
            }

            try {
                Connection conn = DataBaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO book_requests (user_id, book_title, author, description) VALUES (?, ?, ?, ?)"
                );
                stmt.setInt(1, userId);
                stmt.setString(2, title);
                stmt.setString(3, author);
                stmt.setString(4, description);
                stmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(this, "✅ Request submitted successfully!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Failed to submit request.");
                ex.printStackTrace();
            }
        });

        setVisible(true);
    }
}
