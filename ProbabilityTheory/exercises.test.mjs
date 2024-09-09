import { calculateProbabilityOfHeads, calculateProbabilityOfHeadsWithBias } from "./exercises.mjs";
import { strict as assert } from "assert";

// Mock the calculateNChooseK function
const calculateNChooseK = (n, k) => {
  if (n === 5 && k === 3) return 10;
  if (n === 5 && k === 0) return 1;
  if (n === 5 && k === 5) return 1;
  return 0; // Default case for simplicity
};

// Replace the actual import with the mock
const originalCalculateNChooseK = global.calculateNChooseK;
global.calculateNChooseK = calculateNChooseK;

try {
  // Test cases
  assert.equal(calculateProbabilityOfHeads(5, 3), 0.3125, "Test Case 1 Failed");
  assert.equal(calculateProbabilityOfHeads(5, 0), 0.03125, "Test Case 2 Failed");
  assert.equal(calculateProbabilityOfHeads(5, 5), 0.03125, "Test Case 3 Failed");

  console.log("All test cases passed!");
} catch (error) {
  console.error(error.message);
} finally {
  // Restore the original function
  global.calculateNChooseK = originalCalculateNChooseK;
}
