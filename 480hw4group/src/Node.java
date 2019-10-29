/**
 * This class represents the individual in the population. It has the ability to 
 * mutate, shuffle its self, and calculate its fitness.
 * 
 * @author Samuel Riegler
 *
 */

public class Node {
	
	double fitness;
	int[] path;
	
	/**
	 * Constructor for node
	 * @param length - length of the path
	 */
	public Node(int length) {
		path = new int[length];
	}
	
	/**
	 * Copy's an a passed path to this path
	 * @param toCopy - a path to copy
	 */
	public void copy(int [] toCopy) {
		for(int i = 0; i < toCopy.length; i++) {
			path[i] = toCopy[i];
		}
	}
	
	/**
	 * mutates this.path by swapping two points. 
	 */
	public void mutate() {
		double mutRate = Main.getMutationRate();
		// decide if we mutate
		if(Main.getRandom() <= mutRate) {
			// if we mutate get two random positions
			int pos1 = (int) (Main.getRandom() * path.length);
			int pos2 = (int) (Main.getRandom() * path.length);
			
			// make sure the positions are not the same
			while (pos1 == pos2) {
				pos2 = (int) (Main.getRandom() * path.length);
			}
			
			// swap the positions
			int temp = path[pos1];
			path[pos1] = path[pos2];
			path[pos2] = temp;
		}
		// calculate the new fitness
		calcFitness();
	}
	
	/**
	 * calculates the fitness of the path
	 */
	public void calcFitness() {
		fitness = 0;
		for(int i = 1; i < path.length; i++) {
			fitness += Main.getData(path[i - 1], path[i]);
		}
	}
	
	/**
	 * inserts a position, value pare into the path
	 * @param pos - the position for it to be inserted
	 * @param value - the value to be inserted
	 */
	public void setPathPos(int pos, int value) {
		path[pos] = value;
	}
	
	/**
	 * Shuffles the path to generate a random path
	 */
	public void shuffel() {
		int pos1, pos2, temp;
		
		// swap positions the number of times equal to the path length
		for(int i = 0; i < path.length; i++) {
			// choose two positions
			pos1 = (int) (Main.getRandom() * path.length);
			pos2 = (int) (Main.getRandom() * path.length);
			// swap them
			temp = path[pos1];
			path[pos1] = path[pos2];
			path[pos2] = temp;
		}
		// calculate the new fitness
		calcFitness();
	}
}