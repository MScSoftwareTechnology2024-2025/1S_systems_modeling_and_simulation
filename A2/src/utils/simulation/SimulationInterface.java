package utils.simulation;

public interface SimulationInterface {
    public void run();

    public void initialize();

    public void processEvent(EventInterface event);
}
