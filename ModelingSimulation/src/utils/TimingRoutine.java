package utils;

public class TimingRoutine {
    private double clock;

    public TimingRoutine() {
        this.clock = 0;
    }

    public void proceedClock(double time) {
        this.clock += time;
    }

    public double getClock() {
        return clock;
    }

    public double setClock(double time) {
        return this.clock = time;
    }
}
