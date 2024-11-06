package utils.simulation;

import java.util.ArrayList;
import java.util.List;

public class SimulationStatistics {
    private int[] requestCount = { 0, 0, 0 };
    private List<Double> responseTimes = new ArrayList<>();

    public static final int REQUEST_ARRIVED = 0;
    public static final int REQUEST_COMPLETED = 1;
    public static final int REQUEST_DROPPED = 2;

    public double calculateAverageResponseTime() {
        double sum = 0.0;
        for (double responseTime : responseTimes) {
            sum += responseTime;
        }
        return responseTimes.isEmpty() ? 0 : sum / responseTimes.size();
    }

    public void addResponseTime(double responseTime) {
        responseTimes.add(responseTime);
    }

    public void incrementRequestCount(int index) {
        requestCount[index]++;
    }

    public int getRequestCount(int index) {
        return requestCount[index];
    }

    public double getDropRate() {
        return (double) requestCount[REQUEST_DROPPED] / requestCount[REQUEST_ARRIVED];
    }

    public void printStatistics() {
        System.out.println("______Simulation statistics______");
        System.out.println("Request arrived: " + requestCount[REQUEST_ARRIVED]);
        System.out.println("Request completed: " + requestCount[REQUEST_COMPLETED]);
        System.out.println("Request dropped: " + requestCount[REQUEST_DROPPED]);
        System.out.println("Drop rate: " + (this.getDropRate() * 100) + "%");
        System.out.println(
                "Average response time: " + String.format("%.3f", this.calculateAverageResponseTime()) + " seconds");
    }
}
