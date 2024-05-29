package com.example.economicsanalysis;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class GraphController {
    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private NumberAxis yAxis;

    private Indicator indicator;

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
        loadChartData();
    }

    private void loadChartData() {
        if (indicator != null) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(indicator.getName());

            XYChart.Data<String, Number> previousYearData = new XYChart.Data<>("Предшествующий год", indicator.getPreviousYear());
            XYChart.Data<String, Number> currentYearData = new XYChart.Data<>("Отчетный год", indicator.getCurrentYear());

            // Добавление значений внутри кружков
            previousYearData.nodeProperty().addListener((obs, oldNode, newNode) -> addValueLabel(newNode, previousYearData.getYValue()));
            currentYearData.nodeProperty().addListener((obs, oldNode, newNode) -> addValueLabel(newNode, currentYearData.getYValue()));

            series.getData().add(previousYearData);
            series.getData().add(currentYearData);

            lineChart.getData().add(series);
        }
    }

    private void addValueLabel(javafx.scene.Node node, Number value) {
        if (node != null) {
            StackPane stackPane = (StackPane) node;
            Text dataText = new Text(value.toString());
            dataText.getStyleClass().add("chart-data-label");
            stackPane.getChildren().add(dataText);
            StackPane.setAlignment(dataText, javafx.geometry.Pos.CENTER);
            dataText.setTranslateY(0); // Убедитесь, что текст находится в центре
        }
    }
}