import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseReader implements IDatabase {
	ArrayList<Table> tables = new ArrayList<>();
	ArrayList<Person> people = new ArrayList<>();
	
	Connection conn;
	public DatabaseReader(Connection c) {
		this.conn = c;
	}
	ArrayList<Integer> getModifiableTables() {
		//Connection conn = connectToDB();
		ArrayList<Integer> modifiableTables = new ArrayList<>();
		if(conn == null) {
			System.out.println("Cannot connect to DB");
		}
		else {
			try {
				Statement stmt = conn.createStatement();
				String query = "Select table_number, modifyHuh from tables";
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) { // loop over results
					if(rs.getBoolean(2)) { // check to see if the row can be modified
						modifiableTables.add(rs.getInt(1)); // add the table
					}
				}
				//System.out.println(rs.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}			
		} // end of else
		return modifiableTables;
	} // end of method
	Connection connectToDB() {
		return IDatabase.connectToDB();
	}

	ArrayList<Table> getTables(){
		if(conn == null) {
			System.out.println("Cannot connect to DB");
		}
		else {
			try {
				Statement stmt = conn.createStatement();
				String query = "Select * from tables";
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) { // loop over results
					Table t = parseTableFromRow(rs);
					tables.add(t);
				}
				//System.out.println(rs.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}			
		} // end of else

		return tables;
	}

	/*
	 * takes a row from a sql query and returns a table
	 * row should be {table_number, person_1...person_10, isVIP, modifyHuh} so 13 columns total
	 */
	private Table parseTableFromRow(ResultSet rs) {
		Table t = null; // just to initialize it to please the compiler
		try {
			t = new Table(rs.getInt(1));
			for(int i = 2; i <= 11; i++) { // loop over the names
				String name = rs.getNString(i);
				if(name == null) {
					break;
				}else {
					// TODO fix this these people are null basically i.e. just a name
					Person p = new Person(name.split(" ")[0], name.split(" ")[1]);
					t.addPerson(p);
				}
			}
			// check vip status
			boolean isVIP = rs.getBoolean(12);
			if(isVIP) {
				t.isVIPTable = true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return t;
	}
	ArrayList<Person> getPeople(){
		for(Table t: tables) {
			for(int i = 0; i < 9-t.numberOfSpots; i++) {
				people.add(t.seats[i]);
			}
		}
		return people;
	}

	/*
	 * tells whether a person exists in the list of people from the tables table in confSeating DB
	 */
	boolean personExists(String person) {
		if(people.size() == 0) {
			getReadyToFindPeople();
		}
		//System.out.println("Checking this against the list of "+people.size()+" people...");
		for(Person p: people) {
			if(!(p.firstName+p.lastName).equalsIgnoreCase(person)){
				return true;
			}
		}
		return false;
	}
	/*
	 * call the necessary function to fill the people and table lists so
	 * that we do this once not over and over again
	 * call this before personExists
	 */
	void getReadyToFindPeople() {
		getTables();
		getPeople();
	}

}
