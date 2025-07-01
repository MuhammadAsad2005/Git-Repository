package librarymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DeleteBook extends JFrame {

    public DeleteBook() {
        setTitle("❌ Delete Book");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ✅ Background
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/library_bg.jpeg"));
        Image bgImage = bgIcon.getImage().getScaledInstance(900, 600, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(bgImage));
        background.setLayout(new GridBagLayout());

        // ✅ Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setPreferredSize(new Dimension(350, 150));
        formPanel.setBackground(new Color(255, 255, 255, 200));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField bookIdField = new JTextField();
        JButton deleteBtn = new JButton("🗑 Delete Book");

        formPanel.add(new JLabel("Enter Book ID:"));
        formPanel.add(bookIdField);
        formPanel.add(new JLabel(""));
        formPanel.add(deleteBtn);

        background.add(formPanel);
        setContentPane(background);

        deleteBtn.addActionListener(e -> {
            String bookId = bookIdField.getText().trim();

            if (bookId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Please enter a Book ID.");
                return;
            }

            try {
                // ✅ MySQL connection
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "root", "");

                // ✅ Delete query
                String sql = "DELETE FROM books WHERE book_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(bookId));
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "✅ Book deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Book ID not found.");
                }

                stmt.close();
                conn.close();
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid Book ID. Please enter a number.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Error occurred while deleting the book.");
            }
        });

        setVisible(true);
    }
}

