package examples.carwash;

import java.util.Vector;

import generators.random.variate.ExponentialDistribution;
import generators.random.variate.UniformDistribution;

public class CarWashV1 {
    private final double MEAN_INTERARRIVAL = 1 / 1.25; // hours
    // private final double NUMBER_OF_DELAYS = 0;

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
    UniformDistribution ud;

    public CarWashV1() {
        this.ed = new ExponentialDistribution(SEED);
        double upper = 1 / 0.5;
        double lower = 1 / 1.5;
        this.ud = new UniformDistribution(lower, upper);
        this.mean_interarrival = MEAN_INTERARRIVAL;
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

    public void carArrives() {
        this.event_list[0] = this.clock + ed.random(this.mean_interarrival);

        Boolean serverIsBusy = this.server_status == BUSY;

        if (serverIsBusy) {
            this.num_in_q++;

            if (this.num_in_q > Q_LIMIT) {
                System.out.println("Overflow of the array time_arrival at time " + this.clock);
                // The queue is too long
                System.exit(1);
            }
            this.time_arrival[this.num_in_q] = this.clock;
            return;
        }

        double delay = 0.0;
        this.total_delay += delay;

        this.num_delayed++;
        this.server_status = BUSY;

        double service_time = ud.generate();

        this.event_list[1] = this.clock + service_time;
    }

    private void carDeparts() {

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
        this.num_in_q--;

        // Compute the delay of the customer who is beginning service and update the
        // total delay accumulator.
        double delay = this.clock - this.time_arrival[1];
        this.total_delay += delay;

        // Increment the number of customers delayed, and schedule departure.
        this.num_delayed++;

        // Schedule a departure event for this customer
        this.event_list[1] = this.clock + ud.generate();

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
                    this.carArrives();
                    break;
                case 1:
                    this.carDeparts();
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
        System.out.println(ud.toString() + "\n\n");

        // Compute and write estimates of desired measures of performance
        System.out.println("\n\nAverage delay in queue " + this.total_delay / this.num_delayed + " hours \n");
        System.out.println("Average number in queue " + this.area_under_Qt / this.clock + "\n");

        System.out.println("Server utilization " + area_under_Bt / clock + "\n");
        System.out.println("Time simulation ended " + this.clock + " hours\n");

        System.out.println("Number of iterations: " + iterations + "\n");
    }

    public static void main(String[] args) {

        CarWashV1 mm1 = new CarWashV1();

        // Initialize the Simulation
        mm1.initialize();

        // Execute the simulation
        mm1.run();

        // Invoke the report generator and end the simulation.
        mm1.report(null);

        // Plot the results
        // mm1.plot();

        return;
    }
}
