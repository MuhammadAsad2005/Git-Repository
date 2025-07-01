// ✅ MemberDashboard.java 
package librarymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.*;
import java.sql.*;

public class MemberDashboard extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private int userId;
    private String name;
    private String username;

    public MemberDashboard(int userId, String name, String username) {
        this.userId = userId;
        this.name = name;
        this.username = username;

        setTitle("📚 Member Dashboard - Welcome " + name);
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/library_bg.jpeg"));
        Image bgImage = bgIcon.getImage().getScaledInstance(1000, 600, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(bgImage));
        background.setLayout(new BorderLayout());

        // 🔼 Top Panel with Buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);

        searchField = new JTextField(20);
        JButton searchBtn = new JButton("🔍 Search");
        JButton readBtn = new JButton("📖 Read Online");
        JButton downloadBtn = new JButton("⬇ Download");
        JButton myHistoryBtn = new JButton("🧾 My History");
        JButton requestBookBtn = new JButton("📬 Request Book"); // ✅ New button
        JButton logoutBtn = new JButton("🔒 Logout");

        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(readBtn);
        topPanel.add(downloadBtn);
        topPanel.add(myHistoryBtn);
        topPanel.add(requestBookBtn); // ✅ Add button to panel
        topPanel.add(logoutBtn);

        background.add(topPanel, BorderLayout.NORTH);

        // 📚 Book Table
        String[] columns = {"ID", "Title", "Author", "Uploaded By", "Upload Date", "File Path"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        background.add(scrollPane, BorderLayout.CENTER);

        setContentPane(background);
        loadBooks("");

        // 🔽 Action Listeners
        searchBtn.addActionListener(e -> loadBooks(searchField.getText().trim()));
        readBtn.addActionListener(e -> openSelectedPDF("read"));
        downloadBtn.addActionListener(e -> openSelectedPDF("download"));
        myHistoryBtn.addActionListener(e -> new MemberHistoryWindow(userId).setVisible(true));
        requestBookBtn.addActionListener(e -> new RequestBookForm(userId).setVisible(true)); // ✅ Connected to request form
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        setVisible(true);
    }

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

    private void openSelectedPDF(String actionType) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "❗ Please select a book.");
            return;
        }

        int bookId = (int) model.getValueAt(row, 0);
        String title = model.getValueAt(row, 1).toString();
        String filePath = model.getValueAt(row, 5).toString();

        try {
            if ("read".equals(actionType)) {
                Desktop.getDesktop().open(new File(filePath));
                logActivity(bookId, "read");
            } else if ("download".equals(actionType)) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Save PDF As");
                chooser.setSelectedFile(new File(title + ".pdf"));
                if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File source = new File(filePath);
                    File dest = chooser.getSelectedFile();
                    Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    JOptionPane.showMessageDialog(this, "✅ Book downloaded successfully.");
                    logActivity(bookId, "download");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error opening file.");
            ex.printStackTrace();
        }
    }

    private void logActivity(int bookId, String action) {
        try {
            Connection conn = DataBaseConnection.getConnection();
            PreparedStatement logStmt = conn.prepareStatement(
                    "INSERT INTO activity_log (user_id, book_id, action, timestamp) VALUES (?, ?, ?, NOW())"
            );
            logStmt.setInt(1, userId);
            logStmt.setInt(2, bookId);
            logStmt.setString(3, action);
            logStmt.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
