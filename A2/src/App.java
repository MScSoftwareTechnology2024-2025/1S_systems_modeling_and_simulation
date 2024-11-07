import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import simulations.Configuration;
import simulations.common.SimulationStatistics;

public class App {

    public static void main(String[] args) throws Exception {
        // Run configuration 1
        ConfigurationRunner configuration1 = new ConfigurationRunner(
            "configuration1_results.csv", // Output CSV file name
            4,                            // Number of servers
            6,                            // Buffer size
            101                           // Seed offset
        );
        configuration1.run();

        // Run configuration 2
        ConfigurationRunner configuration2 = new ConfigurationRunner(
            "configuration2_results.csv", // Output CSV file name
            5,                            // Number of servers
            5,                            // Buffer size
            201                           // Seed offset
        );
        configuration2.run();
    }
}

class ConfigurationRunner {
    private String csvFile;
    private int servers;
    private int buffer;
    private int seedOffset;

    public ConfigurationRunner(String csvFile, int servers, int buffer, int seedOffset) {
        this.csvFile = csvFile;
        this.servers = servers;
        this.buffer = buffer;
        this.seedOffset = seedOffset;
    }

    public void run() {
        int repetitions = calculateRepetition(servers, buffer);
        System.out.println("Running " + csvFile + " with repetitions: " + repetitions);

        List<Double> dropRates = new ArrayList<>();
        List<Double> utilizations = new ArrayList<>();
        List<Double> averageResponseTimes = new ArrayList<>();

        FileWriter writer = null;

        try {
            writer = new FileWriter(csvFile);
            // Write CSV header with semicolons
            writer.append("Run;DropRate;Utilization;AverageResponseTime\n");

            for (int i = 0; i < repetitions; i++) {
                Configuration configuration = new Configuration(i + seedOffset, servers, buffer);
                SimulationStatistics results = configuration.run();

                double dropRate = results.getDropRate();
                double utilization = results.getUtilization();
                double avgResponseTime = results.calculateAverageResponseTime();

                dropRates.add(dropRate);
                utilizations.add(utilization);
                averageResponseTimes.add(avgResponseTime);

                // Write data to CSV using semicolon separator
                writer.append(String.format(Locale.US, "%d;%.6f;%.6f;%.6f\n",
                    i + 1, dropRate, utilization, avgResponseTime));
            }
        // Calculate overall statistics for Drop Rate
        double meanDropRate = dropRates.stream().mapToDouble(a -> a).average().orElse(0.0);
        double varianceDropRate = dropRates.stream()
            .mapToDouble(a -> Math.pow(a - meanDropRate, 2))
            .sum() / (repetitions - 1);
        double stdDevDropRate = Math.sqrt(varianceDropRate);
        double ciHalfWidthDropRate = 1.96 * stdDevDropRate / Math.sqrt(repetitions);

        // Calculate overall statistics for Utilization
        double meanUtilization = utilizations.stream().mapToDouble(a -> a).average().orElse(0.0);
        double varianceUtilization = utilizations.stream()
            .mapToDouble(a -> Math.pow(a - meanUtilization, 2))
            .sum() / (repetitions - 1);
        double stdDevUtilization = Math.sqrt(varianceUtilization);
        double ciHalfWidthUtilization = 1.96 * stdDevUtilization / Math.sqrt(repetitions);

        // Calculate overall statistics for Average Response Time
        double meanResponseTime = averageResponseTimes.stream().mapToDouble(a -> a).average().orElse(0.0);
        double varianceResponseTime = averageResponseTimes.stream()
            .mapToDouble(a -> Math.pow(a - meanResponseTime, 2))
            .sum() / (repetitions - 1);
        double stdDevResponseTime = Math.sqrt(varianceResponseTime);
        double ciHalfWidthResponseTime = 1.96 * stdDevResponseTime / Math.sqrt(repetitions);

        // Output statistics
        System.out.println("Mean drop rate: " + meanDropRate);
        System.out.println("Variance: " + varianceDropRate);
        System.out.println("95% CI Half-Width: " + ciHalfWidthDropRate);

        // Output additional statistics
        System.out.println("Mean utilization: " + meanUtilization);
        System.out.println("Utilization Variance: " + varianceUtilization);
        System.out.println("Utilization 95% CI Half-Width: " + ciHalfWidthUtilization);

        System.out.println("Mean average response time: " + meanResponseTime);
        System.out.println("Response Time Variance: " + varianceResponseTime);
        System.out.println("Response Time 95% CI Half-Width: " + ciHalfWidthResponseTime);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int calculateRepetition(int SERVERS, int BUFFER) {
        int R_0 = 20;
        double precision = 0.01;
        List<Double> dropRates = new ArrayList<>();
        double Z_value = 1.96;

        for (int i = 0; i < R_0; i++) {
            Configuration configuration = new Configuration(seedOffset + i, SERVERS, BUFFER);
            SimulationStatistics results = configuration.run();
            double dropRate = results.getDropRate();
            dropRates.add(dropRate);
        }

        double meanDropRate = dropRates.stream().mapToDouble(a -> a).average().orElse(0.0);
        double variance = dropRates.stream()
            .mapToDouble(a -> Math.pow(a - meanDropRate, 2))
            .sum() / (R_0 - 1);
        double standardDeviation = Math.sqrt(variance);
        double E = precision * meanDropRate;


        int repetitions = (int) Math.ceil(Math.pow((Z_value * standardDeviation) / E, 2));

        // Ensure repetitions are at least R_0
        repetitions = Math.max(repetitions, R_0);

        return repetitions;
    }
}
