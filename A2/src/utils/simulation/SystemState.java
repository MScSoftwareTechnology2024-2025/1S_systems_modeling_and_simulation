package utils.simulation;

/**
 * The SystemState class represents the state of a system in a simulation.
 * It includes information about the simulation clock, the number of entities
 * in the queue and in service, the time of the last event, the total time the
 * system has been busy, and whether the system is currently busy.
 */
public class SystemState {
    private static final int Q_LIMIT = 10_000;
    private double clock;
    private int numInQ;
    private int numInS;
    private double timeLastEvent;
    private boolean[] servers;
    private double nextArrivalTime = 0.0;

    // Statistics
    private int num_delayed;
    private double total_delay;
    private double area_under_Qt;
    private double area_under_Bt;

    public SystemState(int numberOfServers) {
        this.clock = 0.0;
        this.numInQ = 0;
        this.numInS = 0;
        this.timeLastEvent = 0.0;

        this.num_delayed = 0;
        this.total_delay = 0.0;
        this.area_under_Qt = 0.0;
        this.area_under_Bt = 0.0;

        this.servers = new boolean[numberOfServers];
        // this.eventList[0] = clock + timeOfFirstEvent;
        // this.eventList[1] = Double.MAX_VALUE;
    }

    public void proceedClock(double time) {
        clock = time;
    }

    public double getClock() {
        return clock;
    }

    public boolean isFirstEvent() {
        return clock == 0.0;
    }

    public double getNextArrivalTime() {
        return nextArrivalTime;
    }

    public void setNextArrivalTime(double nextArrivalTime) {
        this.nextArrivalTime = nextArrivalTime;
    }

    public boolean isSystemBusy() {
        for (boolean server : servers) {
            if (!server) {
                return false;
            }
        }

        return true;
    }

    public void assignServer(int serverId) {
        if (serverId < 0 || serverId >= servers.length) {
            throw new IllegalArgumentException("Invalid server ID: " + serverId);
        }

        servers[serverId] = true;
    }

    public int getFreeServer() {
        for (int i = 0; i < servers.length; i++) {
            boolean serverIsFree = !servers[i];
            if (serverIsFree) {
                return i;
            }
        }

        // no servers are available
        return -1;
    }

    public void freeServer(int serverId) {
        if (serverId < 0 || serverId >= servers.length) {
            throw new IllegalArgumentException("Invalid server ID: " + serverId);
        }

        servers[serverId] = false; // Mark server as free
    }
}
