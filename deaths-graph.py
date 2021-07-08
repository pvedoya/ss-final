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
parser.add_argument('-bf', '--blue-formation', help='Formation for blue faction',
                    required=False, default='phalanx')
parser.add_argument('-rn', '--red-n', help='Amount of soldiers for red faction',
                    required=False, default=50)
parser.add_argument('-bn', '--blue-n', help='Amount of soldiers for blue faction',
                    required=False, default=50)
parser.add_argument('-it', '--iterations', help='Amount of iterations to run',
                    required=False, default=10)
parser.add_argument('-t', '--max-time', help='Max time for each iteration',
                    required=False, default=500)

args = parser.parse_args()
output_path = args.output_file
blue_formation = args.blue_formation
blue_n = args.blue_n
red_formation = args.red_formation
red_n = args.red_n

t = float(args.max_time)
it = int(args.iterations)


def run_simulations():
    print("Running simulations with blue formation " + blue_formation + " (" + str(blue_n) + " soldiers), red formation " +
          red_formation + " (" + str(red_n) + " soldiers), for a time of " + str(t) + " and " + str(it) + " iterations")

    red_deaths = []
    blue_deaths = []

    iteration = 0

    while iteration < it:
        os.system("mkdir -p generated-files")
        os.system('java -jar generator/target/generator-1.0-SNAPSHOT-jar-with-dependencies.jar -bf ' + blue_formation
                  + ' -rf ' + red_formation + ' -bn ' + str(blue_n) + ' -rn ' + str(red_n) +
                  ' && java -jar simulation/target/simulation-1.0-SNAPSHOT-jar-with-dependencies.jar -t ' + str(t))

        data = json.load(open(output_path))
        dt = float(data['dt'])
        n = int(data['n'])

        i = 0

        for death in list(data['redDeaths']):
            if(len(red_deaths) == i):
                red_deaths.append([])
            red_deaths[i].append(death)
            i += 1

        i = 0
        for death in list(data['blueDeaths']):
            if(len(blue_deaths) == i):
                blue_deaths.append([])
            blue_deaths[i].append(death)
            i += 1

        iteration += 1
    
    red_sum = []
    blue_sum = []

    red_error = []
    blue_error = []

    for i in red_deaths:
        red_sum.append(np.mean(i)/n)
        red_error.append((np.std(i)/n) * 100)
        
    for i in blue_deaths:
        blue_sum.append(np.mean(i)/n)
        blue_error.append((np.std(i)/n) * 100)

    return red_sum, blue_sum, red_error, blue_error, dt



red_percentages = []
blue_percentages = []
times = []
time = 0


if args.input_file != 'invalid':
    with open(args.input_file, 'r') as file:
        data = json.load(file)
        red_deaths = data['red_deaths']
        red_error = data['red_error']
        blue_deaths = data['blue_deaths']
        blue_error = data['blue_error']
        dt = data['dt']
else:
    red_deaths, blue_deaths, red_error, blue_error, dt = run_simulations()

for i in range(len(red_deaths)):
    times.append(time)
    red_percentages.append(red_deaths[i] * 100)
    blue_percentages.append(blue_deaths[i] * 100)
    time += (5 * dt) #Si cambio cada cuanto guardo dts hay que cambiar esto si o si

plt.errorbar(times, red_percentages, red_error, color='red', label='Fulcrum')
plt.errorbar(times, blue_percentages, blue_error, color='blue', label='Uniforme')

plt.legend()
plt.grid()

plt.xlabel('Tiempo (s)')
plt.ylabel('Porcentaje de muertes (%)')

# I don't want to dump graphic if it's just old data
os.system("mkdir -p generated-files/deaths-graph/")
filename = "generated-files/deaths-graph/" + str(datetime.datetime.now())+".json"
if args.input_file == 'invalid':
    with open(filename, 'w') as outfile:
        json.dump({
            "red_deaths": red_deaths,
            "red_error": red_error,
            "blue_deaths": blue_deaths,
            "blue_error": blue_error,
            "dt": dt
        }, outfile)

plt.show()
