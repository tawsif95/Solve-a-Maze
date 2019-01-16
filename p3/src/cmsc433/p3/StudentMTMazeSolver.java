package cmsc433.p3;

/*
 * Name: Tawsif Siddiqui
 * Citation:
 * 		DFS Solver from the STMazeSolverDFS class.
 */

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This file needs to hold your solver to be tested. You can alter the class to
 * extend any class that extends MazeSolver. It must have a constructor that
 * takes in a Maze. It must have a solve() method that returns the datatype List
 * <Direction> which will either be a reference to a list of steps to take or
 * will be null if the maze cannot be solved.
 */

public class StudentMTMazeSolver extends SkippingMazeSolver {
	public StudentMTMazeSolver(Maze maze) {
		super(maze);
	}

	public List<Direction> solve() {
		// TODO: Implement your code here
		int numProcessors = Runtime.getRuntime().availableProcessors();
		ExecutorService threadPool = Executors.newFixedThreadPool(numProcessors);
		List<Direction> possibleSol = new LinkedList<Direction>();
		List<Callable<List<Direction>>> tasks = new LinkedList<Callable<List<Direction>>>();
		try {
			
			Choice start = firstChoice(maze.getStart());
			while (!start.choices.isEmpty()) {
				
				Choice cur = follow(start.at, start.choices.peek());
				DFS temp = new DFS(cur, start.choices.pop());
				tasks.add(temp);
				
			}
		} catch (SolutionFound e) {
			System.out.println("May day! May day! We have found the MOTH!");
		}

		for (Callable<List<Direction>> t: tasks) {
			try {	
				possibleSol = threadPool.submit(t).get();
				if (possibleSol != null) break;
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			} 
			catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		threadPool.shutdown();
		return possibleSol;
	}

	private class DFS implements Callable<List<Direction>> {
		Choice startPt;
		Direction firstDir;

		public DFS(Choice startPt, Direction firstDir) {
			this.startPt = startPt;
			this.firstDir = firstDir;
		}

		/*
		 * This method is based on List<Direction> solve() method from the
		 * STMazeSolverDFS class. Few minor changes have been made from the
		 * original code.
		 */
		@Override
		public List<Direction> call() {
			
			LinkedList<Choice> choiceStack = new LinkedList<Choice>();
			Choice ch;

			try {
				
				choiceStack.push(this.startPt);
				
				while (!choiceStack.isEmpty()) {
					
					ch = choiceStack.peek();
					if (ch.isDeadend()) {
						// backtrack.
						choiceStack.pop();
						if (!choiceStack.isEmpty()) choiceStack.peek().choices.pop();
						continue;
					}
					choiceStack.push(follow(ch.at, ch.choices.peek()));
				}
				// No solution found.
				return null;
			} 
			catch (SolutionFound e) {
				Iterator<Choice> iter = choiceStack.iterator();
				LinkedList<Direction> solutionPath = new LinkedList<Direction>();
				while (iter.hasNext()) {
					ch = iter.next();
					solutionPath.push(ch.choices.peek());
				}
				solutionPath.push(this.firstDir);

				if (maze.display != null) maze.display.updateDisplay();			
				return pathToFullPath(solutionPath);
			}
		}
	}
}