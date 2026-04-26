package smarthrms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; 

public class AddDepartment extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private final Connecting conn;

    public AddDepartment() {
        this.conn=new Connecting();
        setTitle("Add New Department");
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
            "departmentName"
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
        addButton.addActionListener(e -> {
    String departmentName = JOptionPane.showInputDialog(this, "Enter department name:");
    if (departmentName != null && !departmentName.trim().isEmpty()) {
        tableModel.addRow(new Object[]{departmentName});
    } else {
        JOptionPane.showMessageDialog(this, "Department name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
    }
});

        removeButton.addActionListener(e -> {
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
        DepartmentNameModel department = new DepartmentNameModel(
            tableModel.getValueAt(i, 0).toString()
        );
        conn.insertDepartmentToDB(department);
    }
  tableModel.setRowCount(0);
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddDepartment form = new AddDepartment();
            form.setVisible(true);
        });
    }
}
