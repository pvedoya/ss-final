import json
from matplotlib import animation, colors, pyplot as plt
plt.rcParams.update({'font.size': 22})


def digest():
    data = json.load(
        open("./generated-files/simulation/random-simulation.json"))
    states = list(data['states'])

    gridSize = float(data['l'])

    return states, gridSize


states, gridSize = digest()

fig = plt.figure()
ax = fig.add_subplot(111)

x = []
y = []
colors = []

for s in states[0]:
    x.append(s['x'])
    y.append(s['y'])

    if s['faction'] == "red":
        colors.append('red')
    elif s['faction'] == "blue":
        colors.append("blue")


scat = plt.scatter(x, y, color=colors)
ax.set_aspect('equal', adjustable='box')


def animate(i):
    data = []
    colors = []

    for p in states[i+1]:
        data.append([p['x'], p['y']])


        if p['hp'] <= 0:
            colors.append('white')
        elif p['faction'] == "red":
            colors.append('red')
        else:
            colors.append('blue')

    scat.set_offsets(data)
    scat.set_color(colors)

    return scat,

anim = animation.FuncAnimation(fig, animate, frames=len(states)-1, interval=100 , repeat=False)

plt.xlim(0, gridSize)
plt.ylim(0, gridSize)

plt.xlabel('X (m)')
plt.ylabel('Y (m)')

plt.show()
