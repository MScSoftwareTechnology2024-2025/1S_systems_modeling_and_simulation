package simulations.common;

/**
 * The SystemState class represents the state of a system in a simulation.
 * It includes information about the simulation clock, the number of entities
 * in the queue and in service, the time of the last event, the total time the
 * system has been busy, and whether the system is currently busy.
 */
public class SystemState {
    private double clock;
    private boolean[] servers;
    private double nextArrivalTime = 0.0;
    private double lastEventTime = 0.0;

    public SystemState(int numberOfServers) {
        this.clock = 0.0;
        this.servers = new boolean[numberOfServers];
    }

    public void initialize() {
        this.clock = 0.0;
        for (int i = 0; i < servers.length; i++) {
            servers[i] = false;
        }
    }

    public void setLastEventTime() {
        this.lastEventTime = clock;
    }

    public double getLastEventTime() {
        return lastEventTime;
    }

    public void proceedClock(double time) {
        clock = time;
    }

    public double getClock() {
        return clock;
    }

    public int getNumberOfActiveServers() {
        int activeServers = 0;
        for (boolean server : servers) {
            if (server) {
                activeServers++;
            }
        }

        return activeServers;
    }

    public int getNumberOfServers() {
        return servers.length;
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
