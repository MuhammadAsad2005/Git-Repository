package librarymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminRequestViewer extends JFrame {
    private JTable requestTable;
    private DefaultTableModel model;

    public AdminRequestViewer() {
        setTitle("📬 Book Requests");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table columns
        String[] columns = {"Request ID", "User ID", "Book Title", "Author", "Description", "Request Date", "Status"};
        model = new DefaultTableModel(columns, 0);
        requestTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(requestTable);
        add(scrollPane, BorderLayout.CENTER);

        loadRequests();

        setVisible(true);
    }

    private void loadRequests() {
        try {
            Connection conn = DataBaseConnection.getConnection();
            String sql = "SELECT * FROM book_requests ORDER BY request_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0); // Clear old data

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("request_id"),
                        rs.getInt("user_id"),
                        rs.getString("book_title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getTimestamp("request_date"),
                        rs.getString("status")
                });
            }

            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error loading book requests.");
            ex.printStackTrace();
        }
    }
}
