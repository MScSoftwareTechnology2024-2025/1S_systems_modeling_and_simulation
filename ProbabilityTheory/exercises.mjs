// E1: We have a fair coin. What is the probability of getting exactly 3 heads in 5 flips?

import { calculateNChooseK } from "./StatisticalModelsInSimulation.mjs";

export const calculateProbabilityOfHeads = (flips, heads) => {
  const totalOutcomes = 2 ** flips;
  const successfulOutcomes = calculateNChooseK(flips, heads);
  return successfulOutcomes / totalOutcomes;
};

// E2: We have a unfair coin (P(h) = 0.52, p(t) = 0.48). What is the probability of getting exactly 3 heads in 5 flips?

export const calculateProbabilityOfHeadsWithBias = (flips, heads) => {
  const probabilityOfHeads = 0.52;
  const probabilityOfTails = 0.48;
  const binominalCoefficient = calculateNChooseK(flips, heads);
  const probabilityOfHeadsEqualingHeads = probabilityOfHeads ** heads;
  const probabilityOfTailsEqualingFlipsMinusHeads = probabilityOfTails ** (flips - heads);

  return binominalCoefficient * probabilityOfHeadsEqualingHeads * probabilityOfTailsEqualingFlipsMinusHeads;
};

console.log(calculateProbabilityOfHeadsWithBias(5, 3)); // 0.325...
