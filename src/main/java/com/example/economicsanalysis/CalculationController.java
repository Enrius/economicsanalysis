package com.example.economicsanalysis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class CalculationController {
    @FXML
    private TextArea resultTextArea;
    @FXML
    private ComboBox<String> indicatorComboBox;

    private final DecimalFormat df = new DecimalFormat("#.###");
    private final Map<String, String> indicatorFormulas = new LinkedHashMap<>();
    private ObservableList<Indicator> indicators;

    @FXML
    private void initialize() {
        // Initialize indicator formulas map
        initializeIndicatorFormulas();

        // Set items for the indicator choice box
        indicatorComboBox.setItems(FXCollections.observableArrayList(indicatorFormulas.keySet()));
    }

    public void setIndicators(ObservableList<Indicator> indicators) {
        this.indicators = indicators;
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

        switch (indicator) {
            case "Средний вес поезда брутто":
                result.append(calculateAverageTrainWeight());
                break;
            case "Среднесуточная производительность локомотивов":
                result.append(calculateDailyLocomotivePerformance());
                break;
            case "Среднесуточная отдача локомотива":
                result.append(calculateDailyLocomotiveReturn());
                break;
            case "Коэффициент соотношения темпов роста доходов от перевозок и величины амортизационных отчислений по локомотивам":
                result.append(calculateGrowthRateRatio());
                break;
            case "Коэффициент соотношения темпов роста доходов от перевозок и величины затрат по содержанию и ремонту локомотивов":
                result.append(calculateZatrRateRatio());
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

        resultTextArea.setText(result.toString());
    }

    private String calculateAverageTrainWeight() {
        double previousTonKm = getIndicatorValue("Тонно-километры (млн)", true);
        double currentTonKm = getIndicatorValue("Тонно-километры (млн)", false);
        double previousRun = getIndicatorValue("Пробег локомотивов в голове поездов (лок.-км)", true);
        double currentRun = getIndicatorValue("Пробег локомотивов в голове поездов (лок.-км)", false);

        if (previousRun == 0 || currentRun == 0) {
            return "Ошибка: Пробег локомотивов в голове поездов не может быть равен нулю.";
        }

        double previousResult = previousTonKm * 1000000 / previousRun;
        double currentResult = currentTonKm * 1000000 / currentRun;

        return String.format("Средний вес поезда брутто (предшествующий год) = %s%nСредний вес поезда брутто (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateDailyLocomotivePerformance() {
        double previousTonKm = getIndicatorValue("Тонно-километры (млн)", true);
        double currentTonKm = getIndicatorValue("Тонно-километры (млн)", false);
        double previousPark = getIndicatorValue("Среднесуточная величина эксплуатируемого парка локомотивов (лок. в среднем за сутки)", true);
        double currentPark = getIndicatorValue("Среднесуточная величина эксплуатируемого парка локомотивов (лок. в среднем за сутки)", false);

        if (previousPark == 0 || currentPark == 0) {
            return "Ошибка: Среднесуточная величина эксплуатируемого парка не может быть равна нулю.";
        }

        double previousResult = previousTonKm / previousPark;
        double currentResult = currentTonKm / currentPark;

        return String.format("Среднесуточная производительность локомотивов (предшествующий год) = %s%nСреднесуточная производительность локомотивов (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateDailyLocomotiveReturn() {
        double previousIncome = getIndicatorValue("Доходы от перевозок (тыс. руб)", true);
        double currentIncome = getIndicatorValue("Доходы от перевозок (тыс. руб)", false);
        double previousPark = getIndicatorValue("Среднесуточная величина эксплуатируемого парка локомотивов (лок. в среднем за сутки)", true);
        double currentPark = getIndicatorValue("Среднесуточная величина эксплуатируемого парка локомотивов (лок. в среднем за сутки)", false);

        if (previousPark == 0 || currentPark == 0) {
            return "Ошибка: Среднесуточная величина эксплуатируемого парка не может быть равна нулю.";
        }

        double previousResult = previousIncome / previousPark;
        double currentResult = currentIncome / currentPark;

        return String.format("Среднесуточная отдача локомотива (предшествующий год) = %s%nСреднесуточная отдача локомотива (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateGrowthRateRatio() {
        double previousIncome = getIndicatorValue("Доходы от перевозок (тыс. руб)", true);
        double currentIncome = getIndicatorValue("Доходы от перевозок (тыс. руб)", false);
        double previousAmortization = getIndicatorValue("Амортизационные отчисления по локомотивам (тыс. руб за год)", true);
        double currentAmortization = getIndicatorValue("Амортизационные отчисления по локомотивам (тыс. руб за год)", false);

        if (previousIncome == 0 || previousAmortization == 0) {
            return "Ошибка: Доходы от перевозок или амортизационные отчисления за предшествующий год не могут быть равны нулю.";
        }

        double incomeGrowthRate = currentIncome / previousIncome;
        double amortizationGrowthRate = currentAmortization / previousAmortization;
        double result = incomeGrowthRate / amortizationGrowthRate;

        return String.format("Коэффициент соотношения темпов роста доходов от перевозок и величины амортизационных отчислений по локомотивам = %s%n",
                df.format(result));
    }

    private String calculateZatrRateRatio() {
        double previousIncome = getIndicatorValue("Доходы от перевозок (тыс. руб)", true);
        double currentIncome = getIndicatorValue("Доходы от перевозок (тыс. руб)", false);
        double previousZatr = getIndicatorValue("Затраты по содержанию и ремонту локомотивов (тыс. руб)", true);
        double currentZatr = getIndicatorValue("Затраты по содержанию и ремонту локомотивов (тыс. руб)", false);

        if (previousIncome == 0 || previousZatr == 0) {
            return "Ошибка: Доходы от перевозок или затраты по содержанию и ремонту локомотивов за предшествующий год не могут быть равны нулю.";
        }

        double incomeGrowthRate = currentIncome / previousIncome;
        double zatrGrowthRate = currentZatr / previousZatr;
        double result = incomeGrowthRate / zatrGrowthRate;

        return String.format("Коэффициент соотношения темпов роста доходов от перевозок и величины затрат по содержанию и ремонту локомотивов = %s%n",
                df.format(result));
    }

    // Пример метода для получения значения показателя из переданных данных
    private double getIndicatorValue(String name, boolean previousYear) {
        for (Indicator indicator : indicators) {
            if (indicator.getName().equals(name)) {
                return previousYear ? indicator.getPreviousYear() : indicator.getCurrentYear();
            }
        }
        return 0;
    }

    // Пример метода для расчета "Фондоотдача ОС в натуральном выражении"
    private String calculateFondootdacha() {
        double previousWorkVolume = getIndicatorValue("Объем работы в натуральном выражении (млн т/км брутто)", true);
        double currentWorkVolume = getIndicatorValue("Объем работы в натуральном выражении (млн т/км брутто)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость основных средств не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousWorkVolume * 1000000 / previousAverageAssetValue;
        double currentResult = currentWorkVolume * 1000000 / currentAverageAssetValue;

        return String.format("Фондоотдача ОС в натуральном выражении (предшествующий год) = %s%nФондоотдача ОС в натуральном выражении (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateFondootdachaNat() {
        double previousWorkVolume = getIndicatorValue("Выручка от реализации продукции (тыс руб.)", true);
        double currentWorkVolume = getIndicatorValue("Выручка от реализации продукции (тыс руб.)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость основных средств не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousWorkVolume / previousAverageAssetValue;
        double currentResult = currentWorkVolume / currentAverageAssetValue;

        return String.format("Фондоотдача ОС в стоимостном выражении (предшествующий год) = %s%nФондоотдача ОС в стоимостном выражении (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateFondootdachaAct() {
        double previousWorkVolume = getIndicatorValue("Объем работы в натуральном выражении (млн т/км брутто)", true);
        double currentWorkVolume = getIndicatorValue("Объем работы в натуральном выражении (млн т/км брутто)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость активной части основных средств не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousWorkVolume * 1000000 / previousAverageAssetValue;
        double currentResult = currentWorkVolume * 1000000 / currentAverageAssetValue;

        return String.format("Фондоотдача активной части ОС в натуральном выражении (предшествующий год) = %s%nФондоотдача активной части ОС в натуральном выражении (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateFondootdachaActSt() {
        double previousWorkVolume = getIndicatorValue("Выручка от реализации продукции (тыс руб.)", true);
        double currentWorkVolume = getIndicatorValue("Выручка от реализации продукции (тыс руб.)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость активной части основных средств не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousWorkVolume / previousAverageAssetValue;
        double currentResult = currentWorkVolume / currentAverageAssetValue;

        return String.format("Фондоотдача активной части ОС в стоимостном выражении (предшествующий год) = %s%nФондоотдача активной части ОС в стоимостном выражении (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    // Аналогично для других методов расчета:
    private String calculateFondootdachaTran() {
        double previousWorkVolume = getIndicatorValue("Объем работы в натуральном выражении (млн т/км брутто)", true);
        double currentWorkVolume = getIndicatorValue("Объем работы в натуральном выражении (млн т/км брутто)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость транспортных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость транспортных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость транспортных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость транспортных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость транспортной части основных средств не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousWorkVolume * 1000000 / previousAverageAssetValue;
        double currentResult = currentWorkVolume * 1000000 / currentAverageAssetValue;

        return String.format("Фондоотдача транспортной части ОС в натуральном выражении (предшествующий год) = %s%nФондоотдача транспортной части ОС в натуральном выражении (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateFondootdachaTranSt() {
        double previousWorkVolume = getIndicatorValue("Выручка от реализации продукции (тыс руб.)", true);
        double currentWorkVolume = getIndicatorValue("Выручка от реализации продукции (тыс руб.)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость транспортных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость транспортных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость транспортных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость транспортных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость транспортной части основных средств не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousWorkVolume / previousAverageAssetValue;
        double currentResult = currentWorkVolume / currentAverageAssetValue;

        return String.format("Фондоотдача транспортной части ОС в стоимостном выражении (предшествующий год) = %s%nФондоотдача транспортной части ОС в стоимостном выражении (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateFondootdachaObor() {
        double previousWorkVolume = getIndicatorValue("Объем работы в натуральном выражении (млн т/км брутто)", true);
        double currentWorkVolume = getIndicatorValue("Объем работы в натуральном выражении (млн т/км брутто)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость машин и оборудования (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость машин и оборудования (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость машин и оборудования (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость машин и оборудования (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость машин и оборудования не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousWorkVolume * 1000000 / previousAverageAssetValue;
        double currentResult = currentWorkVolume * 1000000 / currentAverageAssetValue;

        return String.format("Фондоотдача оборудования в натуральном выражении (предшествующий год) = %s%nФондоотдача оборудования в натуральном выражении (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateFondootdachaOborSt() {
        double previousWorkVolume = getIndicatorValue("Выручка от реализации продукции (тыс руб.)", true);
        double currentWorkVolume = getIndicatorValue("Выручка от реализации продукции (тыс руб.)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость машин и оборудования (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость машин и оборудования (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость машин и оборудования (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость машин и оборудования (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость машин и оборудования не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousWorkVolume / previousAverageAssetValue;
        double currentResult = currentWorkVolume / currentAverageAssetValue;

        return String.format("Фондоотдача оборудования в стоимостном выражении (предшествующий год) = %s%nФондоотдача оборудования в стоимостном выражении (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateFondoemkost() {
        double previousRevenue = getIndicatorValue("Выручка от реализации продукции (тыс руб.)", true);
        double currentRevenue = getIndicatorValue("Выручка от реализации продукции (тыс руб.)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость основных средств не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousAverageAssetValue / previousRevenue;
        double currentResult = currentAverageAssetValue / currentRevenue;

        return String.format("Фондоемкость (предшествующий год) = %s%nФондоемкость (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateFondorent() {
        double previousProfit = getIndicatorValue("Прибыль (убыток) до налогообложения (тыс руб.)", true);
        double currentProfit = getIndicatorValue("Прибыль (убыток) до налогообложения (тыс руб.)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость основных средств не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousProfit / previousAverageAssetValue * 100;
        double currentResult = currentProfit / currentAverageAssetValue * 100;

        return String.format("Фондорентабельность (предшествующий год) = %s%nФондорентабельность (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateFondorentAct() {
        double previousProfit = getIndicatorValue("Прибыль (убыток) до налогообложения (тыс руб.)", true);
        double currentProfit = getIndicatorValue("Прибыль (убыток) до налогообложения (тыс руб.)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость активной части основных средств не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousProfit / previousAverageAssetValue * 100;
        double currentResult = currentProfit / currentAverageAssetValue * 100;

        return String.format("Фондорентабельность активной части ОС (предшествующий год) = %s%nФондорентабельность активной части ОС (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateAmort() {
        double previousAmortization = getIndicatorValue("Общая сумма амортизации включаемая в себестоимость (тыс руб.)", true);
        double currentAmortization = getIndicatorValue("Общая сумма амортизации включаемая в себестоимость (тыс руб.)", false);
        double previousRevenue = getIndicatorValue("Выручка от реализации продукции (тыс руб.)", true);
        double currentRevenue = getIndicatorValue("Выручка от реализации продукции (тыс руб.)", false);

        if (previousRevenue == 0 || currentRevenue == 0) {
            return "Ошибка: Выручка от реализации продукции не может быть равна нулю.";
        }

        double previousResult = previousAmortization / previousRevenue;
        double currentResult = currentAmortization / currentRevenue;

        return String.format("Амортизациоемкость (предшествующий год) = %s%nАмортизациоемкость (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateOtnosEconomy() {
        double previousWorkVolume = getIndicatorValue("Объем работы в натуральном выражении (млн т/км брутто)", true);
        double currentWorkVolume = getIndicatorValue("Объем работы в натуральном выражении (млн т/км брутто)", false);
        double previousAssetValueStart = getIndicatorValue("Первоначальная стоимость основных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Первоначальная стоимость основных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Первоначальная стоимость основных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Первоначальная стоимость основных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Первоначальная стоимость ОС не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double currentResult = (currentAverageAssetValue - previousAverageAssetValue) * (currentWorkVolume / previousWorkVolume);

        return String.format("Относительная экономия ОС = %s%n",
                df.format(currentResult));
    }

    private String calculateOtnosEconomyAct() {
        double previousWorkVolume = getIndicatorValue("Объем работы в натуральном выражении (млн т/км брутто)", true);
        double currentWorkVolume = getIndicatorValue("Объем работы в натуральном выражении (млн т/км брутто)", false);
        double previousAssetValueStart = getIndicatorValue("Первоначальная стоимость активной части основных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Первоначальная стоимость активной части основных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Первоначальная стоимость активной части основных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Первоначальная стоимость активной части основных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Первоначальная стоимость активной части ОС не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double currentResult = (currentAverageAssetValue - previousAverageAssetValue) * (currentWorkVolume / previousWorkVolume);

        return String.format("Относительная экономия активной части ОС = %s%n",
                df.format(currentResult));
    }

    private String calculateFondovor() {
        double previousEmployeeCount = getIndicatorValue("Среднесписочная численность работников всего (чел.)", true);
        double currentEmployeeCount = getIndicatorValue("Среднесписочная численность работников всего (чел.)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость основных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость основных средств не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousAverageAssetValue / previousEmployeeCount;
        double currentResult = currentAverageAssetValue / currentEmployeeCount;

        return String.format("Фондовооруженность (предшествующий год) = %s%nФондовооруженность (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateFondovorAct() {
        double previousEmployeeCount = getIndicatorValue("Среднесписочная численность работников всего (чел.)", true);
        double currentEmployeeCount = getIndicatorValue("Среднесписочная численность работников всего (чел.)", false);
        double previousAssetValueStart = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на начало года", true);
        double currentAssetValueStart = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на начало года", false);
        double previousAssetValueEnd = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на конец года", true);
        double currentAssetValueEnd = getIndicatorValue("Остаточная стоимость активной части основных средств (тыс руб.) на конец года", false);

        if (previousAssetValueStart == 0 || currentAssetValueStart == 0 || previousAssetValueEnd == 0 || currentAssetValueEnd == 0) {
            return "Ошибка: Остаточная стоимость активной части основных средств не может быть равна нулю.";
        }

        double previousAverageAssetValue = (previousAssetValueStart + previousAssetValueEnd) / 2;
        double currentAverageAssetValue = (currentAssetValueStart + currentAssetValueEnd) / 2;

        double previousResult = previousAverageAssetValue / previousEmployeeCount;
        double currentResult = currentAverageAssetValue / currentEmployeeCount;

        return String.format("Фондовооруженность активной части ОС (предшествующий год) = %s%nФондовооруженность активной части ОС (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateCoefObor() {
        double previousUsedEquip = getIndicatorValue("Количество используемого оборудования (машины и оборудования)", true);
        double currentUsedEquip = getIndicatorValue("Количество используемого оборудования (машины и оборудования)", false);
        double previousTotalEquip = getIndicatorValue("Количество наличного оборудования", true);
        double currentTotalEquip = getIndicatorValue("Количество наличного оборудования", false);

        if (previousTotalEquip == 0 || currentTotalEquip == 0) {
            return "Ошибка: Количество наличного оборудования не может быть равно нулю.";
        }

        double previousResult = previousUsedEquip / previousTotalEquip;
        double currentResult = currentUsedEquip / currentTotalEquip;

        return String.format("Коэффициент использования парка наличного оборудования (предшествующий год) = %s%nКоэффициент использования парка наличного оборудования (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateCoefOborIsp() {
        double previousUsedEquip = getIndicatorValue("Количество используемого оборудования (машины и оборудования)", true);
        double currentUsedEquip = getIndicatorValue("Количество используемого оборудования (машины и оборудования)", false);
        double previousInstalledEquip = getIndicatorValue("Количество установленного оборудования", true);
        double currentInstalledEquip = getIndicatorValue("Количество установленного оборудования", false);

        if (previousInstalledEquip == 0 || currentInstalledEquip == 0) {
            return "Ошибка: Количество установленного оборудования не может быть равно нулю.";
        }

        double previousResult = previousUsedEquip / previousInstalledEquip;
        double currentResult = currentUsedEquip / currentInstalledEquip;

        return String.format("Коэффициент использования парка установленного оборудования (предшествующий год) = %s%nКоэффициент использования парка установленного оборудования (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateFactTime() {
        double previousRegFundTime = getIndicatorValue("Режимный фонд рабочего времени (ч.)", true);
        double currentRegFundTime = getIndicatorValue("Режимный фонд рабочего времени (ч.)", false);
        double previousRepairTime = getIndicatorValue("Сумма затрат времени на ремонт, наладку, переналадку оборудования в течение года (ч.)", true);
        double currentRepairTime = getIndicatorValue("Сумма затрат времени на ремонт, наладку, переналадку оборудования в течение года (ч.)", false);

        if (previousRepairTime == 0 || currentRepairTime == 0) {
            return "Ошибка: Фактическое время не может быть равно нулю.";
        }

        double previousResult = previousRegFundTime - previousRepairTime;
        double currentResult = currentRegFundTime - currentRepairTime;

        return String.format("Фактический фонд отработанного времени (предшествующий год) = %s%nФактический фонд отработанного времени (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateCoefRezh() {
        double previousRegFundTime = getIndicatorValue("Режимный фонд рабочего времени (ч.)", true);
        double currentRegFundTime = getIndicatorValue("Режимный фонд рабочего времени (ч.)", false);
        double previousRepairTime = getIndicatorValue("Сумма затрат времени на ремонт, наладку, переналадку оборудования в течение года (ч.)", true);
        double currentRepairTime = getIndicatorValue("Сумма затрат времени на ремонт, наладку, переналадку оборудования в течение года (ч.)", false);

        if (previousRepairTime == 0 || currentRepairTime == 0) {
            return "Ошибка: Сумма затрат времени не может быть равна нулю.";
        }

        double previousResult = (previousRegFundTime - previousRepairTime) / previousRegFundTime;
        double currentResult = (currentRegFundTime - currentRepairTime) / currentRegFundTime;

        return String.format("Коэффициент режимного фонда времени (предшествующий год) = %s%nКоэффициент режимного фонда времени (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateUdVes() {
        double previousRegFundTime = getIndicatorValue("Режимный фонд рабочего времени (ч.)", true);
        double currentRegFundTime = getIndicatorValue("Режимный фонд рабочего времени (ч.)", false);
        double previousRepairTime = getIndicatorValue("Сумма затрат времени на ремонт, наладку, переналадку оборудования в течение года (ч.)", true);
        double currentRepairTime = getIndicatorValue("Сумма затрат времени на ремонт, наладку, переналадку оборудования в течение года (ч.)", false);
        double previousDowntime = getIndicatorValue("Время внеплановых простоев оборудования (ч.)", true);
        double currentDowntime = getIndicatorValue("Время внеплановых простоев оборудования (ч.)", false);

        if (previousRepairTime == 0 || currentRepairTime == 0) {
            return "Ошибка: Сумма затрат времени не может быть равна нулю.";
        }

        double previousResult = previousDowntime / (previousRegFundTime - previousRepairTime);
        double currentResult = currentDowntime / (currentRegFundTime - currentRepairTime);

        return String.format("Удельный вес внеплановых простоев (предшествующий год) = %s%nУдельный вес внеплановых простоев (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }

    private String calculateUdZatr() {
        double previousRegFundTime = getIndicatorValue("Режимный фонд рабочего времени (ч.)", true);
        double currentRegFundTime = getIndicatorValue("Режимный фонд рабочего времени (ч.)", false);
        double previousRepairTime = getIndicatorValue("Время нахождения оборудования в ремонте (ч.)", true);
        double currentRepairTime = getIndicatorValue("Время нахождения оборудования в ремонте (ч.)", false);

        if (previousRepairTime == 0 || currentRepairTime == 0) {
            return "Ошибка: Время нахождения в ремонте не может быть равно нулю.";
        }

        double previousResult = previousRepairTime / previousRegFundTime;
        double currentResult = currentRepairTime / currentRegFundTime;

        return String.format("Удельный вес затрат времени на ремонт (предшествующий год) = %s%nУдельный вес затрат времени на ремонт (отчетный год) = %s%n",
                df.format(previousResult), df.format(currentResult));
    }
}