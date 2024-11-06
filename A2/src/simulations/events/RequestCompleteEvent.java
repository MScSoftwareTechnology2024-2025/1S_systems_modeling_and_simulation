package simulations.events;

import utils.simulation.EventInterface;

public class RequestCompleteEvent implements EventInterface {
    double completionTime;
    int server = 0;

    public RequestCompleteEvent(double completionTime, int server) {
        // System.out.println("RequestCompleteEvent: %s %d".formatted(completionTime,
        // server));
        this.completionTime = completionTime;
        this.server = server;
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
