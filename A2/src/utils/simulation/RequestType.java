package utils.simulation;

public class RequestType {
    private String name;
    private double serviceRate;
    private double percentageChance;
    private double serviceTime;

    public RequestType(String name, int serviceTime, double percentageChance) {
        this.name = name;
        this.serviceTime = serviceTime;
        this.serviceRate = this.getServiceRateFromServiceTime(serviceTime);
        this.percentageChance = percentageChance;
    }

    private double getServiceRateFromServiceTime(int serviceTime) {
        return (1.0 / serviceTime);
    }

    public String getName() {
        return name;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public double getServiceRate() {
        return serviceRate;
    }

    public double getPercentageChance() {
        return percentageChance;
    }
}
