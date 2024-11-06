package simulations.events;

import simulations.interfaces.EventInterface;

public class RequestCompleteEvent implements EventInterface {
    double startTime;
    double completionTime;
    int server = 0;

    public RequestCompleteEvent(double startTime, double completionTime, int server) {
        // System.out.println("RequestCompleteEvent: %s %d".formatted(completionTime,
        // server));
        this.startTime = startTime;
        this.completionTime = completionTime;
        this.server = server;
    }

    public double getResponseTime() {
        if (completionTime < startTime) {
            throw new IllegalArgumentException("Completion time is less than start time");
        }

        return completionTime - startTime;
    }

    public int getServer() {
        return server;
    }

    @Override
    public double getTime() {
        return completionTime;
    }

    public static String getEventType() {
        return "RequestCompleteEvent";
    }
}
