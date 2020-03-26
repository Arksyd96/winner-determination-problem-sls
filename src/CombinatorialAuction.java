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
	
	public CombinatorialAuction() {
		offers = new ArrayList<>();
		random = new Random(System.nanoTime());
	}
	
	public void readFromFile(String path) throws IOException {
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
	
	public ArrayList<Integer> findBestAssignment(String path) throws IOException {
		ArrayList<Integer>[] cm = readConflictMatrixFromFile(path);		// Generate conflict matrix
		ArrayList<Integer> assignment = generateSolutionFromRK(cm);		// Generate init solution
		ArrayList<Integer> solution = new ArrayList<>();
		float rand, max, temp, somme = 0;	int bid;
		for(int i = 0; i < 2000; i++) {	// Max iter = 1000
			rand = random.nextFloat();
			if (rand < 0.3) {
				bid = random.nextInt(totalSize);
			} else {
				max = 0;	bid = random.nextInt();
				for(int k = 0; k < totalSize; k++) {
					temp = offers.get(k).getValue();
					if(temp > max) {
						if(!assignment.contains(k)) {
							max = temp;
							bid = k;
						}
					}
				}
			}
			for(int k = 0; k < assignment.size(); k++) {
				if (cm[bid].contains(assignment.get(k)))
					assignment.remove(k);
			}
			assignment.add(bid);
			
			temp = 0;
			for(int j : assignment) {
				temp += offers.get(j).getValue();
			}
			if(temp > somme) {
				somme = temp;
				solution = new ArrayList<Integer>(assignment);
			}
		}
		return solution;
	}
	
	public ArrayList<Integer> generateSolutionFromRK(ArrayList<Integer>[] cm){
		ArrayList<Integer> assignment = new ArrayList<>();
		float[] rkRep = new float[totalSize];
		for(int i = 0; i < totalSize; i++)	rkRep[i] = random.nextFloat();
		float max = rkRep[0];	int index = 0;
		boolean completed = false;
		boolean conflicting = false;
		while(!completed) {
			for (int i = 0; i < totalSize; i++) {
				if (rkRep[i] > max) {
					max = rkRep[i];
					index = i;
				}
			}
			max = 0;	rkRep[index] = 0;	conflicting = false;
			for(int i : assignment) {
				if (cm[index].contains(i)) {
					conflicting = true;
					break;
				}
			}
			if (!conflicting) assignment.add(index);
			completed = true;
			for(float f : rkRep) {
				if(f > 0)	completed = false;
			}
		}
		return assignment;
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
	
	public ArrayList<Integer>[] readConflictMatrixFromFile(String path) throws IOException{
		ArrayList<Integer>[] conflictMatrix = new ArrayList[totalSize];
		for (int i = 0; i < totalSize; i++) { 
			conflictMatrix[i] = new ArrayList<Integer>(); 
        } 
		List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
		for(int i = 0; i < totalSize; i++) {
			String[] objectIds = lines.get(i).split(": ")[1].split(" ");
			for(String obj : objectIds)	conflictMatrix[i].add(Integer.parseInt(obj));
		}
		return conflictMatrix;
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
