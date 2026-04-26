package smarthrms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PasswordReset {

    private final String currentUser; // Store the current user

    public PasswordReset(String currentUser) {
        this.currentUser = currentUser;
    }

    public void createAndShowGUI() {
        // Create the frame
        JFrame frame = new JFrame("Change Password");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setResizable(false); // Disable maximize button
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null); // Center on screen
        // Set application icon
        ImageIcon appIcon = new ImageIcon(ClassLoader.getSystemResource("icons/appIcon.png"));
        frame.setIconImage(appIcon.getImage());
        frame.getContentPane().setBackground(Color.white);

        // Create main panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245)); // Light gray background
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Display the current user
        JLabel userLabel = new JLabel("User: " + currentUser);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(userLabel, gbc);
        gbc.gridwidth = 1; // Reset to default


        // New Password Label
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(newPasswordLabel, gbc);

        // New Password Field
        JPasswordField newPasswordField = new JPasswordField(15);
        newPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(newPasswordField, gbc);

        // Confirm Password Label
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(confirmPasswordLabel, gbc);

        // Confirm Password Field
        JPasswordField confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // Show Password Checkbox
        JCheckBox showPasswordCheckbox = new JCheckBox("Show Password");
        showPasswordCheckbox.setFont(new Font("Arial", Font.PLAIN, 12));
        showPasswordCheckbox.addActionListener(e -> {
            char echoChar = showPasswordCheckbox.isSelected() ? (char) 0 : '*';
            newPasswordField.setEchoChar(echoChar);
            confirmPasswordField.setEchoChar(echoChar);
        });
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(showPasswordCheckbox, gbc);

        // Submit Button
        JButton submitButton = new JButton("Update Password");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(76, 175, 80)); // Green
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> {
            Connecting conn=new Connecting();
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(frame, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String hashedPassword = PasswordUtils.hashPassword(confirmPassword);
                conn.resetPassword(currentUser,hashedPassword);
                JOptionPane.showMessageDialog(frame, "Password updated successfully for user: " + currentUser, "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // Close the frame after successful update
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        // Make the frame visible
        frame.setVisible(true);
    }
}
