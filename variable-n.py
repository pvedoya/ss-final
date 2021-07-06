import json
import os
import argparse
import numpy as np
from matplotlib import pyplot as plt
plt.rcParams.update({'font.size': 22})

parser = argparse.ArgumentParser()
parser.add_argument('-o', '--output-file', help='output file location',
                    required=False, default='generated-files/simulation/random-simulation.json')

parser.add_argument('-rf', '--red-formation', help='Formation for red faction',
                    required=False, default='testudo')
parser.add_argument('-rn', '--red-n', help='Amount of soldiers for red faction',
                    required=False, default=50)

parser.add_argument('-bn', '--blue-n', help='Max amount of soldiers for blue faction',
                    required=False, default=50)
parser.add_argument('-t', '--max-time', help='Max time for each iteration',
                    required=False, default=500)
parser.add_argument('-it', '--iterations', help='Max amount of iterations',
                    required=False, default=10)


args = parser.parse_args()
output_path = args.output_file
blue_n = args.blue_n
red_formation = args.red_formation
red_n = args.red_n
iterations = int(args.iterations)

t = float(args.max_time)

blue_formations = ["phalanx", "testudo", "shieldwall", "uniform"]


def run_simulations():
    print("Running simulations with red formation " + red_formation +
          " (" + str(red_n) + " soldiers), for a time of " + str(t))

    wins_per_formation = []

    for blue_formation in blue_formations:
        soldiers = 5
        total_wins = []

        while soldiers < blue_n:
            it = 0
            wins = 0

            while it < iterations:
                os.system("mkdir -p generated-files")
                os.system('java -jar generator/target/generator-1.0-SNAPSHOT-jar-with-dependencies.jar -bf ' + blue_formation
                          + ' -rf ' + red_formation + ' -bn ' + str(soldiers) + ' -rn ' + str(red_n) +
                          ' && java -jar simulation/target/simulation-1.0-SNAPSHOT-jar-with-dependencies.jar -t ' + str(t))

                data = json.load(open(output_path))
                winner = str(data['winner'])

                if(winner == "red"):
                    wins+=1
                
                it += 1

            total_wins.append((wins/iterations) * 100)
            soldiers += 5

        wins_per_formation.append([blue_formation, total_wins])


    return wins_per_formation, blue_n

wins_per_formation, blue_n = run_simulations()
soldiers = []

for i in range(5, blue_n, 5):
    soldiers.append(i)

for formation in wins_per_formation:
    plt.plot(soldiers, formation[1], label=formation[0])

plt.legend()
plt.grid()

plt.xlabel('Unidades de formacion azul')
plt.ylabel('Porcentaje de victorias (%)')

plt.show()
