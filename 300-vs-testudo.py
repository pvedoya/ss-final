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
spartans = 30
persians = 900


t = float(args.max_time)

red_formation = 'elite'


def run_simulations():


    training = min_training
    red_deaths = []
    blue_deaths = []


    i = 0
    while training <= max_training+0.1*training_step:
        it = 0

        while it < iterations:
            if it == 0:
                red_deaths.append([]) 
                blue_deaths.append([]) 
            os.system("mkdir -p generated-files")
            os.system('java -jar generator/target/generator-1.0-SNAPSHOT-jar-with-dependencies.jar -bf testudo -rf '
             + red_formation + ' -bn '+str(persians)+' -rn '+str(spartans)+' -et '+ str(training) + ' && java -jar simulation/target/simulation-1.0-SNAPSHOT-jar-with-dependencies.jar -t ' + str(t))

            data = json.load(open(output_path))
            red_deaths[i].append(data['redDeaths'][-1])
            blue_deaths[i].append(data['blueDeaths'][-1])
            it += 1

        i += 1
        training += training_step

    # By now I should have two arrays for the deaths of each training step, with each element being an array of the deaths of each iteration

    return red_deaths, blue_deaths



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
        red_deaths = data['red_deaths']
        blue_deaths = data['blue_deaths']
        training = data['training']
else:
    red_deaths, blue_deaths = run_simulations()

# breakpoint()
plt.errorbar(training, [np.average(100*x/spartans) for x in np.array(red_deaths)], [np.std(100*x/spartans) for x in np.array(red_deaths )], color='red', label='espartanos')
# plt.errorbar(training, [np.average(x) for x in np.array(red_deaths)], [np.std(x) for x in np.array(red_deaths )], color='red', label='espartanos')
plt.errorbar(training, [np.log(np.average(100*x/persians)) for x in np.array(blue_deaths)], [np.log(np.std(100*x/persians)) for x in np.array(blue_deaths )], color='blue', label='persas')
# plt.errorbar(training, [np.average(x) for x in np.array(blue_deaths)], [np.std(x) for x in np.array(blue_deaths )], color='blue', label='persas')

# plt.legend(loc='lower right')
# plt.legend(loc='upper left')
plt.grid()

# plt.ylim(0, 600)

plt.xlabel('Entrenamiento de la formación espartana')
# plt.ylabel('Cantidad de muertes por facción')
plt.ylabel('Porcentaje de muertes (%)')



# I don't want to dump graphic if it's just old data
os.system("mkdir -p generated-files/300-vs-testudo/")
filename = "generated-files/300-vs-testudo/" + str(datetime.datetime.now())+".json"
if args.input_file == 'invalid':
    with open(filename, 'w') as outfile:
        json.dump({
            "training": training,
            "red_deaths": red_deaths,
            "blue_deaths": blue_deaths
        }, outfile)

plt.show()
