import java.io.IOException;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws IOException {
		CombinatorialAuction ca = new CombinatorialAuction();
		ca.readOffersFromFile("resources/in601");
		ca.readConflictMatrixFromFile("cm601.txt");
		double tdeb = System.currentTimeMillis();
		ArrayList<Integer> best = ca.findBestAssignment(1000, 0.3);
		System.out.println(best.toString() + " " + ca.calculateValue(best));
		System.out.println(System.currentTimeMillis() - tdeb);
		// System.out.println(solution.toString());
//		float value = 0;
//		for(int i : solution) {
//			value += ca.getOffers().get(i).getValue();
//		}
//		System.out.println(value);
	}

}
