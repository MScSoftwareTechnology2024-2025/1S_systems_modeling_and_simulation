package simulations;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import simulations.events.RequestArrivalEvent;
import simulations.events.RequestCompleteEvent;
import utils.random.RandomWeightedChoice;
import utils.random.variate.ExponentialDistribution;
import utils.simulation.EventInterface;
import utils.simulation.RequestBuffer;
import utils.simulation.RequestType;
import utils.simulation.SimulationInterface;
import utils.simulation.SystemState;

public class Configuration1 implements SimulationInterface {
    private final int DAY = 86400;
    private int[] requestCount = { 0, 0, 0 };
    /**
     * * Configuration 1: 4 servers, buffer 6
     */
    private final int C = 4; // number of servers
    private final int BUFFER = 6; // buffer size
    private final RequestBuffer requestBuffer = new RequestBuffer(BUFFER);
    private double lambda; // interarrival rate
    private SystemState systemState;
    private final List<RequestType> requestType = new ArrayList<RequestType>();
    private final PriorityQueue<EventInterface> eventList = new PriorityQueue<EventInterface>((a, b) -> {
        return Double.compare(a.getTime(), b.getTime());
    });

    ExponentialDistribution expDist;

    public Configuration1(int SEED) {
        requestType.add(new RequestType("Flight", 3, 0.2));
        requestType.add(new RequestType("Flight&Hotel", 7, 0.7));
        requestType.add(new RequestType("Flight&Hotel&Car", 12, 0.1));
        expDist = new ExponentialDistribution(SEED);
        this.lambda = this.calcLambdaFromMinutesToSeconds(34);
    }

    private double calcLambdaFromMinutesToSeconds(double interarrivalTime) {
        return interarrivalTime / 60.0;
    }

    @Override
    public void run() {
        // * initialize the simulation
        this.initialize();

        // * run the simulation until the clock reaches the end of the day
        while (systemState.getClock() < DAY) {
            EventInterface nextEvent = this.getNextEvent();
            this.processEvent(nextEvent);
        }

        System.out.println("Simulation ended");
        System.out.println("Request arrived: " + requestCount[0]);
        System.out.println("Request completed: " + requestCount[1]);
        System.out.println("Request dropped: " + requestCount[2]);
        System.out.println("Drop rate: " + (double) requestCount[2] / requestCount[0]);
        // * return statistics of the simulation
    }

    @Override
    public void initialize() {
        systemState = new SystemState(C);
    }

    @Override
    public void processEvent(EventInterface event) {
        if (event instanceof RequestArrivalEvent) {
            handleRequestArrivalEvent((RequestArrivalEvent) event);
            return;
        }

        if (event instanceof RequestCompleteEvent) {
            handleRequestCompleteEvent((RequestCompleteEvent) event);
            return;
        }

        // ! unknown event exit with an error
        System.exit(1);
    }

    private EventInterface getNextEvent() {
        // if empty, only arrival event is possible
        if (eventList.isEmpty() || this.isTimeForNextArrival()) {
            scheduleNextArrivalEvent();
        }

        // take the next event from the event list and proceed the clock to its time
        EventInterface nextEvent = eventList.poll();
        systemState.proceedClock(nextEvent.getTime());
        return nextEvent;
    }

    private void handleRequestArrivalEvent(RequestArrivalEvent event) {
        int server = systemState.getFreeServer();
        boolean serversBusy = server == -1;

        // drop request if queue/buffer is full
        if (serversBusy && requestBuffer.isFull()) {
            System.out.println("Request dropped");
            requestCount[2]++;
            return;
        }

        // add request to buffer if servers are busy and buffer is not full
        if (serversBusy && !requestBuffer.isFull()) {
            System.out.println("Request added to buffer");
            requestBuffer.addRequest(event);
            return;
        }

        requestCount[0]++;

        System.out.println("Request handled by server: " + server);
        // assign server to the request
        systemState.assignServer(server);
        // schedule complete request event for this request
        scheduleNextDepartureEvent(event, server);
    }

    private void handleRequestCompleteEvent(RequestCompleteEvent event) {
        requestCount[1]++;

        // free server that was processing the event
        systemState.freeServer(event.getServer());

        // update statistics for the simulation
        double requestResponseTime = systemState.getClock() - event.getTime();

        // if there are requests in the buffer, assign a server to the request
        if (!requestBuffer.isEmpty()) {
            // pops the request from the buffer
            RequestArrivalEvent arrivingEvent = requestBuffer.removeRequest();
            handleRequestArrivalEvent(arrivingEvent);
        }
    }

    private void scheduleNextArrivalEvent() {
        double meanInterarrival = expDist.random(1.0 / lambda);
        double nextArrivalTime = systemState.getClock() + meanInterarrival;
        RequestType arrivingRequestType = RandomWeightedChoice.chooseRequestType(requestType);
        systemState.setNextArrivalTime(nextArrivalTime);
        eventList.add(new RequestArrivalEvent(nextArrivalTime, arrivingRequestType));
    }

    private void scheduleNextDepartureEvent(RequestArrivalEvent arrivingEvent, int server) {
        double serviceTime = arrivingEvent.getType().getServiceTime();
        double meanServiceTime = expDist.random(serviceTime);
        double departureTime = systemState.getClock() + meanServiceTime;
        eventList.add(new RequestCompleteEvent(departureTime, server));
    }

    private boolean isTimeForNextArrival() {
        return systemState.getClock() >= systemState.getNextArrivalTime();
    }
}
