package simulations.common;

import java.util.ArrayList;
import java.util.List;

public class SimulationStatistics {
    private int[] requestCount = { 0, 0, 0 };
    private List<Double> responseTimes = new ArrayList<>();
    private double timeUnderLoad = 0.0;
    private double totalTime = 0.0;
    private List<Double> requestsInSystem = new ArrayList<>();

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

    public double calculateAverageRequestsInSystem() {
        double sum = 0.0;
        for (double requests : requestsInSystem) {
            sum += requests;
        }
        return requestsInSystem.isEmpty() ? 0 : sum / requestsInSystem.size();
    }

    public void addRequestsInSystem(double requests) {
        requestsInSystem.add(requests);
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public void addTimeUnderLoad(double time) {
        timeUnderLoad += time;
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

    public double getUtilization() {
        return timeUnderLoad / totalTime;
    }

    public void printStatistics() {
        System.out.println("\n______Simulation statistics______");
        System.out.println("Request arrived: " + requestCount[REQUEST_ARRIVED]);
        System.out.println("Request completed: " + requestCount[REQUEST_COMPLETED]);
        System.out.println("Request dropped: " + requestCount[REQUEST_DROPPED]);
        System.out.println("Drop rate: " + String.format("%.2f", this.getDropRate() * 100) + "%");
        System.out.println("Utilization: " + String.format("%.2f", this.getUtilization() * 100) + "%");
        System.out.println(
                "Average response time: " + String.format("%.2f", this.calculateAverageResponseTime()) + " seconds");
        System.out.println(
                "Average requests in system: " + String.format("%.2f", this.calculateAverageRequestsInSystem()));
    }
}
