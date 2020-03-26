import java.util.ArrayList;
import java.util.List;

public class Offer {
	private float value;
	private ArrayList<Integer> objects;
	
	public Offer() {
		objects = new ArrayList<>();
	}
	
	public Offer(float value, ArrayList<Integer> objects) {
		this.value = value;
		this.objects = objects;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public void addObject(Integer objectId) {
		this.objects.add(objectId);
	}
	
	public ArrayList<Integer> getObjects(){
		return this.objects;
	}
	
	public float getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("Value : " + this.value + " | Objects :");
		for (int i = 0; i < objects.size(); i++)
			str.append(" " + objects.get(i));
		return str.append("\n").toString();
	}
}
