import math

# vars
c_cardio = 18
c_free_weight = 4
c_cable = 8
c_weight_machine = 12

lambda_cardio = 20
lambda_free_weight = 15
lambda_cable = 15
lambda_weight_machine = 15

mu_cardio = 2
mu_free_weight = 6
mu_cable = 12
mu_weight_machine = 6


def calculate_rho(lambda_param, c, mu):
    return lambda_param / (c * mu) 

def P0(lambda_, mu, rho, c):
    part1 = sum((lambda_ / mu) ** n / math.factorial(n) for n in range(c))
    part2 = (lambda_ / mu) ** c / (math.factorial(c) * (1 - rho))
    return 1 / (part1 + part2)

def L(c, rho , P0):
    part1 = c * rho
    part2_numerator = (part1 ** (c + 1)) * P0
    part2_denominator = math.factorial(c) * c * ((1 - rho) ** 2)

    part2 = part2_numerator / part2_denominator
    return part1 + part2

def W(L, lambda_param):
    return L / lambda_param

def W_Q(W, mu):
    return W - (1 / mu)

def L_Q(lambda_param, W_Q):
    return lambda_param * W_Q


print("_______")
print("rho:")
rho_cardio = calculate_rho(lambda_cardio, c_cardio, mu_cardio)
rho_free_weight = calculate_rho(lambda_free_weight, c_free_weight, mu_free_weight)
rho_weight_machine = calculate_rho(lambda_weight_machine, c_weight_machine, mu_weight_machine)
rho_cable = calculate_rho(lambda_cable, c_cable, mu_cable)
print(f"rho for Cardio Machines: {rho_cardio:.6f}")
print(f"rho for Free Weights: {rho_free_weight:.6f}")
print(f"rho for Cable Machines: {rho_cable:.6f}")
print(f"rho for Weight Machines: {rho_weight_machine:.6f}")

print("_______")
print("P0:")
P0_cardio = P0(lambda_cardio, mu_cardio, rho_cardio, c_cardio)
P0_free_weight = P0(lambda_free_weight, mu_free_weight, rho_free_weight, c_free_weight)
P0_cable = P0(lambda_cable, mu_cable, rho_cable, c_cable)
P0_weight_machine = P0(lambda_weight_machine, mu_weight_machine, rho_weight_machine, c_weight_machine)
print(f"P0 for Cardio Machines: {P0_cardio:.6f}")
print(f"P0 for Free Weights: {P0_free_weight:.6f}")
print(f"P0 for Cable Machines: {P0_cable:.6f}")
print(f"P0 for Weight Machines: {P0_weight_machine:.6f}")

print("_______")
print("L:")
L_cardio = L(c_cardio, rho_cardio, P0_cardio)
L_free_weight = L(c_free_weight, rho_free_weight, P0_free_weight)
L_cable = L(c_cable, rho_cable, P0_cable)
L_weight_machine = L(c_weight_machine, rho_weight_machine, P0_weight_machine)
print(f"L for Cardio Machines: {L_cardio:.6f}")
print(f"L for Free Weights: {L_free_weight:.6f}")
print(f"L for Cable Machines: {L_cable:.6f}")
print(f"L for Weight Machines: {L_weight_machine:.6f}")

print("_______")
print("W:")
W_cardio = W(L_cardio, lambda_cardio)
W_free_weight = W(L_free_weight, lambda_free_weight)
W_cable = W(L_cable, lambda_cable)
W_weight_machine = W(L_weight_machine, lambda_weight_machine)
print(f"W for Cardio Machines: {W_cardio:.6f}")
print(f"W for Free Weights: {W_free_weight:.6f}")
print(f"W for Cable Machines: {W_cable:.6f}")
print(f"W for Weight Machines: {W_weight_machine:.6f}")

print("_______")
print("W_Q:")
W_Q_cardio = W_Q(W_cardio, mu_cardio)
W_Q_free_weight = W_Q(W_free_weight, mu_free_weight)
W_Q_cable = W_Q(W_cable, mu_cable)
W_Q_weight_machine = W_Q(W_weight_machine, mu_weight_machine)
print(f"W_Q for Cardio Machines: {W_Q_cardio:.6f}")
print(f"W_Q for Free Weights: {W_Q_free_weight:.6f}")
print(f"W_Q for Cable Machines: {W_Q_cable:.6f}")
print(f"W_Q for Weight Machines: {W_Q_weight_machine:.6f}")

print("_______")
print("L_Q:")
L_Q_cardio = L_Q(lambda_cardio, W_Q_cardio)
L_Q_free_weight = L_Q(lambda_free_weight, W_Q_free_weight)
L_Q_cable = L_Q(lambda_cable, W_Q_cable)
L_Q_weight_machine = L_Q(lambda_weight_machine, W_Q_weight_machine)
print(f"L_Q for Cardio Machines: {L_Q_cardio:.6f}")
print(f"L_Q for Free Weights: {L_Q_free_weight:.6f}")
print(f"L_Q for Cable Machines: {L_Q_cable:.6f}")
print(f"L_Q for Weight Machines: {L_Q_weight_machine:.6f}")


# Whole system metrics

p_only_cardio = 0.25
p_other = 1 - p_only_cardio

# weighted averages of the metrics

def weighted_average(cardio_metric, free_weight_metric, cable_metric, weight_machine_metric):
    leftSide = (p_only_cardio + p_other) * cardio_metric # 1 * cardio_metric
    rightSide = (p_other *  free_weight_metric) + (p_other * cable_metric) + (p_other * weight_machine_metric)

    return leftSide + rightSide


rho_system = weighted_average(rho_cardio, rho_free_weight, rho_cable, rho_weight_machine)
print(f"rho for the whole system: System is utilized {rho_system:.6f} of the time")

P0_system = weighted_average(P0_cardio, P0_free_weight, P0_cable, P0_weight_machine)
print(f"P0 for the whole system: System is not utilized {P0_system:.6f} of the time")

L_system = weighted_average(L_cardio, L_free_weight, L_cable, L_weight_machine)
print(f"L for the whole system: Avg. {L_system:.2f} people are the system")

W_system = weighted_average(W_cardio, W_free_weight, W_cable, W_weight_machine)
print(f"W for the whole system: Avg. Time spent the system is {W_system:.6f} hours")

W_Q_system = weighted_average(W_Q_cardio, W_Q_free_weight, W_Q_cable, W_Q_weight_machine)
print(f"W_Q for the whole system: Avg. Time spent in queue {W_Q_system:.6f} hours")

L_Q_system = weighted_average(L_Q_cardio, L_Q_free_weight, L_Q_cable, L_Q_weight_machine)
print(f"L_Q for the whole system: Avg. {L_Q_system:.2f} people are in the queue")
