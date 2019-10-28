import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Course number: CPS 480
 * Names: Samuel Riegler and Taylor Shaw
 * Assignment: Homework 4
 * 
 * This program uses a genetic algorithm (based on two-position flip) to find the best path
 * between all cities specified in a CSV file. The best path is the path with the lowest fitness score.
 * 
 * This program can be compiled and run by importing the project into Eclipse, or by running any other Java
 * program through the command line.
 */

public class Main {
	
    private final static int MAXGEN = 50000; //{10,000 ; 50,000}
    private final static int RANDSEED = 4321; //{1,000 ; 4,321}
    private final static double MUTRATE = 0.3; //{0.1 ; 0.3}
    private final static int POPSIZE = 40; //{20 ; 40}
    private final static double BESTRATE = 0.20; // 20% of population
    private final static int REPETE_TO_END = 3000;  
    private final static String f1 = "MI-part-19-miles.csv";
    private final static String f2 = "DE-all.csv";
    private final static String f3 = "MI.csv";
    private final static String WriteFile = "Report.txt";
    
    private static Random rnd = new Random(RANDSEED);

    
    private static File file = new File(f1);
    
    private static ArrayList<String> city = new ArrayList<String>();
    private static double[][] data;
    private static Node[] population;
	private static int generationNumber;
    
	/* Time variables */
	private static long start;
	private static long end;
	private static long timeMS;
	private static double timeS;
	
	public static void main(String[] args) throws IOException {
		init();
		run();
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private static void run() throws IOException {
		start = System.currentTimeMillis();
		double last = 0;
		int counter = 0;;
		for(int i = 0; i < MAXGEN; i++) {
			sortLowest();
			
			generationNumber = i + 1;
			
			if(last == population[0].fitness) {
				counter++;
			}else {
				counter = 0;
				last = population[0].fitness;
				System.out.println("Fitness: " + population[0].fitness + "\t\tGeneration: " + i);
			}
			
			if(counter == REPETE_TO_END) {
				System.out.println("ended with best solution");
				break;
			}
			
			// generate children
			genChildren();
			mutateChildren();
		}
		System.out.println("Reached maximum number of generations");
		printToFile();

	}
	
	/**
	 * 
	 */
	private static void mutateChildren() {
		int posToStart = (int) (POPSIZE * BESTRATE) + 1;
		while(posToStart < population.length) {
			population[posToStart].mutate();
			posToStart++;
		}
	}
	
	
	/**
	 * 
	 */
	private static void genChildren() {
		int counter = 0;
		int numToKeep = (int) (POPSIZE * BESTRATE);
		int startIndx = numToKeep + 1;
		int numOfChild = Math.floorDiv(population.length - startIndx, startIndx);
		
		for(int i = 0; i < numToKeep; i++) {
			for(int j = 0; j < numOfChild; j++) {
				population[startIndx] = new Node(population[0].path.length);
				population[startIndx].copy(population[i].path);
				startIndx++;
			}
		}
		// take care of any remainders
		while (startIndx < population.length) {
			population[startIndx] = new Node(population[0].path.length);
			population[startIndx].copy(population[counter].path);
			startIndx++;
			counter++;
		}
	}
	

	/**
	 * 
	 */
	private static void sortLowest() {
		double lowest;
		int lowestIndex = 0;
		
		for(int i = 0; i < (int) (POPSIZE * BESTRATE); i++) {
			lowest = Integer.MAX_VALUE;
			for(int j = i; j < population.length; j++) {
				if(population[j].fitness < lowest) {
					lowest = population[j].fitness;
					lowestIndex = j;
				}
			}
			if(i != lowestIndex) {
				swap(i,lowestIndex);
			}
		}
	}
	

	/**
	 * 
	 * @param x
	 * @param y
	 */
	private static void swap(int x, int y) {
		Node temp = population[x];
		population[x] = population[y];
		population[y] = temp;
	}
	

	/**
	 * 
	 */
	private static void genPopulation() {
		Node master = new Node(city.size());
		for(int i = 0; i < city.size(); i++) {
			master.setPathPos(i, i);
		}
		master.shuffel();
	
		population = new Node[POPSIZE];
		
		for(int i = 0; i < POPSIZE; i++) {
			Node node = new Node(city.size());
			node.copy(master.path);
			node.shuffel();
			population[i] = node;
		}
	}
	

	/**
	 * 
	 */
	private static void init() {
		String s;
		Scanner sc;
		String[] ci;
		int counter = 0;
		
		try {
			sc = new Scanner(file);
			
		}catch(Exception e) {
			System.out.println("cant open / read file");
			return;
		}
		
		// get the number of cities and set a number the the city
		if(sc.hasNext()) {
			s = sc.nextLine();
			ci = s.split(",");
			
			for(int i = 1; i < ci.length; i++) {
				city.add(ci[i]);
//				System.out.print(ci[i] + "\t\t");
			}
			
		}
		System.out.println("");
		
		// fill the data
		data = new double[city.size()][city.size()];
		
		while(sc.hasNext()) {
			s = sc.nextLine();
			ci = s.split(",");
			
			for(int i = 1; i < ci.length; i++) {
				if(ci[i].isEmpty()) {ci[i] = "0.0";}
				data[i - 1][counter] = Double.parseDouble(ci[i]);
//				System.out.print(ci[i] + "\t\t");
			}
			
			System.out.println("");
			counter++;
		}
		sc.close();
		genPopulation();
		
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static double getData(int x, int y) {
		//System.out.println("data: " + data[x][y] + "\tx: " + x + "\ty: " + y);
		return data[x][y];
	}
	

	/**
	 * 
	 * @return
	 */
	public static double getMutationRate() {
		return MUTRATE;
	}

	public static double getRandom() {
		return rnd.nextDouble();
	}


	/**
	 * 
	 * @throws IOException
	 */
	private static void printToFile() throws IOException {
		end = System.currentTimeMillis();
		FileWriter fw = new FileWriter(WriteFile,true);
		PrintWriter pw = new PrintWriter(fw);
		
		pw.printf("FILE: %s\t Time taken: %s\t MAXGEN: %d\t RANDSEEN: %d\t MUTRATE: %f\t POPSIZE: %d\t "
				+ "BEST FITNESS: %f\t GENERATIONS: %d\t \n", 
				file.getName(), findTime(), MAXGEN, RANDSEED, MUTRATE, POPSIZE, population[0].fitness, 
				generationNumber);
		pw.close();
	}
	
	/**
	 * 
	 * @return
	 */
	private static String findTime() {
		timeMS = end - start;
		
		if(timeMS < 1000) {
			return (timeMS + " milliseconds");
		}else {
			timeS = timeMS / 1000.0;
			return (timeS + " seconds");
		}
	}








}