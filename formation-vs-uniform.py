import json
import os
import argparse
import numpy as np
from matplotlib import pyplot as plt
plt.rcParams.update({'font.size': 22})

parser = argparse.ArgumentParser()
parser.add_argument('-o', '--output-file', help='output file location',
                    required=False, default='generated-files/simulation/random-simulation.json')

parser.add_argument('-rn', '--red-n', help='Amount of soldiers for red faction',
                    required=False, default=50)

parser.add_argument('-bn', '--blue-n', help='Max amount of soldiers for uniform formation',
                    required=False, default=65)
parser.add_argument('-t', '--max-time', help='Max time for each iteration',
                    required=False, default=500)
parser.add_argument('-it', '--iterations', help='Max amount of iterations',
                    required=False, default=10)


args = parser.parse_args()
output_path = args.output_file
blue_n = args.blue_n
red_n = args.red_n
iterations = int(args.iterations)

t = float(args.max_time)

red_formations = ["phalanx", "testudo", "shieldwall"]


def run_simulations():

    wins_per_formation = []

    for red_formation in red_formations:
        soldiers = red_n - 10
        total_wins = []

        while soldiers < blue_n:
            it = 0
            wins = 0

            while it < iterations:
                os.system("mkdir -p generated-files")
                os.system('java -jar generator/target/generator-1.0-SNAPSHOT-jar-with-dependencies.jar -bf uniform -rf '
                 + red_formation + ' -bn ' + str(soldiers) + ' -rn ' + str(red_n) + ' && java -jar simulation/target/simulation-1.0-SNAPSHOT-jar-with-dependencies.jar -t ' + str(t))

                data = json.load(open(output_path))
                winner = str(data['winner'])

                if(winner == "red"):
                    wins+=1
                
                it += 1

            total_wins.append((wins/iterations) * 100)
            print(soldiers)
            soldiers += 5

        wins_per_formation.append([red_formation, total_wins])


    return wins_per_formation

wins_per_formation = run_simulations()
soldiers = []

for i in range(red_n - 10, blue_n, 5):
    soldiers.append(i)

for formation in wins_per_formation:
    f = ""
    if formation[0] == "phalanx":
        f = "Falange"
    elif formation[0] == "testudo":
        f = "Testudo"
    elif formation[0] == "shieldwall":
        f = "Muralla de escudos"
    else:
        f = "Uniforme"

    plt.plot(soldiers, formation[1], label=f)

plt.legend()
plt.grid()

plt.ylim(0, 100)

plt.xlabel('Unidades de formacion uniforme (azul)')
plt.ylabel('Porcentaje de victorias de formacion roja (%)')

plt.show()
