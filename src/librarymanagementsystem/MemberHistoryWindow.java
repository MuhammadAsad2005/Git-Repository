// ✅ MemberHistoryWindow.java
package librarymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MemberHistoryWindow extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public MemberHistoryWindow(int userId) {
        setTitle("🧾 My Activity History");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columns = {"Action", "Book Title", "Timestamp"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadHistory(userId);
        setVisible(true);
    }

    private void loadHistory(int userId) {
        try {
            Connection conn = DataBaseConnection.getConnection();
            String sql = """
                SELECT al.action, b.title, al.timestamp
                FROM activity_log al
                JOIN books b ON al.book_id = b.book_id
                WHERE al.user_id = ?
                ORDER BY al.timestamp DESC
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("action"),
                        rs.getString("title"),
                        rs.getTimestamp("timestamp")
                });
            }

            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Unable to load activity history.");
            e.printStackTrace();
        }
    }
}
