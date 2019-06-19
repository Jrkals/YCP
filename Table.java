
public class Table {
	
	int numberOfSpots = 10;
	Person[] seats = new Person[10];
	int tableID; // unique to every table
	String tableName;
	
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

}
