package librarymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class AddBookForm extends JFrame {
    private File selectedPDF;

    public AddBookForm(String uploadedByUsername) {  // This must be username, not name
        setTitle("➕ Add New Book");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Background setup
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/library_bg.jpeg"));
        Image bgImage = bgIcon.getImage().getScaledInstance(900, 600, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(bgImage));
        background.setLayout(new GridBagLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setPreferredSize(new Dimension(400, 250));
        formPanel.setBackground(new Color(255, 255, 255, 200));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JButton browseBtn = new JButton("📂 Browse PDF");
        JButton addBtn = new JButton("📚 Add Book");

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(browseBtn);
        formPanel.add(new JLabel(""));
        formPanel.add(addBtn);
        formPanel.add(new JLabel());

        background.add(formPanel);
        setContentPane(background);

        // PDF Browse
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedPDF = chooser.getSelectedFile();
                JOptionPane.showMessageDialog(this, "📄 Selected: " + selectedPDF.getName());
            }
        });

        // Add Book Logic
        addBtn.addActionListener(e -> {
            String title = titleField.getText();
            String author = authorField.getText();

            if (title.isEmpty() || author.isEmpty() || selectedPDF == null) {
                JOptionPane.showMessageDialog(this, "❌ Please fill all fields and select a PDF.");
                return;
            }

            try {
                // Upload directory
                String uploadDir = "uploaded_books";
                new File(uploadDir).mkdirs();
                File destFile = new File(uploadDir + "/" + selectedPDF.getName());
                Files.copy(selectedPDF.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Insert into DB
                Connection conn = DataBaseConnection.getConnection();
                String sql = "INSERT INTO books (title, author, file_path, uploaded_by, upload_date) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, title);
                stmt.setString(2, author);
                stmt.setString(3, destFile.getAbsolutePath());
                stmt.setString(4, uploadedByUsername); // Must be username, not full name
                stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
                stmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(this, "✅ Book added successfully!");
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Error adding book.");
            }
        });

        setVisible(true);
    }
}
