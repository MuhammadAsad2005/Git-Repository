package librarymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminUserHistory extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public AdminUserHistory() {
        setTitle("🧾 Admin - User Book History");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Column headers
        String[] columns = {"Issue ID", "Username", "Book Title", "Issue Date", "Due Date", "Return Date"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        loadUserHistory();

        setVisible(true);
    }

    private void loadUserHistory() {
        try {
            Connection conn = DataBaseConnection.getConnection();

            String sql = """
                SELECT ib.issue_id, u.username, b.title, ib.issue_date, ib.due_date, ib.return_date
                FROM issued_book ib
                JOIN users u ON ib.user_id = u.id
                JOIN books b ON ib.book_id = b.book_id
                ORDER BY ib.issue_date DESC
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("issue_id"),
                        rs.getString("username"),
                        rs.getString("title"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date"),
                        rs.getDate("return_date") != null ? rs.getDate("return_date") : "❌ Not Returned"
                });
            }

            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Failed to load user history.");
            e.printStackTrace();
        }
    }
}
