
public class Table {

	int numberOfSpots = 10;
	Person[] seats = new Person[10];
	int tableID; // unique to every table
	String tableName;
	boolean isVIPTable = false;
	boolean isModifiable = true; // for DB purposes.

	public Table(int id) {
		tableID = id;
	}

	/*
	 * add person to table and sets the persons table field to this table
	 */
	void addPerson(Person p) {
		if(numberOfSpots == 0) {
			System.out.println("------ERROR: Added to full table-----");
			return;
		}
		else {
			//System.out.println("added "+p+ "to table "+this.tableID);
			seats[10-numberOfSpots] = p;
			numberOfSpots--;
			p.table = this;
		}
	}
	/*
	 * add person to specific slot. Only used in DatabaseReader.parseTableFromRow
	 * in case there is a gap in the DB e.g., person, null, person in the tables table
	 */
	void addPerson(Person p, int slot) {
		seats[slot] = p;
		numberOfSpots--;
		p.table = this;
	}
	/*
	 * remove a person from the table and free up a spot
	 */
	void removePerson(Person p){
		if(numberOfSpots == 10) {
			System.out.println("------ERROR: Subtracted from empty table-----");
		}
		else {
			int index = findInArray(p); // find where the person is in the array
			if(index == -1) {
				System.out.println("NOT FOUND");
			}
			else {
				for(int i = index; i < seats.length-1; i++) {
					seats[i] = seats[i+1]; // shift people down
				}
			}
			numberOfSpots++;
		}
	}
	/*
	 * replace person p with person q at the table. Return p
	 */
	void replacePerson(Person p, Person q) {
		int index = findInArray(p);
		this.seats[index] = q;
		q.table = this;
	}

	private int findInArray(Person p) {
		for(int i = 0; i < seats.length; i++) {
			System.out.print(seats[i] + " \t");
			if(seats[i] == p) {
				return i;
			}
		}
		return -1; // not found, should never return this
	}
	
	public int findInArray(String fullName) {
		Person p = new Person(fullName);
		for(int i = 0; i < seats.length; i++) {
			if(seats[i] == null) continue;
			if(seats[i].firstName.equalsIgnoreCase(p.firstName) &&
					seats[i].lastName.equalsIgnoreCase(p.lastName)) {
				return i;
			}
		}
		return -1; // not found, should never return this
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
