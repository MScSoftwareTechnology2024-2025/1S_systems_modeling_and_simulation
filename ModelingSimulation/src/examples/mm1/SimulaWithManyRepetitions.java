package examples.mm1;

import java.util.Vector;

import generators.random.variate.ExponentialDistribution;

class Data {

	private int num_delayed;
	private double total_delay;
	private double area_under_Qt;
	private double area_under_Bt;
	private double duration;

	public Data(int nd, double td, double qt, double bt, double duration) {
		this.duration = duration;
		this.num_delayed = nd;
		this.total_delay = td;
		this.area_under_Bt = bt;
		this.area_under_Qt = qt;
	}

	public int getNum_delayed() {
		return num_delayed;
	}

	public double getAverage_delay() {
		return total_delay / this.num_delayed;
	}

	public void setTotal_delay(double total_delay) {
		this.total_delay = total_delay;
	}

	public double getAverageNumberInQueue() {
		return area_under_Qt / this.duration;
	}

	public double getAverageServerUtilization() {
		return area_under_Bt / this.duration;
	}

	public String toString() {
		String s = "";

		// Compute and write estimates of desired measures of performance
		s += "\n\nAverage delay in queue " + this.total_delay / this.num_delayed + " minutes \n\n";

		s += "Average number in queue " + this.area_under_Qt / this.duration + "\n\n";

		s += "Server utilization " + area_under_Bt / this.duration + "\n\n";
		s += "Time simulation ended " + this.duration + " minutes\n\n";

		// this.log("Number of iterations: " + iterations + "\n");

		return s;

	}

}

public class SimulaWithManyRepetitions {

	static final private int Q_LIMIT = 1000;
	static final private int BUSY = 1;
	static final private int IDLE = 0;
	static final private int NUM_EVENTS = 3;

	static final private int ARRIVE = 0;
	static final private int DEPART = 1;
	static final private int STOP = 2;

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
	private double event_list[] = new double[NUM_EVENTS];

	// Computation
	private int next_event_type;
	private double mean_interarrival;
	private double mean_service;
	private double time_end;
	// private int iterations;

	ExponentialDistribution ed;

	public SimulaWithManyRepetitions(double mean_interarrival, double mean_service, double time_end) {

		long random_SEED = (long) (Math.random() * 100);
		ed = new ExponentialDistribution(random_SEED);

		/* Read input parameters. */
		this.mean_interarrival = mean_interarrival;
		this.mean_service = mean_service;
		this.time_end = time_end;
	}

	public Data getData() {
		return new Data(this.num_delayed, this.total_delay, this.area_under_Qt, this.area_under_Bt, this.clock);
	}

	public void initialize() {
		// Initialize the simulation clock
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
		this.event_list[ARRIVE] = clock + ed.random(mean_interarrival);
		this.event_list[DEPART] = Double.MAX_VALUE; // 1.0e+30;
		this.event_list[STOP] = time_end;
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
			System.out.println("Event list empty at time " + this.clock);
			System.exit(1);
		}
		/* The event list is not empty, so advance the simulation clock. */
		clock = min_time_next_event;
	}

	private void update_time_avg_stats() {

		double time_since_last_event;

		// Compute time since last event, and update last-event-time marker.
		time_since_last_event = this.clock - this.time_last_event;
		this.time_last_event = this.clock;

		// Update area under number-in-queue function
		this.area_under_Qt += this.num_in_q * time_since_last_event;

		// Update area under server-busy indicator function
		this.area_under_Bt += this.server_status * time_since_last_event;

	}

	private void arrive() {

		// Schedule next arrival.
		this.event_list[ARRIVE] = this.clock + ed.random(this.mean_interarrival);

		// Check to see whether server is busy
		if (server_status == BUSY) {

			// Server is busy, so increment number of customers in queue
			this.num_in_q++;

			// Check to see whether an overflow condition exists.
			if (num_in_q > Q_LIMIT) {
				// The queue has overflowed, so stop the simulation.

				System.out.println("Overflow of the array time_arrival at time " + clock);

				System.exit(2);
			}

			// There is still room in the queue, so store the time of arrival of the
			// arriving customer at the (new) end of time_arrival.
			this.time_arrival[num_in_q] = this.clock;

		} else {

			// Increment the number of customers delayed, and make server busy.
			this.num_delayed++;
			this.server_status = BUSY;

			// Schedule a departure (service completion).
			this.event_list[DEPART] = this.clock + ed.random(mean_service);
		}
	}

	private void depart() {

		// Check to see whether the queue is empty.
		if (this.num_in_q == 0) {
			// The queue is empty so make the server idle and eliminate the departure
			// (service completion) event from consideration.
			this.server_status = IDLE;
			this.event_list[DEPART] = Double.MAX_VALUE;

		} else {
			// The queue is nonempty, so decrement the number of customers in queue.
			this.num_in_q--;

			// Compute the delay of the customer who is beginning service and update the
			// total delay accumulator.
			double delay = this.clock - this.time_arrival[1];
			this.total_delay += delay;

			// Increment the number of customers delayed, and schedule departure.
			this.num_delayed++;

			// Schedule a departure event for this customer
			this.event_list[DEPART] = this.clock + ed.random(mean_service);

			// Move each customer in queue (if any) up one place.
			for (int i = 1; i <= this.num_in_q + 1; i++)
				this.time_arrival[i] = this.time_arrival[i + 1];

		}
	}

	public Data execute() {

		// Run the simulation while more delays are still needed.
		while (this.next_event_type < NUM_EVENTS) {

			// Determine the next event.
			this.timing();

			// Update time-average statistical accumulators
			this.update_time_avg_stats();
			// this.logStatus();

			// Invoke the appropriate event function
			switch (this.next_event_type) {
				case ARRIVE:
					this.arrive();
					break;
				case DEPART:
					this.depart();
					break;
				case STOP:
					return new Data(this.num_delayed, this.total_delay, this.area_under_Qt, this.area_under_Bt,
							this.clock);
			}
		}

		return null;

	}

	/* Main function. */
	public static void main(String[] args) {

		/* Read input parameters. */
		double mean_interarrival = Double.parseDouble(args[0]);
		double mean_service = Double.parseDouble(args[1]);
		int end_time = Integer.parseInt(args[2]);
		int iterations = Integer.parseInt(args[3]);

		Vector<Data> results = new Vector<Data>();

		long start = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++) {

			SimulaWithManyRepetitions mm1 = new SimulaWithManyRepetitions(mean_interarrival, mean_service, end_time);
			// Initialize the Simulation
			mm1.initialize();

			// Execute the simulation
			Data r = mm1.execute();
			results.add(r);

			// System.out.println(r.toString());

			// Invoke the report generator and end the simulation.
			// mm1.report(null);

		}

		long end = System.currentTimeMillis();

		System.out.println("Experiment duration (millis) = \t\t\t" + (end - start));

		double Y_delay = 0;
		double Y_queue = 0;
		double Y_server = 0;

		int R = results.size();

		// AVERAGES
		for (int i = 0; i < R; i++) {

			Data yi = results.get(i);
			Y_delay += (yi.getAverage_delay() / R);
			Y_queue += (yi.getAverageNumberInQueue() / R);
			Y_server += (yi.getAverageServerUtilization() / R);

		}
		System.out.println("Total Average Delay = \t\t\t\t" + Y_delay);
		System.out.println("Total Average Number in Queue = \t\t" + Y_queue);
		System.out.println("Total Average Server Utilization = \t\t" + Y_server);

		double S2_delay = 0;
		double S2_queue = 0;
		double S2_server = 0;
		// VARIANCE
		for (int i = 0; i < R; i++) {
			Data yi = results.get(i);
			S2_delay += (Math.pow(yi.getAverage_delay() - Y_delay, 2) / (R - 1));
			S2_queue += (Math.pow(yi.getAverageNumberInQueue() - Y_queue, 2) / (R - 1));
			S2_server += (Math.pow(yi.getAverageServerUtilization() - Y_server, 2) / (R - 1));
		}

		System.out.println("\nSample Variance Delay (S^2) = \t\t\t" + S2_delay);
		System.out.println("Sample Variance Number in Queue (S^2) = \t" + S2_queue);
		System.out.println("Sample Variance Server Utilization (S^2) = \t" + S2_server);

		// CONFIDENCE INTERVAL t(0.025, 29)
		double S_delay = Math.sqrt(S2_delay);
		double S_queue = Math.sqrt(S2_queue);
		double S_server = Math.sqrt(S2_server);

		// double H_delay = 2.04 * (S_delay / Math.sqrt(R));
		// double H_queue = 2.04 * (S_queue / Math.sqrt(R));
		// double H_server = 2.04 * (S_server / Math.sqrt(R));
		//
		//
		// System.out.println("\nConfidence Interval Delay t(0.025, 29) = \t[" +
		// (Y_delay - H_delay) + "," + (Y_delay + H_delay) + "]");
		// System.out.println("Confidence Interval Queue t(0.025, 29) = \t[" + (Y_queue
		// - H_queue) + "," + (Y_queue + H_queue) + "]");
		// System.out.println("Confidence Interval Server t(0.025, 29) = \t[" +
		// (Y_server - H_server) + "," + (Y_server + H_server) + "]");
		//

		// CONFIDENCE INTERVAL t(0.025, 120)
		// double S_delay = Math.sqrt(S2_delay);
		// double S_queue = Math.sqrt(S2_queue);
		// double S_server = Math.sqrt(S2_server);
		//
		double H_delay = 1.98 * (S_delay / Math.sqrt(R));
		double H_queue = 1.98 * (S_queue / Math.sqrt(R));
		double H_server = 1.98 * (S_server / Math.sqrt(R));

		System.out.println("\nConfidence Interval Delay t(0.025, 120) = \t[" + (Y_delay - H_delay) + ","
				+ (Y_delay + H_delay) + "]");
		System.out.println("Confidence Interval Queue t(0.025, 120) = \t[" + (Y_queue - H_queue) + ","
				+ (Y_queue + H_queue) + "]");
		System.out.println("Confidence Interval Server t(0.025, 120) = \t[" + (Y_server - H_server) + ","
				+ (Y_server + H_server) + "]");
		//

		// Plot the results
		// mm1.plot();

		return;

	}

}
