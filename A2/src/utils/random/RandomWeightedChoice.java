package utils.random;

import java.util.List;
import utils.simulation.RequestType;
import java.util.Random;

public class RandomWeightedChoice {
    private static final Random random = new Random();

    public static RequestType chooseRequestType(List<RequestType> choices) {
        double totalWeight = choices.stream().mapToDouble(RequestType::getPercentageChance).sum();
        double randomValue = random.nextDouble() * totalWeight;

        for (RequestType choice : choices) {
            randomValue -= choice.getPercentageChance();
            if (randomValue < 0) {
                return choice;
            }
        }

        return null;
    }
}
