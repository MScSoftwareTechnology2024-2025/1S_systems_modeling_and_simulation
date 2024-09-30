import random
import math

num_of_random_numbers = 30
seed = 123456789

# exercise 1
def generate_random_number_array():
    random.seed(seed)
    random_numbers = [random.random() for _ in range(num_of_random_numbers)]
    return random_numbers

numbers = generate_random_number_array()

# exercise 2
def frequency_test(numbers):
    # step 1
    numbers.sort()
    # step 2
    N = len(numbers)
    D_plus_arr = []
    D_minus_arr = []
    D = 0
    for i in range(len(numbers)):
        j = i + 1
        R_i = numbers[i]
        D_plus_i = j/N - R_i
        D_minus_i = R_i - (j-1)/N
        D_plus_arr.append(D_plus_i)
        D_minus_arr.append(D_minus_i)
    D_plus = max(D_plus_arr)
    D_minus = max(D_minus_arr)
    # step 3
    D = max(D_plus, D_minus)
    print(f"D: {D}")
    # step 4
    D_alpha = 0.410 # from appendix table

    # step 5
    if D <= D_alpha:
        print("The numbers are uniformly distributed")
    else:
        print("The numbers are not uniformly distributed")


# !EXAMPLE: Test for whether the 3rd, 8th, 13th, and so on, numbers in the sequence at the beginning of this section are autocorrelated using α = 0.05. Here, i = 3 (beginning with the third number), l = 5 (every five numbers), N = 30 (30 numbers in the sequence), and M = 4 (largest integer such that 3 + (M + 1)5 ≤ 30).
def autocorrelation_test(numbers):
    N = len(numbers)
    i = 3 # beginning of the first number
    l = 5 # every l numbers
    alpha = 0.05
    M = (N - i) // l - 1 # largest integer such that i + (M + 1)l <= N
    # M = (N - i - l)
    print(f"M: {M}")
    # ro, sigma, Z_0 <- to calculate
    for m in range(M):
        R_i = numbers[i + 1]
    
# Generator for exponential distributions
def variate_generator_for_exponential_distribution(numbers, mean = 0.5):
    lambda_ = mean
    U = numbers
    X = [-1/lambda_ * math.log(1 - R) for R in numbers]
    print(X)
    

# Generator for triangular distributions


# test 1
# frequency_test(numbers)
# autocorrelation_test(numbers)
variate_generator_for_exponential_distribution(numbers)