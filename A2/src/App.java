import simulations.Configuration;

public class App {
    private final static int repetitions = 1;

    public static void main(String[] args) throws Exception {
        App.runConfiguration1();
        App.runConfiguration2();
    }

    public static void runConfiguration1() {
        int SERVERS = 4;
        int BUFFER = 6;

        for (int i = 0; i < repetitions; i++) {
            Configuration configuration1 = new Configuration(i + 101, SERVERS, BUFFER);
            configuration1.run();
        }
    };

    public static void runConfiguration2() {
        int SERVERS = 5;
        int BUFFER = 5;

        for (int i = 0; i < repetitions; i++) {
            Configuration configuration2 = new Configuration(i + 101, SERVERS, BUFFER);
            configuration2.run();
        }
    };
}
