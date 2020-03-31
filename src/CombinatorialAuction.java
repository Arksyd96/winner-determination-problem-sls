import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CombinatorialAuction {

	private ArrayList<Offer> offers;
	private int totalSize;
	private int objectsCount;
	private Random random;
	private ArrayList<Integer>[] conflictMatrix;
	
	public CombinatorialAuction() {
		offers = new ArrayList<>();
		random = new Random(System.currentTimeMillis());
	}
	
	public void readOffersFromFile(String path) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
		String[] attr = lines.get(0).split(" ");
		totalSize = Integer.parseInt(attr[0]);
		objectsCount = Integer.parseInt(attr[1]);
		
		for(int i = 1; i < lines.size(); i++) {
			Offer offer = new Offer();
			String[] assignment = lines.get(i).split(" ");
			offer.setValue(Float.parseFloat(assignment[0]));
			for (int k = 1; k < assignment.length; k++) {
				offer.addObject(Integer.parseInt(assignment[k]));
			}
			offers.add(offer);
		}
	}
	
	public ArrayList<Integer>[] createConflictMatrix(String path) {
		ArrayList<Integer>[] conflictMatrix = new ArrayList[totalSize];
		for (int i = 0; i < totalSize; i++) { 
			conflictMatrix[i] = new ArrayList<Integer>(); 
        } 
		boolean isConflicting;
		
		for(int i = 0; i < totalSize; i++) {	// Pour chaque offre o1
			for(int k = 0; k < totalSize; k++) {	// et offre o2
				if (k != i) {	// Sachant que o1 != o2
					isConflicting = false;
					for(int o1 : offers.get(i).getObjects()) {	// Si il y'a un objet de o1
						for(int o2 : offers.get(k).getObjects()) {
							if (o1 == o2) {
								conflictMatrix[i].add(k);
								isConflicting = true;
								break;
							}
						}
						if (isConflicting)	break;
					}
				}
			}
		}
		return conflictMatrix;
	}
	
	public void readConflictMatrixFromFile(String path) throws IOException{
		conflictMatrix = new ArrayList[totalSize];
		for (int i = 0; i < totalSize; i++) { 
			conflictMatrix[i] = new ArrayList<Integer>(); 
        } 
		List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
		for(int i = 0; i < totalSize; i++) {
			String[] objectIds = lines.get(i).split(": ")[1].split(" ");
			for(String obj : objectIds)	conflictMatrix[i].add(Integer.parseInt(obj));
		}
	}
	
	public ArrayList<Integer> findBestAssignment(int maxiter, double wprate) throws IOException {
		float rand, max, temp, value, newValue, bestValue = 0;	
		int bid, maxChances = 500;
		int bCount = 0;
		
		// Generate initial solution
		ArrayList<Integer> assignment = generateRandomSolution(4);		
		
		// Calculating it's value
		value = calculateValue(assignment);

		System.out.println("Initiale solution is : " + assignment.toString() + " value : " + value);
		
		ArrayList<Integer> newAss, solution = new ArrayList<>();
		
		for(int i = 0; i < maxiter; i++) {
			
			// if < wprate, we take a random offer
			if (random.nextDouble() < wprate) {
				do { 
					bid = random.nextInt(totalSize); 
				} while(assignment.contains(bid));
			}
			// Else, we take the best offer we have
			else {
				bid = getBestOffer(assignment);
				if(bid == 0) {
					do { 
						bid = random.nextInt(totalSize); 
					} while(assignment.contains(bid));
				} else {
					bCount++;
				}
			}
			
			// taking original value
			value = calculateValue(assignment);
			
			// Adding the bid to the assignment and removing conflicting bids
			newAss = newAssignmentWithBid(bid, assignment);
			newValue = calculateValue(newAss);
			// System.out.println("New vs old : " + newValue + " " + value );
			// Taking the best one
			if(newValue > value) {
				assignment = newAss;
				value = newValue;
			}
			
			// Updating best solution
			if(value > bestValue) {
				solution = new ArrayList<Integer>(assignment);
				bestValue = value;
				maxChances = 20;
			} 
			else {
				maxChances--;
				if (maxChances == 0) {
					assignment = generateRandomSolution(4);
					maxChances = 20;
				}
					
			}
		}
		System.out.println("bcount : " + bCount);
		// System.out.println(solution.toString() + " " + calculateValue(solution));
		return solution;
	}
	
	public ArrayList<Integer> generateRandomSolution(int maxSize){
		// New assignment
		ArrayList<Integer> assignment = new ArrayList<>();
		
		// RK representation (Array of random keys)
		float[] randomKeys = new float[totalSize];
		for(int i = 0; i < totalSize; i++)	
			randomKeys[i] = random.nextFloat();
		
		float max = randomKeys[0];		// initial value of max
		int indexOfMax = 0;
		boolean conflicting = false;	// if Offers are conflicting
		
		for(int k = 0; k < totalSize; k++) {
			// Searching for the max of random keys
			for (int i = 0; i < totalSize; i++) {
				if (randomKeys[i] > max) {
					max = randomKeys[i];
					indexOfMax = i;
				}
			}
			
			// Checking if offer at indexOfMax is conflicting with previous ones
			conflicting = false;
			for(int i : assignment) {
				if (conflictMatrix[indexOfMax].contains(i)) {
					conflicting = true;
					break;
				}
			}
			
			// If not conflicting, we add the offer to the assignment
			if (!conflicting) {
				assignment.add(indexOfMax);
				if(assignment.size() == maxSize)	break;
			}
			
			// Removing key from the array for next iteration
			max = 0;	
			randomKeys[indexOfMax] = 0;	
		}

		return assignment;
	}
	
	public float calculateValue(ArrayList<Integer> assignment) {
		float sum = 0;
		for(int x : assignment)
			sum += offers.get(x).getValue();
		return sum;
	}
	
	public int getBestOffer(ArrayList<Integer> assignment) {
		int indexOfBest = 0;
		float max = 0, temp;
		for(int i = 0; i < totalSize; i++) {
			temp = offers.get(i).getValue();
			if(temp > max && !assignment.contains(i) && !isConflicting(i, assignment)) {
				indexOfBest = i;
				max = temp;
			}
		}
		return indexOfBest;
	}
	
	public ArrayList<Integer> newAssignmentWithBid(int bid, ArrayList<Integer> assignment) {
		ArrayList<Integer> newAss = new ArrayList<Integer>(assignment);
		newAss.add(bid);
		if(newAss.size() > 1)
			newAss.removeIf(ass -> conflictMatrix[bid].contains(ass));
		return newAss;
	}
	
	public boolean isConflicting(int bid, ArrayList<Integer> assignment) {
		for(int i : assignment) {
			if(conflictMatrix[bid].contains(i))
				return true;
		}
		return false;
	}
	
	public ArrayList<Offer> getOffers(){
		return this.offers;
	}
	
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("Total : " + totalSize + "\tObjects count : " + objectsCount + "\n");
		for(int i = 0; i < 10; i++) {
			str.append(offers.get(i).toString());
		}
		return str.toString();
	}
}
