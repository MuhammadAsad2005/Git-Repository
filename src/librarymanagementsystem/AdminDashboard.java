package librarymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    public AdminDashboard(String username) {
        setTitle("📚 Admin Dashboard - Welcome " + username);
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/library_bg.jpeg"));
        Image bgImage = bgIcon.getImage().getScaledInstance(1000, 600, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(bgImage));
        background.setLayout(new BorderLayout());

        // Top panel with controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("🔍 Search");
        JButton viewAllBtn = new JButton("📚 View Books");
        JButton addBookBtn = new JButton("➕ Add Book");
        JButton deleteBookBtn = new JButton("❌ Delete Book");
        JButton viewRequestsBtn = new JButton("📬 View Requests"); // ✅ New button
        JButton logoutBtn = new JButton("🔒 Logout");

        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(viewAllBtn);
        topPanel.add(addBookBtn);
        topPanel.add(deleteBookBtn);
        topPanel.add(viewRequestsBtn); // ✅ Add to panel
        topPanel.add(logoutBtn);

        background.add(topPanel, BorderLayout.NORTH);

        // Book table
        String[] columns = {"ID", "Title", "Author", "Uploaded By (Username)", "Upload Date", "File Path"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        background.add(scrollPane, BorderLayout.CENTER);

        setContentPane(background);

        // Load all books initially
        loadBooks("");

        // Button functionality
        searchBtn.addActionListener(e -> loadBooks(searchField.getText().trim()));
        viewAllBtn.addActionListener(e -> {
            searchField.setText("");
            loadBooks("");
        });

        addBookBtn.addActionListener(e -> new AddBookForm(username));
        deleteBookBtn.addActionListener(e -> deleteSelectedBook());
        // ✅ Show request list
        viewRequestsBtn.addActionListener(e -> new AdminRequestViewer());

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        setVisible(true);
    }

    // 🔄 Load books by title/author (search)
    private void loadBooks(String keyword) {
        try {
            model.setRowCount(0);
            Connection conn = DataBaseConnection.getConnection();
            String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("uploaded_by"),
                        rs.getDate("upload_date"),
                        rs.getString("file_path")
                });
            }

            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error loading books.");
            ex.printStackTrace();
        }
    }

    // 🗑️ Delete selected book
    private void deleteSelectedBook() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "❗ Please select a book to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int bookId = (int) model.getValueAt(row, 0);

        try {
            Connection conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE book_id = ?");
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
            conn.close();

            model.removeRow(row);
            JOptionPane.showMessageDialog(this, "✅ Book deleted successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error deleting book.");
            ex.printStackTrace();
        }
    }
}
