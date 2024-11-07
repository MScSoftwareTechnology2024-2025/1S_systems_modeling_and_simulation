package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class CSVWriter {
    private final String HEADER = "Run;DropRate;Utilization;AverageResponseTime\n";
    FileWriter writer;

    public void start(String csvFile) {
        try {
            // if file exists remove it first
            writer = new FileWriter(csvFile, false);
            writer.append(HEADER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRow(int run, double dropRate, double utilization, double averageResponseTime) {
        try {
            String row = String.format(Locale.US, "%d;%.2f;%.2f;%.2fs", run, dropRate, utilization,
                    averageResponseTime);
            writer.append(row + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
