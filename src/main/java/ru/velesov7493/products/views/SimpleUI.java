package ru.velesov7493.products.views;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SimpleUI extends JFrame {

    private final class BtnEventListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            model.refresh();
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        null,
                        "Выборка пуста!", "Информация",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }

    private ProductTableModel model;
    private JTable table;
    private JButton button;

    public SimpleUI() {
        super("SQLITE JDBC пример");
        setLocationRelativeTo(null);
        setSize(new Dimension(640, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new GridBagLayout());
        button = new JButton("Обновить");
        model = new ProductTableModel();
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(640, 480));
        button.addActionListener(new BtnEventListener());
        table.getColumn("id").setMaxWidth(40);
        add(scrollPane, new GridBagConstraints(
                0, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH,
                GridBagConstraints.BOTH,
                new Insets(1, 1, 1, 1),
                0, 0
        ));
        add(button, new GridBagConstraints(
                0, 1, 1, 1, 0, 0,
                GridBagConstraints.NORTH,
                GridBagConstraints.BOTH,
                new Insets(1, 1, 1, 1),
                0, 0
        ));
    }
}
