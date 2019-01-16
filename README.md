As a part of a class project, I created an efficient solver for two-dimensional mazes. Each maze consists a grid of positions. Someone moving through the maze can generally move in one ofthe four compass directions (North, South, East, West) to advance to an adjacent position;however, walls may block some of these possibilities. There is also a start position, and an exitposition from the maze. For a given maze, starting from the start position, my program will either return a solution (i.e. a path to the exit) or determine that no solution exists.
As equirement, my program  needed to also run faster than all the single-threaded solvers when run on a multicore
machine.
