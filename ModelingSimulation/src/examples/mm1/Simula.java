package examples.mm1;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import generators.random.variate.ExponentialDistribution;

public class Simula {

	static final private int Q_LIMIT = 1000;
	static final private int BUSY = 1;
	static final private int IDLE = 0;
	static final private int NUM_EVENTS = 2;
	static final private long SEED = 15;

	// If you want to have a different simulation for each run, uncomment the
	// following line
	// static final private long SEED = (long) (Math.random() * 100);

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
	private double mean_service;
	private int num_delays_required;
	private int iterations;

	// Logging
	private FileOutputStream outfile;
	private Vector<Double> time = new Vector<Double>();
	private Vector<Double> Qt = new Vector<Double>();
	private Vector<Double> Bt = new Vector<Double>();

	ExponentialDistribution ed;

	public Simula(double mean_interarrival, double mean_service, int num_delays_required) {

		this.ed = new ExponentialDistribution(SEED);

		/* Read input parameters. */
		this.mean_interarrival = mean_interarrival;
		this.mean_service = mean_service;
		this.num_delays_required = num_delays_required;
	}

	public void plot() {

		List<XYChart> charts = new ArrayList<XYChart>();

		charts.add(getChartQt());
		charts.add(getChartBt());

		new SwingWrapper<XYChart>(charts).displayChartMatrix();

	}

	private XYChart getChartQt() {

		int size = Qt.size();
		double[] xData = new double[size];
		double[] yData = new double[size];
		for (int i = 0; i < Qt.size(); i++) {
			xData[i] = time.get(i);
			yData[i] = Qt.get(i);
		}

		XYChart chart = new XYChartBuilder().width(800).height(600).title("Average Number in Queue").xAxisTitle("t")
				.yAxisTitle("Q(t)").build();
		XYSeries series = chart.addSeries("Q(t)", xData, yData);

		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);

		series.setLineColor(XChartSeriesColors.BLACK);
		series.setMarkerColor(Color.BLACK);
		series.setMarker(SeriesMarkers.NONE);
		series.setLineStyle(SeriesLines.SOLID);

		return chart;

	}

	private XYChart getChartBt() {

		int size = Bt.size();
		double[] xData = new double[size];
		double[] yData = new double[size];
		for (int i = 0; i < Bt.size(); i++) {
			xData[i] = time.get(i);
			yData[i] = Bt.get(i);
		}

		XYChart chart = new XYChartBuilder().width(800).height(600).title("Server Utilization").xAxisTitle("t")
				.yAxisTitle("B(t)").build();
		XYSeries series = chart.addSeries("B(t)", xData, yData);

		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Area);

		series.setLineColor(XChartSeriesColors.BLACK);
		series.setMarkerColor(Color.BLACK);
		series.setMarker(SeriesMarkers.NONE);
		series.setLineStyle(SeriesLines.SOLID);

		return chart;

	}

	private void initReport(String s) {

		if (s == null) {
			outfile = null;
			return;
		}

		try {
			outfile = new FileOutputStream(s);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void log(String s) {

		if (outfile == null)
			System.out.print(s);
		else {
			try {
				outfile.write(s.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void closeLog() {
		if (outfile == null)
			return;
		try {
			outfile.flush();
			outfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void report(String s) {

		this.initReport(s);

		// Write report heading and input parameters
		this.log("Single-server queueing system\n\n");
		this.log("Mean interarrival time " + mean_interarrival + " minutes\n\n");
		this.log("Mean service time " + mean_service + " minutes\n\n");
		this.log("Number of customers " + num_delays_required + "\n\n");

		// Compute and write estimates of desired measures of performance
		this.log("\n\nAverage delay in queue " + this.total_delay / this.num_delayed + " minutes \n\n");
		this.log("Average number in queue " + this.area_under_Qt / this.clock + "\n\n");

		this.log("Server utilization " + area_under_Bt / clock + "\n\n");
		this.log("Time simulation ended " + this.clock + " minutes\n\n");

		this.log("Number of iterations: " + iterations + "\n");

		this.closeLog();

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
		this.event_list[0] = clock + ed.random(mean_interarrival);
		this.event_list[1] = Double.MAX_VALUE; // 1.0e+30;
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
			this.log("\nEvent list empty at time " + this.clock);
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
		this.area_under_Bt += this.server_status * time_since_last_event;

	}

	private void arrive() {

		// Schedule next arrival.
		this.event_list[0] = this.clock + ed.random(this.mean_interarrival);

		// Check to see whether server is busy
		if (server_status == BUSY) {

			// Server is busy, so increment number of customers in queue
			this.num_in_q++;

			// Check to see whether an overflow condition exists.
			if (num_in_q > Q_LIMIT) {
				// The queue has overflowed, so stop the simulation.

				this.log("\nOverflow of the array time_arrival at time " + clock);

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
			this.event_list[1] = this.clock + ed.random(mean_service);
		}
	}

	private void depart() {

		// Check to see whether the queue is empty.
		if (this.num_in_q == 0) {
			// The queue is empty so make the server idle and eliminate the departure
			// (service completion) event from consideration.
			this.server_status = IDLE;
			this.event_list[1] = Double.MAX_VALUE;

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
			this.event_list[1] = this.clock + ed.random(mean_service);

			// Move each customer in queue (if any) up one place.
			for (int i = 1; i <= this.num_in_q + 1; i++)
				this.time_arrival[i] = this.time_arrival[i + 1];

		}
	}

	public void run() {

		int j = 0;
		// Run the simulation while more delays are still needed.
		while (this.num_delayed < this.num_delays_required) {

			// Determine the next event.
			this.timing();

			// Update time-average statistical accumulators
			this.update_time_avg_stats();
			// this.logStatus();

			// Invoke the appropriate event function
			switch (this.next_event_type) {
				case 0:
					this.arrive();
					break;
				case 1:
					this.depart();
					break;
			}
			j++;

		}

		iterations = j;
	}

	/* Main function. */
	public static void main(String[] args) {

		/* Read input parameters. */
		double mean_interarrival = Double.parseDouble(args[0]);
		double mean_service = Double.parseDouble(args[1]);
		int num_delays_required = Integer.parseInt(args[2]);

		Simula mm1 = new Simula(mean_interarrival, mean_service, num_delays_required);

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
