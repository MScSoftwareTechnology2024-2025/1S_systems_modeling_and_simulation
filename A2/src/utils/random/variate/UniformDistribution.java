package utils.random.variate;

public class UniformDistribution {
    private double lowerLimit;
    private double upperLimit;

    public UniformDistribution(double a, double b) {
        this.lowerLimit = a;
        this.upperLimit = b;
    }

    public double generate() {
        return lowerLimit + (upperLimit - lowerLimit) * Math.random();
    }

    // sigma2 = (b - a)^2 / 12
    public double variance() {
        return Math.pow(upperLimit - lowerLimit, 2) / 12;
    }

    @Override
    public String toString() {
        return "UniformDistribution [lowerLimit=" + lowerLimit + ", upperLimit=" + upperLimit + "]";
    }
}
