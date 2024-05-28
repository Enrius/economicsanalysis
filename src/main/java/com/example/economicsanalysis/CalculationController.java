package com.example.economicsanalysis;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CalculationController {
    @FXML
    private TextArea resultTextArea;
    @FXML
    private ComboBox<String> indicatorComboBox;

    private final DecimalFormat df = new DecimalFormat("#.###");
    private final Map<String, String> indicatorFormulas = new LinkedHashMap<>();

    @FXML
    private void initialize() {
        // Initialize indicator formulas map
        initializeIndicatorFormulas();

        // Set items for the indicator choice box
        indicatorComboBox.setItems(FXCollections.observableArrayList(indicatorFormulas.keySet()));
    }

    private void initializeIndicatorFormulas() {
        indicatorFormulas.put("Средний вес поезда брутто", "Тонно-километры (млн)/Пробег локомотивов в голове поездов (лок.-км)");
        indicatorFormulas.put("Среднесуточная производительность локомотивов", "Тонно-километры (млн)/Среднесуточная величина эксплуатируемого парка (лок.)");
        indicatorFormulas.put("Среднесуточная отдача локомотива", "Доходы от перевозок (тыс. руб)/Среднесуточная величина эксплуатируемого парка (лок.)");
        indicatorFormulas.put("Коэффициент соотношения темпов роста доходов от перевозок и величины амортизационных отчислений по локомотивам",
                "(Доходы от перевозок (тыс. руб за отчетный год)/Доходы от перевозок (тыс. руб за предшествующий год))/(Амортизационные отчисления по локомотивам (тыс. руб за отчетный год)/Амортизационные отчисления по локомотивам (тыс. руб за предшествующий год))");
        indicatorFormulas.put("Коэффициент соотношения темпов роста доходов от перевозок и величины затрат по содержанию и ремонту локомотивов","");
        indicatorFormulas.put("Фондоотдача ОС в натуральном выражении", "Объем работы в натуральном выражении (млн т/км брутто)/((Остаточная стоимость основных средств (тыс руб.) на начало года+Остаточная стоимость основных средств (тыс руб.) на конец года)/2)");
        indicatorFormulas.put("Фондоотдача ОС в стоимостном выражении", "");
        indicatorFormulas.put("Фондоотдача активной части ОС в натуральном выражении", "");
        indicatorFormulas.put("Фондоотдача активной части ОС в стоимостном выражении", "");
        indicatorFormulas.put("Фондоотдача транспортной части ОС в натуральном выражении", "");
        indicatorFormulas.put("Фондоотдача транспортной части ОС в стоимостном выражении", "");
        indicatorFormulas.put("Фондоотдача оборудования в натуральном выражении", "");
        indicatorFormulas.put("Фондоотдача оборудования в стоимостном выражении", "");
        indicatorFormulas.put("Фондоемкость", "");
        indicatorFormulas.put("Фондорентабельность", "");
        indicatorFormulas.put("Фондорентабельность активной части ОС", "");
        indicatorFormulas.put("Амортизациоемкость", "");
        indicatorFormulas.put("Относительная экономия ОС", "");
        indicatorFormulas.put("Относительная экономия активной части ОС", "");
        indicatorFormulas.put("Фондовооруженность", "");
        indicatorFormulas.put("Фондовооруженность активной части ОС", "");
        indicatorFormulas.put("Коэффициент использования парка наличного оборудования", "");
        indicatorFormulas.put("Коэффициент использования парка установленного оборудования", "");
        indicatorFormulas.put("Фактический фонд отработанного времени", "");
        indicatorFormulas.put("Коэффициент режимного фонда времени", "");
        indicatorFormulas.put("Удельный вес внеплановых простоев", "");
        indicatorFormulas.put("Удельный вес затрат времени на ремонт", "");
    }

    @FXML
    private void handleCalculateIndicator() {
        String selectedIndicator = indicatorComboBox.getValue();
        if (selectedIndicator != null) {
            calculateIndicator(selectedIndicator);
        }
    }

    private void calculateIndicator(String indicator) {
        StringBuilder result = new StringBuilder();
        Path tempDbFile = extractDatabaseFile("/locomotives.db");

        if (tempDbFile != null) {
            String url = "jdbc:sqlite:" + tempDbFile.toString();

            try (Connection conn = DriverManager.getConnection(url)) {
                Statement stmt = conn.createStatement();

                switch (indicator) {
                    case "Средний вес поезда брутто":
                        result.append(calculateAverageTrainWeight(stmt));
                        break;
                    case "Среднесуточная производительность локомотивов":
                        result.append(calculateDailyLocomotivePerformance(stmt));
                        break;
                    case "Среднесуточная отдача локомотива":
                        result.append(calculateDailyLocomotiveReturn(stmt));
                        break;
                    case "Коэффициент соотношения темпов роста доходов от перевозок и величины амортизационных отчислений по локомотивам":
                        result.append(calculateGrowthRateRatio(stmt));
                        break;
                    case "Коэффициент соотношения темпов роста доходов от перевозок и величины затрат по содержанию и ремонту локомотивов":
                        result.append(calculateZatrRateRatio(stmt));
                        break;
                    case "Фондоотдача ОС в натуральном выражении":
                        result.append(calculateFondootdacha());
                        break;
                    case "Фондоотдача ОС в стоимостном выражении":
                        result.append(calculateFondootdachaNat());
                        break;
                    case "Фондоотдача активной части ОС в натуральном выражении":
                        result.append(calculateFondootdachaAct());
                        break;
                    case "Фондоотдача активной части ОС в стоимостном выражении":
                        result.append(calculateFondootdachaActSt());
                        break;
                    case "Фондоотдача транспортной части ОС в натуральном выражении":
                        result.append(calculateFondootdachaTran());
                        break;
                    case "Фондоотдача транспортной части ОС в стоимостном выражении":
                        result.append(calculateFondootdachaTranSt());
                        break;
                    case "Фондоотдача оборудования в натуральном выражении":
                        result.append(calculateFondootdachaObor());
                        break;
                    case "Фондоотдача оборудования в стоимостном выражении":
                        result.append(calculateFondootdachaOborSt());
                        break;
                    case "Фондоемкость":
                        result.append(calculateFondoemkost());
                        break;
                    case "Фондорентабельность":
                        result.append(calculateFondorent());
                        break;
                    case "Фондорентабельность активной части ОС":
                        result.append(calculateFondorentAct());
                        break;
                    case "Амортизациоемкость":
                        result.append(calculateAmort());
                        break;
                    case "Относительная экономия ОС":
                        result.append(calculateOtnosEconomy());
                        break;
                    case "Относительная экономия активной части ОС":
                        result.append(calculateOtnosEconomyAct());
                        break;
                    case "Фондовооруженность":
                        result.append(calculateFondovor());
                        break;
                    case "Фондовооруженность активной части ОС":
                        result.append(calculateFondovorAct());
                        break;
                    case "Коэффициент использования парка наличного оборудования":
                        result.append(calculateCoefObor());
                        break;
                    case "Коэффициент использования парка установленного оборудования":
                        result.append(calculateCoefOborIsp());
                        break;
                    case "Фактический фонд отработанного времени":
                        result.append(calculateFactTime());
                        break;
                    case "Коэффициент режимного фонда времени":
                        result.append(calculateCoefRezh());
                        break;
                    case "Удельный вес внеплановых простоев":
                        result.append(calculateUdVes());
                        break;
                    case "Удельный вес затрат времени на ремонт":
                        result.append(calculateUdZatr());
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            resultTextArea.setText(result.toString());
        }
    }

    private String calculateAverageTrainWeight(Statement stmt) throws Exception {
        String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Тонно-километры (млн)', 'Пробег локомотивов в голове поездов (лок.-км)')";
        ResultSet rs = stmt.executeQuery(query);

        double previousTonKm = 0;
        double currentTonKm = 0;
        double previousRun = 0;
        double currentRun = 0;

        while (rs.next()) {
            String name = rs.getString("name");
            double previousYear = rs.getDouble("previous_year");
            double currentYear = rs.getDouble("current_year");

            if (name.equals("Тонно-километры (млн)")) {
                previousTonKm = previousYear;
                currentTonKm = currentYear;
            } else if (name.equals("Пробег локомотивов в голове поездов (лок.-км)")) {
                previousRun = previousYear;
                currentRun = currentYear;
            }
        }

        // Проверка на деление на ноль
        if (previousRun == 0 || currentRun == 0) {
            return "Ошибка: Пробег локомотивов в голове поездов не может быть равен нулю.";
        }

        double previousResult = previousTonKm * 1000000 / previousRun;
        double currentResult = currentTonKm * 1000000 / currentRun;

        return String.format("Средний вес поезда брутто (предшествующий год) = %s%nСредний вес поезда брутто (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateDailyLocomotivePerformance(Statement stmt) throws Exception {
        String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Тонно-километры (млн)', 'Среднесуточная величина эксплуатируемого парка локомотивов (лок. в среднем за сутки)')";
        ResultSet rs = stmt.executeQuery(query);

        double previousTonKm = 0;
        double currentTonKm = 0;
        double previousPark = 0;
        double currentPark = 0;

        while (rs.next()) {
            String name = rs.getString("name");
            double previousYear = rs.getDouble("previous_year");
            double currentYear = rs.getDouble("current_year");

            if (name.equals("Тонно-километры (млн)")) {
                previousTonKm = previousYear;
                currentTonKm = currentYear;
            } else if (name.equals("Среднесуточная величина эксплуатируемого парка локомотивов (лок. в среднем за сутки)")) {
                previousPark = previousYear;
                currentPark = currentYear;
            }
        }

        // Проверка на деление на ноль
        if (previousPark == 0 || currentPark == 0) {
            return "Ошибка: Среднесуточная величина эксплуатируемого парка не может быть равна нулю.";
        }

        double previousResult = previousTonKm / previousPark;
        double currentResult = currentTonKm / currentPark;

        return String.format("Среднесуточная производительность локомотивов (предшествующий год) = %s%nСреднесуточная производительность локомотивов (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateDailyLocomotiveReturn(Statement stmt) throws Exception {
        String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Доходы от перевозок (тыс. руб)', 'Среднесуточная величина эксплуатируемого парка локомотивов (лок. в среднем за сутки)')";
        ResultSet rs = stmt.executeQuery(query);

        double previousIncome = 0;
        double currentIncome = 0;
        double previousPark = 0;
        double currentPark = 0;

        while (rs.next()) {
            String name = rs.getString("name");
            double previousYear = rs.getDouble("previous_year");
            double currentYear = rs.getDouble("current_year");

            if (name.equals("Доходы от перевозок (тыс. руб)")) {
                previousIncome = previousYear;
                currentIncome = currentYear;
            } else if (name.equals("Среднесуточная величина эксплуатируемого парка локомотивов (лок. в среднем за сутки)")) {
                previousPark = previousYear;
                currentPark = currentYear;
            }
        }

        // Проверка на деление на ноль
        if (previousPark == 0 || currentPark == 0) {
            return "Ошибка: Среднесуточная величина эксплуатируемого парка не может быть равна нулю.";
        }

        double previousResult = previousIncome / previousPark;
        double currentResult = currentIncome / currentPark;

        return String.format("Среднесуточная отдача локомотива (предшествующий год) = %s%nСреднесуточная отдача локомотива (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateGrowthRateRatio(Statement stmt) throws Exception {
        String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Доходы от перевозок (тыс. руб)', 'Амортизационные отчисления по локомотивам (тыс. руб за год)')";
        ResultSet rs = stmt.executeQuery(query);

        double previousIncome = 0;
        double currentIncome = 0;
        double previousAmortization = 0;
        double currentAmortization = 0;

        while (rs.next()) {
            String name = rs.getString("name");
            double previousYear = rs.getDouble("previous_year");
            double currentYear = rs.getDouble("current_year");

            if (name.equals("Доходы от перевозок (тыс. руб)")) {
                previousIncome = previousYear;
                currentIncome = currentYear;
            } else if (name.equals("Амортизационные отчисления по локомотивам (тыс. руб за год)")) {
                previousAmortization = previousYear;
                currentAmortization = currentYear;
            }
        }

        // Проверка на деление на ноль
        if (previousIncome == 0 || previousAmortization == 0) {
            return "Ошибка: Доходы от перевозок или амортизационные отчисления за предшествующий год не могут быть равны нулю.";
        }

        double incomeGrowthRate = currentIncome / previousIncome;
        double amortizationGrowthRate = currentAmortization / previousAmortization;
        double result = incomeGrowthRate / amortizationGrowthRate;

        return String.format("Коэффициент соотношения темпов роста доходов от перевозок и величины амортизационных отчислений по локомотивам = %s%n",
                df.format(result));
    }

    private String calculateZatrRateRatio(Statement stmt) throws Exception {
        String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Доходы от перевозок (тыс. руб)', 'Затраты по содержанию и ремонту локомотивов (тыс. руб)')";
        ResultSet rs = stmt.executeQuery(query);

        double previousIncome = 0;
        double currentIncome = 0;
        double previousZatr = 0;
        double currentZatr = 0;

        while (rs.next()) {
            String name = rs.getString("name");
            double previousYear = rs.getDouble("previous_year");
            double currentYear = rs.getDouble("current_year");

            if (name.equals("Доходы от перевозок (тыс. руб)")) {
                previousIncome = previousYear;
                currentIncome = currentYear;
            } else if (name.equals("Затраты по содержанию и ремонту локомотивов (тыс. руб)")) {
                previousZatr = previousYear;
                currentZatr = currentYear;
            }
        }

        // Проверка на деление на ноль
        if (previousIncome == 0 || previousZatr == 0) {
            return "Ошибка: Доходы от перевозок или затраты по содержанию и ремонту локомотивов за предшествующий год не могут быть равны нулю.";
        }

        double incomeGrowthRate = currentIncome / previousIncome;
        double zatrGrowthRate = currentZatr / previousZatr;
        double result = incomeGrowthRate / zatrGrowthRate;

        return String.format("Коэффициент соотношения темпов роста доходов от перевозок и величины затрат по содержанию и ремонту локомотивов = %s%n",
                df.format(result));
    }

    private String calculateFondootdacha() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Объем работы в натуральном выражении (млн т/км брутто)', 'Остаточная стоимость основных средств (тыс руб.) на начало года', 'Остаточная стоимость основных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Объем работы в натуральном выражении (млн т/км брутто)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость основных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость основных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость основных средств не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousWorkVolume * 1000000 / previousAverageAssetValue;
            double currentResult = currentWorkVolume *1000000 / currentAverageAssetValue;

            return String.format("Фондоотдача ОС в натуральном выражении (предшествующий год) = %s%nФондоотдача ОС в натуральном выражении (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFondootdachaNat() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Выручка от реализации продукции (тыс руб.)', 'Остаточная стоимость основных средств (тыс руб.) на начало года', 'Остаточная стоимость основных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Выручка от реализации продукции (тыс руб.)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость основных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость основных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость основных средств не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousWorkVolume / previousAverageAssetValue;
            double currentResult = currentWorkVolume / currentAverageAssetValue;

            return String.format("Фондоотдача ОС в стоимостном выражении (предшествующий год) = %s%nФондоотдача ОС в стоимостном выражении (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFondootdachaTran() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Объем работы в натуральном выражении (млн т/км брутто)', 'Остаточная стоимость транспортных средств (тыс руб.) на начало года', 'Остаточная стоимость транспортных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Объем работы в натуральном выражении (млн т/км брутто)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость транспортных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость транспортных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость транспортной части основных средств не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousWorkVolume * 1000 / previousAverageAssetValue;
            double currentResult = currentWorkVolume * 1000 / currentAverageAssetValue;

            return String.format("Фондоотдача транспортной части ОС в натуральном выражении (предшествующий год) = %s%nФондоотдача транспортной части ОС в натуральном выражении (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFondootdachaTranSt() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Выручка от реализации продукции (тыс руб.)', 'Остаточная стоимость транспортных средств (тыс руб.) на начало года', 'Остаточная стоимость транспортных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Выручка от реализации продукции (тыс руб.)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость транспортных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость транспортных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость транспортной части основных средств не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousWorkVolume / previousAverageAssetValue;
            double currentResult = currentWorkVolume / currentAverageAssetValue;

            return String.format("Фондоотдача транспортной части ОС в стоимостном выражении (предшествующий год) = %s%nФондоотдача транспортной части ОС в стоимостном выражении (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFondootdachaAct() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Объем работы в натуральном выражении (млн т/км брутто)', 'Остаточная стоимость активной части основных средств (тыс руб.) на начало года', 'Остаточная стоимость активной части основных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Объем работы в натуральном выражении (млн т/км брутто)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость активной части основных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость активной части основных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость активной части основных средств не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousWorkVolume * 1000 / previousAverageAssetValue;
            double currentResult = currentWorkVolume * 1000 / currentAverageAssetValue;

            return String.format("Фондоотдача активной части ОС в натуральном выражении (предшествующий год) = %s%nФондоотдача активной части ОС в натуральном выражении (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFondootdachaActSt() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Выручка от реализации продукции (тыс руб.)', 'Остаточная стоимость активной части основных средств (тыс руб.) на начало года', 'Остаточная стоимость активной части основных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Выручка от реализации продукции (тыс руб.)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость активной части основных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость активной части основных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость активной части основных средств не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousWorkVolume / previousAverageAssetValue;
            double currentResult = currentWorkVolume / currentAverageAssetValue;

            return String.format("Фондоотдача активной части ОС в стоимостном выражении (предшествующий год) = %s%nФондоотдача активной части ОС в стоимостном выражении (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFondootdachaObor() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Объем работы в натуральном выражении (млн т/км брутто)', 'Остаточная стоимость машин и оборудования (тыс руб.) на начало года', 'Остаточная стоимость машин и оборудования (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Объем работы в натуральном выражении (млн т/км брутто)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость машин и оборудования (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость машин и оборудования (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость оборудования не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousWorkVolume * 1000 / previousAverageAssetValue;
            double currentResult = currentWorkVolume * 1000 / currentAverageAssetValue;

            return String.format("Фондоотдача оборудования в натуральном выражении (предшествующий год) = %s%nФондоотдача оборудования в натуральном выражении (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFondootdachaOborSt() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Выручка от реализации продукции (тыс руб.)', 'Остаточная стоимость машин и оборудования (тыс руб.) на начало года', 'Остаточная стоимость машин и оборудования (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Выручка от реализации продукции (тыс руб.)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость машин и оборудования (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость машин и оборудования (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость оборудования не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousWorkVolume / previousAverageAssetValue;
            double currentResult = currentWorkVolume / currentAverageAssetValue;

            return String.format("Фондоотдача оборудования в стоимостном выражении (предшествующий год) = %s%nФондоотдача оборудования в стоимостном выражении (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFondoemkost() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Выручка от реализации продукции (тыс руб.)', 'Остаточная стоимость основных средств (тыс руб.) на начало года', 'Остаточная стоимость основных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Выручка от реализации продукции (тыс руб.)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость основных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость основных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость основных средств не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousAverageAssetValue / previousWorkVolume;
            double currentResult = currentAverageAssetValue / currentWorkVolume;

            return String.format("Фондоемкость (предшествующий год) = %s%nФондоемкость (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFondorent() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Прибыль (убыток) до налогообложения (тыс руб.)', 'Остаточная стоимость основных средств (тыс руб.) на начало года', 'Остаточная стоимость основных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Прибыль (убыток) до налогообложения (тыс руб.)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость основных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость основных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость основных средств не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousWorkVolume / previousAverageAssetValue * 100;
            double currentResult = currentWorkVolume / currentAverageAssetValue * 100;

            return String.format("Фондорентабельность (предшествующий год) = %s%nФондорентабельность (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFondorentAct() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Прибыль (убыток) до налогообложения (тыс руб.)', 'Остаточная стоимость активной части основных средств (тыс руб.) на начало года', 'Остаточная стоимость активной части основных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Прибыль (убыток) до налогообложения (тыс руб.)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость активной части основных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость активной части основных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость активной части основных средств не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousWorkVolume / previousAverageAssetValue * 100;
            double currentResult = currentWorkVolume / currentAverageAssetValue * 100;

            return String.format("Фондорентабельность активной части ОС (предшествующий год) = %s%nФондорентабельность активной части ОС (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateAmort() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Общая сумма амортизации включаемая в себестоимость (тыс руб.)', 'Выручка от реализации продукции (тыс руб.)')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;


            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Общая сумма амортизации включаемая в себестоимость (тыс руб.)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Выручка от реализации продукции (тыс руб.)")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0) {
                return "Ошибка: Остаточная стоимость активной части основных средств не может быть равна нулю.";
            }

            double previousResult = previousWorkVolume / previousAssetValueStart;
            double currentResult = currentWorkVolume / currentAssetValueStart;

            return String.format("Амортизациоемкость (предшествующий год) = %s%nАмортизациоемкость (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateOtnosEconomy() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Объем работы в натуральном выражении (млн т/км брутто)', 'Первоначальная стоимость основных средств (тыс руб.) на начало года', 'Первоначальная стоимость основных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Объем работы в натуральном выражении (млн т/км брутто)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Первоначальная стоимость основных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Первоначальная стоимость основных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Первоначальная стоимость ОС не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double currentResult = (currentAverageAssetValue - previousAverageAssetValue) * (currentWorkVolume/previousWorkVolume);

            return String.format("Относительная экономия ОС = %s%n",
                    df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateOtnosEconomyAct() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Объем работы в натуральном выражении (млн т/км брутто)', 'Первоначальная стоимость активной части основных средств (тыс руб.) на начало года', 'Первоначальная стоимость активной части основных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Объем работы в натуральном выражении (млн т/км брутто)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Первоначальная стоимость активной части основных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Первоначальная стоимость активной части основных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Первоначальная стоимость ОС не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double currentResult = (currentAverageAssetValue - previousAverageAssetValue) * (currentWorkVolume/previousWorkVolume);

            return String.format("Относительная экономия активной части ОС = %s%n",
                    df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFondovor() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Среднесписочная численность работников всего (чел.)', 'Остаточная стоимость основных средств (тыс руб.) на начало года', 'Остаточная стоимость основных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Среднесписочная численность работников всего (чел.)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость основных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость основных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость активной части основных средств не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousAverageAssetValue / previousWorkVolume;
            double currentResult = currentAverageAssetValue / currentWorkVolume;

            return String.format("Фондовооруженность активной части ОС (предшествующий год) = %s%nФондовооруженность активной части ОС (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFondovorAct() throws Exception {
        Path tempDbFile = extractDatabaseFile("/economics.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных economics.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Среднесписочная численность работников всего (чел.)', 'Остаточная стоимость активной части основных средств (тыс руб.) на начало года', 'Остаточная стоимость активной части основных средств (тыс руб.) на конец года')";
            ResultSet rs = stmt.executeQuery(query);

            double previousWorkVolume = 0;
            double currentWorkVolume = 0;
            double previousAssetValueStart = 0;
            double currentAssetValueStart = 0;
            double previousAssetValueEnd = 0;
            double currentAssetValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Среднесписочная численность работников всего (чел.)")) {
                    previousWorkVolume = previousYear;
                    currentWorkVolume = currentYear;
                } else if (name.equals("Остаточная стоимость активной части основных средств (тыс руб.) на начало года")) {
                    previousAssetValueStart = previousYear;
                    currentAssetValueStart = currentYear;
                } else if (name.equals("Остаточная стоимость активной части основных средств (тыс руб.) на конец года")) {
                    previousAssetValueEnd = previousYear;
                    currentAssetValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
                return "Ошибка: Остаточная стоимость активной части основных средств не может быть равна нулю.";
            }

            double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
            double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

            double previousResult = previousAverageAssetValue / previousWorkVolume;
            double currentResult = currentAverageAssetValue / currentWorkVolume;

            return String.format("Фондовооруженность активной части ОС (предшествующий год) = %s%nФондовооруженность активной части ОС (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateCoefObor() throws Exception {
        Path tempDbFile = extractDatabaseFile("/equipment.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных equipment.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Количество используемого оборудования (машины и оборудования)', 'Количество наличного оборудования')";
            ResultSet rs = stmt.executeQuery(query);

            double previousEquipValueStart = 0;
            double currentEquipValueStart = 0;
            double previousEquipValueEnd = 0;
            double currentEquipValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Количество используемого оборудования (машины и оборудования)")) {
                    previousEquipValueStart = previousYear;
                    currentEquipValueStart = currentYear;
                } else if (name.equals("Количество наличного оборудования")) {
                    previousEquipValueEnd = previousYear;
                    currentEquipValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousEquipValueEnd == 0 || currentEquipValueEnd == 0 ) {
                return "Ошибка: Количество наличного оборудования не может быть равно нулю.";
            }

            double previousResult = previousEquipValueStart / previousEquipValueEnd;
            double currentResult = currentEquipValueStart / currentEquipValueEnd;

            return String.format("Коэффициент использования парка наличного оборудования (предшествующий год) = %s%nКоэффициент использования парка наличного оборудования (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateCoefOborIsp() throws Exception {
        Path tempDbFile = extractDatabaseFile("/equipment.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных equipment.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Количество используемого оборудования (машины и оборудования)', 'Количество установленного оборудования')";
            ResultSet rs = stmt.executeQuery(query);

            double previousEquipValueStart = 0;
            double currentEquipValueStart = 0;
            double previousEquipValueEnd = 0;
            double currentEquipValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Количество используемого оборудования (машины и оборудования)")) {
                    previousEquipValueStart = previousYear;
                    currentEquipValueStart = currentYear;
                } else if (name.equals("Количество установленного оборудования")) {
                    previousEquipValueEnd = previousYear;
                    currentEquipValueEnd = currentYear;
                }
            }

            // Проверка на деление на ноль
            if (previousEquipValueEnd == 0 || currentEquipValueEnd == 0 ) {
                return "Ошибка: Количество установленного оборудования не может быть равно нулю.";
            }

            double previousResult = previousEquipValueStart / previousEquipValueEnd;
            double currentResult = currentEquipValueStart / currentEquipValueEnd;

            return String.format("Коэффициент использования парка установленного оборудования (предшествующий год) = %s%nКоэффициент использования парка установленного оборудования (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateFactTime() throws Exception {
        Path tempDbFile = extractDatabaseFile("/equipment.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных equipment.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Режимный фонд рабочего времени (ч.)', 'Сумма затрат времени на ремонт, наладку, переналадку оборудования в течение года (ч.)')";
            ResultSet rs = stmt.executeQuery(query);

            double previousEquipValueStart = 0;
            double currentEquipValueStart = 0;
            double previousEquipValueEnd = 0;
            double currentEquipValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Режимный фонд рабочего времени (ч.)")) {
                    previousEquipValueStart = previousYear;
                    currentEquipValueStart = currentYear;
                } else if (name.equals("Сумма затрат времени на ремонт, наладку, переналадку оборудования в течение года (ч.)")) {
                    previousEquipValueEnd = previousYear;
                    currentEquipValueEnd = currentYear;
                }
            }


            double previousResult = previousEquipValueStart - previousEquipValueEnd;
            double currentResult = currentEquipValueStart - currentEquipValueEnd;

            return String.format("Фактический фонд отработанного времени (предшествующий год) = %s%nФактический фонд отработанного времени (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateCoefRezh() throws Exception {
        Path tempDbFile = extractDatabaseFile("/equipment.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных equipment.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Режимный фонд рабочего времени (ч.)', 'Сумма затрат времени на ремонт, наладку, переналадку оборудования в течение года (ч.)')";
            ResultSet rs = stmt.executeQuery(query);

            double previousEquipValueStart = 0;
            double currentEquipValueStart = 0;
            double previousEquipValueEnd = 0;
            double currentEquipValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Режимный фонд рабочего времени (ч.)")) {
                    previousEquipValueStart = previousYear;
                    currentEquipValueStart = currentYear;
                } else if (name.equals("Сумма затрат времени на ремонт, наладку, переналадку оборудования в течение года (ч.)")) {
                    previousEquipValueEnd = previousYear;
                    currentEquipValueEnd = currentYear;
                }
            }


            double previousResult = (previousEquipValueStart - previousEquipValueEnd) / previousEquipValueStart;
            double currentResult = (currentEquipValueStart - currentEquipValueEnd) / currentEquipValueStart;

            return String.format("Коэффициент режимного фонда времени (предшествующий год) = %s%nКоэффициент режимного фонда времени (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateUdVes() throws Exception {
        Path tempDbFile = extractDatabaseFile("/equipment.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных equipment.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Режимный фонд рабочего времени (ч.)', 'Сумма затрат времени на ремонт, наладку, переналадку оборудования в течение года (ч.)', 'Время внеплановых простоев оборудования (ч.)')";
            ResultSet rs = stmt.executeQuery(query);

            double previousEquipValueStart = 0;
            double currentEquipValueStart = 0;
            double previousEquipValueEnd = 0;
            double currentEquipValueEnd = 0;
            double previousTime = 0;
            double currentTime = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Режимный фонд рабочего времени (ч.)")) {
                    previousEquipValueStart = previousYear;
                    currentEquipValueStart = currentYear;
                } else if (name.equals("Сумма затрат времени на ремонт, наладку, переналадку оборудования в течение года (ч.)")) {
                    previousEquipValueEnd = previousYear;
                    currentEquipValueEnd = currentYear;
                } else if (name.equals("Время внеплановых простоев оборудования (ч.)")) {
                    previousTime = previousYear;
                    currentTime = currentYear;
                }
            }


            double previousResult = previousTime / (previousEquipValueStart - previousEquipValueEnd);
            double currentResult = currentTime / (currentEquipValueStart - currentEquipValueEnd);

            return String.format("Удельный вес внеплановых простоев (предшествующий год) = %s%nУдельный вес внеплановых простоев (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
    }

    private String calculateUdZatr() throws Exception {
        Path tempDbFile = extractDatabaseFile("/equipment.db");

        if (tempDbFile == null) {
            return "Ошибка: Не удалось загрузить файл базы данных equipment.db.";
        }

        String url = "jdbc:sqlite:" + tempDbFile.toString();

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            String query = "SELECT name, previous_year, current_year FROM indicators WHERE name IN ('Режимный фонд рабочего времени (ч.)', 'Время нахождения оборудования в ремонте (ч.)')";
            ResultSet rs = stmt.executeQuery(query);

            double previousEquipValueStart = 0;
            double currentEquipValueStart = 0;
            double previousEquipValueEnd = 0;
            double currentEquipValueEnd = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double previousYear = rs.getDouble("previous_year");
                double currentYear = rs.getDouble("current_year");

                if (name.equals("Режимный фонд рабочего времени (ч.)")) {
                    previousEquipValueStart = previousYear;
                    currentEquipValueStart = currentYear;
                } else if (name.equals("Время нахождения оборудования в ремонте (ч.)")) {
                    previousEquipValueEnd = previousYear;
                    currentEquipValueEnd = currentYear;
                }
            }


            double previousResult = previousEquipValueEnd / previousEquipValueStart;
            double currentResult = currentEquipValueEnd / currentEquipValueStart;

            return String.format("Удельный вес затрат времени на ремонт (предшествующий год) = %s%nУдельный вес затрат времени на ремонт (отчетный год) = %s%n",
                    df.format(previousResult), df.format(currentResult));
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при подключении к базе данных economics.db.";
        }
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