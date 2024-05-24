package com.example.economicsanalysis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.control.TableCell;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;

public class MainController {
    @FXML
    private TableView<Indicator> table;
    @FXML
    private TableColumn<Indicator, String> nameColumn;
    @FXML
    private TableColumn<Indicator, Double> previousYearColumn;
    @FXML
    private TableColumn<Indicator, Double> currentYearColumn;
    @FXML
    private TableColumn<Indicator, Double> absoluteDeviationColumn;
    @FXML
    private TableColumn<Indicator, Double> growthRateColumn;

    private final DecimalFormat df = new DecimalFormat("#.##");

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        previousYearColumn.setCellValueFactory(new PropertyValueFactory<>("previousYear"));
        currentYearColumn.setCellValueFactory(new PropertyValueFactory<>("currentYear"));
        absoluteDeviationColumn.setCellValueFactory(new PropertyValueFactory<>("absoluteDeviation"));
        growthRateColumn.setCellValueFactory(new PropertyValueFactory<>("growthRate"));

        // Set preferred column widths
        nameColumn.setPrefWidth(200);
        previousYearColumn.setPrefWidth(150);
        currentYearColumn.setPrefWidth(150);
        absoluteDeviationColumn.setPrefWidth(150);
        growthRateColumn.setPrefWidth(150);

        // Add custom cell factory for formatting double values to two decimal places
        Callback<TableColumn<Indicator, Double>, TableCell<Indicator, Double>> cellFactory = column -> new TableCell<Indicator, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(df.format(item));
                }
            }
        };

        previousYearColumn.setCellFactory(cellFactory);
        currentYearColumn.setCellFactory(cellFactory);
        absoluteDeviationColumn.setCellFactory(cellFactory);
        growthRateColumn.setCellFactory(cellFactory);
    }

    @FXML
    private void handleLoadData() {
        ObservableList<Indicator> indicators = FXCollections.observableArrayList();
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile != null) {
            String url = "jdbc:sqlite:" + tempDbFile.toString();

            try (Connection conn = DriverManager.getConnection(url)) {
                String query = "SELECT * FROM indicators";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    String name = rs.getString("name");
                    double previousYear = rs.getDouble("previous_year");
                    double currentYear = rs.getDouble("current_year");
                    indicators.add(new Indicator(name, previousYear, currentYear));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            table.setItems(indicators);
        }
    }

    @FXML
    private void handleShowTable() {
        // Your logic to show the table goes here.
        // For now, we can simply call handleLoadData to load the data into the table.
        handleLoadData();
    }

    private Path extractDatabaseFile(String resourcePath) {
        Path tempFile = null;
        try {
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                throw new IllegalStateException("Resource not found: " + resourcePath);
            }
            tempFile = Files.createTempFile("economics", ".db");
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            tempFile.toFile().deleteOnExit(); // Ensure it gets deleted on exit
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }
}