package com.example.economicsanalysis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
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

    @FXML
    private Label uploadData;

    private final DecimalFormat df = new DecimalFormat("#.##");
    private String currentDbPath = "/economics.db";  // Default database

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
            updateDatabase(indicator);
            recalculateAndRefresh(indicator);
        });

        currentYearColumn.setOnEditCommit(event -> {
            Indicator indicator = event.getRowValue();
            indicator.setCurrentYear(event.getNewValue());
            updateDatabase(indicator);
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
    }

    @FXML
    private void handleLoadData() {
        loadData(currentDbPath);
    }

    @FXML
    private void handleLoadDataLocomotives() {
        clearTable();
        currentDbPath = "/locomotives.db";
        uploadData.setText("Локомотивы");
    }

    @FXML
    private void handleLoadDataEquipment() {
        clearTable();
        currentDbPath = "/equipment.db";
        uploadData.setText("Оборудование");
    }

    @FXML
    private void handleLoadDataMain() {
        clearTable();
        currentDbPath = "/economics.db";
        uploadData.setText("Основные средства");
    }

    @FXML
    private void handleOpenCalculationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CalculationWindow.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Расчетные показатели");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData(String dbPath) {
        ObservableList<Indicator> indicators = FXCollections.observableArrayList();
        Path tempDbFile = extractDatabaseFile(dbPath);

        if (tempDbFile != null) {
            String url = "jdbc:sqlite:" + tempDbFile.toString();

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
        double growthRate = (indicator.getCurrentYear() / indicator.getPreviousYear()) * 100;
        indicator.setAbsoluteDeviation(absoluteDeviation);
        indicator.setGrowthRate(growthRate);
        table.refresh();
    }

    private void updateDatabase(Indicator indicator) {
        Path tempDbFile = extractDatabaseFile(currentDbPath);
        if (tempDbFile != null) {
            String url = "jdbc:sqlite:" + tempDbFile.toString();

            try (Connection conn = DriverManager.getConnection(url)) {
                String updateQuery = "UPDATE indicators SET previous_year = ?, current_year = ? WHERE name = ?";
                PreparedStatement pstmt = conn.prepareStatement(updateQuery);
                pstmt.setDouble(1, indicator.getPreviousYear());
                pstmt.setDouble(2, indicator.getCurrentYear());
                pstmt.setString(3, indicator.getName());
                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearTable() {
        table.getItems().clear();
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
