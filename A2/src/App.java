import simulations.Configuration1;

public class App {
    private final static int repetitions = 1;

    public static void main(String[] args) throws Exception {
        App.runConfiguration1();
    }

    public static void runConfiguration1() {
        for (int i = 0; i < repetitions; i++) {
            Configuration1 configuration1 = new Configuration1(i + 101);
            configuration1.run();
        }
    };
}
