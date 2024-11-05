// package examples.webapp;

// public class AcrossReplicationData {
// private double[] Y;
// private double[] StandardError;
// private double[] ConfidenceInterval;

// private int numberOfReplications;
// private WebAppV1 simulation = new WebAppV1();

// public static void main(String[] args) {
// AcrossReplicationData acrossReplicationData = new AcrossReplicationData();
// acrossReplicationData.run();
// }

// private void run() {
// numberOfReplications = 30;
// Y = new double[numberOfReplications];
// StandardError = new double[numberOfReplications];
// ConfidenceInterval = new double[numberOfReplications];

// for (int i = 0; i < numberOfReplications; i++) {
// simulation.initialize();
// simulation.run();
// Y[i] = simulation.getMeanDelay();
// StandardError[i] = simulation.getStandardError();
// ConfidenceInterval[i] = simulation.getConfidenceInterval();
// }

// double sumY = 0;
// double sumSE = 0;
// double sumCI = 0;
// for (int i = 0; i < numberOfReplications; i++) {
// sumY += Y[i];
// sumSE += StandardError[i];
// sumCI += ConfidenceInterval[i];
// }

// double meanY = sumY / numberOfReplications;
// double meanSE = sumSE / numberOfReplications;
// double meanCI = sumCI / numberOfReplications;

// System.out.println("Mean of Y: " + meanY);
// System.out.println("Mean of Standard Error: " + meanSE);
// System.out.println("Mean of Confidence Interval: " + meanCI);
// }
// }
