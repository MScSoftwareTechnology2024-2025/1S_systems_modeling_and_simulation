package simulations.interfaces;

import simulations.common.SimulationStatistics;

public interface SimulationInterface {
    public SimulationStatistics run();

    public void initialize();

    public void processEvent(EventInterface event);
}
