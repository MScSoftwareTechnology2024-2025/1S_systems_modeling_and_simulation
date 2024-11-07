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

    public double getArriveTime() {
        return startTime;
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

    @Override
    public String toString() {
        return "RequestCompleteEvent [startTime=" + startTime + ", completionTime=" + completionTime + ", server="
                + server + "]";
    }
}
