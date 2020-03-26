import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws IOException {
		CombinatorialAuction ca = new CombinatorialAuction();
		ca.readFromFile("resources/in601");
		ArrayList<Integer> solution = ca.findBestAssignment("cm601.txt");
		System.out.println(solution.toString());
		float value = 0;
		for(int i : solution) {
			value += ca.getOffers().get(i).getValue();
		}
		System.out.println(value);
	}

}
