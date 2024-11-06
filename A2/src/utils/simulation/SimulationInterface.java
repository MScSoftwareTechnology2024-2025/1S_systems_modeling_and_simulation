package utils.simulation;

public interface SimulationInterface {
    public SimulationStatistics run();

    public void initialize();

    public void processEvent(EventInterface event);
}
