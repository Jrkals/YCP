import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SeatSwapper implements IDatabase {
	ArrayList<Table> tables = new ArrayList<>();
	Connection conn;

	public SeatSwapper() {
		conn = connectToDB();
	}
	
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		int p1LocAtTable = tables.get(p1Table-1).findInArray(p1);
		int p2LocAtTable = tables.get(p2Table-1).findInArray(p2);
		if(p1LocAtTable == -1 || p2LocAtTable == -1) {
			System.out.println("Error, not found at that table");
		}

		Statement statement;
		try {
			if(conn.isClosed()) conn = IDatabase.connectToDB();
			statement = conn.createStatement();
			String query1 = makeUpdateStatement(p1, p2Table, p2LocAtTable);
			System.out.println(query1);
			String query2 = makeUpdateStatement(p2, p1Table, p1LocAtTable);
			System.out.println(query2);
			String query3 = makeLockStatement(p1Table); // change the modifyHuh value to 0
			System.out.println(query3);
			String query4 = makeLockStatement(p2Table); // change the modifyHuh value to 0
			System.out.println(query4);
			String query5 = makeUpdatePersonStatement(p2, p1Table); // change table_number in people table
			System.out.println(query5);
			String query6 = makeUpdatePersonStatement(p1, p2Table); // change table_number in people table
			System.out.println(query6);
			try {
				statement.addBatch(query1);
				statement.addBatch(query2);
				statement.addBatch(query3);
				statement.addBatch(query4);
				statement.addBatch(query5);
				statement.addBatch(query6);
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
		DatabaseReader dr = new DatabaseReader();
		ArrayList<Person> people = dr.getPeopleFromTables();
		for(Person p: people) {
			if(p.firstName.equalsIgnoreCase(p1.split(" ")[0]) && 
					p.lastName.equalsIgnoreCase(p1.split(" ")[1])){
				return p.firstName + " "+p.lastName;
			}
		}
		dr.close();
		return p1; // at least give back the original string
	}

	/*
	 * takes a command e.g., swap jake nicolson and john adams
	 * returns ["jake nicolson", "john adams"]
	 */
	private String[] parseLine(String command) {
		String[] split = command.split(" ");
		String[] rv = new String[2];
		if(split.length == 6) {
			rv[0] = split[1] +" "+ split[2];
			rv[1] = split[4]+" "+ split[5];
		}
		else {
			int indexOfAnd = 0;
			for(int i = 0; i < split.length; i++) {
				if(split[i].equals("and") || split[i].equals("with")) {
					indexOfAnd = i;
				}
			}
			rv[0] = split[1]; // first name of first person
			for(int i = 2; i < split.length-1; i++) {
				if(i < indexOfAnd) {
					rv[0] += " "+split[i];
				}
				if(i == indexOfAnd) {
					rv[1] = split[i+1]+ " ";
					i+=2;
				}
				if(i > indexOfAnd) {
					rv[1] += split[i] + " ";
				}
			}
			rv[1] += split[split.length-1]; // last name of second person
		}
		System.out.println(rv[0]+ " "+ rv[1]);
		return rv;
	}

	private String makeUpdateStatement(String person, int Table, int LocAtTable) {
		String query = "UPDATE tables SET person_"+LocAtTable+" = \""+person+"\"";
		query +=" WHERE table_number = "+Table+";";
		return query;
	}
	// change the table number in the people table
	private String makeUpdatePersonStatement(String person, int table) {
		return "UPDATE people SET table_number = "+table+" WHERE name = \""+person+"\";";	 
	}
	// p is "FName LName"
	private boolean verifyName(String p) {
		DatabaseReader dr = new DatabaseReader(conn);
		if(dr.personExists(p)) {
			return true;
		}
		dr.close();
		return false;
	}
	/*
	 * finds where in a table in the db a person is 
	 * e.g., spot 6 of some table
	 * table most be modifiable
	 */
	private int findModifiableTableForName(String name) {
		DatabaseReader dr = new DatabaseReader();
		tables = dr.getTables();	
		ArrayList<Integer> modifiables = dr.getModifiableTables();
		for(Table t: tables) {
			if(!modifiables.contains(t.tableID)) continue; // don't return static table
			int loc = t.findInArray(name); // see if the person is in that table
			if(loc != -1) {
				return t.tableID;
			}
		}
		System.out.println("can't find table for "+name);
		dr.close();
		return -1; // should never happen
	}
	/*
	 * finds where in a table in the db a person is 
	 * e.g., spot 6 of some table
	 */
	private int findTableForName(String name) {
		DatabaseReader dr = new DatabaseReader();
		tables = dr.getTables();	
		for(Table t: tables) {
			int loc = t.findInArray(name); // see if the person is in that table
			if(loc != -1) {
				return t.tableID;
			}
		}
		System.out.println("can't find table for "+name);
		dr.close();
		return -1; // should never happen
	}
	/*
	 * finds a name in the given table
	 */
	private int findNameInTable(String fullName, int tableNum) {
		for(Table t: tables) {
			if(t.tableID == tableNum) {
				System.out.println("found "+fullName+" at table "+tableNum);
				return t.findInArray(fullName);
			}
		}
		System.out.println("Couldn't find "+fullName +" in table"+ tableNum);
		return -1;
	}

	public void deletePeople(List<Person> ppl) {
		try {
			if(conn.isClosed()) {
				conn = IDatabase.connectToDB();
			}
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		//System.out.println("In delete People");
		for(Person p: ppl) {
			int tableNum = findModifiableTableForName(p.firstName+" "+p.lastName);
			int loc = findNameInTable(p.firstName+" "+p.lastName, tableNum);
			String deletePeopleStatement = "DELETE FROM people WHERE name = \""+p.firstName+ " "+p.lastName+"\";";
			System.out.println(deletePeopleStatement);
			Statement stmt;
			try {
				stmt = this.conn.createStatement();
				stmt.execute(deletePeopleStatement);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if(tableNum != -1 && loc != -1) {
				String deleteTablesStatement = "UPDATE tables  SET person_"+(loc)+" = NULL"
						+" where table_number = "+tableNum+";";
				System.out.println(deleteTablesStatement);
				
				
				try {
					stmt = this.conn.createStatement();
					stmt.addBatch(deleteTablesStatement);
					stmt.addBatch(deletePeopleStatement);
					int[] result = stmt.executeBatch();
					for(int i = 0; i < result.length; i++) {
						System.out.println(result[i]);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else {
				System.out.println("Tried to delete people who don't exist");
			}

		}
	}

	Connection connectToDB() {
		return IDatabase.connectToDB();
	}

}
