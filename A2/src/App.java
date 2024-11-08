import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

import simulations.Configuration;
import simulations.common.SimulationStatistics;
import utils.CSVWriter;

public class App {

    private static int INITIAL_SEED = 1000;
    private static CSVWriter csvWriter = new CSVWriter();

    public static void main(String[] args) throws Exception {
        App.runConfiguration1();
        App.runConfiguration2();
    }

    private static int calculateRepetition(int SERVERS, int BUFFER) {
        System.out.println("________Calculating required repetitions...________\n");

        int R_0 = 10;
        double precision = 0.05; // 5%
        List<Double> dropRates = new ArrayList<>();
        double Z_value = 1.96; // 95% confidence interval

        for (int i = 0; i < R_0; i++) {
            Configuration configuration = new Configuration(INITIAL_SEED + i, SERVERS, BUFFER);
            SimulationStatistics results = configuration.run();
            double dropRate = results.getDropRate();
            dropRates.add(dropRate);
        }

        double meanDropRate = dropRates.stream().mapToDouble(a -> a).average().orElse(0.0);
        double variance = dropRates.stream()
                .mapToDouble(a -> Math.pow(a - meanDropRate, 2))
                .sum() / (R_0 - 1);
        double standardDeviation = Math.sqrt(variance);

        // Calculate the required number of repetitions
        double n = Math.pow((Z_value * standardDeviation) / precision, 2);
        int requiredRepetitions = (int) Math.ceil(n);

        System.out.println("R_0: " + R_0);
        System.out.println("Mean drop rate: " + meanDropRate);
        System.out.println("Standard Deviation: " + standardDeviation);
        System.out.println("Precision e: " + precision);
        System.out.println("Required repetitions: " + requiredRepetitions);
        return requiredRepetitions;
    }

    private static void runConfiguration1() {
        System.out.println("\n________Starting configuration 1...________\n");
        int servers = 4;
        int buffer = 6;
        int repetitions = App.calculateRepetition(servers, buffer);
        List<Double> dropRates = new ArrayList<>();
        List<Double> utilizations = new ArrayList<>();
        List<Double> averageResponseTimes = new ArrayList<>();

        csvWriter.start("./A2/raw_data/configuration1.csv");
        for (int i = 0; i < repetitions; i++) {
            Configuration configuration = new Configuration(INITIAL_SEED + i, servers, buffer);
            SimulationStatistics results = configuration.run();

            double dropRate = results.getDropRate();
            double utilization = results.getUtilization();
            double avgResponseTime = results.calculateAverageResponseTime();

            dropRates.add(dropRate);
            utilizations.add(utilization);
            averageResponseTimes.add(avgResponseTime);
            csvWriter.addRow(Integer.toString(i + 1), dropRate, utilization, avgResponseTime);
        }

        App.outputAnalysis(dropRates, utilizations, averageResponseTimes);
        csvWriter.close();

        System.out.println("\n________Configuration 1 simulation completed!________\n");
    }

    private static void runConfiguration2() {
        System.out.println("\n________Starting configuration 2...________\n");

        int servers = 5;
        int buffer = 5;
        int repetitions = App.calculateRepetition(servers, buffer);
        List<Double> dropRates = new ArrayList<>();
        List<Double> utilizations = new ArrayList<>();
        List<Double> averageResponseTimes = new ArrayList<>();

        csvWriter.start("./A2/raw_data/configuration2.csv");

        for (int i = 0; i < repetitions; i++) {
            Configuration configuration = new Configuration(INITIAL_SEED + i, servers, buffer);
            SimulationStatistics results = configuration.run();

            double dropRate = results.getDropRate();
            double utilization = results.getUtilization();
            double avgResponseTime = results.calculateAverageResponseTime();

            dropRates.add(dropRate);
            utilizations.add(utilization);
            averageResponseTimes.add(avgResponseTime);
            csvWriter.addRow(Integer.toString(i + 1), dropRate, utilization, avgResponseTime);
        }

        App.outputAnalysis(dropRates, utilizations, averageResponseTimes);
        csvWriter.close();
        System.out.println("\n________Configuration 2 simulation completed!________\n");
    }

    private static void outputAnalysis(List<Double> dropRates, List<Double> utilizations,
            List<Double> averageResponseTimes) {
        // Calculate overall statistics for Drop Rate
        double meanDropRate = dropRates.stream().mapToDouble(a -> a).average().orElse(0.0);
        double varianceDropRate = dropRates.stream()
                .mapToDouble(a -> Math.pow(a - meanDropRate, 2))
                .sum() / (dropRates.size() - 1);
        double stdDevDropRate = Math.sqrt(varianceDropRate);
        double ciHalfWidthDropRate = 1.96 * stdDevDropRate / Math.sqrt(dropRates.size());

        // Calculate overall statistics for Utilization
        double meanUtilization = utilizations.stream().mapToDouble(a -> a).average().orElse(0.0);
        double varianceUtilization = utilizations.stream()
                .mapToDouble(a -> Math.pow(a - meanUtilization, 2))
                .sum() / (utilizations.size() - 1);
        double stdDevUtilization = Math.sqrt(varianceUtilization);
        double ciHalfWidthUtilization = 1.96 * stdDevUtilization / Math.sqrt(utilizations.size());

        // Calculate overall statistics for Average Response Time
        double meanResponseTime = averageResponseTimes.stream().mapToDouble(a -> a).average().orElse(0.0);
        double varianceResponseTime = averageResponseTimes.stream()
                .mapToDouble(a -> Math.pow(a - meanResponseTime, 2))
                .sum() / (averageResponseTimes.size() - 1);
        double stdDevResponseTime = Math.sqrt(varianceResponseTime);
        double ciHalfWidthResponseTime = 1.96 * stdDevResponseTime / Math.sqrt(averageResponseTimes.size());

        System.out.println("\n");
        System.out.println("Mean drop rate: " + String.format(Locale.US, "%.2f", (meanDropRate)) + "%");
        System.out.println("Variance: " + varianceDropRate);
        System.out.println("95% CI Half-Width: " + ciHalfWidthDropRate);
        System.out.println("\n");

        System.out.println("Mean utilization: " + String.format(Locale.US, "%.2f", (meanUtilization)) + "%");
        System.out.println("Utilization Variance: " + varianceUtilization);
        System.out.println("Utilization 95% CI Half-Width: " + ciHalfWidthUtilization);
        System.out.println("\n");

        System.out.println(
                "Mean average response time: " + String.format(Locale.US, "%.2f", meanResponseTime) + " s");
        System.out.println("Response Time Variance: " + varianceResponseTime);
        System.out.println("Response Time 95% CI Half-Width: " + ciHalfWidthResponseTime);
        csvWriter.addRow("Total", meanDropRate, meanUtilization, meanResponseTime);
    }
}
