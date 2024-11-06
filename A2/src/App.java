import simulations.Configuration;
import utils.simulation.SimulationStatistics;

public class App {

    public static void main(String[] args) throws Exception {
        App.runConfiguration1();
        App.runConfiguration2();
    }

    private static int calculateRepetition() {
        // TODO magic
        return 1;
    }

    public static void runConfiguration1() {
        int SERVERS = 4;
        int BUFFER = 6;
        int repetitions = calculateRepetition();

        for (int i = 0; i < repetitions; i++) {
            Configuration configuration1 = new Configuration(i + 101, SERVERS, BUFFER);
            SimulationStatistics results = configuration1.run();
            results.printStatistics();
            // TODO do output anaylsis on the statistics
        }
    };

    public static void runConfiguration2() {
        int SERVERS = 5;
        int BUFFER = 5;
        int repetitions = calculateRepetition();

        for (int i = 0; i < repetitions; i++) {
            Configuration configuration2 = new Configuration(i + 101, SERVERS, BUFFER);
            SimulationStatistics results = configuration2.run();
            results.printStatistics();
            // TODO do output anaylsis on the statistics
        }
    };
}
