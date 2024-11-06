package simulations.events;

import simulations.common.RequestType;
import simulations.interfaces.EventInterface;

public class RequestArrivalEvent implements EventInterface {
    double arrivalTime;
    RequestType type;

    public RequestArrivalEvent(double arrivalTime, RequestType type) {
        // System.out.println("RequestArrivalEvent: %s %s".formatted(arrivalTime,
        // type.getName()));
        this.arrivalTime = arrivalTime;
        this.type = type;
    }

    public RequestType getType() {
        return type;
    }

    @Override
    public double getTime() {
        return arrivalTime;
    }

    public static String getEventType() {
        return "RequestArrivalEvent";
    }
}
