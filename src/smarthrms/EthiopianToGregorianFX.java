package smarthrms;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EthiopianToGregorianFX extends VBox {

    public EthiopianToGregorianFX() {
        buildUI();
    }

    private void buildUI() {
        // Set up the main VBox properties
        setPadding(new Insets(20));
        setSpacing(20);
        setAlignment(Pos.TOP_CENTER);

        // Title
        Label titleLabel = new Label("Ethiopian to Gregorian Date Converter");
        titleLabel.setFont(Font.font("Arial", 20));

        // Form container
        GridPane form = createForm();

        // Result label
        Label resultLabel = new Label();
        resultLabel.setFont(Font.font("Arial", 16));
        resultLabel.setStyle("-fx-text-fill: green;");

        // Copy button, initially hidden
        Button copyBtn = new Button("Copy Date");
        copyBtn.setFont(Font.font("Arial", 14));
        copyBtn.setVisible(false);

        // Convert button
        Button convertBtn = createConvertButton(form, resultLabel, copyBtn);

        // Copy button action
        copyBtn.setOnAction(e -> {
            String dateText = resultLabel.getText().replace("Gregorian Date: ", "");
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(dateText);
            clipboard.setContent(content);
        });

        // Add all components to VBox
        getChildren().addAll(titleLabel, form, convertBtn, resultLabel, copyBtn);
    }

    private GridPane createForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));

        // Year
        Label yearLabel = new Label("Ethiopian Year:");
        TextField yearField = new TextField();
        yearField.setPromptText("e.g. 2015");

        // Month
        Label monthLabel = new Label("Ethiopian Month:");
        ComboBox<Integer> monthCombo = new ComboBox<>();
        monthCombo.setItems(FXCollections.observableArrayList(
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
        ));
        monthCombo.setPromptText("Month 1-13");

        // Day
        Label dayLabel = new Label("Ethiopian Day:");
        TextField dayField = new TextField();
        dayField.setPromptText("1 - 30 (or 5/6 for month 13)");

        // Add components to form
        form.add(yearLabel, 0, 0);
        form.add(yearField, 1, 0);
        form.add(monthLabel, 0, 1);
        form.add(monthCombo, 1, 1);
        form.add(dayLabel, 0, 2);
        form.add(dayField, 1, 2);

        return form;
    }

    private Button createConvertButton(GridPane form, Label resultLabel, Button copyBtn) {
        Button convertBtn = new Button("Convert to Gregorian");
        convertBtn.setFont(Font.font("Arial", 14));

        convertBtn.setOnAction(e -> {
            TextField yearField = (TextField) form.getChildren().get(1);
            ComboBox<Integer> monthCombo = (ComboBox<Integer>) form.getChildren().get(3);
            TextField dayField = (TextField) form.getChildren().get(5);

            String yearText = yearField.getText();
            Integer month = monthCombo.getValue();
            String dayText = dayField.getText();

            if (yearText == null || yearText.isEmpty() || month == null || dayText == null || dayText.isEmpty()) {
                showAlert("Please enter year, month, and day.", Alert.AlertType.WARNING);
                return;
            }

            try {
                int year = Integer.parseInt(yearText);
                int day = Integer.parseInt(dayText);

                if (!validateInput(month, day)) {
                    return;
                }

                LocalDate gregorianDate = ethiopianToGregorian(year, month, day);

                // Format date as dd/MM/yyyy
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = gregorianDate.format(formatter);

                resultLabel.setText("Gregorian Date: " + formattedDate);
                copyBtn.setVisible(true);

            } catch (NumberFormatException ex) {
                showAlert("Year and Day must be valid integers.", Alert.AlertType.ERROR);
            }
        });

        return convertBtn;
    }

    private boolean validateInput(int month, int day) {
        if (month < 1 || month > 13) {
            showAlert("Month must be between 1 and 13.", Alert.AlertType.ERROR);
            return false;
        }

        if (month == 13) {
            if (day < 1 || day > 6) {
                showAlert("Day for month 13 must be between 1 and 6.", Alert.AlertType.ERROR);
                return false;
            }
        } else {
            if (day < 1 || day > 30) {
                showAlert("Day must be between 1 and 30 for months 1-12.", Alert.AlertType.ERROR);
                return false;
            }
        }
        return true;
    }

    /**
     * Ethiopian to Gregorian date conversion logic
     */
    private LocalDate ethiopianToGregorian(int ethYear, int ethMonth, int ethDay) {
        int gregYear = ethYear + 7;

        // Ethiopian New Year falls on Sept 11 unless next Gregorian year is leap year
        LocalDate gregNewYear = LocalDate.of(gregYear, 9, 11);
        if (LocalDate.of(gregYear + 1, 1, 1).isLeapYear()) {
            gregNewYear = LocalDate.of(gregYear, 9, 12);
        }

        int daysOffset = (ethMonth - 1) * 30 + (ethDay - 1);

        return gregNewYear.plusDays(daysOffset);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
