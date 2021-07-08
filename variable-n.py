import json
import datetime
import os
import argparse
import numpy as np
from matplotlib import pyplot as plt
plt.rcParams.update({'font.size': 22})

parser = argparse.ArgumentParser()
parser.add_argument('-i', '--input-file', help='input file location',
                    required=False, default='invalid')
parser.add_argument('-o', '--output-file', help='output file location',
                    required=False, default='generated-files/simulation/random-simulation.json')

parser.add_argument('-rf', '--red-formation', help='Formation for red faction',
                    required=False, default='testudo')
parser.add_argument('-rn', '--red-n', help='Amount of soldiers for red faction',
                    required=False, default=50)

parser.add_argument('-bn', '--blue-n', help='Max amount of soldiers for blue faction',
                    required=False, default=65)
parser.add_argument('-t', '--max-time', help='Max time for each iteration',
                    required=False, default=500)
parser.add_argument('-it', '--iterations', help='Max amount of iterations',
                    required=False, default=10)


args = parser.parse_args()
output_path = args.output_file
blue_n = int(args.blue_n)
red_formation = args.red_formation
red_n = int(args.red_n)
iterations = int(args.iterations)

t = float(args.max_time)

blue_formations = ["phalanx", "testudo", "fulcrum", "uniform"]


def run_simulations():
    print("Running simulations with red formation " + red_formation +
          " (" + str(red_n) + " soldiers), for a time of " + str(t))

    wins_per_formation = []

    for blue_formation in blue_formations:
        if(blue_formation == red_formation):
            continue
        soldiers = red_n - 30
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

                if(winner == "blue"):
                    wins+=1
                
                it += 1

            total_wins.append((wins/iterations) * 100)
            soldiers += 10

        wins_per_formation.append([blue_formation, total_wins])


    return wins_per_formation

soldiers = []

if args.input_file != 'invalid':
    with open(args.input_file, 'r') as file:
        data = json.load(file)
        wins_per_formation = data['wins_per_formation']
else:
    wins_per_formation = run_simulations()

for i in range(red_n - 30, blue_n, 10):
    soldiers.append(i)

for formation in wins_per_formation:
    f = ""
    if formation[0] == "phalanx":
        f = "Falange"
    elif formation[0] == "testudo":
        f = "Testudo"
    elif formation[0] == "fulcrum":
        f = "Fulcrum"
    else:
        f = "Uniforme"
    
    plt.plot(soldiers, formation[1], label=f, marker="o")

plt.legend()
plt.grid()

plt.ylim(0, 110)

plt.xlabel('Unidades del enemigo')
plt.ylabel('Porcentaje de victorias del enemigo (%)')

# I don't want to dump graphic if it's just old data
os.system("mkdir -p generated-files/variable-n/")
filename = "generated-files/variable-n/" + str(datetime.datetime.now())+".json"
if args.input_file == 'invalid':
    with open(filename, 'w') as outfile:
        json.dump({
            "wins_per_formation": wins_per_formation
        }, outfile)

plt.show()
