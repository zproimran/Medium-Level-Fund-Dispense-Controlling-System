package smarthrms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.shape.Circle;

public class DefaultDashboard extends BorderPane {

    private final String currentUser;
    private final Connecting conn;

    public DefaultDashboard(String currentUser) {
        this.currentUser = currentUser;
        this.conn = new Connecting();
        buildUI();
    }

    /* ================= MAIN LAYOUT ================= */
    private void buildUI() {
        setStyle("-fx-background-color: #F4F6F9;");
        
       // setTop(createHeader());
        
        ScrollPane scrollPane = new ScrollPane(createScrollableContent());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        setCenter(scrollPane);
        
      // setBottom(createFooter());
    }

    /* ================= HEADER - Made Larger ================= */
//    private VBox createHeader() {
//        VBox header = new VBox(12);
//        header.setPadding(new Insets(30, 40, 30, 40));
//        header.setStyle("-fx-background-color: linear-gradient(to right, #1A202C, #2D3748);");
//
//        Label title = new Label("Smart HRMS Dashboard");
//        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
//        title.setTextFill(Color.WHITE);
//
//        String fullName = conn.getFullNameByUsername(currentUser);
//        Label user = new Label("Welcome back, " + fullName + " (" + currentUser + ")");
//        user.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
//        user.setTextFill(Color.LIGHTGRAY);
//
//        Label time = new Label(getNow());
//        time.setFont(Font.font("Segoe UI", 16));
//        time.setTextFill(Color.GRAY);
//
//        header.getChildren().addAll(title, user, time);
//        return header;
//    }

    /* ================= SCROLLABLE CONTENT ================= */
    private VBox createScrollableContent() {
        VBox wrapper = new VBox(40);
        wrapper.setPadding(new Insets(40));
        wrapper.setAlignment(Pos.TOP_CENTER);

        wrapper.getChildren().addAll(
                createStatCards(),
                createAllModulesSection(),
                createRecentActivity()
        );
        return wrapper;
    }

    /* ================= KPI STAT CARDS - Made Larger ================= */
    private HBox createStatCards() {
        HBox stats = new HBox(25);
        stats.setAlignment(Pos.CENTER);

        stats.getChildren().addAll(
                statCard("Total Employees", "128", "#3182CE", "/icons/users.png"),
                statCard("On Leave", "12", "#DD6B20", "/icons/leave.png"),
                statCard("Pending Requests", "9", "#805AD5", "/icons/alert.jpg"),
                statCard("Active Funds", "5", "#38A169", "/icons/money.jpg")
        );
        return stats;
    }

    private VBox statCard(String title, String value, String color, String iconPath) {
        VBox box = new VBox(10);
        box.setPrefSize(260, 140);
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 16;" +
                "-fx-border-width: 1;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);"
        );

        HBox titleBox = new HBox(12);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        // Add icon
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setFitWidth(28);
        icon.setFitHeight(28);
        
        Label t = new Label(title);
        t.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        t.setTextFill(Color.GRAY);
        
        titleBox.getChildren().addAll(icon, t);

        Label v = new Label(value);
        v.setFont(Font.font("Segoe UI", FontWeight.BOLD, 34));
        v.setTextFill(Color.web(color));

        box.getChildren().addAll(titleBox, v);
        
        // Add hover effect
        box.setOnMouseEntered(e -> {
            box.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #F7FAFC, white);" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: " + color + ";" +
                "-fx-border-radius: 16;" +
                "-fx-border-width: 2;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8);"
            );
        });
        
        box.setOnMouseExited(e -> {
            box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 16;" +
                "-fx-border-width: 1;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);"
            );
        });

        return box;
    }

    /* ================= ALL MODULES - Made Much Larger ================= */
    private VBox createAllModulesSection() {
        VBox section = new VBox(25);
        section.setAlignment(Pos.TOP_LEFT);
        section.setPrefWidth(Double.MAX_VALUE);

        Label title = new Label("System Modules");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2D3748"));

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));

        // Create larger module cards
        String[][] modules = {
            {"Employee Management", "/icons/users.png", "#2B6CB0", "Register employees, manage profiles, departments and positions."},
            {"Attendance & Devices", "/icons/attendance.png", "#805AD5", "Monitor attendance records and sync biometric devices."},
            {"Leave Management", "/icons/leave.png", "#DD6B20", "Apply, approve and track employee leave requests."},
            {"Payroll Processing", "/icons/payroll.png", "#38A169", "Calculate salaries, allowances, deductions and generate payslips."},
            {"Fund & Expense Management", "/icons/money.jpg", "#6B46C1", "Manage petty cash, expenses, purchases and fund tracking."},
            {"Reports & Analytics", "/icons/report.png", "#C53030", "Generate HR, payroll and finance reports for management."},
            {"User & Role Security", "/icons/security.jpg", "#4A5568", "Control system users, roles, permissions and access levels."},
            {"System Settings", "/icons/settings.png", "#2D3748", "Configure application settings, backups and system preferences."}
        };

        int col = 0, row = 0;
        for (String[] module : modules) {
            grid.add(moduleCardLarge(module[0], module[1], module[2], module[3]), col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }

        section.getChildren().addAll(title, grid);
        return section;
    }

    private VBox moduleCardLarge(String title, String iconPath, String color, String description) {
        VBox card = new VBox(15);
        card.setPrefSize(350, 220);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 18;" +
                "-fx-border-width: 1;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);"
        );

        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Larger icon
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setFitWidth(48);
        icon.setFitHeight(48);

        // Title with larger font
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web(color));

        headerBox.getChildren().addAll(icon, titleLabel);

        // Description with larger font and better line height
        Label desc = new Label(description);
        desc.setWrapText(true);
        desc.setLineSpacing(4);
        desc.setTextFill(Color.web("#4A5568"));
        desc.setFont(Font.font("Segoe UI", 14));
        desc.setPrefHeight(80);

        VBox.setVgrow(desc, Priority.ALWAYS);

        card.getChildren().addAll(headerBox, desc);

        // Enhanced hover effects
        card.setOnMouseEntered(e -> {
            card.setStyle(
                    "-fx-background-color: linear-gradient(145deg, #F7FAFC, white);" +
                    "-fx-background-radius: 18;" +
                    "-fx-border-color: " + color + ";" +
                    "-fx-border-radius: 18;" +
                    "-fx-border-width: 2;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8);"
            );
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 18;" +
                    "-fx-border-color: #E2E8F0;" +
                    "-fx-border-radius: 18;" +
                    "-fx-border-width: 1;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);"
            );
        });

        return card;
    }

    /* ================= RECENT ACTIVITY - Made Larger ================= */
    private VBox createRecentActivity() {
        VBox box = new VBox(20);
        box.setPrefWidth(Double.MAX_VALUE);
        box.setPadding(new Insets(30));
        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 18;" +
                "-fx-border-width: 1;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);"
        );

        Label title = new Label("Recent Activity");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2D3748"));

        VBox activityList = new VBox(15);
        
        // Sample activity items with timestamps
        String[][] activities = {
            {"Employee leave approved for John Doe", "Just now"},
            {"New fund request submitted by Finance Dept", "15 minutes ago"},
            {"Payroll processed successfully for March 2024", "1 hour ago"},
            {"User role updated for Sarah Johnson", "3 hours ago"},
            {"New employee registered: Michael Chen", "Yesterday"}
        };

        for (String[] activity : activities) {
            activityList.getChildren().add(activityItemLarge(activity[0], activity[1]));
        }

        box.getChildren().addAll(title, activityList);
        return box;
    }

    private HBox activityItemLarge(String text, String time) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12, 0, 12, 0));

        Circle bullet = new Circle(6, Color.web("#3182CE"));
        
        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("Segoe UI", 15));
        textLabel.setTextFill(Color.web("#4A5568"));
        
        Label timeLabel = new Label(time);
        timeLabel.setFont(Font.font("Segoe UI", 13));
        timeLabel.setTextFill(Color.GRAY);
        
        HBox.setHgrow(textLabel, Priority.ALWAYS);
        
        item.getChildren().addAll(bullet, textLabel, timeLabel);
        return item;
    }

    /* ================= FOOTER - Made Larger ================= */
//    private VBox createFooter() {
//        VBox footer = new VBox(8);
//        footer.setAlignment(Pos.CENTER);
//        footer.setPadding(new Insets(20));
//        footer.setStyle("-fx-background-color: #2D3748;");
//
//        Label copy = new Label("Smart HRMS © 2024 - Human Resource Management System");
//        copy.setFont(Font.font("Segoe UI", 14));
//        copy.setTextFill(Color.LIGHTGRAY);
//
//        Label version = new Label("Version 1.0.0 | Last Updated: March 2024");
//        version.setFont(Font.font("Segoe UI", 12));
//        version.setTextFill(Color.GRAY);
//
//        footer.getChildren().addAll(copy, version);
//        return footer;
//    }

    private String getNow() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy - hh:mm a"));
    }
}