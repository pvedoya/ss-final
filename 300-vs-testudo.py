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
parser.add_argument('-mit', '--min-training', help='Min amount of training',
                    required=False, default=0.1, type=float)
parser.add_argument('-s', '--training-step', help='training step for each run',
                    required=False, default = 0.1, type=float)
parser.add_argument('-mat', '--max-training', help='Max amount of training',
                    required=False, default=1, type=float)
parser.add_argument('-t', '--max-time', help='Max time for each iteration',
                    required=False, default=500)
parser.add_argument('-it', '--iterations', help='Max amount of iterations',
                    required=False, default=10)


args = parser.parse_args()
output_path = args.output_file
training_step = args.training_step
min_training = args.min_training
max_training = args.max_training
iterations = int(args.iterations)

t = float(args.max_time)

red_formation = 'elite'


def run_simulations():


    training = min_training
    total_wins = []


    while training <= max_training+0.1*training_step:
        it = 0
        wins = 0

        while it < iterations:
            os.system("mkdir -p generated-files")
            os.system('java -jar generator/target/generator-1.0-SNAPSHOT-jar-with-dependencies.jar -bf testudo -rf '
             + red_formation + ' -bn 600 -rn 30 -et '+ str(training) + ' && java -jar simulation/target/simulation-1.0-SNAPSHOT-jar-with-dependencies.jar -t ' + str(t))

            data = json.load(open(output_path))
            winner = str(data['winner'])
            print("And the winner is... " + winner)

            if(winner == "red"):
                wins+=1
            
            it += 1

        total_wins.append((wins/iterations) * 100)
        print("Red won a total of " + str(wins) + " battles")
        print(training)
        training += training_step

    return total_wins



training = []
aux = min_training
# np.arange estaba comportandose para el orto, no me juzgues
while aux <= max_training+0.1*training_step:
    training.append(aux)
    aux += training_step

print(training)

if args.input_file != 'invalid':
    with open(args.input_file, 'r') as file:
        data = json.load(file)
        wins = data['wins']
        training = data['training']
else:
    wins = run_simulations()

plt.plot(training, wins)

plt.grid()

plt.ylim(0, 100)

plt.xlabel('Entrenamiento de la formacion elite')
plt.ylabel('Porcentaje de victorias de formacion elite (%)')


os.system("mkdir -p generated-files/300-vs-testudo/")
filename = "generated-files/300-vs-testudo/" + str(datetime.datetime.now())+".json"

with open(filename, 'w') as outfile:
    json.dump({
        "training": training,
        "wins": wins
    }, outfile)

plt.show()
