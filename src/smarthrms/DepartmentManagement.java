package smarthrms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.table.TableCellRenderer;

public class DepartmentManagement {
    private JTable userTable;  // Changed to non-static field
    JFrame frame;
    String[] columnNames;
    DefaultTableModel tableModel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DepartmentManagement::createAndShowGUI);
    }

    public static void createAndShowGUI() {
        // Create an instance of UserManagement class
        DepartmentManagement deptManagement = new DepartmentManagement();
        deptManagement.setupGUI();  // Use non-static method to setup the GUI
    }

    // Non-static method to setup the GUI
    private void setupGUI() {
        // Create main frame
        frame = new JFrame("Department Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);
        
        frame.setLayout(new BorderLayout());
        // Set application icon
        ImageIcon appIcon = new ImageIcon(ClassLoader.getSystemResource("icons/appIcon.png"));
        frame.setIconImage(appIcon.getImage());
        frame.getContentPane().setBackground(Color.white);

        // Disable maximize option
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);

        // Set background color for the main frame
        frame.getContentPane().setBackground(new Color(240, 240, 240));

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Departmet Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204)); // Blue color
        titlePanel.setBackground(new Color(240, 240, 240)); // Match background
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Button Panel for Add New User
        JPanel buttonPanel = new JPanel();
        JButton addDeptButton = new JButton("Add New Department");
        addDeptButton.setFont(new Font("Arial", Font.BOLD, 16));
        addDeptButton.setBackground(new Color(0, 102, 204)); // Blue background
        addDeptButton.setForeground(Color.WHITE); // White text
        addDeptButton.setFocusPainted(false);
        addDeptButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding inside the button
        buttonPanel.add(addDeptButton);
        frame.add(buttonPanel, BorderLayout.NORTH);
        JButton refreshButton = new JButton("Refresh");
        frame.add(refreshButton,BorderLayout.EAST);
        refreshButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
        //implement your refresh button for new requests
        fetchDepartments();
       // JOptionPane.showMessageDialog(userTable,"Refresh Successful","Refreshed", JOptionPane.INFORMATION_MESSAGE);
             
            }
        });
                // Button Panel for Add New User
        JButton viewDeptButton = new JButton("View Department");
        viewDeptButton.setFont(new Font("Arial", Font.BOLD, 16));
        viewDeptButton.setBackground(new Color(0, 102, 204)); // Blue background
        viewDeptButton.setForeground(Color.WHITE); // White text
        viewDeptButton.setFocusPainted(false);
        viewDeptButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding inside the button
        viewDeptButton.addActionListener(e -> viewSelectedRow());
        buttonPanel.add(viewDeptButton);

        // User Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        // Updated column names with three new columns for buttons
        String[] columnNames = {"UserID","DepartmentName", "", ""};
        Object[][] data = {}; // Initially empty data

        userTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(userTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
         tableModel = new DefaultTableModel(columnNames, 0);
        frame.add(tablePanel, BorderLayout.CENTER);

        // Action Listeners
        addDeptButton.addActionListener(e -> {
            // Open Add User Registration Frame
            new AddDepartment().setVisible(true);
        });

      fetchDepartments();

        // Show the frame
        frame.setVisible(true);
    }

    
    public void fetchDepartments(){
            // Populate user table with data from the database
        tableModel.setRowCount(0);
        Connecting conn = new Connecting();
        List<DepartmentNameModel> departments = conn.getAllDepartments();     
         int i=1;
        for (DepartmentNameModel department : departments) {
            
            Object[] row = {
                    i,
                    department.getDeptName(),
                    "Update", // Update button
                    "Delete"  // Delete button
            };
            tableModel.addRow(row);
            i++;
        }
        

        userTable.setModel(tableModel);

        // Set the column width for the action column (index 6, 7, and 8)
//userTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Activate/Deactivate
//userTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Update
//userTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Delete

// Add button cell renderer and editor for the actions columns
userTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
userTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));
userTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
userTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));

    }

// Custom button renderer for the action buttons
class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        setText(value != null ? value.toString() : ""); // Display button text
        setBackground(isSelected ? Color.CYAN : Color.WHITE); // Change background color when selected
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped()); // Stop editing when the button is clicked
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column) {
        this.label = (value != null) ? value.toString() : "";
        button.setText(label);

        // Add action listeners for the button clicks
        button.addActionListener(e -> {
            String deptName = table.getValueAt(row, 1).toString(); // Get user ID from the first column
            Connecting conn = new Connecting();
            boolean success;


            // Action for Delete button (index 8)
            if (label.equals("Delete")) {
                int response = JOptionPane.showConfirmDialog(
                null, 
                "Are you sure you want to delete Department?", 
                "Delete", 
                JOptionPane.YES_NO_OPTION
                );
             if (response == JOptionPane.YES_OPTION) {
             success = conn.deleteDept(deptName);
             if (success) {
                    JOptionPane.showMessageDialog(null, "Dept deleted successfully.");
                    ((DefaultTableModel) table.getModel()).removeRow(row); // Remove row from table
                } else {
                    JOptionPane.showMessageDialog(null, "Error deleting Dept.");
                }
        }     
            }

            // Action for Update button (index 7)
            if (label.equals("Update")) {
                // Show dialog to update user dat
                
                String[] dept = {"RECEPTION","OPD","IT","XRAY", "EMERGENCY", "PHARMACY","IPD","HR","LABORATORY","ADMINISTRATION","NICU","ICU","OR","GYNEACOLOGY","FINANCE"};
                String newDept = (String) JOptionPane.showInputDialog(null, "Select new Dept: ",
                        "Dept Selection", JOptionPane.QUESTION_MESSAGE, null, dept, dept[0]);

                // If any field is empty, do not proceed with the update
                if (newDept == null||newDept.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                    return;
                }

                // Update the user data in the database
                boolean updateSuccess = conn.updateDept(deptName,newDept);
                if (updateSuccess) {
                    JOptionPane.showMessageDialog(null, "Dept updated successfully.");
                   
                    // Update the table with the new information
                    table.setValueAt(newDept, row, 1); // Full name column
                    
                     return;
                } else {
                    JOptionPane.showMessageDialog(null, "Error updating dept.");
                     return;
                }
            }
        });

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }
}

private void viewSelectedRow() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a row to view!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        StringBuilder rowData = new StringBuilder("Request Details:\n");
        for (int i = 0; i < 2; i++) {
            rowData.append(userTable.getColumnName(i)).append(": ").append(userTable.getValueAt(selectedRow, i)).append("\n");
           
        }
        
        JOptionPane.showMessageDialog(null, rowData.toString(), "View Request", JOptionPane.INFORMATION_MESSAGE);
    }
}
