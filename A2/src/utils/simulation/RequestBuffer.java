package utils.simulation;

import simulations.events.RequestArrivalEvent;
import java.util.LinkedList;
import java.util.Queue;

public class RequestBuffer {
    private Queue<RequestArrivalEvent> buffer;
    private int maxSize;

    public RequestBuffer(int maxSize) {
        this.maxSize = maxSize;
        this.buffer = new LinkedList<>();
    }

    public boolean isFull() {
        return buffer.size() == maxSize;
    }

    public void addRequest(RequestArrivalEvent request) {
        if (!isFull()) {
            buffer.add(request);
        }
    }

    public RequestArrivalEvent removeRequest() {
        if (!buffer.isEmpty()) {
            RequestArrivalEvent event = buffer.poll();
            return event;
        }

        return null;
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    public double getNumberOfRequestsInBuffer() {
        return buffer.size();
    }
}
