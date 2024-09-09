import { calculateNChooseK, calculateFactorial } from "./StatisticalModelsInSimulation.mjs";

// Discrete Distributions
/**
 * - countable set of outcomes
 * - probability mass function
 */

/**
 * Bernoulli Distribution
 *
 * The Bernoulli distribution is like asking, "What are the chances of getting
 * heads if I flip the coin once?" If you flip the coin once, you either get heads
 * (success) or tails (failure). The formula for this is:
 * p(k) = p^k * (1 - p)^(1 - k)
 * Where:
 * ( p ) is the probability of getting heads (success).
 * ( k ) is 1 if you get heads and 0 if you get tails.
 *
 */

export const BernulliDistribution = (k, p = 0.5) => {
  return p ** k * (1 - p) ** (1 - k);
};

const probabilityOfHeads_fairCoin = BernulliDistribution(1, 0.5);
const probabilityOfTails_fairCoin = BernulliDistribution(0, 0.5);
console.log(`BernulliDistribution | Probability of getting heads with a fair coin is ${probabilityOfHeads_fairCoin}`); // 0.5
console.log(`BernulliDistribution | Probability of getting tails with a fair coin is ${probabilityOfTails_fairCoin}`); // 0.5

// Binomial Distribution
/**
 * - Bernoulli distribution extended to multiple trials
 *
 * Now, if you flip the coin multiple times, say 5 times, and you want to know the
 * probability of getting exactly 3 heads, you use the binomial distribution. This
 * is like combining multiple Bernoulli trials.
 *
 * The formula for this is:
 * P(k) = \binom{n}{k} * p^k * (1 - p)^(n - k)
 *
 * Where:
 * ( n ) is the number of flips.
 * ( k ) is the number of heads you want.
 * ( \binom{n}{k} ) is the binomial coefficient, which tells you how many ways you
 * can get ( k ) heads in ( n ) flips.
 */

const BinomialDistribution = (n, k, p = 0.5) => {
  return calculateNChooseK(n, k) * p ** k * (1 - p) ** (n - k);
};

// We have a fair coin, we flip it 5 times. What is the probability of getting exactly 3 heads?
const _5flipsOfCoinWith3Heads = BinomialDistribution(5, 3, 0.5);

console.log(`BinomialDistribution | 5flipsOfCoinWith3Heads is ${_5flipsOfCoinWith3Heads}`); // 0.3125

// Cumulative Distribution Function (CDF)
/**
 * - probability that a random variable X will take a value less than or equal to x
 * - P(X <= x)
 * - CDF = Σ p(x)
 *
 * The CDF is like asking, "What are the chances of getting up to a certain number
 * of heads?" For example, if you want to know the probability of getting 0, 1, or
 * 2 heads in 5 flips, you sum up the probabilities of getting 0 heads, 1 head, and
 * 2 heads.
 */

const CumulativeDistributionFunction = (n, k, p = 0.5) => {
  let sum = 0;
  for (let i = 0; i <= k; i++) {
    sum += BinomialDistribution(n, i, p);
  }
  return sum;
};

// We have a fair coin, we flip it 5 times. What is the probability of getting up to 2 heads?
const _5flipsOfCoinWithUpTo2Heads = CumulativeDistributionFunction(5, 2, 0.5);
console.log(
  `CumulativeDistributionFunction | Cumulative distribution function for _5flipsOfCoinWithUpTo2Heads is ${_5flipsOfCoinWithUpTo2Heads}`
);

// Poisson Distribution
/**
 * Imagine you have a magic clock that tells you how many emails you get every
 * hour. Sometimes you get more emails, sometimes fewer, but on average, you get
 * about 2 emails per hour. The Poisson distribution helps us figure out the
 * probability of getting a certain number of emails in a given time period, based
 * on this average rate.
 *
 * Key Points
 * - Average Rate ((\lambda)): This is the average number of emails you get per hour. In your case, (\lambda = 2).
 * - Time Period: We're looking at the next hour.
 * - Number of Events (k): This is the number of emails you might get in the next hour.
 * - e: Euler's number, approximately 2.71828.
 *
 * _______________________________________________________
 *
 * Example: How Many Emails Will I Get in the Next Hour?
 * Let's say you want to know the probability of getting exactly 3 emails in the next hour.
 * Average Rate ((\lambda)): 2 emails per hour.
 * Number of Emails (k): 3.
 * Using the formula: [ P(3; 2) = \frac{2^3 \cdot e^{-2}}{3!} ]
 *
 * Let's calculate it step-by-step:
 * - Calculate ( 2^3 ): [ 2^3 = 8 ]
 *
 * - Calculate ( e^{-2} ): [ e^{-2} \approx 0.1353 ]
 *
 * - Calculate ( 3! ): [ 3! = 3 \times 2 \times 1 = 6 ]
 *
 * - Put it all together: [ P(3; 2) = \frac{8 \cdot 0.1353}{6} \approx \frac{1.0824}{6} \approx 0.1804 ]
 * ---> So, the probability of getting exactly 3 emails in the next hour is approximately 18.04%.
 */

const PoissonDistribution = (k, lambda) => {
  const e = Math.E; // Euler's number
  const numerator = lambda ** k * e ** -lambda; // lambda^k * e^-lambda
  const denominator = calculateFactorial(k); // k!
  return numerator / denominator; // (lambda^k * e^-lambda) / k!
};

// We have an average of 2 emails per hour. What is the probability of getting exactly 3 emails in the next hour?
const probabilityOfGetting3Emails = PoissonDistribution(3, 2);
console.log(`PoissonDistribution | Probability of getting 3 emails in the next hour is ${probabilityOfGetting3Emails}`);

// Continuous Distributions
/**
 * - uncountable set of outcomes
 * - probability density function
 */

// Uniform Distribution
/**
 * Imagine you have a box of crayons, and all the crayons are exactly the same size
 * and shape. You close your eyes, reach into the box, and pick one crayon at
 * random. Since all the crayons are the same, you have an equal chance of picking
 * any one of them. This is what the Uniform Distribution is like.
 *
 * Key Points
 * - Equal Chance: Every outcome has the same probability.
 * - Range: The outcomes are spread evenly over a certain range.
 * - Example: Picking a Random Number
 *
 * _______________________________________________________
 * Let's say you have a number line from 1 to 10. If you close your eyes and point to a number on this line,
 * you have an equal chance of pointing to any number between 1 and 10. This is a Uniform Distribution.
 *
 * _______________________________________________________
 * Visualizing the Uniform Distribution
 * Imagine a straight line from 1 to 10. Every point on this line is equally likely to be chosen.
 * If you were to plot this on a graph, it would look like a flat, horizontal line because every number has the same chance of being picked.
 */

/**
 *
 * @param {int} lowerBound -> lower bound
 * @param {int} upperBound -> upper bound
 * @param {int} numberOfInterest -> random variable
 * @returns
 */
const UniformDistribution = (lowerBound, upperBound, numberOfInterest) => {
  // if x is less than the lower bound or greater than the upper bound, the probability is 0 / impossible outcome
  if (numberOfInterest < lowerBound || numberOfInterest > upperBound) {
    return 0;
  }

  // if x is between the lower and upper bounds, the probability is 1 / possible outcome
  return 1 / (upperBound - lowerBound);
};

// We have a number line from 1 to 10. What is the probability of picking the number 5?
const probabilityOfPicking5 = UniformDistribution(1, 10, 5);
console.log(`UniformDistribution | Probability of picking number 5 is ${probabilityOfPicking5}`);

// We integrated the Uniform Distribution to get the Cumulative Distribution Function (CDF).
const CDF_UniformDistribution = (lowerBound, upperBound, numberOfInterest) => {
  // if x is less than the lower bound, the probability is 0
  if (numberOfInterest < lowerBound) {
    return 0;
  }

  // if x is greater than the upper bound, the probability is 1
  if (numberOfInterest >= upperBound) {
    return 1;
  }

  // if x is between the lower and upper bounds, the probability is (x - a) / (b - a)
  return (numberOfInterest - lowerBound) / (upperBound - lowerBound);
};

// We have a number line from 1 to 10. What is the probability of picking a number less than or equal to 5?
const probabilityOfPickingLessThanOrEqualTo5 = CDF_UniformDistribution(1, 10, 5);
console.log(
  `CDF_UniformDistribution | Probability of picking a number less than or equal to 5 is ${probabilityOfPickingLessThanOrEqualTo5}`
);

// Normal Distribution
/**
 * Imagine you have a class of students, and you want to know how tall they are.
 * If you measure everyone's height, you'll find that most students are around the
 * average height, with fewer students being very short or very tall.
 * This pattern of heights forms a bell-shaped curve called the Normal Distribution.
 * Key Points
 * - Average (Mean): The middle point of the distribution where most students' heights are clustered.
 * - Spread (Standard Deviation): How spread out the heights are from the average.
 * A small spread means most students are close to the average height, while a large spread means the heights vary more.
 *
 * _______________________________________________________
 * Heights of Students
 * Let's say the average height of students in the class is 4 feet, and the standard deviation is 0.5 feet.
 * This means most students are around 4 feet tall, but some are shorter or taller.
 * The Normal Distribution helps us understand the probability of different heights.
 * _______________________________________________________
 */

const NormalDistribution = (x, mean, stdDev) => {
  const numerator = Math.exp(-((x - mean) ** 2) / (2 * stdDev ** 2)); // e^(-(x - mean)^2 / (2 * stdDev^2))
  const denominator = stdDev * Math.sqrt(2 * Math.PI); // stdDev * sqrt(2 * pi)
  return numerator / denominator; // e^(-(x - mean)^2 / (2 * stdDev^2)) / (stdDev * sqrt(2 * pi))
};

const probabilityOfStudentHeightBeing4Feet = NormalDistribution(4, 4, 0.5);

console.log(
  `NormalDistribution | Probability of a student's height being 4 feet is ${probabilityOfStudentHeightBeing4Feet}`
);
