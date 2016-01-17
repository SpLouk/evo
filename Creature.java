import java.util.ArrayList;


public class Creature {

	ArrayList<Part> partsList;
	
	public Creature(Part p){
		partsList = new ArrayList<Part>();
		partsList.add(p);
	}
	
	public Creature(ArrayList<Part> pList){
		partsList = pList;
	}
}
