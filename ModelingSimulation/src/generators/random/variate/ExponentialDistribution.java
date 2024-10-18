package generators.random.variate;

import generators.random.numbers.SimpleRandomGenerator;

public class ExponentialDistribution {

	SimpleRandomGenerator rg;
	
	public ExponentialDistribution(long seed) {
		rg = new SimpleRandomGenerator(seed);
	}
	
	// Exponential variate generation function
	public double random(double mean) {
		
		/* Return an exponential random variate with mean "mean". */
		double res = -mean * Math.log(rg.random());
		return  res;		
	}
	
}
