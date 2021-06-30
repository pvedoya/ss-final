import json
from matplotlib import colors, pyplot as plt
plt.rcParams.update({'font.size': 22})

def digest():
    data = json.load(open("./generated-files/input/random-input.json"))
    soldiers = list(data['soldiers'])

    gridSize = float(data['gridSize'])
    soldiersAmountPerFaction = int(data['soldiersAmountPerFaction'])
    factions = int(data['factions'])

    return soldiers, gridSize, soldiersAmountPerFaction, factions

soldiers, gridSize, soldiersAmountPerFaction, factions = digest()

fig = plt.figure()
ax = fig.add_subplot(111)

x = []
y = []
colors = []

for s in soldiers:
    x.append(s['x'])
    y.append(s['y'])

    if s['faction'] == "red":
        colors.append('red')
    elif s['faction'] == "blue":
        colors.append("blue")


scat = plt.scatter(x, y, color=colors)
ax.set_aspect('equal', adjustable='box')

plt.xlim(0, 50)
plt.ylim(0, 40)

plt.show()