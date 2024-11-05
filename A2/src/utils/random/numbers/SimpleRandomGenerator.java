package utils.random.numbers;

import java.util.Random;

public class SimpleRandomGenerator {

	private Random r;

	public SimpleRandomGenerator(long seed) {
		this.r = new Random(seed);
	}

	public double random() {
		return r.nextDouble();
	}
}
