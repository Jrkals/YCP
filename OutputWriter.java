import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
/*
 * class to write output to file or screen or mySQL database
 */
public class OutputWriter implements IDatabase {
	FileWriter fw;
	String filename;
	Table[] vipTables;
	Table[] tables;

	public OutputWriter(String filename, Table[] vipTables, Table[] tables)  {
		this.filename = filename;
		this.vipTables = vipTables;
		this.tables = tables;
	}

	public void writeToFile() throws IOException {
		fw = new FileWriter(filename);
		fw.write("Table, Name, Chapter, VIP?, Diet, Spouse\n");
		for(Table t: vipTables) {
			if(t.numberOfSpots < 10) {
				fw.write("Table "+t.tableID+"\n");
				for(int i = 0; i < 10; i++) {
					fw.write("seat:"+(i+1)+", ");
					if(t.seats[i] != null) {
						fw.write(t.seats[i]+"\n");
					}
					else {
						fw.write("empty\n");
					}
				}
			}
		} // end outer for
		for(Table t: tables) {
			if(t.numberOfSpots < 10) {
				fw.write("Table "+t.tableID+"\n");
				for(int i = 0; i < 10; i++) {
					fw.write("seat:"+(i+1)+", ");
					if(t.seats[i] != null) {
						fw.write(t.seats[i]+"\n");
					}
					else {
						fw.write("empty\n");
					}
				}
			}
		} // end outer for
		fw.close();

	}

	void printArrangementToScreen() {
		for(Table t: vipTables) {
			if(t.numberOfSpots < 10) {
				System.out.println("Table "+t.tableID);
				for(int i = 0; i < 10; i++) {
					System.out.print("seat:"+(i+1)+" ");
					if(t.seats[i] != null) {
						System.out.println(t.seats[i]);
					}
					else {
						System.out.println("empty");
					}
				}
			}
		}
		for(Table t: tables) {
			if(t.numberOfSpots < 10) {
				System.out.println("Table "+t.tableID);
				for(int i = 0; i < 10; i++) {
					System.out.print("seat:"+(i+1)+" ");
					if(t.seats[i] != null) {
						System.out.println(t.seats[i]);
					}
					else {
						System.out.println("empty");
					}
				}
			}
		}
	}

	void writeToDB() {
		Connection connection = connectToDB();
		Statement stmt = null;
		if(connection == null) {
			System.out.println("Can't connect to DB");
			return; 
		}
		else {
			try {
				stmt = connection.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			DatabaseReader dr = new DatabaseReader(connection);
			// List of all tabls
			ArrayList<Table> dbTables = dr.getTables();
			for(Table t: tables) {
				if(!tableExistsInList(dbTables, t)) {
					String insert = makeInsertStatement(t);
					try {
						stmt.execute(insert);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			for(Table t: vipTables) {
				if(!tableExistsInList(dbTables, t)) {
					String insert = makeInsertStatement(t);
					try {
						stmt.execute(insert);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			// List of modifiable tables
			ArrayList<Integer> modifiableRows = dr.getModifiableTables();
			System.out.println("Number of modifiable rows is "+modifiableRows.size());
			// Modify the table
			if(modifiableRows.size() > 0) {
				for(int i = 0; i < vipTables.length; i++) {
					Table t = vipTables[i];
					if(modifiableRows.contains(t.tableID)) {
						String statement = makeUpdateStatement(t);
						System.out.println(statement);
						try {
							stmt.executeUpdate(statement);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} // end of if contains
					// re-insert modifiable rows

				} // end of for
				for(int i = 0; i < tables.length; i++) {
					Table t = tables[i];
					if(modifiableRows.contains(t.tableID)) {
						String statement = makeUpdateStatement(t);
						System.out.println(statement);
						try {
							stmt.executeUpdate(statement);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} // end of if
				} // end of for
			} // end of if
		} // end of else
	} // end of method
	/*
	 * looks for table in list
	 */
	private boolean tableExistsInList(ArrayList<Table> dbTables, Table t) {
		for(Table table: dbTables) {
			if(t.tableID == table.tableID) {
				return true;
			}
		}
		return false;
	}

	private String makeInsertStatement(Table t) {
		String sql = "INSERT INTO tables (table_number,"; // first line
		for(int j = 1; j <= 10-t.numberOfSpots; j++) {
			sql += "person_"+(j)+",";
		}
		sql += "isVIP, modifyHuh)";
		sql += " Values ("+t.tableID+","; // last line
		for(int j = 0; j <= 9-t.numberOfSpots; j++) {
			sql += "\""+t.seats[j].firstName+" "+t.seats[j].lastName +"\",";
		}
		if(t.isVIPTable)
			sql += "TRUE,";
		else 
			sql += "FALSE,";
		sql += "TRUE);"; // modifyHuh
		return sql;
	}

	private String makeUpdateStatement(Table t) {
		String sql = "UPDATE tables SET ";
		for(int j = 1; j <= 10-t.numberOfSpots; j++) {
			sql += "person_"+(j)+" = \""+t.seats[j-1].firstName+" "+ t.seats[j-1].lastName+"\",";
		}
		sql += "isVIP = "+t.isVIPTable+",";
		sql += "modifyHuh = "+t.isModifiable+" ";
		sql += "WHERE table_number = "+t.tableID+";";
		return sql;
	}

	void writePeopleToDB(){
		Connection connection = connectToDB();
		Statement stmt = null;
		if(connection == null) {
			System.out.println("Can't connect to DB");
			return; 
		}
		else {
			DatabaseReader dr = new DatabaseReader(connection);
			ArrayList<Person> dbPeople = dr.getPeople();
			ArrayList<Person> localPeople = getPeopleFromTables();
			for(Person p: localPeople) {
				String query = "";
				int personStatusinDB = personExistsInList(dbPeople, p);
				if(personStatusinDB == -1) {
					// insert new person
					query = makeInsertPersonStatement(p);
					System.out.println(query);
				} else if(personStatusinDB == 0) {
					query = makeUpdatePersonStatement(p);
					System.out.println(query);
				} 
				try {
					stmt = connection.createStatement();
					stmt.execute(query);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
	}
	private String makeUpdatePersonStatement(Person p) {
		String query = "";
		if(p.hasDietRestrictions)
			query += "UPDATE people SET diet =\""+p.dietRestrictions+"\",";
		else
			query += "UPDATE people SET diet = NULL,";
		if(p.hasSpouse)
			query += "spouse_name = \""+p.spouse.firstName+" "+p.spouse.lastName+"\"";
		query += "chapter = \""+p.chapter+"\",";
		query += "tags = "+Utilities.asQuotedString(p.tagList)+",";
		query += "table_number = "+p.table.tableID+" ";
		query += "WHERE name = \""+p.firstName+" "+p.lastName+"\";";
		return query;
	}

	/*
	 * statement for inserting person into DB
	 */
	private String makeInsertPersonStatement(Person p) {
		System.out.println(p);
		String query;
		query = "INSERT INTO people (name, diet, spouse_name,";
		query += "chapter,tags, table_number) VALUES (\"";
		query += p.firstName+" "+p.lastName+"\",";
		if(p.hasDietRestrictions)
			query +="\""+p.dietRestrictions+"\",";
		else {
			query += "NULL,";
		}
		if(p.spouse != null)
			query +="\""+ p.spouse.firstName+ " "+p.spouse.lastName+"\",\"";
		else
			query += "NULL,\"";
		query += p.chapter+"\",";
		query += Utilities.asQuotedString(p.tagList)+",";
		query += p.table.tableID+"";
		query += ");";
		return query;
	}
	/*
	 * looks for object p in list
	 * if P exists and is the exact same, return 1
	 * if the name matches but other details don't return 0
	 * if it doesn't exist return -1
	 */
	private int personExistsInList(ArrayList<Person> dbPeople, Person p) {
		int rv = -1;
		for(Person person: dbPeople) {
			if(person.firstName.equals(p.firstName)
					&& person.lastName.equals(p.lastName)) {
				rv = 0;
				if(person.chapter.equals(p.chapter)) {
					if(person.hasDietRestrictions == p.hasDietRestrictions) {
						if(person.hasSpouse == p.hasSpouse && p.hasSpouse == true) {
							if(person.spouse.firstName.equals(p.spouse.lastName)) {
								if(person.tagList.equals(p.tagList)) {
									if(person.table.tableID == p.table.tableID) {
										rv = 1;
									}
								}
							}
						}else {
							if(person.tagList.equals(p.tagList)) {
								if(person.table.tableID == p.table.tableID) {
									rv = 1;
								}
							}
							
						}
					}
				}
			}
		}

		return rv;
	}

	/*
	 * gets the list of people from the vip tables and regular tables
	 */
	private ArrayList<Person> getPeopleFromTables(){
		ArrayList<Person> people = new ArrayList<>();
		for(Table t: vipTables) {
			for(Person p: t.seats) {
				if(p != null) people.add(p);
			}
		}
		for(Table t: tables) {
			for(Person p: t.seats) {
				if(p != null) people.add(p);
			}
		}

		return people;
	}
	/*
	 * checks to see if two people are the same
	 */
	boolean personIsSame(Person p1, Person p2) {
		return p1.firstName.endsWith(p2.firstName) &&
				p1.lastName.equals(p2.lastName) &&
				p1.spouse.firstName.equals(p2.spouse.firstName) &&
				p1.chapter.equals(p2.chapter) &&
				p1.tagList.size() == p2.tagList.size() &&
				p1.table.tableID == p2.table.tableID;
	}


	Connection connectToDB() {
		return IDatabase.connectToDB(); // calls the static method in the IDatabase interface
	}
}
