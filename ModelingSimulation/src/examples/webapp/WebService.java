package examples.webapp;

import generators.random.variate.ExponentialDistribution;

public class WebService {
    private static int BUFFER = 3;
    private static int requests_in_buffer = 0;
    private static int SEED = 100;
    private static double MEAN_SERVICE_RATE = 8; // requests per second
    private boolean isProcessing = false;

    ExponentialDistribution ed;

    public WebService() {
        this.ed = new ExponentialDistribution(SEED);
    }

    public boolean isBusy() {
        return requests_in_buffer >= BUFFER;
    }

    public void handleRequest() {
        if (requests_in_buffer < BUFFER) {
            requests_in_buffer++;
            setProcessing(true);
        }
    }

    public void requestHandled() {
        if (requests_in_buffer > 0) {
            requests_in_buffer--;
            setProcessing(false);
        }
    }

    public double getServiceTime() {
        return ed.random(1 / MEAN_SERVICE_RATE);
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    private void setProcessing(boolean isProcessing) {
        this.isProcessing = isProcessing;
    }
}
