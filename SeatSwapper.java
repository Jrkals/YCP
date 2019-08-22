import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SeatSwapper implements IDatabase {
	ArrayList<Table> tables = new ArrayList<>();
	Connection conn;

	public SeatSwapper() {
		conn = connectToDB();
	}

	boolean swap(String command) {
		boolean success = false;
		String p1;
		String p2;

		String[] ps = parseLine(command);
		p1 = ps[0]; // person 1 name
		p2 = ps[1]; // person 2 name

		// check that people's names are valid
		if(!verifyName(p1) || !verifyName(p2)) {
			System.out.println("these people are not in the system. Try again");
			return success;
		}
		// Use the case sensitive names already in the DB not the names on the command line
		p1 = getRealName(p1);
		p2 = getRealName(p2);
		// swap
		int p1Table = findTableForName(p1);
		int p2Table = findTableForName(p2);
		int p1LocAtTable = tables.get(p1Table-1).findInArray(p1)+1;
		int p2LocAtTable = tables.get(p2Table-1).findInArray(p2)+1;
		if(p1LocAtTable == -1 || p2LocAtTable == -1) {
			System.out.println("Error, not found at that table");
		}

		Statement statement;
		try {
			statement = conn.createStatement();
			String query1 = makeUpdateStatement(p1, p2Table, p2LocAtTable);
			String query2 = makeUpdateStatement(p2, p1Table, p1LocAtTable);
			String query3 = makeLockStatement(p1Table); // change the modifyHuh value to 0
			String query4 = makeLockStatement(p2Table); // change the modifyHuh value to 0
			try {
				statement.addBatch(query1);
				statement.addBatch(query2);
				statement.addBatch(query3);
				statement.addBatch(query4);
				statement.executeBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//verify the swap
		//TODO later for now I will eye it
		success = true;

		return success;
	}
	
	private String makeLockStatement(int p1Table) {
		return "UPDATE tables SET modifyHuh = 0 WHERE table_number = "+p1Table+";";
	}

	//TODO not working. Find some way to keep cases constant
	private String getRealName(String p1) {
		DatabaseReader dr = new DatabaseReader(conn);
		ArrayList<Person> people = dr.getPeople();
		for(Person p: people) {
			if(p.firstName.equalsIgnoreCase(p1.split(" ")[0]) && 
					p.lastName.equalsIgnoreCase(p1.split(" ")[1])){
				return p.firstName + " "+p.lastName;
			}
		}
		return p1; // at least give back the original string
	}

	/*
	 * takes a command e.g., swap jake nicolson and john adams
	 * returns ["jake nicolson", "john adams"]
	 */
	private String[] parseLine(String command) {
		String[] split = command.split(" ");
		String[] rv = new String[2];
		rv[0] = split[1] +" "+ split[2];
		rv[1] = split[4]+" "+ split[5];
		return rv;
	}

	private String makeUpdateStatement(String person, int Table, int LocAtTable) {
		String query = "UPDATE tables SET person_"+LocAtTable+" = \""+person+"\"";
		query +=" WHERE table_number = "+Table+";";
		System.out.println(query);
		return query;
	}
	// p is "FName LName"
	private boolean verifyName(String p) {
		DatabaseReader dr = new DatabaseReader(conn);
		if(dr.personExists(p)) {
			return true;
		}

		return false;
	}

	private int findTableForName(String name) {
		DatabaseReader dr = new DatabaseReader(conn);
		tables = dr.getTables();	
		for(Table t: tables) {
			int loc = t.findInArray(name); // see if the person is in that table
			if(loc != -1) {
				return t.tableID;
			}
		}
		return -1; // should never happen
	}

	Connection connectToDB() {
		return IDatabase.connectToDB();
	}

}
