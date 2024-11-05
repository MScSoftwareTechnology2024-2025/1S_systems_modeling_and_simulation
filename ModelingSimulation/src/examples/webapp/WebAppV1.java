package examples.webapp;

import java.util.Vector;

import generators.random.variate.ExponentialDistribution;

public class WebAppV1 {
    private final double MEAN_INTERARRIVAL = 7; // per second
    private final WebService[] servers = new WebService[2];

    /**
     * Needs to be different because we need to do cleanup after each day (8 hours)
     */
    private final int HOURS_PER_DAY = 8;
    private final int TARGET_DAYS = 90;
    private final int TARGET_HOURS = HOURS_PER_DAY * TARGET_DAYS;

    // Output
    private Vector<Double> time = new Vector<Double>();
    private Vector<Double> Qt = new Vector<Double>();
    private Vector<Double> Bt = new Vector<Double>();

    static final private int Q_LIMIT = 1000;
    static final private int BUSY = 1;
    static final private int IDLE = 0;
    static final private int NUM_EVENTS = 2;
    static final private long SEED = 15;

    // System State
    private int server_status;
    private int num_in_q;
    private double time_arrival[] = new double[Q_LIMIT + 1];
    private double time_last_event;

    // Statistics
    private int num_delayed;
    private double total_delay;
    private double area_under_Qt;
    private double area_under_Bt;

    // Simulation
    private double clock;
    private double event_list[] = new double[2];

    // Computation
    private int next_event_type;
    private double mean_interarrival;
    private int iterations;

    ExponentialDistribution ed;

    public WebAppV1() {
        this.ed = new ExponentialDistribution(SEED);
        this.mean_interarrival = MEAN_INTERARRIVAL;
        this.servers[0] = new WebService();
        this.servers[1] = new WebService();
    }

    private void timing() {
        int i;
        double min_time_next_event = Double.MAX_VALUE - 1;
        next_event_type = -1;

        // Determine the event type of the next event to occur
        for (i = 0; i < NUM_EVENTS; i++)
            if (this.event_list[i] < min_time_next_event) {
                min_time_next_event = this.event_list[i];
                next_event_type = i;
            }

        /* Check to see whether the event list is empty. */
        if (next_event_type == -1) {

            // The event list is empty, so stop the simulation.
            System.exit(1);
        }

        /* The event list is not empty, so advance the simulation clock. */
        clock = min_time_next_event;
    }

    private void update_time_avg_stats() {
        double time_since_last_event;

        time.add(this.time_last_event);
        Qt.add((double) num_in_q);
        Bt.add((double) server_status);

        time.add(clock);
        Qt.add((double) num_in_q);
        Bt.add((double) server_status);

        // Compute time since last event, and update last-event-time marker.
        time_since_last_event = this.clock - this.time_last_event;
        this.time_last_event = this.clock;

        // Update area under number-in-queue function
        this.area_under_Qt += this.num_in_q * time_since_last_event;

        // Update area under server-busy indicator function
        // ! for multiple servers here we (this.server_status/num_of_servers) *
        // time_since_last_event
        this.area_under_Bt += this.server_status * time_since_last_event;

    }

    // for task3 check / manipulate the time of next event based on the current time
    // and server status.. so change the carArrive & carDepart methods to fit the
    // usecase.. also change the type/outcome of data we want to collect in
    // simulation
    public void requestArrives() {
        this.event_list[0] = this.clock + ed.random(this.mean_interarrival);

        if (!this.servers[0].isBusy()) {
            this.servers[0].handleRequest();
            this.event_list[1] = this.clock + this.servers[0].getServiceTime();
            return;
        }

        // when server 2 is handling it, increase the queue
        if (!this.servers[1].isBusy()) {
            this.servers[1].handleRequest();
            this.event_list[1] = this.clock + this.servers[1].getServiceTime();
            return;
        }

        // What happens if both servers are busy?
        this.num_in_q++;
        double delay = 0.0;
        this.total_delay += delay;
        this.num_delayed++;
        this.server_status = BUSY;
    }

    private void requestLeaves() {
        Boolean queueIsEmpty = this.num_in_q == 0;

        // Check to see whether the queue is empty.
        if (queueIsEmpty) {
            // The queue is empty so make the server idle and eliminate the departure
            // (service completion) event from consideration.
            this.server_status = IDLE;
            this.event_list[1] = Double.MAX_VALUE;
            return;
        }

        // The queue is nonempty, so decrement the number of customers in queue.
        // If the first server is busy, then the request is handled by the first server
        if (this.servers[0].isProcessing()) {
            this.servers[0].requestHandled();
        } else {
            this.servers[1].requestHandled();
        }

        System.out.println("Request left the system at " + this.clock);

        // Move each customer in queue (if any) up one place.
        for (int i = 1; i <= this.num_in_q + 1; i++)
            this.time_arrival[i] = this.time_arrival[i + 1];
    }

    public void initialize() {
        this.clock = 0.0;

        // Initialize the state variables
        this.server_status = IDLE;
        this.num_in_q = 0;
        this.time_last_event = 0.0;

        // Initialize the statistical counters
        this.num_delayed = 0;
        this.total_delay = 0.0;
        this.area_under_Qt = 0.0;
        this.area_under_Bt = 0.0;

        // Initialize event list. Since no customers are present,
        // the departure (service completion) event is eliminated from consideration.
        this.event_list[0] = clock + ed.random(MEAN_INTERARRIVAL);
        this.event_list[1] = Double.MAX_VALUE; // 1.0e+30;
    }

    public void run() {
        // Run the simulation

        int j = 0;
        // Run the simulation while more delays are still needed.
        while (this.clock < TARGET_HOURS) {

            // Determine the next event.
            this.timing();

            // Update time-average statistical accumulators
            this.update_time_avg_stats();
            // this.logStatus();

            // Invoke the appropriate event function
            switch (this.next_event_type) {
                case 0:
                    this.requestArrives();
                    break;
                case 1:
                    this.requestLeaves();
                    break;
            }
            j++;

        }

        iterations = j;
    }

    public void report(Object object) {
        // Write report heading and input parameters
        System.out.println("Single-server queueing system\n");
        System.out.println("Mean interarrival time " + mean_interarrival + " hours\n");
        // System.out.println(ud.toString() + "\n\n");

        // Compute and write estimates of desired measures of performance
        System.out.println("\n\nAverage delay in queue " + this.total_delay / this.num_delayed + " hours \n");
        System.out.println("Average number in queue " + this.area_under_Qt / this.clock + "\n");

        System.out.println("Server utilization " + area_under_Bt / clock + "\n");
        System.out.println("Time simulation ended " + this.clock + " hours\n");

        System.out.println("Number of iterations: " + iterations + "\n");
    }

    public static void main(String[] args) {

        WebAppV1 webapp = new WebAppV1();

        // Initialize the Simulation
        webapp.initialize();

        // Execute the simulation
        webapp.run();

        // Invoke the report generator and end the simulation.
        webapp.report(null);

        // Plot the results
        // mm1.plot();

        return;
    }
}
