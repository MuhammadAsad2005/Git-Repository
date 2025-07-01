package librarymanagementsystem;

import javax.swing.*;
import java.awt.*;

public class DownloadBook extends JFrame {
    public DownloadBook() {
        setTitle("📥 Download Book");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel label = new JLabel("📚 Select Book to Download:");
        JComboBox<String> bookList = new JComboBox<>(new String[]{
                "Java Basics.pdf", "Python Guide.pdf", "C++ Masterclass.pdf"
        });

        JButton downloadBtn = new JButton("⬇️ Download");

        panel.add(label);
        panel.add(bookList);
        panel.add(downloadBtn);

        add(panel);
        setVisible(true);
    }
}
