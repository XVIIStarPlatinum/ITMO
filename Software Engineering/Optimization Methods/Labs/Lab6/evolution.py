from random import (random, randint)
from bisect import bisect
from itertools import permutations

graph = [
    [0, 4, 5, 3, 8],
    [4, 0, 7, 6, 8],
    [5, 7, 0, 7, 9],
    [3, 6, 7, 0, 9],
    [8, 8, 9, 9, 0]
]
cities_count = len(graph)
population_size = 4
mutation_probability = 0.01


class Path:
    def __init__(self, sequence):
        self.sequence = sequence

    def f(self):
        distance = 0
        for i in range(1, cities_count):
            distance += graph[self.sequence[i - 1]][self.sequence[i]]
        distance += graph[self.sequence[-1]][self.sequence[0]]
        return distance


def choose_parents(paths):
    parents = []

    non_chosen_paths = [path for path in paths]
    for i in range(population_size):
        sum_of_distances = 0
        for path in non_chosen_paths:
            sum_of_distances += path.f()
        probabilities = []
        for path in non_chosen_paths:
            probabilities.append(path.f() / sum_of_distances)

        cumulative_probabilities = []
        cumulative_sum = 0
        for probability in probabilities:
            cumulative_sum += probability
            cumulative_probabilities.append(cumulative_sum)

        r = random()

        chosen_path_index = bisect(cumulative_probabilities, int(r))
        parents.append(non_chosen_paths[chosen_path_index])
        non_chosen_paths.remove(non_chosen_paths[chosen_path_index])
    return parents


def get_sequence_with_break_points(sequence, break_point_1, break_point_2):
    res = ""
    for i in range(cities_count):
        if i in (break_point_1, break_point_2):
            res += "|"
        res += str(sequence[i])
    return res


def create_next_population(parents):
    pairs = []
    for i in range(0, len(parents), 2):
        pairs.append([parents[i], parents[i + 1]])

    next_population = []

    for pair in pairs:
        parent_1_sequence = pair[0].sequence
        parent_2_sequence = pair[1].sequence

        break_point_1 = randint(1, cities_count - 1)
        break_point_2 = randint(1, cities_count - 1)

        print(f"Скрешиваем: {get_sequence_with_break_points(parent_1_sequence, break_point_1, break_point_2)} {get_sequence_with_break_points(parent_2_sequence, break_point_1, break_point_2)}")

        while break_point_2 == break_point_1:
            break_point_2 = randint(1, cities_count - 1)

        if break_point_2 < break_point_1:
            break_point_1, break_point_2 = break_point_2, break_point_1

        taken_cities = []

        child_1 = [0 for i in range(cities_count)]
        for i in range(break_point_1, break_point_2):
            child_1[i] = parent_2_sequence[i]
            taken_cities.append(parent_2_sequence[i])
        i = 0
        j = break_point_1
        while i < break_point_1:
            if parent_1_sequence[j] not in taken_cities:
                child_1[i] = parent_1_sequence[j]
                taken_cities.append(parent_1_sequence[j])
                i += 1
            if j == cities_count - 1:
                j = 0
            else:
                j += 1
        i = break_point_2
        while i < cities_count:
            if parent_1_sequence[j] not in taken_cities:
                child_1[i] = parent_1_sequence[j]
                taken_cities.append(parent_1_sequence[j])
                i += 1
            if j == cities_count - 1:
                j = 0
            else:
                j += 1

        taken_cities.clear()

        child_2 = [0 for i in range(cities_count)]
        for i in range(break_point_1, break_point_2):
            child_2[i] = parent_1_sequence[i]
            taken_cities.append(parent_1_sequence[i])
        i = 0
        j = break_point_1
        while i < break_point_1:
            if parent_2_sequence[j] not in taken_cities:
                child_2[i] = parent_2_sequence[j]
                taken_cities.append(parent_2_sequence[j])
                i += 1
            if j == cities_count - 1:
                j = 0
            else:
                j += 1
        i = break_point_2
        while i < cities_count:
            if parent_2_sequence[j] not in taken_cities:
                child_2[i] = parent_2_sequence[j]
                taken_cities.append(parent_2_sequence[j])
                i += 1
            if j == cities_count - 1:
                j = 0
            else:
                j += 1

        print(f"Получены потомки: {get_sequence_with_break_points(child_1, break_point_1, break_point_2)} {get_sequence_with_break_points(child_2, break_point_1, break_point_2)}")
        print("="*77)

        next_population.append(Path(child_1))
        next_population.append(Path(child_2))

    return next_population


def mutation(path):
    random_number = random()
    if random_number <= mutation_probability:
        index_1 = randint(0, cities_count - 1)
        index_2 = index_1
        while index_2 == index_1:
            index_2 = randint(0, cities_count - 1)

        mutated_path_sequence = path.sequence

        mutated_path_sequence[index_1], mutated_path_sequence[index_2] = mutated_path_sequence[index_2], \
        mutated_path_sequence[index_1]

        print("Произошла мутация:", "".join(map(str, path.sequence)), "-->", "".join(map(str, mutated_path_sequence)))

        return Path(mutated_path_sequence)

    return None


population_count = 3
cities = range(cities_count)

population = []
for sequence in permutations(cities):
    if len(population) < population_size:
        population.append(Path(sequence))
    else:
        break

current_population_number = 1

while True:
    print(f"Популяция №{current_population_number}:")
    print(" Путь | Значение целевой функции | Вероятность размножения")
    sum_of_distances = 0
    for path in population:
        sum_of_distances += path.f()
    for path in population:
        print(f'{"".join(map(str, path.sequence))} |            {path.f()}            |         {path.f()}/{sum_of_distances}')

    if current_population_number == population_count:
        break

    print("="*77)

    parents = choose_parents(population)
    print("В качестве родителей выбраны следующие особи:")
    for parent in parents:
        print("".join(map(str, parent.sequence)))
    print("="*77)

    next_population = create_next_population(parents)

    for i in range(len(next_population)):
        child = mutation(next_population[i])
        if child is not None:
            next_population[i] = child

    population += next_population
    print("Расширенная популяция:")
    print(" Путь | Значение целевой функции")
    for path in population:
        print(f'{"".join(map(str, path.sequence))} |            {path.f()}')
    population.sort(key=lambda x: x.f())
    population = population[:population_size]

    current_population_number += 1
    print()

print()

optimal_path = population[0]
print("Оптимальный путь:", "".join(map(str, optimal_path.sequence)), "\nРасстояние:", optimal_path.f())
