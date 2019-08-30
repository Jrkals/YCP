import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseReader implements IDatabase {
	ArrayList<Table> tables = new ArrayList<>();
	ArrayList<Table> modifiableTables = new ArrayList<>();

	Connection conn;
	public DatabaseReader(Connection c) {
		this.conn = c;
	}

	public DatabaseReader() {
		conn = IDatabase.connectToDB();
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
				String query = "Select table_number, modifyHuh from tables;";
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
				String query = "Select * from tables;";
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
					continue;
				}else {
					// TODO fix this these people are null basically i.e. just a name
					// Solution: this is handled in Arranger
					Person p = new Person(name.split(" ")[0], name.split(" ")[1]);
					t.addPerson(p, i-1);
				}
			}
			// check vip status
			boolean isVIP = rs.getBoolean(12);
			if(isVIP) {
				t.isVIPTable = true;
			}

			// check modifiability status
			boolean isModifiable = rs.getBoolean(13);
			if(isModifiable) {
				t.isModifiable = true;
			} else {
				t.isModifiable = false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return t;
	}
	/*
	 * returns the people from the tables table
	 */
	ArrayList<Person> getPeopleFromTables(){
		getTables();
		ArrayList<Person> ppl = new ArrayList<Person>();
		for(Table t: tables) {
			for(int i = 0; i <= 10-t.numberOfSpots; i++) {
				if(t.seats[i] != null)
					ppl.add(t.seats[i]);
			}
		}
		return ppl;
	}
	/*
	 * returns the people in the people table
	 */
	ArrayList<Person> getPeople(){
		ArrayList<Person> ppl = new ArrayList<>();
		if(conn == null) {
			System.out.println("Cannot connect to DB");
		}
		else {
			try {
				Statement stmt = conn.createStatement();
				String query = "Select * from people;";
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) { // loop over results
					Person p = parsePersonFromRow(rs);
					ppl.add(p);
				}
				//System.out.println(rs.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}			
		} // end of else

		return ppl;
	}

	private Person parsePersonFromRow(ResultSet rs) {
		Person p = null;
		String name;
		try {
			name = rs.getString(1);
			String diet = rs.getString(2);
			String spouse_name = rs.getString(3);
			String chapter = rs.getString(4);
			String tags = rs.getString(5);
			int table_num = rs.getInt(6);
			p = new Person(name, diet, spouse_name, chapter, tags, table_num);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return p;
	}

	/*
	 * tells whether a person exists in the list of people from the tables table in confSeating DB
	 */
	boolean personExists(String person) {
		ArrayList<Person> ppl = getPeople();
		System.out.println("Checking this against the list of "+ppl.size()+" people...");
		for(Person p: ppl) {
			if(!(p.firstName+p.lastName).equals(person)){
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
		getPeopleFromTables();
	}

}
