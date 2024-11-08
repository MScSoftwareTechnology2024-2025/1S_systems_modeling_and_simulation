package simulations;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import simulations.common.RequestBuffer;
import simulations.common.RequestType;
import simulations.common.SimulationStatistics;
import simulations.common.SystemState;
import simulations.events.RequestArrivalEvent;
import simulations.events.RequestCompleteEvent;
import simulations.interfaces.EventInterface;
import simulations.interfaces.SimulationInterface;
import utils.random.RandomWeightedChoice;
import utils.random.variate.ExponentialDistribution;

public class Configuration implements SimulationInterface {

    private final int REQUEST_PER_MINUTE = 34;
    private final int DAY = 86400; // 24 hours in seconds
    private final RequestBuffer requestBuffer;
    private double lambda; // interarrival rate
    private SystemState systemState;
    private SimulationStatistics statistics = new SimulationStatistics();
    private final List<RequestType> requestType = new ArrayList<RequestType>();
    private final PriorityQueue<EventInterface> eventList = new PriorityQueue<EventInterface>((a, b) -> {
        return Double.compare(a.getTime(), b.getTime());
    });

    ExponentialDistribution expDist;
    RandomWeightedChoice randomWeightedChoice;

    public Configuration(int SEED, int SERVERS, int BUFFER) {
        requestType.add(new RequestType("Flight", 3, 0.2));
        requestType.add(new RequestType("Flight&Hotel", 7, 0.7));
        requestType.add(new RequestType("Flight&Hotel&Car", 12, 0.1));
        expDist = new ExponentialDistribution(SEED);
        randomWeightedChoice = new RandomWeightedChoice(SEED);
        systemState = new SystemState(SERVERS);
        requestBuffer = new RequestBuffer(BUFFER);
        this.lambda = this.calcLambdaFromMinutesToSeconds(REQUEST_PER_MINUTE);
    }

    private double calcLambdaFromMinutesToSeconds(double interarrivalTime) {
        double SECONDS_IN_MINUTE = 60.0;
        return interarrivalTime / SECONDS_IN_MINUTE;
    }

    @Override
    public void initialize() {
        systemState.initialize();
    }

    @Override
    public SimulationStatistics run() {
        // * initialize the simulation
        this.initialize();

        // * run the simulation until the clock reaches the end of the day
        while (systemState.getClock() < DAY) {
            this.updateStatistics();
            EventInterface nextEvent = this.getNextEvent(); // timing method
            this.processEvent(nextEvent);
        }

        // * set the total time of the simulation to statistics
        statistics.setTotalTime(systemState.getClock());

        // * return statistics of the simulation
        return statistics;
    }

    private EventInterface getNextEvent() {
        if (this.isTimeForNextArrival()) {
            scheduleArrivalEvent();
        }

        // || if the clock is past the next arrival time, schedule an arrival event
        if (eventList.isEmpty()) {
            System.out.println("Event list is empty at " + systemState.getClock());
            System.exit(1);
        }

        // take the next event from the event list and proceed the clock to its time
        EventInterface nextEvent = eventList.poll();
        systemState.setLastEventTime();
        systemState.proceedClock(nextEvent.getTime());
        return nextEvent;
    }

    private boolean isTimeForNextArrival() {
        return systemState.getClock() >= systemState.getNextArrivalTime();
    }

    @Override
    public void processEvent(EventInterface event) {
        if (event instanceof RequestArrivalEvent) {
            handleRequestArrivalEvent((RequestArrivalEvent) event);
        }

        if (event instanceof RequestCompleteEvent) {
            handleRequestCompleteEvent((RequestCompleteEvent) event);
        }
    }

    private void handleRequestArrivalEvent(RequestArrivalEvent event) {
        int server = systemState.getFreeServer();
        boolean serversBusy = server == -1;

        // drop request if queue/buffer is full
        if (serversBusy && requestBuffer.isFull()) {
            statistics.incrementRequestCount(SimulationStatistics.REQUEST_DROPPED);
            return;
        }

        // add request to buffer if servers are busy and buffer is not full
        if (serversBusy && !requestBuffer.isFull()) {
            requestBuffer.addRequest(event);
            return;
        }

        statistics.incrementRequestCount(SimulationStatistics.REQUEST_ARRIVED);
        // assign server to the request
        systemState.assignServer(server);

        // schedule complete request event for this request
        scheduleCompleteEvent(event, server);
    }

    private void handleRequestCompleteEvent(RequestCompleteEvent event) {
        statistics.incrementRequestCount(SimulationStatistics.REQUEST_COMPLETED);

        // free server that was processing the event
        systemState.freeServer(event.getServer());

        double responseTime = systemState.getClock() - event.getArriveTime();
        statistics.addResponseTime(responseTime);

        // pops the buffered request from the buffer to process it
        if (!requestBuffer.isEmpty()) {
            RequestArrivalEvent arrivingEvent = requestBuffer.removeRequest();
            handleRequestArrivalEvent(arrivingEvent);
        }
    }

    private void scheduleArrivalEvent() {
        double meanInterarrival = expDist.random(1.0 / lambda);
        double nextArrivalTime = systemState.getClock() + meanInterarrival;
        RequestType arrivingRequestType = this.randomWeightedChoice.chooseRequestType(requestType);
        systemState.setNextArrivalTime(nextArrivalTime);
        eventList.add(new RequestArrivalEvent(nextArrivalTime, arrivingRequestType));
    }

    private void scheduleCompleteEvent(RequestArrivalEvent arrivingEvent, int server) {
        double serviceTime = arrivingEvent.getType().getServiceTime();
        double meanServiceTime = expDist.random(serviceTime);
        double departureTime = systemState.getClock() + meanServiceTime;
        eventList.add(new RequestCompleteEvent(arrivingEvent.getTime(), departureTime, server));
    }

    private void updateStatistics() {
        int activeServers = systemState.getNumberOfActiveServers();
        int numServers = systemState.getNumberOfServers();
        double elapsedTime = systemState.getClock() - systemState.getLastEventTime();
        double utilizedServers = (double) activeServers / numServers;
        double serverUtilization = utilizedServers * elapsedTime;
        statistics.addTimeUnderLoad(serverUtilization);

        // requests in system
        double requestsInSystem = activeServers + requestBuffer.getNumberOfRequestsInBuffer();
        statistics.addRequestsInSystem(requestsInSystem);
    }
}
