
public class Table {
	
	int numberOfSpots = 10;
	Person[] seats = new Person[10];
	int tableID; // unique to every table
	String tableName;
	boolean isVIPTable = false;
	
	public Table(int id) {
		tableID = id;
	}
	
	/*
	 * add person to table 
	 */
	void addPerson(Person p) {
		if(numberOfSpots == 0) {
			System.out.println("------ERROR: Added to empty table-----");
			return;
		}
		else {
			//System.out.println("added "+p+ "to table "+this.tableID);
			seats[10-numberOfSpots] = p;
			numberOfSpots--;
		}
	}
	
	public String toString(){
		String rv = "";
		for(int i = 0; i < 10-numberOfSpots; i++) {
			rv += seats[i].firstName;
			rv += ", ";
		}
		return rv;
	}

}
