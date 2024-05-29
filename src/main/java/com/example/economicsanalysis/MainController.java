package com.example.economicsanalysis;

import com.example.economicsanalysis.Indicator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

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
    @FXML
    private Label uploadData;
    @FXML
    private Button plotButton;

    private final DecimalFormat df = new DecimalFormat("#.##");
    private Path dbFilePath; // Path to the writable database file

    @FXML
    private void initialize() {
        table.setEditable(true);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        previousYearColumn.setCellValueFactory(new PropertyValueFactory<>("previousYear"));
        currentYearColumn.setCellValueFactory(new PropertyValueFactory<>("currentYear"));
        absoluteDeviationColumn.setCellValueFactory(new PropertyValueFactory<>("absoluteDeviation"));
        growthRateColumn.setCellValueFactory(new PropertyValueFactory<>("growthRate"));

        nameColumn.setPrefWidth(200);
        previousYearColumn.setPrefWidth(150);
        currentYearColumn.setPrefWidth(150);
        absoluteDeviationColumn.setPrefWidth(150);
        growthRateColumn.setPrefWidth(150);

        nameColumn.setCellFactory(column -> new TableCell<Indicator, String>() {
            private final Text text = new Text();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    text.setText(item);
                    text.wrappingWidthProperty().bind(nameColumn.widthProperty().subtract(10));
                    setGraphic(text);
                }
            }
        });

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

        previousYearColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        currentYearColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        previousYearColumn.setOnEditCommit(event -> {
            Indicator indicator = event.getRowValue();
            indicator.setPreviousYear(event.getNewValue());
            recalculateAndRefresh(indicator);
        });

        currentYearColumn.setOnEditCommit(event -> {
            Indicator indicator = event.getRowValue();
            indicator.setCurrentYear(event.getNewValue());
            recalculateAndRefresh(indicator);
        });

        absoluteDeviationColumn.setCellFactory(cellFactory);
        growthRateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(df.format(item));
                    setTextFill(item >= 100 ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
                }
            }
        });
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                plotButton.setVisible(true);
            } else {
                plotButton.setVisible(false);
            }
        });

        // Extract the default database at initialization
        dbFilePath = extractDatabaseFile("/economics.db");
    }

    @FXML
    private void handlePlotGraph() {
        Indicator selectedIndicator = table.getSelectionModel().getSelectedItem();
        if (selectedIndicator != null) {
            showGraphWindow(selectedIndicator);
        }
    }

    private void showGraphWindow(Indicator indicator) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GraphWindow.fxml"));
            Parent root = loader.load();

            GraphController graphController = loader.getController();
            graphController.setIndicator(indicator);

            Stage stage = new Stage();
            stage.setTitle("График для " + indicator.getName());
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoadData() {
        loadData(dbFilePath);
    }

    @FXML
    private void handleLoadDataLocomotives() {
        clearTable();
        dbFilePath = extractDatabaseFile("/locomotives.db");
        uploadData.setText("Локомотивы");
        loadData(dbFilePath);
    }

    @FXML
    private void handleLoadDataEquipment() {
        clearTable();
        dbFilePath = extractDatabaseFile("/equipment.db");
        uploadData.setText("Оборудование");
        loadData(dbFilePath);
    }

    @FXML
    private void handleLoadDataMain() {
        clearTable();
        dbFilePath = extractDatabaseFile("/economics.db");
        uploadData.setText("Основные средства");
        loadData(dbFilePath);
    }

    @FXML
    private void handleOpenCalculationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CalculationWindow.fxml"));
            Parent root = loader.load();

            CalculationController calculationController = loader.getController();
            calculationController.setIndicators(table.getItems()); // Передаем текущие данные в CalculationController

            Stage stage = new Stage();
            stage.setTitle("Расчетные показатели");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData(Path dbFilePath) {
        ObservableList<Indicator> indicators = FXCollections.observableArrayList();

        if (dbFilePath != null) {
            String url = "jdbc:sqlite:" + dbFilePath.toString();

            try (Connection conn = DriverManager.getConnection(url)) {
                String query = "SELECT name, previous_year, current_year FROM indicators";
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
    private void handleCalculate() {
        for (Indicator indicator : table.getItems()) {
            recalculateAndRefresh(indicator);
        }
    }

    private void recalculateAndRefresh(Indicator indicator) {
        double absoluteDeviation = indicator.getCurrentYear() - indicator.getPreviousYear();
        double growthRate = (indicator.getPreviousYear() != 0) ? (indicator.getCurrentYear() / indicator.getPreviousYear()) * 100 : 0;
        indicator.setAbsoluteDeviation(absoluteDeviation);
        indicator.setGrowthRate(growthRate);
        table.refresh();
    }

    private void clearTable() {
        table.getItems().clear();
    }

    private Path extractDatabaseFile(String resourcePath) {
        Path targetFile = Path.of(System.getProperty("user.dir"), resourcePath.substring(1));
        try {
            if (!Files.exists(targetFile)) {
                InputStream inputStream = getClass().getResourceAsStream(resourcePath);
                if (inputStream == null) {
                    throw new IllegalStateException("Resource not found: " + resourcePath);
                }
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetFile;
    }

        // Close the alert
        private void showNotification(String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle("Информация");
            alert.show();

            // Close the alert after a short delay (2 seconds)
            new Thread(() -> {
                try {
                    // Simulate a delay
                    Thread.sleep(2000);

                    // Close the dialog in the JavaFX Application Thread
                    Platform.runLater(() -> {
                        if (alert.isShowing()) {
                            alert.close();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        public void handleUpdateData(ActionEvent actionEvent) {
            updateDatabase();
        }

        private void updateDatabase() {
            if (dbFilePath != null) {
                String url = "jdbc:sqlite:" + dbFilePath.toString();

                try (Connection conn = DriverManager.getConnection(url)) {
                    conn.setAutoCommit(false); // Start transaction

                    String updateQuery = "UPDATE indicators SET previous_year = ?, current_year = ? WHERE name = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                        for (Indicator indicator : table.getItems()) {
                            pstmt.setDouble(1, indicator.getPreviousYear());
                            pstmt.setDouble(2, indicator.getCurrentYear());
                            pstmt.setString(3, indicator.getName());
                            pstmt.addBatch();
                        }

                        pstmt.executeBatch(); // Execute all updates
                        conn.commit(); // Commit transaction
                        showNotification("Данные успешно обновлены!");
                    } catch (SQLException e) {
                        conn.rollback(); // Rollback transaction on error
                        e.printStackTrace();
                        showNotification("Ошибка при обновлении данных!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showNotification("Ошибка при подключении к базе данных!");
                }
            }
        }
    }