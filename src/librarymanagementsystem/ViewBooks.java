package librarymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;

public class ViewBooks extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private int userId;

    public ViewBooks(int userId) {
        this.userId = userId;
        setTitle("📚 Browse Library");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Background
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/library_bg.jpeg"));
        Image bgImage = bgIcon.getImage().getScaledInstance(1000, 600, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(bgImage));
        background.setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("🔍 Search");
        JButton readBtn = new JButton("📖 Read Online");
        JButton downloadBtn = new JButton("⬇ Download");
        JButton historyBtn = new JButton("🧾 My History");
        JButton logoutBtn = new JButton("🔒 Logout");

        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(readBtn);
        topPanel.add(downloadBtn);
        topPanel.add(historyBtn);
        topPanel.add(logoutBtn);

        background.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Title", "Author", "Uploaded By", "Upload Date", "File Path"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        background.add(scrollPane, BorderLayout.CENTER);
        setContentPane(background);

        // Load all books initially
        loadBooks("");

        // Events
        searchBtn.addActionListener(e -> loadBooks(searchField.getText().trim()));

        readBtn.addActionListener(e -> {
            if (openSelectedPDF()) {
                logActivity("read");
            }
        });

        downloadBtn.addActionListener(e -> {
            if (downloadSelectedPDF()) {
                logActivity("download");
            }
        });

        historyBtn.addActionListener(e -> new MemberHistoryWindow(userId).setVisible(true));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        // Double-click to open PDF
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    if (openSelectedPDF()) {
                        logActivity("read");
                    }
                }
            }
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
            ex.printStackTrace();
        }
    }

    private boolean openSelectedPDF() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "❗ Please select a book to read.");
            return false;
        }
        String filePath = model.getValueAt(row, 5).toString();
        try {
            Desktop.getDesktop().open(new File(filePath));
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Unable to open PDF.");
            return false;
        }
    }

    private boolean downloadSelectedPDF() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "❗ Please select a book to download.");
            return false;
        }

        String filePath = model.getValueAt(row, 5).toString();
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save PDF As");
        chooser.setSelectedFile(new File(model.getValueAt(row, 1).toString() + ".pdf"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File source = new File(filePath);
            File dest = chooser.getSelectedFile();
            try {
                Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "✅ Book downloaded successfully!");
                return true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error during download.");
                return false;
            }
        }
        return false;
    }

    private void logActivity(String action) {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int bookId = (int) model.getValueAt(row, 0);
        try {
            Connection conn = DataBaseConnection.getConnection();
            String sql = "INSERT INTO activity_log (user_id, book_id, action, timestamp) VALUES (?, ?, ?, NOW())";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setString(3, action);
            stmt.executeUpdate();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
