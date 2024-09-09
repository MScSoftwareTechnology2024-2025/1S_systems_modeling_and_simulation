// Some for fun functions to play with statistical models in simulation

// Calculate the factorial of a number
// n! = n * (n-1) * (n-2) * ... * 1
// 0! = 1
// 5! = 5 * 4 * 3 * 2 * 1 = 120
const calculateFactorial = (n) => {
  if (n === 0) return 1;
  return n * calculateFactorial(n - 1);
};

// Calculate the number of ways to choose k items from n items -> n choose k (n C k)
// n! / (k! * (n-k)!)
// 5 choose 2 = 5! / (2! * (5-2)!) = 10
const calculateNChooseK = (n, k) => {
  return calculateFactorial(n) / (calculateFactorial(k) * calculateFactorial(n - k));
};

const roundTo3DecimalPlaces = (num) => {
  const factor = 1000;
  return Math.round(num * factor) / factor;
};

export { calculateFactorial, calculateNChooseK, roundTo3DecimalPlaces };
