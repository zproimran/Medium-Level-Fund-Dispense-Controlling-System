package smarthrms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; 

public class AddRole extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private final Connecting conn;

    public AddRole() {
        this.conn=new Connecting();
        setTitle("Add New Role");
        setSize(300, 300);
                // Set application icon
        ImageIcon appIcon = new ImageIcon(ClassLoader.getSystemResource("icons/appIcon.png"));
        setIconImage(appIcon.getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
        setAlwaysOnTop(true);
        setLocation(600, 100);
        // Define table columns
        String[] columnNames = {
            "RoleName"
        };
        // Create table model with editable cells
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add table to scroll pane
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);

        // Button panel for actions
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Row");
        JButton removeButton = new JButton("Remove Row");
        JButton submitButton = new JButton("Submit");
        JButton resetButton = new JButton("Reset");

        // Button styling
        addButton.setBackground(new Color(0, 153, 76));
        addButton.setForeground(Color.WHITE);
        removeButton.setBackground(new Color(204, 0, 0));
        removeButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(0, 102, 204));
        submitButton.setForeground(Color.WHITE);
        resetButton.setBackground(Color.GRAY);
        resetButton.setForeground(Color.WHITE);

        // Button actions
        // Button actions
        addButton.addActionListener(e -> {
    String roleName = JOptionPane.showInputDialog(this, "Enter role name:");
    if (roleName != null && !roleName.trim().isEmpty()) {
        tableModel.addRow(new Object[]{roleName});
    } else {
        JOptionPane.showMessageDialog(this, "Role name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
    }
});        removeButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        submitButton.addActionListener(e -> handleSubmit());
        resetButton.addActionListener(e -> tableModel.setRowCount(0));

        // Add buttons to panel
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleSubmit() {
    if (tableModel.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "No data entered!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    for (int i = 0; i < tableModel.getRowCount(); i++) {
        RoleNameModel role = new RoleNameModel(
            tableModel.getValueAt(i, 0).toString()
        );
        conn.insertRoleToDB(role);
    }
    tableModel.setRowCount(0);
    
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddRole form = new AddRole();
            form.setVisible(true);
        });
    }
}
